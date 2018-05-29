package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.tasks.BuildConvictedReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ShowReportPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_CONVICTED_REPORT_NUMBER = "CONVICTED_REPORT_NUMBER";
	public static final String KEY_CONVICTED_REPORT_DATE = "CONVICTED_REPORT_DATE";
	
	@FXML private TextField txtReportNumber;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;
	
	private FileChooser fileChooser = new FileChooser();
	private ConvictedReport convictedReport;
	private Map<Integer, String> fingerprintImages;
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showReport.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnPrintReport);
		GuiUtils.makeButtonClickableByPressingEnter(btnSaveReportAsPDF);
	}
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
								resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Long reportNumber = (Long) uiInputData.get(KEY_CONVICTED_REPORT_NUMBER);
			Long reportDate = (Long) uiInputData.get(KEY_CONVICTED_REPORT_DATE);
			txtReportNumber.setText(String.valueOf(reportNumber));
			
			Long GeneralFileNumber = (Long)
									uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
			txtGeneralFileNumber.setText(String.valueOf(GeneralFileNumber));
			
			convictedReport = (ConvictedReport) uiInputData.get(
														ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
			convictedReport.setReportNumber(reportNumber);
			convictedReport.setReportDate(reportDate);
			
			@SuppressWarnings("unchecked")
			Map<Integer, String> fingerprintImages = (Map<Integer, String>)
								uiInputData.get(FingerprintCapturingFxController.KEY_FINGERPRINTS_IMAGES);
			this.fingerprintImages = fingerprintImages;
		}
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		String headerText = resources.getString("printConvictedPresent.startingOver.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
	
	@FXML
	private void onPrintReportButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(btnPrintReport, false);
		GuiUtils.showNode(btnSaveReportAsPDF, false);
		GuiUtils.showNode(piProgress, true);
		
		if(jasperPrint.get() == null)
		{
			BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
			                                                                                 fingerprintImages);
			buildConvictedReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildConvictedReportTask.getValue();
			    jasperPrint.set(value);
			    printConvictedReport(value);
			});
			buildConvictedReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintReport, true);
			    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			    Throwable exception = buildConvictedReportTask.getException();
			
			    String errorCode = RegisterConvictedPresentErrorCodes.C007_00004.getCode();
			    String[] errorDetails = {"failed while building the convicted report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildConvictedReportTask);
		}
		else printConvictedReport(jasperPrint.get());
	}
	
	@FXML
	private void onSaveReportAsPdfButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnPrintReport, false);
			GuiUtils.showNode(btnSaveReportAsPDF, false);
			GuiUtils.showNode(piProgress, true);
			
			if(jasperPrint.get() == null)
			{
				BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
				                                                                                 fingerprintImages);
				buildConvictedReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildConvictedReportTask.getValue();
				    jasperPrint.set(value);
					try
					{
						saveConvictedReportAsPDF(value, new FileOutputStream(selectedFile));
					}
					catch(Exception e)
					{
						GuiUtils.showNode(piProgress, false);
						GuiUtils.showNode(btnStartOver, true);
						GuiUtils.showNode(btnPrintReport, true);
						GuiUtils.showNode(btnSaveReportAsPDF, true);
						
						String errorCode = RegisterConvictedPresentErrorCodes.C007_00005.getCode();
						String[] errorDetails = {"failed while saving the convicted report as PDF!"};
						Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
					}
				});
				buildConvictedReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnStartOver, true);
				    GuiUtils.showNode(btnPrintReport, true);
				    GuiUtils.showNode(btnSaveReportAsPDF, true);
				    
				    Throwable exception = buildConvictedReportTask.getException();
				    
				    String errorCode = RegisterConvictedPresentErrorCodes.C007_00006.getCode();
				    String[] errorDetails = {"failed while building the convicted report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildConvictedReportTask);
			}
			else
			{
				try
				{
					saveConvictedReportAsPDF(jasperPrint.get(), new FileOutputStream(selectedFile));
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piProgress, false);
					GuiUtils.showNode(btnStartOver, true);
					GuiUtils.showNode(btnPrintReport, true);
					GuiUtils.showNode(btnSaveReportAsPDF, true);
					
					String errorCode = RegisterConvictedPresentErrorCodes.C007_00007.getCode();
					String[] errorDetails = {"failed while saving the convicted report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void printConvictedReport(JasperPrint jasperPrint)
	{
		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
		   GuiUtils.showNode(piProgress, false);
		   GuiUtils.showNode(btnStartOver, true);
		   GuiUtils.showNode(btnPrintReport, true);
		   GuiUtils.showNode(btnSaveReportAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		    
		    String errorCode = RegisterConvictedPresentErrorCodes.C007_00008.getCode();
		    String[] errorDetails = {"failed while printing the convicted report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveConvictedReportAsPDF(JasperPrint jasperPrint, OutputStream pdfOutputStream)
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint, pdfOutputStream);
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		    
		    showSuccessNotification(resources.getString("printConvictedPresent.savingAsPDF.success.message"));
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = RegisterConvictedPresentErrorCodes.C007_00009.getCode();
		    String[] errorDetails = {"failed while saving the convicted report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}