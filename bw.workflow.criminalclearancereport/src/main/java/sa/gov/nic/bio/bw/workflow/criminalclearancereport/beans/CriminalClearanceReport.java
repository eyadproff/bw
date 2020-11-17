package sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans;

public class CriminalClearanceReport {

    private Long samisId;
    private String requestedName;
    private String reason;
    private Long reportNumber;

    public CriminalClearanceReport(Long samisId, String requestedName, String reason) {
        this.samisId = samisId;
        this.requestedName = requestedName;
        this.reason = reason;
    }

    public Long getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(Long reportNumber) {
        this.reportNumber = reportNumber;
    }

    public Long getSamisId() {
        return samisId;
    }

    public void setSamisId(Long samisId) {
        this.samisId = samisId;
    }

    public String getRequestedName() {
        return requestedName;
    }

    public void setRequestedName(String requestedName) {
        this.requestedName = requestedName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
