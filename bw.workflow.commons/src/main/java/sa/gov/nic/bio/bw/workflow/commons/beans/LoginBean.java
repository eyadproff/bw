package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.UserInfo;

public class LoginBean extends JavaBean
{
	private UserInfo userInfo;
	private String userToken;
	
	public LoginBean(){}
	
	public UserInfo getUserInfo(){return userInfo;}
	public void setUserInfo(UserInfo userInfo){this.userInfo = userInfo;}
	public String getUserToken(){return userToken;}
	public void setUserToken(String userToken){this.userToken = userToken;}
}