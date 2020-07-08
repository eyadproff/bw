module bw.workflow.biometricsexception {
    requires bw.core;
    requires bw.workflow.commons;
    requires bio.commons;


    opens sa.gov.nic.bio.bw.workflow.biometricsexception.bundles;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.fxml to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.tasks to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.lookups to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.beans to org.apache.commons.lang3;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.controllers to javafx.fxml, bw.core;
}