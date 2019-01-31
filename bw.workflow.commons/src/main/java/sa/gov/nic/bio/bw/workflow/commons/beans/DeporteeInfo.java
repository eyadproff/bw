package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;

public class DeporteeInfo extends JavaBean
{
	private Long samisId;
	private Name name;
	private Long birthDate;
	private Integer nationality;
	private Long sponsorId;
	private String gender;
	private String isEnrolled;
	private Long createTimeStamp;
	private Integer lkCreateTerminalLocation;
	private Long generalFileNumber;
	private String ageCategory;
	private Long prisonerNumber;
	private String face;
	
	public Long getSamisId(){return samisId;}
	public void setSamisId(Long samisId){this.samisId = samisId;}
	
	public Name getName(){return name;}
	public void setName(Name name){this.name = name;}
	
	public Long getBirthDate(){return birthDate;}
	public void setBirthDate(Long birthDate){this.birthDate = birthDate;}
	
	public Integer getNationality(){return nationality;}
	public void setNationality(Integer nationality){this.nationality = nationality;}
	
	public Long getSponsorId(){return sponsorId;}
	public void setSponsorId(Long sponsorId){this.sponsorId = sponsorId;}
	
	public String getGender(){return gender;}
	public void setGender(String gender){this.gender = gender;}
	
	public String getIsEnrolled(){return isEnrolled;}
	public void setIsEnrolled(String isEnrolled){this.isEnrolled = isEnrolled;}
	
	public Long getCreateTimeStamp(){return createTimeStamp;}
	public void setCreateTimeStamp(Long createTimeStamp){this.createTimeStamp = createTimeStamp;}
	
	public Integer getLkCreateTerminalLocation(){return lkCreateTerminalLocation;}
	public void setLkCreateTerminalLocation(Integer lkCreateTerminalLocation){this.lkCreateTerminalLocation = lkCreateTerminalLocation;}
	
	public Long getGeneralFileNumber(){return generalFileNumber;}
	public void setGeneralFileNumber(Long generalFileNumber){this.generalFileNumber = generalFileNumber;}
	
	public String getAgeCategory(){return ageCategory;}
	public void setAgeCategory(String ageCategory){this.ageCategory = ageCategory;}
	
	public Long getPrisonerNumber(){return prisonerNumber;}
	public void setPrisonerNumber(Long prisonerNumber){this.prisonerNumber = prisonerNumber;}
	
	public String getFace(){return face;}
	public void setFace(String face){this.face = face;}
}