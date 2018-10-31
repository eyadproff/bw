package sa.gov.nic.bio.bw.client.features.commons.webservice;

import java.util.Objects;

public class PersonType
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
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PersonType that = (PersonType) o;
		return code == that.code && Objects.equals(descriptionAR, that.descriptionAR) &&
			   Objects.equals(descriptionEN, that.descriptionEN) &&
			   Objects.equals(ifrPersonType, that.ifrPersonType);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, descriptionAR, descriptionEN, ifrPersonType);
	}
	
	@Override
	public String toString()
	{
		return "PersonType{" + "code=" + code + ", descriptionAR='" + descriptionAR + '\'' +
			   ", descriptionEN='" + descriptionEN + '\'' + ", ifrPersonType='" + ifrPersonType + '\'' + '}';
	}
}