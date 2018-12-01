package sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class VisaApplicantEnrollmentResponse extends JavaBean
{
	private long applicantId;
	private long enrollmentDate;
	
	public long getApplicantId(){return applicantId;}
	public void setApplicantId(long applicantId){this.applicantId = applicantId;}
	
	public long getEnrollmentDate(){return enrollmentDate;}
	public void setEnrollmentDate(long enrollmentDate){this.enrollmentDate = enrollmentDate;}
}