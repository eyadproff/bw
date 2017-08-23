package sa.gov.nic.bio.bw.client.login.webservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import sa.gov.nic.bio.bw.client.core.webservice.ApiResponseBase;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class LoginBean implements Serializable
{
	public static class UserInfo implements Serializable
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
		
		public long getBadPasswordTime()
		{
			return badPasswordTime;
		}
		
		public void setBadPasswordTime(long badPasswordTime)
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
	}
	
	private UserInfo userInfo;
	private String userToken;
	
	public UserInfo getUserInfo()
	{
		return userInfo;
	}
	
	public void setUserInfo(UserInfo userInfo)
	{
		this.userInfo = userInfo;
	}
	
	public String getUserToken()
	{
		return userToken;
	}
	
	public void setUserToken(String userToken)
	{
		this.userToken = userToken;
	}
}