package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.beans.FingerprintCardIdentificationRecordReport;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.tasks.BuildFingerprintCardIdentificationRecordReportTask;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Map;

@FxmlFile("extendedInquiryByFingerprintsResult.fxml")
public class ExtendedInquiryByFingerprintsResultPaneFxController extends InquiryByFingerprintsResultPaneFxController
{
	@Input private Map<Integer, String> fingerprintBase64Images;
	
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;
	
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		super.onAttachedToScene();
		
		fileChooser.setTitle(resources.getString("fileChooser.saveRecordAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.saveRecordAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		boolean fingerprintHit = status == Status.HIT;
		GuiUtils.showNode(btnPrintRecord, fingerprintHit);
		GuiUtils.showNode(btnSaveRecordAsPDF, fingerprintHit);
	}
	
	@FXML
	private void onPrintRecordButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		BuildFingerprintCardIdentificationRecordReportTask task = createNewBuildTask();
		
		task.setOnSucceeded(event ->
		{
		    JasperPrint value = task.getValue();
		    printDeadPersonRecordReport(value);
		});
		task.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = task.getException();
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00002.getCode();
		    String[] errorDetails = {"failed while building the fingerprint card identification report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onSaveRecordAsPdfButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			BuildFingerprintCardIdentificationRecordReportTask task = createNewBuildTask();
			task.setOnSucceeded(event ->
			{
			    JasperPrint value = task.getValue();
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
			
			        String errorCode = FingerprintCardIdentificationErrorCodes.C013_00003.getCode();
			        String[] errorDetails =
			                {"failed while saving the fingerprint card identification report as PDF!"};
			        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
			    }
			});
			task.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintRecord, true);
			    GuiUtils.showNode(btnSaveRecordAsPDF, true);
			
			    Throwable exception = task.getException();
			
			    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00004.getCode();
			    String[] errorDetails = {"failed while building the fingerprint card identification report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(task);
		}
	}
	
	private BuildFingerprintCardIdentificationRecordReportTask createNewBuildTask()
	{
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(btnPrintRecord, false);
		GuiUtils.showNode(btnSaveRecordAsPDF, false);
		GuiUtils.showNode(piProgress, true);
		
		Gender gender = normalizedPersonInfo.getGender();
		Country nationality = normalizedPersonInfo.getNationality();
		LocalDate birthDate = normalizedPersonInfo.getBirthDate();
		Long personId = normalizedPersonInfo.getPersonId();
		PersonType personType = normalizedPersonInfo.getPersonType();
		String documentId = normalizedPersonInfo.getDocumentId();
		DocumentType documentType = normalizedPersonInfo.getDocumentType();
		LocalDate documentIssuanceDate = normalizedPersonInfo.getDocumentIssuanceDate();
		LocalDate documentExpiryDate = normalizedPersonInfo.getDocumentExpiryDate();
		
		FingerprintCardIdentificationRecordReport fingerprintCardIdentificationRecordReport =
			new FingerprintCardIdentificationRecordReport(
				civilBiometricsId != null ? AppUtils.localizeNumbers(String.valueOf(civilBiometricsId)) : null,
				criminalBiometricsId != null ? AppUtils.localizeNumbers(String.valueOf(criminalBiometricsId)) : null,
				normalizedPersonInfo.getFacePhotoBase64(),
				normalizedPersonInfo.getFirstName(),
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
		
		return new BuildFingerprintCardIdentificationRecordReportTask(fingerprintCardIdentificationRecordReport);
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
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00005.getCode();
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
		
		    showSuccessNotification(resources.getString("message.savingAsPDF.success"));
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
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00006.getCode();
		    String[] errorDetails = {"failed while saving the fingerprint card identification report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}