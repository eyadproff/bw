package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.util.function.Consumer;

@FxmlFile("showingPersonInfo.fxml")
public class ShowingPersonInfoFxController extends WizardStepFxControllerBase
{
	@Input private Long personId;
	@Input private PersonInfo personInfo;
	@Output private NormalizedPersonInfo normalizedPersonInfo;
	
	@FXML private ScrollPane infoPane;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblPersonId;
	@FXML private Label lblPersonType;
	@FXML private Label lblDocumentId;
	@FXML private Label lblDocumentType;
	@FXML private Label lblDocumentIssuanceDate;
	@FXML private Label lblDocumentExpiryDate;
	@FXML private Button btnStartOver;
	@FXML private Button btnConfirmPersonInfo;
	
	@Override
	protected void onAttachedToScene()
	{
		normalizedPersonInfo = new NormalizedPersonInfo(personInfo);
		
		String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
		Gender gender = normalizedPersonInfo.getGender();
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);
		
		String notAvailable = resources.getString("label.notAvailable");
		Consumer<Label> consumer = label ->
		{
			label.setText(notAvailable);
			label.setTextFill(Color.RED);
		};
		
		GuiUtils.setLabelText(lblFirstName, normalizedPersonInfo.getFirstNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblFatherName, normalizedPersonInfo.getFatherNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblGrandfatherName, normalizedPersonInfo.getGrandfatherNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblFamilyName, normalizedPersonInfo.getFamilyNameLabel()).orElse(consumer);
		GuiUtils.setLabelText(lblGender, normalizedPersonInfo.getGender()).orElse(consumer);
		GuiUtils.setLabelText(lblNationality, normalizedPersonInfo.getNationality()).orElse(consumer);
		GuiUtils.setLabelText(lblOccupation, normalizedPersonInfo.getOccupation()).orElse(consumer);
		GuiUtils.setLabelText(lblBirthPlace, normalizedPersonInfo.getBirthPlace()).orElse(consumer);
		GuiUtils.setLabelText(lblBirthDate, normalizedPersonInfo.getBirthDate()).orElse(consumer);
		GuiUtils.setLabelText(lblPersonId, normalizedPersonInfo.getPersonId()).orElse(consumer);
		GuiUtils.setLabelText(lblPersonType, normalizedPersonInfo.getPersonType()).orElse(consumer);
		GuiUtils.setLabelText(lblDocumentId, normalizedPersonInfo.getDocumentId()).orElse(consumer);
		GuiUtils.setLabelText(lblDocumentType, normalizedPersonInfo.getDocumentType()).orElse(consumer);
		GuiUtils.setLabelText(lblDocumentIssuanceDate, normalizedPersonInfo.getDocumentIssuanceDate()).orElse(consumer);
		GuiUtils.setLabelText(lblDocumentExpiryDate, normalizedPersonInfo.getDocumentExpiryDate()).orElse(consumer);
		
		infoPane.autosize();
		btnConfirmPersonInfo.requestFocus();
	}
}