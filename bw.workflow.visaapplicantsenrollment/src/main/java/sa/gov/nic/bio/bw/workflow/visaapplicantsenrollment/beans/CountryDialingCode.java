package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class CountryDialingCode extends JavaBean
{
	private String isoAlpha3Code;
	private int dialingCode;
	private String countryArabicName;
	private String countryEnglishName;
	
	public CountryDialingCode(String isoAlpha3Code, int dialingCode, String countryArabicName,
	                          String countryEnglishName)
	{
		this.isoAlpha3Code = isoAlpha3Code;
		this.dialingCode = dialingCode;
		this.countryArabicName = countryArabicName;
		this.countryEnglishName = countryEnglishName;
	}
	
	public String getIsoAlpha3Code(){return isoAlpha3Code;}
	public void setIsoAlpha3Code(String isoAlpha3Code){this.isoAlpha3Code = isoAlpha3Code;}
	
	public int getDialingCode(){return dialingCode;}
	public void setDialingCode(int dialingCode){this.dialingCode = dialingCode;}
	
	public String getCountryArabicName(){return countryArabicName;}
	public void setCountryArabicName(String countryArabicName){this.countryArabicName = countryArabicName;}
	
	public String getCountryEnglishName(){return countryEnglishName;}
	public void setCountryEnglishName(String countryEnglishName){this.countryEnglishName = countryEnglishName;}
}