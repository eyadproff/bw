package sa.gov.nic.bio.bw.features.registerconvictedpresent.webservice;

import java.util.Objects;

public class CrimeCode
{
	private int crimeEvent;
	private int crimeClass;
	
	public CrimeCode(int crimeEvent, int crimeClass)
	{
		this.crimeEvent = crimeEvent;
		this.crimeClass = crimeClass;
	}
	
	public int getCrimeEvent(){return crimeEvent;}
	public void setCrimeEvent(int crimeEvent){this.crimeEvent = crimeEvent;}
	
	public int getCrimeClass(){return crimeClass;}
	public void setCrimeClass(int crimeClass){this.crimeClass = crimeClass;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		CrimeCode crimeCode = (CrimeCode) o;
		return crimeEvent == crimeCode.crimeEvent && crimeClass == crimeCode.crimeClass;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(crimeEvent, crimeClass);
	}
	
	@Override
	public String toString()
	{
		return "CrimeCode{" + "crimeEvent=" + crimeEvent + ", crimeClass=" + crimeClass + '}';
	}
}