module bw.workflow.fingerprintsinquiry
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bcl.utils;
	requires biokit.library;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.fingerprintsinquiry to bw.core;
	opens sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.webservice;
	exports sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.tasks;
	exports sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.beans;
	exports sa.gov.nic.bio.bw.workflow.fingerprintsinquiry.controllers;
}