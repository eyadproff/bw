module bw.workflow.biometricsidentification
{

    requires bw.core;
    requires bw.workflow.commons;
    requires bio.commons;
    requires bw.workflow.faceverification;
    requires bw.workflow.searchbyfaceimage;
    requires com.google.gson;

   // opens sa.gov.nic.bio.bw.workflow.biometricsverification.bundles;
    opens sa.gov.nic.bio.bw.workflow.biometricsidentification to bw.core;
   // opens sa.gov.nic.bio.bw.workflow.biometricsverification.fxml to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsidentification.tasks to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsidentification.controllers to javafx.fxml, bw.core;
   // opens sa.gov.nic.bio.bw.workflow.biometricsverification.beans to org.apache.commons.lang3, com.google.gson;
}