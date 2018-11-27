package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class BiometricsExchangeDecision extends JavaBean
{
	private int bioExchangePartyCode;
	private boolean bioExchangeSystemDecision;
	private boolean bioExchangeOperatorDecision;
	
	public BiometricsExchangeDecision(int bioExchangePartyCode, boolean bioExchangeSystemDecision,
	                                  boolean bioExchangeOperatorDecision)
	{
		this.bioExchangePartyCode = bioExchangePartyCode;
		this.bioExchangeSystemDecision = bioExchangeSystemDecision;
		this.bioExchangeOperatorDecision = bioExchangeOperatorDecision;
	}
	
	public int getBioExchangePartyCode(){return bioExchangePartyCode;}
	public void setBioExchangePartyCode(int bioExchangePartyCode){this.bioExchangePartyCode = bioExchangePartyCode;}
	
	public boolean getBioExchangeSystemDecision(){return bioExchangeSystemDecision;}
	public void setBioExchangeSystemDecision(boolean bioExchangeSystemDecision)
														{this.bioExchangeSystemDecision = bioExchangeSystemDecision;}
	
	public boolean isBioExchangeOperatorDecision(){return bioExchangeOperatorDecision;}
	public void setBioExchangeOperatorDecision(boolean bioExchangeOperatorDecision)
													{this.bioExchangeOperatorDecision = bioExchangeOperatorDecision;}
}