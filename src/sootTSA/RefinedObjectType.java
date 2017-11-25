package sootTSA;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import comparators.RegionComparator;
import soot.Hierarchy;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;

public class RefinedObjectType extends RefinedType {
	private RefType cls;
	private Set<Region> regions;

	public RefinedObjectType(RefType c) {
		if (RefinedType.isStringType(c))
			throw new RuntimeException("internal error: use RefinedStringType instead");
		cls = c;
		// TODO: add a comparator function (we have done it somewhere else...)
		regions = new TreeSet<Region>(new RegionComparator());
	}
	
	public RefinedObjectType(RefinedType t) {
		RefinedObjectType ot = (RefinedObjectType)t;  
		cls = ot.cls;
		regions = new TreeSet<Region>(new RegionComparator());
		regions.addAll(ot.regions);
	}

	public RefinedObjectType(RefType c, Region r) {
		if (RefinedType.isStringType(c))
			throw new RuntimeException("internal error: use RefinedStringType instead");
		cls = c;
		regions = new TreeSet<Region>(new RegionComparator());
		if (r != null)
			regions.add(r);
	}
	
	public RefinedObjectType(RefType c, Set<Region> regs) {
		if (RefinedType.isStringType(c))
			throw new RuntimeException("internal error: use RefinedStringType instead");
		cls = c;
		regions = regs;
	}
	
//	public RefinedObjectType(Boolean b) {
//		Region r = b ? Main.badRegion : Main.goodRegion;		
//		RefinedObjectType t = new RefinedObjectType(RefinedType.getStringType(), r);
//		cls = t.cls;
//		regions = t.regions;
//	}

	public RefType getType() {
		return cls;
	}

	public Set<Region> getRegions() {
		return regions;
	}

	public void setRegions(Set<Region> reg) {
		this.regions = reg;
	}
	
	public String toString() {
		String str = "ObjType[" + cls + ", " + regions + "]";
		return str;
	}
	
	// NOTE: we look at classes only, not at interfaces!	
	SootClass getLeastCommonSuperclassOf(SootClass c1, SootClass c2) {
		Hierarchy h = Scene.v().getActiveHierarchy();
		// NOTE: that method is not implemented yet...
		// h.getLeastCommonSuperclassOf(c1, c2);
		List<SootClass> l1 = h.getSuperclassesOf(c1);
		List<SootClass> l2 = h.getSuperclassesOf(c2);
		
		// TODO: the following implementation assumes that 
		// classes in l1 and l2 are listed the "right" order 
		for (SootClass i1: l1)
			if (l2.contains(i1))
				return i1;
		
		throw new RuntimeException("internal error");
	}

	@Override
	public RefinedType join(RefinedType t2) {
		if (t2 instanceof RefinedNonRefType)
			if (t2.getType() instanceof NullType)
				return this;
			else
				throw new RuntimeException("join failure: " + this + " with " + t2);

		else if (t2 instanceof RefinedArrayType)
			return t2.join(this);
		
		else if (t2 instanceof RefinedStringType) {
			RefType clsObject = Scene.v().getRefType("java.lang.Object"); 
			RefinedObjectType out = new RefinedObjectType(clsObject);
			out.regions.addAll(regions);
			out.regions.addAll(((RefinedStringType)t2).annot);
			return out;
		}
		
		RefinedObjectType ot2 = (RefinedObjectType)t2;
		// System.out.println("[RefinedType.join] cls1 = " + cls + " cls2 = " + ot2.cls);
		RefType c = cls;
		if (!cls.equals(ot2.cls)) {
			SootClass cThis = cls.getSootClass();
			SootClass cOther = ot2.cls.getSootClass();

			if (isTypeSubTypeOf(cThis, cOther))
				c = ot2.cls;
			else if (isTypeSubTypeOf(cOther, cThis))
				c = cls;			
			else {
				SootClass sc = getLeastCommonSuperclassOf(cThis, cOther);
				c = sc.getType();
				// System.out.println("LeastCommonSuperclassOf " + cThis + " and " + cOther + " is " + c);
			}
		}
		RefinedObjectType out = new RefinedObjectType(c);
		out.regions.addAll(regions);
		out.regions.addAll(ot2.regions);
		return out;
	}
	
//	@Override
//	public RefinedType meet(RefinedType t2) {
//		RefinedObjectType ot2 = (RefinedObjectType)t2;
//		// System.out.println("[RefinedType.meet] cls1 = " + cls + " cls2 = " + ot2.cls);
//		assert(cls.equals(ot2.cls));
//		Set<Region> newRegions = new TreeSet<Region>(regions);
//		newRegions.retainAll(ot2.regions);
//		RefinedObjectType out = new RefinedObjectType(cls, newRegions);		
//		return out;
//	}

	public static Boolean isTypeSubTypeOf(SootClass c1, SootClass c2) {
		Hierarchy h = Scene.v().getActiveHierarchy();
		// Main.mainLog.finest("Is " + c1 + " a subtype of " + c2 + "?");
		return (c1.isInterface() && c2.isInterface() && h.isInterfaceSubinterfaceOf(c1, c2))
			|| (c1.isConcrete() && (h.isClassSubclassOf(c1, c2) || (c2.isInterface() && h.getImplementersOf(c2).contains(c1))));
	}
	
	@Override
	public Boolean subType(RefinedType t2) {
		if (t2 instanceof RefinedObjectType) {
			RefinedObjectType ot2 = (RefinedObjectType)t2;
			if (!cls.equals(ot2.cls)) {
				SootClass cThis = cls.getSootClass();
				SootClass cOther = ot2.cls.getSootClass();
				if (!isTypeSubTypeOf(cThis, cOther))
					return false;
			}
			return ot2.regions.containsAll(regions);
		}
		else
			return false;
	}

	@Override
	public int compareTo(RefinedType other) {
		if (other instanceof RefinedNonRefType || other instanceof RefinedStringType)
			return -1;
		else if (other instanceof RefinedArrayType)
			return 1;
		
		RefinedObjectType objType = (RefinedObjectType)other;
		if (this == objType)
			return 0;
		
		if (cls != objType.cls)
			return cls.toString().compareTo(objType.cls.toString());

		if (regions.equals(objType.regions))
			return 0;
		
		int s1 = regions.size();
		int s2 = objType.regions.size();
		if (s1 != s2)
			return s1 - s2;	
		
		Iterator<Region> it1 = regions.iterator();
		Iterator<Region> it2 = objType.regions.iterator();
		while(it1.hasNext()) {			
			Region r1 = it1.next();
			Region r2 = it2.next();
			if (!r1.equals(r2))
				return r1.compareTo(r2);
		}

		return 0;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;		
		if (obj == null)
			return false;		
		if (getClass() != obj.getClass())
			return false;
		
		RefinedObjectType other = (RefinedObjectType) obj;
		return this.compareTo(other) == 0;
	}
}
