package sa.gov.nic.bio.bw.features.fingerprintcardidentification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.features.commons.webservice.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.features.commons.workflow.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.features.fingerprintcardidentification.beans.FingerprintCardIdentificationRecordReport;
import sa.gov.nic.bio.bw.features.fingerprintcardidentification.tasks.BuildFingerprintCardIdentificationRecordReportTask;
import sa.gov.nic.bio.bw.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("inquiryResult.fxml")
public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	@Input private Status status;
	@Input private Long personId;
	@Input private Long civilBiometricsId;
	@Input private Long criminalBiometricsId;
	@Input private PersonInfo personInfo;
	@Input private Map<Integer, Image> fingerprintImages;
	@Input private Map<Integer, String> fingerprintBase64Images;
	
	@FXML private VBox paneNoHitMessage;
	@FXML private ScrollPane paneResult;
	@FXML private ImageView ivPersonPhoto;
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivLeftThumb;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftLittle;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblCivilBiometricsId;
	@FXML private Label lblCriminalBiometricsId;
	@FXML private Label lblPersonId;
	@FXML private Label lblPersonType;
	@FXML private Label lblDocumentId;
	@FXML private Label lblDocumentType;
	@FXML private Label lblDocumentIssuanceDate;
	@FXML private Label lblDocumentExpiryDate;
	@FXML private Label lblOccupation;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;
	
	private FingerprintCardIdentificationRecordReport fingerprintCardIdentificationRecordReport;
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveRecordAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.saveRecordAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		boolean fingerprintHit = status == Status.HIT;
		
		GuiUtils.showNode(paneResult, fingerprintHit);
		GuiUtils.showNode(btnPrintRecord, fingerprintHit);
		GuiUtils.showNode(btnSaveRecordAsPDF, fingerprintHit);
		GuiUtils.showNode(paneNoHitMessage, !fingerprintHit);
		
		if(fingerprintHit)
		{
			populateData();
			GuiUtils.showNode(paneResult, true);
		}
	}
	
	@FXML
	private void onPrintRecordButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(btnPrintRecord, false);
		GuiUtils.showNode(btnSaveRecordAsPDF, false);
		GuiUtils.showNode(piProgress, true);
		
		if(jasperPrint.get() == null)
		{
			BuildFingerprintCardIdentificationRecordReportTask buildFingerprintCardIdentificationRecordReportTask =
					new BuildFingerprintCardIdentificationRecordReportTask(fingerprintCardIdentificationRecordReport);
			
			buildFingerprintCardIdentificationRecordReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildFingerprintCardIdentificationRecordReportTask.getValue();
			    jasperPrint.set(value);
			    printDeadPersonRecordReport(value);
			});
			buildFingerprintCardIdentificationRecordReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintRecord, true);
			    GuiUtils.showNode(btnSaveRecordAsPDF, true);
			
			    Throwable exception = buildFingerprintCardIdentificationRecordReportTask.getException();
			
			    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00004.getCode();
			    String[] errorDetails = {"failed while building the fingerprint card identification report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildFingerprintCardIdentificationRecordReportTask);
		}
		else printDeadPersonRecordReport(jasperPrint.get());
	}
	
	@FXML
	private void onSaveRecordAsPdfButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnPrintRecord, false);
			GuiUtils.showNode(btnSaveRecordAsPDF, false);
			GuiUtils.showNode(piProgress, true);
			
			if(jasperPrint.get() == null)
			{
				BuildFingerprintCardIdentificationRecordReportTask buildFingerprintCardIdentificationRecordReportTask =
					new BuildFingerprintCardIdentificationRecordReportTask(fingerprintCardIdentificationRecordReport);
				buildFingerprintCardIdentificationRecordReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildFingerprintCardIdentificationRecordReportTask.getValue();
				    jasperPrint.set(value);
				    try
				    {
				        saveDeadPersonRecordReportAsPDF(value, new FileOutputStream(selectedFile));
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piProgress, false);
				        GuiUtils.showNode(btnStartOver, true);
				        GuiUtils.showNode(btnPrintRecord, true);
				        GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				        String errorCode = FingerprintCardIdentificationErrorCodes.C013_00005.getCode();
				        String[] errorDetails =
						                    {"failed while saving the fingerprint card identification report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildFingerprintCardIdentificationRecordReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnStartOver, true);
				    GuiUtils.showNode(btnPrintRecord, true);
				    GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				    Throwable exception = buildFingerprintCardIdentificationRecordReportTask.getException();
				
				    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00006.getCode();
				    String[] errorDetails = {"failed while building the fingerprint card identification report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildFingerprintCardIdentificationRecordReportTask);
			}
			else
			{
				try
				{
					saveDeadPersonRecordReportAsPDF(jasperPrint.get(), new FileOutputStream(selectedFile));
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piProgress, false);
					GuiUtils.showNode(btnStartOver, true);
					GuiUtils.showNode(btnPrintRecord, true);
					GuiUtils.showNode(btnSaveRecordAsPDF, true);
					
					String errorCode = FingerprintCardIdentificationErrorCodes.C013_00007.getCode();
					String[] errorDetails = {"failed while saving the fingerprint card identification report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void populateData()
	{
		NormalizedPersonInfo normalizedPersonInfo = new NormalizedPersonInfo(personId, civilBiometricsId,
		                                                                     criminalBiometricsId, personInfo,
		                                                                     null,
		                                                                     resources.getString("label.male"),
		                                                                     resources.getString("label.female"));
		
		String faceImageBase64 = normalizedPersonInfo.getFaceImageBase64();
		if(faceImageBase64 != null)
		{
			byte[] bytes = Base64.getDecoder().decode(faceImageBase64);
			ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
			                           resources.getString("label.personPhoto"),
			                           resources.getString("label.contextMenu.showImage"), false);
		}
		
		lblPersonId.setText(normalizedPersonInfo.getPersonIdLabel());
		lblCivilBiometricsId.setText(normalizedPersonInfo.getCivilBiometricsIdLabel());
		lblCriminalBiometricsId.setText(normalizedPersonInfo.getCriminalBiometricsIdLabel());
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
		
		Map<Integer, String> dialogTitleMap = GuiUtils.constructFingerprintDialogTitles(resources);
		Map<Integer, ImageView> imageViewMap = GuiUtils.constructFingerprintImageViewMap(ivRightThumb, ivRightIndex,
		                                                                                 ivRightMiddle, ivRightRing,
		                                                                                 ivRightLittle, ivLeftThumb,
		                                                                                 ivLeftIndex, ivLeftMiddle,
		                                                                                 ivLeftRing, ivLeftLittle);
		
		fingerprintImages.forEach((position, fingerprintImage) ->
		{
		    ImageView imageView = imageViewMap.get(position);
		    String dialogTitle = dialogTitleMap.get(position);
		
		    imageView.setImage(fingerprintImage);
		    GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
		                               dialogTitle, resources.getString("label.contextMenu.showImage"),
		                               false);
		});
		
		fingerprintCardIdentificationRecordReport =
					new FingerprintCardIdentificationRecordReport(faceImageBase64, normalizedPersonInfo.getFirstName(),
		                                                          normalizedPersonInfo.getFatherName(),
		                                                          normalizedPersonInfo.getGrandfatherName(),
		                                                          normalizedPersonInfo.getFamilyName(),
		                                                          normalizedPersonInfo.getGenderLabel(),
		                                                          normalizedPersonInfo.getNationalityLabel(),
		                                                          normalizedPersonInfo.getOccupation(),
		                                                          normalizedPersonInfo.getBirthPlace(),
		                                                          normalizedPersonInfo.getBirthDateLabel(),
		                                                          normalizedPersonInfo.getPersonIdLabel(),
		                                                          normalizedPersonInfo.getPersonTypeLabel(),
		                                                          normalizedPersonInfo.getCivilBiometricsIdLabel(),
		                                                          normalizedPersonInfo.getCriminalBiometricsIdLabel(),
		                                                          normalizedPersonInfo.getDocumentId(),
		                                                          normalizedPersonInfo.getDocumentTypeLabel(),
		                                                          normalizedPersonInfo.getDocumentIssuanceDateLabel(),
		                                                          normalizedPersonInfo.getDocumentExpiryDateLabel(),
		                                                          fingerprintBase64Images);
	}
	
	private void printDeadPersonRecordReport(JasperPrint jasperPrint)
	{
		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00008.getCode();
		    String[] errorDetails = {"failed while printing the dead person record report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveDeadPersonRecordReportAsPDF(JasperPrint jasperPrint, OutputStream pdfOutputStream)
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint, pdfOutputStream);
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    showSuccessNotification(resources.getString("message.savingAsPDF.success"));
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00009.getCode();
		    String[] errorDetails = {"failed while saving the fingerprint card identification report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}