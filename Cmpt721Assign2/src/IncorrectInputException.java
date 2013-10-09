
public class IncorrectInputException extends Exception {
	public IncorrectInputException() {
		
	}
	public IncorrectInputException (String message) {
		super(message);
	}
	public IncorrectInputException (Throwable throwable) {
		super(throwable);
	}
	public IncorrectInputException (String message, Throwable throwable) {
		super(message, throwable);
	}
}
