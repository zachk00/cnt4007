package torent.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class pFormatter extends Formatter{

    @Override
    public String format(LogRecord record) {
        return new Date(record.getMillis()) + ": " + record.getMessage() + "\n";
    }
     
}
