package sa.gov.nic.bio.bw.workflow.commons.webservice;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NormalizedPersonInfo
{
	private String facePhotoBase64;
	private Long personId;
	private String personIdLabel;
	private PersonType personType;
	private String personTypeLabel;
	private String documentId;
	private String documentIdLabel;
	private DocumentType documentType;
	private String documentTypeLabel;
	private LocalDate documentIssuanceDate;
	private String documentIssuanceDateLabel;
	private LocalDate documentExpiryDate;
	private String documentExpiryDateLabel;
	private String firstName;
	private String firstNameLabel;
	private String fatherName;
	private String fatherNameLabel;
	private String grandfatherName;
	private String grandfatherNameLabel;
	private String familyName;
	private String familyNameLabel;
	private Gender gender;
	private String genderLabel;
	private Country nationality;
	private String nationalityLabel;
	private String occupation;
	private String occupationLabel;
	private String birthPlace;
	private String birthPlaceLabel;
	private LocalDate birthDate;
	private String birthDateLabel;
	private Boolean isOut;
	
	public NormalizedPersonInfo(Long personId, PersonInfo personInfo, String notAvailable, String maleText,
	                            String femaleText)
	{
		this.personId = personId;
		
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		
		if(personId != null) personIdLabel = AppUtils.localizeNumbers(String.valueOf(personId));
		else personIdLabel = notAvailable;
		
		if(personInfo == null)
		{
			firstNameLabel = notAvailable;
			fatherNameLabel = notAvailable;
			grandfatherNameLabel = notAvailable;
			familyNameLabel = notAvailable;
			genderLabel = notAvailable;
			nationalityLabel = notAvailable;
			occupationLabel = notAvailable;
			birthPlaceLabel = notAvailable;
			birthDateLabel = notAvailable;
			personTypeLabel = notAvailable;
			documentIdLabel = notAvailable;
			documentTypeLabel = notAvailable;
			documentIssuanceDateLabel = notAvailable;
			documentExpiryDateLabel = notAvailable;
			
			return;
		}
		
		
		facePhotoBase64 = personInfo.getFace();
		
		Name name = personInfo.getName();
		
		firstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(), false);
		fatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(), false);
		grandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(), name.getTranslatedGrandFatherName(),
		                                         false);
		familyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(), false);
		
		firstNameLabel = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(), true);
		fatherNameLabel = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(), true);
		grandfatherNameLabel = AppUtils.buildNamePart(name.getGrandfatherName(), name.getTranslatedGrandFatherName(),
		                                              true);
		familyNameLabel = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(), true);
		
		if(firstNameLabel == null) firstNameLabel = notAvailable;
		if(fatherNameLabel == null) fatherNameLabel = notAvailable;
		if(grandfatherNameLabel == null) grandfatherNameLabel = notAvailable;
		if(familyNameLabel == null) familyNameLabel = notAvailable;
		
		int genderInt = personInfo.getGender();
		if(genderInt == 1 || genderInt == 2) gender = Gender.values()[genderInt - 1];
		
		if(gender != null)
		{
			switch(gender)
			{
				case MALE: genderLabel = maleText; break;
				case FEMALE: genderLabel = femaleText; break;
			}
		}
		else genderLabel = notAvailable;
		
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		for(Country country : countries)
		{
			if(country.getCode() == personInfo.getNationality())
			{
				nationality = country;
				break;
			}
		}
		
		if(nationality != null) nationalityLabel = arabic ? nationality.getDescriptionAR() :
															nationality.getDescriptionEN();
		else nationalityLabel = notAvailable;
		
		PersonIdInfo identityInfo = personInfo.getIdentityInfo();
		
		occupation = identityInfo != null ? identityInfo.getOccupation() : null;
		if(occupation != null && !occupation.trim().isEmpty()) occupationLabel = AppUtils.localizeNumbers(occupation);
		else occupationLabel = notAvailable;
		
		birthPlace = personInfo.getBirthPlace();
		if(birthPlace != null && !birthPlace.trim().isEmpty()) birthPlaceLabel = AppUtils.localizeNumbers(birthPlace);
		else birthPlaceLabel = notAvailable;
		
		Date date = personInfo.getBirthDate();
		if(date != null && date.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			birthDate = date.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			birthDateLabel = AppUtils.formatHijriGregorianDate(AppUtils.gregorianDateToMilliSeconds(birthDate));
		}
		else birthDateLabel = notAvailable;
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>) Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
		
		String personTypeString = personInfo.getPersonType();
		if(personTypeString != null)
		{
			for(PersonType type : personTypes)
			{
				if(personTypeString.equals(type.getIfrPersonType()))
				{
					personType = type;
					break;
				}
			}
			
			if(personType != null) personTypeLabel = arabic ? personType.getDescriptionAR() :
															  personType.getDescriptionEN();
			else personTypeLabel = notAvailable;
		}
		else personTypeLabel = notAvailable;
		
		documentId = identityInfo != null ? identityInfo.getIdNumber() : null;
		if(documentId != null && !documentId.trim().isEmpty()) documentIdLabel = AppUtils.localizeNumbers(documentId);
		else documentIdLabel = notAvailable;
		
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
		
		Integer documentTypeInteger = identityInfo != null ? identityInfo.getIdType() : null;
		if(documentTypeInteger != null)
		{
			for(DocumentType type : documentTypes)
			{
				if(type.getCode() == documentTypeInteger)
				{
					documentType = type;
					break;
				}
			}
			
			if(documentType != null) documentTypeLabel = documentType.getDesc();
			else documentTypeLabel = notAvailable;
		}
		else documentTypeLabel = notAvailable;
		
		date = identityInfo != null ? identityInfo.getIdIssueDate() : null;
		if(date != null && date.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			documentIssuanceDate = date.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			documentIssuanceDateLabel = AppUtils.formatHijriGregorianDate(
															AppUtils.gregorianDateToMilliSeconds(documentIssuanceDate));
		}
		else documentIssuanceDateLabel = notAvailable;
		
		date = identityInfo != null ? identityInfo.getIdExpirDate() : null;
		if(date != null && date.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			documentExpiryDate = date.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			documentExpiryDateLabel = AppUtils.formatHijriGregorianDate(
															AppUtils.gregorianDateToMilliSeconds(documentExpiryDate));
		}
		else documentExpiryDateLabel = notAvailable;
	}
	
	public String getFacePhotoBase64(){return facePhotoBase64;}
	public void setFacePhotoBase64(String facePhotoBase64){this.facePhotoBase64 = facePhotoBase64;}
	
	public Long getPersonId(){return personId;}
	public void setPersonId(Long personId){this.personId = personId;}
	
	public String getPersonIdLabel(){return personIdLabel;}
	public void setPersonIdLabel(String personIdLabel){this.personIdLabel = personIdLabel;}
	
	public PersonType getPersonType(){return personType;}
	public void setPersonType(PersonType personType){this.personType = personType;}
	
	public String getPersonTypeLabel(){return personTypeLabel;}
	public void setPersonTypeLabel(String personTypeLabel){this.personTypeLabel = personTypeLabel;}
	
	public String getDocumentId(){return documentId;}
	public void setDocumentId(String documentId){this.documentId = documentId;}
	
	public String getDocumentIdLabel(){return documentIdLabel;}
	public void setDocumentIdLabel(String documentIdLabel){this.documentIdLabel = documentIdLabel;}
	
	public DocumentType getDocumentType(){return documentType;}
	public void setDocumentType(DocumentType documentType){this.documentType = documentType;}
	
	public String getDocumentTypeLabel(){return documentTypeLabel;}
	public void setDocumentTypeLabel(String documentTypeLabel){this.documentTypeLabel = documentTypeLabel;}
	
	public LocalDate getDocumentIssuanceDate(){return documentIssuanceDate;}
	public void setDocumentIssuanceDate(LocalDate documentIssuanceDate)
																{this.documentIssuanceDate = documentIssuanceDate;}
	
	public String getDocumentIssuanceDateLabel(){return documentIssuanceDateLabel;}
	public void setDocumentIssuanceDateLabel(String documentIssuanceDateLabel)
														{this.documentIssuanceDateLabel = documentIssuanceDateLabel;}
	
	public LocalDate getDocumentExpiryDate(){return documentExpiryDate;}
	public void setDocumentExpiryDate(LocalDate documentExpiryDate){this.documentExpiryDate = documentExpiryDate;}
	
	public String getDocumentExpiryDateLabel(){return documentExpiryDateLabel;}
	public void setDocumentExpiryDateLabel(String documentExpiryDateLabel)
															{this.documentExpiryDateLabel = documentExpiryDateLabel;}
	
	public String getFirstName(){return firstName;}
	public void setFirstName(String firstName){this.firstName = firstName;}
	
	public String getFirstNameLabel(){return firstNameLabel;}
	public void setFirstNameLabel(String firstNameLabel){this.firstNameLabel = firstNameLabel;}
	
	public String getFatherName(){return fatherName;}
	public void setFatherName(String fatherName){this.fatherName = fatherName;}
	
	public String getFatherNameLabel(){return fatherNameLabel;}
	public void setFatherNameLabel(String fatherNameLabel){this.fatherNameLabel = fatherNameLabel;}
	
	public String getGrandfatherName(){return grandfatherName;}
	public void setGrandfatherName(String grandfatherName){this.grandfatherName = grandfatherName;}
	
	public String getGrandfatherNameLabel(){return grandfatherNameLabel;}
	public void setGrandfatherNameLabel(String grandfatherNameLabel){this.grandfatherNameLabel = grandfatherNameLabel;}
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	public String getFamilyNameLabel(){return familyNameLabel;}
	public void setFamilyNameLabel(String familyNameLabel){this.familyNameLabel = familyNameLabel;}
	
	public Gender getGender(){return gender;}
	public void setGender(Gender gender){this.gender = gender;}
	
	public String getGenderLabel(){return genderLabel;}
	public void setGenderLabel(String genderLabel){this.genderLabel = genderLabel;}
	
	public Country getNationality(){return nationality;}
	public void setNationality(Country nationality){this.nationality = nationality;}
	
	public String getNationalityLabel(){return nationalityLabel;}
	public void setNationalityLabel(String nationalityLabel){this.nationalityLabel = nationalityLabel;}
	
	public String getOccupation(){return occupation;}
	public void setOccupation(String occupation){this.occupation = occupation;}
	
	public String getOccupationLabel(){return occupationLabel;}
	public void setOccupationLabel(String occupationLabel){this.occupationLabel = occupationLabel;}
	
	public String getBirthPlace(){return birthPlace;}
	public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}
	
	public String getBirthPlaceLabel(){return birthPlaceLabel;}
	public void setBirthPlaceLabel(String birthPlaceLabel){this.birthPlaceLabel = birthPlaceLabel;}
	
	public LocalDate getBirthDate(){return birthDate;}
	public void setBirthDate(LocalDate birthDate){this.birthDate = birthDate;}
	
	public String getBirthDateLabel(){return birthDateLabel;}
	public void setBirthDateLabel(String birthDateLabel){this.birthDateLabel = birthDateLabel;}
	
	public Boolean getIsOut(){return isOut;}
	public void setIsOut(Boolean isOut){ this.isOut = isOut;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		NormalizedPersonInfo that = (NormalizedPersonInfo) o;
		return Objects.equals(facePhotoBase64, that.facePhotoBase64) && Objects.equals(personId, that.personId) &&
			   Objects.equals(personIdLabel, that.personIdLabel) && Objects.equals(personType, that.personType) &&
			   Objects.equals(personTypeLabel, that.personTypeLabel) && Objects.equals(documentId, that.documentId) &&
			   Objects.equals(documentIdLabel, that.documentIdLabel) &&
			   Objects.equals(documentType, that.documentType) &&
			   Objects.equals(documentTypeLabel, that.documentTypeLabel) &&
			   Objects.equals(documentIssuanceDate, that.documentIssuanceDate) &&
			   Objects.equals(documentIssuanceDateLabel, that.documentIssuanceDateLabel) &&
			   Objects.equals(documentExpiryDate, that.documentExpiryDate) &&
			   Objects.equals(documentExpiryDateLabel, that.documentExpiryDateLabel) &&
			   Objects.equals(firstName, that.firstName) && Objects.equals(firstNameLabel, that.firstNameLabel) &&
			   Objects.equals(fatherName, that.fatherName) && Objects.equals(fatherNameLabel, that.fatherNameLabel) &&
			   Objects.equals(grandfatherName, that.grandfatherName) &&
			   Objects.equals(grandfatherNameLabel, that.grandfatherNameLabel) &&
			   Objects.equals(familyName, that.familyName) && Objects.equals(familyNameLabel, that.familyNameLabel) &&
			   gender == that.gender && Objects.equals(genderLabel, that.genderLabel) &&
			   Objects.equals(nationality, that.nationality) &&
			   Objects.equals(nationalityLabel, that.nationalityLabel) &&
			   Objects.equals(occupation, that.occupation) && Objects.equals(occupationLabel, that.occupationLabel) &&
			   Objects.equals(birthPlace, that.birthPlace) && Objects.equals(birthPlaceLabel, that.birthPlaceLabel) &&
			   Objects.equals(birthDate, that.birthDate) && Objects.equals(birthDateLabel, that.birthDateLabel) &&
			   Objects.equals(isOut, that.isOut);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(facePhotoBase64, personId, personIdLabel, personType, personTypeLabel, documentId,
		                    documentIdLabel, documentType, documentTypeLabel, documentIssuanceDate,
		                    documentIssuanceDateLabel, documentExpiryDate, documentExpiryDateLabel, firstName,
		                    firstNameLabel, fatherName, fatherNameLabel, grandfatherName, grandfatherNameLabel,
		                    familyName, familyNameLabel, gender, genderLabel, nationality, nationalityLabel, occupation,
		                    occupationLabel, birthPlace, birthPlaceLabel, birthDate, birthDateLabel, isOut);
	}
	
	@Override
	public String toString()
	{
		return "NormalizedPersonInfo{" + "facePhotoBase64='" + facePhotoBase64 + '\'' + ", personId=" + personId +
			   ", personIdLabel='" + personIdLabel + '\'' + ", personType=" + personType + ", personTypeLabel='" +
			   personTypeLabel + '\'' + ", documentId='" + documentId + '\'' + ", documentIdLabel='" +
			   documentIdLabel + '\'' + ", documentType=" + documentType + ", documentTypeLabel='" +
			   documentTypeLabel + '\'' + ", documentIssuanceDate=" + documentIssuanceDate +
			   ", documentIssuanceDateLabel='" + documentIssuanceDateLabel + '\'' + ", documentExpiryDate=" +
			   documentExpiryDate + ", documentExpiryDateLabel='" + documentExpiryDateLabel + '\'' + ", firstName='" +
			   firstName + '\'' + ", firstNameLabel='" + firstNameLabel + '\'' + ", fatherName='" + fatherName + '\'' +
			   ", fatherNameLabel='" + fatherNameLabel + '\'' + ", grandfatherName='" + grandfatherName + '\'' +
			   ", grandfatherNameLabel='" + grandfatherNameLabel + '\'' + ", familyName='" + familyName + '\'' +
			   ", familyNameLabel='" + familyNameLabel + '\'' + ", gender=" + gender + ", genderLabel='" +
			   genderLabel + '\'' + ", nationality=" + nationality + ", nationalityLabel='" + nationalityLabel + '\'' +
			   ", occupation='" + occupation + '\'' + ", occupationLabel='" + occupationLabel + '\'' +
			   ", birthPlace='" + birthPlace + '\'' + ", birthPlaceLabel='" + birthPlaceLabel + '\'' +
			   ", birthDate=" + birthDate + ", birthDateLabel='" + birthDateLabel + '\'' + ", isOut=" + isOut + '}';
	}
}