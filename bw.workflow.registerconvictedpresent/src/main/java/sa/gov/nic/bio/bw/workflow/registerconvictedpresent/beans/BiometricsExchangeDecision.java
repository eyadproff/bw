package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class BiometricsExchangeDecision extends JavaBean
{
	private int partyCode;
	private boolean systemDecision;
	private boolean operatorDecision;
	
	public BiometricsExchangeDecision(int partyCode, boolean systemDecision, boolean operatorDecision)
	{
		this.partyCode = partyCode;
		this.systemDecision = systemDecision;
		this.operatorDecision = operatorDecision;
	}
	
	public int getPartyCode(){return partyCode;}
	public void setPartyCode(int partyCode){this.partyCode = partyCode;}
	
	public boolean getSystemDecision(){return systemDecision;}
	public void setSystemDecision(boolean systemDecision){this.systemDecision = systemDecision;}
	
	public boolean getOperatorDecision(){return operatorDecision;}
	public void setOperatorDecision(boolean operatorDecision){this.operatorDecision = operatorDecision;}
}