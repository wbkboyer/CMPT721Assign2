import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class wfm {
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
		int defClauseCount = 0;
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
				defClauseCount++;
			}
			
			tokens = scNextLine.split(" ");
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals(",")) {
					dummyDC.add(dummyAtomGroup);
					dummyAtomGroup = new ArrayList<atom>();
					whatPartOfDC++;
				}
				else if (tokens[i].equals("EMPTY")) {
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
		/*
		 * Need to:
		 * 2) initialize the lists $T_\Pi$ and $F_\Pi$
		 * 		- 	To be considered: what kind of datastructure do we want to use for 
		 * 			$T_\Pi$ and $F_\Pi$? What Features are desired?
		 * 				a)	Want to have easy access to the most-used atoms, instead
		 * 					of having to keep scanning through the list...
		 * 3) 
		 * 4) implement atleast
		 * 		i)	For the renaming of negative atoms, do we make a new atom where
		 * 			we take the negation of the truth assignment of the old atom,
		 * 			if it has already been assigned, unknown otherwise?
		 * 5) implement atmost
		 * 		i) 	How do we deal with union operation?
		 * 		ii) How do we deal with set difference?
		 */
		
		wfm program = new wfm();
		try {
			Scanner sc = new Scanner(new File(args[0]));
			program.readProblem(sc);
			program.printAtomLists(true);
		} 
		catch (FileNotFoundException e) {
			System.out.println("File '"+args[0]+"' not found!");
		}
		catch (IncorrectInputException e) {
			System.out.println(e.getMessage());
		}
	}
}
