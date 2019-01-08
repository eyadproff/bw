module bw.workflow.scfaceverification
{
	requires retrofit2;
	requires bw.core;
	requires bw.workflow.commons;
	requires bw.workflow.searchbyfaceimage;
	requires bio.commons;
	requires bw.workflow.faceverification;
	
	opens sa.gov.nic.bio.bw.workflow.scfaceverification to bw.core;
	opens sa.gov.nic.bio.bw.workflow.scfaceverification.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.scfaceverification.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.scfaceverification.controllers to javafx.fxml, bw.core;
}