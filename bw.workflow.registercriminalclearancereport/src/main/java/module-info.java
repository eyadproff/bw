module bw.workflow.registercriminalclearancereport {

    requires bw.core;
    requires bw.workflow.commons;
    requires bw.lib.jasperreports;
    requires bio.commons;
    requires biokit.library;
    requires com.google.gson;
    requires bw.workflow.civilcriminalfingerprintsinquiry;

    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.bundles;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereport to bw.core;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.fxml to bw.core;
//    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.lookups to bw.core;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks to bw.core;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans to bw.core, com.google.gson, org.apache.commons.lang3;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers to javafx.fxml, bw.core;
    exports sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks;
    exports sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;
    exports sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans;
    exports sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.webservice;


}