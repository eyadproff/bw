package sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans;

import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;

public class CriminalClearanceReport {

    private Long samisId;
    private String requestedName;
    private String reason;
    private Long reportNumber;
    //    LocalDate crDt;
    //    LocalDate expireDate;
    private String face;
    private List<Finger> fingers;

    public CriminalClearanceReport(Long samisId, String requestedName, String reason, String face, List<Finger> fingers) {
        this.samisId = samisId;
        this.requestedName = requestedName;
        this.reason = reason;
        this.reportNumber = reportNumber;
        this.face = face;
        this.fingers = fingers;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public List<Finger> getFingers() {
        return fingers;
    }

    public void setFingers(List<Finger> fingers) {
        this.fingers = fingers;
    }


    //    public LocalDate getCrDt() {
    //        return crDt;
    //    }
    //
    //    public void setCrDt(LocalDate crDt) {
    //        this.crDt = crDt;
    //    }
    //
    //    public LocalDate getExpireDate() {
    //        return expireDate;
    //    }
    //
    //    public void setExpireDate(LocalDate expireDate) {
    //        this.expireDate = expireDate;
    //    }

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
