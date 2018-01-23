package comparators;

import java.util.Comparator;

import monoids.Monoid;
import sootTSA.Region;

public class RegionComparator implements Comparator<Region> {
	public int compare(Region r1, Region r2) {
		return r1.compareTo(r2);
	}				
}
