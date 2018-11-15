package sa.gov.nic.bio.bw.commons.resources;

import java.io.InputStream;
import java.net.URL;

public interface JavaResource
{
	String getFileName();
	
	default URL getAsUrl()
	{
		return getClass().getResource(getFileName());
	}
	
	default InputStream getAsInputStream()
	{
		return getClass().getResourceAsStream(getFileName());
	}
}