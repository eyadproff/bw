package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;

public class CriminalFingerprint extends JavaBean
{
	private List<Finger> fingers;
	private List<Integer> missing;
	
	public CriminalFingerprint(List<Finger> fingers, List<Integer> missing)
	{
		this.fingers = fingers;
		this.missing = missing;
	}
	
	public List<Finger> getFingers(){return fingers;}
	public void setFingers(List<Finger> fingers){this.fingers = fingers;}
	
	public List<Integer> getMissing(){return missing;}
	public void setMissing(List<Integer> missing){this.missing = missing;}
}