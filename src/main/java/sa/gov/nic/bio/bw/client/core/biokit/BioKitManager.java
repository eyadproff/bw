package sa.gov.nic.bio.bw.client.core.biokit;

public class BioKitManager
{
	private TempService proxy;
	
	
	
	public static TempService getFingerprintDeviceService()
	{
		return new TempService(){};
	}
}