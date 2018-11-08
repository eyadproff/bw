module bw.workflow.visaapplicantsenrollment
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires biokit.library;
	requires barbecue;
	
	opens sa.gov.nic.bio.bw.features.visaapplicantsenrollment to bw.core;
	opens sa.gov.nic.bio.bw.features.visaapplicantsenrollment.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.visaapplicantsenrollment.lookups to bw.core;
	opens sa.gov.nic.bio.bw.features.visaapplicantsenrollment.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.features.visaapplicantsenrollment.controllers;
}