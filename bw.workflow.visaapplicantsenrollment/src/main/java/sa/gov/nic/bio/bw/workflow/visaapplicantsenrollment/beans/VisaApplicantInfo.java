package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans;

import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public class VisaApplicantInfo extends JavaBean
{
	private Long applicantId;
	private ZonedDateTime enrollmentDate;
	private String firstName;
	private String secondName;
	private String otherName;
	private String familyName;
	private Country nationality;
	private LocalDate birthDate;
	private String passportNumber;
	private Gender gender;
	private VisaTypeBean visaType;
	private LocalDate issueDate;
	private Country issuanceCountry;
	private LocalDate expirationDate;
	private Country birthPlace;
	private PassportTypeBean passportType;
	private String mobileNumber;
	private String facePhoto;
	private List<Finger> fingerprints;
	private List<Integer> missingFingerprints;
	
	public VisaApplicantInfo(Long applicantId, ZonedDateTime enrollmentDate, String firstName, String secondName,
	                         String otherName, String familyName, Country nationality, LocalDate birthDate,
	                         String passportNumber, Gender gender, VisaTypeBean visaType, LocalDate issueDate,
	                         Country issuanceCountry, LocalDate expirationDate, Country birthPlace,
	                         PassportTypeBean passportType, String mobileNumber, String facePhoto,
	                         List<Finger> fingerprints, List<Integer> missingFingerprints)
	{
		this.applicantId = applicantId;
		this.enrollmentDate = enrollmentDate;
		this.firstName = firstName;
		this.secondName = secondName;
		this.otherName = otherName;
		this.familyName = familyName;
		this.nationality = nationality;
		this.birthDate = birthDate;
		this.passportNumber = passportNumber;
		this.gender = gender;
		this.visaType = visaType;
		this.issueDate = issueDate;
		this.issuanceCountry = issuanceCountry;
		this.expirationDate = expirationDate;
		this.birthPlace = birthPlace;
		this.passportType = passportType;
		this.mobileNumber = mobileNumber;
		this.facePhoto = facePhoto;
		this.fingerprints = fingerprints;
		this.missingFingerprints = missingFingerprints;
	}
	
	public Long getApplicantId(){return applicantId;}
	public void setApplicantId(Long applicantId){this.applicantId = applicantId;}
	
	public ZonedDateTime getEnrollmentDate(){return enrollmentDate;}
	public void setEnrollmentDate(ZonedDateTime enrollmentDate){this.enrollmentDate = enrollmentDate;}
	
	public String getFirstName(){return firstName;}
	public void setFirstName(String firstName){this.firstName = firstName;}
	
	public String getSecondName(){return secondName;}
	public void setSecondName(String secondName){this.secondName = secondName;}
	
	public String getOtherName(){return otherName;}
	public void setOtherName(String otherName){this.otherName = otherName;}
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	public Country getNationality(){return nationality;}
	public void setNationality(Country nationality){this.nationality = nationality;}
	
	public LocalDate getBirthDate(){return birthDate;}
	public void setBirthDate(LocalDate birthDate){this.birthDate = birthDate;}
	
	public String getPassportNumber(){return passportNumber;}
	public void setPassportNumber(String passportNumber){this.passportNumber = passportNumber;}
	
	public Gender getGender(){return gender;}
	public void setGender(Gender gender){this.gender = gender;}
	
	public VisaTypeBean getVisaType(){return visaType;}
	public void setVisaType(VisaTypeBean visaType){this.visaType = visaType;}
	
	public LocalDate getIssueDate(){return issueDate;}
	public void setIssueDate(LocalDate issueDate){this.issueDate = issueDate;}
	
	public Country getIssuanceCountry(){return issuanceCountry;}
	public void setIssuanceCountry(Country issuanceCountry){this.issuanceCountry = issuanceCountry;}
	
	public LocalDate getExpirationDate(){return expirationDate;}
	public void setExpirationDate(LocalDate expirationDate){this.expirationDate = expirationDate;}
	
	public Country getBirthPlace(){return birthPlace;}
	public void setBirthPlace(Country birthPlace){this.birthPlace = birthPlace;}
	
	public PassportTypeBean getPassportType(){return passportType;}
	public void setPassportType(PassportTypeBean passportType){this.passportType = passportType;}
	
	public String getMobileNumber(){return mobileNumber;}
	public void setMobileNumber(String mobileNumber){this.mobileNumber = mobileNumber;}
	
	public String getFacePhoto(){return facePhoto;}
	public void setFacePhoto(String facePhoto){this.facePhoto = facePhoto;}
	
	public List<Finger> getFingerprints(){return fingerprints;}
	public void setFingerprints(List<Finger> fingerprints){this.fingerprints = fingerprints;}
	
	public List<Integer> getMissingFingerprints(){return missingFingerprints;}
	public void setMissingFingerprints(List<Integer> missingFingerprints)
																	{this.missingFingerprints = missingFingerprints;}
}