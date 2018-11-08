package sa.gov.nic.bio.bw.features.visaapplicantsenrollment.webservice;

import sa.gov.nic.bio.bw.features.commons.beans.Gender;
import sa.gov.nic.bio.bw.features.commons.webservice.Country;
import sa.gov.nic.bio.bw.features.commons.webservice.Finger;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class VisaApplicantInfo
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
	private String faceImage;
	private List<Finger> fingerprints;
	private List<Integer> missingFingerprints;
	
	public VisaApplicantInfo(Long applicantId, ZonedDateTime enrollmentDate, String firstName, String secondName,
	                         String otherName, String familyName, Country nationality, LocalDate birthDate,
	                         String passportNumber, Gender gender, VisaTypeBean visaType, LocalDate issueDate,
	                         Country issuanceCountry, LocalDate expirationDate, Country birthPlace,
	                         PassportTypeBean passportType, String mobileNumber, String faceImage,
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
		this.faceImage = faceImage;
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
	
	public String getFaceImage(){return faceImage;}
	public void setFaceImage(String faceImage){this.faceImage = faceImage;}
	
	public List<Finger> getFingerprints(){return fingerprints;}
	public void setFingerprints(List<Finger> fingerprints){this.fingerprints = fingerprints;}
	
	public List<Integer> getMissingFingerprints(){return missingFingerprints;}
	public void setMissingFingerprints(List<Integer> missingFingerprints)
																	{this.missingFingerprints = missingFingerprints;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		VisaApplicantInfo that = (VisaApplicantInfo) o;
		return Objects.equals(applicantId, that.applicantId) && Objects.equals(enrollmentDate, that.enrollmentDate) &&
			   Objects.equals(firstName, that.firstName) && Objects.equals(secondName, that.secondName) &&
			   Objects.equals(otherName, that.otherName) && Objects.equals(familyName, that.familyName) &&
			   Objects.equals(nationality, that.nationality) && Objects.equals(birthDate, that.birthDate) &&
			   Objects.equals(passportNumber, that.passportNumber) && gender == that.gender &&
			   Objects.equals(visaType, that.visaType) && Objects.equals(issueDate, that.issueDate) &&
			   Objects.equals(issuanceCountry, that.issuanceCountry) &&
			   Objects.equals(expirationDate, that.expirationDate) && Objects.equals(birthPlace, that.birthPlace) &&
			   Objects.equals(passportType, that.passportType) && Objects.equals(mobileNumber, that.mobileNumber) &&
			   Objects.equals(faceImage, that.faceImage) && Objects.equals(fingerprints, that.fingerprints) &&
			   Objects.equals(missingFingerprints, that.missingFingerprints);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(applicantId, enrollmentDate, firstName, secondName, otherName, familyName, nationality,
		                    birthDate, passportNumber, gender, visaType, issueDate, issuanceCountry, expirationDate,
		                    birthPlace, passportType, mobileNumber, faceImage, fingerprints, missingFingerprints);
	}
	
	@Override
	public String toString()
	{
		return "VisaApplicantInfo{" + "applicantId=" + applicantId + ", enrollmentDate=" + enrollmentDate +
			   ", firstName='" + firstName + '\'' + ", secondName='" + secondName + '\'' + ", otherName='" +
			   otherName + '\'' + ", familyName='" + familyName + '\'' + ", nationality=" + nationality +
			   ", birthDate=" + birthDate + ", passportNumber='" + passportNumber + '\'' + ", gender=" + gender +
			   ", visaType=" + visaType + ", issueDate=" + issueDate + ", issuanceCountry=" + issuanceCountry +
			   ", expirationDate=" + expirationDate + ", birthPlace=" + birthPlace + ", passportType=" +
			   passportType + ", mobileNumber='" + mobileNumber + '\'' + ", faceImage='" + faceImage + '\'' +
			   ", fingerprints=" + fingerprints + ", missingFingerprints=" + missingFingerprints + '}';
	}
}