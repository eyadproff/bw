module bw.workflow.registerconvictednotpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.workflow.fingerprintcardidentification;
	requires bw.workflow.registerconvictedpresent;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.registerconvictednotpresent to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.webservice;
	exports sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.tasks;
	exports sa.gov.nic.bio.bw.workflow.registerconvictednotpresent.beans;
}