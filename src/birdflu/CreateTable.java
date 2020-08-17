package birdflu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Static methods for creating/dropping tables, sequences, views using .txt-files
 * containing DDL-statements (without semicolon, one complete statement per line)
 * 
 * Modul Datenbanksysteme, Dozent Aljoscha Marcel Everding, SS2020
 * 
 * @author Simon Aschenbrenner, Luis Rieke, Paul Gronemeyer, Büsra Bagci
 */
public final class CreateTable {
	
	public static final String DROP_TABLES = "DROP_TABLES.txt";
	public static final String[] TABLES = { // Feste Reihenfolge wegen Integritätsbed.
			"H5N1_BEGRIFF_A.txt",
			"H5N1_BEGRIFF_B.txt",
			"H5N1_BEGRIFF_C.txt",
			"H5N1_BEGRIFF.txt",
			"LAND.txt",
			"FAELLE.txt",
			"WEBSEITE_KATEGORIE.txt",
			"WEBSEITE_PRE.txt",
			"WEBSEITE.txt",
			"FOLGE_KATEGORIE.txt",
			"FOLGE_BEGRIFF.txt",
			"KRANKHEIT.txt",
			"NUTZER_IN.txt",
			"SEQUENCE.txt",
			"H5N1_SUCHE.txt",
			"FOLGESUCHE.txt",
			"KRANKHEIT_SUCHE.txt",
			"FOLGESUCHE_UEBERSICHT.txt"
	};
	
	/**
	 * Convenience method for dropping and subsequent creation
	 */
	public static void init() {
		clear();
		create();
	}
	
	/**
	 * This method tries to drop all tables, sequences and views specified in the
	 * corresponding .txt-file. If a table doesn't exist in the connected database
	 * a SQL-Exception is thrown and the batch execution stops; perhaps leading
	 * to missed commands, e.g. tables that are not dropped. Check database if an
	 * exception is thrown, delete manually and use the create method to rebuild.
	 */
	public static void clear() {
		Query query = new Query();
		read(DROP_TABLES, query);
		query.close();
	}
	
	/**
	 * Creates all tables, sequences and views in the order they are listed in the
	 * array TABLES using the corresponding .txt-files containing the DDL-statements
	 */
	public static void create() {
		Query query = new Query();
		for(String filename : TABLES) {
			read(filename, query);
		}
		query.close();
	}
	
	/**
	 * Exception handling prior to the batch-method below.
	 * 
	 * @param filename - .txt-file containing the DDL-statements
	 * @param query - query-object that will handle the execution of the statements
	 */
	private static void read(String filename, Query query) {
		File file = new File("./tables/" + filename);
		Scanner in = null;
		
		if(new InputFileFilter().accept(file)) {
			Logger.getLogger("File Logger").fine("Reading " + filename);
			try {
				in = new Scanner(file);
				if(batch(in, query)) {
					Logger.getLogger("File Logger").info("Executed batch of "
							+ filename);
				} else {
					Logger.getLogger("File Logger").warning("Error while executing "
							+ "batch of " + filename);
				}
			} catch (FileNotFoundException e) {
				Logger.getLogger("File Logger").warning
					(filename + " not found");
			} catch (NoSuchElementException e) {
				Logger.getLogger("File Logger").warning
					(filename + "missing element");
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
	
	/**
	 * Supplies the DDL-statements as Strings in a LinkedList to the executeBatch-
	 * method of the query-object
	 * 
	 * @param in - scanner reading statements from a .txt-file
	 * @param query - query-object that will execute these statements
	 * @return true if the whole batch was executed successfully, false if not
	 */
	private static boolean batch(Scanner in, Query query) {
		LinkedList<String> statements = new LinkedList<String>();
		while(in.hasNextLine()) {
			statements.add(in.nextLine());
		}
		return query.executeBatch(statements);
	}
}
