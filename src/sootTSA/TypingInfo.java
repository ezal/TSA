package sootTSA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import comparators.RegionComparator;
import monoids.Monoid;

import soot.ArrayType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;


public class TypingInfo {
	public final MethodTable mTable; // the built-in table for external library methods
	public SootMethod epMethod; // the entry point

	public TypingInfo() {
		mTable = new MethodTable();
	}
	
	// NOTE: table columns must be separated by one or more 'tab's; 
	//       parsing is very sensitive, check for correct use of delimiters
	public void parseMethodTableFile(String filename) {
		Main.mainLog.info("parsing built-in method table file " + filename);
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;		     
		    while ((line = br.readLine()) != null) {
		    	String trimmed = line.trim();
		    //	System.out.println(trimmed);
		    	if (trimmed.equals("")) 
		    		continue;		    	
		    	if (trimmed.charAt(0) == '#') // ignore comments
		    	   continue;
		    	addMethodTableEntry(trimmed);
		    }
		} catch (FileNotFoundException e) {
			Main.mainLog.severe("method table file not found");
			System.exit(1);
		} catch (IOException e) {
			Main.mainLog.severe("error reading from tables file");
			System.exit(1);
		}
	}
	
	void addMethodTableEntry(String str) {
		String[] cols = str.split("\t+");
		
//		System.out.println("method table: ");
//		for(int i = 0; i < cols.length; i++)
//			System.out.println("we have read: --" + cols[i] + "--");
		
		if (cols.length != 5) {
			Main.mainLog.severe("couldn't parse method table entry; wrong number of columns: " + cols.length);
			throw new RuntimeException("parse error");
		}
		
		// NOTE: strangely, sometimes this works also when the package name is not present; 
		// however, note that comparison is string-based so it will fail 
		// if there is a typo in the class name
		if (!Scene.v().containsClass(cols[0])) {
			Main.mainLog.finest("Class " + cols[0] + " not present in Scene. Ignoring entry in built-in method table.");
			return;
		}
		
		SootClass c = Scene.v().getSootClass(cols[0]);
		
		List<RefinedType> argsTypes = parseRefinedTypeList(cols[2]);
		// in the method table, we require that non-string arguments have exactly one region 
		for (RefinedType e: argsTypes) {
			if (e instanceof RefinedObjectType && ((RefinedObjectType)e).getRegions().size() != 1) {
				Main.mainLog.severe("method argument has no or more than one regions");
				throw new RuntimeException("parse error");
			}
		}
		
		RefinedType retType = parseRefinedType(cols[3]);

		// We build the signature of the method
		// NOTE: in case of overloaded methods, we might need to explicitly specify the signature, 
		// instead of deducing it from the list of refined types of the arguments
		List<Type> aList = new LinkedList<Type>();
		for (RefinedType argType: argsTypes)
			aList.add(argType.getType());
		Type rType = retType.getType();

		// NOTE: we assume it's not a static method
		SootMethodRef m = Scene.v().makeMethodRef(c, cols[1], aList, rType, false);
		
		Effects effects = new Effects(TSA.parseSet(cols[4]));

		// Main.mainLog.fine("adding entry " + m + " " + r + " " + l + " " + retType);
		mTable.put(m, DefaultContext.getInstance(), TSA.starRegion, argsTypes, new TypeAndEffects(retType, effects));
	}

	List<RefinedType> parseRefinedTypeList(String str) {
		String[] elems = str.substring(1,str.length()-1).split("; ");
		LinkedList<RefinedType> l = new LinkedList<RefinedType>();
		for (int i = 0; i < elems.length; i++) {
			if (!elems[i].equals("")) 
				l.addLast(parseRefinedType(elems[i]));
		}
		return l;
	}
	
	RefinedType parseRefinedType(String str) {
		RefinedType refType;

		String[] refTypeStr = str.split(", ");
		if (refTypeStr.length == 1)
			return new RefinedNonRefType(Scene.v().getType(str));
		if (refTypeStr.length > 2)
			throw new RuntimeException("method table parse error: "
					+ "a refined type should be a tuple of a class name and a set of regions "
					+ "separated by ', '; regions should be seprated by ','");
		
		String classStr = refTypeStr[0].substring(1);
		String annotStr = refTypeStr[1].substring(1, refTypeStr[1].length()-2);
		// Main.mainLog.finer("parsing string annotStr = " + annotStr);
		if (classStr.startsWith("String") ) {
			Monoid e = TSA.mon.parseElement(annotStr);
			if (classStr.equals("String")) 				
				refType = new RefinedStringType(e);
			else if (classStr.equals("String[]"))
				refType = new RefinedArrayType(new RefinedStringType(e));
			else
				throw new RuntimeException("method table parse error: unknown class name");
		} 
		else {
			Type t = Scene.v().getType(classStr);
			// System.out.println("[parseRefinedType] sootCls = " + classStr + ", that is, " + fc);
			String regStr = refTypeStr[1].substring(1, refTypeStr[1].length()-2);
			String[] regs = regStr.split(",");
			Set<Region> fRegions= new TreeSet<Region>(new RegionComparator());
			for (int i = 0; i < regs.length; i++) {
				if (!regs[i].equals("")) {
					Region fr;
					if (regs[i].equals("nil"))
						fr = TSA.nilRegion;
					else if (regs[i].equals("?"))
						fr = TSA.unknownRegion;
					else {
						fr = TSA.mon.parseElement(regs[i]); // could be a monoid element, if the class is Object
						if (fr == null)
							throw new RuntimeException("method table parse error: unrecognized region");
					}
					fRegions.add(fr);
				}
			}
			if (t instanceof RefType)
				refType = new RefinedObjectType((RefType)t, fRegions);
			else if (t instanceof ArrayType) {
				Type et = ((ArrayType)t).getElementType();
				if (et instanceof RefType)
					refType = new RefinedArrayType((RefType)et, fRegions);
				else {
					Main.mainLog.severe("cannot give regions to non-Ref type");
					throw new RuntimeException("spec error");
				}
			} else {
				Main.mainLog.severe("cannot give regions to non-Ref type");
				throw new RuntimeException("spec error");
			}
		}
		return refType;
	}
}
