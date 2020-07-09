package birdflu;

public class Data {

	public static void main(String[] args) {
		
		Query query = new Query(Statements.FRAGE_1_B);
		int[] test = null;
		
		try {
			test = query.getIntArray(2);
		} catch (NoDataException e) {
			System.err.println(e.getMessage());
		}
		
		for (int i : test) {
			System.out.println(i);
		}
		
		query.close();

	}

}
