package sootTSA;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import comparators.RegionComparator;
import monoids.Monoid;
import soot.NullType;
import soot.RefType;
import soot.Scene;

// NOTE: Maybe we could simply use the RefinedObjectType class instead?
public class RefinedStringType extends RefinedType {
	Set<Monoid> annot;

	public RefinedStringType() {
		annot = new TreeSet<Monoid>(new RegionComparator());
		annot.add(TSA.mon.neutralElement());
	}
	
	public RefinedStringType(Monoid elem) {
		annot = new TreeSet<Monoid>(new RegionComparator());
		annot.add(elem);
	}
	
	public RefinedStringType(Set<Monoid> elems) {
		annot = new TreeSet<Monoid>(new RegionComparator());
		annot.addAll(elems);
	}
	
	public RefinedStringType(RefinedType t) {
		annot = ((RefinedStringType)t).annot;
	}
	
	public Set<Monoid> getAnnot() {
		return annot;
	}
	
	public String toString() {
		return "StrType(" + annot + ")";
	}

	@Override
	public RefinedType join(RefinedType t2) {
		if (t2 instanceof RefinedStringType) {
			RefinedStringType newt = new RefinedStringType(annot);			 
			newt.annot.addAll(((RefinedStringType)t2).annot);
			return newt;
		} 
		else if (t2 instanceof RefinedNonRefType) {
			if (t2.getType() instanceof NullType)
				return new RefinedStringType(annot);
			else
				throw new RuntimeException("internal error");
		}
		else if (t2 instanceof RefinedObjectType) {
			RefType clsObject = Scene.v().getRefType("java.lang.Object"); 
			RefinedObjectType out = new RefinedObjectType(clsObject);
			Set<Region> regions = ((RefinedObjectType) t2).getRegions();			
			regions.addAll(annot);
			out.setRegions(regions);
			return out;
		}
		throw new RuntimeException("internal error");
	}
	
	@Override
	public Boolean subType(RefinedType other) {
		if (other instanceof RefinedStringType) 
			return (((RefinedStringType)other).annot.containsAll(annot));
		else if (other instanceof RefinedObjectType) {
			if (other.getType().equals("java.lang.Object"))
				return (((RefinedObjectType)other).getRegions().containsAll(annot));
		}
		return false;
	}

	@Override
	public int compareTo(RefinedType other) {
		if (other instanceof RefinedNonRefType)
			return 1;
		else if (other instanceof RefinedObjectType || other instanceof RefinedArrayType)
			return -1;
		
		RefinedStringType t2 = (RefinedStringType) other;
		if (this == t2)
			return 0;
		
		if (annot.equals(t2.annot))
			return 0;
		
		int s1 = annot.size();
		int s2 = t2.annot.size();
		if (s1 != s2)
			return s1 - s2;	
		
		Iterator<Monoid> it1 = annot.iterator();
		Iterator<Monoid> it2 = t2.annot.iterator();
		while(it1.hasNext()) {			
			Monoid r1 = it1.next();
			Monoid r2 = it2.next();
			if (!r1.equals(r2))
				return r1.compareTo(r2);
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;		
		if (obj == null)
			return false;		
		if (getClass() != obj.getClass())
			return false;
		
		RefinedStringType other = (RefinedStringType) obj;
		return annot.equals(other.annot);
	}

	@Override
	public RefType getType() {
		return (RefType) Scene.v().getType("java.lang.String");
	}
}
