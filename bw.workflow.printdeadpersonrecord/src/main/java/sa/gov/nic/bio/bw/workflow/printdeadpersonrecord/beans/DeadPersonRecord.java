package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;

public class DeadPersonRecord extends JavaBean
{
	private Long samisId;
	private Long enrollmentDate;
	private Long barcodeNumber;
	private List<Finger> subjFingers;
	private List<Integer> subjMissingFingers;
	private String subjFace;
	private String operatorId;
	
	public DeadPersonRecord(Long samisId, Long enrollmentDate, Long barcodeNumber, List<Finger> subjFingers,
	                        List<Integer> subjMissingFingers, String subjFace, String operatorId)
	{
		this.samisId = samisId;
		this.enrollmentDate = enrollmentDate;
		this.barcodeNumber = barcodeNumber;
		this.subjFingers = subjFingers;
		this.subjMissingFingers = subjMissingFingers;
		this.subjFace = subjFace;
		this.operatorId = operatorId;
	}
	
	public Long getSamisId(){return samisId;}
	public void setSamisId(Long samisId){this.samisId = samisId;}
	
	public Long getEnrollmentDate(){return enrollmentDate;}
	public void setEnrollmentDate(Long enrollmentDate){this.enrollmentDate = enrollmentDate;}
	
	public Long getBarcodeNumber(){return barcodeNumber;}
	public void setBarcodeNumber(Long barcodeNumber){this.barcodeNumber = barcodeNumber;}
	
	public List<Finger> getSubjFingers(){return subjFingers;}
	public void setSubjFingers(List<Finger> subjFingers){this.subjFingers = subjFingers;}
	
	public List<Integer> getSubjMissingFingers(){return subjMissingFingers;}
	public void setSubjMissingFingers(List<Integer> subjMissingFingers){this.subjMissingFingers = subjMissingFingers;}
	
	public String getSubjFace(){return subjFace;}
	public void setSubjFace(String subjFace){this.subjFace = subjFace;}
	
	public String getOperatorId(){return operatorId;}
	public void setOperatorId(String operatorId){this.operatorId = operatorId;}
}