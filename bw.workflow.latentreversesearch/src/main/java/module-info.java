module bw.workflow.latentreversesearch
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	requires javafx.web;
	
	opens sa.gov.nic.bio.bw.workflow.latentreversesearch.bundles;
	opens sa.gov.nic.bio.bw.workflow.latentreversesearch to bw.core;
	opens sa.gov.nic.bio.bw.workflow.latentreversesearch.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.latentreversesearch.beans to org.apache.commons.lang3, com.google.gson;
}