package sa.gov.nic.bio.bw.client.core.utils;

import java.util.Locale;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter
{
	@Override
	public synchronized String format(LogRecord record)
	{
		// inject thread name
		String threadName = Thread.currentThread().getName();
		String message = super.format(record).replace("{{{", "[" + threadName + "] (").replace("}}}", ")");
		return AppUtils.replaceNumbers(message, Locale.ENGLISH);
	}
}