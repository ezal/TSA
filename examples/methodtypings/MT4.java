package methodtypings;

import ourlib.nonapp.TaintAPI;

interface B4 {
	String s();
}

class C4 implements B4 {
	public String s() {
		return "okC";
	}
}

class D4 implements B4 {
	public String s() {
		return TaintAPI.getTaintedString();
	}
}

public class MT4 {
	void m_ok() {
		B4 c = new C4();
		TaintAPI.outputString(c.s()); // OK
	}
	
	void m_bad() {
		B4 d = new D4();
		TaintAPI.outputString(d.s()); // BAD
	}
	
	void m_bad2(B4 d) {		
		TaintAPI.outputString(d.s()); // BAD
	}
}
