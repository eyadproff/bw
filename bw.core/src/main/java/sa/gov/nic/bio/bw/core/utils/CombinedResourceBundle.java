package sa.gov.nic.bio.bw.core.utils;

import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.bw.core.workflow.ResourceBundleProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CombinedResourceBundle extends ResourceBundle implements AppLogger
{
	private Map<String, ResourceBundleProvider> moduleResourceBundleProviders;
	private Locale locale;
	private Map<String, String> combinedResources = new HashMap<>();
	private String currentResourceBundleProviderModule;
	
	public CombinedResourceBundle(Map<String, ResourceBundleProvider> moduleResourceBundleProviders, Locale locale)
	{
		this.moduleResourceBundleProviders = moduleResourceBundleProviders;
		this.locale = locale;
	}
	
	public void load()
	{
		List<ResourceBundleProvider> resourceBundleProviders = new ArrayList<>(moduleResourceBundleProviders.values());
		resourceBundleProviders.sort(Comparator.comparingInt(ResourceBundleProvider::getPriority));
		resourceBundleProviders.forEach(resourceBundleProvider ->
		{
			ResourceBundle bundle = resourceBundleProvider.getResourceBundle(locale);
			if(bundle == null) return;
			
			Enumeration<String> keysEnumeration = bundle.getKeys();
			ArrayList<String> keysList = Collections.list(keysEnumeration);
			keysList.forEach(key -> combinedResources.putIfAbsent(key, bundle.getString(key)));
		});
	}
	
	public void reload(Locale locale)
	{
		this.locale = locale;
		combinedResources.clear();
		load();
	}
	
	public void setCurrentResourceBundleProviderModule(String currentResourceBundleProviderModule)
	{
		this.currentResourceBundleProviderModule = currentResourceBundleProviderModule;
	}
	
	@Override
	public Object handleGetObject(String key)
	{
		if(key == null)
		{
			LOGGER.warning("key is null!");
			return null;
		}
		
		block: if(currentResourceBundleProviderModule != null)
		{
			ResourceBundleProvider resourceBundleProvider =
												moduleResourceBundleProviders.get(currentResourceBundleProviderModule);
			if(resourceBundleProvider == null) break block;
			
			ResourceBundle bundle = resourceBundleProvider.getResourceBundle(locale);
			if(bundle == null) break block;
			
			try
			{
				return bundle.getString(key);
			}
			catch(MissingResourceException e)
			{
				return combinedResources.get(key);
			}
		}
		
		return combinedResources.get(key);
	}
	
	@Override
	public Enumeration<String> getKeys()
	{
		return Collections.enumeration(combinedResources.keySet());
	}
}