package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class WatchListRecord extends JavaBean {

    private int actionCode;
    private String actionMessage;
    private long samisId;
    private int sourceCode;
    private long sourceId;
    private String sourceMessage;

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public void setActionMessage(String actionMessage) {
        this.actionMessage = actionMessage;
    }

    public long getSamisId() {
        return samisId;
    }

    public void setSamisId(long samisId) {
        this.samisId = samisId;
    }

    public int getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(int sourceCode) {
        this.sourceCode = sourceCode;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceMessage() {
        return sourceMessage;
    }

    public void setSourceMessage(String sourceMessage) {
        this.sourceMessage = sourceMessage;
    }
}
