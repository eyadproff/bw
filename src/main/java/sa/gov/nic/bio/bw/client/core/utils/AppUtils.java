package sa.gov.nic.bio.bw.client.core.utils;

/*import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;*/

import java.io.*;
import java.net.*;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class AppUtils
{
	private static final Logger LOGGER = Logger.getLogger(AppUtils.class.getName());
	
	private static final String LOGGING_CONFIG = "sa/gov/nic/bio/bw/client/commons/logging.properties";
	private static final String FONT_AWESOME_FILE = "sa/gov/nic/bio/bw/client/core/fonts/fontawesome-webfont-4.7.0.2016.ttf";
	
	/*private static final FontAwesome FONTAWESOME_INSTANCE = new FontAwesome(AppUtils.getResourceString(FONT_AWESOME_FILE));
	
	public static org.controlsfx.glyphfont.Glyph createFontAwesomeIcon(Glyph icon)
	{
		return FONTAWESOME_INSTANCE.create(icon);
	}*/
	
	public static URL getResourceURL(String resourceName)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResource(resourceName);
	}
	
	public static String getResourceString(String resourceName)
	{
		URL url = getResourceURL(resourceName);
		return url != null ? url.toExternalForm() : null;
	}
	
	public static InputStream getResourceAsStream(String resourceName)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResourceAsStream(resourceName);
	}
	
	public static ResourceBundle getResourceBundle(String resourceBundlePath, GuiLanguage language)
	{
		if(language == null) return ResourceBundle.getBundle(resourceBundlePath, UTF8Control.INSTANCE);
		else return ResourceBundle.getBundle(resourceBundlePath, language.getLocale(), UTF8Control.INSTANCE);
	}
	
	public static String getMachineIpAddress() throws SocketException
	{
		for(NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces()))
		{
			for(InetAddress address : Collections.list(nic.getInetAddresses()))
			{
				if(!address.isLoopbackAddress() && address instanceof Inet4Address)
				{
					return address.getHostAddress();
				}
			}
		}
		
		return null;
	}
	
	public static List<String> listResourceFiles(ProtectionDomain protectionDomain, String endsWith) throws IOException
	{
		List<String> resources = new ArrayList<>();
		
		URL jar = protectionDomain.getCodeSource().getLocation();
		ZipInputStream zip = new ZipInputStream(jar.openStream());
		
		while(true)
		{
			ZipEntry e = zip.getNextEntry();
			if(e == null) break;
			String name = e.getName();
			
			if(name.endsWith(endsWith)) resources.add(name);
		}
		
		return resources;
	}
}