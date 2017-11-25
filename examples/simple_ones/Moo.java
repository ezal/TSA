package simple_ones;

import ourlib.nonapp.TaintAPI;

class MooA {
	String x;
	String y;
}

class MooB extends MooA {
	String x;
}

class MooC extends MooA {
}

public class Moo {
	public static void f() {
		MooB b = new MooB();
		b.y = TaintAPI.getTaintedString();
		MooA a = b;		
		TaintAPI.outputString(a.y); // BAD
	}
	
	public static void f2() {
		MooB b = new MooB();
		b.x = TaintAPI.getTaintedString();
		MooA a = b;		
		TaintAPI.outputString(a.x); // OK
	}
	
	public static void g() {
		int n = 0;
		MooA a;		
		if (n == 0) {
			a = new MooB();
			a.y = TaintAPI.getTaintedString();
		}
		else 
			a = new MooC();
		TaintAPI.outputString(a.y); // BAD
	}
	
	public static void h() {
		int n = 0;
		MooA a;		
		if (n == 1) {
			a = new MooB();
			a.x = TaintAPI.getTaintedString();
		}
		else 
			a = new MooC();
		TaintAPI.outputString(a.x); // OK
	}
	
	public static void main(String[] args) {
		System.out.println("f:");
		f();
		
		System.out.println("f2:");
		f2();
		
		System.out.println("g:");
		g();
		
		System.out.println("h:");
		h();
	}
}
