package monoids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sootTSA.PosRegion;
import sootTSA.Region;
import util.Pair;

public enum ReqMonoid implements Monoid {
	Id, Req, Use;
	
	public Monoid neutralElement() { return Id; }
	
	private static Set<ReqMonoid> allowed = new HashSet<ReqMonoid>();
	static {
		allowed.add(Id);
		allowed.add(Req);
	}
	// allowed.add(Id, Req);
	
	private static Map<Pair<Monoid, Monoid>, Monoid> table = new HashMap<Pair<Monoid, Monoid>, Monoid>();	
	static {
		table.put(new Pair<Monoid, Monoid>(Req, Req), Req);
		table.put(new Pair<Monoid, Monoid>(Req, Use), Req);
		table.put(new Pair<Monoid, Monoid>(Use, Use), Use);
		table.put(new Pair<Monoid, Monoid>(Use, Req), Use);
		
		for (ReqMonoid m : ReqMonoid.values())
			table.put(new Pair<Monoid, Monoid>(Id, m), m);

		for (ReqMonoid m : ReqMonoid.values())
			table.put(new Pair<Monoid, Monoid>(m, Id), m);
	}
	
	@Override
	public Monoid op(Monoid x, Monoid y) {
		Pair<Monoid, Monoid> p = new Pair<Monoid, Monoid>(x, y);
		return table.get(p);
	}

	@Override
	public Monoid parseElement(String s) {
		if (s.equals("Req"))
			return Req;
		else if (s.equals("Use"))
			return Use;
		else if (s.equals("Id"))
			return Id;
		else
			throw new RuntimeException("internal error: unknown monoid element " + s);
	}
	
	@Override
	public Boolean allowed(Set<Monoid> set) {
		for (Monoid e: set) {
			if (e instanceof ReqMonoid) {
				if (!allowed.contains(e))
					return false;
			}
			else
				throw new RuntimeException("internal error");
		}
		return true;
	}
	
	public String toString() {
		if (this == Req)
			return "Req";
		else if (this == Use)
			return "Use";
		else if (this == Id)
			return "Id";
		else
			throw new RuntimeException("internal error");
	}

	public int compareTo(Region r) {
		if (r instanceof ReqMonoid)
			return this.compareTo((ReqMonoid)r);
		else if (r instanceof PosRegion)
			return 1;
		else 
			throw new RuntimeException("unknown region type");
	}
	
	public Monoid parseLiteral(String s) {
		return Req;
	}

}
