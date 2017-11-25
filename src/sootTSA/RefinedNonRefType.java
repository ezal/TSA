package sootTSA;

import soot.NullType;
// import soot.RefType;
import soot.Type;

public class RefinedNonRefType extends RefinedType {
	Type type;

	public RefinedNonRefType(Type t) {
		type = t;
	}
	
	@Override
	public RefinedType join(RefinedType other) {
		if (this.equals(other))
			return this;
		else if (type instanceof NullType)
			return other;
		else
			throw new RuntimeException("join failure");
	}

//	@Override
//	public RefinedType meet(RefinedType other) {
//		if (this.equals(other))
//			return this;
//		else
//			throw new RuntimeException("meet failure");
//	}

	@Override
	public Boolean subType(RefinedType other) {
		return this.equals(other);
	}

	@Override
	public int compareTo(RefinedType other) {		
		if (!(other instanceof RefinedNonRefType))
			return -1;

		RefinedNonRefType t2 = (RefinedNonRefType)other;
		return type.toString().compareTo(t2.type.toString());
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "NonRefType(" + type + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RefinedNonRefType))
			return false;
		
		RefinedNonRefType other = (RefinedNonRefType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.toString().equals(other.type.toString()))
			return false;
		return true;
	}
	
	
}
