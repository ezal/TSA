package sootTSA;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import comparators.ToStringComparator;
import soot.Body;
import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.VoidType;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;
import soot.util.HashChain;
import soot.util.NumberedString;

public class InterProcAnalysis {
	TypingInfo typeInfo;
	private InterProcState state;
	private int crtIter = 0;
	private Map<SootMethodRef, Set<SootMethod>> overrides;
	// NOTE: this name is misleading; see how this map is built
	
	private Map<SootMethodRef, SootClass> inheritedFrom;
	
	private Map<SootMethodRef, ExceptionalUnitGraph> cfgs;
	
	private Region thisRegion;
	private List<RefinedType> epArgRefTypes;
	
	public Effects getEffects() {
		TypeAndEffects te = state.mTable.get(typeInfo.epMethod.makeRef(), thisRegion, epArgRefTypes);
		return te.effects;
	}
	
	private Set<SootClass> getLeafClasses(SootClass c) {
		Hierarchy h = Scene.v().getActiveHierarchy();
		Set<SootClass> res = new TreeSet<SootClass>(new ToStringComparator<SootClass>()); 
		if (c.isInterface()) {
			for (SootClass subc: h.getDirectSubinterfacesOf(c))
				res.addAll(getLeafClasses(subc));
			for (SootClass subc: h.getDirectImplementersOf(c))
				res.addAll(getLeafClasses(subc));
		}
		else {		 		
			List<SootClass> subclasses = h.getDirectSubclassesOf(c);
			if (subclasses.isEmpty())
				res.add(c);
			else
				for (SootClass subc: subclasses)
					res.addAll(getLeafClasses(subc));
		}
		return res;
	}

	public InterProcAnalysis(TypingInfo info) {
		this.typeInfo = info;
		state = new InterProcState(new MethodTable(), new FieldTable(), typeInfo);
		
		// We put the entry point in the state
		// While we do this, we also populate the typePool
		SootMethod m = info.epMethod;		
		List<Type> argsTypes = m.getParameterTypes();
		epArgRefTypes = new LinkedList<RefinedType>();
		
		int cnt = 1;
		for (Type typ: argsTypes) {
			// NOTE: we assume the non-string object parameters are all 
			// in pairwise different regions
			Region r = new PosRegion("I", cnt++);
			RefinedType t = RefinedType.initRefinedType(typ, r);
			epArgRefTypes.add(t);

			if (typ instanceof RefType) {
				RefType rt = (RefType) typ;
				SootClass c = rt.getSootClass();				
				for (SootClass subc: getLeafClasses(c))
					state.addToTypePool(new RefinedObjectType(subc.getType(), r));
			}
		}
		
		if (m.isStatic()) 
			thisRegion = TSA.nilRegion;
		else			
			thisRegion = new PosRegion("I", 0);
		
		RefinedType retType = RefinedType.initRefinedType(m.getReturnType(), null);
		Effects effects = new Effects(false);
		TypeAndEffects typeAndEffect = new TypeAndEffects(retType, effects);

		// TODO: need to call addToMethodTable instead??
		state.mTable.put(m.makeRef(), thisRegion, epArgRefTypes, typeAndEffect);
		
		overrides = new TreeMap<SootMethodRef, Set<SootMethod>>(new ToStringComparator<SootMethodRef>());		
		inheritedFrom = new TreeMap<SootMethodRef, SootClass>(new ToStringComparator<SootMethodRef>());
		cfgs = new TreeMap<SootMethodRef, ExceptionalUnitGraph>(new ToStringComparator<SootMethodRef>());
	}
	
	public int getIter() {
		return crtIter;
	}
	
	private SootClass searchUp(SootClass crt, NumberedString sig) {
    	Main.mainLog.finer("searching in " + crt + " for " + sig);
    	if (crt.declaresMethod(sig)) {
    		SootMethod m = crt.getMethod(sig);
    		if (m.isNative())
    			throw new RuntimeException("cannot deal with native methods yet");
    		if (!m.isAbstract()) {
    			// NOTE: this call is only for debugging purposes; that is, 
    			// it has no other purpose that checking that it succeeds
    			if (crt.isApplicationClass())
    				m.retrieveActiveBody(); 
    			Main.mainLog.finer("found");
    			return crt;
    		}
    	}
    	if (crt.isInterface()) {
    		Main.mainLog.warning(crt + " is an interface; searchUp is flawed for interfaces!");
    		return null;
    	}
    	else 
    		if (crt.hasSuperclass())
    			return searchUp(crt.getSuperclass(), sig);
    		else
    			return null;
    }
	
	public SootClass getInheritedFrom(SootClass c, SootMethodRef mRef) {
		SootClass from = null;
		if (inheritedFrom.containsKey(mRef)) {
			from = inheritedFrom.get(mRef);
		}
		else {
			from = searchUp(c, mRef.getSubSignature());
			inheritedFrom.put(mRef, from);
		}
		return from;
	}
	
	private Set<SootMethod> findOverrides(SootMethodRef mRef) {
		NumberedString sig = mRef.getSubSignature();
	
		Set<SootMethod> methods = new TreeSet<SootMethod>(new ToStringComparator<SootMethod>());
		
		// Note: Any class (except Object) extends exactly one other class (implicitly Object)
		SootClass mCls = mRef.declaringClass();
		SootClass crtCls = mCls;
		while (crtCls.hasSuperclass()) {
			crtCls = crtCls.getSuperclass();
			Main.mainLog.fine("Searching in class " + crtCls);
			//if (!crtCls.getName().equals("java.lang.Object") && crtCls.declaresMethod(name, params, ret)) {
			if (crtCls.declaresMethod(sig)) {
				Main.mainLog.fine("method found in class " + crtCls);
				methods.add(crtCls.getMethod(sig));
			}
		}
		
		// We now look at interfaces.
		SortedSet<SootClass> superInterfaces = new TreeSet<SootClass>(new ToStringComparator<SootClass>());
		superInterfaces.addAll(mCls.getInterfaces());
		while (!superInterfaces.isEmpty()) {
			SootClass crtIntf = superInterfaces.first();
			superInterfaces.remove(crtIntf);
			Main.mainLog.fine("Searching in interface " + crtIntf);
			if (crtIntf.declaresMethod(sig)) {
				Main.mainLog.fine("method found in interface " + crtIntf);
				methods.add(crtIntf.getMethod(sig));
			}	
			else {
				// System.out.println(crtIntf + " has " + crtIntf.getMethods());
				superInterfaces.addAll(crtIntf.getInterfaces());
			}
		}
		
		return methods;
	}
	
	public Set<SootMethod> getOverrides(SootMethodRef mRef) {
		Set<SootMethod> overridden = null;
		if (overrides.containsKey(mRef)) {
			overridden = overrides.get(mRef);
		}
		else {
			overridden = findOverrides(mRef);
			overrides.put(mRef, overridden);
		}
		return overridden;
	}
	
	ExceptionalUnitGraph getCFG(SootClass c, SootMethodRef mRef) {
		if (cfgs.containsKey(mRef))
			return cfgs.get(mRef);
		else {
			ExceptionalUnitGraph unitGraph = null;
			NumberedString sig = mRef.getSubSignature();
			if (!c.isInterface() && c.isApplicationClass() && c.declaresMethod(sig)) {
				SootMethod m = c.getMethod(sig);
				if (m.isConcrete()) {
					Body b = m.retrieveActiveBody();
					unitGraph = new ExceptionalUnitGraph(b);					
				}
			}
			cfgs.put(mRef, unitGraph);
			return unitGraph;
		}
	}
	
	public void doAnalysis(int maxIter) {
		Set<SootClass> staticInitClasses = new TreeSet<SootClass>(new ToStringComparator<SootClass>());
		// Main.mainLog.info("[InterProcAnalysis] initial mTable:\n" + mTable);
		InterProcState oldState = null;
		do {
			if (crtIter >= maxIter)
				break;
			
			crtIter++;
			
			oldState = new InterProcState(state, typeInfo);
			
			Main.mainLog.info("======== Iteration: " + crtIter + "\n");
			Main.mainLog.info("At the beginning of iteration, old tables: \n" + oldState.fTable + oldState.mTable + oldState.typePool);
			
			// we go through each entry in the method table
			// more precisely, we first go through each method
			// TODO: this is inefficient, as we probably do not need to do this:
			// we probably just need to go through the "new" entries
			for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> mEntry: oldState.mTable.getTable().entrySet()) 
			{
				SootMethodRef mRef = mEntry.getKey();
				SootClass c = mRef.declaringClass();
//				if (!c.equals(TypeBasedAnalysis.getDeclaringClass(mRef))) {
//					Main.mainLog.severe("entries in the tables should only be for methods declared in some class");
//					throw new RuntimeException("internal error");
//				}
				
				Main.mainLog.info("====== Analyzing method: " + mRef + " (class: " + c + ")");

				if (c.isApplicationClass() && c.declaresMethodByName("<clinit>") && !staticInitClasses.contains(c)) {
					Main.mainLog.info("class with static initializer: " + c);
					staticInitClasses.add(c);
					SootMethod staticInit = c.getMethodByName("<clinit>");
					SootMethodRef staticInitRef = staticInit.makeRef();
					if (!staticInit.isStaticInitializer())
						throw new RuntimeException("internal error");
					 
					if (!oldState.mTable.getTable().containsKey(staticInitRef)) {
						RefinedType refType = RefinedType.initRefinedType(staticInit.getReturnType(), null);
						state.mTable.put(staticInitRef, 
								TSA.nilRegion, 
								new LinkedList<RefinedType>(),
								new TypeAndEffects(refType, new Effects(false)));
					}
				}
				
				ExceptionalUnitGraph unitGraph = getCFG(c, mRef);
				if (unitGraph != null) { // that is, the method has a body				
					for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> rEntry: mEntry.getValue().entrySet()) 
					{
						Region rThis = rEntry.getKey();
						for (Entry<List<RefinedType>, TypeAndEffects> aEntry: rEntry.getValue().entrySet()) {
							List<RefinedType> argsTypes = aEntry.getKey();
							TypeAndEffects retTypeAndEffects = aEntry.getValue();

							Main.mainLog.info("==== Analyzing entry: " + mRef + " for " + rThis + " " + argsTypes + " => " + retTypeAndEffects + "\n");

							IntraProcAnalysis intra = new IntraProcAnalysis(this, mRef, unitGraph, typeInfo, state, rThis, argsTypes, retTypeAndEffects);
							
							state = intra.getOutState();
							TypeAndEffects newRetTypeAndEffects = state.mTable.get(mRef, rThis, argsTypes);
							if (!retTypeAndEffects.equals(newRetTypeAndEffects))
								Main.mainLog.info("old type and effects: " + retTypeAndEffects + " new type and effects: "+ newRetTypeAndEffects);
							Main.mainLog.info("==== Done analyzing entry\n");
						}
					}
				}
			}
			
			// we do something similar for the field table
			for (Entry<RefType, Map<Region, Map<SootField, RefinedType>>> cEntry: oldState.fTable.getTable().entrySet()) 
			{
				RefType c = cEntry.getKey();
				for (Entry<Region, Map<SootField, RefinedType>> rEntry: cEntry.getValue().entrySet()) {
					Region r = rEntry.getKey();
					for (Entry<SootField, RefinedType> fEntry: rEntry.getValue().entrySet()) {
						SootField f = fEntry.getKey();
						RefinedType t = fEntry.getValue();
						SootClass d = f.getDeclaringClass();
						if (!d.getType().equals(c))
							state.updateFTable(d.getType(), r, f, t);
					}
				}
			}

			debugEquals(oldState, crtIter);
		}
		while (!state.equals(oldState));
		
		// NOTE: the reporting is done in Main.main()
	}
	
	private void debugEquals(InterProcState oldState, int i) {
		Main.mainLog.info("At end of iteration " + i + ", old tables: \n" + oldState.fTable + oldState.mTable + oldState.typePool + "\n");
	}
}
