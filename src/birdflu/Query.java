package birdflu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class Query {
	
	protected String statement;
	protected ResultSet result;
	
	public Query(String statement) {
		
		this.statement = statement;
		
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection con = DriverManager.getConnection
					("jdbc:oracle:thin:@localhost:1521:rispdb1", "s908606", "dadatenbanken303!");
			Statement stmt = con.createStatement();
			result = stmt.executeQuery(statement);
			result.close();
			stmt.close();	
		} catch (SQLException e) {
			e.getMessage();
			e.getSQLState();
			e.getErrorCode();
		}
	}
	
	public int[] getIntArray(int column) throws NoDataException {
		LinkedList<Integer> list = new LinkedList<Integer>();
		try {
			while (result.next()) { list.add(result.getInt(column)); }
		} catch (SQLException e) {
			e.getMessage();
			e.getSQLState();
			e.getErrorCode();
		}
		if (list.size() < 1) { throw new NoDataException(statement); }
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i).intValue();
		}
		return array;
	}	
}
