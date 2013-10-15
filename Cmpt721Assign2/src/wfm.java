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
	
	private boolean searchForAtom (atom dummyAtom, ArrayList<atom> arrayToSearch) {
		return arrayToSearch.contains(dummyAtom);
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
						doesAtomExist = searchForAtom(dummyAtom, this.A);
						if (!doesAtomExist) { 
							this.A.add(dummyAtom);
						}
						dummyAtomGroup.add(dummyAtom);
					}
					else {
						dummyAtom = new atom(tokens[i]);
						doesAtomExist = searchForAtom(dummyAtom, this.A);
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
		 */
		ArrayList<atom> Tnew = new ArrayList<atom>();
		ArrayList<atom> Fnew = new ArrayList<atom>();
		ArrayList<atom> Tposs;
		
		do {
			/*
			 * 1. 	(a) Run the bottom up procedure on those rules that don’t contain a
			 *	negated atom in the body.
			 *		(b) Call the set of newly-derived atoms T^{new} . (These atoms must be true.)
			 */
			Tnew = bottomUp();
				
			/*
			 * 		(c) Add these atoms to T.
			 */
			addToList (Tnew, this.T_Pi);
			
			/*
			 * 		(d) “Compile” the atoms in T^{new} into the set of rules:
			 *			For a \in T^{new} ,
			 *				– delete any rule with a in the head or \tilde a in the body,
			 *				– remove any occurrences of a from the remaining rules.
			 */
			compileIntoRules(Tnew, this.T_Pi);
			
			/*
			 * 2. 	(a) Run the bottom up procedure on the rule set, but ignoring all negated
			 *	atoms.
			 *		(b) Call this set T^{poss}. (These atoms might potentially become true later.)
			 */
			Tposs = bottomUp();
			
			/*
			 * 		(c) Let F^{new} be those atoms whose truth value is “unknown” and that don’t
			 *			appear in T^{poss}. (The atoms in F^{new} are those that cannot possibly be
			 *			true, and so are (now) known to be false.)
			 */
			Fnew = arrayListSetDiff(this.A, Tposs);
			
			/*
			 * 		(d) Add the atoms in F^{new} to F.
			 */
			addToList(Fnew, this.F_Pi);
			
			/*
			 * 		(e) “Compile” the atoms in F^{new} into the set of rules:
			 *			For a \in F^{new},
			 *				– delete any rule with a in the head or a in the body,
			 *				– remove any occurrences of ∼ a from the remaining rules.
			 */
			compileIntoRules(Fnew, this.F_Pi);
			
		} while (!(Tnew.equals(this.T_Pi) && Fnew.equals(this.F_Pi)));	
	}
	
	
	private ArrayList<atom> arrayListSetDiff(ArrayList<atom> A, ArrayList<atom> B) {
		ArrayList<atom> setDiff = new ArrayList<atom>();
		for (int i = 0; i < B.size(); i++) {
			if (A.contains(B.get(i))) {
				continue;
			}
			else {
				if (setDiff.contains(B.get(i))) continue;
				else setDiff.add(B.get(i));
			}
		}
		return setDiff;
	}

	private void addToList(ArrayList<atom> listOfAtomsToAdd, ArrayList<atom> existingAtomList) {
		atom dummyAtom = new atom(null);
		boolean doesAtomExist = false;
		for (int i = 0; i < listOfAtomsToAdd.size(); i++) {
			dummyAtom = listOfAtomsToAdd.get(i);
			doesAtomExist = searchForAtom(dummyAtom, existingAtomList); // search in T_Pi
			if (!doesAtomExist) {
				existingAtomList.add(dummyAtom);
			}
		}
	}

	/*
	 * C := {};
	 *		repeat
	 *			either
	 *				choose r \in A such that
	 *					r is 'h \Leftarrow b_1 \wedge \ldots \wedge b_m'
	 *					b_i \in C for all i and
	 *					h \neg \in C
	 *				C := C \\union {h}
	 *			or
	 *				choose h such that for every rule
	 *					h \Leftarrow b_1 \wedge \ldots \wedge b_m
	 *						either for some b_i we have \tilde \b_i \in C
	 *						or some b_i = \tilde g and g \in C
	 *				C := C \\union {\tilde h}
	 *		until 
	 *			no more choices
	 */
	private ArrayList<atom> bottomUp () {
		ArrayList<atom> consequences = new ArrayList<atom>();
		/*while (true) {
			// go through, looking at dummyAtom.appearsInHeadOfDC is true, and then see if all the atoms in the ArrayList<
			
		}*/
		return consequences;
		
	}
	
	private void compileIntoRules(ArrayList<atom> newAtoms, ArrayList<atom> assignedAtoms) {
		
		
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
