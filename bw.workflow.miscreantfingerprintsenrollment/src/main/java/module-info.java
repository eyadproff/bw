module bw.workflow.miscreantfingerprintsenrollment
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	requires biokit.library;
	
	opens sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.bundles;
	opens sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment to bw.core;
	opens sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.fxml to bw.core;
	//opens sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers to javafx.fxml, bw.core;
	//opens sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans to org.apache.commons.lang3, com.google.gson;
	exports sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers;
}