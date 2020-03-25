package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class Latent extends JavaBean
{
	private long latentId;
	private String latentImageWsq;
	private String latentImageBase64;
	private Integer generalScore;
	private List<FingerHitDetails> fingerHitDetails;
	
	public long getLatentId(){return latentId;}
	public void setLatentId(long latentId){this.latentId = latentId;}
	
	public String getLatentImageWsq(){return latentImageWsq;}
	public void setLatentImageWsq(String latentImageWsq){this.latentImageWsq = latentImageWsq;}
	
	public String getLatentImageBase64(){return latentImageBase64;}
	public void setLatentImageBase64(String latentImageBase64){this.latentImageBase64 = latentImageBase64;}
	
	public Integer getGeneralScore(){return generalScore;}
	public void setGeneralScore(Integer generalScore){this.generalScore = generalScore;}
	
	public List<FingerHitDetails> getFingerHitDetails(){return fingerHitDetails;}
	public void setFingerHitDetails(List<FingerHitDetails> fingerHitDetails){this.fingerHitDetails = fingerHitDetails;}
}