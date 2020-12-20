package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans.MiscreantInfo;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.beans.NormalizedMiscreantInfo;

import java.util.function.Consumer;

@FxmlFile("showingMiscreantInfo.fxml")
public class ShowingMiscreantInfoFxController extends WizardStepFxControllerBase
{
	@Input private MiscreantInfo miscreantInfo;
	@Output private NormalizedMiscreantInfo normalizedMiscreantInfo;
	
	@FXML private ScrollPane infoPane;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Button btnStartOver;
	@FXML private Button btnConfirmMiscreantInfo;
	
	@Override
	protected void onAttachedToScene()
	{
		normalizedMiscreantInfo = new NormalizedMiscreantInfo(miscreantInfo);
		
		String notAvailable = resources.getString("label.notAvailable");
		Consumer<Label> consumer = label ->
		{
			label.setText(notAvailable);
			label.setTextFill(Color.RED);
		};
		
		GuiUtils.setLabelText(lblFirstName, normalizedMiscreantInfo.getFirstNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblFatherName, normalizedMiscreantInfo.getFatherNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblGrandfatherName, normalizedMiscreantInfo.getGrandfatherNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblFamilyName, normalizedMiscreantInfo.getFamilyNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblGender, normalizedMiscreantInfo.getGender()).orElse(consumer);
		GuiUtils.setLabelText(lblNationality, normalizedMiscreantInfo.getNationality()).orElse(consumer);
		GuiUtils.setLabelText(lblOccupation, normalizedMiscreantInfo.getOccupation()).orElse(consumer);
		GuiUtils.setLabelText(lblBirthPlace, normalizedMiscreantInfo.getBirthPlace()).orElse(consumer);
		GuiUtils.setLabelText(lblBirthDate, true, normalizedMiscreantInfo.getBirthDate()).orElse(consumer);
		
		infoPane.autosize();
		btnConfirmMiscreantInfo.requestFocus();
	}
}