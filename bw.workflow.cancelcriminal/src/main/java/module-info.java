module bw.workflow.cancelcriminal
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.cancelcriminal to bw.core;
	opens sa.gov.nic.bio.bw.workflow.cancelcriminal.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.cancelcriminal.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.cancelcriminal.controllers to javafx.fxml, bw.core;
}