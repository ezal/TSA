package simple_ones;

import ourlib.nonapp.TaintAPI;

public class StaticField1 {
	static String s = TaintAPI.getTaintedString();
	
	void m() {
		TaintAPI.outputString(s);
	}
}
