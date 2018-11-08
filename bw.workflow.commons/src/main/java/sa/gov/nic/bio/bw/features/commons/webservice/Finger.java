package sa.gov.nic.bio.bw.features.commons.webservice;

import java.util.List;
import java.util.Objects;

public class Finger
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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Finger finger = (Finger) o;
		return type == finger.type && presence == finger.presence && Objects.equals(image, finger.image) &&
			   Objects.equals(fingerCoordinates, finger.fingerCoordinates);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(type, image, presence, fingerCoordinates);
	}
	
	@Override
	public String toString()
	{
		return "Finger{" + "type=" + type + ", image='" + image + '\'' + ", presence=" + presence +
			   ", fingerCoordinates=" + fingerCoordinates + '}';
	}
}