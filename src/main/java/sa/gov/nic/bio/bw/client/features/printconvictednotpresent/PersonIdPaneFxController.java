package sa.gov.nic.bio.bw.client.features.printconvictednotpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdType;
import sa.gov.nic.bio.bw.client.features.printconvictednotpresent.utils.PrintConvictedNotPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonIdPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_PERSON_INFO_INQUIRY_PERSON_ID = "PERSON_INFO_INQUIRY_PERSON_ID";
	public static final String KEY_PERSON_INFO_INQUIRY_PERSON_TYPE = "PERSON_INFO_INQUIRY_PERSON_TYPE";
	
	@FXML private ProgressIndicator piProgress;
	@FXML private TextField txtPersonId;
	@FXML private ComboBox<PersonIdType> cboPersonIdType;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/personId.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		
		btnNext.disableProperty().bind(txtPersonId.textProperty().isEmpty().or(txtPersonId.disabledProperty()));
		btnNext.setOnAction(actionEvent ->
		{
			hideNotification();
			piProgress.setVisible(true);
			txtPersonId.setDisable(true);
			cboPersonIdType.setDisable(true);
			
			Map<String, Object> uiDataMap = new HashMap<>();
			uiDataMap.put(KEY_PERSON_INFO_INQUIRY_PERSON_ID, Long.parseLong(txtPersonId.getText()));
			uiDataMap.put(KEY_PERSON_INFO_INQUIRY_PERSON_TYPE, cboPersonIdType.getValue().getCode());
			Context.getWorkflowManager().submitUserTask(uiDataMap);
		});
		
		@SuppressWarnings("unchecked") List<PersonIdType> personIdTypes = (List<PersonIdType>)
												Context.getUserSession().getAttribute("lookups.personIdTypes");
		cboPersonIdType.getItems().setAll(personIdTypes);
		cboPersonIdType.setConverter(new StringConverter<PersonIdType>()
		{
			@Override
			public String toString(PersonIdType object)
			{
				StringBuilder sb = new StringBuilder();
				
				if(Context.getGuiLanguage() == GuiLanguage.ARABIC) sb.append(object.getDescriptionAR());
				else sb.append(object.getDescriptionEN());
				
				return AppUtils.replaceNumbersOnly(sb.toString(), Context.getGuiLanguage().getLocale());
			}
			
			@Override
			public PersonIdType fromString(String string)
			{
				return null;
			}
		});
		cboPersonIdType.getSelectionModel().selectFirst();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm) txtPersonId.requestFocus();
		else
		{
			piProgress.setVisible(false);
			txtPersonId.setDisable(false);
			cboPersonIdType.setDisable(false);
			
			@SuppressWarnings("unchecked")
			ServiceResponse<PersonInfo> serviceResponse = (ServiceResponse<PersonInfo>)
																	uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			
			if(serviceResponse.isSuccess())
			{
				PersonInfo result = serviceResponse.getResult();
				
				if(result != null)
				{
					uiInputData.put(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT, result);
					goNext();
				}
				else
				{
					String errorCode = PrintConvictedNotPresentErrorCodes.C009_00001.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
			                            serviceResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent actionEvent)
	{
		btnNext.fire();
	}
}