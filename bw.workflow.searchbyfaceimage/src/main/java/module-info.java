module bw.workflow.searchbyfaceimage
{
	requires bw.core;
	requires bw.workflow.commons;
	
	opens sa.gov.nic.bio.bw.features.searchbyfaceimage to bw.core;
	opens sa.gov.nic.bio.bw.features.searchbyfaceimage.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.features.searchbyfaceimage.controllers;
}