module bw.bw.workflow.criminalclearancereport {

    requires bw.core;
    requires bw.workflow.commons;
    requires bw.lib.jasperreports;
    requires bio.commons;
    requires biokit.library;
    requires com.google.gson;
    requires bw.workflow.civilcriminalfingerprintsinquiry;

    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.bundles;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport to bw.core;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.fxml to bw.core;
//    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.lookups to bw.core;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks to bw.core;
//    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans to bw.core, com.google.gson, org.apache.commons.lang3;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers to javafx.fxml, bw.core;

}