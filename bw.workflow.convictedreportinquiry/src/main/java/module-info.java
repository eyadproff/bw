module bw.workflow.convictedreportinquiry
{
	requires bw.core;
	requires bw.workflow.registerconvictedpresent;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.features.convictedreportinquiry to bw.core;
	opens sa.gov.nic.bio.bw.features.convictedreportinquiry.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.convictedreportinquiry.workflow to bw.core;
	opens sa.gov.nic.bio.bw.features.convictedreportinquiry.controllers to javafx.fxml, bw.core;
}