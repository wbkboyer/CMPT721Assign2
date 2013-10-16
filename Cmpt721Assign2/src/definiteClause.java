import java.util.ArrayList;
import java.util.Set;


public class definiteClause {
	private static final String EMPTY = null;
	atom head;
	ArrayList posAtoms;
	ArrayList negAtoms;
	
	public definiteClause() {
		this.head = new atom(EMPTY);
		this.posAtoms = new ArrayList<atom>();
		this.negAtoms = new ArrayList<atom>();
	}
	public definiteClause (atom head) {
		this.head = head;
		this.posAtoms = new ArrayList<atom>();
		this.negAtoms = new ArrayList<atom>();
	}
	public definiteClause (atom head, ArrayList posAtoms, ArrayList negAtoms) {
		this.head = head;
		this.posAtoms = posAtoms;
		this.negAtoms = negAtoms;
	}
}
