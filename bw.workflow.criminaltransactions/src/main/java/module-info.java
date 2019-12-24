module bw.workflow.criminaltransactions
{
	requires bw.core;
	requires bw.workflow.registerconvictedpresent;
	requires bw.workflow.commons;
	requires bw.lib.jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions.bundles;
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions to bw.core;
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions.lookups to bw.core;
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions.beans to bw.core, com.google.gson, org.apache.commons.lang3;
	opens sa.gov.nic.bio.bw.workflow.criminaltransactions.controllers to javafx.fxml, bw.core;
}