package monoids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sootTSA.PosRegion;
import sootTSA.Region;
import util.Pair;

/*
 * This enumeration represents a monoid defined to enforce
 * guideline for preventing XSS attacks. 
 */

public enum XssMonoid implements Monoid {
	Lit, C1, C2, Script, EndScript, C1Script, Input, C2EndScript;
	
	private static Set<XssMonoid> allowed = new HashSet<XssMonoid>();
	static {
		allowed.add(Lit);
		allowed.add(C1);
		allowed.add(Script); 
		allowed.add(EndScript);
		allowed.add(C1Script);
	}
	
	@Override
	public Boolean allowed(Set<Monoid> set) {
		for (Monoid e: set) {
			if (e instanceof XssMonoid) {
				if (!allowed.contains(e))
					return false;
			}
			else
				throw new RuntimeException("internal error");
		}
		return true;
	}


	private static Map<Pair<Monoid, Monoid>, Monoid> table = new HashMap<Pair<Monoid, Monoid>, Monoid>();

	static {
		for (XssMonoid m : XssMonoid.values())
			table.put(new Pair<Monoid, Monoid>(Lit, m), m);

		for (XssMonoid m : XssMonoid.values())
			table.put(new Pair<Monoid, Monoid>(m, Lit), m);

		table.put(new Pair<Monoid, Monoid>(C1, C1), C1);
		table.put(new Pair<Monoid, Monoid>(C1, Script), C1Script);
		table.put(new Pair<Monoid, Monoid>(C1, C1Script), C1Script);
		table.put(new Pair<Monoid, Monoid>(C1, EndScript), C1);
		table.put(new Pair<Monoid, Monoid>(C1, C2), Input);
		table.put(new Pair<Monoid, Monoid>(C1, C2EndScript), Input);
		table.put(new Pair<Monoid, Monoid>(C1, Input), Input);

		table.put(new Pair<Monoid, Monoid>(C1Script, C1), Input);
		table.put(new Pair<Monoid, Monoid>(C1Script, Script), C1Script);
		table.put(new Pair<Monoid, Monoid>(C1Script, C1Script),	Input);
		table.put(new Pair<Monoid, Monoid>(C1Script, EndScript), C1);
		table.put(new Pair<Monoid, Monoid>(C1Script, C2), C1Script);
		table.put(new Pair<Monoid, Monoid>(C1Script, C2EndScript), C1);
		table.put(new Pair<Monoid, Monoid>(C1Script, Input), Input);

		table.put(new Pair<Monoid, Monoid>(Script, C1), Input);
		table.put(new Pair<Monoid, Monoid>(Script, Script),	Script);
		table.put(new Pair<Monoid, Monoid>(Script, C1Script), Input);
		table.put(new Pair<Monoid, Monoid>(Script, EndScript), EndScript);
		table.put(new Pair<Monoid, Monoid>(Script, C2), Script);
		table.put(new Pair<Monoid, Monoid>(Script, C2EndScript), EndScript);
		table.put(new Pair<Monoid, Monoid>(Script, Input), Input);

		table.put(new Pair<Monoid, Monoid>(EndScript, C1), EndScript);
		table.put(new Pair<Monoid, Monoid>(EndScript, Script), Script);
		table.put(new Pair<Monoid, Monoid>(EndScript, C1Script), Script);
		table.put(new Pair<Monoid, Monoid>(EndScript, EndScript), EndScript);
		table.put(new Pair<Monoid, Monoid>(EndScript, C2), Input);	
		table.put(new Pair<Monoid, Monoid>(EndScript, C2EndScript), Input);
		table.put(new Pair<Monoid, Monoid>(EndScript, Input), Input);

		table.put(new Pair<Monoid, Monoid>(C2, C1), Input);
		table.put(new Pair<Monoid, Monoid>(C2, Script), C2);
		table.put(new Pair<Monoid, Monoid>(C2, C1Script), Input);
		table.put(new Pair<Monoid, Monoid>(C2, EndScript), C2EndScript);
		table.put(new Pair<Monoid, Monoid>(C2, C2), C2);
		table.put(new Pair<Monoid, Monoid>(C2, C2EndScript), C2EndScript);
		table.put(new Pair<Monoid, Monoid>(C2, Input), Input);

		table.put(new Pair<Monoid, Monoid>(C2EndScript, C1), C2EndScript);
		table.put(new Pair<Monoid, Monoid>(C2EndScript, Script), C2);
		table.put(new Pair<Monoid, Monoid>(C2EndScript, C1Script), C2);
		table.put(new Pair<Monoid, Monoid>(C2EndScript, EndScript), C2EndScript);
		table.put(new Pair<Monoid, Monoid>(C2EndScript, C2), Input);
		table.put(new Pair<Monoid, Monoid>(C2EndScript,	C2EndScript), Input);
		table.put(new Pair<Monoid, Monoid>(C2EndScript, Input),	Input);

		table.put(new Pair<Monoid, Monoid>(Input, C1), Input);
		table.put(new Pair<Monoid, Monoid>(Input, C1Script), Input);
		table.put(new Pair<Monoid, Monoid>(Input, Script), Input);
		table.put(new Pair<Monoid, Monoid>(Input, EndScript), Input);
		table.put(new Pair<Monoid, Monoid>(Input, C2), Input);
		table.put(new Pair<Monoid, Monoid>(Input, C2EndScript),	Input);
		table.put(new Pair<Monoid, Monoid>(Input, Input), Input);
	}

	@Override
	public int compareTo(Region r) {
		if (r instanceof XssMonoid)
			return this.compareTo((XssMonoid) r);
		else if (r instanceof PosRegion)
			return 1;
		else
			throw new RuntimeException("unknown region type");
	}

	@Override
	public Monoid neutralElement() {
		return Lit;
	}

	@Override
	public Monoid op(Monoid x, Monoid y) {
		Pair<Monoid, Monoid> p = new Pair<Monoid, Monoid>(x, y);
		return table.get(p);
	}

	@Override
	public Monoid parseElement(String s) {
		if (s.equals("Lit"))
			return Lit;
		else if (s.equals("C1"))
			return C1;
		else if (s.equals("C2"))
			return C2;
		else if (s.equals("Script"))
			return Script;
		else if (s.equals("EndScript"))
			return EndScript;
		else if (s.equals("C1Script"))
			return C1Script;
		else if (s.equals("Input"))
			return Input;
		else if (s.equals("C2EndScript"))
			return C2EndScript;
		else
			return null;
	}

	public String toString() {
		if (this == Lit)
			return "Lit";
		else if (this == C1)
			return "C1";
		else if (this == C2)
			return "C2";
		else if (this == Script)
			return "Script";
		else if (this == EndScript)
			return "EndScript";
		else if (this == C1Script)
			return "C1Script";
		else if (this == Input)
			return "Input";
		else if (this == C2EndScript)
			return "C2EndScript";
		else
			throw new RuntimeException("internal error");
	}

	@Override
	public Monoid parseLiteral(String s) {
		throw new RuntimeException("not implemented yet");
	}
}
