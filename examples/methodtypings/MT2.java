package methodtypings;

import ourlib.nonapp.TaintAPI;

class C2 {
	String s() {
		return "okC";	
	}
}

class D2 extends C2 {}

class E2 extends D2 {}

class F2 extends E2 {
	String s() {
		return "okF";
	}
}

class G2 extends D2 {
	String s() {
		return TaintAPI.getTaintedString();
	}
}


public class MT2 {
	void md() {
		D2 d = new D2();
		TaintAPI.outputString(d.s()); // OK
	}
	
	void me() {
		D2 d = new E2();
		TaintAPI.outputString(d.s()); // OK
	}
	
	void mg() {
		D2 d = new G2();
		TaintAPI.outputString(d.s()); // BAD
	}
}
