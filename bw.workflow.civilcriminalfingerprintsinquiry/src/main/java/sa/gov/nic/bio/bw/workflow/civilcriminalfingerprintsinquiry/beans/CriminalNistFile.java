package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;

public class CriminalNistFile extends JavaBean
{
	private String face;
	private List<Finger> fingerList;
	private List<Integer> missing;
	
	public CriminalNistFile(String face, List<Finger> fingerList, List<Integer> missing)
	{
		this.face = face;
		this.fingerList = fingerList;
		this.missing = missing;
	}
	
	public String getFace(){return face;}
	public void setFace(String face){this.face = face;}
	
	public List<Finger> getFingerList(){return fingerList;}
	public void setFingerList(List<Finger> fingerList){this.fingerList = fingerList;}
	
	public List<Integer> getMissing(){return missing;}
	public void setMissing(List<Integer> missing){this.missing = missing;}
}