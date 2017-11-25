package simple_ones;

import mockup.misc.LinkedList;
import ourlib.nonapp.TaintAPI;

public class Hoo {
	public static void main(String[] args) {
		LinkedList<String> l = new LinkedList<String>("abc");
		String x = TaintAPI.getTaintedString();
		l.add(x);
		String y = l.get(0);
		TaintAPI.outputString(y);
	}
}
