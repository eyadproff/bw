package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice;

import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;

import java.util.List;
import java.util.Objects;

public class VisaApplicantInfo
{
	private Long applicantId;
	private Long enrollmentDate;
	private String firstName;
	private String secondName;
	private String otherName;
	private String familyName;
	private Integer nationalityCode;
	private Long birthDate;
	private String passportNumber;
	private Integer genderCode;
	private Integer visaTypeCode;
	private Long issueDate;
	private Integer issuanceCountry;
	private Long expirationDate;
	private Integer birthPlaceCode;
	private Integer passportType;
	private String mobileNumber;
	private String face;
	private List<Finger> fingers;
	private List<Integer> missingFingers;
	
	public VisaApplicantInfo(Long applicantId, Long enrollmentDate, String firstName, String secondName,
	                         String otherName, String familyName, Integer nationalityCode, Long birthDate,
	                         String passportNumber, Integer genderCode, Integer visaTypeCode, Long issueDate,
	                         Integer issuanceCountry, Long expirationDate, Integer birthPlaceCode,
	                         Integer passportType, String mobileNumber, String face, List<Finger> fingers,
	                         List<Integer> missingFingers)
	{
		this.applicantId = applicantId;
		this.enrollmentDate = enrollmentDate;
		this.firstName = firstName;
		this.secondName = secondName;
		this.otherName = otherName;
		this.familyName = familyName;
		this.nationalityCode = nationalityCode;
		this.birthDate = birthDate;
		this.passportNumber = passportNumber;
		this.genderCode = genderCode;
		this.visaTypeCode = visaTypeCode;
		this.issueDate = issueDate;
		this.issuanceCountry = issuanceCountry;
		this.expirationDate = expirationDate;
		this.birthPlaceCode = birthPlaceCode;
		this.passportType = passportType;
		this.mobileNumber = mobileNumber;
		this.face = face;
		this.fingers = fingers;
		this.missingFingers = missingFingers;
	}
	
	public Long getApplicantId(){return applicantId;}
	public void setApplicantId(Long applicantId){this.applicantId = applicantId;}
	
	public Long getEnrollmentDate(){return enrollmentDate;}
	public void setEnrollmentDate(Long enrollmentDate){this.enrollmentDate = enrollmentDate;}
	
	public String getFirstName(){return firstName;}
	public void setFirstName(String firstName){this.firstName = firstName;}
	
	public String getSecondName(){return secondName;}
	public void setSecondName(String secondName){this.secondName = secondName;}
	
	public String getOtherName(){return otherName;}
	public void setOtherName(String otherName){this.otherName = otherName;}
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	public Integer getNationalityCode(){return nationalityCode;}
	public void setNationalityCode(Integer nationalityCode){this.nationalityCode = nationalityCode;}
	
	public Long getBirthDate(){return birthDate;}
	public void setBirthDate(Long birthDate){this.birthDate = birthDate;}
	
	public String getPassportNumber(){return passportNumber;}
	public void setPassportNumber(String passportNumber){this.passportNumber = passportNumber;}
	
	public Integer getGenderCode(){return genderCode;}
	public void setGenderCode(Integer genderCode){this.genderCode = genderCode;}
	
	public Integer getVisaTypeCode(){return visaTypeCode;}
	public void setVisaTypeCode(Integer visaTypeCode){this.visaTypeCode = visaTypeCode;}
	
	public Long getIssueDate(){return issueDate;}
	public void setIssueDate(Long issueDate){this.issueDate = issueDate;}
	
	public Integer getIssuanceCountry(){return issuanceCountry;}
	public void setIssuanceCountry(Integer issuanceCountry){this.issuanceCountry = issuanceCountry;}
	
	public Long getExpirationDate(){return expirationDate;}
	public void setExpirationDate(Long expirationDate){this.expirationDate = expirationDate;}
	
	public Integer getBirthPlaceCode(){return birthPlaceCode;}
	public void setBirthPlaceCode(Integer birthPlaceCode){this.birthPlaceCode = birthPlaceCode;}
	
	public Integer getPassportType(){return passportType;}
	public void setPassportType(Integer passportType){this.passportType = passportType;}
	
	public String getMobileNumber(){return mobileNumber;}
	public void setMobileNumber(String mobileNumber){this.mobileNumber = mobileNumber;}
	
	public String getFace(){return face;}
	public void setFace(String face){this.face = face;}
	
	public List<Finger> getFingers(){return fingers;}
	public void setFingers(List<Finger> fingers){this.fingers = fingers;}
	
	public List<Integer> getMissingFingers(){return missingFingers;}
	public void setMissingFingers(List<Integer> missingFingers){this.missingFingers = missingFingers;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		VisaApplicantInfo that = (VisaApplicantInfo) o;
		return Objects.equals(applicantId, that.applicantId) && Objects.equals(enrollmentDate, that.enrollmentDate) &&
			   Objects.equals(firstName, that.firstName) && Objects.equals(secondName, that.secondName) &&
			   Objects.equals(otherName, that.otherName) && Objects.equals(familyName, that.familyName) &&
			   Objects.equals(nationalityCode, that.nationalityCode) && Objects.equals(birthDate, that.birthDate) &&
			   Objects.equals(passportNumber, that.passportNumber) && Objects.equals(genderCode, that.genderCode) &&
			   Objects.equals(visaTypeCode, that.visaTypeCode) && Objects.equals(issueDate, that.issueDate) &&
			   Objects.equals(issuanceCountry, that.issuanceCountry) &&
			   Objects.equals(expirationDate, that.expirationDate) &&
			   Objects.equals(birthPlaceCode, that.birthPlaceCode) &&
			   Objects.equals(passportType, that.passportType) && Objects.equals(mobileNumber, that.mobileNumber) &&
			   Objects.equals(face, that.face) && Objects.equals(fingers, that.fingers) &&
			   Objects.equals(missingFingers, that.missingFingers);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(applicantId, enrollmentDate, firstName, secondName, otherName, familyName, nationalityCode,
		                    birthDate, passportNumber, genderCode, visaTypeCode, issueDate, issuanceCountry,
		                    expirationDate, birthPlaceCode, passportType, mobileNumber, face, fingers, missingFingers);
	}
	
	@Override
	public String toString()
	{
		return "VisaApplicantInfo{" + "applicantId=" + applicantId + ", enrollmentDate=" + enrollmentDate +
			   ", firstName='" + firstName + '\'' + ", secondName='" + secondName + '\'' + ", otherName='" +
			   otherName + '\'' + ", familyName='" + familyName + '\'' + ", nationalityCode=" + nationalityCode +
			   ", birthDate=" + birthDate + ", passportNumber='" + passportNumber + '\'' + ", genderCode=" +
			   genderCode + ", visaTypeCode=" + visaTypeCode + ", issueDate=" + issueDate + ", issuanceCountry=" +
			   issuanceCountry + ", expirationDate=" + expirationDate + ", birthPlaceCode=" + birthPlaceCode +
			   ", passportType=" + passportType + ", mobileNumber='" + mobileNumber + '\'' + ", face='" + face +
			   '\'' + ", fingers=" + fingers + ", missingFingers=" + missingFingers + '}';
	}
}