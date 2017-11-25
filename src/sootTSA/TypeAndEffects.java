package sootTSA;

import soot.Type;

public class TypeAndEffects {
	RefinedType type;
	Effects effects;
	
	TypeAndEffects(RefinedType t, Effects e) {
		type = t;
		effects = e;
	}
	
	public RefinedType getType() {
		return type;
	}
	
	public Effects getEffects() {
		return effects;
	}
	
	public static TypeAndEffects initTypeAndEffects(Type typ, Boolean library) {
		Region newRegion = null;
		if (library) 
			newRegion = TSA.unknownRegion;
		RefinedType refType = RefinedType.initRefinedType(typ, newRegion);
		Effects effects = new Effects(false);
		return new TypeAndEffects(refType, effects);
    }
	
	@Override
	public String toString() {		
		return "(" + type + ", " + effects + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;		
		if (obj == null)
			return false;		
		if (getClass() != obj.getClass())
			return false;
		
		TypeAndEffects other = (TypeAndEffects) obj;
		return type.equals(other.type) && effects.equals(other.effects);
	}

	public TypeAndEffects join(TypeAndEffects other) {
		RefinedType newT = type.join(other.type);
		Effects newE = effects.union(other.effects);
		return new TypeAndEffects(newT, newE);
	}
}
