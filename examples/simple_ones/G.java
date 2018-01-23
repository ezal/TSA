package simple_ones;

import ourlib.nonapp.TaintAPI;

class A {
	public String s;
	
	void foo (String x) {
		s = x;
	}
}

public class G {
	String f(String x) {
		A a = new A();
		a.foo(x);
		return a.s;
	}

	void m() {
		f(TaintAPI.getTaintedString());
		String ok = f("ok");
		TaintAPI.outputString(ok);
	}
}

