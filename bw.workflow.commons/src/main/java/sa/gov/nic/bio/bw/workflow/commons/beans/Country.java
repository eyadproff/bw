package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

public class Country extends JavaBean implements LocalizedText
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
}