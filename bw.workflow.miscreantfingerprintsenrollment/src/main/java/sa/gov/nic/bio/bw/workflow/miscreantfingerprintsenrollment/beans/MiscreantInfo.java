package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;

public class MiscreantInfo extends JavaBean
{
	private Name name;
	private Long birthDate;
	private String birthPlace;
	private Integer nationality;
	private Integer gender;
	private String occupation;
	
	public Name getName(){return name;}
	public void setName(Name name){this.name = name;}
	
	public Long getBirthDate(){return birthDate;}
	public void setBirthDate(Long birthDate){this.birthDate = birthDate;}
	
	public String getBirthPlace(){return birthPlace;}
	public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}
	
	public Integer getNationality(){return nationality;}
	public void setNationality(Integer nationality){this.nationality = nationality;}
	
	public Integer getGender(){return gender;}
	public void setGender(Integer gender){this.gender = gender;}
	
	public String getOccupation(){return occupation;}
	public void setOccupation(String occupation){this.occupation = occupation;}
}