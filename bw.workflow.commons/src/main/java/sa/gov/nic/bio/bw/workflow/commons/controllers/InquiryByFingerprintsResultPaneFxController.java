package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.SelectableItem;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BuildFingerprintInquiryReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.commons.ui.PTableColumn;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@FxmlFile("inquiryByFingerprintsResult.fxml")
public class InquiryByFingerprintsResultPaneFxController extends WizardStepFxControllerBase
{
	@Input protected Boolean hideRegisterUnknownButton;
	@Input protected Boolean hideConfirmationButton;
	@Input protected Boolean ignoreCriminalFingerprintsInquiryResult;
	@Input protected Status status;
	@Input protected Long civilBiometricsId;
	@Input protected Long criminalBiometricsId;
	@Input protected Map<Long, PersonInfo> civilPersonInfoMap;
	@Input protected Map<Integer, PersonInfo> oldCriminalPersonInfoMap;
	@Input protected Map<Long, PersonInfo> newCriminalPersonInfoMap;
	@Input protected Map<Integer, String> fingerprintBase64Images;
	@Output protected NormalizedPersonInfo normalizedPersonInfo;

	@FXML private ScrollPane infoPane;
	@FXML private ImageViewPane paneImageView;
	@FXML private TitledPane tpPaneCriminal;
	@FXML private Pane paneImage;
	@FXML private Pane paneFingerprintsHitResults;
	@FXML private Pane gridPane;
	@FXML private Pane paneCivilFingerprintsNoHit;
	@FXML private Pane paneCivilFingerprintsHit;
	@FXML private Pane paneCriminalFingerprintsNoHit;
	@FXML private Pane paneCriminalFingerprintsHit;
	@FXML private Pane paneNoHitMessage;
	@FXML private Pane paneButtons;
	@FXML private TabPane tabPaneCriminal;
	@FXML private Tab tabOldSystem;
	@FXML private Tab tabNewSystem;
	@FXML private TableView<Long> tvCivilPersonIds;
	@FXML private TableView<SelectableItem<Integer>> tvDisRecords;
	@FXML private TableView<SelectableItem<Long>> tvReportNumbers;
	@FXML private PTableColumn<Long, Long> tcCivilSelection;
	@FXML private PTableColumn<Long, Long> tcPersonIdSequence;
	@FXML private PTableColumn<Long, String> tcPersonId;
	@FXML private PTableColumn<SelectableItem<Integer>, SelectableItem<Integer>> tcOldCriminalSelection;
	@FXML private PTableColumn<SelectableItem<Integer>, String> tcDisRecordSequence;
	@FXML private PTableColumn<SelectableItem<Long>, SelectableItem<Long>> tcNewCriminalSelection;
	@FXML private PTableColumn<SelectableItem<Long>, SelectableItem<Long>> tcReportNumberSequence;
	@FXML private PTableColumn<SelectableItem<Long>, String> tcReportNumber;
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
	@FXML private Label lblNaturalizedSaudi;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnShowReport;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;

	private final FileChooser fileChooser = new FileChooser();
	private final AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private boolean civilHit;
	private boolean criminalHit;
	private Long selectedCivilPersonId;
	private int selectedCivilPersonIdIndex = -1;
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
										resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);

		boolean hideConfirmationButton = this.hideConfirmationButton != null && this.hideConfirmationButton;
		if(hideConfirmationButton)
		{
			btnConfirmPersonInformation.setDisable(true);
			GuiUtils.showNode(btnConfirmPersonInformation, false);
			tvCivilPersonIds.getColumns().remove(tcCivilSelection);
			tvDisRecords.getColumns().remove(tcOldCriminalSelection);
			tvReportNumbers.getColumns().remove(tcNewCriminalSelection);
			
			tcPersonId.setPercentageWidth(tcPersonId.getPercentageWidth() + tcCivilSelection.getPercentageWidth());
			tcDisRecordSequence.setPercentageWidth(tcDisRecordSequence.getPercentageWidth() +
			                                       tcOldCriminalSelection.getPercentageWidth());
			tcReportNumber.setPercentageWidth(tcReportNumber.getPercentageWidth() +
			                                  tcNewCriminalSelection.getPercentageWidth());
		}
		
		if(ignoreCriminalFingerprintsInquiryResult != null && ignoreCriminalFingerprintsInquiryResult)
		{
			GuiUtils.showNode(tpPaneCriminal, false);
			GuiUtils.showNode(btnShowReport, false);
			GuiUtils.showNode(btnPrintReport, false);
			GuiUtils.showNode(btnSaveReportAsPDF, false);
		}
		
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		civilHit = status == Status.HIT && civilBiometricsId != null;
		criminalHit = status == Status.HIT && criminalBiometricsId != null;
		
		for(Long personId : civilPersonInfoMap.keySet())
		{
			selectedCivilPersonIdIndex++;
			if(String.valueOf(personId).startsWith("800")) continue;
			selectedCivilPersonId = personId;
			break;
		}
		
		tcCivilSelection.setCellFactory(tc -> new TableCell<>()
		{
			@Override
			public void updateItem(Long item, boolean empty)
			{
				super.updateItem(item, empty);
				
				var tableRow = getTableRow();
				
				if(!hideConfirmationButton && tableRow != null && tableRow.getIndex() == selectedCivilPersonIdIndex)
				{
					CheckBox checkBox = new CheckBox();
					checkBox.setSelected(true);
					checkBox.setDisable(true);
					setGraphic(checkBox);
				}
				else setGraphic(null);
			}
		});
		tcOldCriminalSelection.setCellFactory(tc -> new TableCell<>()
		{
			@Override
			public void updateItem(SelectableItem<Integer> item, boolean empty)
			{
				super.updateItem(item, empty);
				
				TableRow<?> tableRow = getTableRow();
				if(tableRow != null && item != null)
				{
					if(!hideConfirmationButton && !civilHit)
					{
						CheckBox checkBox = new CheckBox();
						checkBox.setSelected(item.isSelected());
						checkBox.setDisable(true);
						setGraphic(checkBox);
					}
					else setGraphic(null);
				}
				else setGraphic(null);
			}
		});
		tcNewCriminalSelection.setCellFactory(tc -> new TableCell<>()
		{
			@Override
			public void updateItem(SelectableItem<Long> item, boolean empty)
			{
				super.updateItem(item, empty);
				
				TableRow<?> tableRow = getTableRow();
				if(tableRow != null && item != null)
				{
					if(!hideConfirmationButton && !civilHit)
					{
						CheckBox checkBox = new CheckBox();
						checkBox.setSelected(item.isSelected());
						checkBox.setDisable(true);
						setGraphic(checkBox);
					}
					else setGraphic(null);
				}
				else setGraphic(null);
			}
		});
		
		tcCivilSelection.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcOldCriminalSelection.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcNewCriminalSelection.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		
		tvDisRecords.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			if(oldValue != null) oldValue.setSelected(false);
			if(newValue != null) newValue.setSelected(true);
			tvDisRecords.refresh();
		});
		tvReportNumbers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(oldValue != null) oldValue.setSelected(false);
		    if(newValue != null) newValue.setSelected(true);
			tvReportNumbers.refresh();
		});
		
		GuiUtils.initSequenceTableColumn(tcPersonIdSequence);
		GuiUtils.initSequenceTableColumn(tcReportNumberSequence);
		
		tcPersonIdSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcReportNumberSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		
		tcPersonId.setCellValueFactory(param -> new SimpleStringProperty(AppUtils.localizeNumbers(
																		String.valueOf(param.getValue()))));
		tcDisRecordSequence.setCellValueFactory(param -> new SimpleStringProperty(AppUtils.localizeNumbers(
																		String.valueOf(param.getValue().getItem()))));
		tcReportNumber.setCellValueFactory(param -> new SimpleStringProperty(AppUtils.localizeNumbers(
																		String.valueOf(param.getValue().getItem()))));
		
		if(civilHit)
		{
			GuiUtils.showNode(paneCivilFingerprintsHit, true);
			GuiUtils.showNode(tvCivilPersonIds, true);
			
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
				            tvReportNumbers.getSelectionModel().clearSelection();
				            tvDisRecords.getSelectionModel().clearSelection();
				            tvCivilPersonIds.getSelectionModel().select(newValue);
				        });
				    }
				});
				
				Set<Long> civilPersonIds = civilPersonInfoMap.keySet();
				tvCivilPersonIds.getItems().setAll(civilPersonIds);
			}
		}
		else GuiUtils.showNode(paneCivilFingerprintsNoHit, true);
		
		if(criminalHit)
		{
			GuiUtils.showNode(paneCriminalFingerprintsHit, true);
			GuiUtils.showNode(tabPaneCriminal, true);
			
			lblCriminalBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(criminalBiometricsId)));
			
			if(oldCriminalPersonInfoMap != null)
			{
				tvDisRecords.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
				{
				    if(newValue != null)
				    {
				        populatePersonInfo(oldCriminalPersonInfoMap.get(newValue.getItem()));
				        Platform.runLater(() ->
				        {
					        tvCivilPersonIds.getSelectionModel().clearSelection();
					        tvReportNumbers.getSelectionModel().clearSelection();
				            tvDisRecords.getSelectionModel().select(newValue);
				        });
				    }
				});
				
				tvDisRecords.getItems().clear();
				Set<Integer> disRecordSequences = oldCriminalPersonInfoMap.keySet();
				for(Integer disRecordSequence : disRecordSequences)
				{
					tvDisRecords.getItems().add(new SelectableItem<>(disRecordSequence, false));
				}
			}
			
			if(newCriminalPersonInfoMap != null)
			{
				tvReportNumbers.getSelectionModel().selectedItemProperty().addListener(
																					(observable, oldValue, newValue) ->
				{
				    if(newValue != null)
				    {
				        populatePersonInfo(newCriminalPersonInfoMap.get(newValue.getItem()));
				        Platform.runLater(() ->
				        {
				            tvCivilPersonIds.getSelectionModel().clearSelection();
					        tvDisRecords.getSelectionModel().clearSelection();
				            tvReportNumbers.getSelectionModel().select(newValue);
				        });
				    }
				});
				
				tvReportNumbers.getItems().clear();
				Set<Long> reportNumbers = newCriminalPersonInfoMap.keySet();
				for(Long reportNumber : reportNumbers)
				{
					tvReportNumbers.getItems().add(new SelectableItem<>(reportNumber, false));
				}
			}
		}
		else GuiUtils.showNode(paneCriminalFingerprintsNoHit, true);
		
		if(status == Status.HIT)
		{
			GuiUtils.showNode(paneFingerprintsHitResults, true);
			GuiUtils.showNode(infoPane, true);
			
			if(!tvCivilPersonIds.getItems().isEmpty())
			{
				tvCivilPersonIds.getSelectionModel().select(selectedCivilPersonIdIndex);
				if(!tvDisRecords.getItems().isEmpty()) tabPaneCriminal.getSelectionModel().select(tabOldSystem);
				else tabPaneCriminal.getSelectionModel().select(tabNewSystem);
			}
			else if(!tvReportNumbers.getItems().isEmpty())
			{
				tabPaneCriminal.getSelectionModel().select(tabNewSystem);
				tvReportNumbers.getSelectionModel().select(0);
			}
			else if(!tvDisRecords.getItems().isEmpty())
			{
				tabPaneCriminal.getSelectionModel().select(tabOldSystem);
				tvDisRecords.getSelectionModel().select(0);
			}
			else populatePersonInfo(null);
			
			if(!btnConfirmPersonInformation.isDisabled()) btnConfirmPersonInformation.requestFocus();
			else btnStartOver.requestFocus();
		}
		else
		{
			GuiUtils.showNode(btnConfirmPersonInformation, false);
			GuiUtils.showNode(paneNoHitMessage, true);
			if(btnRegisterUnknownPerson != null)
						GuiUtils.showNode(btnRegisterUnknownPerson, hideRegisterUnknownButton == null
																	|| !hideRegisterUnknownButton);
			
			btnStartOver.requestFocus();
		}
		
		btnShowReport.disableProperty().bind(Bindings.size(tvReportNumbers.getSelectionModel()
				                                                          .getSelectedItems()).isEqualTo(0));
		
		EventHandler<? super KeyEvent> keyReleasedEventHandler = keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnShowReport.fire();
		};
		EventHandler<? super MouseEvent> mouseEventHandler = mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnShowReport.fire();
			}
		};
		
		tvReportNumbers.setOnKeyReleased(keyReleasedEventHandler);
		tvReportNumbers.setOnMouseClicked(mouseEventHandler);

		paneButtons.visibleProperty().bind(piProgress.visibleProperty().not());
		paneButtons.managedProperty().bind(piProgress.managedProperty().not());
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		boolean civilHit = paneCivilFingerprintsHit.isVisible();
		
		if(civilHit)
		{
			normalizedPersonInfo = new NormalizedPersonInfo(civilPersonInfoMap.get((selectedCivilPersonId)));
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
		if(btnConfirmPersonInformation != null) btnConfirmPersonInformation.requestFocus();
	}
	
	private static void fillLabelTextWithBlack(Label... labels)
	{
		for(Label label : labels) label.setTextFill(Color.BLACK);
	}
	
	@FXML
	private void onShowReportButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		try
		{
			ShowReportDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), ShowReportDialogFxController.class, true);
		
			if(controller != null)
			{
				SelectableItem<Long> selectedItem = tvReportNumbers.getSelectionModel().getSelectedItem();
				if(selectedItem != null)
				{
					controller.setReportNumber(selectedItem.getItem());
					controller.show();
				}
			}
		}
		catch(Exception e)
		{
			String errorCode = CommonsErrorCodes.C008_00037.getCode();
			String[] errorDetails = {"Failed to load (" + ShowReportDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
		}
	}

	@FXML
	private void onPrintReportButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(piProgress, true);

		if(jasperPrint.get() == null)
		{
			var task = newBuildFingerprintInquiryReportTask();
			task.setOnSucceeded(event ->
			{
				JasperPrint value = task.getValue();
				jasperPrint.set(value);
				printFingerprintInquiryReport(value);
			});
			task.setOnFailed(event ->
			{
				GuiUtils.showNode(piProgress, false);

				Throwable exception = task.getException();

				String errorCode = CommonsErrorCodes.C008_00042.getCode();
				String[] errorDetails = {"failed while building the fingerprint inquiry report!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			});
			Context.getExecutorService().submit(task);
		}
		else printFingerprintInquiryReport(jasperPrint.get());
	}

	@FXML
	private void onSaveReportAsPdfButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

		if(selectedFile != null)
		{
			GuiUtils.showNode(piProgress, true);

			if(jasperPrint.get() == null)
			{
				var task = newBuildFingerprintInquiryReportTask();
				task.setOnSucceeded(event ->
				{
					JasperPrint value = task.getValue();
					jasperPrint.set(value);
					try
					{
						saveFingerprintInquiryReportAsPDF(value, selectedFile);
					}
					catch(Exception e)
					{
						GuiUtils.showNode(piProgress, false);

						String errorCode = CommonsErrorCodes.C008_00043.getCode();
						String[] errorDetails = {"failed while saving the fingerprint inquiry report as PDF!"};
						Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
					}
				});
				task.setOnFailed(event ->
				{
					GuiUtils.showNode(piProgress, false);

					Throwable exception = task.getException();

					String errorCode = CommonsErrorCodes.C008_00044.getCode();
					String[] errorDetails = {"failed while building the fingerprint inquiry report!"};
					Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
				});
				Context.getExecutorService().submit(task);
			}
			else
			{
				try
				{
					saveFingerprintInquiryReportAsPDF(jasperPrint.get(), selectedFile);
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piProgress, false);

					String errorCode = CommonsErrorCodes.C008_00045.getCode();
					String[] errorDetails = {"failed while saving the fingerprint inquiry report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
				}
			}
		}
	}

	private void printFingerprintInquiryReport(JasperPrint jasperPrint)
	{
		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
			GuiUtils.showNode(piProgress, false);
		});
		printReportTask.setOnFailed(event ->
		{
			GuiUtils.showNode(piProgress, false);

			Throwable exception = printReportTask.getException();

			String errorCode = CommonsErrorCodes.C008_00046.getCode();
			String[] errorDetails = {"failed while printing the fingerprint inquiry report!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		});
		Context.getExecutorService().submit(printReportTask);
	}

	private void saveFingerprintInquiryReportAsPDF(JasperPrint jasperPrint, File selectedFile)
																						throws FileNotFoundException
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
																			   new FileOutputStream(selectedFile));
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
			GuiUtils.showNode(piProgress, false);

			showSuccessNotification(resources.getString("fingerprintInquiry.savingAsPDF.success.message"));

			try
			{
				Desktop.getDesktop().open(selectedFile);
			}
			catch(Exception e)
			{
				LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
			}
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
			GuiUtils.showNode(piProgress, false);

			Throwable exception = printReportTaskAsPdfTask.getException();

			String errorCode = CommonsErrorCodes.C008_00047.getCode();
			String[] errorDetails = {"failed while saving the fingerprint inquiry report as PDF!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}

	private BuildFingerprintInquiryReportTask newBuildFingerprintInquiryReportTask()
	{
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		String inquirerId = AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()));

		return new BuildFingerprintInquiryReportTask(inquirerId, civilHit, criminalHit, civilBiometricsId,
													 criminalBiometricsId, civilPersonInfoMap,
													 oldCriminalPersonInfoMap, newCriminalPersonInfoMap,
													 fingerprintBase64Images);
	}
}