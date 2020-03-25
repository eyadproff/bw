package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

public enum Decision
{
	ASSOCIATE_LATENT(1),
	COMPLETE_WITHOUT_ASSOCIATING_LATENT(2),
	VIEW_ONLY(3);
	
	private final int code;
	
	Decision(int code)
	{
		this.code = code;
	}
	
	public int getCode(){return code;}
	
	public static Decision findByCode(int code)
	{
		for(var decision : values())
		{
			if(code == decision.getCode()) return decision;
		}
		
		return null;
	}
}