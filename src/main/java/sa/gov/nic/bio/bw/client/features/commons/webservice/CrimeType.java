package sa.gov.nic.bio.bw.client.features.commons.webservice;

import java.util.Objects;

public class CrimeType
{
	private int classCode;
	private int eventCode;
	private String classDesc;
	private String eventDesc;
	
	public CrimeType(int classCode, int eventCode, String classDesc, String eventDesc)
	{
		this.classCode = classCode;
		this.eventCode = eventCode;
		this.classDesc = classDesc;
		this.eventDesc = eventDesc;
	}
	
	public int getClassCode(){return classCode;}
	public void setClassCode(int classCode){this.classCode = classCode;}
	
	public int getEventCode(){return eventCode;}
	public void setEventCode(int eventCode){this.eventCode = eventCode;}
	
	public String getClassDesc(){return classDesc;}
	public void setClassDesc(String classDesc){this.classDesc = classDesc;}
	
	public String getEventDesc(){return eventDesc;}
	public void setEventDesc(String eventDesc){this.eventDesc = eventDesc;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		CrimeType crimeType = (CrimeType) o;
		return classCode == crimeType.classCode && eventCode == crimeType.eventCode &&
			   Objects.equals(classDesc, crimeType.classDesc) && Objects.equals(eventDesc, crimeType.eventDesc);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(classCode, eventCode, classDesc, eventDesc);
	}
	
	@Override
	public String toString()
	{
		return "CrimeType{" + "classCode=" + classCode + ", eventCode=" + eventCode + ", classDesc='" + classDesc +
			   '\'' + ", eventDesc='" + eventDesc + '\'' + '}';
	}
}