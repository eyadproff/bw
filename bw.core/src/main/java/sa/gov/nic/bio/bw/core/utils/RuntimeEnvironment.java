package sa.gov.nic.bio.bw.core.utils;

public enum RuntimeEnvironment
{
	PROD, INT, DEV, LOCAL;
	
	public static RuntimeEnvironment byName(String name)
	{
		for(RuntimeEnvironment env : values())
		{
			if(env.name().equalsIgnoreCase(name)) return env;
		}
		
		return null;
	}
}