package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class FingerHit extends JavaBean
{
	private int position;
	private int score;
	private String imageBase64;
	
	public int getPosition(){return position;}
	public void setPosition(int position){this.position = position;}
	
	public int getScore(){return score;}
	public void setScore(int score){this.score = score;}
	
	public String getImageBase64(){return imageBase64;}
	public void setImageBase64(String imageBase64){this.imageBase64 = imageBase64;}
}