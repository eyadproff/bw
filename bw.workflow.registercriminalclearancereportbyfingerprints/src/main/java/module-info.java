module bw.workflow.registercriminalclearancereportbyfingerprints {
    requires bw.core;
    requires bw.workflow.commons;
    requires biokit.library;
    requires bw.lib.jasperreports;
    requires bio.commons;
    requires com.google.gson;
    requires bw.workflow.registercriminalclearancereport;

    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereportbyfingerprints.bundles;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereportbyfingerprints to bw.core;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereportbyfingerprints.fxml to bw.core;
    opens sa.gov.nic.bio.bw.workflow.registercriminalclearancereportbyfingerprints.controllers to javafx.fxml, bw.core;
}