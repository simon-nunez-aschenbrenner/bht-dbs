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
 * @author Simon Aschenbrenner, Luis Rieke, Paul Gronemeyer, Büsra Bagci
 */

public class Query {
	
	protected String query;
	protected Connection con;
	protected ResultSet result;
	protected Statement stmt;
	
	/**
	 * Set up SQL Database Connection and initialize Statement-object
	 */
	
	public Query() {
		this.query = "Unknown Query";
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			con = DriverManager.getConnection
					("jdbc:oracle:thin:@localhost:1521:rispdb1", "s908606", "dadatenbanken303!");
			stmt = con.createStatement();
			Logger.getLogger("SQL Logger").finer("Established connection to database");
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").severe("SQL Exception: " + e.getMessage());
		}
	}

	/**
	 * Set up SQL Database Connection and execute a single statement
	 */
	
	public Query(String query) {
		this();
		this.query = query;
		try {
			result = stmt.executeQuery(query);
			Logger.getLogger("SQL Logger").finer("Executed query: " + query);
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").warning("SQL Exception: " + e.getMessage());
		}
	}

	/**
	 * Execute a batch of statements (for DDL)
	 */
	public boolean executeBatch(LinkedList<String> queries) {
		try {
			for(String sql : queries) {
				stmt.addBatch(sql);
				Logger.getLogger("SQL Logger").finest("Adding to batch: " + sql);
			}
			int[] resultArray = stmt.executeBatch();
			String resultString = "";
			boolean batchSuccess = true;
			for(int i : resultArray) {
				if(i < 0) { batchSuccess = false; }
				resultString += Integer.toString(i);
			}
			Logger.getLogger("SQL Logger").finer("Results: " + resultString);
			return batchSuccess;
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").warning("SQL Exception: " + e.getMessage());
			return false;
		}
	}
	
	public ResultSet getResultSet() {
		return result;
	}
	
	public LinkedList<Integer> getList(int column) throws NoDataException {
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		try {
			while (result.next()) {
				Integer value = result.getInt(column);
				if (result.wasNull()) {
					Logger.getLogger("SQL Logger").finest("Last value read was null");
				}
				Logger.getLogger("SQL Logger").finest(
						String.format("Last read value: %d", value));
				list.add(value);
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
				if (result.wasNull()) {
					Logger.getLogger("SQL Logger").finest("Last key read was null");
				}
				Integer value = result.getInt(valueColumn);
				if (result.wasNull()) {
					Logger.getLogger("SQL Logger").finest("Last value read was null");
				}
				Logger.getLogger("SQL Logger").finest(
						String.format("Last read key/value pair: %s %d", key, value));
				if(key != null) { map.put(key, value); }
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
			con.close();
		} catch (SQLException e) {
			Logger.getLogger("SQL Logger").severe("SQL Exception: " + e.getMessage());
		}
	}
}