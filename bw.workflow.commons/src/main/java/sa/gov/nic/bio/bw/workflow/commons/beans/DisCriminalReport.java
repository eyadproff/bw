package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;

import java.util.List;

public class DisCriminalReport extends JavaBean
{
	private Long generalFileNumber;
	private Name subjtName;
	private Integer subjNationalityCode;
	private String subjOccupation;
	private String subjGender; //'M' or 'F'
	private Long subjBirthDate;
	private String subjBirthPlace;
	private Long subjSamisId;
	private Integer subjSamisType;
	private String subjDocId;
	private Integer subjDocType;
	private Long subjDocIssDate;
	private String subjDocIdIssuePlace;
	private String subjDescription;
	private List<CrimeCode> disCrimeCodes;
	private DisClearanceInfo disClearanceInfo;
	private DisJudgementInfo disJudgementInfo;
	
	public Long getGeneralFileNumber(){return generalFileNumber;}
	public void setGeneralFileNumber(Long generalFileNumber){this.generalFileNumber = generalFileNumber;}
	
	public Name getSubjtName(){return subjtName;}
	public void setSubjtName(Name subjtName){this.subjtName = subjtName;}
	
	public Integer getSubjNationalityCode(){return subjNationalityCode;}
	public void setSubjNationalityCode(Integer subjNationalityCode){this.subjNationalityCode = subjNationalityCode;}
	
	public String getSubjOccupation(){return subjOccupation;}
	public void setSubjOccupation(String subjOccupation){this.subjOccupation = subjOccupation;}
	
	public String getSubjGender(){return subjGender;}
	public void setSubjGender(String subjGender){this.subjGender = subjGender;}
	
	public Long getSubjBirthDate(){return subjBirthDate;}
	public void setSubjBirthDate(Long subjBirthDate){this.subjBirthDate = subjBirthDate;}
	
	public String getSubjBirthPlace(){return subjBirthPlace;}
	public void setSubjBirthPlace(String subjBirthPlace){this.subjBirthPlace = subjBirthPlace;}
	
	public Long getSubjSamisId(){return subjSamisId;}
	public void setSubjSamisId(Long subjSamisId){this.subjSamisId = subjSamisId;}
	
	public Integer getSubjSamisType(){return subjSamisType;}
	public void setSubjSamisType(Integer subjSamisType){this.subjSamisType = subjSamisType;}
	
	public String getSubjDocId(){return subjDocId;}
	public void setSubjDocId(String subjDocId){this.subjDocId = subjDocId;}
	
	public Integer getSubjDocType(){return subjDocType;}
	public void setSubjDocType(Integer subjDocType){this.subjDocType = subjDocType;}
	
	public Long getSubjDocIssDate(){return subjDocIssDate;}
	public void setSubjDocIssDate(Long subjDocIssDate){this.subjDocIssDate = subjDocIssDate;}
	
	public String getSubjDocIdIssuePlace(){return subjDocIdIssuePlace;}
	public void setSubjDocIdIssuePlace(String subjDocIdIssuePlace){this.subjDocIdIssuePlace = subjDocIdIssuePlace;}
	
	public String getSubjDescription(){return subjDescription;}
	public void setSubjDescription(String subjDescription){this.subjDescription = subjDescription;}
	
	public List<CrimeCode> getDisCrimeCodes(){return disCrimeCodes;}
	public void setDisCrimeCodes(List<CrimeCode> disCrimeCodes){this.disCrimeCodes = disCrimeCodes;}
	
	public DisClearanceInfo getDisClearanceInfo(){return disClearanceInfo;}
	public void setDisClearanceInfo(DisClearanceInfo disClearanceInfo){this.disClearanceInfo = disClearanceInfo;}
	
	public DisJudgementInfo getDisJudgementInfo(){return disJudgementInfo;}
	public void setDisJudgementInfo(DisJudgementInfo disJudgementInfo){this.disJudgementInfo = disJudgementInfo;}
}