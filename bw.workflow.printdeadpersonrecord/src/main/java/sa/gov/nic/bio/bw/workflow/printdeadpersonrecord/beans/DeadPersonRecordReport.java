package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans;

import java.util.Map;
import java.util.Objects;

public class DeadPersonRecordReport
{
	private String recordId;
	private String enrollerId;
	private String inquirerId;
	private String enrollmentTime;
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
	private String personId;
	private String personType;
	private String documentId;
	private String documentType;
	private String documentIssuanceDate;
	private String documentExpiryDate;
	private Map<Integer, String> fingerprintsImages;
	
	public DeadPersonRecordReport(String recordId, String enrollerId, String inquirerId, String enrollmentTime,
	                              String faceBase64, String firstName, String fatherName, String grandfatherName,
	                              String familyName, String gender, String nationality, String occupation,
	                              String birthPlace, String birthDate, String personId, String personType,
	                              String documentId, String documentType, String documentIssuanceDate,
	                              String documentExpiryDate, Map<Integer, String> fingerprintsImages)
	{
		this.recordId = recordId;
		this.enrollerId = enrollerId;
		this.inquirerId = inquirerId;
		this.enrollmentTime = enrollmentTime;
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
		this.personId = personId;
		this.personType = personType;
		this.documentId = documentId;
		this.documentType = documentType;
		this.documentIssuanceDate = documentIssuanceDate;
		this.documentExpiryDate = documentExpiryDate;
		this.fingerprintsImages = fingerprintsImages;
	}
	
	public String getRecordId(){return recordId;}
	public void setRecordId(String recordId){this.recordId = recordId;}
	
	public String getEnrollerId(){return enrollerId;}
	public void setEnrollerId(String enrollerId){this.enrollerId = enrollerId;}
	
	public String getInquirerId(){return inquirerId;}
	public void setInquirerId(String inquirerId){this.inquirerId = inquirerId;}
	
	public String getEnrollmentTime(){return enrollmentTime;}
	public void setEnrollmentTime(String enrollmentTime){this.enrollmentTime = enrollmentTime;}
	
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
	
	public String getPersonId(){return personId;}
	public void setPersonId(String personId){this.personId = personId;}
	
	public String getPersonType(){return personType;}
	public void setPersonType(String personType){this.personType = personType;}
	
	public String getDocumentId(){return documentId;}
	public void setDocumentId(String documentId){this.documentId = documentId;}
	
	public String getDocumentType(){return documentType;}
	public void setDocumentType(String documentType){this.documentType = documentType;}
	
	public String getDocumentIssuanceDate(){return documentIssuanceDate;}
	public void setDocumentIssuanceDate(String documentIssuanceDate)
																{this.documentIssuanceDate = documentIssuanceDate;}
	
	public String getDocumentExpiryDate(){return documentExpiryDate;}
	public void setDocumentExpiryDate(String documentExpiryDate)
															{this.documentExpiryDate = documentExpiryDate;}
	
	public Map<Integer, String> getFingerprintsImages(){return fingerprintsImages;}
	public void setFingerprintsImages(Map<Integer, String> fingerprintsImages)
																	{this.fingerprintsImages = fingerprintsImages;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		DeadPersonRecordReport that = (DeadPersonRecordReport) o;
		return Objects.equals(recordId, that.recordId) && Objects.equals(enrollerId, that.enrollerId) &&
			   Objects.equals(inquirerId, that.inquirerId) && Objects.equals(enrollmentTime, that.enrollmentTime) &&
			   Objects.equals(faceBase64, that.faceBase64) && Objects.equals(firstName, that.firstName) &&
			   Objects.equals(fatherName, that.fatherName) && Objects.equals(grandfatherName, that.grandfatherName) &&
			   Objects.equals(familyName, that.familyName) && Objects.equals(gender, that.gender) &&
			   Objects.equals(nationality, that.nationality) && Objects.equals(occupation, that.occupation) &&
			   Objects.equals(birthPlace, that.birthPlace) && Objects.equals(birthDate, that.birthDate) &&
			   Objects.equals(personId, that.personId) && Objects.equals(personType, that.personType) &&
			   Objects.equals(documentId, that.documentId) && Objects.equals(documentType, that.documentType) &&
			   Objects.equals(documentIssuanceDate, that.documentIssuanceDate) &&
			   Objects.equals(documentExpiryDate, that.documentExpiryDate) &&
			   Objects.equals(fingerprintsImages, that.fingerprintsImages);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(recordId, enrollerId, inquirerId, enrollmentTime, faceBase64, firstName, fatherName,
		                    grandfatherName, familyName, gender, nationality, occupation, birthPlace, birthDate,
		                    personId, personType, documentId, documentType, documentIssuanceDate, documentExpiryDate,
		                    fingerprintsImages);
	}
	
	@Override
	public String toString()
	{
		return "DeadPersonRecordReport{" + "recordId='" + recordId + '\'' + ", enrollerId='" + enrollerId + '\'' +
			   ", inquirerId='" + inquirerId + '\'' + ", enrollmentTime='" + enrollmentTime + '\'' + ", faceBase64='" +
			   faceBase64 + '\'' + ", firstName='" + firstName + '\'' + ", fatherName='" + fatherName + '\'' +
			   ", grandfatherName='" + grandfatherName + '\'' + ", familyName='" + familyName + '\'' + ", gender='" +
			   gender + '\'' + ", nationality='" + nationality + '\'' + ", occupation='" + occupation + '\'' +
			   ", birthPlace='" + birthPlace + '\'' + ", birthDate='" + birthDate + '\'' + ", personId='" + personId +
			   '\'' + ", personType='" + personType + '\'' + ", documentId='" + documentId + '\'' +
			   ", documentType='" + documentType + '\'' + ", documentIssuanceDate='" + documentIssuanceDate + '\'' +
			   ", documentExpiryDate='" + documentExpiryDate + '\'' + ", fingerprintsImages=" +
			   fingerprintsImages + '}';
	}
}