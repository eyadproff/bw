module bw.workflow.commons
{
	exports sa.gov.nic.bio.bw.features.commons.ui;
	exports sa.gov.nic.bio.bw.features.commons.beans;
	requires bw.core;
	requires bw.lib.jasperreports;
	requires gson;
	requires java.desktop;
	requires biokit.library;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.features.commons.images;
	opens sa.gov.nic.bio.bw.features.commons.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.commons.workflow to bw.core;
	opens sa.gov.nic.bio.bw.features.commons.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.features.commons.lookups;
	exports sa.gov.nic.bio.bw.features.commons.webservice;
	exports sa.gov.nic.bio.bw.features.commons.controllers;
	exports sa.gov.nic.bio.bw.features.commons.workflow;
	exports sa.gov.nic.bio.bw.features.commons.tasks;
	exports sa.gov.nic.bio.bw.features.commons.utils;
}