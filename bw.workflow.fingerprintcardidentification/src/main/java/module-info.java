module bw.workflow.fingerprintcardidentification
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bcl.utils;
	requires biokit.library;
	
	opens sa.gov.nic.bio.bw.features.fingerprintcardidentification to bw.core;
	opens sa.gov.nic.bio.bw.features.fingerprintcardidentification.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.fingerprintcardidentification.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.features.fingerprintcardidentification.controllers;
}