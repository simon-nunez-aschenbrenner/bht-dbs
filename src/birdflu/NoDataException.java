package birdflu;

public class NoDataException extends Exception {
	
	public NoDataException(String message) {
		super(message + "\nhas yielded no results!");
	}
}
