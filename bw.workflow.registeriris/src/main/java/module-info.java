module bw.workflow.registeriris
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	requires biokit.library;
	
	opens sa.gov.nic.bio.bw.workflow.registeriris.bundles;
	opens sa.gov.nic.bio.bw.workflow.registeriris to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registeriris.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.registeriris.controllers to javafx.fxml, bw.core;
	exports sa.gov.nic.bio.bw.workflow.registeriris.controllers;
}