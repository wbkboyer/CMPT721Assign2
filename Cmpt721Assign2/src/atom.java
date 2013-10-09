
public class atom {
	public String name;
	public int truthValue;
	public int numPosInstances;
	public int numNegInstances;
	public boolean appearsInHeadOfDC;
	public boolean appearsInBodyOfDC;
	
	/*
	 * Initializes the atom object with its name in the program, and sets its truth value
	 * to -1 to indicate that its truth value is initially unknown.
	 */
	public atom (String name) {
		this.name = name;
		this.truthValue = truthValue;
		this.numPosInstances = 0;
		this.numNegInstances = 0;
	}
	
	/*
	 * Overloading constructor.
	 */
	public atom (String name, int truthValue, boolean appearsInHeadOfDC, boolean appearsInBodyOfDC) {
		this.name = name;
		this.truthValue = truthValue;
		this.appearsInHeadOfDC = appearsInHeadOfDC;
		this.appearsInBodyOfDC = appearsInBodyOfDC;
		this.numPosInstances = 0;
		this.numNegInstances = 0;
	}
}
