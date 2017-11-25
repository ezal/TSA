package sootTSA;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import comparators.RegionComparator;
import comparators.ToStringComparator;
import soot.RefType;
import soot.SootField;

public class FieldTable {
	Map<RefType, Map<Region, Map<SootField, RefinedType>>> table = 
			new TreeMap<RefType, Map<Region, Map<SootField, RefinedType>>>();

	public FieldTable() {
	}
	
	public FieldTable(FieldTable fTable) {
		for (Entry<RefType, Map<Region,  Map<SootField, 
				RefinedType>>> cEntry: fTable.table.entrySet()) 
		{
			RefType c = cEntry.getKey();	
			for (Entry<Region, Map<SootField, RefinedType>> rEntry: cEntry.getValue().entrySet()) 
			{
				Region r = rEntry.getKey();				
				for (Entry<SootField, RefinedType> fEntry: rEntry.getValue().entrySet()) 
				{
					SootField f = fEntry.getKey();
					RefinedType fType = fEntry.getValue();
					put(c, r, f, fType);
				}
			}
		}
	}
	
	public Map<RefType, Map<Region, Map<SootField, RefinedType>>> getTable() {
		return table;
	}

	public RefinedType get(RefType c, Region r, SootField f) {
		Map<Region, Map<SootField, RefinedType>> t1 = table.get(c);
		if (t1 != null) {
			Map<SootField, RefinedType> t2 = t1.get(r);
			if (t2 != null) {
				return t2.get(f);
			}
		}
		
		return null;
	}
	
	public void put(RefType c, Region r, SootField f, RefinedType typ) {
		Map<Region, Map<SootField, RefinedType>> tab1 = table.get(c);
		if (tab1 != null) {
			Map<SootField, RefinedType> tab2 = tab1.get(r);
			if (tab2 != null) {
				tab2.put(f, typ);
			} else {
				tab2 = new TreeMap<SootField, RefinedType>(new ToStringComparator<SootField>());
				tab2.put(f, typ);
				tab1.put(r, tab2);
			}
		} else {
			tab1 = new TreeMap<Region, Map<SootField, RefinedType>>(new RegionComparator());
			TreeMap<SootField, RefinedType> tab2 = 
					new TreeMap<SootField, RefinedType>(new ToStringComparator<SootField>());
			tab2.put(f, typ);
			tab1.put(r, tab2);
			table.put(c, tab1);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FieldTable))
			return false;
		FieldTable other = (FieldTable) obj;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	public String toString() {
		String s = "";
		for (Entry<RefType, Map<Region,  Map<SootField, RefinedType>>> cEntry: table.entrySet()) 
		{
			RefType c = cEntry.getKey();			
			for (Entry<Region, Map<SootField, RefinedType>> rEntry: cEntry.getValue().entrySet()) 
			{
				Region r = rEntry.getKey();				
				for (Entry<SootField, RefinedType> fEntry: rEntry.getValue().entrySet()) 
				{
					SootField f = fEntry.getKey();
					RefinedType fType = fEntry.getValue();
					s += "  " + c + ", " + r + ", " + f + ", " + fType + "\n";
				}
			}
		}
		return s;
	}
}
