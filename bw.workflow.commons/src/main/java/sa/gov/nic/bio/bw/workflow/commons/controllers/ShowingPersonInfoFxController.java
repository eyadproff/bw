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
import sa.gov.nic.bio.bw.workflow.commons.webservice.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PersonInfo;

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
		normalizedPersonInfo = new NormalizedPersonInfo(personId,
		                                                personInfo, resources.getString("label.notAvailable"),
		                                                resources.getString("label.male"),
		                                                resources.getString("label.female"));
		
		String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
		Gender gender = normalizedPersonInfo.getGender();
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);
		
		lblPersonId.setText(normalizedPersonInfo.getPersonIdLabel());
		lblFirstName.setText(normalizedPersonInfo.getFirstNameLabel());
		lblFatherName.setText(normalizedPersonInfo.getFatherNameLabel());
		lblGrandfatherName.setText(normalizedPersonInfo.getGrandfatherNameLabel());
		lblFamilyName.setText(normalizedPersonInfo.getFamilyNameLabel());
		lblGender.setText(normalizedPersonInfo.getGenderLabel());
		lblNationality.setText(normalizedPersonInfo.getNationalityLabel());
		lblOccupation.setText(normalizedPersonInfo.getOccupationLabel());
		lblBirthPlace.setText(normalizedPersonInfo.getBirthPlaceLabel());
		lblBirthDate.setText(normalizedPersonInfo.getBirthDateLabel());
		lblPersonType.setText(normalizedPersonInfo.getPersonTypeLabel());
		lblDocumentId.setText(normalizedPersonInfo.getDocumentIdLabel());
		lblDocumentType.setText(normalizedPersonInfo.getDocumentTypeLabel());
		lblDocumentIssuanceDate.setText(normalizedPersonInfo.getDocumentIssuanceDateLabel());
		lblDocumentExpiryDate.setText(normalizedPersonInfo.getDocumentExpiryDateLabel());
		
		if(normalizedPersonInfo.getPersonId() == null) lblPersonId.setTextFill(Color.RED);
		if(normalizedPersonInfo.getFirstName() == null) lblFirstName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getFatherName() == null) lblFatherName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getGrandfatherName() == null) lblGrandfatherName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getFamilyName() == null) lblFamilyName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getGender() == null) lblGender.setTextFill(Color.RED);
		if(normalizedPersonInfo.getNationality() == null) lblNationality.setTextFill(Color.RED);
		if(normalizedPersonInfo.getOccupation() == null) lblOccupation.setTextFill(Color.RED);
		if(normalizedPersonInfo.getBirthPlace() == null) lblBirthPlace.setTextFill(Color.RED);
		if(normalizedPersonInfo.getBirthDate() == null) lblBirthDate.setTextFill(Color.RED);
		if(normalizedPersonInfo.getPersonType() == null) lblPersonType.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentId() == null) lblDocumentId.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentType() == null) lblDocumentType.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentIssuanceDate() == null) lblDocumentIssuanceDate.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentExpiryDate() == null) lblDocumentExpiryDate.setTextFill(Color.RED);
		
		//infoPane.autosize();
		btnConfirmPersonInfo.requestFocus();
	}
}