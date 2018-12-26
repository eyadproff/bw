package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class BiometricsExchangeCrimeType extends JavaBean
{
	private Integer orgPartyCode;
	private Integer crimeClassCode;
	private Integer crimeEventCode;
	
	public BiometricsExchangeCrimeType(Integer orgPartyCode, Integer crimeClassCode, Integer crimeEventCode)
	{
		this.orgPartyCode = orgPartyCode;
		this.crimeClassCode = crimeClassCode;
		this.crimeEventCode = crimeEventCode;
	}
	
	public Integer getOrgPartyCode(){return orgPartyCode;}
	public void setOrgPartyCode(Integer orgPartyCode){this.orgPartyCode = orgPartyCode;}
	
	public Integer getCrimeClassCode(){return crimeClassCode;}
	public void setCrimeClassCode(Integer crimeClassCode){this.crimeClassCode = crimeClassCode;}
	
	public Integer getCrimeEventCode(){return crimeEventCode;}
	public void setCrimeEventCode(Integer crimeEventCode){this.crimeEventCode = crimeEventCode;}
}