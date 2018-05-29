package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow;

import java.util.Objects;

public class VisaApplicantEnrollmentResponse
{
	private long applicantId;
	private long enrollmentDate;
	
	public long getApplicantId(){return applicantId;}
	public void setApplicantId(long applicantId){this.applicantId = applicantId;}
	
	public long getEnrollmentDate(){return enrollmentDate;}
	public void setEnrollmentDate(long enrollmentDate){this.enrollmentDate = enrollmentDate;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		VisaApplicantEnrollmentResponse that = (VisaApplicantEnrollmentResponse) o;
		return applicantId == that.applicantId && enrollmentDate == that.enrollmentDate;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(applicantId, enrollmentDate);
	}
	
	@Override
	public String toString()
	{
		return "VisaApplicantEnrollmentResponse{" + "applicantId=" + applicantId + ", enrollmentDate=" +
			   enrollmentDate + '}';
	}
}