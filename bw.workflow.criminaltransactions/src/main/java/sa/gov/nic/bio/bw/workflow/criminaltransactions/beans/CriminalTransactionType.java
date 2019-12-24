package sa.gov.nic.bio.bw.workflow.criminaltransactions.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

public class CriminalTransactionType extends JavaBean implements LocalizedText
{
	private int code;
	private String descriptionAr;
	private String descriptionEn;
	
	public CriminalTransactionType(int code, String descriptionAr, String descriptionEn)
	{
		this.code = code;
		this.descriptionAr = descriptionAr;
		this.descriptionEn = descriptionEn;
	}
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getDescriptionAr(){return descriptionAr;}
	public void setDescriptionAr(String descriptionAr){this.descriptionAr = descriptionAr;}
	
	public String getDescriptionEn(){return descriptionEn;}
	public void setDescriptionEn(String descriptionEn){this.descriptionEn = descriptionEn;}
	
	@Override
	public String getArabicText()
	{
		return descriptionAr;
	}
	
	@Override
	public String getEnglishText()
	{
		return descriptionEn;
	}
}