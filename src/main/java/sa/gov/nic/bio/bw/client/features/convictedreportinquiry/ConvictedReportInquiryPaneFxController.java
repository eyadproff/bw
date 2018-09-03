package sa.gov.nic.bio.bw.client.features.convictedreportinquiry;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.IdType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks.InquiryTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks.LookupTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConvictedReportInquiryPaneFxController extends BodyFxControllerBase
{
	public TableColumn<ConvictedReport, ConvictedReport> tcSequence;
	public TableColumn<ConvictedReport, String> tvName;
	public TableColumn<ConvictedReport, String> tcIdNumber;
	public TableColumn<ConvictedReport, String> tcIdType;
	public TableColumn<ConvictedReport, String> tcNationality;
	public TableColumn<ConvictedReport, String> tcOperatorId;
	public TableColumn<ConvictedReport, String> tcRegistrationDate;
	@FXML private BorderPane paneConvictedReports;
	@FXML private ProgressIndicator piLookup;
	@FXML private ProgressIndicator piInquiry;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private TableView<ConvictedReport> tvConvictedReports;
	@FXML private Button btnRetryLookup;
	@FXML private Button btnInquiry;
	@FXML private Button btnShowReport;
	
	private LookupTask lookupTask;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtGeneralFileNumber, null, null,
		                                   10);
		
		btnInquiry.disableProperty().bind(txtGeneralFileNumber.textProperty().isEmpty()
				                                                                    .or(piInquiry.visibleProperty()));
		btnShowReport.disableProperty().bind(Bindings.size(tvConvictedReports.getItems()).isEqualTo(0));
		
		tcSequence.setSortable(false);
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
							setText(AppUtils.replaceNumbersOnly(String.valueOf(tableRow.getIndex() + 1),
							                                    Locale.getDefault()));
						}
						else setText("");
					}
				};
			}
		});
		
		tvName.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
			
		    Name name = convictedReport.getSubjtName();
			if(name == null) return null;
			
			String firstName = name.getFirstName();
			String fatherName = name.getFatherName();
			String grandfatherName = name.getGrandfatherName();
			String familyName = name.getFamilyName();
			
			if(firstName == null || firstName.trim().equals("-")) firstName = "";
			if(fatherName == null || fatherName.trim().equals("-")) fatherName = "";
			if(grandfatherName == null || grandfatherName.trim().equals("-")) grandfatherName = "";
			if(familyName == null || familyName.trim().equals("-")) familyName = "";
			
			String fullName = firstName + " " + fatherName + " " + grandfatherName + " " + familyName;
			fullName = fullName.trim().replaceAll("\\s+", " "); // remove extra spaces
		    
		    return new SimpleStringProperty(fullName);
		});
		
		tcIdNumber.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
		    return new SimpleStringProperty(convictedReport.getSubjDocId());
		});
		
		tcIdType.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
			
			@SuppressWarnings("unchecked") List<IdType> idTypes = (List<IdType>)
													Context.getUserSession().getAttribute("lookups.idTypes");
			
			Integer idTypeInteger = convictedReport.getSubjDocType();
			if(idTypeInteger != null)
			{
				IdType theIdType = null;
				
				for(IdType type : idTypes)
				{
					if(type.getCode() == idTypeInteger)
					{
						theIdType = type;
						break;
					}
				}
				
				if(theIdType != null)
				{
					String idType = AppUtils.replaceNumbersOnly(theIdType.getDesc(), Locale.getDefault());
					return new SimpleStringProperty(idType);
				}
			}
		    
		    return null;
		});
		
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
		
		tcOperatorId.setCellValueFactory(param ->
		{
			ConvictedReport convictedReport = param.getValue();
			return new SimpleStringProperty(convictedReport.getOperatorId());
		});
		
		tcRegistrationDate.setCellValueFactory(param ->
		{
		    ConvictedReport convictedReport = param.getValue();
			return new SimpleStringProperty(
								AppUtils.formatHijriGregorianDate(convictedReport.getReportDate() * 1000));
		});
		
		initializeLookupTask();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm) Context.getExecutorService().submit(lookupTask);
		else
		{
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			disableUiControls(false);
			
			if(serviceResponse.isSuccess())
			{
				// TODO
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
		disableUiControls(true);
		
		String sGeneralFileNumber = txtGeneralFileNumber.getText();
		long generalFileNumber = Long.parseLong(sGeneralFileNumber);
		InquiryTask inquiryTask = new InquiryTask(generalFileNumber);
		
		inquiryTask.setOnSucceeded(event ->
		{
			disableUiControls(false);
			
			ServiceResponse<List<ConvictedReport>> serviceResponse = inquiryTask.getValue();
			
			if(serviceResponse.isSuccess())
			{
				List<ConvictedReport> convictedReports = serviceResponse.getResult();
				tvConvictedReports.getItems().setAll(convictedReports);
			}
			else
			{
				reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                       serviceResponse.getErrorDetails());
			}
		});
		
		inquiryTask.setOnFailed(event ->
		{
			disableUiControls(false);
			
			Throwable exception = inquiryTask.getException();
			String errorCode = ConvictedReportInquiryErrorCodes.C014_00002.getCode();
			String[] errorDetails = {"failed while inquire for the convicted report (" + sGeneralFileNumber + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		
		Context.getExecutorService().submit(inquiryTask);
	}
	
	@FXML
	private void onShowReportButtonClicked(ActionEvent actionEvent)
	{
	
	}
}