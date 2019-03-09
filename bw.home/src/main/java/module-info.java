module bw.home
{
	opens sa.gov.nic.bio.bw.home.css;
	
	requires bw.core;
	requires bcl.utils;
	requires bw.workflow.commons;
	
	provides sa.gov.nic.bio.bw.core.workflow.Workflow with sa.gov.nic.bio.bw.home.HomeWorkflow;
	
	opens sa.gov.nic.bio.bw.home.bundles;
	opens sa.gov.nic.bio.bw.home to bw.core;
	opens sa.gov.nic.bio.bw.home.fxml to bw.core;
	opens sa.gov.nic.bio.bw.home.controllers to javafx.fxml, bw.core;
}