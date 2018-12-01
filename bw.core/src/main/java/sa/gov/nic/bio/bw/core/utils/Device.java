package sa.gov.nic.bio.bw.core.utils;

public enum Device
{
	BIO_UTILITIES,
	FINGERPRINT_SCANNER,
	CAMERA, PASSPORT_SCANNER;
	
	public static Device byName(String name)
	{
		for(Device device : values())
		{
			if(device.name().toLowerCase().equals(name.toLowerCase())) return device;
		}
		
		return null;
	}
}