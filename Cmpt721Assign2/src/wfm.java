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
			System.out.println(dummyDC);
			this.definiteClauseList.add(dummyDC);
			if (!this.A.contains(dummyDC.head)) {
				this.A.add(dummyDC.head);
			}
			addToList(dummyDC.posAtoms, this.A);
			addToList(dummyDC.negAtoms, this.A);
		}
		System.out.println();
	}
	
	private void solve() {
		/*
		 * 	Loop until there are no changes to T or F:		
		 */
		ArrayList<atom> Tnew;
		ArrayList<atom> Fnew;
		ArrayList<atom> Tposs;
		ArrayList<atom> union = new ArrayList<atom>();
		ArrayList<definiteClause> dummyDCList = new ArrayList<definiteClause>();
		definiteClause clauseToConsider;
		
		do {
			Tnew = new ArrayList<atom>();
			Fnew = new ArrayList<atom>();
			Tposs = new ArrayList<atom>();
			/*
			 * 1. 	(a) Run the bottom up procedure on those rules that don’t contain a
			 *	negated atom in the body.
			 *		(b) Call the set of newly-derived atoms T^{new} . (These atoms must be true.)
			 */
			for (int i = 0; i < this.definiteClauseList.size(); i++) {
				clauseToConsider = this.definiteClauseList.get(i);
				if (clauseToConsider.negAtoms.isEmpty()) {
					dummyDCList.add(clauseToConsider);
				}
			}
			
			System.out.println("dummyDCList before compile: \n"+dummyDCList);
			
			Tnew = bottomUp(dummyDCList);
			System.out.println("Tnew: "+Tnew);
			/*
			 * 		(c) Add these atoms to T.
			 */
			addToList(Tnew, this.T_Pi);
			
			/*
			 * 		(d) “Compile” the atoms in T^{new} into the set of rules:
			 *			For a \in T^{new} ,
			 *				– delete any rule with a in the head or \tilde a in the body,
			 *				– remove any occurrences of a from the remaining rules.
			 */
			
			for (int i = 0; i < this.definiteClauseList.size(); i++) {
				if (Tnew.contains(this.definiteClauseList.get(i).head)) {
					this.definiteClauseList.remove(i);
					i--;
				}
				else {
					for (int j = 0; j < Tnew.size(); j++) {
						if (this.definiteClauseList.get(i).negAtoms.contains(Tnew.get(j))) {
							this.definiteClauseList.remove(i);
							i--;
						}
					}
				}	
			}
			for (int i = 0; i < this.definiteClauseList.size(); i++) {
				this.definiteClauseList.get(i).posAtoms.removeAll(Tnew);
			}
			
			System.out.println("Recall the rule list: \n "+this.definiteClauseList);
			
			if (!this.definiteClauseList.isEmpty()) {
				/*
				 * 2. 	(a) Run the bottom up procedure on the rule set, but ignoring all negated
				 *	atoms.
				 *		(b) Call this set T^{poss}. (These atoms might potentially become true later.)
				 */
				for (int i = 0; i < this.definiteClauseList.size(); i++) {
					this.definiteClauseList.get(i).negAtoms = new ArrayList<atom>();
				}
				System.out.println("definiteClauseList after ignoring negative atoms: \n"+this.definiteClauseList);
				Tposs = bottomUp(this.definiteClauseList);

				System.out.println("Tposs is: "+Tposs);
				/*
				 * 		(c) Let F^{new} be those atoms whose truth value is “unknown” and that don’t
				 *			appear in T^{poss}. (The atoms in F^{new} are those that cannot possibly be
				 *			true, and so are (now) known to be false.)
				 */
				addToList(Tnew, union);
				addToList(Tposs, union);
				System.out.println("Tnew is: "+Tnew);
				System.out.println("union of Tnew and Tposs is: "+union);
				Fnew = arrayListSetDiff(this.A, union);
				System.out.println("Fnew is: "+Fnew);
				
				/*
				 * 		(d) Add the atoms in F^{new} to F.
				 */
				addToList(Fnew, this.F_Pi);
				System.out.println("F_Pi is: "+this.F_Pi);
				
				/*
				 * 		(e) “Compile” the atoms in F^{new} into the set of rules:
				 *			For a \in F^{new},
				 *				– delete any rule with a in the head or a in the body,
				 *				– remove any occurrences of ~ a from the remaining rules.
				 */
				for (int i = 0; i < this.definiteClauseList.size(); i++) {
					if (Fnew.contains(this.definiteClauseList.get(i).head)) {
						this.definiteClauseList.remove(i);
						i--;
					}
					else {
						for (int j = 0; j < Fnew.size(); j++) {
							if (this.definiteClauseList.get(i).posAtoms.contains(Fnew.get(j))) {
								this.definiteClauseList.remove(i);
								i--;
							}
						}
					}	
				}
				for (int i = 0; i < this.definiteClauseList.size(); i++) {
					this.definiteClauseList.get(i).negAtoms.removeAll(Fnew);
				}
				/*
				for (int i = 0; i < this.definiteClauseList.size(); i++) {
					if (Fnew.contains(this.definiteClauseList.get(i).head)) {
						this.definiteClauseList.remove(i);
						//i--;
					}
					else if (!arrayListSetDiff(Fnew, this.definiteClauseList.get(i).posAtoms).isEmpty()){
						this.definiteClauseList.remove(i);
						//i--;
					}	
				}
				for (int i = 0; i < this.definiteClauseList.size(); i++) {
					this.definiteClauseList.get(i).negAtoms.removeAll(Fnew);
				}*/
				/*outerloop2: for (int i = 0; i < this.definiteClauseList.size(); i++) {
					for (int j = 0; j < Fnew.size(); j++) {
						if (this.definiteClauseList.get(i).head.equals(this.F_Pi.get(j))) {
							this.definiteClauseList.remove(i);
							break outerloop2;
						}
						else {
							for (int k = 0; k < this.definiteClauseList.get(i).negAtoms.size(); k++) {
								if (this.definiteClauseList.get(i).negAtoms.get(k).equals(this.F_Pi.get(j))) {
									this.definiteClauseList.remove(i);
									break outerloop2;
								}
							}
						}
					}
				}
				for (int i = 0; i < this.definiteClauseList.size(); i++) {
					this.definiteClauseList.get(i).posAtoms.removeAll(this.F_Pi);
				}*/
				
				System.out.println("definiteClauseList after compiling in Fnew: \n"+this.definiteClauseList);

				System.out.println("Tnew: "+Tnew+ " and Fnew: "+Fnew+ " and Tposs: "+Tposs);
			}
			printAtomLists(false);
		} while (!(arrayListSetDiff(Tnew, this.T_Pi).isEmpty() && arrayListSetDiff(Fnew, this.F_Pi).isEmpty()) || !Tposs.isEmpty());
	}

	/*
	 * C := {};
	 * repeat
	 * 		choose all r \in A (the set of rules) such that
	 * 			r is a rule of the form 'h \Leftarrow b_1 \wedge \ldots \wedge b_m'
	 * 			b_i \in C for all i, and
	 * 			h \neg \in C;
	 * 				C := C \cup {h}
	 * until no more choices
	 */

	private ArrayList<atom> bottomUp(ArrayList<definiteClause> A) { // maybe create A' = A and then delete rules from A'  to make less runs
		ArrayList<atom> C = new ArrayList<atom>();
		ArrayList<atom> Cnew = new ArrayList<atom>();
		do {
			addToList(Cnew, C);
			Cnew = new ArrayList<atom>();
			for (int i = 0; i < A.size(); i++) {
				if ((A.get(i).posAtoms.isEmpty() || C.containsAll(A.get(i).posAtoms)) && !C.contains(A.get(i).head)) {
					Cnew.add(A.get(i).head);
				}
			}
		} while (!arrayListSetDiff(Cnew, C).isEmpty());
		return C;
	}
	
	private ArrayList<atom> arrayListSetDiff(ArrayList<atom> A, ArrayList<atom> B) {
		ArrayList<atom> setDiff = new ArrayList<atom>();
		setDiff.addAll(A);
		for (int i = 0; i < B.size(); i++) {
			setDiff.remove(B.get(i));
		}
		return setDiff;
	}
	
	private void addToList(ArrayList<atom> listOfAtomsToAdd, ArrayList<atom> existingAtomList) {
		atom dummyAtom = new atom(null);
		for (int i = 0; i < listOfAtomsToAdd.size(); i++) {
			dummyAtom = listOfAtomsToAdd.get(i);
			if (!existingAtomList.contains(dummyAtom)) {
				existingAtomList.add(dummyAtom);
			}
		}
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
			System.out.println("Final Result:");
			program.printAtomLists(false);
		} 
		catch (FileNotFoundException e) {
			System.out.println("File '"+args[0]+"' not found!");
		}
		catch (IncorrectInputException e) {
			System.out.println(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("General Exception: ");
			e.printStackTrace();
		}
	}
}
