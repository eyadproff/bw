package sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

public class FingerprintInquiryStatusResult
{
	public static final int STATUS_INQUIRY_PENDING = 2;
	public static final int STATUS_INQUIRY_NO_HIT = 91;
	public static final int STATUS_INQUIRY_HIT = 93;
	
	private Map<Long,PersonInfo> personInfo;;
	@SerializedName("l_candidate")
	private int lCandidate;
	@SerializedName("x_candidate")
	private int xCandidate;
	private int status;
	
	public Map<Long, PersonInfo> getPersonInfo(){return personInfo;}
	public void setPersonInfo(Map<Long, PersonInfo> personInfo){this.personInfo = personInfo;}
	
	public int getLCandidate(){return lCandidate;}
	public void setLCandidate(int lCandidate){this.lCandidate = lCandidate;}
	
	public int getXCandidate(){return xCandidate;}
	public void setXCandidate(int xCandidate){this.xCandidate = xCandidate;}
	
	public int getStatus(){return status;}
	public void setStatus(int status){this.status = status;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FingerprintInquiryStatusResult that = (FingerprintInquiryStatusResult) o;
		return lCandidate == that.lCandidate && xCandidate == that.xCandidate && status == that.status &&
			   Objects.equals(personInfo, that.personInfo);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(personInfo, lCandidate, xCandidate, status);
	}
	
	@Override
	public String toString()
	{
		return "FingerprintInquiryStatusResult{" + "personInfo=" + personInfo + ", lCandidate=" + lCandidate +
			   ", xCandidate=" + xCandidate + ", status=" + status + '}';
	}
}