package sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice;

import java.io.Serializable;
import java.util.Objects;

public class PassportTypeBean implements Serializable
{
	private int code;
	private String descriptionAR;
	private String descriptionEN;
	
	public PassportTypeBean(int code, String descriptionAR, String descriptionEN)
	{
		this.code = code;
		this.descriptionAR = descriptionAR;
		this.descriptionEN = descriptionEN;
	}
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getDescriptionAR(){return descriptionAR;}
	public void setDescriptionAR(String descriptionAR){this.descriptionAR = descriptionAR;}
	
	public String getDescriptionEN(){return descriptionEN;}
	public void setDescriptionEN(String descriptionEN){this.descriptionEN = descriptionEN;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		PassportTypeBean that = (PassportTypeBean) o;
		return code == that.code && Objects.equals(descriptionAR, that.descriptionAR) &&
			   Objects.equals(descriptionEN, that.descriptionEN);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, descriptionAR, descriptionEN);
	}
	
	@Override
	public String toString()
	{
		return "PassportTypeBean{" + "code=" + code + ", descriptionAR='" + descriptionAR + '\'' + ", descriptionEN='" +
			   descriptionEN + '\'' + '}';
	}
}