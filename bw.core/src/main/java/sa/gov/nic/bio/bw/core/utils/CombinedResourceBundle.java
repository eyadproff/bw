package sa.gov.nic.bio.bw.core.utils;

import sa.gov.nic.bio.bw.core.workflow.ResourceBundleProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class CombinedResourceBundle extends ResourceBundle
{
	private Map<String, String> combinedResources = new HashMap<>();
	private Collection<ResourceBundleProvider> resourceBundleProviders;
	private Locale locale;
	
	public CombinedResourceBundle(Collection<ResourceBundleProvider> resourceBundleProviders, Locale locale)
	{
		this.resourceBundleProviders = resourceBundleProviders;
		this.locale = locale;
	}
	
	public void load()
	{
		resourceBundleProviders.forEach(resourceBundleProvider ->
		{
			ResourceBundle bundle = resourceBundleProvider.getErrorsResourceBundle(locale);
			if(bundle == null) return;
			
			Enumeration<String> keysEnumeration = bundle.getKeys();
			ArrayList<String> keysList = Collections.list(keysEnumeration);
			keysList.forEach(key -> combinedResources.put(key, bundle.getString(key)));
		});
	}
	
	@Override
	public Object handleGetObject(String key)
	{
		return combinedResources.get(key);
	}
	
	@Override
	public Enumeration<String> getKeys()
	{
		return Collections.enumeration(combinedResources.keySet());
	}
}