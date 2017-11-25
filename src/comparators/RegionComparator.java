package comparators;

import java.util.Comparator;

import monoids.Monoid;
import sootTSA.Region;

public class RegionComparator implements Comparator<Region> {
	public int compare(Region r1, Region r2) {
		return r1.compareTo(r2);
//		if (r1 instanceof PosRegion)
//			return ((PosRegion)r1).compareTo(r2);
//		else if (r1 instanceof Monoid)
//			return ((Monoid)r1).compareTo(r2);
//		else 
//			throw new RuntimeException("unknown region type");
	}				
}
