module bw.workflow.criminalclearancereportanonymousperson {
    requires bw.core;
    requires bw.workflow.commons;
    requires biokit.library;
    requires bw.lib.jasperreports;
    requires bio.commons;
    requires com.google.gson;
    requires bw.workflow.criminalclearancereport;
//    requires bw.workflow.civilcriminalfingerprintsinquiry;

    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.bundles;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson to bw.core;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.fxml to bw.core;
    //    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.lookups to bw.core;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.tasks to bw.core;
    //    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.beans to bw.core, com.google.gson, org.apache.commons.lang3;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.controllers to javafx.fxml, bw.core;
}