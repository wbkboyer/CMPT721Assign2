import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class wfm {
	public ArrayList<atom> T_Pi;
	public ArrayList<atom> F_Pi;
	public ArrayList<atom> A;
	
	public wfm (int numAtoms) {
		A = new ArrayList<atom>(numAtoms);
		T_Pi = new ArrayList<atom>(numAtoms);
		T_Pi = new ArrayList<atom>(numAtoms);
	}
	
	/*
	 * The purpose of this method is to check the format of the lists of
	 * the positive and negative literals in each definite clause.
	 */
	
	private void addToAtomList(atom a, int whichList) {
		/*
		 * whichList will tell you whether the atom needs to be added to:
		 * 1)	A (during the initial reading of the problem)
		 * 2)	T_Pi (when the atom is determined to be true)
		 * 3)	F_Pi (when the atom is determined to be false)
		 */
		switch (whichList) {
			case 1: this.A.add(a);
			case 2: this.T_Pi.add(a);
			case 3: this.F_Pi.add(a);
		}
	}
	
	private int nextAvailIndex(int i) {
		/*
		 * Iterate through the 
		 */
		return 0;
	}

	private void checkBodyFormat(Scanner sc, int whatPartOfDC) throws IncorrectInputException  {
		/*
		 * Need to make sure that:
		 * 1)	'[[' doesn't happen except when in the head (when whatPartOfDC == 1)
		 * 2)	']]' doesn't happen, except when in the negative atom list (when whatPartOfDC == 3).
		 * 3)	neither '[ ,' nor '[,' happens.
		 * 4)	neither ', ]' nor ',]' happens.
		 * 5)	neither ',,' nor ', ,' happens.
		 */
		
	}
	
	/* 
	 * Initially implemented for use with just plain old linear arrays; maybe a priority queue based on
	 * the number of occurrences in the head of a rule and the number of occurrences in the body would
	 * be nice!
	 */
	private int searchForAtom (String atomName, int whichArray) {
		switch (whichArray) {
		/*
		 * should i make some kind of overloaded equals method which allows
		 * me to compare the string atomName to the name field of each object?
		 * 
		 */
			case 1: return this.A.indexOf(atomName); // how do I get it to return the atom object whose name is atomName?
			case 2: return this.T_Pi.indexOf(atomName);
			case 3: return this.F_Pi.indexOf(atomName);
			default: return -1;
		}
	}
	
	private void readProblem(Scanner sc) throws IncorrectInputException {
		/*
		 * whatPartOfDC = 1 when you're dealing with the head, 2 if you're dealing
		 * with the positive atoms, and 3 if you're dealing with the negative atoms.
		 */
		int whatPartOfDC = 1;
		int indexOfAtom;
		String scNextLine;
		while (sc.hasNext()) {
			scNextLine = sc.nextLine(); // dummy variable to allow "peeking" so as to act on the input.
			String[] scNextLineArray = scNextLine.split(" ");
			for (int i = 0; i < scNextLineArray.length; i++) {
				if (whatPartOfDC == 1) {
					if (scNextLineArray[i].equals("[")) {
						if (scNextLineArray[i+1].equals("[")) {
							throw new IncorrectInputException("Incorrect input; cannot start a definite clause with '[['!");
						}
						else if (scNextLineArray[i+1].equals(",")) {
							throw new IncorrectInputException("Incorrect input; cannot have empty head of definite clause!");
						}
						else if (scNextLineArray[i+1].equals("#")) {
							throw new IncorrectInputException("Incorrect input; that's not the right place for a comment!");
						}
						/*
						 * If the next token to occur is ']', we're okay with that, because that is the empty
						 * formula, which is vacuously true. So there are really no other special cases to consider.
						 */
						else {
							/* 
							 * Search through list A of atoms to see if this atom exists already. If not, add
							 * the atom to the array. If so, make sure it is mentioned that the atom appears in
							 * the head of a definite clause.
							 */
							indexOfAtom = searchForAtom(scNextLineArray[i], 1);
							if (indexOfAtom == -1) {
								addToAtomList(new atom(scNextLineArray[i], -1, true,false), 1);
							}
							else {
								/*
								 * Change boolean appearsInHeadOfDC to true without changing appearsInBodyOfDC.
								 */
								this.A.get(indexOfAtom).appearsInHeadOfDC = true;
							}
							whatPartOfDC++;
						}
					}
					else {
						throw new IncorrectInputException("Incorrect input; must denote start of definite clause with '['!");
					}
				}
				else if (whatPartOfDC == 2) {
					
				}
				else if (whatPartOfDC == 3) {
					
					
					/*
					 * Once '#' is reached, Scanner allows you to go to the next line of input.
					 */
					if (sc.next().equals("#")) {
						whatPartOfDC = 1;
						/*
						 * Leave the for loop which is iterating from the current line of 
						 * the file (in the form of the String array) to enter the next 
						 * iteration of the while loop.
						 */
						break;
					}
				}
			}	
		}
		
		
		/*int whatPartOfDC = 1;
		while (sc.hasNext()) {
			if (whatPartOfDC == 1) {
				if (sc.next().equals("[")) {
					if (sc.next().equals("[")) {
						throw new IncorrectInputException("Incorrect input!");
					}
					else {
						
					}
				}
				if (sc.next().equals("[")) {
					whatPartOfDC++;
				}
			}
			else if (whatPartOfDC == 2) {
				if (sc.next().equals("[")) {
					if (sc.next().equals("[")) {
						throw new IncorrectInputException("Incorrect input!");
					}
					else {
						
					}
				}
				if (sc.next().equals("[")) {
					whatPartOfDC++;
				}
			}
			else if (whatPartOfDC == 3) {
				if (sc.next().equals("[")) {
					if (sc.next().equals("[")) {
						throw new IncorrectInputException("Incorrect input!");
					}
					else {
						
					}
				}
				whatPartOfDC = 1;
				if (sc.next().equals(" #")) {
						
				}
			}
		}*/
		
		
	}
	
	public static void main (String[] args){ 
		/*
		 * Need to:
		 * 1) read in the program
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
		
		/*
		 * Read in the program.
		 */
		
		wfm program = new wfm(20);
		try {
			Scanner sc = new Scanner(new File(args[0]));
			//sc.useDelimiter(",\\s");
			program.readProblem(sc);
		} 
		catch (FileNotFoundException e) {
			System.out.println("File '"+args[0]+"' not found!");
		}
		catch (IncorrectInputException e) {
			System.out.println(e.getMessage());
		}
	}
}
