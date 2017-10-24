package sa.gov.nic.bio.bw.client.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends Formatter
{
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss.SSS a", Locale.ENGLISH);
	
	@Override
	public synchronized String format(LogRecord record)
	{
		ZonedDateTime zonedDateTime = AppUtils.milliSecondsToGregorianDataTime(record.getMillis());
		String timestamp = DATE_TIME_FORMATTER.format(zonedDateTime);
		
		String logMessage = timestamp + " - " + "[" + record.getLevel() + "][" +
							Thread.currentThread().getName() + "][" + record.getSourceClassName() +
							"." + record.getSourceMethodName() + "()] " +
							formatMessage(record) + System.lineSeparator();
		
		Throwable thrown = record.getThrown();
		
		if(thrown != null)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			thrown.printStackTrace(pw);
			String sStackTrace = sw.toString();
			logMessage += "~" + sStackTrace + "\n"; // added ~ to know whether the exception is coming through this formatter or not
		}
		
		return logMessage;
	}
}