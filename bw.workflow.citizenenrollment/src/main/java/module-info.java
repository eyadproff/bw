module bw.workflow.citizenenrollment
{
	requires bw.core;
	
	opens sa.gov.nic.bio.bw.features.citizenenrollment to bw.core;
	opens sa.gov.nic.bio.bw.features.citizenenrollment.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.citizenenrollment.controllers to javafx.fxml, bw.core;
}