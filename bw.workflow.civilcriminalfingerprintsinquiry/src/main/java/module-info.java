module bw.workflow.civilcriminalfingerprintsinquiry
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bcl.utils;
	requires biokit.library;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.bundles;
	opens sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry to bw.core;
	opens sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.webservice;
	exports sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks;
	exports sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.beans;
	exports sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers;
}