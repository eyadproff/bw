package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by Fouad on 18-Jul-17.
 */
public class ConfigManager
{
	private static final String CONFIG_FILE_PATH = "sa/gov/nic/bio/bw/client/core/config/config.properties";
	private Properties properties = new Properties();
	
	public void load() throws IOException
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_PATH);
		InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		properties.load(isr);
	}
	
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}
}