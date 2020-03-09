package sa.gov.nic.bio.bw.workflow.citizenenrollment.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonIdInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.WantedInfo;

import java.util.Date;
import java.util.List;

public class PersonInfo extends JavaBean
{
	private Long samisId;
	private Name name;
	private Date birthDate;
	private String birthPlace;
	private String birthHijGregInd;
	private String personType;
	private Integer nationality;
	private String nationalityDesc;
	private Integer gender;
	private PersonIdInfo identityInfo;
	private String face;
	private Boolean isOut;
	private List<WantedInfo> wantedInfos;
	private String isEnrolled;
	private String deathInd;

	public Long getSamisId(){return samisId;}
	public void setSamisId(Long samisId){this.samisId = samisId;}
	
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
	
	public Integer getNationality(){return nationality;}
	public void setNationality(Integer nationality){this.nationality = nationality;}
	
	public String getNationalityDesc(){return nationalityDesc;}
	public void setNationalityDesc(String nationalityDesc){this.nationalityDesc = nationalityDesc;}
	
	public Integer getGender(){return gender;}
	public void setGender(Integer gender){this.gender = gender;}
	
	public PersonIdInfo getIdentityInfo(){return identityInfo;}
	public void setIdentityInfo(PersonIdInfo identityInfo){this.identityInfo = identityInfo;}
	
	public String getFace(){return face;}
	public void setFace(String face){this.face = face;}
	
	public Boolean isOut(){return isOut;}
	public void setIsOut(Boolean isOut){this.isOut = isOut;}
	
	public List<WantedInfo> getWantedInfos(){return wantedInfos;}
	public void setWantedInfos(List<WantedInfo> wantedInfos){this.wantedInfos = wantedInfos;}

	public Boolean getOut() {
		return isOut;
	}

	public void setOut(Boolean out) {
		isOut = out;
	}

	public String getIsEnrolled() {
		return isEnrolled;
	}

	public void setIsEnrolled(String isEnrolled) {
		this.isEnrolled = isEnrolled;
	}

	public String getDeathInd() {
		return deathInd;
	}

	public void setDeathInd(String deathInd) {
		this.deathInd = deathInd;
	}
}