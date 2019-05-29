package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BuildConvictedReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByReportNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryBySearchCriteriaWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@FxmlFile("showReportDialog.fxml")
public class ShowReportDialogFxController extends BodyFxControllerBase
{
	@FXML private ConvictedReportNestedFxController convictedReportNestedPaneController;
	@FXML private ScrollPane paneReport;
	@FXML private VBox paneReportHistory;
	@FXML private Pane convictedReportNestedPane;
	@FXML private Pane paneLoadingInProgress;
	@FXML private Pane paneLoadingError;
	@FXML private Pane buttonPane;
	@FXML private TableView<ConvictedReport> tvReportHistory;
	@FXML private TableColumn<ConvictedReport, ConvictedReport> tcReportSequence;
	@FXML private TableColumn<ConvictedReport, String> tcReportNumber;
	@FXML private Label lblWatermarkOldVersion;
	@FXML private Label lblWatermarkDeletedReport;
	@FXML private ProgressIndicator piPrinting;
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;
	@FXML private Dialog<ButtonType> dialog;
	
	private FileChooser fileChooser = new FileChooser();
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	
	private Long reportNumber;
	private ConvictedReport convictedReport;
	private Map<Integer, String> fingerprintBase64Images;
	private Map<Integer, String> palmBase64Images;
	
	@Override
	protected void initialize()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		GuiUtils.initSequenceTableColumn(tcReportSequence);
		tcReportSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcReportNumber.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    Long reportNumber = convictedReport.getReportNumber();
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(reportNumber)));
		});
		
		dialog.setOnShown(event ->
        {
	        setData(ConvictedReportInquiryByReportNumberWorkflowTask.class, "reportNumber", reportNumber);
	
	        boolean success = executeUiTask(ConvictedReportInquiryByReportNumberWorkflowTask.class, new SuccessHandler()
	        {
		        @Override
		        protected void onSuccess()
		        {
			        convictedReport = getData("convictedReport");
			
			        Long rootReportNumber = convictedReport.getRootReportNumber();
			        boolean rootReport = rootReportNumber == null || rootReportNumber.equals(0L);
			        
			        setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
			                "rootReportNumber", rootReport ? convictedReport.getReportNumber() :
					                                                   convictedReport.getRootReportNumber());
			        setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
			                "showOldReports", true);
			        setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
			                "showDeletedReports", true);
			        setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
			                "recordsPerPage", 1000);
			        setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
			                "pageIndex", 0);
			
			        boolean success = executeUiTask(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
			                                        new SuccessHandler()
			        {
				        @Override
				        protected void onSuccess()
				        {
					        List<ConvictedReport> convictedReports = getData("convictedReports");
					        if(rootReport)
					        {
					        	convictedReports.add(convictedReport);
						        convictedReports.sort(Comparator.comparingLong(ConvictedReport::getReportDate));
						        handleConvictedReport(convictedReports);
					        }
					        else
					        {
						        setData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
						                "reportNumber", rootReportNumber);
						
						        boolean success = executeUiTask(ConvictedReportInquiryByReportNumberWorkflowTask.class,
						                                        new SuccessHandler()
						        {
							        @Override
							        protected void onSuccess()
							        {
								        ConvictedReport rootConvictedReport = getData("convictedReport");
								
								        convictedReports.add(rootConvictedReport);
								        convictedReports.sort(Comparator.comparingLong(ConvictedReport::getReportDate));
								        handleConvictedReport(convictedReports);
							        }
						        }, throwable ->
						        {
							        GuiUtils.showNode(paneLoadingInProgress, false);
							        GuiUtils.showNode(paneLoadingError, true);
							
							        String errorCode = CommonsErrorCodes.C008_00040.getCode();
							        String[] errorDetails = {"failed to load the root convicted report! Report number" +
								                                                        " (" + rootReportNumber + ")"};
							        Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
						        });
						
						        if(!success)
						        {
							        GuiUtils.showNode(paneLoadingInProgress, false);
							        GuiUtils.showNode(paneLoadingError, true);
						        }
					        }
				        }
			        }, throwable ->
			        {
				        GuiUtils.showNode(paneLoadingInProgress, false);
				        GuiUtils.showNode(paneLoadingError, true);
				
				        String errorCode = CommonsErrorCodes.C008_00041.getCode();
				        String[] errorDetails = {"failed to load the convicted report! Report number (" +
						                                                                            reportNumber + ")"};
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
		        String[] errorDetails = {"failed to retrieve the convicted report by report number ( " +
		                                                                                            reportNumber + ")"};
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
	
	private void handleConvictedReport(List<ConvictedReport> convictedReports)
	{
		if(!convictedReports.isEmpty())
		{
			tvReportHistory.getItems().addAll(convictedReports);
			for(ConvictedReport convictedReport : convictedReports)
			{
				if(reportNumber.equals(convictedReport.getReportNumber()))
				{
					tvReportHistory.getSelectionModel().select(convictedReport);
					break;
				}
			}
			
			tvReportHistory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
			{
				if(newValue == null) return; // un-select action
				
				btnPrintReport.setDisable(true);
				btnSaveReportAsPDF.setDisable(true);
				tvReportHistory.setDisable(true);
				GuiUtils.showNode(lblWatermarkOldVersion, false);
				GuiUtils.showNode(lblWatermarkDeletedReport, false);
				GuiUtils.showNode(paneReport, false);
				GuiUtils.showNode(paneLoadingInProgress, true);
				
				setData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				        "reportNumber", newValue.getReportNumber());
				boolean success = executeUiTask(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                new SuccessHandler()
				{
					@Override
					protected void onSuccess()
					{
						convictedReport = getData("convictedReport");
						populateData();
						tvReportHistory.setDisable(false);
						tvReportHistory.requestFocus();
					}
				}, throwable ->
				{
					tvReportHistory.setDisable(false);
					GuiUtils.showNode(paneLoadingInProgress, false);
					GuiUtils.showNode(paneLoadingError, true);
					
					String errorCode = CommonsErrorCodes.C008_00038.getCode();
					String[] errorDetails = {"failed to retrieve the convicted report by report " +
							"number (" + newValue.getReportNumber() + ")"};
					Context.getCoreFxController().showErrorDialog(errorCode, throwable,
					                                              errorDetails);
				});
				
				if(!success)
				{
					GuiUtils.showNode(paneLoadingInProgress, false);
					GuiUtils.showNode(paneLoadingError, true);
					tvReportHistory.setDisable(false);
				}
			});
			
			segmentFingerprints();
		}
		else
		{
			GuiUtils.showNode(paneLoadingInProgress, false);
			GuiUtils.showNode(paneLoadingError, true);
			
			String errorCode = CommonsErrorCodes.C008_00039.getCode();
			String[] errorDetails = {"the returned list is empty for root report number" +
					" ( " + convictedReport.getRootReportNumber() + ")"};
			Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
		}
	}
	
	private void segmentFingerprints()
	{
		List<Finger> subjFingers = convictedReport.getSubjFingers();
		List<Integer> subjMissingFingers = convictedReport.getSubjMissingFingers();
		
		setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
		        "fingerprints", subjFingers);
		setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
		        "missingFingerprints", subjMissingFingers);
		
		boolean success = executeUiTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
																									new SuccessHandler()
		{
			@Override
			protected void onSuccess()
			{
				fingerprintBase64Images = getData("fingerprintBase64Images");
				GuiUtils.showNode(paneReportHistory, true);
				populateData();
				tvReportHistory.requestFocus();
			}
		}, throwable ->
		{
			GuiUtils.showNode(paneLoadingInProgress, false);
			GuiUtils.showNode(paneLoadingError, true);
			
			String errorCode = CommonsErrorCodes.C008_00029.getCode();
			String[] errorDetails = {"failed to load the fingerprints for report number ( " +
					reportNumber + ")"};
			Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
		});
		
		if(!success)
		{
			GuiUtils.showNode(paneLoadingInProgress, false);
			GuiUtils.showNode(paneLoadingError, true);
		}
	}
	
	private void populateData()
	{
		if(ConvictedReport.Status.ACTIVE.equals(convictedReport.getStatus()))
		{
			btnPrintReport.setDisable(false);
			btnSaveReportAsPDF.setDisable(false);
		}
		else if(ConvictedReport.Status.UPDATED.equals(convictedReport.getStatus()))
		{
			GuiUtils.showNode(lblWatermarkOldVersion, true);
		}
		else if(ConvictedReport.Status.DELETED.equals(convictedReport.getStatus()))
		{
			GuiUtils.showNode(lblWatermarkDeletedReport, true);
		}
		
		convictedReportNestedPaneController.populateConvictedReportData(convictedReport, fingerprintBase64Images,
		                                                                palmBase64Images);
		
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