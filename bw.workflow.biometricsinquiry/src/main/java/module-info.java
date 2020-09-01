module bw.workflow.biometricsinquiry
{

    requires bw.core;
    requires bw.workflow.commons;
    requires bio.commons;
    requires bw.workflow.irisinquiry;
    requires bw.workflow.searchbyfaceimage;
    requires com.google.gson;

    opens sa.gov.nic.bio.bw.workflow.biometricsinquiry.bundles;
    opens sa.gov.nic.bio.bw.workflow.biometricsinquiry to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsinquiry.fxml to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsinquiry.controllers to javafx.fxml, bw.core;
    //  opens sa.gov.nic.bio.bw.workflow.biometricsinquiry.tasks to bw.core;

    // opens sa.gov.nic.bio.bw.workflow.biometricsverification.beans to org.apache.commons.lang3, com.google.gson;
}
