package sa.gov.nic.bio.bw.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class ConfigManager
{
	private static final String CONFIG_FILE_PATH = "/sa/gov/nic/bio/bw/core/config/config.properties";
	private Properties properties = new Properties();
	
	public void load() throws IOException
	{
		InputStream is = ConfigManager.class.getResourceAsStream(CONFIG_FILE_PATH);
		InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		properties.load(isr);
	}
	
	public void addProperties(Map<String, String> properties)
	{
		this.properties.putAll(properties);
	}
	
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}
}