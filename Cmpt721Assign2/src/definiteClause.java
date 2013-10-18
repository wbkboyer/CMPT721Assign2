import java.util.ArrayList;
import java.util.Set;

public class definiteClause {
	private static final String EMPTY = null;
	atom head;
	ArrayList posAtoms;
	ArrayList negAtoms;
	
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
	
	public void printDC() {
		System.out.println("[" + this.head + ", " + this.posAtoms + " , " + this.negAtoms + "]");
	}
}
