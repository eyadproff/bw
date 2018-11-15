module bw.workflow.convictedreportinquiry
{
	requires bw.core;
	requires bw.workflow.registerconvictedpresent;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquiry to bw.core;
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquiry.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquiry.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquiry.controllers to javafx.fxml, bw.core;
}