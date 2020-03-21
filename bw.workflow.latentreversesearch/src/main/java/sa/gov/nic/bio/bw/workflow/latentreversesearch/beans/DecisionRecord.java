package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class DecisionRecord extends JavaBean
{
	private Decision decision;
	private String latentNumber;
	private Integer fingerPosition;
	private long operatorId;
	private long decisionTimestamp;
	
	public Decision getDecision(){return decision;}
	public void setDecision(Decision decision){this.decision = decision;}
	
	public String getLatentNumber()
	{
		return latentNumber;
	}
	
	public void setLatentNumber(String latentNumber)
	{
		this.latentNumber = latentNumber;
	}
	
	public Integer getFingerPosition()
	{
		return fingerPosition;
	}
	
	public void setFingerPosition(Integer fingerPosition)
	{
		this.fingerPosition = fingerPosition;
	}
	
	public long getOperatorId()
	{
		return operatorId;
	}
	
	public void setOperatorId(long operatorId)
	{
		this.operatorId = operatorId;
	}
	
	public long getDecisionTimestamp()
	{
		return decisionTimestamp;
	}
	
	public void setDecisionTimestamp(long decisionTimestamp)
	{
		this.decisionTimestamp = decisionTimestamp;
	}
}