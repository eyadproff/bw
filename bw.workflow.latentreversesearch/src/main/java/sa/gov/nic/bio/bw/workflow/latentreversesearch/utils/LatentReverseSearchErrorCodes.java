package sa.gov.nic.bio.bw.workflow.latentreversesearch.utils;

public enum LatentReverseSearchErrorCodes
{
	C018_00001, C018_00002;
	
	public final String getCode()
	{
		return name().replace("_", "-");
	}
}