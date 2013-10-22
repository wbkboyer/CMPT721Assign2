/*
 *	CMPT 721 Assignment 2 - definiteClause.java
 *		Wanda B. Boyer
 *	Student number 301242166
 */

import java.util.ArrayList;

public class definiteClause implements Cloneable{
	atom head;
	ArrayList<atom> posAtoms;
	ArrayList<atom> negAtoms;
	boolean lookedAt = false;
	
	  public Object clone() throws CloneNotSupportedException {
		  definiteClause clone = (definiteClause)super.clone();
	        return clone;
	    }
	
	public definiteClause(String tokenString) {
		String[] tokens = tokenString.trim().split(",");
		
		this.head = new atom(tokens[0].trim());
		this.head.appearsInHeadOfDC = true;
		
		this.posAtoms = addToAtomGroup(tokens[1].trim().split(" "));
		this.negAtoms = addToAtomGroup(tokens[2].trim().split(" "));
	}
	
	private ArrayList<atom> addToAtomGroup (String[] tokens) {
		ArrayList<atom> atomGroup = new ArrayList<atom>();
		atom dummyAtom;
		
		for (int i = 0; i < tokens.length; i++) {
			if (!tokens[i].equals("EMPTY")) {
				dummyAtom = new atom(tokens[i]);
				if (!atomGroup.contains(dummyAtom)) {
					atomGroup.add(dummyAtom);
				}
			}
		}
		return atomGroup;
	}
	@Override
	public String toString() {
		return "[" + this.head + ", " + this.posAtoms + " , " + this.negAtoms + "]\n";
	}
}
