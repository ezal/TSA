package sootTSA;

import monoids.Monoid;

public class PosRegion implements Region {
	private String str; 
	private int r;

	public PosRegion(String s, int n) {
		// System.out.println("new region: " + s + ":" + n);
		str = s;
		r = n;
	}
	
	public String getString() {
		return str;
	}
	public void setString(String s) {
		str = s;
	}

	public String toString() {
		switch (r) {
		case TSA.UNK: return "R?";
		case TSA.STAR: return "*";
		case TSA.NIL: return "nil";
		default:
			if (str.equals("I"))
				return "I" + r;
			else
				return "R" + r;
		}
	}

	public int compareTo(Region reg) {
		if (reg instanceof PosRegion) {
			PosRegion p = (PosRegion)reg;
			if (str.equals(p.str))
				return this.r - p.r;
			else 
				return str.compareTo(p.str);
		}
		else if (reg instanceof Monoid)
			return -1;
		else 
			throw new RuntimeException("unknown region type");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PosRegion other = (PosRegion) obj;
		if (r != other.r)
			return false;
		return true;
	}
}
