package sa.gov.nic.bio.bw.client.features.commons.webservice;

import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.PersonIdInfo;

import java.util.Date;
import java.util.Objects;

public class PersonInfo
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
	private Boolean isOutKingdom;
	
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
	
	public Boolean isOutKingdom(){return isOutKingdom;}
	public void setOutKingdom(Boolean outKingdom){this.isOutKingdom = outKingdom;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PersonInfo that = (PersonInfo) o;
		return samisId == that.samisId && nationality == that.nationality && gender == that.gender &&
			   Objects.equals(name, that.name) && Objects.equals(birthDate, that.birthDate) &&
			   Objects.equals(birthPlace, that.birthPlace) && Objects.equals(birthHijGregInd, that.birthHijGregInd) &&
			   Objects.equals(personType, that.personType) && Objects.equals(nationalityDesc, that.nationalityDesc) &&
			   Objects.equals(identityInfo, that.identityInfo) && Objects.equals(face, that.face) &&
			   Objects.equals(isOutKingdom, that.isOutKingdom);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(samisId, name, birthDate, birthPlace, birthHijGregInd, personType, nationality,
		                    nationalityDesc, gender, identityInfo, face, isOutKingdom);
	}
	
	@Override
	public String toString()
	{
		return "PersonInfo{" + "samisId=" + samisId + ", name=" + name + ", birthDate=" + birthDate +
			   ", birthPlace='" + birthPlace + '\'' + ", birthHijGregInd='" + birthHijGregInd + '\'' +
			   ", personType='" + personType + '\'' + ", nationality=" + nationality + ", nationalityDesc='" +
			   nationalityDesc + '\'' + ", gender=" + gender + ", identityInfo=" + identityInfo + ", face='" +
			   face + '\'' + ", isOutKingdom=" + isOutKingdom + '}';
	}
}