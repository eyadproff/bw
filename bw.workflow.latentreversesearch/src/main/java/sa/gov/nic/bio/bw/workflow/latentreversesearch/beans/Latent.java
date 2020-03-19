package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import java.util.List;

public class Latent
{
	private String number;
	private int score;
	private String imageBase64;
	private List<FingerHit> fingerHits;
	
	public String getNumber(){return number;}
	public void setNumber(String number){this.number = number;}
	
	public int getScore(){return score;}
	public void setScore(int score){this.score = score;}
	
	public String getImageBase64(){return imageBase64;}
	public void setImageBase64(String imageBase64){this.imageBase64 = imageBase64;}
	
	public List<FingerHit> getFingerHits(){return fingerHits;}
	public void setFingerHits(List<FingerHit> fingerHits){this.fingerHits = fingerHits;}
}