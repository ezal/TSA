package simple_ones;

import ourlib.nonapp.TaintAPI;

class GooA {
	void foo(String s) {
		s.charAt(0);
	}
}

class GooB extends GooA {
}

class GooC extends GooB {
	void foo(String s) {
		TaintAPI.outputString(s);
	}	
}

class GooD extends GooB {
	void foo(String s) {
		s.substring(0);
	}
}

public class Goo {
	public static void main(String[] args) {
		GooA a = new GooB();
		a.foo(TaintAPI.getTaintedString());
		
		GooB b = null;
		if (args.length == 0) {
			b = new GooC();
		} else {
			b = new GooD();
		}
		b.foo(TaintAPI.getTaintedString());
	}
}
