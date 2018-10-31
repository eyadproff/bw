package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.beans.DeadPersonRecordReport;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.tasks.BuildDeadPersonRecordReportTask;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.utils.PrintDeadPersonRecordPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showRecord.fxml")
public class ShowRecordPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private Long recordId;
	@Input(alwaysRequired = true) private DeadPersonRecord deadPersonRecord;
	@Input private PersonInfo personInfo;
	@Input private Map<Integer, String> fingerprintImages;
	
	@FXML private VBox paneImage;
	@FXML private ImageViewPane paneImageView;
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
	@FXML private Label lblEnrollerId;
	@FXML private Label lblEnrollmentTime;
	@FXML private Label lblRecordId;
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
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;
	
	private DeadPersonRecordReport deadPersonRecordReport;
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveRecordAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
								resources.getString("fileChooser.saveRecordAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		populateData(recordId, deadPersonRecord, personInfo, fingerprintImages);
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		startOver();
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
			BuildDeadPersonRecordReportTask buildDeadPersonRecordReportTask =
														new BuildDeadPersonRecordReportTask(deadPersonRecordReport);
			
			buildDeadPersonRecordReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildDeadPersonRecordReportTask.getValue();
			    jasperPrint.set(value);
			    printDeadPersonRecordReport(value);
			});
			buildDeadPersonRecordReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintRecord, true);
			    GuiUtils.showNode(btnSaveRecordAsPDF, true);
			
			    Throwable exception = buildDeadPersonRecordReportTask.getException();
			
			    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00003.getCode();
			    String[] errorDetails = {"failed while building the dead person record report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildDeadPersonRecordReportTask);
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
				BuildDeadPersonRecordReportTask buildDeadPersonRecordReportTask =
														new BuildDeadPersonRecordReportTask(deadPersonRecordReport);
				buildDeadPersonRecordReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildDeadPersonRecordReportTask.getValue();
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
				
				        String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00004.getCode();
				        String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildDeadPersonRecordReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnStartOver, true);
				    GuiUtils.showNode(btnPrintRecord, true);
				    GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				    Throwable exception = buildDeadPersonRecordReportTask.getException();
				
				    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00005.getCode();
				    String[] errorDetails = {"failed while building the dead person record report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildDeadPersonRecordReportTask);
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
					
					String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00006.getCode();
					String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void populateData(Long recordIdLong, DeadPersonRecord deadPersonRecord,
	                          PersonInfo personInfo, Map<Integer, String> fingerprintsImages)
	{
		NormalizedPersonInfo normalizedPersonInfo = new NormalizedPersonInfo(deadPersonRecord.getSamisId(),
		                                                                     null, null,
		                                                                     personInfo, null,
		                                                                     resources.getString("label.male"),
		                                                                     resources.getString("label.female"));
		
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
		
		String recordId = null;
		String enrollerId = null;
		String enrollmentTime = null;
		
		if(recordIdLong != null)
		{
			recordId = AppUtils.localizeNumbers(String.valueOf(recordIdLong));
			lblRecordId.setText(recordId);
		}
		
		
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		String inquirerId = AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()));
		
		String sEnrollerId = deadPersonRecord.getOperatorId();
		if(sEnrollerId != null && !sEnrollerId.trim().isEmpty())
		{
			enrollerId = AppUtils.localizeNumbers(sEnrollerId);
			lblEnrollerId.setText(enrollerId);
		}
		
		Long enrollmentTimeLong = deadPersonRecord.getEnrollmentDate();
		if(enrollmentTimeLong != null)
		{
			enrollmentTime = AppUtils.formatHijriGregorianDateTime(enrollmentTimeLong * 1000);
			lblEnrollmentTime.setText(enrollmentTime);
		}
		
		String faceImageBase64 = deadPersonRecord.getSubjFace();
		if(faceImageBase64 != null)
		{
			byte[] bytes = Base64.getDecoder().decode(faceImageBase64);
			ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
			                           resources.getString("label.deadPersonPhoto"),
			                           resources.getString("label.contextMenu.showImage"), false);
		}

		if(fingerprintsImages != null)
		{
			Map<Integer, String> dialogTitleMap = GuiUtils.constructFingerprintDialogTitles(resources);
			Map<Integer, ImageView> imageViewMap = GuiUtils.constructFingerprintImageViewMap(ivRightThumb, ivRightIndex,
			                                                                                 ivRightMiddle, ivRightRing,
			                                                                                 ivRightLittle, ivLeftThumb,
			                                                                                 ivLeftIndex, ivLeftMiddle,
			                                                                                 ivLeftRing, ivLeftLittle);
			
			fingerprintsImages.forEach((position, fingerprintImage) ->
			{
			    ImageView imageView = imageViewMap.get(position);
			    String dialogTitle = dialogTitleMap.get(position);
			
			    byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
			    imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
			    GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
			                               dialogTitle, resources.getString("label.contextMenu.showImage"),
			                               false);
			});
		}
		
		deadPersonRecordReport = new DeadPersonRecordReport(recordId, enrollerId, inquirerId, enrollmentTime,
		                                                    faceImageBase64, normalizedPersonInfo.getFirstName(),
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
		                                                    normalizedPersonInfo.getDocumentId(),
				                                            normalizedPersonInfo.getDocumentTypeLabel(),
				                                            normalizedPersonInfo.getDocumentIssuanceDateLabel(),
				                                            normalizedPersonInfo.getDocumentExpiryDateLabel(),
		                                                    fingerprintsImages);
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
		
		    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00007.getCode();
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
		
		    showSuccessNotification(resources.getString("printDeadPersonRecord.savingAsPDF.success.message"));
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00008.getCode();
		    String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}