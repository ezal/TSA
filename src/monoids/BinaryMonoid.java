package monoids;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sootTSA.PosRegion;
import sootTSA.Region;
import util.Pair;

public enum BinaryMonoid implements Monoid {
	T, U;
	
	private static Map<Pair<Monoid, Monoid>, Monoid> table = new HashMap<Pair<Monoid, Monoid>, Monoid>();	
	static {
		table.put(new Pair<Monoid, Monoid>(U, U), U);
		table.put(new Pair<Monoid, Monoid>(U, T), T);
		table.put(new Pair<Monoid, Monoid>(T, U), T);
		table.put(new Pair<Monoid, Monoid>(T, T), T);
	}

	@Override
	public Monoid neutralElement() {
		return U;
	}

	@Override
	public Monoid op(Monoid x, Monoid y) {
		Pair<Monoid, Monoid> p = new Pair<Monoid, Monoid>(x, y);
		return table.get(p);
	}

	@Override
	public Monoid parseElement(String s) {
		if (s.equals("T"))
			return T;
		else if (s.equals("U"))
			return U;
		else
			return null;
	}
	
	@Override
	public Boolean allowed(Set<Monoid> set) {
		for (Monoid e: set) {
			if (e instanceof BinaryMonoid) {
				if (e == T)
					return false;
			}
			else
				throw new RuntimeException("internal error");
		}
		return true;
	}
	
	public String toString() {
		if (this == U)
			return "U";
		else if (this == T)
			return "T";
		else
			throw new RuntimeException("internal error");
	}

	public int compareTo(Region r) {
		if (r instanceof BinaryMonoid)
			return this.compareTo((BinaryMonoid)r);
		else if (r instanceof PosRegion)
			return 1;
		else 
			throw new RuntimeException("unknown region type");
	}
	
	public Monoid parseLiteral(String s) {
		// all literals are untainted
		return U;
	}
}
