package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.interfaces.LocalizedText;

public class BiometricsExchangeParty extends JavaBean implements LocalizedText
{
	private Integer orgPartyCode;
	private String orgArabicName;
	private String orgEnglishName;
	
	public BiometricsExchangeParty(Integer orgPartyCode, String orgArabicName, String orgEnglishName)
	{
		this.orgPartyCode = orgPartyCode;
		this.orgArabicName = orgArabicName;
		this.orgEnglishName = orgEnglishName;
	}
	
	public Integer getOrgPartyCode(){return orgPartyCode;}
	public void setOrgPartyCode(Integer orgPartyCode){this.orgPartyCode = orgPartyCode;}
	
	public String getOrgArabicName(){return orgArabicName;}
	public void setOrgArabicName(String orgArabicName){this.orgArabicName = orgArabicName;}
	
	public String getOrgEnglishName(){return orgEnglishName;}
	public void setOrgEnglishName(String orgEnglishName){this.orgEnglishName = orgEnglishName;}
	
	@Override
	public String getArabicText()
	{
		return orgArabicName;
	}
	
	@Override
	public String getEnglishText()
	{
		return orgEnglishName;
	}
}