package sa.gov.nic.bio.bw.features.commons.beans;

import sa.gov.nic.bio.bw.core.beans.UserInfo;

import java.io.Serializable;

public class LoginBean implements Serializable
{
	private UserInfo userInfo;
	private String userToken;
	
	public LoginBean(){}
	
	public UserInfo getUserInfo(){return userInfo;}
	public void setUserInfo(UserInfo userInfo){this.userInfo = userInfo;}
	public String getUserToken(){return userToken;}
	public void setUserToken(String userToken){this.userToken = userToken;}
	
	@Override
	public String toString()
	{
		return "LoginBean{" + "userInfo=" + userInfo + ", userToken='" + userToken + '\'' + '}';
	}
}