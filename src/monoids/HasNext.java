package monoids;

import java.util.Set;

import sootTSA.Region;

public class HasNext implements Monoid {
	
	// the automaton is as follows
	// the events are: 
	//   iterator(c,i)
	//   update(c)
	//   use(i)
	// ->s 
	// 

	@Override
	public int compareTo(Region r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Monoid neutralElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Monoid op(Monoid x, Monoid y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Monoid parseElement(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean allowed(Set<Monoid> set) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Monoid parseLiteral(String s) {
		// TODO Auto-generated method stub
		return null;
	}

}
