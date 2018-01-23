package sootTSA;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import comparators.ContextComparator;
import comparators.ListComparator;
import comparators.RegionComparator;
import comparators.ToStringComparator;

import monoids.Monoid;

import soot.SootMethodRef;



public class MethodTable {
	Map<SootMethodRef, Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>> table;
	// static private Map<SootMethodRef, SootMethodRef> libMap;
	// NOTE: Alternatively, we could have something like:
	// Map<SootMethodRef, Set<MethodRefinedType>> table;
	
	public Map<SootMethodRef, Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>> getTable() {
		return table;
	}

	public MethodTable() {
		table = new TreeMap<SootMethodRef, Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>>(new ToStringComparator<SootMethodRef>());		
	}

	// copy constructor
	public MethodTable(MethodTable mTable) {
		table = new TreeMap<SootMethodRef, Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>>(new ToStringComparator<SootMethodRef>());
		for (Entry<SootMethodRef, Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>> mEntry: mTable.table.entrySet()) 
		{
			SootMethodRef m = mEntry.getKey();
			for (Entry<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> cEntry: mEntry.getValue().entrySet()) {
				Context ctx = cEntry.getKey();
				for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> rEntry: cEntry.getValue().entrySet()) 
				{
					Region r = rEntry.getKey();					
					for (Entry<List<RefinedType>, TypeAndEffects> aEntry: rEntry.getValue().entrySet()) {
						List<RefinedType> argsTypes = aEntry.getKey();
						TypeAndEffects retType = aEntry.getValue();
						put(m, ctx, r, argsTypes, retType);
					}
				}
			}
		}
	}

	private Map<List<RefinedType>, TypeAndEffects> newArgTypeList() {
		Map<List<RefinedType>, TypeAndEffects> map = new TreeMap<List<RefinedType>, TypeAndEffects>(new ListComparator());
		return map;
	}
	
	private Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> get(SootMethodRef m) {
		return table.get(m);
	}
	
	public TypeAndEffects get(SootMethodRef m, Context ctx, Region r, List<RefinedType> args) {
		Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> tab0 = table.get(m);
		if (tab0 != null) {
			Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = tab0.get(ctx);
			if (tab1 != null) {
				Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(r);
				if (tab2 != null) {
					return tab2.get(args);
				}
			}
		}
		return null;
	}

	public Boolean containsKey(SootMethodRef m, Context ctx, Region r, List<RefinedType> args) {
		Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> tab0 = table.get(m);
		if (tab0 != null) {
			Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = tab0.get(ctx);
			if (tab1 != null) {
				Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(r);
				if (tab2 != null) {
					return tab2.containsKey(args);
				}
			}
		}
		return false;
	}
	
	public void put(SootMethodRef m, Context ctx, Region r, List<RefinedType> argsTypes, TypeAndEffects retType) {
		Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> tab0 = table.get(m);
		if (tab0 != null) {
			Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = tab0.get(ctx);
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
				tab0.put(ctx, tab1);
				table.put(m, tab0);
			}
		}
		else {
			tab0 = new TreeMap<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>(new ContextComparator());
			TreeMap<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = new TreeMap<Region, Map<List<RefinedType>, TypeAndEffects>>(new RegionComparator());
			Map<List<RefinedType>, TypeAndEffects> tab2 = newArgTypeList();
			tab2.put(argsTypes, retType);
			tab1.put(r, tab2);
			tab0.put(ctx, tab1);
			table.put(m, tab0);
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

	public String toString() {
		String s = "";
		for (Entry<SootMethodRef, Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>>> mEntry: table.entrySet()) {
			SootMethodRef m = mEntry.getKey();
			for (Entry<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> cEntry: mEntry.getValue().entrySet()) {
				Context ctx = cEntry.getKey();
				for (Entry<Region, Map<List<RefinedType>, TypeAndEffects>> rEntry: cEntry.getValue().entrySet()) {
					Region rThis = rEntry.getKey();
					for (Entry<List<RefinedType>, TypeAndEffects> aEntry: rEntry.getValue().entrySet()) {						
						List<RefinedType> argsTypes = aEntry.getKey();
						TypeAndEffects retType = aEntry.getValue();						
						s += "  " + m + ", " + ctx + ", " + rThis + ", " + argsTypes + ", " + retType + "\n";   
					}
				}
			}
		}
		return s;
	}
	
	public TypeAndEffects getMatch(SootMethodRef m, List<RefinedType> argsTypes) {
		Main.mainLog.finer("searching for " + m + " " + argsTypes);
//		// a built-in entry, as it is a template (which we cannot express using startRegion) 
//		if (m.getSignature().equals("<java.lang.String: java.lang.String toString()>")) {
//			TypeAndEffects res = new TypeAndEffects(new RefinedStringType((Monoid)r), new Effects(false));
//			Main.mainLog.finer("using default entry for this case: " + res); 
//			return res;
//		}
		Map<Context, Map<Region, Map<List<RefinedType>, TypeAndEffects>>> tab0 = table.get(m);
		if (tab0 != null) {
			Map<Region, Map<List<RefinedType>, TypeAndEffects>> tab1 = tab0.get(DefaultContext.getInstance());
			if (tab1 != null) {
				// Main.mainLog.finer(m + " found; now searching for the thisRegion " + r);
				// the "star" region matches any region
				Map<List<RefinedType>, TypeAndEffects> tab2 = tab1.get(TSA.starRegion);
				if (tab2 != null) {
					// Main.mainLog.finer(r + " found; now searching for the args " + argsTypes);
					if (tab2.containsKey(argsTypes)) {
						TypeAndEffects res = tab2.get(argsTypes);
						Main.mainLog.finer("we found: " + res);
						return res;
					}
				}
				else
					throw new RuntimeException("internal error: region in built-in table should be starRegion");
			}
			else 
				throw new RuntimeException("internal error: context in built-in table should be the default context");
		}
		Main.mainLog.finer("not found");
		return null;
	}
}
