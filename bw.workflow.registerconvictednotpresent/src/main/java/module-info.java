module bw.workflow.registerconvictednotpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.workflow.fingerprintcardidentification;
	requires bw.workflow.registerconvictedpresent;
	
	opens sa.gov.nic.bio.bw.features.registerconvictednotpresent to bw.core;
	opens sa.gov.nic.bio.bw.features.registerconvictednotpresent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.registerconvictednotpresent.controllers to javafx.fxml, bw.core;
}