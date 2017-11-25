package typetranslation;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import comparators.ToStringComparator;
import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import sootTSA.Main;


public class Transform {
	// we store the mapping between old methods and new methods in a map
	Map<SootMethod, SootMethod> methodMap = 
			new TreeMap<SootMethod, SootMethod>(new ToStringComparator<SootMethod>());
	
	TypeMap typeMap;
	
	public Transform() {
		// for (String className: TypeMap.nameMap.values()) {
//		for (Entry<String, String> entry: TypeMap.nameMap.entrySet()) {
//			String classKey = entry.getKey();
//			String classVal = entry.getValue();
//			if (Scene.v().containsClass(classKey)) {
//				// Main.mainLog.finer("loading " + classVal);
//				// System.out.println("scene contains " + classKey);
//				Scene.v().addBasicClass(classVal, SootClass.BODIES);
//				Scene.v().loadClassAndSupport(classVal).setApplicationClass();
//			}
//		}
		typeMap = new TypeMap();
	}

	public void transform() {
		// We proceed in two phases:
		// * First, we build empty new methods with updated signatures
		// * Second, for application classes, we build the bodies of the new methods from the bodies 
		//   of the old methods, we remove the old methods, and we attach the new methods to their classes.		
		//   We do the same for library classes, however we don't update each method's body 
		//   (since they don't have one), we just update each method's signature.
		//   A special case are the classes corresponding to those in 'mockup'. For those, 
		//   we just moved the method declarations from the initial class to the corresponding class in 'mockup', 
		//   except for those which already have an implementation in 'mockup'. 
		
		// 1st phase: build new methods
		for (SootClass c: Scene.v().getApplicationClasses()) {
			// System.out.println("transforming " + c);
			if (!c.getName().startsWith("mockup")) { // we don't transform classes in "mockup"
				Iterator<SootField> it = c.getFields().snapshotIterator();
				while (it.hasNext()) {
					SootField f = it.next();
					Type type = f.getType();
					Type newType = typeMap.getNewType(type);
					if (newType != type) {
						c.removeField(f);
						c.addField(new SootField(f.getName(), newType, f.getModifiers()));
					}
				}
				for (SootMethod m: c.getMethods()) {
					// System.out.println("transforming " + m);
					SootMethod newm = transformMethodSig(m, true);
					methodMap.put(m, newm);					
				}
			}
		}
		for (SootClass c: Scene.v().getLibraryClasses()) {
			if (c.resolvingLevel() >= SootClass.SIGNATURES) {
				Boolean fromOurLib = TypeMap.nameMap.containsKey(c.getName());	
				// TODO: need to update fields??
				for (SootMethod m: c.getMethods()) {
					SootMethod newm = transformMethodSig(m, false);
					if (newm != m || fromOurLib)
						methodMap.put(m, newm);
				}
			}
		}
		
		// 2nd phase: transform the bodies		
		for (Entry<SootMethod, SootMethod> entry: methodMap.entrySet()) {
			SootMethod m = entry.getKey();
			SootMethod newm = entry.getValue();
			SootClass c = m.getDeclaringClass();
			// System.out.println("m = " + m + "  class: " + m.getDeclaringClass());
			if (!c.getName().startsWith("mockup")) {
				if (c.isApplicationClass() && (c.isConcrete() || (c.isAbstract() && !c.isInterface()))) {
					// System.out.println("  updating body");
					Body body = m.retrieveActiveBody();
					new BodyTransformer(body, methodMap, typeMap);
					newm.setActiveBody(body);
				}
			}
		}
		
		// 3rd phase: transform the signatures 
		for (Entry<SootMethod, SootMethod> entry: methodMap.entrySet()) {
			SootMethod m = entry.getKey();
			SootMethod newm = entry.getValue();
			SootClass c = m.getDeclaringClass();
			// System.out.println("m = " + m + "  class: " + m.getDeclaringClass());
			if (!c.getName().startsWith("mockup")) {
				if (c.resolvingLevel() >= SootClass.SIGNATURES) {
					// System.out.println("  updated signature: " + newm);
					c.removeMethod(m);
					RefType type = c.getType();
					RefType newType = typeMap.getNewRefType(type);
					// System.out.println("  oldType: " + type + "  newType: " + newType);
					if (newType != type) {
						// System.out.println("  oldType: " + type + "  newType: " + newType);
						SootClass newc = newType.getSootClass();
						if (!newc.declaresMethod(newm.getSubSignature())) {
							newc.addMethod(newm);
							newm.setDeclaringClass(newc);							
//							if (m.getName().equals("<clinit>")) {
//								System.out.println("  method " + m.getSubSignature() + " from " + c + " declared now also in " + newc);
//								Body b = m.retrieveActiveBody();
//								for (Unit u: b.getUnits())
//									System.out.println("    " + u);
//							}
						}
					}
					else {
						c.addMethod(newm);
						newm.setDeclaringClass(c);
						// System.out.println("  updated signature: " + newm);
					}					
				}
			}
		}
	}
	
	// For application classes we build new methods, because their bodies 
	// may contain types that we want to transform, even though their signature 
	// does not change. For library classes, we only build new methods 
	// if the signature changes. Even this might not be needed.
	private SootMethod transformMethodSig(SootMethod m, Boolean app) {
		List<Type> parameterTypes = m.getParameterTypes();
		List<Type> newParameterTypes = typeMap.getNewTypeList(parameterTypes);
		Type returnType = m.getReturnType();
		Type newReturnType = typeMap.getNewType(returnType);
		if (app || newParameterTypes != parameterTypes || newReturnType != returnType)
			return new SootMethod(m.getName(), newParameterTypes, 
					newReturnType, m.getModifiers(), m.getExceptions());
		else
			return m;
	}
}

