package simple_ones;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HasNext {
	void use(Integer x) {}
	
	void f() {
		List<Integer> l = new LinkedList<Integer>();
		l.add(1);
		l.add(2);
		Iterator<Integer> i = l.iterator();
		List<Integer> l2 = l;
		l2.add(3);
		Integer n = i.next();
		use(n);
	}
}
