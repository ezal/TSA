package simple_ones;

import ourlib.nonapp.TaintAPI;

public class Foo {
	public static String append(String x, String y) {
		String z;
		z = x + y;
		return z; 
	}
	
	public static void main(String[] args) {
		String s = append("a", TaintAPI.getTaintedString());
		s += "b";
		TaintAPI.outputString(s);
	}
}
