package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import java.util.Date;
import java.util.Objects;

public class PersonIdInfo
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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PersonIdInfo that = (PersonIdInfo) o;
		return versionNumber == that.versionNumber && Objects.equals(idNumber, that.idNumber) &&
			   Objects.equals(idType, that.idType) && Objects.equals(idIssueDate, that.idIssueDate) &&
			   Objects.equals(idExpirDate, that.idExpirDate) && Objects.equals(occupation, that.occupation);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(idNumber, idType, idIssueDate, idExpirDate, occupation, versionNumber);
	}
	
	@Override
	public String toString()
	{
		return "PersonIdInfo{" + "idNumber='" + idNumber + '\'' + ", idType='" + idType + '\'' + ", idIssueDate=" +
			   idIssueDate + ", idExpirDate=" + idExpirDate + ", occupation='" + occupation + '\'' +
			   ", versionNumber=" + versionNumber + '}';
	}
}