package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NormalizedMiscreantInfo extends JavaBean
{
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
	
	public NormalizedMiscreantInfo(MiscreantInfo miscreantInfo)
	{
		if(miscreantInfo == null) return;
		
		Name name = miscreantInfo.getName();
		
		if(name != null)
		{
			firstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(), false);
			fatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(), false);
			grandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(), name.getTranslatedGrandFatherName(),
			                                         false);
			familyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(), false);
			
			firstNameLabel = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(), true);
			fatherNameLabel = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(),
			                                         true);
			grandfatherNameLabel = AppUtils.buildNamePart(name.getGrandfatherName(),
			                                              name.getTranslatedGrandFatherName(),
			                                              true);
			familyNameLabel = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(),
			                                         true);
		}
		
		Integer genderInt = miscreantInfo.getGender();
		if(genderInt != null && (genderInt == 1 || genderInt == 2)) gender = Gender.values()[genderInt - 1];
		
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		Integer nationalityInteger = miscreantInfo.getNationality();
		if(nationalityInteger != null)
		{
			List<Country> countriesPlusUnknown = new ArrayList<>(countries);
			String unknownNationalityText =
								AppUtils.getCoreStringsResourceBundle().getString("combobox.unknownNationality");
			Country unknownNationality = new Country(0, null, unknownNationalityText,
			                                         unknownNationalityText);
			countriesPlusUnknown.add(0, unknownNationality);
			
			for(Country country : countriesPlusUnknown)
			{
				if(country.getCode() == nationalityInteger)
				{
					nationality = country;
					break;
				}
			}
		}
		
		occupation = miscreantInfo.getOccupation() != null ? miscreantInfo.getOccupation().strip() : null;
		birthPlace = miscreantInfo.getBirthPlace() != null ? miscreantInfo.getBirthPlace().strip() : null;
		
		Long unixTimeSeconds = miscreantInfo.getBirthDate();
		if(unixTimeSeconds != null && unixTimeSeconds * 1000L > AppConstants.SAMIS_DB_DATE_EPOCH_MS_NOT_SET_VALUE)
		{
			birthDate = LocalDate.ofInstant(Instant.ofEpochSecond(unixTimeSeconds), AppConstants.SAUDI_ZONE);
		}
	}
	
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
}