package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class ConvictedReportResponse extends JavaBean
{
	private long reportDate;
	private long reportNumber;
	private Long generalFileNumber;
	
	public long getReportDate(){return reportDate;}
	public void setReportDate(long reportDate){this.reportDate = reportDate;}
	
	public long getReportNumber(){return reportNumber;}
	public void setReportNumber(long reportNumber){this.reportNumber = reportNumber;}
	
	public Long getGeneralFileNumber(){return generalFileNumber;}
	public void setGeneralFileNumber(Long generalFileNumber){this.generalFileNumber = generalFileNumber;}
}