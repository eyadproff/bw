module bw.workflow.printdeadpersonrecord
{
	requires bw.core;
	requires bw.workflow.commons;
	requires jasperreports;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.features.printdeadpersonrecord to bw.core;
	opens sa.gov.nic.bio.bw.features.printdeadpersonrecord.fxml to bw.core;
	opens sa.gov.nic.bio.bw.features.printdeadpersonrecord.workflow to bw.core;
	opens sa.gov.nic.bio.bw.features.printdeadpersonrecord.controllers to javafx.fxml, bw.core;
}