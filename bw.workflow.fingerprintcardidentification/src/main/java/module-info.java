module bw.workflow.fingerprintcardidentification
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bcl.utils;
	requires biokit.library;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.fingerprintcardidentification to bw.core;
	opens sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers;
}