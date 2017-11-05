package sa.gov.nic.bio.bw.client.core.webservice;

import java.io.Serializable;

public class RefreshTokenBean implements Serializable
{
	private String userToken;
	
	public String getUserToken()
	{
		return userToken;
	}
	
	public void setUserToken(String userToken)
	{
		this.userToken = userToken;
	}
}