package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import org.apache.commons.lang3.builder.CompareToBuilder;

public class CrimeType extends JavaBean implements Comparable<CrimeType>
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
	public int compareTo(CrimeType o)
	{
		return new CompareToBuilder().append(this.classCode, o.classCode)
						             .append(this.eventCode, o.eventCode)
						             .toComparison();
	}
}