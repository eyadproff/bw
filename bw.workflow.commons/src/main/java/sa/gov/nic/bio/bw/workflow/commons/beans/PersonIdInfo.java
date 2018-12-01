package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.Date;

public class PersonIdInfo extends JavaBean
{
	private String idNumber;
	private Integer idType;
	private Date idIssueDate;
	private Date idExpirDate;
	private String occupation;
	private int versionNumber;
	
	public String getIdNumber(){return idNumber;}
	public void setIdNumber(String idNumber){this.idNumber = idNumber;}
	
	public Integer getIdType(){return idType;}
	public void setIdType(Integer idType){this.idType = idType;}
	
	public Date getIdIssueDate(){return idIssueDate;}
	public void setIdIssueDate(Date idIssueDate){this.idIssueDate = idIssueDate;}
	
	public Date getIdExpirDate(){return idExpirDate;}
	public void setIdExpirDate(Date idExpirDate){this.idExpirDate = idExpirDate;}
	
	public String getOccupation(){return occupation;}
	public void setOccupation(String occupation){this.occupation = occupation;}
	
	public int getVersionNumber(){return versionNumber;}
	public void setVersionNumber(int versionNumber){this.versionNumber = versionNumber;}
}