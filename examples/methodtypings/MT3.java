package methodtypings;

import ourlib.nonapp.TaintAPI;

interface I3 {
	String s();
}

interface J3 {
	String s();
}

interface K3 extends I3, J3 { }


class C3 implements I3 {
	public String s() {
		return "okC3";
	}
}

class D3 implements I3 {
	public String s() {
		return TaintAPI.getTaintedString();
	}
}

class E3 implements K3 {
	public String s() {
		return "okC3";
	}
}

class F3 implements K3 {
	public String s() {
		return TaintAPI.getTaintedString();
	}
}


public class MT3 {

}
