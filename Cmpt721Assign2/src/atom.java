
public class atom {
	public String name;
	public boolean appearsInHeadOfDC;

	public atom (String name) {
		this.name = name;
	}
	
	public atom() {
		this.name = null;
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
