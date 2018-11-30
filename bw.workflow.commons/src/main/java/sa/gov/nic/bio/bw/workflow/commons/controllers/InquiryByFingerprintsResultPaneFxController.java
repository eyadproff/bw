package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@FxmlFile("inquiryByFingerprintsResult.fxml")
public class InquiryByFingerprintsResultPaneFxController extends WizardStepFxControllerBase
{
	@Input protected Boolean hideRegisterUnknownButton;
	@Input protected Status status;
	@Input protected Long civilBiometricsId;
	@Input protected Long criminalBiometricsId;
	@Input protected Map<Long, PersonInfo> civilPersonInfoMap;
	@Input protected Map<Long, PersonInfo> criminalPersonInfoMap;
	@Output protected NormalizedPersonInfo normalizedPersonInfo;
	
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
	@FXML private TableView<Long>  tvReportNumbers;
	@FXML private TableColumn<Long, Long> tcPersonIdSequence;
	@FXML private TableColumn<Long, String> tcPersonId;
	@FXML private TableColumn<Long, Long> tcReportNumberSequence;
	@FXML private TableColumn<Long, String> tcReportNumber;
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
	@FXML private Label lblCivilBiometricsId;
	@FXML private Label lblCriminalBiometricsId;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	
	@Override
	protected void onAttachedToScene()
	{
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		initSection(tvCivilPersonIds, tvReportNumbers, tcPersonIdSequence, tcPersonId, civilBiometricsId,
		            lblCivilBiometricsId, civilPersonInfoMap, paneCivilFingerprintsHit, paneCivilFingerprintsNoHit);
		
		initSection(tvReportNumbers, tvCivilPersonIds, tcReportNumberSequence, tcReportNumber, criminalBiometricsId,
		            lblCriminalBiometricsId, criminalPersonInfoMap, paneCriminalFingerprintsHit,
		            paneCriminalFingerprintsNoHit);
		
		if(status == Status.HIT)
		{
			GuiUtils.showNode(paneFingerprintsHitResults, true);
			GuiUtils.showNode(infoPane, true);
			if(btnConfirmPersonInformation != null) GuiUtils.showNode(btnConfirmPersonInformation, true);
			
			if(!tvCivilPersonIds.getItems().isEmpty()) tvCivilPersonIds.getSelectionModel().select(0);
			else if(!tvReportNumbers.getItems().isEmpty()) tvReportNumbers.getSelectionModel().select(0);
			else populatePersonInfo(null);
			
			if(btnConfirmPersonInformation != null) btnConfirmPersonInformation.requestFocus();
		}
		else
		{
			GuiUtils.showNode(paneNoHitMessage, true);
			if(btnRegisterUnknownPerson != null)
						GuiUtils.showNode(btnRegisterUnknownPerson, hideRegisterUnknownButton == null
																	|| !hideRegisterUnknownButton);
			
			btnStartOver.requestFocus();
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
		fillLabelTextWithBlack(lblFirstName, lblFatherName, lblGrandfatherName, lblFamilyName, lblGender,
							   lblNationality, lblOccupation, lblBirthPlace, lblBirthDate, lblPersonId, lblPersonType,
							   lblDocumentId, lblDocumentType, lblDocumentIssuanceDate, lblDocumentExpiryDate);
		
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
		if(btnConfirmPersonInformation != null) btnConfirmPersonInformation.requestFocus();
	}
	
	private void initSection(TableView<Long> tvSection, TableView<Long> tvOther,
	                                TableColumn<Long, Long> tcSequence, TableColumn<Long, String> tcLong,
	                                Long biometricsId, Label lblBiometricsId, Map<Long, PersonInfo> personInfoMap,
	                                Pane paneFingerprintsHit, Pane paneFingerprintsNoHit)
	{
		GuiUtils.initSequenceTableColumn(tcSequence);
		tcSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcLong.setCellValueFactory(param -> new SimpleStringProperty(AppUtils.localizeNumbers(
				String.valueOf(param.getValue()))));
		
		if(status == Status.HIT && biometricsId != null)
		{
			GuiUtils.showNode(paneFingerprintsHit, true);
			GuiUtils.showNode(tvSection, true);
			
			lblBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(biometricsId)));
			
			if(personInfoMap != null)
			{
				tvSection.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
				{
					tvOther.getSelectionModel().clearSelection();
					if(newValue != null) populatePersonInfo(personInfoMap.get(newValue));
					tvSection.requestFocus();
				});
				
				Set<Long> civilPersonIds = personInfoMap.keySet();
				tvSection.getItems().setAll(civilPersonIds);
			}
		}
		else GuiUtils.showNode(paneFingerprintsNoHit, true);
	}
	
	private static void fillLabelTextWithBlack(Label... labels)
	{
		for(Label label : labels) label.setTextFill(Color.BLACK);
	}
}