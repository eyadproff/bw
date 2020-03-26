package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

public enum LatentJobStatus
{
	NEW("N"),
	IN_PROGRESS("W"),
	COMPLETED("C");
	
	private final String code;
	
	LatentJobStatus(String code)
	{
		this.code = code;
	}
	
	public String getCode(){return code;}
	
	public static LatentJobStatus findByCode(String code)
	{
		for(var status : values())
		{
			if(status.getCode().equalsIgnoreCase(code)) return status;
		}
		
		return null;
	}
}