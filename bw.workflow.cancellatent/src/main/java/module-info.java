module bw.workflow.cancellatent
{
	requires bw.core;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.cancellatent to bw.core;
	opens sa.gov.nic.bio.bw.workflow.cancellatent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.cancellatent.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.cancellatent.controllers to javafx.fxml, bw.core;
}