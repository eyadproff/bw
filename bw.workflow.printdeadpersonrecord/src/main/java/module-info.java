module bw.workflow.printdeadpersonrecord
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.lib.jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.bundles;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord to bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans to org.apache.commons.lang3, com.google.gson;
}