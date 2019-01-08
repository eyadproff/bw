package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class WantedInfo extends JavaBean
{
	private String action;
	private String source;
	
	public String getAction(){return action;}
	public void setAction(String action){this.action = action;}
	
	public String getSource(){return source;}
	public void setSource(String source){this.source = source;}
}