package sa.gov.nic.bio.bw.client.features.cancelcriminal;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.tasks.LookupTask;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.utils.CancelCriminalErrorCodes;
import sa.gov.nic.bio.bw.client.features.cancelcriminal.webservice.PersonIdType;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancelCriminalPaneFxController extends BodyFxControllerBase
{
	@FXML private ProgressIndicator piLookupPersonIdTypes;
	@FXML private Button btnRetryLookupPersonIdTypes;
	@FXML private TabPane tabPane;
	@FXML private Tab tabByPersonId;
	@FXML private Tab tabByInquiryId;
	@FXML private TextField txtPersonId;
	@FXML private ComboBox<PersonIdType> cboPersonIdType;
	@FXML private TextField txtCriminalId;
	@FXML private TextField txtInquiryId;
	@FXML private TextField txtCriminalId2;
	@FXML private VBox bottomBox;
	@FXML private Button btnCancelCriminal;
	@FXML private ProgressIndicator piCancelCriminal;
	
	private LookupTask lookupTask;
	
	@FXML
	protected void initialize()
	{
		tabByPersonId.setGraphic(AppUtils.createFontAwesomeIcon('\uf2bb'));
		tabByInquiryId.setGraphic(AppUtils.createFontAwesomeIcon('\uf1c0'));
		
		tabByPersonId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue) Platform.runLater(txtPersonId::requestFocus);
		});
		
		tabByInquiryId.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue) Platform.runLater(txtInquiryId::requestFocus);
		});
		
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCriminalId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtInquiryId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtCriminalId2, "\\d*", "[^\\d]", 10);
		
		
		BooleanBinding personIdEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding inquiryIdEmptyBinding = txtInquiryId.textProperty().isEmpty();
		BooleanBinding criminalNumberEmptyBinding = txtCriminalId.textProperty().isEmpty();
		BooleanBinding criminal2NumberEmptyBinding = txtCriminalId2.textProperty().isEmpty();
		
		BooleanBinding byPersonIdTabReadyBinding = tabByPersonId.selectedProperty()
														        .and(personIdEmptyBinding.not())
																.and(criminalNumberEmptyBinding.not());
		
		BooleanBinding byInquiryIdTabReadyBinding = tabByInquiryId.selectedProperty()
																  .and(inquiryIdEmptyBinding.not())
																  .and(criminal2NumberEmptyBinding.not());
		
		btnCancelCriminal.disableProperty().bind(byPersonIdTabReadyBinding.not().and(byInquiryIdTabReadyBinding.not()));
		GuiUtils.makeButtonClickable(btnCancelCriminal);
		GuiUtils.makeButtonClickable(btnRetryLookupPersonIdTypes);
		
		cboPersonIdType.setConverter(new StringConverter<PersonIdType>()
		{
			@Override
			public String toString(PersonIdType object)
			{
				return formatPersonIdType(object);
			}
			
			@Override
			public PersonIdType fromString(String string)
			{
				return null;
			}
		});
		
		initializeLookupTask();
	}
	
	@Override
	public void onControllerReady()
	{
		txtPersonId.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> dataMap)
	{
		if(newForm) Context.getExecutorService().submit(lookupTask);
		else
		{
			ServiceResponse<?> serviceResponse = (ServiceResponse<?>) dataMap.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			disableUiControls(false);
			
			if(serviceResponse.isSuccess())
			{
				String personId = txtPersonId.getText().trim();
				PersonIdType personIdType = cboPersonIdType.getValue();
				String criminalId = txtCriminalId.getText().trim();
				String inquiryId = txtInquiryId.getText().trim();
				String criminalId2 = txtCriminalId2.getText().trim();
				
				Boolean resultBean = (Boolean) serviceResponse.getResult();
				if(resultBean != null && resultBean)
				{
					if(tabByPersonId.isSelected())
					{
						String message = String.format(
								stringsBundle.getString("cancelCriminal.byPersonId.success"), criminalId,
								personId, formatPersonIdType(personIdType));
						showSuccessNotification(message);
					}
					else
					{
						String message = String.format(
								stringsBundle.getString("cancelCriminal.byInquiryId.success"), criminalId2,
								inquiryId);
						showSuccessNotification(message);
					}
				}
				else
				{
					if(tabByPersonId.isSelected())
					{
						String message = String.format(
								stringsBundle.getString("cancelCriminal.byPersonId.failure"), criminalId,
								personId, formatPersonIdType(personIdType));
						showWarningNotification(message);
					}
					else
					{
						String message = String.format(
								stringsBundle.getString("cancelCriminal.byInquiryId.failure"), criminalId2,
								inquiryId);
						showWarningNotification(message);
					}
				}
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
			                            serviceResponse.getErrorDetails());
			
			txtPersonId.requestFocus();
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnCancelCriminal.fire();
	}
	
	@FXML
	private void onRetryLookupPersonIdTypesButtonClicked(ActionEvent actionEvent)
	{
		initializeLookupTask();
		Context.getExecutorService().submit(lookupTask);
	}
	
	@FXML
	private void onCancelCriminalButtonClicked(ActionEvent actionEvent)
	{
		String headerText = stringsBundle.getString("cancelCriminal.confirmation.header");
		Map<String, Object> uiDataMap = new HashMap<>();
		
		if(tabByPersonId.isSelected())
		{
			String personId = txtPersonId.getText().trim();
			PersonIdType personIdType = cboPersonIdType.getValue();
			String criminalId = txtCriminalId.getText().trim();
			
			String contentText = String.format(
										stringsBundle.getString("cancelCriminal.byPersonId.confirmation.message"),
			                            criminalId, personId, formatPersonIdType(personIdType));
			boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
			
			hideNotification();
			disableUiControls(true);
			
			if(tabByPersonId.isSelected())
			{
				uiDataMap.put("personId", personId);
				uiDataMap.put("personIdType", personIdType.getCode());
				uiDataMap.put("criminalId", criminalId);
			}
		}
		else
		{
			String inquiryId = txtInquiryId.getText().trim();
			String criminalId = txtCriminalId2.getText().trim();
			
			String contentText = String.format(
					stringsBundle.getString("cancelCriminal.byInquiryId.confirmation.message"),
					criminalId, inquiryId);
			boolean confirmed = coreFxController.showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
			
			hideNotification();
			disableUiControls(true);
			
			if(tabByPersonId.isSelected())
			{
				uiDataMap.put("inquiryId", inquiryId);
				uiDataMap.put("criminalId", criminalId);
			}
		}
		
		coreFxController.submitForm(uiDataMap);
	}
	
	private String formatPersonIdType(PersonIdType personIdType)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(personIdType.getCode()).append("- ");
		
		if(coreFxController.getCurrentLanguage() == GuiLanguage.ARABIC)
		{
			sb.append(personIdType.getDescriptionAR());
		}
		else sb.append(personIdType.getDescriptionEN());
		
		return AppUtils.replaceNumbersOnly(sb.toString(), coreFxController.getCurrentLanguage().getLocale());
	}
	
	private void initializeLookupTask()
	{
		lookupTask = new LookupTask();
		lookupTask.setOnSucceeded(event ->
		{
		    ServiceResponse<List<PersonIdType>> serviceResponse = lookupTask.getValue();
		    if(serviceResponse.isSuccess())
		    {
		        List<PersonIdType> personIdTypes = serviceResponse.getResult();
		        cboPersonIdType.getItems().addAll(personIdTypes);
		        cboPersonIdType.getSelectionModel().select(0);
		        afterLookup(true);
		    }
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
		    String errorCode = CancelCriminalErrorCodes.C006_00001.getCode();
		    Exception exception = (Exception) lookupTask.getException();
		    String[] errorDetails = {"Failed to load PersonIdTypes!"};
		    coreFxController.showErrorDialog(errorCode, exception, errorDetails);
		});
	}
	
	private void afterLookup(boolean success)
	{
		if(success)
		{
			GuiUtils.showNode(piLookupPersonIdTypes, false);
			GuiUtils.showNode(tabPane, true);
			GuiUtils.showNode(bottomBox, true);
			txtPersonId.requestFocus();
		}
		else
		{
			GuiUtils.showNode(piLookupPersonIdTypes, false);
			GuiUtils.showNode(btnRetryLookupPersonIdTypes, true);
		}
	}
	
	private void disableUiControls(boolean bool)
	{
		coreFxController.getMenuPaneController().showOverlayPane(bool);
		
		tabPane.setDisable(bool);
		txtPersonId.setDisable(bool);
		txtCriminalId.setDisable(bool);
		
		GuiUtils.showNode(piCancelCriminal, bool);
		GuiUtils.showNode(btnCancelCriminal, !bool);
	}
}