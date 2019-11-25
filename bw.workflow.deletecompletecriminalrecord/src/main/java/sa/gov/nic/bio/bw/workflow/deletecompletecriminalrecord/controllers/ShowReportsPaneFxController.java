package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowReportDialogFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.utils.DeleteCompleteCriminalRecordErrorCodes;

import java.util.List;

@FxmlFile("showReports.fxml")
public class ShowReportsPaneFxController extends WizardStepFxControllerBase
{
	@Input private Integer resultsTotalCount;
	@Input private List<ConvictedReport> convictedReports;
	@Input private Long criminalBiometricsId;
	@Output private Integer recordsPerPage;
	@Output private Integer pageIndex;
	
	@FXML private TitledPane tpSearchResults;
	@FXML private Pane paneTable;
	@FXML private Pagination pagination;
	@FXML private TableView<ConvictedReport> tvConvictedReports;
	@FXML private TableColumn<ConvictedReport, String> tcSequence;
	@FXML private TableColumn<ConvictedReport, String> tcName;
	@FXML private TableColumn<ConvictedReport, String> tcPersonId;
	@FXML private TableColumn<ConvictedReport, String> tcDocumentId;
	@FXML private TableColumn<ConvictedReport, String> tcNationality;
	@FXML private TableColumn<ConvictedReport, String> tcGender;
	@FXML private TableColumn<ConvictedReport, String> tcRegistrationDate;
	@FXML private Label lblConvictedReportsPlaceHolder;
	@FXML private ProgressIndicator piConvictedReportsPlaceHolder;
	@FXML private Button btnStartOver;
	@FXML private Button btnDeleteReport;
	@FXML private Button btnShowSelectedReport;
	
	private Node paginationControlBox;
	private boolean tableInitialized = false;
	
	@Override
	protected void onAttachedToScene()
	{
		tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
		                        AppUtils.localizeNumbers(String.valueOf(resultsTotalCount)) + ")");
		
		pagination.setMaxPageIndicatorCount(AppConstants.TABLE_PAGINATION_PAGES_PER_ITERATION);
		GuiUtils.localizePagination(pagination);
		pagination.setPageFactory(this::createPage);
		paginationControlBox = pagination.lookup(".control-box");
		pagination.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			// disable controlling the pagination by keyboard
			if(event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) event.consume();
		});
		
		btnShowSelectedReport.disableProperty().bind(Bindings.size(tvConvictedReports.getSelectionModel()
				                                                   .getSelectedItems()).isEqualTo(0));
		
		EventHandler<? super KeyEvent> keyReleasedEventHandler = keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnShowSelectedReport.fire();
		};
		EventHandler<? super MouseEvent> mouseEventHandler = mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnShowSelectedReport.fire();
			}
		};
		
		tvConvictedReports.setOnKeyReleased(keyReleasedEventHandler);
		tvConvictedReports.setOnMouseClicked(mouseEventHandler);
		
		tcSequence.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    int sequence = convictedReport.getSequence();
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(sequence)));
		});
		
		tcName.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    Name name = convictedReport.getSubjtName();
		    return new SimpleStringProperty(AppUtils.constructName(name));
		});
		
		tcPersonId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    Long subjSamisId = convictedReport.getSubjSamisId();
		    if(subjSamisId == null) return null;
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(subjSamisId)));
		});
		
		tcDocumentId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    String documentId = convictedReport.getSubjDocId();
		    if(documentId == null) return null;
		    return new SimpleStringProperty(AppUtils.localizeNumbers(documentId));
		});
		
		tcNationality.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		
		    @SuppressWarnings("unchecked")
		    List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		    Integer nationalityCode = convictedReport.getSubjNationalityCode();
		    if(nationalityCode == null) return null;
		
		    if(nationalityCode == 0)
		    {
		        return new SimpleStringProperty(resources.getString("combobox.unknownNationality"));
		    }
		
		    Country countryBean = null;
		
		    for(Country country : countries)
		    {
		        if(country.getCode() == nationalityCode)
		        {
		            countryBean = country;
		            break;
		        }
		    }
		
		    if(countryBean != null)
		    {
		        boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		        String nationality = arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN();
		        return new SimpleStringProperty(nationality);
		    }
		
		    return null;
		});
		
		tcGender.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    String subjGender = convictedReport.getSubjGender();
		    if(subjGender == null) return null;
		
		    Gender gender = "F".equals(subjGender) ? Gender.FEMALE : Gender.MALE;
		    return new SimpleStringProperty(gender.toString());
		});
		
		tcRegistrationDate.setCellValueFactory(param ->
		{
		    boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		    ConvictedReport convictedReport = param.getValue();
		    Long reportDate = convictedReport.getReportDate();
		    if(reportDate == null) return null;
		    return new SimpleStringProperty(AppUtils.formatHijriDateSimple(reportDate, rtl));
		});
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
				Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
		
		pageIndex = 0;
		recordsPerPage = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE;
		initPagination();
		setTableData();
		tableInitialized = true;
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			initPagination();
			setTableData();
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		if(paginationControlBox != null) paginationControlBox.setDisable(bShow);
		
		piConvictedReportsPlaceHolder.setVisible(bShow);
		lblConvictedReportsPlaceHolder.setVisible(!bShow);
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
				ConvictedReport selectedItem = tvConvictedReports.getSelectionModel().getSelectedItem();
				controller.setReportNumber(selectedItem.getReportNumber());
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = DeleteCompleteCriminalRecordErrorCodes.C015_00001.getCode();
			String[] errorDetails = {"Failed to load (" + ShowReportDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
		}
	}
	
	private void initPagination()
	{
		if(resultsTotalCount != null)
		{
			tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
					                        AppUtils.localizeNumbers(String.valueOf(resultsTotalCount)) + ")");
			
			int pageCount = (resultsTotalCount - 1) / AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE + 1;
			if(pageIndex > pageCount) pageIndex = pageCount;
			pagination.setPageCount(pageCount);
			pagination.setCurrentPageIndex(pageIndex);
		}
	}
	
	private void setTableData()
	{
		if(convictedReports != null)
		{
			for(int i = 0; i < convictedReports.size(); i++)
			{
				int sequence = AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE * pageIndex + i + 1;
				ConvictedReport convictedReport = convictedReports.get(i);
				convictedReport.setSequence(sequence);
			}
			
			tvConvictedReports.getItems().setAll(convictedReports);
			tvConvictedReports.requestFocus();
		}
	}
	
	private Node createPage(int pageIndex)
	{
		if(tableInitialized)
		{
			tvConvictedReports.getItems().clear();
			this.pageIndex = pageIndex;
			resultsTotalCount = null;
			convictedReports = null;
			tpSearchResults.setText(resources.getString("label.searchResults") + " (" +
					                                                    AppUtils.localizeNumbers("0") + ")");
			continueWorkflow();
		}
		
		return paneTable;
	}
	
	@FXML
	private void onDeleteReportButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("deleteCompleteCriminalRecord.confirmation.header");
		String contentText = String.format(
							resources.getString("deleteCompleteCriminalRecord.confirmation.message"),
                            AppUtils.localizeNumbers(AppUtils.localizeNumbers(String.valueOf(criminalBiometricsId))));
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		if(!confirmed) return;
		
		goNext();
	}
}