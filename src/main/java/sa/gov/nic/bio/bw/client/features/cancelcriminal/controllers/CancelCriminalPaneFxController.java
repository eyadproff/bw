package sa.gov.nic.bio.bw.client.features.cancelcriminal.controllers;

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
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@FxmlFile("cancelCriminal.fxml")
public class CancelCriminalPaneFxController extends BodyFxControllerBase
{
	@FXML private TabPane tabPane;
	@FXML private Tab tabByPersonId;
	@FXML private Tab tabByInquiryId;
	@FXML private TextField txtPersonId;
	@FXML private ComboBox<SamisIdType> cboPersonIdType;
	@FXML private TextField txtCriminalId;
	@FXML private TextField txtInquiryId;
	@FXML private TextField txtCriminalId2;
	@FXML private VBox bottomBox;
	@FXML private Button btnCancelCriminal;
	@FXML private ProgressIndicator piCancelCriminal;
	
	@Override
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
		
		
		
		cboPersonIdType.setConverter(new StringConverter<SamisIdType>()
		{
			@Override
			public String toString(SamisIdType object)
			{
				return formatPersonIdType(object);
			}
			
			@Override
			public SamisIdType fromString(String string)
			{
				return null;
			}
		});
		
		@SuppressWarnings("unchecked")
		List<SamisIdType> samisIdTypes = (List<SamisIdType>)
													Context.getUserSession().getAttribute(SamisIdTypesLookup.KEY);
		cboPersonIdType.getItems().addAll(samisIdTypes);
		cboPersonIdType.getSelectionModel().select(0);
	}
	
	@Override
	protected void onAttachedToScene()
	{
		txtPersonId.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(!newForm)
		{
			TaskResponse<?> taskResponse = (TaskResponse<?>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			
			disableUiControls(false);
			
			if(taskResponse.isSuccess())
			{
				String personId = txtPersonId.getText().trim();
				SamisIdType samisIdType = cboPersonIdType.getValue();
				String criminalId = txtCriminalId.getText().trim();
				String inquiryId = txtInquiryId.getText().trim();
				String criminalId2 = txtCriminalId2.getText().trim();
				
				Boolean resultBean = (Boolean) taskResponse.getResult();
				if(resultBean != null && resultBean)
				{
					if(tabByPersonId.isSelected())
					{
						String message = String.format(
								resources.getString("cancelCriminal.byPersonId.success"), criminalId,
								personId, formatPersonIdType(samisIdType));
						showSuccessNotification(message);
					}
					else
					{
						String message = String.format(
								resources.getString("cancelCriminal.byInquiryId.success"), criminalId2,
								inquiryId);
						showSuccessNotification(message);
					}
				}
				else
				{
					if(tabByPersonId.isSelected())
					{
						String message = String.format(
								resources.getString("cancelCriminal.byPersonId.failure"), criminalId,
								personId, formatPersonIdType(samisIdType));
						showWarningNotification(message);
					}
					else
					{
						String message = String.format(
								resources.getString("cancelCriminal.byInquiryId.failure"), criminalId2,
								inquiryId);
						showWarningNotification(message);
					}
				}
			}
			else reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
			                                taskResponse.getErrorDetails());
			
			txtPersonId.requestFocus();
		}
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnCancelCriminal.fire();
	}
	
	@FXML
	private void onCancelCriminalButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("cancelCriminal.confirmation.header");
		Map<String, Object> uiDataMap = new HashMap<>();
		
		if(tabByPersonId.isSelected())
		{
			String personId = txtPersonId.getText().trim();
			SamisIdType samisIdType = cboPersonIdType.getValue();
			String criminalId = txtCriminalId.getText().trim();
			
			String contentText = String.format(
										resources.getString("cancelCriminal.byPersonId.confirmation.message"),
			                            criminalId, personId, formatPersonIdType(samisIdType));
			boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
			
			hideNotification();
			disableUiControls(true);
			
			uiDataMap.put("personId", Long.parseLong(personId));
			uiDataMap.put("samisIdType", samisIdType.getCode());
			uiDataMap.put("criminalId", Long.parseLong(criminalId));
		}
		else
		{
			String inquiryId = txtInquiryId.getText().trim();
			String criminalId = txtCriminalId2.getText().trim();
			
			String contentText = String.format(
					resources.getString("cancelCriminal.byInquiryId.confirmation.message"),
					criminalId, inquiryId);
			boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
			
			if(!confirmed) return;
			
			hideNotification();
			disableUiControls(true);
			
			uiDataMap.put("inquiryId", Long.parseLong(inquiryId));
			uiDataMap.put("criminalId", Long.parseLong(criminalId));
		}
		
		if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
	}
	
	private String formatPersonIdType(SamisIdType samisIdType)
	{
		StringBuilder sb = new StringBuilder();
		
		if(Context.getGuiLanguage() == GuiLanguage.ARABIC)
		{
			sb.append(samisIdType.getDescriptionAR());
		}
		else sb.append(samisIdType.getDescriptionEN());
		
		return AppUtils.localizeNumbers(sb.toString(), Locale.getDefault(), false);
	}
	
	private void disableUiControls(boolean bool)
	{
		tabPane.setDisable(bool);
		txtPersonId.setDisable(bool);
		txtCriminalId.setDisable(bool);
		
		GuiUtils.showNode(piCancelCriminal, bool);
		GuiUtils.showNode(btnCancelCriminal, !bool);
	}
}