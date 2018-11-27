package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

public class PersonType extends JavaBean implements LocalizedText
{
	private int code;
	private String descriptionAR;
	private String descriptionEN;
	private String ifrPersonType;
	
	public PersonType(int code, String descriptionAR, String descriptionEN, String ifrPersonType)
	{
		this.code = code;
		this.descriptionAR = descriptionAR;
		this.descriptionEN = descriptionEN;
		this.ifrPersonType = ifrPersonType;
	}
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getDescriptionAR(){return descriptionAR;}
	public void setDescriptionAR(String descriptionAR){this.descriptionAR = descriptionAR;}
	
	public String getDescriptionEN(){return descriptionEN;}
	public void setDescriptionEN(String descriptionEN){this.descriptionEN = descriptionEN;}
	
	public String getIfrPersonType(){return ifrPersonType;}
	public void setIfrPersonType(String ifrPersonType){this.ifrPersonType = ifrPersonType;}
	
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