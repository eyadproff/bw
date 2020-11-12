package sa.gov.nic.bio.bw.workflow.commons.beans;

public class BioExclusion {
    private Integer seqNum;
    private Long samisId;
    private Integer casueId;
    private Integer bioType;
    private Integer position;
    private String description;
    private Long crDt;
    private Integer status;
    private Long createDate;
    private Long expireDate;
    private Long deleterId;
    private Long operatorId;
    private Integer month;

    public BioExclusion() {

    }

    public BioExclusion(Long samisId, Integer casueId, Integer bioType, Integer position, Long expireDate) {
        this.samisId = samisId;
        this.casueId = casueId;
        this.bioType = bioType;
        this.position = position;
        this.expireDate = expireDate;
    }

    public BioExclusion(Long samisId, Integer casueId, Integer bioType, Long expireDate) {
        this.samisId = samisId;
        this.casueId = casueId;
        this.bioType = bioType;
        this.expireDate = expireDate;

    }

    public Integer getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(Integer seqNum) {
        this.seqNum = seqNum;
    }

    public Long getSamisId() {
        return samisId;
    }

    public void setSamisId(Long samisId) {
        this.samisId = samisId;
    }

    public Integer getCasueId() {
        return casueId;
    }

    public void setCasueId(Integer casueId) {
        this.casueId = casueId;
    }

    public Integer getBioType() {
        return bioType;
    }

    public void setBioType(Integer bioType) {
        this.bioType = bioType;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCrDt() {
        return crDt;
    }

    public void setCrDt(Long crDt) {
        this.crDt = crDt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }


    public Long getDeleterId() {
        return deleterId;
    }

    public void setDeleterId(Long deleterId) {
        this.deleterId = deleterId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
