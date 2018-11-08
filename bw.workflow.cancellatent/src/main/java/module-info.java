module bw.workflow.cancellatent
{
	requires bw.core;
	
	opens sa.gov.nic.bio.bw.features.cancellatent to bw.core;
	opens sa.gov.nic.bio.bw.features.cancellatent.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.cancellatent.workflow to bw.core;
	opens sa.gov.nic.bio.bw.features.cancellatent.controllers to javafx.fxml, bw.core;
}