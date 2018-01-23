package sa.gov.nic.bio.bw.client.login.webservice;

import java.io.Serializable;
import java.util.Arrays;

public class UserInfo implements Serializable
{
	private String distinguishedName;
	private String userName;
	private String gender;
	private String userID;
	private String locationId;
	private String[] originalStringRoles;
	private int badPasswordCount;
	private long badPasswordTime;
	private long lastLogonTime;
	private String locationName;
	private String operatorName;
	private long operatorId;
	private long accountExperiyDate;
	private long passwordLastSet;
	private int[] locations;
	private int status;
	private String faceImage;
	
	public UserInfo(){}
	
	public String getDistinguishedName(){return distinguishedName;}
	public void setDistinguishedName(String distinguishedName){this.distinguishedName = distinguishedName;}
	
	public String getUserName(){return userName;}
	public void setUserName(String userName){this.userName = userName;}
	
	public String getGender(){return gender;}
	public void setGender(String gender){this.gender = gender;}
	
	public String getUserID(){return userID;}
	public void setUserID(String userID){this.userID = userID;}
	
	public String getLocationId(){return locationId;}
	public void setLocationId(String locationId){this.locationId = locationId;}
	
	public String[] getOriginalStringRoles(){return originalStringRoles;}
	public void setOriginalStringRoles(String[] originalStringRoles){this.originalStringRoles = originalStringRoles;}
	
	public int getBadPasswordCount(){return badPasswordCount;}
	public void setBadPasswordCount(int badPasswordCount){this.badPasswordCount = badPasswordCount;}
	
	public long getBadPasswordTime(){return badPasswordTime;}
	public void setBadPasswordTime(long badPasswordTime){this.badPasswordTime = badPasswordTime;}
	
	public long getLastLogonTime(){return lastLogonTime;}
	public void setLastLogonTime(long lastLogonTime){this.lastLogonTime = lastLogonTime;}
	
	public String getLocationName(){return locationName;}
	public void setLocationName(String locationName){this.locationName = locationName;}
	
	public String getOperatorName(){return operatorName;}
	public void setOperatorName(String operatorName){this.operatorName = operatorName;}
	
	public long getOperatorId(){return operatorId;}
	public void setOperatorId(long operatorId){this.operatorId = operatorId;}
	
	public long getAccountExperiyDate(){return accountExperiyDate;}
	public void setAccountExperiyDate(long accountExperiyDate){this.accountExperiyDate = accountExperiyDate;}
	
	public long getPasswordLastSet(){return passwordLastSet;}
	public void setPasswordLastSet(long passwordLastSet){this.passwordLastSet = passwordLastSet;}
	
	public int[] getLocations(){return locations;}
	public void setLocations(int[] locations){this.locations = locations;}
	
	public int getStatus(){return status;}
	public void setStatus(int status){this.status = status;}
	
	public String getFaceImage(){return faceImage;}
	public void setFaceImage(String faceImage){this.faceImage = faceImage;}
	
	@Override
	public String toString()
	{
		return "UserInfo{" + "distinguishedName='" + distinguishedName + '\'' + ", userName='" + userName + '\'' + ", gender='" + gender + '\'' +
				", userID='" + userID + '\'' + ", locationId='" + locationId + '\'' + ", originalStringRoles=" + Arrays.toString(originalStringRoles) +
				", badPasswordCount=" + badPasswordCount + ", badPasswordTime=" + badPasswordTime + ", lastLogonTime=" + lastLogonTime +
				", locationName='" + locationName + '\'' + ", operatorName='" + operatorName + '\'' + ", operatorId=" + operatorId +
				", accountExperiyDate=" + accountExperiyDate + ", passwordLastSet=" + passwordLastSet + ", locations=" + Arrays.toString(locations) +
				", status=" + status + ", faceImage='" + faceImage + '\'' + '}';
	}
}