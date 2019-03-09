module bw.workflow.citizenenrollment
{
	requires bw.core;
	
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.bundles;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment to bw.core;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers to javafx.fxml, bw.core;
}