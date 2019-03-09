package sa.gov.nic.bio.bw.workflow.commons.utils;

import sa.gov.nic.bio.bw.core.workflow.ResourceBundleProvider;

import java.util.Locale;
import java.util.ResourceBundle;


/*
 * DO NOT DELETE THIS CLASS, IT WILL BE PICKED UP BY THE CLASS SCANNER.
 */
public class CommonsResourceBundleProvider implements ResourceBundleProvider
{
	@Override
	public ResourceBundle getResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName().substring(0,
                                      getClass().getPackageName().lastIndexOf('.')) + ".bundles.strings", locale);
	}
	
	@Override
	public int getPriority()
	{
		return 4;
	}
}
