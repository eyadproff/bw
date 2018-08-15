package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.beans;

import java.util.Map;
import java.util.Objects;

public class FingerprintCardIdentificationRecordReport
{
	private String faceBase64;
	private String firstName;
	private String fatherName;
	private String grandfatherName;
	private String familyName;
	private String gender;
	private String nationality;
	private String occupation;
	private String birthPlace;
	private String birthDate;
	private String idNumber;
	private String idType;
	private String idIssuanceDate;
	private String idExpirationDate;
	private Map<Integer, String> fingerprintsImages;
	
	public FingerprintCardIdentificationRecordReport(String faceBase64, String firstName, String fatherName,
	                                                 String grandfatherName, String familyName, String gender,
	                                                 String nationality, String occupation, String birthPlace,
	                                                 String birthDate, String idNumber, String idType,
	                                                 String idIssuanceDate, String idExpirationDate,
	                                                 Map<Integer, String> fingerprintsImages)
	{
		this.faceBase64 = faceBase64;
		this.firstName = firstName;
		this.fatherName = fatherName;
		this.grandfatherName = grandfatherName;
		this.familyName = familyName;
		this.gender = gender;
		this.nationality = nationality;
		this.occupation = occupation;
		this.birthPlace = birthPlace;
		this.birthDate = birthDate;
		this.idNumber = idNumber;
		this.idType = idType;
		this.idIssuanceDate = idIssuanceDate;
		this.idExpirationDate = idExpirationDate;
		this.fingerprintsImages = fingerprintsImages;
	}
	
	public String getFaceBase64(){return faceBase64;}
	public void setFaceBase64(String faceBase64){this.faceBase64 = faceBase64;}
	
	public String getFirstName(){return firstName;}
	public void setFirstName(String firstName){this.firstName = firstName;}
	
	public String getFatherName(){return fatherName;}
	public void setFatherName(String fatherName){this.fatherName = fatherName;}
	
	public String getGrandfatherName(){return grandfatherName;}
	public void setGrandfatherName(String grandfatherName){this.grandfatherName = grandfatherName;}
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	public String getGender(){return gender;}
	public void setGender(String gender){this.gender = gender;}
	
	public String getNationality(){return nationality;}
	public void setNationality(String nationality){this.nationality = nationality;}
	
	public String getOccupation(){return occupation;}
	public void setOccupation(String occupation){this.occupation = occupation;}
	
	public String getBirthPlace(){return birthPlace;}
	public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}
	
	public String getBirthDate(){return birthDate;}
	public void setBirthDate(String birthDate){this.birthDate = birthDate;}
	
	public String getIdNumber(){return idNumber;}
	public void setIdNumber(String idNumber){this.idNumber = idNumber;}
	
	public String getIdType(){return idType;}
	public void setIdType(String idType){this.idType = idType;}
	
	public String getIdIssuanceDate(){return idIssuanceDate;}
	public void setIdIssuanceDate(String idIssuanceDate){this.idIssuanceDate = idIssuanceDate;}
	
	public String getIdExpirationDate(){return idExpirationDate;}
	public void setIdExpirationDate(String idExpirationDate){this.idExpirationDate = idExpirationDate;}
	
	public Map<Integer, String> getFingerprintsImages(){return fingerprintsImages;}
	public void setFingerprintsImages(Map<Integer, String> fingerprintsImages)
																		{this.fingerprintsImages = fingerprintsImages;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerprintCardIdentificationRecordReport that = (FingerprintCardIdentificationRecordReport) o;
		return Objects.equals(faceBase64, that.faceBase64) && Objects.equals(firstName, that.firstName) &&
			   Objects.equals(fatherName, that.fatherName) && Objects.equals(grandfatherName, that.grandfatherName) &&
			   Objects.equals(familyName, that.familyName) && Objects.equals(gender, that.gender) &&
			   Objects.equals(nationality, that.nationality) && Objects.equals(occupation, that.occupation) &&
			   Objects.equals(birthPlace, that.birthPlace) && Objects.equals(birthDate, that.birthDate) &&
			   Objects.equals(idNumber, that.idNumber) && Objects.equals(idType, that.idType) &&
			   Objects.equals(idIssuanceDate, that.idIssuanceDate) &&
			   Objects.equals(idExpirationDate, that.idExpirationDate) &&
			   Objects.equals(fingerprintsImages, that.fingerprintsImages);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(faceBase64, firstName, fatherName, grandfatherName, familyName, gender, nationality,
		                    occupation, birthPlace, birthDate, idNumber, idType, idIssuanceDate, idExpirationDate,
		                    fingerprintsImages);
	}
	
	@Override
	public String toString()
	{
		return "DeadPersonRecordReport{" + "faceBase64='" + faceBase64 + '\'' + ", firstName='" + firstName + '\'' +
			   ", fatherName='" + fatherName + '\'' + ", grandfatherName='" + grandfatherName + '\'' +
			   ", familyName='" + familyName + '\'' + ", gender='" + gender + '\'' + ", nationality='" + nationality +
			   '\'' + ", occupation='" + occupation + '\'' + ", birthPlace='" + birthPlace + '\'' + ", birthDate='" +
			   birthDate + '\'' + ", idNumber='" + idNumber + '\'' + ", idType='" + idType + '\'' +
			   ", idIssuanceDate='" + idIssuanceDate + '\'' + ", idExpirationDate='" + idExpirationDate + '\'' +
			   ", fingerprintsImages=" + fingerprintsImages + '}';
	}
}