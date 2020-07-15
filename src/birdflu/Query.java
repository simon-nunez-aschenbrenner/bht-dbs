package birdflu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Class for collecting data for the semester project "Bird Flu"
 * 
 * Modul Datenbanksysteme, Dozent Aljoscha Marcel Everding, SS2020
 * 
 * @author Simon Aschenbrenner, Luis Rieke, Büsra Bagci, Paul Gronemeyer
 */
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
			Logger.getLogger("SQL Logger").severe("SQL Exception: " + e.getMessage());
		}
	}
	
	public LinkedList<Integer> getList(int column) throws NoDataException {
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		try {
			while (result.next()) {
				list.add(result.getInt(column));
			}
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").warning("SQL Exception: " + e.getMessage());
		}
		
		if (list.size() < 1) { throw new NoDataException(query); }
		else { return list; }
	}
	
	public HashMap<String, Integer> getMap(int keyColumn, int valueColumn)
			throws NoDataException {
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		try {
			while (result.next()) {
				String key = result.getString(keyColumn);
				Integer value = result.getInt(valueColumn);
				if(key != null && value != null) { map.put(key, value); }
			}
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").warning("SQL Exception: " + e.getMessage());
		}
		
		if (map.size() < 1) { throw new NoDataException(query); }
		else { return map; }
	}
	
	public void close() {
		try {
			result.close();
			stmt.close();
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").severe("SQL Exception: " + e.getMessage());
		}
	}
}