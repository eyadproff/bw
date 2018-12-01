package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

public class DocumentType extends JavaBean implements LocalizedText
{
	private int code;
	private String desc;
	
	public DocumentType(int code, String desc)
	{
		this.code = code;
		this.desc = desc;
	}
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getDesc(){return desc;}
	public void setDesc(String desc){this.desc = desc;}
	
	@Override
	public String getArabicText()
	{
		return desc;
	}
	
	@Override
	public String getEnglishText()
	{
		return desc;
	}
}