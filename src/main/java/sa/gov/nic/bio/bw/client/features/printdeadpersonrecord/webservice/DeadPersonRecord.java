package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice;

import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;

import java.util.List;
import java.util.Objects;

public class DeadPersonRecord
{
	private Long samisId;
	private List<Finger> fingerprints;
	private List<Integer> missingFingerprints;
	private String faceBase64;
	private long enrollerId;
	private long enrollmentTimestamp;
	
	public DeadPersonRecord(Long samisId, List<Finger> fingerprints, List<Integer> missingFingerprints,
	                        String faceBase64, long enrollerId, long enrollmentTimestamp)
	{
		this.samisId = samisId;
		this.fingerprints = fingerprints;
		this.missingFingerprints = missingFingerprints;
		this.faceBase64 = faceBase64;
		this.enrollerId = enrollerId;
		this.enrollmentTimestamp = enrollmentTimestamp;
	}
	
	public Long getSamisId(){return samisId;}
	public void setSamisId(Long samisId){this.samisId = samisId;}
	
	public List<Finger> getFingerprints(){return fingerprints;}
	public void setFingerprints(List<Finger> fingerprints){this.fingerprints = fingerprints;}
	
	public List<Integer> getMissingFingerprints(){return missingFingerprints;}
	public void setMissingFingerprints(List<Integer> missingFingerprints)
																	{this.missingFingerprints = missingFingerprints;}
	
	public String getFaceBase64(){return faceBase64;}
	public void setFaceBase64(String faceBase64){this.faceBase64 = faceBase64;}
	
	public long getEnrollerId(){return enrollerId;}
	public void setEnrollerId(long enrollerId){this.enrollerId = enrollerId;}
	
	public long getEnrollmentTimestamp(){return enrollmentTimestamp;}
	public void setEnrollmentTimestamp(long enrollmentTimestamp){this.enrollmentTimestamp = enrollmentTimestamp;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		DeadPersonRecord that = (DeadPersonRecord) o;
		return enrollerId == that.enrollerId && enrollmentTimestamp == that.enrollmentTimestamp &&
			   Objects.equals(samisId, that.samisId) && Objects.equals(fingerprints, that.fingerprints) &&
			   Objects.equals(missingFingerprints, that.missingFingerprints) &&
			   Objects.equals(faceBase64, that.faceBase64);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(samisId, fingerprints, missingFingerprints, faceBase64, enrollerId, enrollmentTimestamp);
	}
	
	@Override
	public String toString()
	{
		return "DeadPersonRecord{" + "samisId=" + samisId + ", fingerprints=" + fingerprints +
			   ", missingFingerprints=" + missingFingerprints + ", faceBase64='" + faceBase64 + '\'' +
			   ", enrollerId=" + enrollerId + ", enrollmentTimestamp=" + enrollmentTimestamp + '}';
	}
}