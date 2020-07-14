package birdflu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.logging.Logger;

public class Query {
	
	protected String query;
	protected ResultSet result;
	protected Statement stmt;
	
	public Query(String query) {
		
		this.query = query;
		
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection con = DriverManager.getConnection
					("jdbc:oracle:thin:@localhost:1521:rispdb1", "s908606", "dadatenbanken303!");
			stmt = con.createStatement();
			result = stmt.executeQuery(query);	
		} catch (SQLException e) {
			Logger logger = Logger.getLogger("SQL Logger");
			logger.warning(e.getMessage());
		}
	}
	
	public int[] getIntArray(int column) throws NoDataException {
		LinkedList<Integer> list = new LinkedList<Integer>();
		try {
			while (result.next()) {
				list.add(result.getInt(column));
			}
		} catch (SQLException e) {
			e.getMessage();
			e.getSQLState();
			e.getErrorCode();
		}
		if (list.size() < 1) { throw new NoDataException(query); }
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i).intValue();
		}
		return array;
	}	
	
	public boolean close() {
		try {
			result.close();
			stmt.close();
			return true;
		} catch (SQLException e) {
			Logger logger = Logger.getLogger("SQL Logger");
			logger.warning(e.getMessage());
			return false;
		}
	}
}
