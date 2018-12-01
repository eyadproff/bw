package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class CrimeCode extends JavaBean
{
	private int crimeEvent;
	private int crimeClass;
	private List<BiometricsExchangeDecision> criminalBioExchange;
	
	public CrimeCode(int crimeEvent, int crimeClass, List<BiometricsExchangeDecision> criminalBioExchange)
	{
		this.crimeEvent = crimeEvent;
		this.crimeClass = crimeClass;
		this.criminalBioExchange = criminalBioExchange;
	}
	
	public CrimeCode(int crimeEvent, int crimeClass)
	{
		this.crimeEvent = crimeEvent;
		this.crimeClass = crimeClass;
	}
	
	// copy constructor
	public CrimeCode(CrimeCode crimeCode)
	{
		this.crimeEvent = crimeCode.crimeEvent;
		this.crimeClass = crimeCode.crimeClass;
		this.criminalBioExchange = crimeCode.criminalBioExchange;
	}
	
	public int getCrimeEvent(){return crimeEvent;}
	public void setCrimeEvent(int crimeEvent){this.crimeEvent = crimeEvent;}
	
	public int getCrimeClass(){return crimeClass;}
	public void setCrimeClass(int crimeClass){this.crimeClass = crimeClass;}
	
	public List<BiometricsExchangeDecision> getCriminalBioExchange(){return criminalBioExchange;}
	public void setCriminalBioExchange(List<BiometricsExchangeDecision> criminalBioExchange)
																	{this.criminalBioExchange = criminalBioExchange;}
}