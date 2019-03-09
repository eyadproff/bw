package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BuildConvictedReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByReportNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showReportDialog.fxml")
public class ShowReportDialogFxController extends BodyFxControllerBase
{
	@FXML private ConvictedReportNestedFxController convictedReportNestedPaneController;
	@FXML private ScrollPane paneReport;
	@FXML private Pane convictedReportNestedPane;
	@FXML private Pane paneLoadingInProgress;
	@FXML private Pane paneLoadingError;
	@FXML private Pane buttonPane;
	@FXML private ProgressIndicator piPrinting;
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;
	@FXML private Dialog<ButtonType> dialog;
	
	private FileChooser fileChooser = new FileChooser();
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	
	private Long reportNumber;
	private ConvictedReport convictedReport;
	private Map<Integer, String> fingerprintBase64Images;
	
	@Override
	protected void initialize()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		dialog.setOnShown(event ->
		{
			setData(ConvictedReportInquiryByReportNumberWorkflowTask.class, "reportNumber", reportNumber);
			boolean success = executeUiTask(ConvictedReportInquiryByReportNumberWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					convictedReport = getData("convictedReport");
					List<Finger> subjFingers = convictedReport.getSubjFingers();
					List<Integer> subjMissingFingers = convictedReport.getSubjMissingFingers();
					
					setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					        "fingerprints", subjFingers);
					setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					        "missingFingerprints", subjMissingFingers);
					
					boolean success = executeUiTask(
						ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class, new SuccessHandler()
					{
					    @Override
					    protected void onSuccess()
					    {
					        fingerprintBase64Images = getData("fingerprintBase64Images");
					        populateData();
					    }
					}, throwable ->
					{
					    GuiUtils.showNode(paneLoadingInProgress, false);
					    GuiUtils.showNode(paneLoadingError, true);
					
					    String errorCode = CommonsErrorCodes.C008_00029.getCode();
					    String[] errorDetails = {"failed to load the fingerprints!"};
					    Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
					});
					
					if(!success)
					{
						GuiUtils.showNode(paneLoadingInProgress, false);
						GuiUtils.showNode(paneLoadingError, true);
					}
				}
			}, throwable ->
			{
			    GuiUtils.showNode(paneLoadingInProgress, false);
			    GuiUtils.showNode(paneLoadingError, true);
			
			    String errorCode = CommonsErrorCodes.C008_00030.getCode();
			    String[] errorDetails = {"failed to load the convicted report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
			});
			
			if(!success)
			{
				GuiUtils.showNode(paneLoadingInProgress, false);
				GuiUtils.showNode(paneLoadingError, true);
			}
			
			// workaround to center buttons and remove extra spaces
			ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
			buttonBar.getButtons().setAll(buttonPane);
			HBox hBox = (HBox) buttonBar.lookup(".container");
			hBox.setPadding(new Insets(0.0, 10.0, 10.0, 10.0));
			hBox.getChildren().remove(0);
			
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.sizeToScene();
			stage.setMinWidth(stage.getWidth());
			stage.setMinHeight(stage.getHeight());
		});
	}
	
	private void populateData()
	{
		convictedReportNestedPaneController.populateConvictedReportData(convictedReport, fingerprintBase64Images);
		
		btnPrintReport.setDisable(false);
		btnSaveReportAsPDF.setDisable(false);
		GuiUtils.showNode(paneLoadingInProgress, false);
		GuiUtils.showNode(paneReport, true);
	}
	
	public void setReportNumber(Long reportNumber)
	{
		this.reportNumber = reportNumber;
	}
	
	public void show()
	{
		dialog.show();
	}
	
	@FXML
	private void onPrintReportButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(btnPrintReport, false);
		GuiUtils.showNode(btnSaveReportAsPDF, false);
		GuiUtils.showNode(piPrinting, true);
		
		if(jasperPrint.get() == null)
		{
			BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
			                                                                                 fingerprintBase64Images);
			buildConvictedReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildConvictedReportTask.getValue();
			    jasperPrint.set(value);
			    printConvictedReport(value);
			});
			buildConvictedReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piPrinting, false);
			    GuiUtils.showNode(btnPrintReport, true);
			    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			    Throwable exception = buildConvictedReportTask.getException();
			
			    String errorCode = CommonsErrorCodes.C008_00031.getCode();
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
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			GuiUtils.showNode(btnPrintReport, false);
			GuiUtils.showNode(btnSaveReportAsPDF, false);
			GuiUtils.showNode(piPrinting, true);
			
			if(jasperPrint.get() == null)
			{
				BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
			                                                                                 fingerprintBase64Images);
				buildConvictedReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildConvictedReportTask.getValue();
				    jasperPrint.set(value);
				    try
				    {
				        saveConvictedReportAsPDF(value, selectedFile);
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piPrinting, false);
				        GuiUtils.showNode(btnPrintReport, true);
				        GuiUtils.showNode(btnSaveReportAsPDF, true);
				
				        String errorCode = CommonsErrorCodes.C008_00032.getCode();
				        String[] errorDetails = {"failed while saving the convicted report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildConvictedReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piPrinting, false);
				    GuiUtils.showNode(btnPrintReport, true);
				    GuiUtils.showNode(btnSaveReportAsPDF, true);
				
				    Throwable exception = buildConvictedReportTask.getException();
				
				    String errorCode = CommonsErrorCodes.C008_00033.getCode();
				    String[] errorDetails = {"failed while building the convicted report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildConvictedReportTask);
			}
			else
			{
				try
				{
					saveConvictedReportAsPDF(jasperPrint.get(), selectedFile);
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piPrinting, false);
					GuiUtils.showNode(btnPrintReport, true);
					GuiUtils.showNode(btnSaveReportAsPDF, true);
					
					String errorCode = CommonsErrorCodes.C008_00034.getCode();
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
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		
		    String errorCode = CommonsErrorCodes.C008_00035.getCode();
		    String[] errorDetails = {"failed while printing the convicted report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveConvictedReportAsPDF(JasperPrint jasperPrint, File selectedFile) throws FileNotFoundException
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
		                                                                       new FileOutputStream(selectedFile));
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			String title = resources.getString("showConvictedReport.savingAsPDF.success.title");
			String contentText = resources.getString("showConvictedReport.savingAsPDF.success.message");
			String buttonText = resources.getString("showConvictedReport.savingAsPDF.success.button");
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			
			try
			{
				Desktop.getDesktop().open(selectedFile);
			}
			catch(Exception e)
			{
				LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
			}
			
			DialogUtils.showInformationDialog(stage, Context.getCoreFxController(), title, null, contentText,
			                                  buttonText, rtl);
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = CommonsErrorCodes.C008_00036.getCode();
		    String[] errorDetails = {"failed while saving the convicted report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}