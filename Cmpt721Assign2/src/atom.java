
public class atom {
	public String name;
	public int truthValue = -1; // Truth value is initially unknown.
	public int numPosInstances = 0;
	public int numNegInstances = 0;
	public boolean appearsInHeadOfDC;
	public boolean appearsInBodyOfDC;
	
	/*
	 * Initializes the atom object with its name in the program.
	 */
	public atom (String name) {
		this.name = name;
	}
	
	/*
	 * Overloading constructor.
	 */
	public atom (String name, int truthValue, boolean appearsInHeadOfDC) {
		this.name = name;
		this.truthValue = truthValue;
		this.appearsInHeadOfDC = appearsInHeadOfDC; // if an atom doesn't appear in the head, by CK, must be false
		//this.appearsInBodyOfDC = appearsInBodyOfDC;
	}
	
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object atomToCompareTo) { // Right!
	    if (atomToCompareTo == null) {
	        return false;
	    }
	    if (getClass() != atomToCompareTo.getClass()) {
	        return false;
	    }
	    final atom other = (atom) atomToCompareTo;
	    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
	        return false;
	    }
	    return true;
    }
}
