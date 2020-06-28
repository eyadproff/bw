package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class LatentOperatorDecision extends JavaBean
{
	private Decision decision;
	private long tcn;
	private Long civilBiometricsId;
	private String latentNumber;
	
	public LatentOperatorDecision(Decision decision, long tcn)
	{
		this.decision = decision;
		this.tcn = tcn;
	}
	
	public LatentOperatorDecision(Decision decision, long tcn, Long civilBiometricsId, String latentNumber)
	{
		this.decision = decision;
		this.tcn = tcn;
		this.civilBiometricsId = civilBiometricsId;
		this.latentNumber = latentNumber;
	}
	
	public Decision getDecision(){return decision;}
	public void setDecision(Decision decision){this.decision = decision;}
	
	public long getTcn(){return tcn;}
	public void setTcn(long tcn){this.tcn = tcn;}
	
	public Long getCivilBiometricsId(){return civilBiometricsId;}
	public void setCivilBiometricsId(Long civilBiometricsId){this.civilBiometricsId = civilBiometricsId;}
	
	public String getLatentNumber(){return latentNumber;}
	public void setLatentNumber(String latentNumber){this.latentNumber = latentNumber;}
}
