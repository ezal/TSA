package monoids;

import java.util.Set;

import sootTSA.Region;

public interface Monoid extends Region {
	public Monoid neutralElement();
	public Monoid op(Monoid x, Monoid y);
	public Monoid parseElement(String s);
	public Boolean allowed(Set<Monoid> set);
	public Monoid parseLiteral(String s); // 'lit2word' in the paper
}
