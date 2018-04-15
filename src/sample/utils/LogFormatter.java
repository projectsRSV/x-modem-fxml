package sample.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.S");

    @Override
    public String format(LogRecord record) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[")
                .append(String.valueOf(dateFormat.format(new Date(record.getMillis()))))
                .append("]")
                .append(String.format("%-30s", record.getLoggerName()))
                .append(record.getLevel().getLocalizedName())
                .append(String.format("%-4s", ": "))
                .append(record.getMessage())
                .append("\n");
        return stringBuilder.toString();
    }
}

