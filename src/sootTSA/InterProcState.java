package sootTSA;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethodRef;
import soot.Type;
import soot.util.Chain;

public class InterProcState {
	public MethodTable mTable;
	public FieldTable fTable;
	public Set<RefinedObjectType> typePool;
	
	private TypingInfo typeInfo;
	
	public InterProcState(MethodTable builtinMTable, FieldTable builtinFTable, TypingInfo tInfo) {
		mTable = new MethodTable(builtinMTable);
		fTable = new FieldTable(builtinFTable);
		typePool = new TreeSet<RefinedObjectType>();
		
		typeInfo = tInfo;
	}
	
	public InterProcState(InterProcState state, TypingInfo tInfo) {
		mTable = new MethodTable(state.mTable); 
		fTable = new FieldTable(state.fTable);
		typePool = new TreeSet<RefinedObjectType>(state.typePool);
		
		typeInfo = tInfo;
	}
	
    void updateFTable(RefType c, Region r, SootField f, RefinedType newt) {
    	RefinedType oldt = fTable.get(c, r, f);
    	if (oldt == null) 
    		fTable.put(c, r, f, newt);
    	else 
    		if (!newt.subType(oldt)) {
    			fTable.put(c, r, f, newt.join(oldt));
    		}
    		else {
    			Main.mainLog.info(newt + " sub-type of " + oldt);
    		}
    }
    
    public TypeAndEffects initTypeAndEffects(SootMethodRef mRef, SootClass c, Region r, List<RefinedType> argTypes) {
    	TypeAndEffects typeAndEffects;
    	Type retType = mRef.returnType();
    	Boolean library = c.isLibraryClass();
    	if (library) {
    		typeAndEffects = typeInfo.mTable.getMatch(mRef, argTypes);
    		if (typeAndEffects == null) 
    			typeAndEffects = TypeAndEffects.initTypeAndEffects(retType, library);
    	}
    	else
    		typeAndEffects = TypeAndEffects.initTypeAndEffects(retType, library);

		return typeAndEffects;		
	}
    
    // Add a new entry in the table, and then close it by "supertyping" 
    // (thus ensuring well-formedness)
    void addToMethodTable(SootMethodRef mRef, Context ctx, Region r, List<RefinedType> argTypes, TypeAndEffects newTypeAndEffects) {    	
    	SootClass c = mRef.declaringClass();
    	Main.mainLog.finer("args: " + mRef + ", " + r + ", " + argTypes + ": " + newTypeAndEffects);
    	
    	// update current entry if needed
    	TypeAndEffects oldTypeAndEffects, updatedTypeAndEffects;
    	boolean existingEntry;
    	if (mTable.containsKey(mRef, ctx, r, argTypes)) {
    		Main.mainLog.finer("outState.mTable contains entry");
			oldTypeAndEffects = mTable.get(mRef, ctx, r, argTypes);
			existingEntry = true;
    	}
    	else {
    		Main.mainLog.finer("outState.mTable does not contain entry");
    		oldTypeAndEffects = initTypeAndEffects(mRef, c, r, argTypes); // that is, this is the implicit entry
    		existingEntry = false;
    	}
    		
    	if (newTypeAndEffects == null)
    		updatedTypeAndEffects = oldTypeAndEffects;
    	else
    		updatedTypeAndEffects = oldTypeAndEffects.join(newTypeAndEffects);
    	
		if (!existingEntry || !updatedTypeAndEffects.equals(oldTypeAndEffects)) {
			String expl = existingEntry ? "updated" : "new";
			Main.mainLog.finer(expl + " entry for " + mRef + ", " + r + ", " + argTypes + ": " + updatedTypeAndEffects);
			mTable.put(mRef, ctx, r, argTypes, updatedTypeAndEffects);
		}
		else {
			Main.mainLog.finer("existing entry, no update needed for " + mRef + ", " + r + ", " + argTypes + ": " + updatedTypeAndEffects);
		}
		
    	// update entries for superclasses
    	if (! c.equals(Scene.v().getSootClass("java.lang.Object"))) {
			if (! c.isInterface()) {
				SootClass sCls = c.getSuperclass();
				SootMethodRef newRef = Scene.v().makeMethodRef(sCls, 
						mRef.name(), mRef.parameterTypes(), mRef.returnType(), mRef.isStatic());
				addToMethodTable(newRef, ctx, r, argTypes, updatedTypeAndEffects);
			}
			Chain<SootClass> superCls = c.getInterfaces();
			for (SootClass sIntf: superCls) {
				SootMethodRef newRef = Scene.v().makeMethodRef(sIntf, 
						mRef.name(), mRef.parameterTypes(), mRef.returnType(), mRef.isStatic());
				addToMethodTable(newRef, ctx, r, argTypes, updatedTypeAndEffects);
			}
		}
    }
    
    // Add a new atomic refined type to the pool, and then close it by "supertyping"
    void addToTypePool(RefinedObjectType refinedT) {
    	if (!typePool.contains(refinedT)) {
    		typePool.add(refinedT);
    		
    		// Also add all super refined types
    		SootClass c = refinedT.getType().getSootClass();
    		if (refinedT.getRegions().size() != 1)
    			throw new RuntimeException("internal error: the type pool only contains atomic types");
    		Region r = refinedT.getRegions().iterator().next();
    		if (! c.equals(Scene.v().getSootClass("java.lang.Object"))) {
    			if (! c.isInterface()) {
    				SootClass sCls = c.getSuperclass();
    				addToTypePool(new RefinedObjectType(sCls.getType(), r));
    			}
    			Chain<SootClass> superCls = c.getInterfaces();
    			for (SootClass sIntf: superCls)
    				addToTypePool(new RefinedObjectType(sIntf.getType(), r));
    		}
    	}
    }


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		InterProcState other = (InterProcState) obj;
		
		return (mTable.equals(other.mTable) && fTable.equals(other.fTable) && typePool.equals(other.typePool));
	}
}
