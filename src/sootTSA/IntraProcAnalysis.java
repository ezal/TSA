package sootTSA;	

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import comparators.ListComparator;
import comparators.RegionComparator;
import monoids.Monoid;

import soot.ArrayType;
import soot.Body;
import soot.Hierarchy;
import soot.Immediate;
import soot.NullType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.VoidType;
import soot.jimple.AnyNewExpr;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.Expr;
import soot.jimple.FieldRef;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.SwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.NumberedString;

import soot.Local;

public class IntraProcAnalysis extends ForwardFlowAnalysis<Unit, IntraProcState> {
	// private final TypingInfo typeInfo;
	private final InterProcState inState;
	private final InterProcState outState;
	private final InterProcAnalysis interProcAnalysis;
	private final IntraProcState initState;

	// what we analyze:
	private final SootMethodRef aMethodRef;
	private final SootMethod aMethod;
	private final Region aThisRegion;
	private final List<RefinedType> aArgTypes;
	private final RefinedType aRetType;
	private final Effects aEffects;
	
	 
	public InterProcState getOutState() {
		return outState;
	}

	public static Boolean hasStringType(Value v) {
    	return RefinedType.isStringType(v.getType());
    }
	
	private IntraProcState buildInitState(Body b) {
		IntraProcState s = new IntraProcState();
		Map<Local, RefinedType> refTypes = s.getRefTypes();

		for(Local loc: b.getLocals()) {
			Type lType = loc.getType();
			RefType refType = null;
			if (lType instanceof RefType) {
				refType = (RefType)lType;
				if (hasStringType(loc))
					refTypes.put(loc, new RefinedStringType());
				else
					refTypes.put(loc, new RefinedObjectType(refType));
			}
			else if (lType instanceof ArrayType) {
				Type bType = ((ArrayType)lType).getArrayElementType();
				if (bType instanceof RefType) {
					refType = (RefType)bType;
					if (RefinedType.isStringType(refType))
						refTypes.put(loc, new RefinedArrayType(new RefinedStringType()));
					else
						refTypes.put(loc, new RefinedArrayType(new RefinedObjectType(refType)));
				}
			}
			else {
				refTypes.put(loc, new RefinedNonRefType(lType));
			}

		}

		return s;
	}
	
    public IntraProcAnalysis(InterProcAnalysis a, SootMethodRef mRef, UnitGraph g, TypingInfo info, InterProcState state, Region rThis, List<RefinedType> typesOfArgs, TypeAndEffects typeAndEffectss) {
        super(g);
        
        interProcAnalysis = a;
        
        // this.typeInfo = info;
        inState = state;
        outState = new InterProcState(state, a.typeInfo);
        
        aMethodRef = mRef;
        aMethod = g.getBody().getMethod();
        aThisRegion = rThis; if (rThis == null) throw new RuntimeException("internal error");
        aArgTypes  = typesOfArgs; if (typesOfArgs == null) throw new RuntimeException("internal error");
        aRetType = typeAndEffectss.getType(); if (aRetType == null) throw new RuntimeException("internal error");
        aEffects = typeAndEffectss.getEffects(); if (aEffects == null) throw new RuntimeException("internal error");
        
        this.initState = buildInitState(g.getBody());
        doAnalysis();
    }

    private List<RefinedType> getListRefinedTypes(IntraProcState state, List<Value> valueList) {
    	LinkedList<RefinedType> typeList = new LinkedList<RefinedType>();    	
    	for (Value v: valueList) {
    		RefinedType refType = null;
    		Type t = v.getType();
    		if (v instanceof Immediate) {
    			if (v instanceof Local) {
    				refType = state.getRefType((Local)v);
    				if (refType == null) // && t instanceof RefType) 
    					throw new RuntimeException("internal error: something seems wrong");
    			}
    			else {
    				if (!(v instanceof Constant))
    					throw new RuntimeException("internal error");
    				
    				if (t instanceof RefType)
    					if (RefinedType.isStringType(t))
    						refType = new RefinedStringType();
    					else
    						refType = new RefinedObjectType((RefType)t);
    				else if (t instanceof PrimType)
    					refType = new RefinedNonRefType(t);
    				else if (t instanceof NullType)
    					refType = new RefinedNonRefType(t);
    				else
    					throw new RuntimeException("internal error: unhandled case " + t);
    			}
    		}
    		else {
    			throw new RuntimeException("internal error: unexpected case");
    		}
    		typeList.addLast(refType);
    	}
    	return typeList;
    }
 
    private Set<RefinedType> getAllFieldRefinedTypes(RefType c, RefinedObjectType objType, SootField f) {
		Set<RefinedType> set = new TreeSet<RefinedType>();
		for (Region r: objType.getRegions()) {
			RefinedType typ = inState.fTable.get(c, r, f);
			if (typ != null)
				set.add(typ); 
		}
		return set;
    }
    
//    private Boolean isTainted(AbstractState s, Local v) {
//    	RefinedType typ = s.getRefType(v);
//    	if (typ instanceof RefinedObjectType) {
//    		RefinedObjectType t = (RefinedObjectType)typ;
//			return (RefinedType.isStringType(t.getType()) && t.getRegions().contains(Main.badRegion));
//    	}
//    	return false;
//    }

    // return 'true' if 'e' represents a call of a method of a String-like class
    private Boolean isStringClassCall(InvokeExpr e) {
    	if (e instanceof InstanceInvokeExpr) {
			Local valThis = (Local) ((InstanceInvokeExpr)e).getBase();
			return hasStringType(valThis);
		}
		else if (e instanceof StaticInvokeExpr) {
			SootClass c = e.getMethod().getDeclaringClass();
			return RefinedType.isStringType(c.getType());
		}
    	Main.mainLog.severe("unhandled invoke expression type: " + e.getClass().getName());
		throw new RuntimeException("internal error");
    }
    
    TypeAndEffects handleStringMethodInvocation(IntraProcState in, InvokeExpr e) {
    	Main.mainLog.fine("String method invocation");
    	SootMethod m = e.getMethod();
    	
    	RefinedType retRefType = null; 
		if (RefinedType.isStringType(m.getReturnType())) {
			if (e instanceof InstanceInvokeExpr) {
				Local valThis = (Local) ((InstanceInvokeExpr)e).getBase();
				RefinedType thisType = in.getRefType(valThis);
				
				if (m.getName().equals("concat")) {
					Value valArg = e.getArgs().get(0);
					RefinedType argType = null;
					if (valArg instanceof Local)
						argType = in.getRefType((Local)valArg);
					else if (valArg instanceof Constant) {
						String lit = ((Constant)valArg).toString();
						argType = new RefinedStringType(TSA.mon.parseLiteral(lit));
					}
					else
						throw new RuntimeException("unhandled case: " + valArg);
					retRefType = thisType.join(argType);
				}
				else {
					Boolean stringArg = false;
					for (Value v: e.getArgs())
						if (RefinedType.isStringType(v.getType()))
							stringArg = true;
					if (stringArg) {
						Main.mainLog.severe("unhandled String call, argument is a string, method not 'concat': " + e);
						throw new RuntimeException("unhandled case");
					}
					else
						retRefType = thisType;
				}
			}
			else if (e instanceof StaticInvokeExpr) {
				if (m.getName().equals("valueOf")) {
					Value valArg = e.getArgs().get(0);
					if (valArg instanceof NullConstant) // String.valueOf(null) returns "null"
						retRefType = new RefinedStringType(TSA.mon.parseLiteral("null"));
					else if (valArg instanceof Local) {
						if (RefinedType.isStringType(valArg.getType()))
							retRefType = in.getRefType((Local)valArg);
						else {
							// String.valueOf(obj) returns obj.toString()
							Main.mainLog.info("transforming String.valueOf(obj) into obj.toString()");
							SootClass cArg = ((RefType)valArg.getType()).getSootClass();
							SootMethodRef newMRef = Scene.v().makeMethodRef(cArg, "toString", new LinkedList<Type>(), VoidType.v(), false);
							InvokeExpr newExpr = Jimple.v().newVirtualInvokeExpr((Local)valArg, newMRef, new LinkedList<Value>());
							retRefType = handleMethodInvocation(in, newExpr).getType();
						}
					}
					else {
						Main.mainLog.severe("unhandled call to valueOf: " + valArg.getClass());
						throw new RuntimeException("unhandled case");
					} 
				}
				else {
					Main.mainLog.severe("unhandled String call to static method; method not valueOf: " + e);
					throw new RuntimeException("unhandled case");
				}
			}
			else {
				Main.mainLog.severe("unhandled invoke expression: " + e);
				throw new RuntimeException("internal error");
			}
		}
		else if (e.getType() instanceof PrimType) { 
			retRefType = new RefinedNonRefType(e.getType());
		}
		else {
			Main.mainLog.severe("unhandled case: method of a String-like class returns non-string object");
			throw new RuntimeException("internal error");
		}
		
		return new TypeAndEffects(retRefType, new Effects(false));
	}
    
    Set<List<RefinedType>> crossProduct(List<RefinedType> a) {
    	int len = a.size();
    	Set<List<RefinedType>> ns = new TreeSet<List<RefinedType>>(new ListComparator());
    	if (len == 0) {
    		List<RefinedType> l = new LinkedList<RefinedType>();
    		ns.add(l);
    	} 
    	else {
    		RefinedType t = a.get(len-1);
    		Set<List<RefinedType>> s = crossProduct(a.subList(0, len-1));
    		Iterator<List<RefinedType>> it = s.iterator();
			while (it.hasNext()) {
				List<RefinedType> l = it.next();
				if (t instanceof RefinedObjectType) {
	    			RefType rt = ((RefinedObjectType)t).getType();
	    			for (Region r: ((RefinedObjectType)t).getRegions()) {
	    				List<RefinedType> nl = new LinkedList<RefinedType>(l);
	    				nl.add(new RefinedObjectType(rt, r));
	    				ns.add(nl);
	    			}	    			
	    		} else if (t instanceof RefinedStringType) {	    			
	    			for (Monoid e: ((RefinedStringType)t).getAnnot()) {
	    				List<RefinedType> nl = new LinkedList<RefinedType>(l);
	    				nl.add(new RefinedStringType(e));
	    				ns.add(nl);
	    			}	    			
	    		} else {
	    			List<RefinedType> nl = new LinkedList<RefinedType>(l);
	    			nl.add(t);
	    			ns.add(nl);
	    		}
			}
    	}
    	return ns;
    }

	RefinedObjectType getRefinedTypeOfThis(IntraProcState in, InvokeExpr e, Type retType) {
		RefType objType = null;
		Set<Region> objRegions = null;
		if (e instanceof SpecialInvokeExpr || e instanceof StaticInvokeExpr) {
			SootClass cls = e.getMethodRef().declaringClass();
			if (cls.getName().equals("java.lang.Object")) {
				// nothing to be checked in this case
				if (!retType.equals(VoidType.v()))
					throw new RuntimeException("internal error");
				return null;
			}
			objType = cls.getType();
		}
		
		if (e instanceof InstanceInvokeExpr) {
			Local valThis = (Local) ((InstanceInvokeExpr)e).getBase();
			if (! (e instanceof SpecialInvokeExpr)) {
				objType = (RefType) valThis.getType();
			}								
			objRegions = ((RefinedObjectType)(in.getRefType(valThis))).getRegions();
		} 
		else if (e instanceof StaticInvokeExpr) {
			objRegions = new TreeSet<Region>(new RegionComparator());
			objRegions.add(TSA.nilRegion);
		} 
		else {
			Main.mainLog.severe("unhandled invoke expression type: " + e.getClass().getName());
			throw new RuntimeException("internal error");
		}
		
		if (objType == null || objRegions == null)
			throw new RuntimeException("internal error");
		
		return new RefinedObjectType(objType, objRegions);
	}	
	

	public void addMethodTableEntries(SootMethodRef mRef, SootClass c, Region r, List<RefinedType> argTypes, Type retType) {
		outState.addToMethodTable(mRef, r, argTypes, null);
		List<SootClass> all;
		Hierarchy h = Scene.v().getActiveHierarchy();
		if (c.isInterface())						
			all = h.getImplementersOf(c);
		else
			all = h.getSubclassesOf(c);
		
		// TODO: if c = Object then we visit the whole hierarchy! 
		for (SootClass subC: all) {
			RefType t = subC.getType();
			if (! RefinedType.isStringType(t)) {
				RefinedObjectType ot = new RefinedObjectType(t, r);
				if (outState.typePool.contains(ot)) {
					Main.mainLog.finer("adding new entry for subclass " + subC + " of " + c);					
					SootMethodRef newRef = Scene.v().makeMethodRef(subC, 
							mRef.name(), mRef.parameterTypes(), mRef.returnType(), mRef.isStatic());

					outState.addToMethodTable(newRef, r, argTypes, null);
				}
				else 
					Main.mainLog.finer("typePool does not contain " + ot);
			}
			else 
				Main.mainLog.finer("typePool does not contain string type " + t);
		}
	}
	
	TypeAndEffects handleMethodInvocation(IntraProcState in, InvokeExpr e) {		
		SootMethodRef m  = e.getMethodRef();
		
		Type retType = m.returnType();
		RefinedType objRefType = getRefinedTypeOfThis(in, e, retType);		
		if (objRefType == null)
			return null;
		
		// find out which entry in the table we are looking for
		SootClass cls = ((RefType)objRefType.getType()).getSootClass();
		
		SootClass cM;
		if (cls.isInterface()) {			
			cM = cls;
		}
		else {
			cM = interProcAnalysis.getInheritedFrom(cls, m);
			Main.mainLog.finer(cls + " is a proper class; we use " + cM + " for the method entry");
		}
		
		SootMethodRef mRef;
		if (cM.equals(m.declaringClass()))
			mRef = m;
		else
			// NOTE: unclear to us what class declaringClass() returns!!!
			mRef = Scene.v().makeMethodRef(cM, m.name(), m.parameterTypes(), m.returnType(), m.isStatic());
		
		List<RefinedType> mTypesAsList = getListRefinedTypes(in, e.getArgs());
		mTypesAsList.add(0, objRefType);
		
		// from the refined type of the object and the arguments, obtain a set of atomic refined types 
		Set<List<RefinedType>> mTypes = crossProduct(mTypesAsList); 

		Set<RefinedType> setRefinedTypes = new TreeSet<RefinedType>();
		Effects setEffects = new Effects(true);
		for (List<RefinedType> entry: mTypes) {
			RefinedObjectType oType = (RefinedObjectType) entry.get(0);
			Set<Region> oRegions = oType.getRegions();
			if (oRegions.size() != 1) {
				throw new RuntimeException("internal error: not an atomic refined type");
				// NOTE: we could do a similar check for the argument types
				// or we could update the data type for the method table (to be as in the paper)
			}
			Region r = (Region) oRegions.toArray()[0];
			List<RefinedType> argTypes = entry.subList(1, entry.size());
			
			Main.mainLog.finer("Looking for entry: m = " + mRef + " r = " + r + " argTypes = "  + argTypes);
			
			TypeAndEffects typeAndEffects = null;
			if (inState.mTable.containsKey(mRef, r, argTypes)) {
				typeAndEffects = inState.mTable.get(mRef, r, argTypes);
				Main.mainLog.finer("entry found: " + typeAndEffects);
			}
			else {
				Main.mainLog.finer("entry not found in inState.mTable");
				// also add entries for all subclasses that declare this method
				addMethodTableEntries(mRef, cM, r, argTypes, retType);
				// just add an entry in the outState.mTable, so we get it from there
				typeAndEffects = outState.mTable.get(mRef, r, argTypes);
				if (typeAndEffects == null) {
					throw new RuntimeException("internal error: entry not found and not added");
					// typeAndEffects = TypeAndEffects.initTypeAndEffects(retType, cls.isLibraryClass());
					// Main.mainLog.warning("no entry added for: m = " + m + " r = " + r + " argTypes = "  + argTypes);
				}
			}
			setRefinedTypes.add(typeAndEffects.getType());
			setEffects = setEffects.union(typeAndEffects.getEffects());
		}
		
		if (setRefinedTypes.isEmpty()) {
			if (!setEffects.isEmpty())
				throw new RuntimeException("internal error");
			Main.mainLog.warning("empty region set for instance or one of the arguments!");
			// TODO: Should we rather stop? How?
			RefinedType retRefType = RefinedType.initRefinedType(retType, null);
			Effects effects = new Effects(false);
			return new TypeAndEffects(retRefType, effects);
		}
		else {
			RefinedType retRefType = RefinedType.join(setRefinedTypes);
			return new TypeAndEffects(retRefType, setEffects);
		}
    }    
	
	PosRegion getPosRegion(Stmt stm) {
		int lineNumber = stm.getJavaSourceStartLineNumber();
		LineNumberTag ntag = (LineNumberTag)stm.getTag("LineNumberTag");
		if (ntag.getLineNumber() != lineNumber)
			throw new RuntimeException("internal error, though not a big deal...");
		SootClass c = aMethod.getDeclaringClass();
		SourceFileTag ftag = (SourceFileTag)c.getTag("SourceFileTag");
		if (lineNumber > 1 && ftag != null) {
			return new PosRegion(c.getJavaPackageName() + ftag.getName(), lineNumber);
		} else {
			if (ftag == null) {
				Main.mainLog.severe("could not obtain file name. Exiting...");
				throw new RuntimeException("internal error");
			}
			if (lineNumber == -1) {
				Main.mainLog.severe("could not obtain line number. Exiting...");
				throw new RuntimeException("internal error");
			}
			else {
				Main.mainLog.severe("the corresponding region is already used. Exiting...");
				throw new RuntimeException("internal error");
			}
			
			
		}
	}
    
    // Computes the refined type of the value v, together with its effect.
    // * returns null when we can "safely ignore" the refined type of v 
    // (that is, we cannot compute it and this "does not matter")
    TypeAndEffects getTypeAndEffect(IntraProcState in, Value v, Stmt stm) {
    	RefinedType refType = null;
    	Type t = v.getType();
    	if (t instanceof PrimType && !(v instanceof InvokeExpr)) {
			Main.mainLog.finer("rv = " + v + " has type " + v.getType() + ", which is a PrimType: we do not track such values.");
			refType = new RefinedNonRefType(t);
		}

    	else if (v instanceof Immediate) {
			if (v instanceof Local) {
				refType = in.getRefType((Local) v);
			} else {
				if (!(v instanceof Constant))
					throw new RuntimeException("internal error");
				if (hasStringType(v))
					refType = new RefinedStringType();
				else if (v instanceof NullConstant) {
					refType = new RefinedNonRefType(t);
				}
				else {
					Main.mainLog.severe("rv = " + v + " has type " + v.getType() + " and its class is " + v.getClass());
					throw new RuntimeException("unhandled case");
				}
			}
		}
   	
		else if (v instanceof Expr) 
		{
			if (v instanceof InvokeExpr) {
				InvokeExpr e = (InvokeExpr)v;
				if (isStringClassCall(e))
					return handleStringMethodInvocation(in, e);
				else
					return handleMethodInvocation(in, e);
			} // end of  if (v instanceof InvokeExpr)
			
			else if (v instanceof AnyNewExpr) {
				if (hasStringType(v)) {
					refType = new RefinedStringType();
				}
				else {
					Region r = getPosRegion(stm);
					Type vrt = ((AnyNewExpr)v).getType();
					if (vrt instanceof RefType) {
						refType =  new RefinedObjectType((RefType)vrt, r);
						outState.addToTypePool((RefinedObjectType)refType);
					}
					else if (vrt instanceof ArrayType) {
						Type ut = ((ArrayType)vrt).getElementType();
						if (ut instanceof RefType) {
							if (RefinedType.isStringType(ut))
								refType = new RefinedArrayType(new RefinedStringType());
							else
								refType = new RefinedArrayType(new RefinedObjectType((RefType)ut, r));
						}
						else
							refType = new RefinedNonRefType(ut);
					}
					else {
						Main.mainLog.severe("AnyNewExpr: " + v + " is of type " + vrt);
						throw new RuntimeException("unhandled case");
					}
				}
			}

			else if (v instanceof CastExpr) {
				RefType castTo = null;
				if (v.getType() instanceof RefType)
					castTo = (RefType) v.getType();
				else if (v.getType() instanceof ArrayType)
					throw new RuntimeException("not yet handled"); 
				Value initVal = ((CastExpr)v).getOp();
				if (initVal instanceof Local) {
					RefinedType oldType = in.getRefType((Local)initVal);
					if (oldType instanceof RefinedObjectType) {
						Set<Region> regions = ((RefinedObjectType)oldType).getRegions();
						if (RefinedType.isStringType(castTo)) {
							// throw new RuntimeException("unhandled case (cast)");
							// we use the same regions as in the old type...
							Set<Region> sameRegions = ((RefinedObjectType) oldType).getRegions();
							// We convert Set<Region> to Set<MonoidElement>
							// TODO: How can this be done more efficiently?
							Set<Monoid> sameElements = new TreeSet<Monoid>();
							for(Region r: sameRegions) {
								if (r instanceof Monoid)
									sameElements.add((Monoid)r);
								else if (r.equals(TSA.nilRegion) || r.equals(TSA.unknownRegion))
									sameElements.add(TSA.mon.neutralElement());
								else
									throw new RuntimeException("internal error");
							}
							refType = new RefinedStringType(sameElements);

						}
						else
							refType = new RefinedObjectType(castTo, regions);
					}
					else if(oldType instanceof RefinedStringType) {
						if (RefinedType.isStringType(castTo)) {
							refType = oldType;
						}
						else
							throw new RuntimeException("unhandled case");
					}
					else 
						throw new RuntimeException("internal error");
				}
				else {
					Main.mainLog.severe("unhandled case (cast of a non-local): " + initVal);
					throw new RuntimeException("unhandled case");
				}
			}
			else if (v instanceof LengthExpr) {
				return null; // NOTE: left hand side must be a integer (ie primitive type); so it's safe to ignore
			}
			else {
				Main.mainLog.severe("unhandled expression type: " + v.getClass());
				throw new RuntimeException("unhandled case");
			}
		} 
    	
		else if (v instanceof Ref) {
			// two subcases: IdentityRef and ConcreteRef
			Main.mainLog.finer(v + " is instance of Ref: " + v.getClass());
			// if (v instanceof IdentityRef) {
			  // three subcases: ThisRef, ParameterRef, and CaughtExceptionRef 
			if (v instanceof ThisRef) {
				// example: r0 := @this: securibench.micro.basic.Basic30;
				Type vt = ((ThisRef)v).getType();
				refType = new RefinedObjectType(((RefType)vt), aThisRegion);
    		}
    		else if (v instanceof ParameterRef) {
    			// example: @parameter0: javax.servlet.http.HttpServletRequest
				refType = aArgTypes.get(((ParameterRef)v).getIndex());
    		}
    		else if (v instanceof CaughtExceptionRef) {
    			// NOTE: we treat such expressions as new expressions
    			// TODO: probably not the right way to deal with exceptions...
    			CaughtExceptionRef expt = (CaughtExceptionRef)v;
    			Region r = getPosRegion(stm);
    			RefType vrt = (RefType) expt.getType();
    			refType = new RefinedObjectType(vrt, r);
    		}
			// if (v instanceof ConcreteRef) 
			   // two subcases: FieldRef and ArrayRef
    		// if (v instanceof FieldRef) { 
			   // two subcases: InstanceFieldRef and StaticFieldRef 
    		else if (v instanceof InstanceFieldRef) {
    			// example: r4.<securibench.micro.basic.Basic30$Data: java.lang.String value1>
    			Local obj = (Local) ((InstanceFieldRef)v).getBase();
    			RefType c = (RefType) obj.getType();
    			// TODO: why not use getDeclaringClass() ?
    			RefinedObjectType objRefType = (RefinedObjectType) in.getRefType(obj);
    			SootField f = ((InstanceFieldRef)v).getField(); // TODO: update field table definition: use SootFieldRef instead of SootField?
    	    	Set<RefinedType> set = getAllFieldRefinedTypes(c, objRefType, f);
    	    	if (set.isEmpty())
    	    		refType = RefinedType.initRefinedType(v.getType(), null);
    	    	else
    	    		refType = RefinedType.join(set);
    		}
    		else if (v instanceof StaticFieldRef) {
    			SootField f = ((StaticFieldRef)v).getField();    			
    			RefType c = f.getDeclaringClass().getType();
    			RefinedType res = inState.fTable.get(c, TSA.nilRegion, f);
    			if (res == null) {
    				// TODO: see message
    				Main.mainLog.info("Entry not found in field table! Should we introduce it?");
    				refType = RefinedType.initRefinedType(f.getType(), null);
    			}
    			else
    				refType = res;
    		}
    		else if (v instanceof ArrayRef) {
    			// We use the refined type of the "base" value
    			ArrayRef a = (ArrayRef)v;
    			Value b = a.getBase();
    			RefinedArrayType rt = (RefinedArrayType) in.getRefType((Local)b);
    			if (rt == null)
    				Main.mainLog.severe("local variable " + b + " not found in table");
    			refType = rt.getRefinedType();
    		}
    		else {
    			// We should have treated all cases; still, we double-check...
    			Main.mainLog.warning("rvalue is another instance of Ref; not yet handled: " + v.getClass() + " (type = " + v.getType() + ")");
    			throw new RuntimeException("unhandled case");
    		}
		} // end of  if (rv instance of Ref)
		else { 
			// There should be no other case, that is, (rv instance of Immediate | Ref | Expr).
			Main.mainLog.severe("unknown case for rvalue: " + v.getClass());
			throw new RuntimeException("unhandled case");
		} // end of  if (rv instance of Immediate | Ref | Expr | _)
    	
    	
    	return new TypeAndEffects(refType, new Effects(false));
    }    

    @Override
	protected void flowThrough(IntraProcState in, Unit d, IntraProcState out) {
    	Main.mainLog.fine("analyzing unit: " + d.toString());
    	Main.mainLog.fine("in: " + in.toString());
    	
		out.getRefTypes().putAll(in.getRefTypes());
		out.effects = in.effects;

        if (!(d instanceof Stmt))
        	throw new RuntimeException("unhandled unit: " + d);
        
        Stmt stm = (Stmt)d;
        
        if (stm instanceof DefinitionStmt) {
        	Value lv = ((DefinitionStmt)stm).getLeftOp();
        	Value rv = ((DefinitionStmt)stm).getRightOp();

        	TypeAndEffects typeAndEffects = getTypeAndEffect(in, rv, stm);
        	
        	Main.mainLog.finer("lv := rv => getTypeAndEffect(rv) = " + typeAndEffects);

        	if (typeAndEffects == null) {
        		if (!(rv instanceof NullConstant) && !(rv instanceof LengthExpr))
        			throw new RuntimeException("getRefinedType(" + rv + ") = null");
        		// else we do nothing: note that we already copied in to out
        	}
        	else {
        		out.effects = out.effects.concat(typeAndEffects.effects);
        		
        		if (lv.getType() instanceof PrimType) {
        			Main.mainLog.fine("ignoring statement, as lv's type is a primitive type");        		
        		}
        		else {
        			RefinedType rvType = typeAndEffects.getType();
        			if (rvType == null) {
        				if (!(rv instanceof NullConstant))
        					throw new RuntimeException("RefinedType(" + rv + ") = null");
        				//  else we do nothing: note that we already copied in to out
        			}
        			else if (lv instanceof Local)
        				out.overrideRefType((Local)lv, rvType);
        			else if (lv instanceof FieldRef) {
        				FieldRef fRef = ((FieldRef)lv);
        				SootField f = fRef.getField();
        				RefType c = f.getDeclaringClass().getType();

        				if (lv instanceof InstanceFieldRef) { // case o.f = rv
        					Local obj = (Local) ((InstanceFieldRef)fRef).getBase();
        					RefinedObjectType objType = (RefinedObjectType) in.getRefType(obj);
        					RefType objt = (RefType) obj.getType();
        					for (Region r: objType.getRegions())        						
        						outState.updateFTable(objt, r, f, rvType);
        				}
        				else { // case C.f = rv
        					if (!(lv instanceof StaticFieldRef))
        						throw new RuntimeException("internal error " + lv);
        					RefinedType fType = inState.fTable.get(c, TSA.nilRegion, fRef.getField());
        					if (fType == null || fType.subType(rvType)) {
        						outState.updateFTable(c, TSA.nilRegion, f, rvType);
        					}
        				}
        			}
        			else if (lv instanceof ArrayRef) {
        				Value base = ((ArrayRef)lv).getBase();
        				out.augmentRefType((Local)base, new RefinedArrayType(rvType));
        			}
        			else {
        				// There should be no other case:
        				// From Revi's master thesis, we can have:
        				// * local = rvalue                 --- already handled
        				// * field = immediate              --- already handled
        				// * local.field = immediate        --- already handled
        				// * local[immediate] = immediate   --- already considered, not handled
        				Main.mainLog.severe("unhandled case: left value of AssignStmt: " + stm + " is " + lv);
        				throw new RuntimeException("unhandled case");
        			}
        		}
        	}
        }        
        else if (stm instanceof InvokeStmt) {
        	InvokeExpr e = stm.getInvokeExpr();
        	if (!isStringClassCall(e)) {
        		TypeAndEffects typeAndEffects = handleMethodInvocation(in, e);
        		if (typeAndEffects != null) 
            		out.effects = out.effects.concat(typeAndEffects.effects);
        	}
        	else {
        		SootMethodRef m = e.getMethodRef();
        		if (m.name().equals("<init>") && e.getArgs().size() > 0) {
        			Type typ = m.parameterType(0);
        			if (typ instanceof RefType) {
        				RefType t = (RefType) typ;
        				if (t.getClassName().equals("java.lang.String")) {
        					Local valThis = (Local) ((InstanceInvokeExpr)e).getBase();
            				Value arg = e.getArgs().get(0);
            				if (arg instanceof Local)
            					out.augmentRefType(valThis, in.getRefType((Local)arg));
        				}
        				else {
        					Main.mainLog.severe("unhandled case: String.<init>(" + t.getClassName() + ")");
            				throw new RuntimeException("unhandled case");
        				}
        			}
        		}
        	}
        }
        else if (stm instanceof ReturnStmt) {
        	Effects mEffects = in.getEffects();
        	Effects newEffects = aEffects.union(mEffects);
        	// TODO: need to update out.Effects?
        	// RefinedType mRetType = in.getRefType(?);  
        	Value v = ((ReturnStmt)stm).getOp();
        	if (v instanceof Local) {
        		RefinedType vType = in.getRefType((Local)v);
        		if (!(v.getType() instanceof PrimType)) {
        			if (!vType.subType(aRetType) || !newEffects.equals(aEffects)) {
        				Main.mainLog.finer(vType + " NOT subtype of " + aRetType + ": update to the join of the two: " + aRetType.join(vType));
        				outState.addToMethodTable(aMethodRef, aThisRegion, aArgTypes, new TypeAndEffects(aRetType.join(vType), newEffects));
        			}
        			else {
        				Main.mainLog.finer(vType + " subtype of " + aRetType + ": all ok, no update needed.");
        			}
        		}
        	}
        	else if (v instanceof Constant) {        		
//        		if (!(v instanceof NullConstant) && !(v.getType() instanceof PrimType)) {
//        			Main.mainLog.finer(v + " is a non-null constant of a non-primitive type, namely " + v.getType());
//        			throw new RuntimeException("unhandled case");
//        		}
        		if (!newEffects.equals(aEffects)) {
        			if (!(aRetType instanceof RefinedNonRefType))
        				throw new RuntimeException("internal error"); 
        			TypeAndEffects newte = new TypeAndEffects(aRetType, newEffects);
        			outState.addToMethodTable(aMethodRef, aThisRegion, aArgTypes, newte);
            	}
        	}
        	else {
        		throw new RuntimeException("unhandled case in ReturnStmt: " + v);
        	}
        	// nothing to be done!
		} 
        else if (stm instanceof ReturnVoidStmt) {
        	if (!aRetType.getType().equals(VoidType.v()))
        		throw new RuntimeException("[flowThrough] internal error: ReturnVoidStmt and retType != void");
        	
        	Effects mEffects = in.getEffects();
        	Effects newEffects = aEffects.union(mEffects);
        	if (!newEffects.equals(aEffects)) {
        		TypeAndEffects newte = new TypeAndEffects(new RefinedNonRefType(VoidType.v()), newEffects);
        		outState.addToMethodTable(aMethodRef, aThisRegion, aArgTypes, newte);
        	}        	
        }
        else if (stm instanceof RetStmt) {
			// TODO: What is the difference between RetStmt and ReturnStmt?
			throw new RuntimeException("[flowThrough] RetStmt not handled");
        } 
        else if (stm instanceof GotoStmt) {
        	// nothing to be done!
        } else if (stm instanceof NopStmt) {
        	// nothing to be done!
        } else if (stm instanceof IfStmt) {
        	// nothing to be done!
        } else if (stm instanceof SwitchStmt) {
        	// nothing to be done!
        } else if (stm instanceof ThrowStmt) {
        	// nothing to be done!
        } else {
        	Main.mainLog.severe("unhandled statement: " + stm.toString());
        	Main.mainLog.severe("statement's type is: " + stm.getClass().getName() + " (or " + stm.getClass().getName() + ")");
        	throw new RuntimeException("unhandled case");
        }
        
        Main.mainLog.fine("out: " + out + "\n");
    }

	@Override
	protected void merge(IntraProcState in1, IntraProcState in2, IntraProcState out) {
//		Main.mainLog.finer("merging in1 = " + in1); 
//		Main.mainLog.finer("   with in2 = " + in2);
//		Main.mainLog.finer("        out = " + out);
		// NOTE: 'out' seems to be built with newInitialFlow or entryInitialFlow
		
		Map<Local, RefinedType> in1Types = in1.getRefTypes();
		Map<Local, RefinedType> in2Types = in2.getRefTypes();
		Map<Local, RefinedType> outTypes = out.getRefTypes();
		for(Local v: in1Types.keySet()) {
			RefinedType vType1 = in1Types.get(v);
			if (in2Types.containsKey(v)) {
				RefinedType vType2 = in2Types.get(v);
				outTypes.put(v, vType1.join(vType2));
			} else
				outTypes.put(v, vType1);
		}
		
		for (Local v: in2Types.keySet()) {
			RefinedType vType2 = in2Types.get(v);
			if (!in1Types.containsKey(v)) {
				outTypes.put(v, vType2);
			}
		}
		
		out.effects = out.effects.union(in1.effects).union(in2.effects);
		
		// Main.mainLog.finer(" the result = " + out + "\n");
	}

	@Override
	protected void copy(IntraProcState from, IntraProcState to) {
//		Main.mainLog.finer("copying from = " + from); 
//		Main.mainLog.finer("       to to = " + to);
		// NOTE: 'to' seems to be built with "new AbstractState" 
		//       and not with newInitialFlow or entryInitialFlow
		
		to.getRefTypes().putAll(from.getRefTypes());
		to.effects.getSet().addAll(from.effects.getSet());
		
//		Main.mainLog.finer(" the result = " + to + "\n");
	}

	@Override
	protected IntraProcState newInitialFlow() {
		IntraProcState s = new IntraProcState();
		copy(this.initState, s);
		return s;
	}

	@Override
	protected IntraProcState entryInitialFlow() {
		return newInitialFlow();
	}
}
