module bw.workflow.editconvictedreport
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	requires bw.workflow.deleteconvictedreport;
	
	opens sa.gov.nic.bio.bw.workflow.editconvictedreport.bundles;
	opens sa.gov.nic.bio.bw.workflow.editconvictedreport to bw.core;
	opens sa.gov.nic.bio.bw.workflow.editconvictedreport.fxml to bw.core;
	//opens sa.gov.nic.bio.bw.workflow.editconvictedreport.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers to javafx.fxml, bw.core;
}