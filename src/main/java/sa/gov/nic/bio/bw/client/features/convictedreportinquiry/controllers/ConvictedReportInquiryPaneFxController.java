package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.controllers;

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
import javafx.util.Pair;
import sa.gov.nic.bio.bw.client.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.Map;

public class ConvictedReportInquiryPaneFxController extends BodyFxControllerBase
{
	private static final String FXML_SHOW_REPORT_DIALOG =
								"sa/gov/nic/bio/bw/client/features/convictedreportinquiry/fxml/show_report_dialog.fxml";
	
	@Input private List<Pair<ConvictedReport, Map<Integer, String>>> convictedReports;
	@Output private Long generalFileNumber;
	
	@FXML private TableView<Pair<ConvictedReport, Map<Integer, String>>> tvConvictedReports;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, Pair<ConvictedReport, Map<Integer, String>>>
																											tcSequence;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tvName;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcSamisId;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcSamisIdType;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcNationality;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcOperatorId;
	@FXML private TableColumn<Pair<ConvictedReport, Map<Integer, String>>, String> tcRegistrationDate;
	@FXML private BorderPane paneConvictedReports;
	@FXML private ProgressIndicator piLookup;
	@FXML private ProgressIndicator piInquiry;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private Button btnInquiry;
	@FXML private Button btnShowReport;
	
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
		tcSequence.setCellFactory(new Callback<TableColumn<Pair<ConvictedReport, Map<Integer, String>>,
				Pair<ConvictedReport, Map<Integer, String>>>,
				TableCell<Pair<ConvictedReport, Map<Integer, String>>, Pair<ConvictedReport, Map<Integer, String>>>>()
		{
			@Override
			public TableCell<Pair<ConvictedReport, Map<Integer, String>>, Pair<ConvictedReport, Map<Integer, String>>>
									call(TableColumn<Pair<ConvictedReport, Map<Integer, String>>, Pair<ConvictedReport,
																		   Map<Integer, String>>> param)
			{
				return new TableCell<Pair<ConvictedReport, Map<Integer, String>>,
									 Pair<ConvictedReport, Map<Integer, String>>>()
				{
					@Override
					protected void updateItem(Pair<ConvictedReport, Map<Integer, String>> item, boolean empty)
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
		    ConvictedReport convictedReport = param.getValue().getKey();
		    Name name = convictedReport.getSubjtName();
		    return new SimpleStringProperty(AppUtils.constructName(name));
		});
		
		tcSamisId.impl_setReorderable(false);
		tcSamisId.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue().getKey();
			
		    Long subjSamisId = convictedReport.getSubjSamisId();
			if(subjSamisId == null) return null;
			
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(subjSamisId)));
		});
		
		tcSamisIdType.impl_setReorderable(false);
		tcSamisIdType.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue().getKey();
			
			@SuppressWarnings("unchecked")
			List<SamisIdType> samisIdTypes = (List<SamisIdType>)
														Context.getUserSession().getAttribute(SamisIdTypesLookup.KEY);
			
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
		    ConvictedReport convictedReport = param.getValue().getKey();
		
			@SuppressWarnings("unchecked")
			List<CountryBean> countries = (List<CountryBean>)
														Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Integer nationalityCode = convictedReport.getSubjNationalityCode();
			
			if(nationalityCode == 0)
			{
				return new SimpleStringProperty(resources.getString("combobox.unknownNationality"));
			}
			
			CountryBean countryBean = null;
			
			for(CountryBean country : countries)
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
		
		tcOperatorId.impl_setReorderable(false);
		tcOperatorId.setCellValueFactory(param ->
		{
			ConvictedReport convictedReport = param.getValue().getKey();
			return new SimpleStringProperty(AppUtils.localizeNumbers(convictedReport.getOperatorId()));
		});
		
		tcRegistrationDate.impl_setReorderable(false);
		tcRegistrationDate.setCellValueFactory(param ->
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		    ConvictedReport convictedReport = param.getValue().getKey();
			return new SimpleStringProperty(
							AppUtils.formatHijriDateSimple(convictedReport.getReportDate() * 1000, rtl));
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
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
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			disableUiControls(false);
			
			if(serviceResponse.isSuccess())
			{
				tvConvictedReports.getItems().setAll(convictedReports);
				tvConvictedReports.requestFocus();
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
		generalFileNumber = Long.parseLong(sGeneralFileNumber);
		
		continueWorkflow();
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
				Pair<ConvictedReport, Map<Integer, String>> selectedItem =
															tvConvictedReports.getSelectionModel().getSelectedItem();
				controller.setConvictedReportWithFingerprintImages(selectedItem.getKey(), selectedItem.getValue());
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