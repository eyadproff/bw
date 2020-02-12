package sa.gov.nic.bio.bw.workflow.biometricsexception.beans;

public class Fingerprint {

    private boolean missOrNot = false;
    private Cause cause;
    private Integer Status;
    private int Position;
    private Integer seqNum;
    private Boolean alreadyAdded = false;
    private String description;


    public boolean isMissOrNot() {
        return missOrNot;
    }

    public void setMissOrNot(boolean missOrNot) {
        this.missOrNot = missOrNot;
    }

    public Cause getCause() {
        return cause;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public Integer getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(Integer SeqNum) {
        this.seqNum = SeqNum;
    }

    public Boolean getAlreadyAdded() {
        return alreadyAdded;
    }

    public void setAlreadyAdded(Boolean alreadyAdded) {
        this.alreadyAdded = alreadyAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
