package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class NistFileResponse extends JavaBean
{
    private String nist;

    public String getNist(){return nist;}
    public void setNist(String nist){this.nist = nist;}
}
