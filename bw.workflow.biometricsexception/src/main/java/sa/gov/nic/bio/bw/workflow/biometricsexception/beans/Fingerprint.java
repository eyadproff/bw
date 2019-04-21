package sa.gov.nic.bio.bw.workflow.biometricsexception.beans;

public class Fingerprint {

    private boolean missOrNot;
    private String Couse;
    private int Status;


    public boolean isMissOrNot() {
        return missOrNot;
    }

    public void setMissOrNot(boolean missOrNot) {
        this.missOrNot = missOrNot;
    }

    public String getCouse() {
        return Couse;
    }

    public void setCouse(String couse) {
        Couse = couse;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
