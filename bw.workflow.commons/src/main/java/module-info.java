module bw.workflow.commons
{
	exports sa.gov.nic.bio.bw.workflow.commons.ui;
	exports sa.gov.nic.bio.bw.workflow.commons.beans;
	requires bw.core;
	requires bw.lib.jasperreports;
	requires com.google.gson;
	requires java.desktop;
	requires biokit.library;
	requires bio.commons;
	requires org.apache.commons.lang3;
	
	opens sa.gov.nic.bio.bw.workflow.commons.fxml;
	opens sa.gov.nic.bio.bw.workflow.commons.bundles;
	opens sa.gov.nic.bio.bw.workflow.commons.tasks;
	opens sa.gov.nic.bio.bw.workflow.commons.controllers;
	opens sa.gov.nic.bio.bw.workflow.commons.beans;
	exports sa.gov.nic.bio.bw.workflow.commons.lookups;
	exports sa.gov.nic.bio.bw.workflow.commons.webservice;
	exports sa.gov.nic.bio.bw.workflow.commons.controllers;
	exports sa.gov.nic.bio.bw.workflow.commons.tasks;
	exports sa.gov.nic.bio.bw.workflow.commons.utils;
}