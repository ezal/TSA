package simple_ones;

import ourlib.nonapp.TaintAPI;

public class ExcpExample {
	void m() {
		String s = TaintAPI.getTaintedString();
		try {
			System.out.println("here ok");
		} catch (Exception e) {
			TaintAPI.outputString(s);
		}
	}
}
