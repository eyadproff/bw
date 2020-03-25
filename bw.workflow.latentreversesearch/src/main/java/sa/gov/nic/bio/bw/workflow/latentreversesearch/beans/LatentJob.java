package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class LatentJob extends JavaBean
{
	private Long jobId;
	private Long tcn;
	private Long bioId;
	private Long samisId;
	private Long locationId;
	private Long operatorId;
	private Long createTime;
	private Long updateTime;
	private String status;
	
	public Long getJobId(){return jobId;}
	public void setJobId(Long jobId){this.jobId = jobId;}
	
	public Long getTcn(){return tcn;}
	public void setTcn(Long tcn){this.tcn = tcn;}
	
	public Long getBioId(){return bioId;}
	public void setBioId(Long bioId){this.bioId = bioId;}
	
	public Long getSamisId(){return samisId;}
	public void setSamisId(Long samisId){this.samisId = samisId;}
	
	public Long getLocationId(){return locationId;}
	public void setLocationId(Long locationId){this.locationId = locationId;}
	
	public Long getOperatorId(){return operatorId;}
	public void setOperatorId(Long operatorId){this.operatorId = operatorId;}
	
	public Long getCreateTime(){return createTime;}
	public void setCreateTime(Long createTime){this.createTime = createTime;}
	
	public Long getUpdateTime(){return updateTime;}
	public void setUpdateTime(Long updateTime){this.updateTime = updateTime;}
	
	public String getStatus(){return status;}
	public void setStatus(String status){this.status = status;}
}