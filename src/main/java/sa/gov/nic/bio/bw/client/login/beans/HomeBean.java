package sa.gov.nic.bio.bw.client.login.beans;

import javafx.scene.image.Image;

import java.util.Objects;

public class HomeBean
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
	private Image faceImage;
	
	public HomeBean(String username, String operatorName, String location, String sLoginTime, String sLastLogonTime,
	                String sLastFailedLoginTime, String sFailedLoginCount, String sLastPasswordChangeTime,
	                String sPasswordExpirationTime, Image faceImage)
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
		this.faceImage = faceImage;
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
	
	public Image getFaceImage(){return faceImage;}
	public void setFaceImage(Image faceImage){this.faceImage = faceImage;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		HomeBean homeBean = (HomeBean) o;
		return Objects.equals(username, homeBean.username) && Objects.equals(operatorName, homeBean.operatorName) &&
			   Objects.equals(location, homeBean.location) && Objects.equals(sLoginTime, homeBean.sLoginTime) &&
			   Objects.equals(sLastLogonTime, homeBean.sLastLogonTime) &&
			   Objects.equals(sLastFailedLoginTime, homeBean.sLastFailedLoginTime) &&
			   Objects.equals(sFailedLoginCount, homeBean.sFailedLoginCount) &&
			   Objects.equals(sLastPasswordChangeTime, homeBean.sLastPasswordChangeTime) &&
			   Objects.equals(sPasswordExpirationTime, homeBean.sPasswordExpirationTime) &&
			   Objects.equals(faceImage, homeBean.faceImage);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(username, operatorName, location, sLoginTime, sLastLogonTime, sLastFailedLoginTime,
		                    sFailedLoginCount, sLastPasswordChangeTime, sPasswordExpirationTime, faceImage);
	}
	
	@Override
	public String toString()
	{
		return "HomeBean{" + "username='" + username + '\'' + ", operatorName='" + operatorName + '\'' +
			   ", location='" + location + '\'' + ", sLoginTime='" + sLoginTime + '\'' + ", sLastLogonTime='" +
			   sLastLogonTime + '\'' + ", sLastFailedLoginTime='" + sLastFailedLoginTime + '\'' +
			   ", sFailedLoginCount='" + sFailedLoginCount + '\'' + ", sLastPasswordChangeTime='" +
			   sLastPasswordChangeTime + '\'' + ", sPasswordExpirationTime='" + sPasswordExpirationTime + '\'' +
			   ", faceImage='" + faceImage + '\'' + '}';
	}
}