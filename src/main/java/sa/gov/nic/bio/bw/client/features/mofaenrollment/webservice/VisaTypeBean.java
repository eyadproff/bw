package sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice;

import java.io.Serializable;
import java.util.Objects;

public class VisaTypeBean implements Serializable
{
	private int code;
	private String mofaVisaType;
	private String descriptionAR;
	private String descriptionEN;
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getMofaVisaType(){return mofaVisaType;}
	public void setMofaVisaType(String mofaVisaType){this.mofaVisaType = mofaVisaType;}
	
	public String getDescriptionAR(){return descriptionAR;}
	public void setDescriptionAR(String descriptionAR){this.descriptionAR = descriptionAR;}
	
	public String getDescriptionEN(){return descriptionEN;}
	public void setDescriptionEN(String descriptionEN){this.descriptionEN = descriptionEN;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		VisaTypeBean that = (VisaTypeBean) o;
		return code == that.code && Objects.equals(mofaVisaType, that.mofaVisaType) && Objects.equals(descriptionAR, that.descriptionAR) && Objects.equals(descriptionEN, that.descriptionEN);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, mofaVisaType, descriptionAR, descriptionEN);
	}
	
	@Override
	public String toString()
	{
		return "VisaTypeBean{" + "code=" + code + ", mofaVisaType='" + mofaVisaType + '\'' + ", descriptionAR='" + descriptionAR + '\'' + ", descriptionEN='" + descriptionEN + '\'' + '}';
	}
}