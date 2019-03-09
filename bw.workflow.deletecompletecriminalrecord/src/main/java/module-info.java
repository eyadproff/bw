module bw.workflow.deletecompletecriminalrecord
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.bundles;
	opens sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers to javafx.fxml, bw.core;
}