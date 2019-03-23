package sa.gov.nic.bio.bw.core.workflow;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleProvider
{
	ResourceBundle getResourceBundle(Locale locale);
	default int getPriority()
	{
		return 5; // less = higher priority
	}
}