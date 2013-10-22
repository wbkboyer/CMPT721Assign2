/*
 *	CMPT 721 Assignment 2 - atom.java
 *		Wanda B. Boyer
 *	Student number 301242166
 */
public class atom {
	public String name;
	public boolean appearsInHeadOfDC;

	public atom (String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}
	
	@Override public boolean equals(Object atomToCompareTo) { // Right!
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
