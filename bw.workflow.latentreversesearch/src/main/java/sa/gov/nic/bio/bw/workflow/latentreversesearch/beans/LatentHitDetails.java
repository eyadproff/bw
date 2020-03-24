package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class LatentHitDetails extends JavaBean
{
	private List<DecisionRecord> decisionRecords;
	private List<Latent> latents;
	private LatentJobStatus status;
	private Long lockedByOperatorId;
	private String associatedLatentNumber;
	private boolean operatorCanOverride;
	
	public List<DecisionRecord> getDecisionRecords(){return decisionRecords;}
	public void setDecisionRecords(List<DecisionRecord> decisionRecords){this.decisionRecords = decisionRecords;}
	
	public List<Latent> getLatents(){return latents;}
	public void setLatents(List<Latent> latents){this.latents = latents;}
	
	public LatentJobStatus getStatus(){return status;}
	public void setStatus(LatentJobStatus status){this.status = status;}
	
	public Long getLockedByOperatorId(){return lockedByOperatorId;}
	public void setLockedByOperatorId(Long lockedByOperatorId){this.lockedByOperatorId = lockedByOperatorId;}
	
	public String getAssociatedLatentNumber(){return associatedLatentNumber;}
	public void setAssociatedLatentNumber(String associatedLatentNumber){this.associatedLatentNumber = associatedLatentNumber;}
	
	public boolean isOperatorCanOverride(){return operatorCanOverride;}
	public void setOperatorCanOverride(boolean operatorCanOverride){this.operatorCanOverride = operatorCanOverride;}
}