package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice;

import java.util.Objects;

public class FingerprintInquiryStatusResult
{
	public static final int STATUS_INQUIRY_PENDING = 2;
	public static final int STATUS_INQUIRY_NO_HIT = 91;
	public static final int STATUS_INQUIRY_HIT = 93;
	
	private int status;
	private long civilHitBioId;
	private long crimnalHitBioId;
	private long samisId;
	private PersonInfo personInfo;
	
	public int getStatus(){return status;}
	public void setStatus(int status){this.status = status;}
	
	public long getCivilHitBioId(){return civilHitBioId;}
	public void setCivilHitBioId(long civilHitBioId){this.civilHitBioId = civilHitBioId;}
	
	public long getCrimnalHitBioId(){return crimnalHitBioId;}
	public void setCrimnalHitBioId(long crimnalHitBioId){this.crimnalHitBioId = crimnalHitBioId;}
	
	public long getSamisId(){return samisId;}
	public void setSamisId(long samisId){this.samisId = samisId;}
	
	public PersonInfo getPersonInfo(){return personInfo;}
	public void setPersonInfo(PersonInfo personInfo){this.personInfo = personInfo;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerprintInquiryStatusResult result = (FingerprintInquiryStatusResult) o;
		return status == result.status && civilHitBioId == result.civilHitBioId &&
			   crimnalHitBioId == result.crimnalHitBioId && samisId == result.samisId &&
			   Objects.equals(personInfo, result.personInfo);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(status, civilHitBioId, crimnalHitBioId, samisId, personInfo);
	}
	
	@Override
	public String toString()
	{
		return "FingerprintInquiryStatusResult{" + "status=" + status + ", civilHitBioId=" + civilHitBioId +
			   ", crimnalHitBioId=" + crimnalHitBioId + ", samisId=" + samisId + ", personInfo=" + personInfo + '}';
	}
}