module bw.workflow.visaapplicantsenrollment
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires biokit.library;
	requires barbecue;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.bundles;
	opens sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment to bw.core;
	opens sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.lookups to bw.core;
	opens sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.visaapplicantsenrollment.controllers;
}