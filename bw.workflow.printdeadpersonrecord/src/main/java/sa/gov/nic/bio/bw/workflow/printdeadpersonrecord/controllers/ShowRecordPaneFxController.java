package sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans.DeadPersonRecord;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.beans.DeadPersonRecordReport;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.tasks.BuildDeadPersonRecordReportTask;
import sa.gov.nic.bio.bw.workflow.printdeadpersonrecord.utils.PrintDeadPersonRecordPresentErrorCodes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showRecord.fxml")
public class ShowRecordPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private Long recordId;
	@Input(alwaysRequired = true) private DeadPersonRecord deadPersonRecord;
	@Input private PersonInfo personInfo;
	@Input private Map<Integer, String> fingerprintBase64Images;
	
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
		
		populateData();
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
			
			    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00001.getCode();
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
				        saveDeadPersonRecordReportAsPDF(value, selectedFile);
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piProgress, false);
				        GuiUtils.showNode(btnStartOver, true);
				        GuiUtils.showNode(btnPrintRecord, true);
				        GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				        String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00002.getCode();
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
				
				    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00003.getCode();
				    String[] errorDetails = {"failed while building the dead person record report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildDeadPersonRecordReportTask);
			}
			else
			{
				try
				{
					saveDeadPersonRecordReportAsPDF(jasperPrint.get(), selectedFile);
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
			}
		}
	}
	
	private void populateData()
	{
		NormalizedPersonInfo normalizedPersonInfo = new NormalizedPersonInfo(personInfo);
		
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, normalizedPersonInfo.getFacePhotoBase64(), true,
		                               normalizedPersonInfo.getGender());
		GuiUtils.setLabelText(lblFirstName, normalizedPersonInfo.getFirstNameLabel());
		GuiUtils.setLabelText(lblFatherName, normalizedPersonInfo.getFatherNameLabel());
		GuiUtils.setLabelText(lblGrandfatherName, normalizedPersonInfo.getGrandfatherNameLabel());
		GuiUtils.setLabelText(lblFamilyName, normalizedPersonInfo.getFamilyNameLabel());
		GuiUtils.setLabelText(lblGender, normalizedPersonInfo.getGender());
		GuiUtils.setLabelText(lblNationality, normalizedPersonInfo.getNationality());
		GuiUtils.setLabelText(lblOccupation, normalizedPersonInfo.getOccupation());
		GuiUtils.setLabelText(lblBirthPlace, normalizedPersonInfo.getBirthPlace());
		GuiUtils.setLabelText(lblBirthDate, normalizedPersonInfo.getBirthDate());
		GuiUtils.setLabelText(lblPersonId, normalizedPersonInfo.getPersonId());
		GuiUtils.setLabelText(lblPersonType, normalizedPersonInfo.getPersonType());
		GuiUtils.setLabelText(lblDocumentId, normalizedPersonInfo.getDocumentId());
		GuiUtils.setLabelText(lblDocumentType, normalizedPersonInfo.getDocumentType());
		GuiUtils.setLabelText(lblDocumentIssuanceDate, normalizedPersonInfo.getDocumentIssuanceDate());
		GuiUtils.setLabelText(lblDocumentExpiryDate, normalizedPersonInfo.getDocumentExpiryDate());
		
		String sRecordId = null;
		String enrollerId = null;
		String enrollmentTime = null;
		
		if(recordId != null)
		{
			sRecordId = AppUtils.localizeNumbers(String.valueOf(recordId));
			lblRecordId.setText(sRecordId);
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
			enrollmentTime = AppUtils.formatHijriGregorianDateTime(enrollmentTimeLong);
			lblEnrollmentTime.setText(enrollmentTime);
		}
		
		String facePhotoBase64 = deadPersonRecord.getSubjFace();
		Gender gender = normalizedPersonInfo.getGender();
		Country nationality = normalizedPersonInfo.getNationality();
		LocalDate birthDate = normalizedPersonInfo.getBirthDate();
		Long personId = normalizedPersonInfo.getPersonId();
		PersonType personType = normalizedPersonInfo.getPersonType();
		String documentId = normalizedPersonInfo.getDocumentId();
		DocumentType documentType = normalizedPersonInfo.getDocumentType();
		LocalDate documentIssuanceDate = normalizedPersonInfo.getDocumentIssuanceDate();
		LocalDate documentExpiryDate = normalizedPersonInfo.getDocumentExpiryDate();
		
		deadPersonRecordReport = new DeadPersonRecordReport(sRecordId, enrollerId, inquirerId, enrollmentTime,
		                  facePhotoBase64, normalizedPersonInfo.getFirstName(),
		                  normalizedPersonInfo.getFatherName(),
		                  normalizedPersonInfo.getGrandfatherName(),
		                  normalizedPersonInfo.getFamilyName(),
		                  gender != null ? gender.toString() : null,
		                  nationality != null ? nationality.getArabicText() : null,
		                  normalizedPersonInfo.getOccupation(),
		                  normalizedPersonInfo.getBirthPlace(),
		                  birthDate != null ? AppUtils.formatHijriGregorianDate(birthDate) : null,
		                  personId != null ? AppUtils.localizeNumbers(String.valueOf(personId)) : null,
		                  personType != null ? personType.getArabicText() : null,
		                  documentId != null ? AppUtils.localizeNumbers(documentId) : null,
		                  documentType != null ? documentType.getArabicText() : null,
		                  documentIssuanceDate != null ? AppUtils.formatHijriGregorianDate(documentIssuanceDate) : null,
		                  documentExpiryDate != null ? AppUtils.formatHijriGregorianDate(documentExpiryDate) : null,
		                  fingerprintBase64Images);
		
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);
		
		GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb, ivRightIndex, ivRightMiddle,
		                                 ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle,
		                                 ivLeftRing, ivLeftLittle);
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
		
		    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00005.getCode();
		    String[] errorDetails = {"failed while printing the dead person record report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveDeadPersonRecordReportAsPDF(JasperPrint jasperPrint, File selectedFile)
																						throws FileNotFoundException
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
		                                                                       new FileOutputStream(selectedFile));
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    showSuccessNotification(resources.getString("printDeadPersonRecord.savingAsPDF.success.message"));
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
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00006.getCode();
		    String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}