module bw.workflow.citizenenrollment
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	requires biokit.library;
	
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.bundles;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment to bw.core;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.citizenenrollment.beans to org.apache.commons.lang3;
}