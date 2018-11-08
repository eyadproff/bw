package sa.gov.nic.bio.bw.features.visaapplicantsenrollment.webservice;

import java.util.Objects;

public class CountryDialingCode
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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		CountryDialingCode that = (CountryDialingCode) o;
		return dialingCode == that.dialingCode && Objects.equals(isoAlpha3Code, that.isoAlpha3Code) &&
			   Objects.equals(countryArabicName, that.countryArabicName) &&
			   Objects.equals(countryEnglishName, that.countryEnglishName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(isoAlpha3Code, dialingCode, countryArabicName, countryEnglishName);
	}
	
	@Override
	public String toString()
	{
		return "CountryDialingCode{" + "isoAlpha3Code='" + isoAlpha3Code + '\'' + ", dialingCode=" + dialingCode +
			   ", countryArabicName='" + countryArabicName + '\'' + ", countryEnglishName='" + countryEnglishName +
			   '\'' + '}';
	}
}