package sa.gov.nic.bio.bw.client.core.utils;

/*import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;*/

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public final class AppUtils
{
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
	
	public static List<String> listResourceFiles(String base, String endsWith) throws IOException, URISyntaxException
	{
		base = base.replace('.', '/');
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		URI uri = getResourceURL(base).toURI();
		FileSystems.newFileSystem(uri, env);
		Path basePath = Paths.get(uri);
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*" + endsWith);
		BiPredicate<Path, BasicFileAttributes> matcher = (p, bfa) -> bfa.isRegularFile() && pathMatcher.matches(p);
		return Files.find(basePath, 999, matcher)
					.map(Path::toString)
					.map(s -> s.substring(1)) // remove leading slashes
					.collect(Collectors.toList());
	}
}