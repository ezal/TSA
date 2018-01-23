package simple_ones;

import java.util.Collection;
import java.util.LinkedList;
import ourlib.nonapp.TaintAPI;

public class StaticField2 {
	static Collection<String> ll = new LinkedList<String>();
	
	void m() {
		ll.add(TaintAPI.getTaintedString());
		String s = (String) ll.iterator().next();
		TaintAPI.outputString(s);
	}
}
