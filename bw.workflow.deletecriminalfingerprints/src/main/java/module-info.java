module bw.workflow.deletecriminalfingerprints
{
	requires bw.core;
	requires bw.workflow.commons;
	requires bio.commons;
	
	opens sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.bundles;
	opens sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.fxml to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.tasks to bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.controllers to javafx.fxml, bw.core;
	opens sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.beans to org.apache.commons.lang3, com.google.gson;
}