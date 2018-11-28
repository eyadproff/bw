package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.util.List;
import java.util.function.Consumer;

@FxmlFile("inquiryByFingerprintsResult.fxml")
public class InquiryByFingerprintsResultPaneFxController extends WizardStepFxControllerBase
{
	@Input private Boolean hideRegisterUnknownButton;
	@Input private Status status;
	@Input private Long civilBiometricsId;
	@Input private Long criminalBiometricsId;
	@Input private List<Long> civilPersonIds;
	@Input private PersonInfo personInfo;
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
	@FXML private TableColumn<Long, CheckBox> tcCheckBox;
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
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtCriminalBiometricsId;
	@FXML private CheckBox cbCivilFingerprints;
	@FXML private CheckBox cbCriminalFingerprints;
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
		tcCheckBox.setCellValueFactory(param ->
		{
			if(civilPersonIds.isEmpty() || !param.getValue().equals(civilPersonIds.get(0))) return null;
			
			CheckBox checkBox = new CheckBox();
			checkBox.setSelected(true);
			checkBox.setDisable(true);
			checkBox.getStyleClass().add("fakely-enabled");
			return new SimpleObjectProperty<>(checkBox);
		});
		
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
				civilSelected = true;
			}
			else
			{
				GuiUtils.showNode(paneCivilFingerprintsNoHit, true);
				GuiUtils.showNode(cbCivilFingerprints, false);
			}
			
			if(criminalBiometricsId != null)
			{
				GuiUtils.showNode(paneCriminalFingerprintsHit, true);
				GuiUtils.showNode(cbCriminalFingerprints, true);
				
				txtCriminalBiometricsId.setText(String.valueOf(criminalBiometricsId));
				if(!civilSelected) cbCriminalFingerprints.setSelected(true);
			}
			else
			{
				GuiUtils.showNode(paneCriminalFingerprintsNoHit, true);
				GuiUtils.showNode(cbCriminalFingerprints, false);
				cbCriminalFingerprints.setDisable(true);
			}
			
			populatePersonInfo();
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
	
	private void populatePersonInfo()
	{
		if(personInfo == null) return;
		
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
		btnConfirmPersonInformation.requestFocus();
	}
}