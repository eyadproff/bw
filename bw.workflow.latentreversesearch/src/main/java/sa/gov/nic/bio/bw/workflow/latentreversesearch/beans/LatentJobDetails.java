package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class LatentJobDetails extends JavaBean
{
	private LatentHitsDetails latentHitsDetails;
	private Long linkedLatentHit;
	private List<DecisionHistory> decisionHistoryList;
	private LatentJob latentJob;
	
	public LatentHitsDetails getLatentHitsDetails(){return latentHitsDetails;}
	public void setLatentHitsDetails(LatentHitsDetails latentHitsDetails){this.latentHitsDetails = latentHitsDetails;}
	
	public Long getLinkedLatentHit(){return linkedLatentHit;}
	public void setLinkedLatentHit(Long linkedLatentHit){this.linkedLatentHit = linkedLatentHit;}
	
	public List<DecisionHistory> getDecisionHistoryList(){return decisionHistoryList;}
	public void setDecisionHistoryList(List<DecisionHistory> decisionHistoryList){this.decisionHistoryList = decisionHistoryList;}
	
	public LatentJob getLatentJob(){return latentJob;}
	public void setLatentJob(LatentJob latentJob){this.latentJob = latentJob;}
}