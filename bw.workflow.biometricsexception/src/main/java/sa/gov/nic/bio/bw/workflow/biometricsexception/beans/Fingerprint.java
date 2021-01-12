package sa.gov.nic.bio.bw.workflow.biometricsexception.beans;

public class Fingerprint {

    private boolean missOrNot = false;
    private Cause cause;
    private Integer months;
    private int position;
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

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
