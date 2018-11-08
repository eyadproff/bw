package sa.gov.nic.bio.bw.core.webservice;

import java.io.Serializable;
import java.util.Objects;

public class RefreshTokenBean implements Serializable
{
	private String userToken;
	
	public String getUserToken(){return userToken;}
	public void setUserToken(String userToken){this.userToken = userToken;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		RefreshTokenBean that = (RefreshTokenBean) o;
		return Objects.equals(userToken, that.userToken);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(userToken);
	}
	
	@Override
	public String toString()
	{
		return "RefreshTokenBean{" + "userToken='" + userToken + '\'' + '}';
	}
}