module bw.workflow.faceverification
{
	requires retrofit2;
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.workflow.searchbyfaceimage;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.features.faceverification to bw.core;
	opens sa.gov.nic.bio.bw.features.faceverification.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.faceverification.workflow to bw.core;
	opens sa.gov.nic.bio.bw.features.faceverification.controllers to javafx.fxml, bw.core;
}