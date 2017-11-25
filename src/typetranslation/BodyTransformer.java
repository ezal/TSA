package typetranslation;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import comparators.ToStringComparator;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.Expr;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.UnopExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Chain;


public class BodyTransformer {
	Body body;
	Map<SootMethod, SootMethod> methodMap;
	Map<Local, Local> localsMap;
	TypeMap typeMap;
		
	BodyTransformer(Body b, Map<SootMethod, SootMethod> mmap, TypeMap tmap) {
		body = b;
		methodMap = mmap;
		typeMap = tmap;
		
		localsMap = new TreeMap<Local, Local>(new ToStringComparator<Local>());
		
		// System.out.println("  transforming body of method: " + b.getMethod());
		transform();
	}
	
	private void transform() {
        transformLocals();
        transformUnits();
        // TODO: transform traps...
	}
	
	private void transformLocals() {
		Chain<Local> locals = body.getLocals();
		// Do we also need a map between old locals and new locals? Yes
		// We modify the locals chain in place
		Iterator<Local> it = locals.snapshotIterator();
		while (it.hasNext()) { // OK to modify collection while iterating over it? It seems so.
			Local local = it.next();
			Type oldType = local.getType();
			// System.out.println("  oldType: " + oldType);
			Type newType = typeMap.getNewType(oldType);
			if (oldType != newType) {
				Local newLocal = Jimple.v().newLocal(local.getName(), newType);
				localsMap.put(local, newLocal);
				locals.insertBefore(newLocal, local);
				locals.remove(local);				
			}
		}
	}
	
	private void transformUnits() {
		// we don't build new units, we just replace the values inside!
		Chain<Unit> units = body.getUnits();
		Iterator<Unit> it = units.snapshotIterator();
		while (it.hasNext()) {
			Unit unit = it.next();
			Iterator<ValueBox> vbIt = unit.getUseAndDefBoxes().iterator();
			while (vbIt.hasNext()) {
				ValueBox vb = vbIt.next();
				Value v = vb.getValue();
				Value newv = transformValue(v);
				if (newv != v) {
					vb.setValue(newv);
				}
			}
		}
	}

	private Value transformValue(Value v) {
		Value newv = v;
		if (v instanceof Constant) {
			Type type = v.getType();
			if (type != typeMap.getNewType(type))
				throw new RuntimeException("internal error: unexpected type");
		}
		else if (v instanceof Local) {
			newv = localsMap.get((Local)v);
			if (newv == null)
				newv = v;
		}
		else if (v instanceof Ref) {
			Ref ref = (Ref)v;
			Type type = ref.getType();
			Type newType = typeMap.getNewType(type);
			if (newType != type) {
				if (ref instanceof FieldRef) {
					// TODO: What is the difference between a FieldRef and a SootFieldRef? 
					// A FieldRef appears in the body of a method (like in x = o.f or o.f = e)
					// A SootField represent a field of a class
					// Soot says a SootFieldRef is a "representation of a reference to a field as it appears in a class file."
					SootFieldRef fRef = ((FieldRef)ref).getFieldRef();
					SootFieldRef newFRef = Scene.v().makeFieldRef(fRef.declaringClass(), fRef.name(), newType, fRef.isStatic());
					if (ref instanceof InstanceFieldRef) {
						InstanceFieldRef iref = (InstanceFieldRef)ref;
						Value base = iref.getBase();
						Value newBase = transformValue(base);
						newv = Jimple.v().newInstanceFieldRef(newBase, newFRef);
					}
					else if (ref instanceof StaticFieldRef) {
						newv = Jimple.v().newStaticFieldRef(newFRef);
					}
					else
						throw new RuntimeException("value " + v + " of type " + v.getClass() + " not (yet) handled");	
				}
				else if (ref instanceof ParameterRef) {
					newv = Jimple.v().newParameterRef(newType, ((ParameterRef)ref).getIndex());
				}
				else if (ref instanceof ArrayRef) {
					ArrayRef aref = (ArrayRef)ref;
					Value newBase = transformValue(aref.getBase());
					Value newIndex = transformValue(aref.getIndex());
					newv = Jimple.v().newArrayRef(newBase, newIndex);
				}
				else
					throw new RuntimeException("value " + v + " of type " + v.getClass() + " not (yet) handled");
			}
		}
		else if (v instanceof Expr) {
			if (v instanceof UnopExpr || v instanceof BinopExpr) {
			}
			else if (v instanceof InvokeExpr) { // TODO: put all this into a separate function
				newv = transformInvokeExpr((InvokeExpr)v);
			}
			else if (v instanceof NewExpr) {
				RefType oldType = ((NewExpr)v).getBaseType();
				RefType newType = typeMap.getNewRefType(oldType);
				if (newType != oldType) {
					newv = Jimple.v().newNewExpr(newType);
				}
				else
					newv = v;
			}
			else if (v instanceof NewArrayExpr) {
				Type oldType = ((NewArrayExpr)v).getBaseType();
				Type newType = typeMap.getNewType(oldType);
				Value size = ((NewArrayExpr)v).getSize();
				if (newType != oldType) {
					newv = Jimple.v().newNewArrayExpr(newType, size);
				}
				else
					newv = v;
			}
			else if (v instanceof CastExpr) {
				CastExpr e = (CastExpr)v;
				Value val = e.getOp();				
				Type castType = e.getCastType();
				Value newVal = transformValue(val);
				Type newCastType = typeMap.getNewType(castType);
				if (newVal != val || newCastType != castType)
					newv = Jimple.v().newCastExpr(newVal, newCastType);
			}
			else if (v instanceof InstanceOfExpr) {
				InstanceOfExpr e = (InstanceOfExpr)v;
				Value val = e.getOp();
				Type checkType = e.getCheckType();				
				Value newVal = transformValue(val);
				Type newCheckType = typeMap.getNewType(checkType);
				if (newVal != val || newCheckType != checkType)
					newv = Jimple.v().newInstanceOfExpr(newVal, newCheckType);
			}
			else
				throw new RuntimeException("value " + v + " of type " + v.getClass() + " not (yet) handled");
		}
		else
			throw new RuntimeException("value " + v + " of type " + v.getClass() + " not (yet) handled");
		
		return newv;
	}
	
	public List<Value> transformValues(List<Value> valueList) {
		List<Value> newList = new LinkedList<Value>();
		Boolean changed = false;
		for (Value value: valueList) {
			Value newValue = transformValue(value);
			if (newValue != value)
				changed = true;
			newList.add(newValue);
		}
		if (changed)
			return newList;
		else
			return valueList;
					
	}
	
	private InvokeExpr transformInvokeExpr(InvokeExpr e) {
		InvokeExpr newe = e;

		Value base = null;
		Local newBase = null;
		if (e instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr ie = (InstanceInvokeExpr)e;
			base = ie.getBase();
			newBase = (Local)transformValue(base);
		}

		List<Value> args = e.getArgs();		
		List<Value> newArgs = transformValues(args);

		SootMethodRef mRef = e.getMethodRef();
		SootMethodRef newMRef = mRef;
		
		Type returnType = mRef.returnType();
		Type newReturnType = typeMap.getNewType(returnType);
		List<Type> parameterTypes = mRef.parameterTypes();
		List<Type> newParameterTypes = typeMap.getNewTypeList(parameterTypes);
		SootClass c = mRef.declaringClass();
		SootClass newc = c;
		RefType type = c.getType();
		RefType newType = typeMap.getNewRefType(type);
		if (newType != type)
			newc = newType.getSootClass();
		
		if (returnType != newReturnType || parameterTypes != newParameterTypes || c != newc) {
			newMRef = Scene.v().makeMethodRef(newc, mRef.name(), newParameterTypes, newReturnType, mRef.isStatic());
		}

		if (newBase != base || newArgs != args || newMRef != mRef) {
			if (e instanceof InterfaceInvokeExpr) {
				if (c.getName().equals("java.util.Iterator")) 
					// Iterator is not an interface is our case... should we modify more?
					newe = Jimple.v().newVirtualInvokeExpr(newBase, newMRef, newArgs);
				else
					newe = Jimple.v().newInterfaceInvokeExpr(newBase, newMRef, newArgs);
			}
			else if (e instanceof VirtualInvokeExpr)
				newe = Jimple.v().newVirtualInvokeExpr(newBase, newMRef, newArgs);
			else if (e instanceof SpecialInvokeExpr)
				newe = Jimple.v().newSpecialInvokeExpr(newBase, newMRef, newArgs);
			else if (e instanceof StaticInvokeExpr)
				newe = Jimple.v().newStaticInvokeExpr(newMRef, newArgs);
			else
				throw new RuntimeException("value " + e + " of type " + e.getClass() + " not (yet) handled");
		}

		return newe;
	}
}
