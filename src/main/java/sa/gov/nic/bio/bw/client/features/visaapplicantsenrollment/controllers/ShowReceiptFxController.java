package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.controllers;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.tasks.BuildForeignEnrollmentReceiptTask;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.utils.VisaApplicantsEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantEnrollmentResponse;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showReceipt.fxml")
public class ShowReceiptFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private VisaApplicantInfo visaApplicantInfo;
	@Input(alwaysRequired = true) private VisaApplicantEnrollmentResponse visaApplicantEnrollmentResponse;
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintImages;
	
	@FXML private TextField txtRegistrationNumber;
	@FXML private Pane paneProgress;
	@FXML private Pane paneError;
	@FXML private SwingNode nodeBarcode;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintReceipt;
	@FXML private Button btnSaveReceiptAsPDF;
	
	private FileChooser fileChooser = new FileChooser();
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveReceiptAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
								resources.getString("fileChooser.saveReceiptAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		Long registrationId = visaApplicantInfo.getApplicantId();
		String sRegistrationId = String.valueOf(registrationId);
		txtRegistrationNumber.setText(sRegistrationId);
		
		@SuppressWarnings("unchecked") Task<Barcode> generatingBarcodeTask = new Task<Barcode>()
		{
			@Override
			protected Barcode call() throws Exception
			{
				Barcode barcode = BarcodeFactory.createCode128(sRegistrationId);
				barcode.setDrawingQuietSection(false);
				
				return barcode;
			}
		};
		generatingBarcodeTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(paneProgress, false);
		    GuiUtils.showNode(nodeBarcode, true);
		
		    Barcode barcode = generatingBarcodeTask.getValue();
		    SwingUtilities.invokeLater(() -> nodeBarcode.setContent(barcode));
		});
		generatingBarcodeTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(paneProgress, false);
		    GuiUtils.showNode(nodeBarcode, false);
		    GuiUtils.showNode(paneError, true);
		
		    Throwable exception = generatingBarcodeTask.getException();
		
		    String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00005.getCode();
		    String[] errorDetails = {"failed to generate the barcode for the number " + sRegistrationId};
		    reportNegativeTaskResponse(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(generatingBarcodeTask);
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		String headerText = resources.getString("visaApplicantsEnrollment.startingOver.confirmation.header");
		String contentText = resources.getString("visaApplicantsEnrollment.startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
	
	@FXML
	private void onPrintReceiptButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(btnPrintReceipt, false);
		GuiUtils.showNode(btnSaveReceiptAsPDF, false);
		GuiUtils.showNode(piProgress, true);
		
		if(jasperPrint.get() == null)
		{
			BuildForeignEnrollmentReceiptTask buildForeignEnrollmentReceiptTask =
											new BuildForeignEnrollmentReceiptTask(visaApplicantInfo, fingerprintImages);
			buildForeignEnrollmentReceiptTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildForeignEnrollmentReceiptTask.getValue();
			    jasperPrint.set(value);
			    printReceipt(value);
			});
			buildForeignEnrollmentReceiptTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintReceipt, true);
			    GuiUtils.showNode(btnSaveReceiptAsPDF, true);
			
			    Throwable exception = buildForeignEnrollmentReceiptTask.getException();
			
			    String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00006.getCode();
			    String[] errorDetails = {"failed while building the receipt!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildForeignEnrollmentReceiptTask);
		}
		else printReceipt(jasperPrint.get());
	}
	
	@FXML
	private void onSaveReceiptAsPdfButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnPrintReceipt, false);
			GuiUtils.showNode(btnSaveReceiptAsPDF, false);
			GuiUtils.showNode(piProgress, true);
			
			if(jasperPrint.get() == null)
			{
				BuildForeignEnrollmentReceiptTask buildForeignEnrollmentReceiptTask =
											new BuildForeignEnrollmentReceiptTask(visaApplicantInfo, fingerprintImages);
				buildForeignEnrollmentReceiptTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildForeignEnrollmentReceiptTask.getValue();
				    jasperPrint.set(value);
				    try
				    {
				        saveReceiptAsPDF(value, new FileOutputStream(selectedFile));
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piProgress, false);
				        GuiUtils.showNode(btnStartOver, true);
				        GuiUtils.showNode(btnPrintReceipt, true);
				        GuiUtils.showNode(btnSaveReceiptAsPDF, true);
				
				        String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00007.getCode();
				        String[] errorDetails = {"failed while saving the receipt as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildForeignEnrollmentReceiptTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnStartOver, true);
				    GuiUtils.showNode(btnPrintReceipt, true);
				    GuiUtils.showNode(btnSaveReceiptAsPDF, true);
				
				    Throwable exception = buildForeignEnrollmentReceiptTask.getException();
				
				    String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00008.getCode();
				    String[] errorDetails = {"failed while building the receipt!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildForeignEnrollmentReceiptTask);
			}
			else
			{
				try
				{
					saveReceiptAsPDF(jasperPrint.get(), new FileOutputStream(selectedFile));
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piProgress, false);
					GuiUtils.showNode(btnStartOver, true);
					GuiUtils.showNode(btnPrintReceipt, true);
					GuiUtils.showNode(btnSaveReceiptAsPDF, true);
					
					String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00009.getCode();
					String[] errorDetails = {"failed while saving the receipt as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void printReceipt(JasperPrint jasperPrint)
	{
		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnStartOver, true);
			GuiUtils.showNode(btnPrintReceipt, true);
			GuiUtils.showNode(btnSaveReceiptAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintReceipt, true);
		    GuiUtils.showNode(btnSaveReceiptAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		
		    String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00010.getCode();
		    String[] errorDetails = {"failed while printing the receipt!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveReceiptAsPDF(JasperPrint jasperPrint, OutputStream pdfOutputStream)
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint, pdfOutputStream);
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintReceipt, true);
		    GuiUtils.showNode(btnSaveReceiptAsPDF, true);
		
		    showSuccessNotification(resources.getString("visaApplicantsEnrollment.savingAsPDF.success.message"));
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintReceipt, true);
		    GuiUtils.showNode(btnSaveReceiptAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00011.getCode();
		    String[] errorDetails = {"failed while saving the receipt as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}