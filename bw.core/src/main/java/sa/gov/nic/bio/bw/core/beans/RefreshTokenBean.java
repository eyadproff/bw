package sa.gov.nic.bio.bw.core.beans;

public class RefreshTokenBean  extends JavaBean
{
	private String userToken;
	
	public String getUserToken(){return userToken;}
	public void setUserToken(String userToken){this.userToken = userToken;}
}