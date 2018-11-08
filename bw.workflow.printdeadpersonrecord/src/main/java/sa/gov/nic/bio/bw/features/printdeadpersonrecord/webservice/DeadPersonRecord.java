package sa.gov.nic.bio.bw.features.printdeadpersonrecord.webservice;

import sa.gov.nic.bio.bw.features.commons.webservice.Finger;

import java.util.List;
import java.util.Objects;

public class DeadPersonRecord
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
	
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		DeadPersonRecord that = (DeadPersonRecord) o;
		return Objects.equals(samisId, that.samisId) && Objects.equals(enrollmentDate, that.enrollmentDate) &&
			   Objects.equals(barcodeNumber, that.barcodeNumber) && Objects.equals(subjFingers, that.subjFingers) &&
			   Objects.equals(subjMissingFingers, that.subjMissingFingers) &&
			   Objects.equals(subjFace, that.subjFace) && Objects.equals(operatorId, that.operatorId);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(samisId, enrollmentDate, barcodeNumber, subjFingers, subjMissingFingers, subjFace,
		                    operatorId);
	}
	
	@Override
	public String toString()
	{
		return "DeadPersonRecord{" + "samisId=" + samisId + ", enrollmentDate=" + enrollmentDate + ", barcodeNumber=" +
			   barcodeNumber + ", subjFingers=" + subjFingers + ", subjMissingFingers=" + subjMissingFingers +
			   ", subjFace='" + subjFace + '\'' + ", operatorId='" + operatorId + '\'' + '}';
	}
}