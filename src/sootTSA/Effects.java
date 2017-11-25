package sootTSA;

import java.util.Set;
import java.util.HashSet;

import monoids.Monoid;


public class Effects {  
	private Set<Monoid> set;    // a set of elements of the monoid 'mon'

	public Set<Monoid> getSet() {
		return set;
	}
	
	// TODO: check that we need both cases (ie empty can be true or false)
	// TODO: this parameter leads to unreadable code (eg what is the new MonoidElemSet(false) doing?). Fix this
	public Effects(Boolean empty) {
		set = new HashSet<Monoid>();
		if (!empty)
			set.add(TSA.mon.neutralElement());
	}

	public Effects(Set<Monoid> other) {
		set = new HashSet<Monoid>(other);
	}
	
	public Effects union(Effects other) {
		Set<Monoid> retSet = new HashSet<Monoid>(this.set); 
		retSet.addAll(other.set);
		return new Effects(retSet);
	}
	
	public Effects intersect(Effects other) {
		Set<Monoid> retSet = new HashSet<Monoid>(); 
		for (Monoid e: set)
			if (other.set.contains(e))
				retSet.add(e);
		return new Effects(retSet);
	}
	
	public Boolean subset(Effects other) {
		return other.set.containsAll(set);
	}
	
	public Boolean subset(Set<Region> other) {
		return other.containsAll(set);
	}
	
	public Effects concat(Effects other) {
		Set<Monoid> newSet = new HashSet<Monoid>();
		for (Monoid e: set)
			for (Monoid f: other.set) 		
				newSet.add(e.op(e, f));
		return new Effects(newSet);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;		
		if (obj == null)
			return false;		
		if (getClass() != obj.getClass())
			return false;
		
		Effects other = (Effects) obj;
		return set.equals(other.set); 
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}
	
	@Override
	public String toString() {		
		return set.toString();
	}
}
