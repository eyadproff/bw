package sa.gov.nic.bio.bw.core.utils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter
{
	@Override
	public boolean isLoggable(LogRecord record)
	{
		return record.getSourceClassName().startsWith("sa.gov.nic.bio.bw") ||
			   record.getSourceClassName().startsWith("java.util.Spliterators"); // for method references
	}
}