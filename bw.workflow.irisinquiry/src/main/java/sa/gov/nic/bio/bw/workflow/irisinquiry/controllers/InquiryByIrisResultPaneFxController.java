package sa.gov.nic.bio.bw.workflow.irisinquiry.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.commons.ui.PTableColumn;
import sa.gov.nic.bio.bw.workflow.irisinquiry.tasks.IrisInquiryStatusCheckerWorkflowTask.Status;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@FxmlFile("inquiryByIrisResult.fxml")
public class InquiryByIrisResultPaneFxController extends WizardStepFxControllerBase
{
	@Input protected Status status;
	@Input protected Long civilBiometricsId;
	@Input protected Map<Long, PersonInfo> civilPersonInfoMap;

	@FXML private ScrollPane infoPane;
	@FXML private ImageViewPane paneImageView;
	@FXML private Pane paneImage;
	@FXML private Pane paneIrisHitResults;
	@FXML private Pane paneNoHitMessage;
	@FXML private TableView<Long> tvCivilPersonIds;
	@FXML private PTableColumn<Long, Long> tcCivilSelection;
	@FXML private PTableColumn<Long, Long> tcPersonIdSequence;
	@FXML private PTableColumn<Long, String> tcPersonId;
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
	@FXML private Label lblNaturalizedSaudi;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		tcCivilSelection.setCellFactory(tc -> new TableCell<>()
		{
			@Override
			public void updateItem(Long item, boolean empty)
			{
				super.updateItem(item, empty);
				
				TableRow tableRow = getTableRow();
				
				if(tableRow != null && tableRow.getIndex() == 0)
				{
					CheckBox checkBox = new CheckBox();
					checkBox.setSelected(true);
					checkBox.setDisable(true);
					setGraphic(checkBox);
				}
				else setGraphic(null);
			}
		});
		
		tcCivilSelection.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		GuiUtils.initSequenceTableColumn(tcPersonIdSequence);
		tcPersonIdSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcPersonId.setCellValueFactory(param -> new SimpleStringProperty(AppUtils.localizeNumbers(
																		String.valueOf(param.getValue()))));
		
		if(status == Status.HIT)
		{
			GuiUtils.showNode(tvCivilPersonIds, true);
			GuiUtils.showNode(paneIrisHitResults, true);
			GuiUtils.showNode(infoPane, true);
			
			lblCivilBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(civilBiometricsId)));
			
			if(civilPersonInfoMap != null)
			{
				tvCivilPersonIds.getSelectionModel().selectedItemProperty().addListener((observable, oldValue,
				                                                                         newValue) ->
				{
				    if(newValue != null)
				    {
				        populatePersonInfo(civilPersonInfoMap.get(newValue));
				        Platform.runLater(() ->
				        {
				            tvCivilPersonIds.getSelectionModel().select(newValue);
				        });
				    }
				});
				
				Set<Long> civilPersonIds = civilPersonInfoMap.keySet();
				tvCivilPersonIds.getItems().setAll(civilPersonIds);
				
				if(!tvCivilPersonIds.getItems().isEmpty())
				{
					tvCivilPersonIds.getSelectionModel().select(0);
				}
				else populatePersonInfo(null);
			}
		}
		else GuiUtils.showNode(paneNoHitMessage, true);
		
		btnStartOver.requestFocus();
	}
	
	private void populatePersonInfo(PersonInfo personInfo)
	{
		fillLabelTextWithBlack(lblFirstName, lblFatherName, lblGrandfatherName, lblFamilyName, lblGender,
							   lblNationality, lblOccupation, lblBirthPlace, lblBirthDate, lblPersonId, lblPersonType,
							   lblDocumentId, lblDocumentType, lblDocumentIssuanceDate, lblDocumentExpiryDate);
		
		var normalizedPersonInfo = new NormalizedPersonInfo(personInfo);

		GuiUtils.showNode(lblNaturalizedSaudi, normalizedPersonInfo.getNationality() != null &&
							normalizedPersonInfo.getNationality().getCode() > 0 &&
							!"SAU".equalsIgnoreCase(normalizedPersonInfo.getNationality().getMofaNationalityCode()) &&
							String.valueOf(normalizedPersonInfo.getPersonId()).startsWith("1"));
		
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
	}
	
	private static void fillLabelTextWithBlack(Label... labels)
	{
		for(Label label : labels) label.setTextFill(Color.BLACK);
	}
}