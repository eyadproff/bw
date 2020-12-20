module bw.workflow.criminalclearancereportinquiry {

    requires bw.core;
    requires bw.workflow.commons;
    requires bw.lib.jasperreports;
    requires bio.commons;
    requires com.google.gson;
    requires bw.workflow.registercriminalclearancereport;

    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.bundles;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry to bw.core;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.fxml to bw.core;
    //    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.lookups to bw.core;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.tasks to bw.core;
    //    opens sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans to bw.core, com.google.gson, org.apache.commons.lang3;
    opens sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.controllers to javafx.fxml, bw.core;
}