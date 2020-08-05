package sa.gov.nic.bio.bw.workflow.biometricsverification.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

public class MatchingResponse extends JavaBean {
    private boolean matched;
    private PersonInfo personInfo;

    public MatchingResponse(boolean matched, PersonInfo personInfo) {
        this.matched = matched;
        this.personInfo = personInfo;
    }

    public boolean isMatched() {return matched;}

    public void setMatched(boolean matched) {this.matched = matched;}

    public PersonInfo getPersonInfo() {return personInfo;}

    public void setPersonInfo(PersonInfo personInfo) {this.personInfo = personInfo;}
}