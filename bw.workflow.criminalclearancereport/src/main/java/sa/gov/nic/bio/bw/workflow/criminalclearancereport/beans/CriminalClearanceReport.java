package sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.Date;
import java.util.List;

public class CriminalClearanceReport extends JavaBean {

    private Integer sequence; // used in TableView
    private Long reportNumber;
    private Long samisId;
    private String passportNumber;
    private Name fullName;
    private Integer nationality;
    private String requestedName;
    private String reason;
    private String face;
    private List<Finger> fingers;
    private Date createDate;
    private Date expireDate;
    private String issueDateH;
    private String expireDateH;
    private Date dateOfBirth;
    private String dateOfBirthHijri;

    public CriminalClearanceReport(Long reportNumber, Long samisId, String passportNumber, Name fullName, Integer nationality, String requestedName, String reason, String face,
            List<Finger> fingers, Date createDate, Date expireDate, String issueDateH, String expireDateH, Date dateOfBirth, String dateOfBirthHijri) {
        this.reportNumber = reportNumber;
        this.samisId = samisId;
        this.passportNumber = passportNumber;
        this.fullName = fullName;
        this.nationality = nationality;
        this.requestedName = requestedName;
        this.reason = reason;
        this.face = face;
        this.fingers = fingers;
        this.createDate = createDate;
        this.expireDate = expireDate;
        this.issueDateH = issueDateH;
        this.expireDateH = expireDateH;
        this.dateOfBirth = dateOfBirth;
        this.dateOfBirthHijri = dateOfBirthHijri;
    }

    public CriminalClearanceReport(Name fullName, String requestedName, String reason) {
        this.fullName = fullName;
        this.requestedName = requestedName;
        this.reason = reason;
    }

    public CriminalClearanceReport(Long samisId, Name fullName, Integer nationality, String face,
            List<Finger> fingers, Date dateOfBirth, String dateOfBirthHijri, String requestedName, String reason) {
        this.samisId = samisId;
        this.fullName = fullName;
        this.nationality = nationality;
        this.requestedName = requestedName;
        this.reason = reason;
        this.face = face;
        this.fingers = fingers;
        this.dateOfBirth = dateOfBirth;
        this.dateOfBirthHijri = dateOfBirthHijri;
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public Name getFullName() {
        return fullName;
    }

    public void setFullName(Name fullName) {
        this.fullName = fullName;
    }

    public Integer getNationality() {
        return nationality;
    }

    public void setNationality(Integer nationality) {
        this.nationality = nationality;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getIssueDateH() {
        return issueDateH;
    }

    public void setIssueDateH(String issueDateH) {
        this.issueDateH = issueDateH;
    }

    public String getExpireDateH() {
        return expireDateH;
    }

    public void setExpireDateH(String expireDateH) {
        this.expireDateH = expireDateH;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirthHijri() {
        return dateOfBirthHijri;
    }

    public void setDateOfBirthHijri(String dateOfBirthHijri) {
        this.dateOfBirthHijri = dateOfBirthHijri;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
