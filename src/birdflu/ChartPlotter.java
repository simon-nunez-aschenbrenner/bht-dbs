package birdflu;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ChartPlotter {

	public static void main(String[] args) {
		
		Logger sqlLogger = Logger.getLogger("SQL Logger");
		Logger crtLogger = Logger.getLogger("Chart Logger");
		
		sqlLogger.setUseParentHandlers(false);
		crtLogger.setUseParentHandlers(false);
		
		Handler handler = new ConsoleHandler();
		sqlLogger.addHandler(handler);
		crtLogger.addHandler(handler);
		
		Query query = new Query(Statements.FRAGE_1_B);
		LineChart chart = new LineChart(query, "Frage1b", "x", "y");
		chart.save();
		query.close();
	}
}
