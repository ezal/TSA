package comparators;

import java.util.Comparator;
import java.util.List;

import sootTSA.RefinedType;

public class ListComparator implements Comparator<List<RefinedType>> {
	public int compare(List<RefinedType> l1, List<RefinedType> l2) {
		int s1 = l1.size();
		int s2 = l2.size();
		if (s1 != s2)
			return s1 - s2;

		for (int i = 0; i < s1; i++) {
			RefinedType t1 = l1.get(i);
			RefinedType t2 = l2.get(i);
			if (!t1.equals(t2))
				return t1.compareTo(t2);	
		}

		return 0;
	}
}

