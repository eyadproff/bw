module bw.workflow.faceverification
{
	requires retrofit2;
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.workflow.searchbyfaceimage;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.faceverification to bw.core;
	opens sa.gov.nic.bio.bw.workflow.faceverification.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.faceverification.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.faceverification.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.faceverification.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.faceverification.controllers;
	exports sa.gov.nic.bio.bw.workflow.faceverification.beans;
}