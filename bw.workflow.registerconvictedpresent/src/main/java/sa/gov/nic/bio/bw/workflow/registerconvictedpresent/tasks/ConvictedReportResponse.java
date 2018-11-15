package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import java.util.Objects;

public class ConvictedReportResponse
{
	private long reportDate;
	private long reportNumber;
	
	public long getReportDate(){return reportDate;}
	public void setReportDate(long reportDate){this.reportDate = reportDate;}
	
	public long getReportNumber(){return reportNumber;}
	public void setReportNumber(long reportNumber){this.reportNumber = reportNumber;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		ConvictedReportResponse that = (ConvictedReportResponse) o;
		return reportDate == that.reportDate && reportNumber == that.reportNumber;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(reportDate, reportNumber);
	}
	
	@Override
	public String toString()
	{
		return "ConvictedReportResponse{" + "reportDate=" + reportDate + ", reportNumber=" + reportNumber + '}';
	}
}