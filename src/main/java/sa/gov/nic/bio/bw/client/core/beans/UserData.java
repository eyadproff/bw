package sa.gov.nic.bio.bw.client.core.beans;

import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.util.ArrayList;
import java.util.List;

public class UserData
{
	private LoginBean loginBean;
	private List<String> roles = new ArrayList<>();
	
	public UserData(){}
	
	public void addRole(String role)
	{
		roles.add(role);
	}
	
	public void addRoles(List<String> roles)
	{
		this.roles.addAll(roles);
	}
	
	public boolean hasRole(String role)
	{
		return roles.contains(role);
	}
	
	public LoginBean getLoginBean()
	{
		return loginBean;
	}
	
	public void setLoginBean(LoginBean loginBean)
	{
		this.loginBean = loginBean;
	}
	
	public void deleteLoginBean(){this.loginBean = null;}
	public void deleteRoles(){this.roles.clear();}
}