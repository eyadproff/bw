module bw.workflow.cancelcriminal
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.features.cancelcriminal to bw.core;
	opens sa.gov.nic.bio.bw.features.cancelcriminal.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.cancelcriminal.workflow to bw.core;
	opens sa.gov.nic.bio.bw.features.cancelcriminal.controllers to javafx.fxml, bw.core;
}