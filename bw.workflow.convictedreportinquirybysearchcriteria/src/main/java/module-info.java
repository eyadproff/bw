module bw.workflow.convictedreportinquirybysearchcriteria
{
	requires bw.core;
	requires bw.workflow.registerconvictedpresent;
	requires bw.workflow.commons;
	requires bw.lib.jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.bundles;
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria to bw.core;
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.controllers to javafx.fxml, bw.core;
}