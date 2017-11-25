package typetranslation;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import comparators.ToStringComparator;
import soot.ArrayType;
import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;



public class TypeMap
{
	public static Map<String, String> nameMap = new TreeMap<String, String>();

	static {
		nameMap.put("java.lang.StringBuffer", "mockup.misc.StringBuffer");
		nameMap.put("java.lang.StringBuilder", "mockup.misc.StringBuilder");
		
		nameMap.put("java.util.Collection", "mockup.misc.Collection");
		nameMap.put("java.util.Iterator", "mockup.misc.Iterator");
		nameMap.put("java.util.List", "mockup.misc.Collection");
		nameMap.put("java.util.LinkedList", "mockup.misc.LinkedList");
		nameMap.put("java.util.ArrayList", "mockup.misc.LinkedList");
		nameMap.put("java.util.AbstractSequentialList", "mockup.misc.LinkedList");
		nameMap.put("java.util.Enumeration", "mockup.misc.Enumeration");
		nameMap.put("java.util.StringTokenizer", "mockup.misc.StringTokenizer");
		nameMap.put("java.util.Arrays", "mockup.misc.Arrays");
		nameMap.put("java.util.Map", "mockup.misc.Map");
		nameMap.put("java.util.HashMap", "mockup.misc.HashMap");
		nameMap.put("java.util.Map$Entry", "mockup.misc.Map$Entry");
		nameMap.put("java.util.Set", "mockup.misc.Set");
		nameMap.put("java.util.HashSet", "mockup.misc.HashSet");
		nameMap.put("java.util.TreeSet", "mockup.misc.HashSet");
		nameMap.put("javax.servlet.http.HttpServlet", "mockup.javax.servlet.DummyHttpServlet");
		
		nameMap.put("javax.servlet.http.Cookie", "mockup.misc.Cookie");
		nameMap.put("java.io.InputStreamReader", "mockup.misc.InputStreamReader");
		nameMap.put("java.io.Reader", "mockup.misc.Reader");
		nameMap.put("java.io.BufferedReader", "mockup.misc.BufferedReader");
		
		nameMap.put("com.oreilly.servlet.MultipartRequest", "mockup.com.oreilly.servlet.MultipartRequest");
		// nameMap.put("java.io.PrintWriter", "mockup.misc.PrintWriter");
	}
	
	private Map<RefType, RefType> newTypes = 
			new TreeMap<RefType, RefType>(new ToStringComparator<RefType>());
	
	public TypeMap() {
		RefType std, our;

		for (Entry<String, String> entry: nameMap.entrySet()) 
			if (Scene.v().containsClass(entry.getKey())) {
				std = Scene.v().getRefType(entry.getKey());
				our = Scene.v().getRefType(entry.getValue());
				newTypes.put(std, our);
				// System.out.println("added the mapping: " + std + " -> " + our);
			}
	}

	public RefType getNewRefType(RefType t) {
		RefType newt = newTypes.get((RefType)t);
		if (newt == null)
			return t;
		else
			return newt;
	}

	public Type getNewType(Type t) {
		if (t instanceof RefType)
			return getNewRefType((RefType)t);
		else if (t instanceof ArrayType) {
			Type et = ((ArrayType)t).getArrayElementType();
			return ArrayType.v(getNewType(et), ((ArrayType)t).numDimensions);
		}
		else
			return t;
	}
	
	public List<Type> getNewTypeList(List<Type> typesList) {
		List<Type> newList = new LinkedList<Type>();
		Boolean changed = false;
		for (Type type: typesList) {
			Type newType = getNewType(type);
			if (newType != type)
				changed = true;
			newList.add(newType);
		}
		if (changed)
			return newList;
		else
			return typesList;
					
	}
}
