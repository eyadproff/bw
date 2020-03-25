package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class DecisionHistory extends JavaBean
{
	private Long jobId;
	private Long tcn;
	private Long operatorId;
	private Integer decision;
	private Long decisionDate;
	private Long linkedLatentBioId;
	private Long linkedCivilBioID;
	private Integer score;
	private Integer fingerPos;;
	
	public Long getJobId(){return jobId;}
	public void setJobId(Long jobId){this.jobId = jobId;}
	
	public Long getTcn(){return tcn;}
	public void setTcn(Long tcn){this.tcn = tcn;}
	
	public Long getOperatorId(){return operatorId;}
	public void setOperatorId(Long operatorId){this.operatorId = operatorId;}
	
	public Integer getDecision(){return decision;}
	public void setDecision(Integer decision){this.decision = decision;}
	
	public Long getDecisionDate(){return decisionDate;}
	public void setDecisionDate(Long decisionDate){this.decisionDate = decisionDate;}
	
	public Long getLinkedLatentBioId(){return linkedLatentBioId;}
	public void setLinkedLatentBioId(Long linkedLatentBioId){this.linkedLatentBioId = linkedLatentBioId;}
	
	public Long getLinkedCivilBioID(){return linkedCivilBioID;}
	public void setLinkedCivilBioID(Long linkedCivilBioID){this.linkedCivilBioID = linkedCivilBioID;}
	
	public Integer getScore(){return score;}
	public void setScore(Integer score){this.score = score;}
	
	public Integer getFingerPos(){return fingerPos;}
	public void setFingerPos(Integer fingerPos){this.fingerPos = fingerPos;}
}