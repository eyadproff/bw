package sa.gov.nic.bio.bw.core.workflow;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleProvider
{
	ResourceBundle getStringsResourceBundle(Locale locale);
	ResourceBundle getErrorsResourceBundle(Locale locale);
}