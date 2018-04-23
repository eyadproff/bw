package sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice;

import java.util.Date;
import java.util.Objects;

public class PersonInfo
{
	private Name name;
	private Date birthDate;
	private String birthPlace;
	private String personType;
	private int nationality;
	private String nationalityDesc;
	private int gender;
	private long samisId;
	private String face;
	private String occupation;
	private int idIssueDate;
	private int idExpiryDate;
	
	public Name getName(){return name;}
	public void setName(Name name){this.name = name;}
	
	public Date getBirthDate(){return birthDate;}
	public void setBirthDate(Date birthDate){this.birthDate = birthDate;}
	
	public String getBirthPlace(){return birthPlace;}
	public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}
	
	public String getPersonType(){return personType;}
	public void setPersonType(String personType){this.personType = personType;}
	
	public int getNationality(){return nationality;}
	public void setNationality(int nationality){this.nationality = nationality;}
	
	public String getNationalityDesc(){return nationalityDesc;}
	public void setNationalityDesc(String nationalityDesc){this.nationalityDesc = nationalityDesc;}
	
	public int getGender(){return gender;}
	public void setGender(int gender){this.gender = gender;}
	
	public long getSamisId(){return samisId;}
	public void setSamisId(long samisId){this.samisId = samisId;}
	
	public String getFace(){return face;}
	public void setFace(String face){this.face = face;}
	
	public String getOccupation(){return occupation;}
	public void setOccupation(String occupation){this.occupation = occupation;}
	
	public int getIdIssueDate(){return idIssueDate;}
	public void setIdIssueDate(int idIssueDate){this.idIssueDate = idIssueDate;}
	
	public int getIdExpiryDate(){return idExpiryDate;}
	public void setIdExpiryDate(int idExpiryDate){this.idExpiryDate = idExpiryDate;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PersonInfo that = (PersonInfo) o;
		return nationality == that.nationality && gender == that.gender && samisId == that.samisId &&
			   idIssueDate == that.idIssueDate && idExpiryDate == that.idExpiryDate &&
			   Objects.equals(name, that.name) && Objects.equals(birthDate, that.birthDate) &&
			   Objects.equals(birthPlace, that.birthPlace) && Objects.equals(personType, that.personType) &&
			   Objects.equals(nationalityDesc, that.nationalityDesc) && Objects.equals(face, that.face) &&
			   Objects.equals(occupation, that.occupation);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(name, birthDate, birthPlace, personType, nationality, nationalityDesc, gender, samisId,
		                    face, occupation, idIssueDate, idExpiryDate);
	}
	
	@Override
	public String toString()
	{
		return "PersonInfo{" + "name=" + name + ", birthDate=" + birthDate + ", birthPlace='" + birthPlace + '\'' +
			   ", personType='" + personType + '\'' + ", nationality=" + nationality + ", nationalityDesc='" +
			   nationalityDesc + '\'' + ", gender=" + gender + ", samisId=" + samisId + ", face='" + face + '\'' +
			   ", occupation='" + occupation + '\'' + ", idIssueDate=" + idIssueDate + ", idExpiryDate=" +
			   idExpiryDate + '}';
	}
}