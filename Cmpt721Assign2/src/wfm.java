import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class wfm {
	final String EMPTY = "EMPTY";
	public ArrayList<atom> T_Pi;
	public ArrayList<atom> F_Pi;
	public ArrayList<atom> A;
	
	public ArrayList<ArrayList<ArrayList<atom>>> definiteClauseList;
	
	public wfm () {
		A = new ArrayList<atom>();
		T_Pi = new ArrayList<atom>();
		F_Pi = new ArrayList<atom>();
		definiteClauseList = new ArrayList<ArrayList<ArrayList<atom>>>();
	}
	
	private boolean searchForAtom (atom dummyAtom, int whichArray) {
		switch (whichArray) {
			case 1: return this.A.contains(dummyAtom);
			case 2: return this.T_Pi.contains(dummyAtom);
			case 3: return this.F_Pi.contains(dummyAtom);
			default: return false;
		}
	}
	
	private void readProblem(Scanner sc) throws IncorrectInputException {
		boolean doesAtomExist = false;
		int whatPartOfDC = 1; // whatPartOfDC takes values 1 (head) 2 (positive atoms) and 3 (negative atoms)
		String scNextLine;
		String[] tokens;
		ArrayList<ArrayList<atom>> dummyDC = new ArrayList<ArrayList<atom>>();
		ArrayList<atom> dummyAtomGroup = new ArrayList<atom>();
		atom dummyAtom;
		
		while (sc.hasNext()) {
			scNextLine = sc.nextLine();
			if (scNextLine.contains("#")){
				if (scNextLine.indexOf("#") == 0) {
					scNextLine = ""; // I AM CHOOSING THE BEHAVIOUR TO IGNORE EMPTY DEFINITE CLAUSES IN THE KB (otherwise, automatically unsat!)
				}
				else{
					scNextLine = scNextLine.substring(0, scNextLine.indexOf("#") - 1);
				}
				whatPartOfDC = 1;
			}
			
			tokens = scNextLine.split(" ");
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals(",")) {
					dummyDC.add(dummyAtomGroup);
					dummyAtomGroup = new ArrayList<atom>();
					whatPartOfDC++;
				}
				else if (tokens[i].equals(EMPTY)) {
					dummyAtomGroup.add(new atom(null));
				}
				else {
					if (whatPartOfDC == 1) {
						dummyAtom = new atom(tokens[i],-1, true);
						doesAtomExist = searchForAtom(dummyAtom, 1);
						if (!doesAtomExist) { 
							this.A.add(dummyAtom);
						}
						dummyAtomGroup.add(dummyAtom);
					}
					else {
						dummyAtom = new atom(tokens[i]);
						doesAtomExist = searchForAtom(dummyAtom, 1);
						if (!doesAtomExist) {
							this.A.add(dummyAtom);
						}
						dummyAtomGroup.add(dummyAtom);
					}
				}
			}
			this.definiteClauseList.add(dummyDC);
			dummyDC = new ArrayList<ArrayList<atom>>();
			
		}
	}

	private void solve() {
		/*
		 * 	Loop until there are no changes to T or F:
		 *	1. 	(a) Run the bottom up procedure on those rules that don’t contain a
		 *	negated atom in the body.
		 *		(b) Call the set of newly-derived atoms T^{new} . (These atoms must be true.)
		 *		(c) Add these atoms to T.
		 *		(d) “Compile” the atoms in T^{new} into the set of rules:
		 *			For a \in T^{new} ,
		 *				– delete any rule with a in the head or ∼ a in the body,
		 *				– remove any occurrences of a from the remaining rules.
		 *	2. 	(a) Run the bottom up procedure on the rule set, but ignoring all negated
		 *	atoms.
		 *		(b) Call this set T^{poss}. (These atoms might potentially become true later.)
		 *		(c) Let F^{new} be those atoms whose truth value is “unknown” and that don’t
		 *	appear in T^{poss}. (The atoms in F^{new} are those that cannot possibly be
		 *	true, and so are (now) known to be false.)
		 *		(d) Add the atoms in F^{new} to F.
		 *		(e) “Compile” the atoms in F^{new} into the set of rules:
		 *			For a \in F^{new},
		 *				– delete any rule with a in the head or a in the body,
		 *				– remove any occurrences of ∼ a from the remaining rules.
		 */
		
		ArrayList<atom> Tnew = new ArrayList<atom>();
		ArrayList<atom> Fnew = new ArrayList<atom>();
		atom dummyAtom = new atom(null);
		boolean doesAtomExist = false;

		while (true) {
			// Make sure atom hasn't yet been added to T_Pi and set its truthValue to true.
			for (int i = 0; i < Tnew.size(); i++) {
				dummyAtom = Tnew.get(i);
				doesAtomExist = searchForAtom(dummyAtom, 2); // search in T_Pi
				if (!doesAtomExist) {
					this.T_Pi.add(dummyAtom);
					
				}
			}
			//	Loop until there are no changes to T or F:
			if (Tnew.equals(this.T_Pi) && Fnew.equals(this.F_Pi)) {
				break;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void printAtomLists(boolean printAToo) {
		if (printAToo){
			if (this.A.isEmpty()) {
				System.out.println("A is empty.");
			}
			else {
				String atomListAasString = A.toString();
				System.out.println("Atom list A:" + atomListAasString);
			}
		}
		
		if (T_Pi.isEmpty()) {
			System.out.println("T_Pi is empty.");
		}
		else {
			String atomListT_PiasString = T_Pi.toString();
			System.out.println("T_Pi:" + atomListT_PiasString);
		}
		
		if (F_Pi.isEmpty()) {
			System.out.println("F_Pi is empty.");
		}
		else {
			String atomListF_PiasString = F_Pi.toString();
			System.out.println("F_Pi" + atomListF_PiasString);
		}
	}
	
	public static void main (String[] args){ 		
		wfm program = new wfm();
		try {
			Scanner sc = new Scanner(new File(args[0]));
			program.readProblem(sc);
			program.solve();
			//program.printAtomLists(true);
		} 
		catch (FileNotFoundException e) {
			System.out.println("File '"+args[0]+"' not found!");
		}
		catch (IncorrectInputException e) {
			System.out.println(e.getMessage());
		}
	}
}
