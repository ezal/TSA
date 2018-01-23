package comparators;

import java.util.Comparator;

import sootTSA.Context;

public class ContextComparator implements Comparator<Context> {
	public int compare(Context c1, Context c2) {
		return c1.compareTo(c2);
	}
}
