package sa.gov.nic.bio.bw.workflow.commons.webservice;

import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

import java.util.Objects;

public class Country implements LocalizedText
{
	private int code;
	private String mofaNationalityCode;
	private String descriptionAR;
	private String descriptionEN;
	
	public Country(int code, String mofaNationalityCode, String descriptionAR, String descriptionEN)
	{
		this.code = code;
		this.mofaNationalityCode = mofaNationalityCode;
		this.descriptionAR = descriptionAR;
		this.descriptionEN = descriptionEN;
	}
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getMofaNationalityCode(){return mofaNationalityCode;}
	public void setMofaNationalityCode(String mofaNationalityCode){this.mofaNationalityCode = mofaNationalityCode;}
	
	public String getDescriptionAR(){return descriptionAR;}
	public void setDescriptionAR(String descriptionAR){this.descriptionAR = descriptionAR;}
	
	public String getDescriptionEN(){return descriptionEN;}
	public void setDescriptionEN(String descriptionEN){this.descriptionEN = descriptionEN;}
	
	@Override
	public String getArabicText()
	{
		return descriptionAR;
	}
	
	@Override
	public String getEnglishText()
	{
		return descriptionEN;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Country that = (Country) o;
		return code == that.code && Objects.equals(mofaNationalityCode, that.mofaNationalityCode) &&
			   Objects.equals(descriptionAR, that.descriptionAR) && Objects.equals(descriptionEN, that.descriptionEN);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, mofaNationalityCode, descriptionAR, descriptionEN);
	}
	
	@Override
	public String toString()
	{
		return "Country{" + "code=" + code + ", mofaNationalityCode='" + mofaNationalityCode + '\'' +
			   ", descriptionAR='" + descriptionAR + '\'' + ", descriptionEN='" + descriptionEN + '\'' + '}';
	}
}