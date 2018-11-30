module bw.workflow.searchbyfaceimage
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.searchbyfaceimage to bw.core;
	opens sa.gov.nic.bio.bw.workflow.searchbyfaceimage.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.searchbyfaceimage.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers;
}