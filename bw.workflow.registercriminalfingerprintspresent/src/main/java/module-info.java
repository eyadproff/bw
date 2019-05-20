module bw.workflow.registercriminalfingerprintspresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.lib.jasperreports;
	requires bio.commons;
	requires biokit.library;
	requires bw.workflow.registerconvictedpresent;
	
	opens sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.bundles;
	opens sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers;
}