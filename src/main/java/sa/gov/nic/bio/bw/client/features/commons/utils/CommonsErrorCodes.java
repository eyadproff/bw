package sa.gov.nic.bio.bw.client.features.commons.utils;

public enum CommonsErrorCodes
{
	C008_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}