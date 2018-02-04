package sa.gov.nic.bio.bw.client.login.utils;

public enum LoginErrorCodes
{
	C003_00001, C003_00002,
	
	N003_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}