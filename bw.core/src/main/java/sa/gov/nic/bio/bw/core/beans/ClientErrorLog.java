package sa.gov.nic.bio.bw.core.beans;

public class ClientErrorLog extends JavaBean
{
    private String clientAppCode;
    private Long clientWorkflowCode;
    private String screenshot1Base64;
    private String screenshot2Base64;
    private String screenshot3Base64;
    private String errorCode;
    private String stacktrace;
    private String errorDetails;
    private String nicLocation;
    private String latitude;
    private String longitude;
    private String clientVersion;
    private String clientOS;
    private Long clientWorkflowTcn;
    private String operatorId;
    private String operatorUsername;
    
    public String getClientAppCode(){return clientAppCode;}
    public void setClientAppCode(String clientAppCode){this.clientAppCode = clientAppCode;}
    
    public Long getClientWorkflowCode(){return clientWorkflowCode;}
    public void setClientWorkflowCode(Long clientWorkflowCode){this.clientWorkflowCode = clientWorkflowCode;}
    
    public String getScreenshot1Base64(){return screenshot1Base64;}
    public void setScreenshot1Base64(String screenshot1Base64){this.screenshot1Base64 = screenshot1Base64;}
    
    public String getScreenshot2Base64(){return screenshot2Base64;}
    public void setScreenshot2Base64(String screenshot2Base64){this.screenshot2Base64 = screenshot2Base64;}
    
    public String getScreenshot3Base64(){return screenshot3Base64;}
    public void setScreenshot3Base64(String screenshot3Base64){this.screenshot3Base64 = screenshot3Base64;}
    
    public String getErrorCode(){return errorCode;}
    public void setErrorCode(String errorCode){this.errorCode = errorCode;}
    
    public String getStacktrace(){return stacktrace;}
    public void setStacktrace(String stacktrace){this.stacktrace = stacktrace;}
    
    public String getErrorDetails(){return errorDetails;}
    public void setErrorDetails(String errorDetails){this.errorDetails = errorDetails;}
    
    public String getNicLocation(){return nicLocation;}
    public void setNicLocation(String nicLocation){this.nicLocation = nicLocation;}
    
    public String getLatitude(){return latitude;}
    public void setLatitude(String latitude){this.latitude = latitude;}
    
    public String getLongitude(){return longitude;}
    public void setLongitude(String longitude){this.longitude = longitude;}
    
    public String getClientVersion(){return clientVersion;}
    public void setClientVersion(String clientVersion){this.clientVersion = clientVersion;}
    
    public String getClientOS(){return clientOS;}
    public void setClientOS(String clientOS){this.clientOS = clientOS;}
    
    public Long getClientWorkflowTcn(){return clientWorkflowTcn;}
    public void setClientWorkflowTcn(Long clientWorkflowTcn){this.clientWorkflowTcn = clientWorkflowTcn;}
    
    public String getOperatorId(){return operatorId;}
    public void setOperatorId(String operatorId){this.operatorId = operatorId;}
    
    public String getOperatorUsername(){return operatorUsername;}
    public void setOperatorUsername(String operatorUsername){this.operatorUsername = operatorUsername;}
}