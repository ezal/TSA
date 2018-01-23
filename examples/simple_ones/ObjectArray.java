package simple_ones;

import ourlib.nonapp.TaintAPI;

public class ObjectArray {
	public static void main(String[] args) {
		Object[] a = new Object[1];
		a[0] = TaintAPI.getTaintedString();
		String s = (String) a[0]; 
		TaintAPI.outputString(s);
	}
}
