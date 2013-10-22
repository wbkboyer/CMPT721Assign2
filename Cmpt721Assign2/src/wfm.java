/*
 *	CMPT 721 Assignment 2 - wfm.java
 *		Wanda B. Boyer
 *	Student number 301242166
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
		definiteClause dummyDC;
		
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
			this.definiteClauseList.add(dummyDC);
			if (!this.A.contains(dummyDC.head)) {
				this.A.add(dummyDC.head);
			}
			addToList(dummyDC.posAtoms, this.A);
			addToList(dummyDC.negAtoms, this.A);
		}
	}
	
	private void solve() {
		/*
		 * 	Loop until there are no changes to T or F:		
		 */
		ArrayList<atom> Tnew;
		ArrayList<atom> Fnew;
		ArrayList<atom> Tposs = new ArrayList<atom>();
		ArrayList<atom> TpossOld;
		ArrayList<atom> union = new ArrayList<atom>();
		ArrayList<definiteClause> dummyDCList;
		definiteClause clauseToConsider;
		do {
			dummyDCList = new ArrayList<definiteClause>();
			Tnew = new ArrayList<atom>();
			Fnew = new ArrayList<atom>();
			TpossOld = Tposs;
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
			System.out.println("definite clause list considered when deriving Tnew :\n "+dummyDCList);
			Tnew = arrayListSetDiff(bottomUp(dummyDCList), this.T_Pi);
			System.out.println("Tnew: "+Tnew+"\n");
			/*
			 * 		(c) Add these atoms to T.
			 */
			addToList(Tnew, this.T_Pi);
			
			/*
			 * 		(d) "Compile" the atoms in T^{new} into the set of rules:
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
			System.out.println("definite clause list after compiling in Tnew:\n "+this.definiteClauseList);
			if (!this.definiteClauseList.isEmpty()) {
				/*
				 * 2. 	(a) Run the bottom up procedure on the rule set, but ignoring all negated
				 *	atoms.
				 *		(b) Call this set T^{poss}. (These atoms might potentially become true later.)
				 */
				dummyDCList = new ArrayList<definiteClause>();
				for(int i = 0; i < this.definiteClauseList.size(); i++) {
					try {
						dummyDCList.add(i, (definiteClause) (this.definiteClauseList.get(i).clone()));
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < dummyDCList.size(); i++) {
					dummyDCList.get(i).negAtoms = new ArrayList<atom>();
				}
				System.out.println("definite clause list used to compute Tposs:\n "+dummyDCList);
				Tposs = arrayListSetDiff(bottomUp(dummyDCList), Tnew);
				System.out.println("Tposs: "+Tposs+ "\n");
				/*
				 * 		(c) Let F^{new} be those atoms whose truth value is "unknown" and that don’t
				 *			appear in T^{poss}. (The atoms in F^{new} are those that cannot possibly be
				 *			true, and so are (now) known to be false.)
				 */
				addToList(Tnew, union);
				addToList(Tposs, union);
				Fnew = arrayListSetDiff(this.A, union);
				System.out.println("Fnew: "+Fnew+"\n");
				/*
				 * 		(d) Add the atoms in F^{new} to F.
				 */
				addToList(Fnew, this.F_Pi);
				
				/*
				 * 		(e) "Compile" the atoms in F^{new} into the set of rules:
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
				System.out.println("definite clause list after compiling in Fnew:\n "+this.definiteClauseList);
			}
			System.out.println("---------------");
			printAtomLists(false);
			System.out.println("---------------\n\n");
		} while (!(Tnew.isEmpty() || Fnew.isEmpty() || this.definiteClauseList.isEmpty()));
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
	private ArrayList<atom> bottomUp(ArrayList<definiteClause> A) {
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
	
	private void printAtomLists(boolean printAInstead) {
		if (printAInstead){
			System.out.println("A: "+ this.A+"\n");
		}
		else{
			System.out.println("T_Pi: "+ this.T_Pi+ ", F_Pi: "+ this.F_Pi);
		}
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
