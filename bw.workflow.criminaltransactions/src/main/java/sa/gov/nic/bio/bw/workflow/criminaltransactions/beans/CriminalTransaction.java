package sa.gov.nic.bio.bw.workflow.criminaltransactions.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class CriminalTransaction extends JavaBean
{
	private int sequence;
	private Long tcn;
	private Integer eventType;
	private String criminalId;
	private String delinkSamisId;
	private String criminalReportId;
	private Long operatorId;
	private Boolean successful;
	private String locationId;
	private String apiRspCode;
	private String apiHttpCode;
	private Long timestamp;
	private String criminalFingerprintSource;
	private String criminalWorkflowSource;
	
	public int getSequence(){return sequence;}
	public void setSequence(int sequence){this.sequence = sequence;}
	
	public Long getTcn(){return tcn;}
	public void setTcn(Long tcn){this.tcn = tcn;}
	
	public Integer getEventType(){return eventType;}
	public void setEventType(Integer eventType){this.eventType = eventType;}
	
	public String getCriminalId(){return criminalId;}
	public void setCriminalId(String criminalId){this.criminalId = criminalId;}
	
	public String getDelinkSamisId(){return delinkSamisId;}
	public void setDelinkSamisId(String delinkSamisId){this.delinkSamisId = delinkSamisId;}
	
	public String getCriminalReportId(){return criminalReportId;}
	public void setCriminalReportId(String criminalReportId){this.criminalReportId = criminalReportId;}
	
	public Long getOperatorId(){return operatorId;}
	public void setOperatorId(Long operatorId){this.operatorId = operatorId;}
	
	public Boolean getSuccessful(){return successful;}
	public void setSuccessful(Boolean successful){this.successful = successful;}
	
	public String getLocationId(){return locationId;}
	public void setLocationId(String locationId){this.locationId = locationId;}
	
	public String getApiRspCode(){return apiRspCode;}
	public void setApiRspCode(String apiRspCode){this.apiRspCode = apiRspCode;}
	
	public String getApiHttpCode(){return apiHttpCode;}
	public void setApiHttpCode(String apiHttpCode){this.apiHttpCode = apiHttpCode;}
	
	public Long getTimestamp(){return timestamp;}
	public void setTimestamp(Long timestamp){this.timestamp = timestamp;}
	
	public String getCriminalFingerprintSource(){return criminalFingerprintSource;}
	public void setCriminalFingerprintSource(String criminalFingerprintSource){this.criminalFingerprintSource = criminalFingerprintSource;}
	
	public String getCriminalWorkflowSource(){return criminalWorkflowSource;}
	public void setCriminalWorkflowSource(String criminalWorkflowSource){this.criminalWorkflowSource = criminalWorkflowSource;}
}