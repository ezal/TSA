package sootTSA;

import java.util.Set;

import soot.ArrayType;
import soot.RefType;
import soot.Scene;
import soot.Type;

public abstract class RefinedType implements Comparable<RefinedType> {
	public abstract RefinedType join(RefinedType other);
	public abstract Boolean subType(RefinedType other); // non-strict
	
	// NOTE: We currently have 4 subclasses with the following ordering on their objects: 
	// RefinedNonRefType < RefinedStringType < RefinedObjectType < RefinedArrayType

	public abstract Type getType();	
	
	public static Boolean isStringType(Type t) {
		String str = t.toString();
		return str.equals("java.lang.String");
    }
	
	public static RefType getStringType() {
		return (RefType) Scene.v().getType("java.lang.String");
	}
	
	public static RefinedType initRefinedType(Type t, Region initReg) {
		if (t instanceof RefType) 
			if (isStringType(t))
				return new RefinedStringType();
			else
				return new RefinedObjectType((RefType)t, initReg);
		else if (t instanceof ArrayType) {
			Type et = ((ArrayType)t).getElementType();
			if (et instanceof RefType)
				if (isStringType(et))
					return new RefinedArrayType(new RefinedStringType());
				else
					return new RefinedArrayType(new RefinedObjectType((RefType)et, initReg));
			else
				return new RefinedNonRefType(t);
		}
		else
			return new RefinedNonRefType(t);
	}
	
	public static RefinedType join(Set<RefinedType> types) {
		if (types.isEmpty())
			throw new RuntimeException("internal error: cannot compute the join over the empty set");

		int i = 0;
		RefinedType res = null;
		for (RefinedType typ: types) {
			i++;
			if (i == 1) {
				if (typ instanceof RefinedObjectType)
					res = new RefinedObjectType(typ);
				else if (typ instanceof RefinedStringType)
					res = new RefinedStringType(typ);
				else if (typ instanceof RefinedNonRefType)
					res = typ;
				else if (typ instanceof RefinedArrayType)
					res = new RefinedArrayType(((RefinedArrayType) typ).getRefinedType());
				else
					throw new RuntimeException("internal error: [join] unknown refined type");
			}
			else
				res = res.join(typ);
		}
		return res;
	}
}
