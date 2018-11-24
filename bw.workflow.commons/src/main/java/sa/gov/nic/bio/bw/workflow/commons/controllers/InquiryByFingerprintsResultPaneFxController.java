package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.commons.webservice.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PersonInfo;

import java.util.List;

@FxmlFile("inquiryByFingerprintsResult.fxml")
public class InquiryByFingerprintsResultPaneFxController extends WizardStepFxControllerBase
{
	@Input private Boolean hideRegisterUnknownButton;
	@Input private Status status;
	@Input private Long civilBiometricsId;
	@Input private Long criminalBiometricsId;
	@Input private List<Long> civilPersonIds;
	@Output private NormalizedPersonInfo normalizedPersonInfo;
	
	@FXML private ScrollPane infoPane;
	@FXML private ImageViewPane paneImageView;
	@FXML private Pane paneImage;
	@FXML private Pane paneFingerprintsHitResults;
	@FXML private Pane gridPane;
	@FXML private Pane paneCivilFingerprintsNoHit;
	@FXML private Pane paneCivilFingerprintsHit;
	@FXML private Pane paneCriminalFingerprintsNoHit;
	@FXML private Pane paneCriminalFingerprintsHit;
	@FXML private Pane paneNoHitMessage;
	@FXML private TableView<Long> tvCivilPersonIds;
	@FXML private TableColumn<Long, Long> tcSequence;
	@FXML private TableColumn<Long, String> tcPersonId;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblCivilBiometricsIdLabel;
	@FXML private Label lblCivilBiometricsId;
	@FXML private Label lblCriminalBiometricsIdLabel;
	@FXML private Label lblCriminalBiometricsId;
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
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private RadioButton rdoCivilFingerprints;
	@FXML private RadioButton rdoCriminalFingerprints;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	
	@Override
	protected void onAttachedToScene()
	{
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		GuiUtils.initSequenceTableColumn(tcSequence);
		tcSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcPersonId.setCellValueFactory(param -> new SimpleStringProperty(AppUtils.localizeNumbers(
																				String.valueOf(param.getValue()))));
		
		boolean civilSelected = false;
		
		if(status == Status.HIT)
		{
			GuiUtils.showNode(paneFingerprintsHitResults, true);
			GuiUtils.showNode(infoPane, true);
			GuiUtils.showNode(btnConfirmPersonInformation, true);
			
			if(civilBiometricsId != null)
			{
				GuiUtils.showNode(paneCivilFingerprintsHit, true);
				GuiUtils.showNode(tvCivilPersonIds, true);
				
				txtCivilBiometricsId.setText(String.valueOf(civilBiometricsId));
				tvCivilPersonIds.getItems().setAll(civilPersonIds);
				rdoCivilFingerprints.setSelected(true);
				civilSelected = true;
			}
			else
			{
				GuiUtils.showNode(paneCivilFingerprintsNoHit, true);
				GuiUtils.showNode(rdoCivilFingerprints, false);
				rdoCivilFingerprints.setDisable(true);
			}
			
			if(criminalBiometricsId != null)
			{
				GuiUtils.showNode(paneCriminalFingerprintsHit, true);
				GuiUtils.showNode(rdoCriminalFingerprints, true);
				
				txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
				if(!civilSelected) rdoCriminalFingerprints.setSelected(true);
			}
			else
			{
				GuiUtils.showNode(paneCriminalFingerprintsNoHit, true);
				GuiUtils.showNode(rdoCriminalFingerprints, false);
				rdoCriminalFingerprints.setDisable(true);
			}
		}
		else
		{
			GuiUtils.showNode(paneNoHitMessage, true);
			GuiUtils.showNode(btnRegisterUnknownPerson, hideRegisterUnknownButton == null
															|| !hideRegisterUnknownButton);
		}
	}
	
	@FXML
	private void onRegisterUnknownPersonButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("inquiry.registerUnknown.confirmation.header");
		String contentText = resources.getString("inquiry.registerUnknown.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) goNext();
	}
	
	private void populatePersonInfo(PersonInfo personInfo)
	{
		//normalizedPersonInfo = new NormalizedPersonInfo(personId, civilBiometricsId, criminalBiometricsId,
		//                                                personInfo, resources.getString("label.notAvailable"),
		//                                                resources.getString("label.male"),
		//                                                resources.getString("label.female"));
		//
		//String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
		//Gender gender = normalizedPersonInfo.getGender();
		//GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);
		//
		//lblPersonId.setText(normalizedPersonInfo.getPersonIdLabel());
		//lblCivilBiometricsId.setText(normalizedPersonInfo.getCivilBiometricsIdLabel());
		//lblCriminalBiometricsId.setText(normalizedPersonInfo.getCriminalBiometricsIdLabel());
		//lblFirstName.setText(normalizedPersonInfo.getFirstNameLabel());
		//lblFatherName.setText(normalizedPersonInfo.getFatherNameLabel());
		//lblGrandfatherName.setText(normalizedPersonInfo.getGrandfatherNameLabel());
		//lblFamilyName.setText(normalizedPersonInfo.getFamilyNameLabel());
		//lblGender.setText(normalizedPersonInfo.getGenderLabel());
		//lblNationality.setText(normalizedPersonInfo.getNationalityLabel());
		//lblOccupation.setText(normalizedPersonInfo.getOccupationLabel());
		//lblBirthPlace.setText(normalizedPersonInfo.getBirthPlaceLabel());
		//lblBirthDate.setText(normalizedPersonInfo.getBirthDateLabel());
		//lblPersonType.setText(normalizedPersonInfo.getPersonTypeLabel());
		//lblDocumentId.setText(normalizedPersonInfo.getDocumentIdLabel());
		//lblDocumentType.setText(normalizedPersonInfo.getDocumentTypeLabel());
		//lblDocumentIssuanceDate.setText(normalizedPersonInfo.getDocumentIssuanceDateLabel());
		//lblDocumentExpiryDate.setText(normalizedPersonInfo.getDocumentExpiryDateLabel());
		//
		//if(normalizedPersonInfo.getPersonId() == null) lblPersonId.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getCivilBiometricsId() == null) lblCivilBiometricsId.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getCriminalBiometricsId() == null) lblCriminalBiometricsId.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getFirstName() == null) lblFirstName.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getFatherName() == null) lblFatherName.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getGrandfatherName() == null) lblGrandfatherName.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getFamilyName() == null) lblFamilyName.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getGender() == null) lblGender.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getNationality() == null) lblNationality.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getOccupation() == null) lblOccupation.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getBirthPlace() == null) lblBirthPlace.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getBirthDate() == null) lblBirthDate.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getPersonType() == null) lblPersonType.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getDocumentId() == null) lblDocumentId.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getDocumentType() == null) lblDocumentType.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getDocumentIssuanceDate() == null) lblDocumentIssuanceDate.setTextFill(Color.RED);
		//if(normalizedPersonInfo.getDocumentExpiryDate() == null) lblDocumentExpiryDate.setTextFill(Color.RED);
		//
		//infoPane.autosize();
		//btnConfirmPersonInformation.requestFocus();
	}
}