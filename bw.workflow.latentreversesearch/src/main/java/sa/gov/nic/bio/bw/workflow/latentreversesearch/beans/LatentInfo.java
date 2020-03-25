package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class LatentInfo extends JavaBean
{
	private String latentId;
	private String fingerImage;
	private String fingerTemplate;
	private Integer fingerPos;
	//private LatentCrimeInfo crimeInfo; // we don't need this
	
	public String getLatentId(){return latentId;}
	public void setLatentId(String latentId){this.latentId = latentId;}
	
	public String getFingerImage(){return fingerImage;}
	public void setFingerImage(String fingerImage){this.fingerImage = fingerImage;}
	
	public String getFingerTemplate(){return fingerTemplate;}
	public void setFingerTemplate(String fingerTemplate){this.fingerTemplate = fingerTemplate;}
	
	public Integer getFingerPos(){return fingerPos;}
	public void setFingerPos(Integer fingerPos){this.fingerPos = fingerPos;}
}