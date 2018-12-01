module bw.workflow.registerconvictedpresent
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	requires org.apache.commons.lang3;
	
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;
	exports sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;
}