package simple_ones;

import ourlib.nonapp.TaintAPI;

public class Ebad {
	public static void main(String[] args){
		F f1 = new F(TaintAPI.getTaintedString());
		F f2 = new F("GOOD");
		TaintAPI.outputString(f1.s);
		f1.s = "good again";
		System.out.println("Ebad.main executed successfully");
	}
}
