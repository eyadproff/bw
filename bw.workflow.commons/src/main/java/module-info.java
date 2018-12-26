module bw.workflow.commons
{
	exports sa.gov.nic.bio.bw.workflow.commons.ui;
	exports sa.gov.nic.bio.bw.workflow.commons.beans;
	requires bw.core;
	requires bw.lib.jasperreports;
	requires gson;
	requires java.desktop;
	requires biokit.library;
	requires bio.commons;
	requires org.apache.commons.lang3;
	
	opens sa.gov.nic.bio.bw.workflow.commons.images;
	opens sa.gov.nic.bio.bw.workflow.commons.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.commons.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.commons.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.commons.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.commons.lookups;
	exports sa.gov.nic.bio.bw.workflow.commons.webservice;
	exports sa.gov.nic.bio.bw.workflow.commons.controllers;
	exports sa.gov.nic.bio.bw.workflow.commons.tasks;
	exports sa.gov.nic.bio.bw.workflow.commons.utils;
}