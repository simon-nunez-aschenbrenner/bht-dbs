package test;

import java.sql.*;

public class DataBaseTest {

	public DataBaseTest() {
	}

	public static void main(String[] args) {
		
		String query = "select to_char(querydata.querytime, 'mm.dd'), count(distinct querydata.querytime) from aoldata.querydata where query like '%bird flu%' group by to_char(querydata.querytime, 'mm.dd') order by 1";
		ResultSet result = null;
		int[] array = new int[92];
		
		try {
			
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection con = DriverManager.getConnection
					("jdbc:oracle:thin:@localhost:1521:rispdb1", "s908606", "dadatenbanken303!");
			Statement stmt = con.createStatement();
			result = stmt.executeQuery(query);
			
			int i = 0;
			while (result.next()) {
				array[i] = result.getInt(2);
				i++;
				if (i >= array.length) {
					System.err.println("Array too small");
					break;
				}
			}
			result.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.getMessage();
			e.getSQLState();
			e.getErrorCode();
		}
	
	}
}
