module bw.workflow.registerconvictedpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.features.registerconvictedpresent to bw.core;
	opens sa.gov.nic.bio.bw.features.registerconvictedpresent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.features.registerconvictedpresent.lookups;
	exports sa.gov.nic.bio.bw.features.registerconvictedpresent.webservice;
	exports sa.gov.nic.bio.bw.features.registerconvictedpresent.tasks;
	exports sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers;
	exports sa.gov.nic.bio.bw.features.registerconvictedpresent.workflow;
}