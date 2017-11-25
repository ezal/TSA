package simple_ones;

import ourlib.nonapp.TaintAPI;

// @Taints("<testers.A: java.lang.String getParameter()>")
// @Brakes("<testers.A: void compile(java.lang.String)>")
public class Egood {
	public static void main(String[] args) {
		F f1 = new F(TaintAPI.getTaintedString());
		F f2 = new F("GOOD");
		TaintAPI.outputString(f2.s);
		f1.s = "good again";
		System.out.println("Egood.main executed successfully");
	}
}
