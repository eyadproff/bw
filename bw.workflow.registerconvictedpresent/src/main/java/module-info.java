module bw.workflow.registerconvictedpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;
}