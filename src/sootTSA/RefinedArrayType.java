package sootTSA;

import java.util.Set;
import java.util.TreeSet;

import monoids.Monoid;
import soot.RefType;
import soot.Scene;
import soot.Type;

public class RefinedArrayType extends RefinedType {
	private RefinedType refType;
	
	public RefinedType getRefinedType() {
		return refType; 
	}
	
//	private RefinedArrayType(RefinedObjectType t) {
//		refType = t;
//	}
	
	public RefinedArrayType(RefType c) {
		if (RefinedType.isStringType(c))
			refType = new RefinedStringType();
		else 
			refType = new RefinedObjectType(c);
	}
	
	public RefinedArrayType(RefType c, Set<Region> regs) {
		if (RefinedType.isStringType(c)) {
			Set<Monoid> elems = new TreeSet<Monoid>();	
			for (Region r: regs) elems.add((Monoid)r);
			refType = new RefinedStringType(elems);
		}
		else
			refType = new RefinedObjectType(c, regs);
	}
	
	public RefinedArrayType(RefinedType t) {
		if (t instanceof RefinedNonRefType)
			refType = new RefinedNonRefType(t.getType());
		else if (t instanceof RefinedObjectType)
			refType = new RefinedObjectType(t);
		else if (t instanceof RefinedStringType)
			refType = new RefinedStringType(t);
		else 
			throw new RuntimeException("two dimensional arrays not supported");
		
		// OLD
//		if (t instanceof RefinedArrayType)
//			refType = ((RefinedArrayType)t).refType;
//		else
//			refType = new RefinedObjectType(t);
	}

//	public RefinedArrayType(RefType c, Region r) {
//		refType = new RefinedObjectType(c, r);
//	}
//	
//	public RefinedArrayType(RefType c, Set<Region> regs) {
//		refType = new RefinedObjectType(c, regs);
//	}
	
//	public RefinedArrayType(Boolean b) {
//		refType = new RefinedObjectType(b);
//	}
	
	
	@Override
	public RefinedType join(RefinedType other) {
		RefinedType joinType;
		if (other instanceof RefinedArrayType)
			joinType = refType.join(((RefinedArrayType)other).refType);
		else
			// joinType = (RefinedObjectType) refType.join(other);
			throw new RuntimeException("unhandled case: " + other);
		return new RefinedArrayType(joinType);
	}

//	@Override
//	public RefinedType meet(RefinedType other) {
//		RefinedObjectType meetType;
//		if (other instanceof RefinedArrayType)
//			meetType = (RefinedObjectType) refType.meet(((RefinedArrayType)other).refType);
//		else
//			meetType = (RefinedObjectType) refType.meet(other); 
//		return new RefinedArrayType(meetType);
//	}

	@Override
	public Boolean subType(RefinedType other) {
		if (other instanceof RefinedArrayType)
			return refType.subType(((RefinedArrayType)other).refType);
		else
			return refType.subType(other); 
	}

	@Override
	public int compareTo(RefinedType other) {
		if (!(other instanceof RefinedArrayType))
			return 1;
		
		return refType.compareTo(((RefinedArrayType)other).refType);
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;		
		if (obj == null)
			return false;		
		if (getClass() != obj.getClass())
			return false;
		
		return refType.equals(((RefinedArrayType)obj).refType);
	}

	@Override
	public Type getType() {
		Type t = refType.getType();
		return Scene.v().getType(t + "[]");
	}

	public String toString() {
		return "ArrayType[" + refType.toString() + "]";
	}
}
