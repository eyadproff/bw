package sa.gov.nic.bio.bw.workflow.commons.beans;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class HomeBean extends JavaBean
{
	private String username;
	private String operatorName;
	private String location;
	private String sLoginTime;
	private String sLastLogonTime;
	private String sLastFailedLoginTime;
	private String sFailedLoginCount;
	private String sLastPasswordChangeTime;
	private String sPasswordExpirationTime;
	private Image facePhoto;
	
	public HomeBean(String username, String operatorName, String location, String sLoginTime, String sLastLogonTime,
	                String sLastFailedLoginTime, String sFailedLoginCount, String sLastPasswordChangeTime,
	                String sPasswordExpirationTime, Image facePhoto)
	{
		this.username = username;
		this.operatorName = operatorName;
		this.location = location;
		this.sLoginTime = sLoginTime;
		this.sLastLogonTime = sLastLogonTime;
		this.sLastFailedLoginTime = sLastFailedLoginTime;
		this.sFailedLoginCount = sFailedLoginCount;
		this.sLastPasswordChangeTime = sLastPasswordChangeTime;
		this.sPasswordExpirationTime = sPasswordExpirationTime;
		this.facePhoto = facePhoto;
	}
	
	public String getUsername(){return username;}
	public void setUsername(String username){this.username = username;}
	
	public String getOperatorName(){return operatorName;}
	public void setOperatorName(String operatorName){this.operatorName = operatorName;}
	
	public String getLocation(){return location;}
	public void setLocation(String location){this.location = location;}
	
	public String getsLoginTime(){return sLoginTime;}
	public void setsLoginTime(String sLoginTime){this.sLoginTime = sLoginTime;}
	
	public String getsLastLogonTime(){return sLastLogonTime;}
	public void setsLastLogonTime(String sLastLogonTime){this.sLastLogonTime = sLastLogonTime;}
	
	public String getsLastFailedLoginTime(){return sLastFailedLoginTime;}
	public void setsLastFailedLoginTime(String sLastFailedLoginTime){this.sLastFailedLoginTime = sLastFailedLoginTime;}
	
	public String getsFailedLoginCount(){return sFailedLoginCount;}
	public void setsFailedLoginCount(String sFailedLoginCount){this.sFailedLoginCount = sFailedLoginCount;}
	
	public String getsLastPasswordChangeTime(){return sLastPasswordChangeTime;}
	public void setsLastPasswordChangeTime(String sLastPasswordChangeTime)
															{this.sLastPasswordChangeTime = sLastPasswordChangeTime;}
	
	public String getsPasswordExpirationTime(){return sPasswordExpirationTime;}
	public void setsPasswordExpirationTime(String sPasswordExpirationTime)
															{this.sPasswordExpirationTime = sPasswordExpirationTime;}
	
	public Image getFacePhoto(){return facePhoto;}
	public void setFacePhoto(Image facePhoto){this.facePhoto = facePhoto;}
}