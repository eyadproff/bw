package sa.gov.nic.bio.bw.features.commons.utils;

import sa.gov.nic.bio.bw.core.workflow.ResourceBundleProvider;

import java.util.Locale;
import java.util.ResourceBundle;

public class CommonsResourceBundleProvider implements ResourceBundleProvider
{
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName().substring(0,
                                      getClass().getPackageName().lastIndexOf('.')) + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName().substring(0,
                                      getClass().getPackageName().lastIndexOf('.')) + ".bundles.errors", locale);
	}
}
