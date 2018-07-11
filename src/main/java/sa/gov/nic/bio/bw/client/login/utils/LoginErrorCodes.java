package sa.gov.nic.bio.bw.client.login.utils;

public enum LoginErrorCodes
{
	C003_00001, C003_00002, C003_00003, C003_00004, C003_00005, C003_00006, C003_00007, C003_00008,
	
	N003_00001;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}