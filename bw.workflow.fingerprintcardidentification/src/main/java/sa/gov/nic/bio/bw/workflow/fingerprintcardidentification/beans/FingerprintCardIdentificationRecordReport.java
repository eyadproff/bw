package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.Map;

public class FingerprintCardIdentificationRecordReport extends JavaBean
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
	private String personId;
	private String personType;
	private String civilBiometricsId;
	private String criminalBiometricsId;
	private String documentId;
	private String documentType;
	private String documentIssuanceDate;
	private String documentExpiryDate;
	private Map<Integer, String> fingerprintsImages;
	
	public FingerprintCardIdentificationRecordReport(String faceBase64, String firstName, String fatherName,
	                                                 String grandfatherName, String familyName, String gender,
	                                                 String nationality, String occupation, String birthPlace,
	                                                 String birthDate, String personId, String personType,
	                                                 String civilBiometricsId, String criminalBiometricsId,
	                                                 String documentId, String documentType,
	                                                 String documentIssuanceDate, String documentExpiryDate,
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
		this.personId = personId;
		this.personType = personType;
		this.civilBiometricsId = civilBiometricsId;
		this.criminalBiometricsId = criminalBiometricsId;
		this.documentId = documentId;
		this.documentType = documentType;
		this.documentIssuanceDate = documentIssuanceDate;
		this.documentExpiryDate = documentExpiryDate;
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
	
	public String getPersonId(){return personId;}
	public void setPersonId(String personId){this.personId = personId;}
	
	public String getPersonType(){return personType;}
	public void setPersonType(String personType){this.personType = personType;}
	
	public String getCivilBiometricsId(){return civilBiometricsId;}
	public void setCivilBiometricsId(String civilBiometricsId){this.civilBiometricsId = civilBiometricsId;}
	
	public String getCriminalBiometricsId(){return criminalBiometricsId;}
	public void setCriminalBiometricsId(String criminalBiometricsId){this.criminalBiometricsId = criminalBiometricsId;}
	
	public String getDocumentId(){return documentId;}
	public void setDocumentId(String documentId){this.documentId = documentId;}
	
	public String getDocumentType(){return documentType;}
	public void setDocumentType(String documentType){this.documentType = documentType;}
	
	public String getDocumentIssuanceDate(){return documentIssuanceDate;}
	public void setDocumentIssuanceDate(String documentIssuanceDate){this.documentIssuanceDate = documentIssuanceDate;}
	
	public String getDocumentExpiryDate(){return documentExpiryDate;}
	public void setDocumentExpiryDate(String documentExpiryDate){this.documentExpiryDate = documentExpiryDate;}
	
	public Map<Integer, String> getFingerprintsImages(){return fingerprintsImages;}
	public void setFingerprintsImages(Map<Integer, String> fingerprintsImages)
																		{this.fingerprintsImages = fingerprintsImages;}
}