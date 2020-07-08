package sa.gov.nic.bio.bw.workflow.biometricsexception.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class SubmissionAndDeletionResponse extends JavaBean{

        private Long tcn;

        public Long getTcn(){return tcn;}
        public void setTcn(Long tcn){this.tcn = tcn;}

}
