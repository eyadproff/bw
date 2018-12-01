package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class Finger extends JavaBean
{
	private int type;
	private String image;
	private int presence;
	private List<FingerCoordinate> fingerCoordinates;
	
	public Finger(int type, String image, List<FingerCoordinate> fingerCoordinates)
	{
		this.type = type;
		this.image = image;
		this.fingerCoordinates = fingerCoordinates;
	}
	
	public int getType(){return type;}
	public void setType(int type){this.type = type;}
	
	public String getImage(){return image;}
	public void setImage(String image){this.image = image;}
	
	public int getPresence(){return presence;}
	public void setPresence(int presence){this.presence = presence;}
	
	public List<FingerCoordinate> getFingerCoordinates(){return fingerCoordinates;}
	public void setFingerCoordinates(List<FingerCoordinate> fingerCoordinates)
	{this.fingerCoordinates = fingerCoordinates;}
}