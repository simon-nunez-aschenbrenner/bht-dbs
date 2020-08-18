package birdflu;

public class NoDataException extends Exception {
	
	public NoDataException(String query) {
		super(query + " has yielded no results!");
	}
}
