package sa.gov.nic.bio.bw.workflow.commons.webservice;

import java.util.List;
import java.util.Objects;

public class FingerprintInquiryStatusResult
{
	public static final int STATUS_INQUIRY_PENDING = 2;
	public static final int STATUS_INQUIRY_NO_HIT = 91;
	public static final int STATUS_INQUIRY_HIT = 93;
	
	private int status;
	private Long civilHitBioId;
	private Long crimnalHitBioId;
	private List<Long> civilIdList;
	
	public int getStatus(){return status;}
	public void setStatus(int status){this.status = status;}
	
	public Long getCivilHitBioId(){return civilHitBioId;}
	public void setCivilHitBioId(Long civilHitBioId){this.civilHitBioId = civilHitBioId;}
	
	public Long getCrimnalHitBioId(){return crimnalHitBioId;}
	public void setCrimnalHitBioId(Long crimnalHitBioId){this.crimnalHitBioId = crimnalHitBioId;}
	
	public List<Long> getCivilIdList(){return civilIdList;}
	public void setCivilIdList(List<Long> civilIdList){this.civilIdList = civilIdList;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerprintInquiryStatusResult that = (FingerprintInquiryStatusResult) o;
		return status == that.status && Objects.equals(civilHitBioId, that.civilHitBioId) &&
			   Objects.equals(crimnalHitBioId, that.crimnalHitBioId) && Objects.equals(civilIdList, that.civilIdList);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(status, civilHitBioId, crimnalHitBioId, civilIdList);
	}
	
	@Override
	public String toString()
	{
		return "FingerprintInquiryStatusResult{" + "status=" + status + ", civilHitBioId=" + civilHitBioId +
			   ", crimnalHitBioId=" + crimnalHitBioId + ", civilIdList=" + civilIdList + '}';
	}
}