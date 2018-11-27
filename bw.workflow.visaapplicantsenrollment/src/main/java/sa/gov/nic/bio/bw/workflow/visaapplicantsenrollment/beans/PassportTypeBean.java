package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class PassportTypeBean extends JavaBean
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
}