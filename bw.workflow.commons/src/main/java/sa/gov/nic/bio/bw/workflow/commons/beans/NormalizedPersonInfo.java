package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class NormalizedPersonInfo extends JavaBean
{
	private String facePhotoBase64;
	private Long personId;
	private PersonType personType;
	private String documentId;
	private DocumentType documentType;
	private LocalDate documentIssuanceDate;
	private LocalDate documentExpiryDate;
	private String firstName;
	private String firstNameLabel;
	private String fatherName;
	private String fatherNameLabel;
	private String grandfatherName;
	private String grandfatherNameLabel;
	private String familyName;
	private String familyNameLabel;
	private Gender gender;
	private Country nationality;
	private String occupation;
	private String birthPlace;
	private LocalDate birthDate;
	private Boolean isOut;
	
	public NormalizedPersonInfo(PersonInfo personInfo)
	{
		if(personInfo == null) return;
		
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
		
		int genderInt = personInfo.getGender();
		if(genderInt == 1 || genderInt == 2) gender = Gender.values()[genderInt - 1];
		
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
		
		PersonIdInfo identityInfo = personInfo.getIdentityInfo();
		
		occupation = identityInfo != null ? identityInfo.getOccupation() : null;
		birthPlace = personInfo.getBirthPlace();
		
		Date date = personInfo.getBirthDate();
		if(date != null && date.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			birthDate = date.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
		}
		
		personId = identityInfo != null ? Long.valueOf(identityInfo.getIdNumber()) : null;
		
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
		}
		
		documentId = identityInfo != null ? identityInfo.getIdNumber() : null;
		
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
		}
		
		date = identityInfo != null ? identityInfo.getIdIssueDate() : null;
		if(date != null && date.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			documentIssuanceDate = date.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
		}
		
		date = identityInfo != null ? identityInfo.getIdExpirDate() : null;
		if(date != null && date.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			documentExpiryDate = date.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
		}
	}
	
	public String getFacePhotoBase64(){return facePhotoBase64;}
	public void setFacePhotoBase64(String facePhotoBase64){this.facePhotoBase64 = facePhotoBase64;}
	
	public Long getPersonId(){return personId;}
	public void setPersonId(Long personId){this.personId = personId;}
	
	public PersonType getPersonType(){return personType;}
	public void setPersonType(PersonType personType){this.personType = personType;}
	
	public String getDocumentId(){return documentId;}
	public void setDocumentId(String documentId){this.documentId = documentId;}
	
	public DocumentType getDocumentType(){return documentType;}
	public void setDocumentType(DocumentType documentType){this.documentType = documentType;}
	
	public LocalDate getDocumentIssuanceDate(){return documentIssuanceDate;}
	public void setDocumentIssuanceDate(LocalDate documentIssuanceDate)
																{this.documentIssuanceDate = documentIssuanceDate;}
	
	public LocalDate getDocumentExpiryDate(){return documentExpiryDate;}
	public void setDocumentExpiryDate(LocalDate documentExpiryDate){this.documentExpiryDate = documentExpiryDate;}
	
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
	
	public Country getNationality(){return nationality;}
	public void setNationality(Country nationality){this.nationality = nationality;}
	
	public String getOccupation(){return occupation;}
	public void setOccupation(String occupation){this.occupation = occupation;}
	
	public String getBirthPlace(){return birthPlace;}
	public void setBirthPlace(String birthPlace){this.birthPlace = birthPlace;}
	
	public LocalDate getBirthDate(){return birthDate;}
	public void setBirthDate(LocalDate birthDate){this.birthDate = birthDate;}
	
	public Boolean getIsOut(){return isOut;}
	public void setIsOut(Boolean isOut){ this.isOut = isOut;}
}