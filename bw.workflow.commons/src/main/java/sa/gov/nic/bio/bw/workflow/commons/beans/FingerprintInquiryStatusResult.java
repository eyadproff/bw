package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class FingerprintInquiryStatusResult extends JavaBean
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
}