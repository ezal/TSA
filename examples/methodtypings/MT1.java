package methodtypings;

import ourlib.nonapp.TaintAPI;

class B1 {
	String s1() {
		return TaintAPI.getTaintedString();
	}
	
	String s2() {
		return "okB";
	}
}

class C1 extends B1 {}

class D1 extends C1 {
	String s1() {
		return "okD";
	}
	
	String s2() {
		return TaintAPI.getTaintedString();
	}
}



public class MT1 {
	void mb1() {
		C1 c = new C1();
		TaintAPI.outputString(c.s1()); // BAD
	}
	
	void mb2() {
		C1 c = new C1();
		TaintAPI.outputString(c.s2()); // OK
	}
	
	void mc1() {
		C1 d = new D1();
		TaintAPI.outputString(d.s1()); // OK
	}
	
	void mc2() {
		C1 d = new D1();
		TaintAPI.outputString(d.s2()); // BAD
	}
	
	public static void main(String[] args) {
		MT1 x = new MT1();
		x.mb1();
	}
}



