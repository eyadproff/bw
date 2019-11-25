module bw.workflow.irisinquiry
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	requires biokit.library;
	
	opens sa.gov.nic.bio.bw.workflow.irisinquiry.bundles;
	opens sa.gov.nic.bio.bw.workflow.irisinquiry to bw.core;
	opens sa.gov.nic.bio.bw.workflow.irisinquiry.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.irisinquiry.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.irisinquiry.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.irisinquiry.beans to org.apache.commons.lang3;
	exports sa.gov.nic.bio.bw.workflow.irisinquiry.controllers;
}