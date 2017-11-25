package sootTSA;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import comparators.ListComparator;
import comparators.RegionComparator;
import comparators.ToStringComparator;

import java.util.Set;

import monoids.Monoid;

import soot.SootMethodRef;



public class MethodTable {
	Map<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> table;
	// static private Map<SootMethodRef, SootMethodRef> libMap;
	// NOTE: Alternatively, we could have something like:
	// Map<SootMethodRef, Set<MethodRefinedType>> table;
	
	public Map<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> getTable() {
		return table;
	}

	public MethodTable() {
		table = new TreeMap<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>(new ToStringComparator<SootMethodRef>());		
	}

	// copy constructor
	public MethodTable(MethodTable mTable) {
		table = new TreeMap<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>(new ToStringComparator<SootMethodRef>());
		for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> mEntry: mTable.table.entrySet()) 
		{
			SootMethodRef m = mEntry.getKey();
			for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> rEntry: mEntry.getValue().entrySet()) 
			{
				Region r = rEntry.getKey();					
				for (Entry<List<RefinedType>, TypeAndEffects> aEntry: rEntry.getValue().entrySet()) {
					List<RefinedType> argsTypes = aEntry.getKey();
					TypeAndEffects retType = aEntry.getValue();
					put(m, r, argsTypes, retType);
				}
			}
		}
	}

	private Map<List<RefinedType>, TypeAndEffects> newArgTypeList() {
		Map<List<RefinedType>, TypeAndEffects> map = new TreeMap<List<RefinedType>, TypeAndEffects>(new ListComparator());
		return map;
	}
	
	public Map<Region, Map<List<RefinedType>, TypeAndEffects>> get(SootMethodRef m) {
		return table.get(m);
	}
	
	public TypeAndEffects get(SootMethodRef m, Region r, List<RefinedType> args) {
		Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = table.get(m);
		if (tab1 != null) {
			Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(r);
			if (tab2 != null) {
				return tab2.get(args);
			}
		}
		return null;
	}

	public Boolean containsKey(SootMethodRef m, Region r, List<RefinedType> args) {
		// System.out.println("[containsKey] get(c); c = " + c);
		Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = table.get(m);
		if (tab1 != null) {
			// System.out.println("[containsKey] get(m); m = " + m);
			Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(r);
			if (tab2 != null) {
				// System.out.println("[containsKey] get(r); r = " + r);
				return tab2.containsKey(args);
			}
		}
		return false;
	}
	
	public void put(SootMethodRef m, Region r, List<RefinedType> argsTypes, TypeAndEffects retType) {
		Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = table.get(m);
		if (tab1 != null) {
			Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(r);
			if (tab2 != null) {
				tab2.put(argsTypes, retType);
			} else {
				tab2 = newArgTypeList();
				tab2.put(argsTypes, retType);
				tab1.put(r, tab2);
			}				
		} else {
			tab1 = new TreeMap<Region, Map<List<RefinedType>, TypeAndEffects>>(new RegionComparator());
			Map<List<RefinedType>, TypeAndEffects> tab2 = newArgTypeList();
			tab2.put(argsTypes, retType);
			tab1.put(r, tab2);
			table.put(m, tab1);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MethodTable))
			return false;

		MethodTable other = (MethodTable) obj;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table)) {
			return false;
		}
		return true;
	}
	
	public boolean equalsDEBUG(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MethodTable))
			return false;

		MethodTable other = (MethodTable) obj;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table)) {
			Set<Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>> eset1 = table.entrySet();
			Set<Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>> eset2 = other.table.entrySet();
			
//			Set<SootMethodRef> keys1 = table.keySet();
//			Set<SootMethodRef> keys2 = other.table.keySet();
//			System.out.println("keys1 equals keys2 " + keys1.equals(keys2));

			Collection<Map<Region, Map<List<RefinedType>, TypeAndEffects>>> vals1 = table.values();
			Collection<Map<Region, Map<List<RefinedType>, TypeAndEffects>>> vals2 = other.table.values();
			
			Set<Map<Region, Map<List<RefinedType>, TypeAndEffects>>> svals1 = new HashSet(vals1);
			Set<Map<Region, Map<List<RefinedType>, TypeAndEffects>>> svals2 = new HashSet(vals2);
			
			
			System.out.println("vals1 equals vals2 " + vals1.equals(vals2));
//			System.out.println("vals1 equals vals2 " + vals1.containsAll(vals2));
//			System.out.println("vals1 equals vals2 " + vals2.containsAll(vals1));
			// System.out.println("list1 equals list2 " + svals1.equals(svals2));
			
			for (Map<Region, Map<List<RefinedType>, TypeAndEffects>> m1: vals1) {
				System.out.println("table contains " + m1);
				// if (!vals2.contains(m1)) {
				Boolean exists = false;
				for (Map<Region, Map<List<RefinedType>, TypeAndEffects>> m2: vals2) {
					boolean b1 = m1.equals(m2);
					boolean b2 = m2.equals(m1);
					if (b1 != b2) {
						System.out.println("AMAZING " + m1 + " vs " + m2);
						
						Set<Region> keys1 = m1.keySet();
						Set<Region> keys2 = m2.keySet();
						System.out.println("keys1 equals keys2 " + keys1.equals(keys2));
			
						Collection<Map<List<RefinedType>, TypeAndEffects>> mv1 = m1.values();
						Collection<Map<List<RefinedType>, TypeAndEffects>> mv2 = m2.values();
			
						System.out.println("mvals1 equals mvals2 " + mv1.equals(mv2));
						System.out.println("mvals1 contains mvals2 " + mv1.containsAll(mv2));
						System.out.println("mvals2 contains mvals1 " + mv2.containsAll(mv1));
						
						
						for (Map<List<RefinedType>, TypeAndEffects> em1: mv1) {							
							Boolean e = false;
							for (Map<List<RefinedType>, TypeAndEffects> em2: mv2) {
								boolean eb1 = em1.equals(em2);
								boolean eb2 = em2.equals(em1);
								if (eb1 != eb2) {
									System.out.println("AMAZING222 " + em1 + " vs " + em2);
									
									Set<List<RefinedType>> k1 = em1.keySet();
									Set<List<RefinedType>> k2 = em2.keySet();
									System.out.println("k1 equals k2 " + k1.equals(k2));
						
									Collection<TypeAndEffects> mmv1 = em1.values();
									Collection<TypeAndEffects> mmv2 = em2.values();
						
									System.out.println("mmvals1 = " + mmv1 + " mmvals2 = " + mmv2);
									System.out.println("mmvals1 equals mmvals2 " + mmv1.equals(mmv2));
									System.out.println("mmvals2 equals mmvals1 " + mmv2.equals(mmv1));
									System.out.println("mmvals1 contains mmvals2 " + mmv1.containsAll(mmv2));
									System.out.println("mmvals2 contains mmvals1 " + mmv2.containsAll(mmv1));
									
									for (Entry<List<RefinedType>, TypeAndEffects> e1: em1.entrySet()) {
										if (!em2.entrySet().contains(e1)) {
											System.out.println("em2 does NOT contain " + e1);
										}
									}
									for (Entry<List<RefinedType>, TypeAndEffects> e2: em2.entrySet()) {
										if (!em1.entrySet().contains(e2)) {
											System.out.println("em1 does NOT contain " + e2);
										}
									}
									
//									
//									for (Map<List<RefinedType>, TypeAndEffects> em1: mv1) {							
//										Boolean e = false;
//										for (Map<List<RefinedType>, TypeAndEffects> em2: mv2) {
//											boolean eb1 = em1.equals(em2);
//											boolean eb2 = em2.equals(em1);
//											if (eb1 != eb2) {
//												System.out.println("AMAZING222 " + em1 + " vs " + em2);								
//											}
//											else
//												if (eb1) {
//													e = true;
//													break;
//												}
//										}
//										if (!e)
//											System.out.println("mv2 does not contain value " + em1);
//									}						
//									break;						
								}
								else
									if (eb1) {
										e = true;
										break;
									}
							}
							if (!e)
								System.out.println("mv2 does not contain value " + em1);
						}						
						break;						
					}
					else
						if (b1) {
							exists = true;
							break;
						}
					//System.out.println("table does not contain " + m1);
				}
				if (!exists)
					System.out.println("other.table does not contain value " + m1);
			}
//			for (Map<Region, Map<List<RefinedType>, TypeAndEffects>> m2: vals2) {
//				System.out.println("other.table contains " + m2);
//				if (!vals1.contains(m2)) {
//					System.out.println("table does not contain " + m2);
//				}
//			}
			
//			for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> e2: eset2) {
//				// if (!eset1.contains(e2))
//				SootMethodRef m2 = e2.getKey();
//				for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> e1: eset1) {
//					SootMethodRef m2 = e2.getKey();
//				}
//			}
			
//			for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> e2: eset2) {
//				if (!eset1.contains(e2)) {
//					System.out.println("new table does not contain old entry " + e2);										
//					System.out.println("  old entry value: " + e2.getValue());
//					SootMethodRef m = e2.getKey();					
//					System.out.println("  new value: " + table.get(m));
//					if (!e2.getValue().equals(table.get(e2.getKey()))) {
//						Set<Entry<Region, Map<List<RefinedType>, TypeAndEffects>>> eeset1 = e2.getValue().entrySet();
//						Set<Entry<Region, Map<List<RefinedType>, TypeAndEffects>>> eeset2 = table.get(e2.getKey()).entrySet();
//						for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> ee2: eeset2) {
//							if (!eeset1.contains(ee2)) {
//								System.out.println("eeset1 does not contain entry " + e2);
//							}
//						}
//						for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> ee1: eeset1) {
//							if (!eeset2.contains(ee1)) {
//								System.out.println("eeset2 does not contain entry " + ee1);
//							}
//						}
//					}
//					else
//						System.out.println("HOWEVER TABLES ARE EQUAL!!!");
//				}
//			}
//			for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> e1: eset1) {
//				if (!eset2.contains(e1))
//					System.out.println("old table does not contain new entry " + e1);
//			}
			return false;
		}
		return true;
	}

	public String toString() {
		String s = "";
		for (Entry<SootMethodRef, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> mEntry: table.entrySet()) {
			SootMethodRef m = mEntry.getKey();			
			for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> rEntry: mEntry.getValue().entrySet()) {
				Region rThis = rEntry.getKey();
				for (Entry<List<RefinedType>, TypeAndEffects> aEntry: rEntry.getValue().entrySet()) {						
					List<RefinedType> argsTypes = aEntry.getKey();
					TypeAndEffects retType = aEntry.getValue();						
					s += "  " + m + ", " + rThis + ", " + argsTypes + ", " + retType + "\n";   
				}
			}
		}
		return s;
	}
	
	public TypeAndEffects getMatch(SootMethodRef m, Region r, List<RefinedType> argsTypes) {
		Main.mainLog.finer("searching for " + m + " " + r + " " + argsTypes);
		// a built-in entry, as it is a template (which we cannot express using startRegion) 
		if (m.getSignature().equals("<java.lang.String: java.lang.String toString()>")) {
			TypeAndEffects res = new TypeAndEffects(new RefinedStringType((Monoid)r), new Effects(false));
			Main.mainLog.finer("using default entry for this case: " + res); 
			return res;
		}
		Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = table.get(m);
		if (tab1 != null) {
			// Main.mainLog.finer(m + " found; now searching for the thisRegion " + r);
			// the "star" region matches any region
			Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(TSA.starRegion);
			if (tab2 == null)
				tab2 = tab1.get(r);
			if (tab2 != null) {
				// Main.mainLog.finer(r + " found; now searching for the args " + argsTypes);
				if (tab2.containsKey(argsTypes)) {
					TypeAndEffects res = tab2.get(argsTypes);
					Main.mainLog.finer("we found: " + res);
					return res;
				}
			}
		}
		Main.mainLog.finer("not found");
		return null;
	}
}
