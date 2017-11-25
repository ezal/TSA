package comparators;

import java.util.Comparator;

import soot.SootMethodRef;

public class ToStringComparator<T> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return o1.toString().compareTo(o2.toString());
	}

}
