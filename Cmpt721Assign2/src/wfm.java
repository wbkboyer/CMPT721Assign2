import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;


public class wfm {
	public ArrayList<atom> T_Pi;
	public ArrayList<atom> F_Pi;
	public ArrayList<atom> A;
	
	public ArrayList<definiteClause> definiteClauseList;
	
	public wfm () {
		A = new ArrayList<atom>();
		T_Pi = new ArrayList<atom>();
		F_Pi = new ArrayList<atom>();
		definiteClauseList = new ArrayList<definiteClause>();
	}
		
	private void readProblem(Scanner sc) throws IncorrectInputException {
		String scNextLine;
		String[] tokens;		
		definiteClause dummyDC;
		System.out.println("Input Problem:");
		while (sc.hasNext()) {
			scNextLine = sc.nextLine();
			if (scNextLine.contains("#")){
				if (scNextLine.indexOf("#") == 0) {
					scNextLine = ""; // I AM CHOOSING THE BEHAVIOUR TO IGNORE EMPTY DEFINITE CLAUSES IN THE KB (otherwise, automatically unsat!)
				}
				else{
					scNextLine = scNextLine.substring(0, scNextLine.indexOf("#") - 1).trim();
				}
			}
			dummyDC = new definiteClause(scNextLine);
			dummyDC.printDC();
			this.definiteClauseList.add(dummyDC);
			this.A.addAll(dummyDC.posAtoms);
			this.A.addAll(dummyDC.negAtoms);
		}
		System.out.println();
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
			 *				– remove any occurrences of ~ a from the remaining rules.
			 */
			compileIntoRules(Fnew, this.F_Pi);
			printAtomLists(false);
		} while (!(arrayListSetDiff(Tnew, this.T_Pi).isEmpty() && arrayListSetDiff(Fnew, this.F_Pi).isEmpty()));
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
			if (!existingAtomList.contains(dummyAtom)) {
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
		/*ArrayList<atom> newConsequences = new ArrayList<atom>();
		do {
			// go through, looking at dummyAtom.appearsInHeadOfDC is true, and then see if all the atoms in the ArrayList<
			
		} while (!consequences.equals(newConsequences));*/
		return consequences;
		
	}
	
	private void compileIntoRules(ArrayList<atom> newAtoms, ArrayList<atom> assignedAtoms) {
		
		
	}
	
	private void printAtomLists(boolean printAToo) {
		if (printAToo){
			System.out.println("A: "+ this.A);
		}
		System.out.println("T_Pi: "+ this.T_Pi+ ", F_Pi: "+ this.F_Pi + "\n");
	}
	
	public static void main (String[] args){ 		
		wfm program = new wfm();
		try {
			Scanner sc = new Scanner(new File(args[0]));
			program.readProblem(sc);
			program.printAtomLists(true);
			program.solve();
		} 
		catch (FileNotFoundException e) {
			System.out.println("File '"+args[0]+"' not found!");
		}
		catch (IncorrectInputException e) {
			System.out.println(e.getMessage());
		}
	}
}
