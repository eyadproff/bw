module bw.workflow.biometricsexception {
    requires bw.core;
    requires bw.workflow.commons;


    opens sa.gov.nic.bio.bw.workflow.biometricsexception.bundles;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.fxml to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsexception.controllers to javafx.fxml, bw.core;
}