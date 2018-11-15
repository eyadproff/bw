module bw.workflow.printdeadpersonrecord
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord to bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers to javafx.fxml, bw.core;
}