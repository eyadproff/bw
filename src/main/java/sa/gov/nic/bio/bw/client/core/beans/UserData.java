package sa.gov.nic.bio.bw.client.core.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserData
{
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
}