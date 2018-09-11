package sa.gov.nic.bio.bw.client.features.convictedreportinquiry;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks.LookupTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvictedReportInquiryPaneFxController extends BodyFxControllerBase
{
	public static final String KEY_GENERAL_FILE_NUMBER = "GENERAL_FILE_NUMBER";
	
	private static final String FXML_SHOW_REPORT_DIALOG =
								"sa/gov/nic/bio/bw/client/features/convictedreportinquiry/fxml/show_report_dialog.fxml";
	
	@FXML private TableView<ConvictedReport> tvConvictedReports;
	@FXML private TableColumn<ConvictedReport, ConvictedReport> tcSequence;
	@FXML private TableColumn<ConvictedReport, String> tvName;
	@FXML private TableColumn<ConvictedReport, String> tcSamisId;
	@FXML private TableColumn<ConvictedReport, String> tcSamisIdType;
	@FXML private TableColumn<ConvictedReport, String> tcNationality;
	@FXML private TableColumn<ConvictedReport, String> tcOperatorId;
	@FXML private TableColumn<ConvictedReport, String> tcRegistrationDate;
	@FXML private BorderPane paneConvictedReports;
	@FXML private ProgressIndicator piLookup;
	@FXML private ProgressIndicator piInquiry;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private Button btnRetryLookup;
	@FXML private Button btnInquiry;
	@FXML private Button btnShowReport;
	
	private LookupTask lookupTask;
	
	@SuppressWarnings({"unchecked", "deprecation"})
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtGeneralFileNumber, "\\d*", "[^\\d]",
		                                   10);
		
		btnInquiry.disableProperty().bind(txtGeneralFileNumber.textProperty().isEmpty()
				                                                                    .or(piInquiry.visibleProperty()));
		btnShowReport.disableProperty().bind(Bindings.size(tvConvictedReports.getSelectionModel().getSelectedItems())
			                                                                                    .isEqualTo(0));
		
		tvConvictedReports.setOnKeyReleased(keyEvent ->
		{
			if(keyEvent.getCode() == KeyCode.ENTER) btnShowReport.fire();
		});
		tvConvictedReports.setOnMouseClicked(mouseEvent ->
		{
			if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2)
			{
				btnShowReport.fire();
			}
		});
		
		tcSequence.setSortable(false);
		tcSequence.impl_setReorderable(false);
		tcSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper(p.getValue()));
		tcSequence.setCellFactory(new Callback<TableColumn<ConvictedReport, ConvictedReport>,
				TableCell<ConvictedReport, ConvictedReport>>()
		{
			@Override
			public TableCell<ConvictedReport, ConvictedReport> call(TableColumn<ConvictedReport, ConvictedReport> param)
			{
				return new TableCell<ConvictedReport, ConvictedReport>()
				{
					@Override
					protected void updateItem(ConvictedReport item, boolean empty)
					{
						super.updateItem(item, empty);
						
						TableRow tableRow = getTableRow();
						
						if(tableRow != null && item != null)
						{
							setText(AppUtils.localizeNumbers(String.valueOf(tableRow.getIndex() + 1)));
						}
						else setText("");
					}
				};
			}
		});
		
		tvName.impl_setReorderable(false);
		tvName.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    Name name = convictedReport.getSubjtName();
		    return new SimpleStringProperty(AppUtils.constructName(name));
		});
		
		tcSamisId.impl_setReorderable(false);
		tcSamisId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(convictedReport.getSubjSamisId())));
		});
		
		tcSamisIdType.impl_setReorderable(false);
		tcSamisIdType.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
			
			@SuppressWarnings("unchecked") List<SamisIdType> samisIdTypes = (List<SamisIdType>)
												Context.getUserSession().getAttribute("lookups.samisIdTypes");
			
			Integer samisIdTypeInteger = convictedReport.getSubjSamisType();
			if(samisIdTypeInteger != null)
			{
				SamisIdType theSamisIdType = null;
				
				for(SamisIdType type : samisIdTypes)
				{
					if(type.getCode() == samisIdTypeInteger)
					{
						theSamisIdType = type;
						break;
					}
				}
				
				if(theSamisIdType != null)
				{
					boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
					String samisIdType = AppUtils.localizeNumbers(arabic ? theSamisIdType.getDescriptionAR() :
							                                               theSamisIdType.getDescriptionEN());
					return new SimpleStringProperty(samisIdType);
				}
			}
		    
		    return null;
		});
		
		tcNationality.impl_setReorderable(false);
		tcNationality.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		
			@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
													Context.getUserSession().getAttribute("lookups.countries");
			
			CountryBean countryBean = null;
			
			for(CountryBean country : countries)
			{
				if(country.getCode() == convictedReport.getSubjNationalityCode())
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
		
		tcOperatorId.impl_setReorderable(false);
		tcOperatorId.setCellValueFactory(param ->
		{
			ConvictedReport convictedReport = param.getValue();
			return new SimpleStringProperty(AppUtils.localizeNumbers(convictedReport.getOperatorId()));
		});
		
		tcRegistrationDate.impl_setReorderable(false);
		tcRegistrationDate.setCellValueFactory(param ->
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		    ConvictedReport convictedReport = param.getValue();
			return new SimpleStringProperty(
							AppUtils.formatHijriDateSimple(convictedReport.getReportDate() * 1000, rtl));
		});
		
		initializeLookupTask();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Context.getExecutorService().submit(lookupTask);
			
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
			
			if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
			{
				deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			}
		}
		else
		{
			@SuppressWarnings("unchecked")
			ServiceResponse<List<ConvictedReport>> serviceResponse =
							(ServiceResponse<List<ConvictedReport>>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			disableUiControls(false);
			
			if(serviceResponse.isSuccess())
			{
				if(serviceResponse.isSuccess())
				{
					List<ConvictedReport> convictedReports = serviceResponse.getResult();
					tvConvictedReports.getItems().setAll(convictedReports);
					tvConvictedReports.requestFocus();
				}
				else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
					                        serviceResponse.getErrorDetails());
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
			                            serviceResponse.getErrorDetails());
			
			tvConvictedReports.requestFocus();
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnInquiry.fire();
	}
	
	@FXML
	private void onRetryLookupButtonClicked(ActionEvent actionEvent)
	{
		initializeLookupTask();
		Context.getExecutorService().submit(lookupTask);
	}
	
	private void initializeLookupTask()
	{
		lookupTask = new LookupTask();
		lookupTask.setOnSucceeded(event ->
		{
		    ServiceResponse<Void> serviceResponse = lookupTask.getValue();
		    
		    if(serviceResponse.isSuccess()) afterLookup(true);
		    else
		    {
		        afterLookup(false);
		        reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
		                               serviceResponse.getErrorDetails());
		    }
		});
		lookupTask.setOnFailed(event ->
		{
		    afterLookup(false);
		    String errorCode = ConvictedReportInquiryErrorCodes.C014_00001.getCode();
		    Exception exception = (Exception) lookupTask.getException();
		    String[] errorDetails = {"Failed to load lookup data!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
	}
	
	private void afterLookup(boolean success)
	{
		if(success)
		{
			GuiUtils.showNode(piLookup, false);
			GuiUtils.showNode(paneConvictedReports, true);
			txtGeneralFileNumber.requestFocus();
		}
		else
		{
			GuiUtils.showNode(piLookup, false);
			GuiUtils.showNode(btnRetryLookup, true);
		}
	}
	
	private void disableUiControls(boolean bool)
	{
		txtGeneralFileNumber.setDisable(bool);
		tvConvictedReports.setDisable(bool);
		
		GuiUtils.showNode(btnInquiry, !bool);
		GuiUtils.showNode(piInquiry, bool);
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		tvConvictedReports.getItems().clear();
		disableUiControls(true);
		
		String sGeneralFileNumber = txtGeneralFileNumber.getText();
		long generalFileNumber = Long.parseLong(sGeneralFileNumber);
		
		Map<String, Object> uiDataMap = new HashMap<>();
		uiDataMap.put(KEY_GENERAL_FILE_NUMBER, generalFileNumber);
		
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	@FXML
	private void onShowReportButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		try
		{
			
			ShowReportDialogFxController controller = DialogUtils.buildCustomDialogByFxml(
					Context.getCoreFxController().getStage(), FXML_SHOW_REPORT_DIALOG, resources, rtl, true);
			
			if(controller != null)
			{
				ConvictedReport selectedItem = tvConvictedReports.getSelectionModel().getSelectedItem();
				controller.setConvictedReport(selectedItem);
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = ConvictedReportInquiryErrorCodes.C014_00002.getCode();
			String[] errorDetails = {"Failed to load (" + FXML_SHOW_REPORT_DIALOG + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
		}
	}
}