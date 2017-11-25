package sootTSA;

import java.util.Map;
import java.util.TreeMap;

import comparators.ToStringComparator;
import soot.Local;


public class IntraProcState {
	private Map<Local, RefinedType> refTypes;
	Effects effects;
	
	public IntraProcState() {
		refTypes = new TreeMap<Local, RefinedType>(new ToStringComparator<Local>());
		effects = new Effects(false); // the set containing the neutral element
	}
	
	public Map<Local, RefinedType> getRefTypes() {
		return refTypes;
	}

	public void setRefTypes(Map<Local, RefinedType> depMap) {
		this.refTypes = depMap;
	}

	public Effects getEffects() {
		return effects;
	}

	public void setEffects(Effects e) {
		effects = e;
	}

	public RefinedType getRefType(Local v) {
		return refTypes.get(v);
	}
	
	public void augmentRefType(Local v, RefinedType refType) {
		if (refTypes.containsKey(v)) {
			RefinedType oldType = refTypes.get(v);
			refTypes.put(v, oldType.join(refType));
		} else
			refTypes.put(v, refType);
	}
	
	public void overrideRefType(Local v, RefinedType refType) {
		refTypes.put(v, refType);
	}

	public String toString() {
	    String str = "Abstract State = {" + effects + "; ";
	    for(Local v: refTypes.keySet()) {
	    	RefinedType refType = refTypes.get(v);
	    	if (!(refType instanceof RefinedNonRefType))
	    		str += v + " -> " + refType + "; ";

	    		/* OLD:
	    		RefinedRefType type = (RefinedRefType)refType;
	    		Set<Region> regs = type.getRegions();
	    		if (RefinedType.isStringType(type.getType())) {
	    			if (regs.size() == 1) {
	    				if (regs.iterator().next() != Main.goodRegion)
	    					str += v + " --> " + refType + "; ";
	    			} else {
//	    				if (regs.size() == 0)
//	    					throw new RuntimeException("String with no region: " + v);
	    				str += v + " --> " + refType + "; ";
	    			}		
	    		} else // if (regs.size() != 1 || (regs.size() != 0 && regs.iterator().next() != Main.nilRegion))
	    			str += v + " --> " + refType + "; ";
	    	}
//	    	else {
//	    		str += v + " --> " + refType + "; ";
//	    	}
 */
	    }
		str += "}";
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		IntraProcState other = (IntraProcState) obj;

		if (refTypes == null) {
			if (other.refTypes != null)
				return false;
		} else { 
			if (!refTypes.equals(other.refTypes))
				return false;
		}

		if (effects == null) {
			if (other.effects != null)
				return false;
		} else {
			if (!effects.equals(other.effects))
				return false;
		}

		// at this point locals and effects are equal
		return true;
	}
}
