package sootTSA;

import java.util.LinkedList;
import soot.SootMethod;
import soot.jimple.Stmt;
import util.Pair;

public class CallStringContext implements Context {
	private int k;
	private LinkedList<Pair<SootMethod, Stmt>> callString;
	
	CallStringContext(int n) {
		k = n;
		callString = new LinkedList<Pair<SootMethod, Stmt>>();
	}
	
	CallStringContext(CallStringContext ctx) {
		k = ctx.k;
		callString = new LinkedList<Pair<SootMethod, Stmt>>(ctx.callString);
	}
	
	CallStringContext push(SootMethod m, Stmt s) {
		CallStringContext ctx = new CallStringContext(this); 
		if (k > 0) {
			ctx.callString.addLast(new Pair<SootMethod, Stmt>(m,s));
			if (ctx.callString.size() > k)
				ctx.callString.removeFirst();
		}
		return ctx;
		
	}
	
	public String toString() {
		String str = "<";
		for (Pair<SootMethod, Stmt> el: callString) {
			SootMethod m = el.first;
			Stmt s = el.second;
			str += m.getName() + "," + s.getJavaSourceStartLineNumber();
		}
		str += ">";
		return str;
	}

	@Override
	public int compareTo(Context c) {
		if (c instanceof DefaultContext)
			return -1;
		else if (c instanceof CallStringContext) {
			LinkedList<Pair<SootMethod, Stmt>> cs = ((CallStringContext)c).callString;
			int s1 = callString.size();
			int s2 = cs.size();
			if (s1 != s2)
				return s1 - s2;

			for (int i = 0; i < s1; i++) {
				Pair<SootMethod, Stmt> p1 = callString.get(i);
				Pair<SootMethod, Stmt> p2 = cs.get(i);
				if (!p1.equals(p2)) {
					SootMethod m1 = p1.first;
					SootMethod m2 = p2.first;
					if (m1.equals(m2)) {
						Stmt stm1 = p1.second;
						Stmt stm2 = p2.second;
						int ln1 = stm1.getJavaSourceStartLineNumber();
						int ln2 = stm2.getJavaSourceStartLineNumber();
						if (ln1 == ln2)
							Main.mainLog.severe("same line numbers for two different statements " + stm1 + " " + stm2);
						else return ln1 - ln2;
					}
					else {
						return m1.toString().compareTo(m2.toString());
					}
				}
			}
		}
		return 0;
	}
}
