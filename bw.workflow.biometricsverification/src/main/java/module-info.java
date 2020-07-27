

module bw.workflow.biometricsverification
{
    requires bw.core;
    requires bw.workflow.commons;
    requires bio.commons;
    requires bw.workflow.faceverification;
    requires bw.workflow.searchbyfaceimage;



    opens sa.gov.nic.bio.bw.workflow.biometricsverification.bundles;
    opens sa.gov.nic.bio.bw.workflow.biometricsverification to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsverification.fxml to bw.core;
  //  opens sa.gov.nic.bio.bw.workflow.biometricsverification.tasks to bw.core;
    opens sa.gov.nic.bio.bw.workflow.biometricsverification.controllers to javafx.fxml, bw.core;
   // opens sa.gov.nic.bio.bw.workflow.biometricsverification.beans to org.apache.commons.lang3, com.google.gson;

}