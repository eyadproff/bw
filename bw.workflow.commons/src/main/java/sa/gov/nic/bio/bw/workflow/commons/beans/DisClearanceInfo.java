package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class DisClearanceInfo extends JavaBean
{
	private String clearanceNumber;
	private Long clearanceDate;
	private String clearanceDesc;
	
	public String getClearanceNumber(){return clearanceNumber;}
	public void setClearanceNumber(String clearanceNumber){this.clearanceNumber = clearanceNumber;}
	
	public Long getClearanceDate(){return clearanceDate;}
	public void setClearanceDate(Long clearanceDate){this.clearanceDate = clearanceDate;}
	
	public String getClearanceDesc(){return clearanceDesc;}
	public void setClearanceDesc(String clearanceDesc){this.clearanceDesc = clearanceDesc;}
}