package sa.gov.nic.bio.bw.client.login.webservice;

import com.squareup.moshi.Json;

import java.util.Arrays;
import java.util.Objects;

public class LoginBean
{
	@Json(name = "AUTH")
	private String accessToken;
	
	private String distinguishedName;
	private String userName;
	private String gender;
	private String userID;
	private String locationId;
	private String[] originalStringRoles;
	private int badPasswordCount;
	private int badPasswordTime;
	private long lastLogonTime;
	private String locationName;
	private String operatorName;
	private long operatorId;
	private long accountExperiyDate;
	private long passwordLastSet;
	private int[] locations;
	private int status;
	
	public String getAccessToken()
	{
		return accessToken;
	}
	
	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}
	
	public String getDistinguishedName()
	{
		return distinguishedName;
	}
	
	public void setDistinguishedName(String distinguishedName)
	{
		this.distinguishedName = distinguishedName;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getGender()
	{
		return gender;
	}
	
	public void setGender(String gender)
	{
		this.gender = gender;
	}
	
	public String getUserID()
	{
		return userID;
	}
	
	public void setUserID(String userID)
	{
		this.userID = userID;
	}
	
	public String getLocationId()
	{
		return locationId;
	}
	
	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}
	
	public String[] getOriginalStringRoles()
	{
		return originalStringRoles;
	}
	
	public void setOriginalStringRoles(String[] originalStringRoles)
	{
		this.originalStringRoles = originalStringRoles;
	}
	
	public int getBadPasswordCount()
	{
		return badPasswordCount;
	}
	
	public void setBadPasswordCount(int badPasswordCount)
	{
		this.badPasswordCount = badPasswordCount;
	}
	
	public int getBadPasswordTime()
	{
		return badPasswordTime;
	}
	
	public void setBadPasswordTime(int badPasswordTime)
	{
		this.badPasswordTime = badPasswordTime;
	}
	
	public long getLastLogonTime()
	{
		return lastLogonTime;
	}
	
	public void setLastLogonTime(long lastLogonTime)
	{
		this.lastLogonTime = lastLogonTime;
	}
	
	public String getLocationName()
	{
		return locationName;
	}
	
	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}
	
	public String getOperatorName()
	{
		return operatorName;
	}
	
	public void setOperatorName(String operatorName)
	{
		this.operatorName = operatorName;
	}
	
	public long getOperatorId()
	{
		return operatorId;
	}
	
	public void setOperatorId(long operatorId)
	{
		this.operatorId = operatorId;
	}
	
	public long getAccountExperiyDate()
	{
		return accountExperiyDate;
	}
	
	public void setAccountExperiyDate(long accountExperiyDate)
	{
		this.accountExperiyDate = accountExperiyDate;
	}
	
	public long getPasswordLastSet()
	{
		return passwordLastSet;
	}
	
	public void setPasswordLastSet(long passwordLastSet)
	{
		this.passwordLastSet = passwordLastSet;
	}
	
	public int[] getLocations()
	{
		return locations;
	}
	
	public void setLocations(int[] locations)
	{
		this.locations = locations;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		LoginBean loginBean = (LoginBean) o;
		return badPasswordCount == loginBean.badPasswordCount && badPasswordTime == loginBean.badPasswordTime && lastLogonTime == loginBean.lastLogonTime && operatorId == loginBean.operatorId && accountExperiyDate == loginBean.accountExperiyDate && passwordLastSet == loginBean.passwordLastSet && status == loginBean.status && Objects.equals(accessToken, loginBean.accessToken) && Objects.equals(distinguishedName, loginBean.distinguishedName) && Objects.equals(userName, loginBean.userName) && Objects.equals(gender, loginBean.gender) && Objects.equals(userID, loginBean.userID) && Objects.equals(locationId, loginBean.locationId) && Arrays.equals(originalStringRoles, loginBean.originalStringRoles) && Objects.equals(locationName, loginBean.locationName) && Objects.equals(operatorName, loginBean.operatorName) && Arrays.equals(locations, loginBean.locations);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(accessToken, distinguishedName, userName, gender, userID, locationId, originalStringRoles, badPasswordCount, badPasswordTime, lastLogonTime, locationName, operatorName, operatorId, accountExperiyDate, passwordLastSet, locations, status);
	}
	
	@Override
	public String toString()
	{
		return "LoginBean{" + "accessToken='" + accessToken + '\'' + ", distinguishedName='" + distinguishedName + '\'' + ", userName='" + userName + '\'' + ", gender='" + gender + '\'' + ", userID='" + userID + '\'' + ", locationId='" + locationId + '\'' + ", originalStringRoles=" + Arrays.toString(originalStringRoles) + ", badPasswordCount=" + badPasswordCount + ", badPasswordTime=" + badPasswordTime + ", lastLogonTime=" + lastLogonTime + ", locationName='" + locationName + '\'' + ", operatorName='" + operatorName + '\'' + ", operatorId=" + operatorId + ", accountExperiyDate=" + accountExperiyDate + ", passwordLastSet=" + passwordLastSet + ", locations=" + Arrays.toString(locations) + ", status=" + status + '}';
	}
}