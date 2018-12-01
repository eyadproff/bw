module bw.login
{
	exports sa.gov.nic.bio.bw.login;
	requires bw.core;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	requires controlsfx;
	requires biokit.library;
	requires java.prefs;
	requires bw.workflow.commons;
	requires bcl.utils;
	requires bio.commons;
	
	provides sa.gov.nic.bio.bw.core.workflow.Workflow with sa.gov.nic.bio.bw.login.LoginWorkflow;
	
	opens sa.gov.nic.bio.bw.login to bw.core;
	opens sa.gov.nic.bio.bw.login.fxml to bw.core;
	opens sa.gov.nic.bio.bw.login.tasks to bw.core;
	opens sa.gov.nic.bio.bw.login.controllers to javafx.fxml, bw.core;
}