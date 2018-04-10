package sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice;

import java.util.Objects;

public class Finger
{
	private int type;
	private String image;
	private int presence;
	private FingerCoordinate fingerCoordinate;
	
	public Finger(int type, String image, FingerCoordinate fingerCoordinate)
	{
		this.type = type;
		this.image = image;
		this.fingerCoordinate = fingerCoordinate;
	}
	
	public int getType(){return type;}
	public void setType(int type){this.type = type;}
	
	public String getImage(){return image;}
	public void setImage(String image){this.image = image;}
	
	public int getPresence(){return presence;}
	public void setPresence(int presence){this.presence = presence;}
	
	public FingerCoordinate getFingerCoordinate(){return fingerCoordinate;}
	public void setFingerCoordinate(FingerCoordinate fingerCoordinate){this.fingerCoordinate = fingerCoordinate;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Finger finger = (Finger) o;
		return type == finger.type && Objects.equals(image, finger.image) &&
			   Objects.equals(fingerCoordinate, finger.fingerCoordinate);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(type, image, fingerCoordinate);
	}
	
	@Override
	public String toString()
	{
		return "Finger{" + "type=" + type + ", image='" + image + '\'' + ", fingerCoordinate=" + fingerCoordinate + '}';
	}
}