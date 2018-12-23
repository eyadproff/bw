package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class DisJudgementInfo extends JavaBean
{
	private String judgementNumber;
	private String judgementPlace;
	private Long judgmentDate;
	private String judgementDesc;
	private Long arrestDate;
	private String policeFileNumber;
	private Long prisonerNumber;
	private Integer caseType;
	
	public String getJudgementNumber(){return judgementNumber;}
	public void setJudgementNumber(String judgementNumber){this.judgementNumber = judgementNumber;}
	
	public String getJudgementPlace(){return judgementPlace;}
	public void setJudgementPlace(String judgementPlace){this.judgementPlace = judgementPlace;}
	
	public Long getJudgmentDate(){return judgmentDate;}
	public void setJudgmentDate(Long judgmentDate){this.judgmentDate = judgmentDate;}
	
	public String getJudgementDesc(){return judgementDesc;}
	public void setJudgementDesc(String judgementDesc){this.judgementDesc = judgementDesc;}
	
	public Long getArrestDate(){return arrestDate;}
	public void setArrestDate(Long arrestDate){this.arrestDate = arrestDate;}
	
	public String getPoliceFileNumber(){return policeFileNumber;}
	public void setPoliceFileNumber(String policeFileNumber){this.policeFileNumber = policeFileNumber;}
	
	public Long getPrisonerNumber(){return prisonerNumber;}
	public void setPrisonerNumber(Long prisonerNumber){this.prisonerNumber = prisonerNumber;}
	
	public Integer getCaseType(){return caseType;}
	public void setCaseType(Integer caseType){this.caseType = caseType;}
}