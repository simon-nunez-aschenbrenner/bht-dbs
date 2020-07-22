package birdflu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Class for creating tables in DDL using a txt-file formated exactly like this:
 * 
 * <tablename>
 * <column1>, <column2>, ...
 * <column1> TYPE [PRIMARY KEY], [<column2> TYPE [REFERENCES <table>(<column>)]], ...
 * <valueColumn1>, <valuecolumn2>, ...
 * <valueColumn1>, <valuecolumn2>, ...
 * ...
 * 
 * Modul Datenbanksysteme, Dozent Aljoscha Marcel Everding, SS2020
 * 
 * @author Simon Aschenbrenner, Luis Rieke, Paul Gronemeyer, Büsra Bagci
 *
 */
public class CreateTable {
	
	public static final String INPUT_DIRECTORY = "./rawdata/";
	public static final String[] TABLES = {
			"H5N1_TERMS_A",
			"H5N1_TERMS_B",
			"H5N1_TERMS_C",
			"COUNTRIES",
			"FOLLOWUP_CATEGORIES",
			"FOLLOWUPS",
			"WEBSITE_CATEGORIES",
			"WEBSITES"
	};
	
	public CreateTable(String filename) {
		
		File file = new File(INPUT_DIRECTORY + filename);
		Scanner in = null;
		
		if(new InputFileFilter().accept(file)) {
			Logger.getLogger("File Logger").fine("Reading " + filename);
			try {
				in = new Scanner(file);
				if(create(in)) {
					Logger.getLogger("File Logger").info("Executed batch of " + filename);
				} else {
					Logger.getLogger("File Logger").warning("Could not execute batch of "
							+ filename);
				}
			} catch (FileNotFoundException e) {
				Logger.getLogger("File Logger").warning
					(filename + " not found: " + e.getMessage());
			} catch (Exception e) {
				Logger.getLogger("File Logger").warning
				("Error while reading " + filename + ": " + e.getMessage());
			} finally {
				if(in != null) {
					try {
						in.close();
					} catch (Exception e) {
						Logger.getLogger("File Logger").warning
						("Error while closing " + filename + ": " + e.getMessage());
					}
				}
			}
		} else {
			Logger.getLogger("File Logger").warning
			("Could not read " + filename + ": Wrong file type");
		}
	}
	
	private boolean create(Scanner in) throws NoSuchElementException {
		
		String tableName = in.nextLine();
		String columns = in.nextLine();
		String integrity = in.nextLine();
		LinkedList<String> insertions = new LinkedList<String>();
		insertions.add(String.format("CREATE TABLE %s(%s)", tableName, integrity));
		
		while(in.hasNextLine()) {
			insertions.add("INSERT INTO " + tableName + "(" + columns + ") VALUES (" + in.nextLine() + ")");
		}
		
		Query query = new Query(insertions);
		boolean success = query.batchSuccess();
		query.close();
		return success;
	}
}
