package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class FingerHitDetails extends JavaBean
{
	private Integer score;
	private Integer position;
	private Integer generalScore;
	
	public Integer getScore(){return score;}
	public void setScore(Integer score){this.score = score;}
	
	public Integer getPosition(){return position;}
	public void setPosition(Integer position){this.position = position;}
	
	public Integer getGeneralScore(){return generalScore;}
	public void setGeneralScore(Integer generalScore){this.generalScore = generalScore;}
}