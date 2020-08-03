package birdflu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Class for creating tables using txt-files containing the DDL-statements
 * without semicolon. One complete statement per line.
 * 
 * Modul Datenbanksysteme, Dozent Aljoscha Marcel Everding, SS2020
 * 
 * @author Simon Aschenbrenner, Luis Rieke, Paul Gronemeyer, Büsra Bagci
 *
 */
public class CreateTable {
	
	public static final String INPUT_DIRECTORY = "./tables/";
	public static final String DROP_TABLES = "DROP_TABLES.txt";
	public static final String[] TABLES = {
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
			"KRANKHEIT_SUCHE.txt"
	};
	
	public static void init() {
		clear();
		create();
	}
	
	public static void clear() {
		
		Query query = new Query();
		read(DROP_TABLES, query);
		query.close();
	}
	
	public static void create() {

		Query query = new Query();
		for(String filename : TABLES) {
			read(filename, query);
		}
		query.close();
	}
	
	private static void read(String filename, Query query) {
		
		File file = new File(INPUT_DIRECTORY + filename);
		Scanner in = null;
		
		if(new InputFileFilter().accept(file)) {
			Logger.getLogger("File Logger").fine("Reading " + filename);
			try {
				in = new Scanner(file);
				if(batch(in, query)) {
					Logger.getLogger("File Logger").info("Executed batch of " + filename);
				} else {
					Logger.getLogger("File Logger").warning("Error while executing batch of "
							+ filename);
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
	
	private static boolean batch(Scanner in, Query query) {
		
		LinkedList<String> statements = new LinkedList<String>();
		while(in.hasNextLine()) {
			statements.add(in.nextLine());
		}
		return query.executeBatch(statements);
	}
}
