package birdflu;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class FluFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
        String time = ZonedDateTime.ofInstant(record.getInstant(),
        		ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern
        		("dd.MM.yy HH:mm:ss"));
        String source = record.getLoggerName();
        if (record.getSourceClassName() != null) {
            source += " @ " + record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
               source += "." + record.getSourceMethodName();
            }
        }
        String message = formatMessage(record);
        return String.format("%s: %s%n%s [%s]%n%n", record.getLevel().getName(),
        		message, time, source);
	}
}
