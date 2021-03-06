package sootTSA;

import java.util.Set;
import java.util.TreeSet;

import monoids.Monoid;
import soot.RefType;
import soot.Scene;
import soot.Type;

public class RefinedArrayType extends RefinedType {
	private RefinedType refType;
	private RefinedType refElemType;
	
	public RefinedType getRefinedType() {
		return refType; 
	}
	
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
	}		
	
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
