module bw.workflow.deleteconvictedreport
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.deleteconvictedreport.bundles;
	opens sa.gov.nic.bio.bw.workflow.deleteconvictedreport to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deleteconvictedreport.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deleteconvictedreport.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers to javafx.fxml, bw.core;
	
	exports sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers to bw.workflow.editconvictedreport;
}