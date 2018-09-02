package sa.gov.nic.bio.bw.client.features.convictedreportinquiry;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdType;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks.LookupTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.List;
import java.util.Map;

public class ConvictedReportInquiryPaneFxController extends BodyFxControllerBase
{
	@FXML private BorderPane paneConvictedReports;
	@FXML private ProgressIndicator piLookup;
	@FXML private ProgressIndicator piInquiry;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private TableView tvConvictedReports;
	@FXML private Button btnRetryLookup;
	@FXML private Button btnInquiry;
	@FXML private Button btnShowReport;
	
	private LookupTask lookupTask;
	
	@Override
	protected void initialize()
	{
		GuiUtils.applyValidatorToTextField(txtGeneralFileNumber, null, null,
		                                   10);
		
		btnInquiry.disableProperty().bind(txtGeneralFileNumber.textProperty().isEmpty());
		btnShowReport.disableProperty().bind(Bindings.size(tvConvictedReports.getItems()).isEqualTo(0));
		
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
		    ServiceResponse<List<PersonIdType>> serviceResponse = lookupTask.getValue();
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
		btnInquiry.setDisable(bool);
		tvConvictedReports.setDisable(bool);
		
		GuiUtils.showNode(btnInquiry, !bool);
		GuiUtils.showNode(piInquiry, bool);
	}
	
	@FXML
	private void onShowReportButtonClicked(ActionEvent actionEvent)
	{
	
	}
}