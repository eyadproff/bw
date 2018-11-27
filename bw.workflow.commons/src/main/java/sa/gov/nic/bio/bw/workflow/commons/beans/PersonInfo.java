package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;

import java.util.Date;

public class PersonInfo extends JavaBean
{
	private long samisId;
	private Name name;
	private Date birthDate;
	private String birthPlace;
	private String birthHijGregInd;
	private String personType;
	private int nationality;
	private String nationalityDesc;
	private int gender;
	private PersonIdInfo identityInfo;
	private String face;
	private Boolean isOut;
	
	public long getSamisId(){return samisId;}
	public void setSamisId(long samisId){this.samisId = samisId;}
	
	public Name getName(){return name;}
	public void setName(Name name){this.name = name;}
	
	public Date getBirthDate(){return birthDate;}
	public void setBirthDate(Date birthDate){this.birthDate = birthDate;}
	
	public String getBirthPlace(){return birthPlace;}
	public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}
	
	public String getBirthHijGregInd(){return birthHijGregInd;}
	public void setBirthHijGregInd(String birthHijGregInd){this.birthHijGregInd = birthHijGregInd;}
	
	public String getPersonType(){return personType;}
	public void setPersonType(String personType){this.personType = personType;}
	
	public int getNationality(){return nationality;}
	public void setNationality(int nationality){this.nationality = nationality;}
	
	public String getNationalityDesc(){return nationalityDesc;}
	public void setNationalityDesc(String nationalityDesc){this.nationalityDesc = nationalityDesc;}
	
	public int getGender(){return gender;}
	public void setGender(int gender){this.gender = gender;}
	
	public PersonIdInfo getIdentityInfo(){return identityInfo;}
	public void setIdentityInfo(PersonIdInfo identityInfo){this.identityInfo = identityInfo;}
	
	public String getFace(){return face;}
	public void setFace(String face){this.face = face;}
	
	public Boolean isOut(){return isOut;}
	public void setIsOut(Boolean isOut){this.isOut = isOut;}
}