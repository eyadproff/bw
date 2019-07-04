package sa.gov.nic.bio.bw.workflow.cancellatent.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;

@FxmlFile("cancelLatent.fxml")
public class CancelLatentPaneFxController extends ContentFxControllerBase
{
	@Input private Boolean success;
	@Output private Long personId;
	@Output private String latentId;
	
	@FXML private TextField txtPersonId;
	@FXML private TextField txtLatentId;
	@FXML private Button btnCancelLatent;
	@FXML private ProgressIndicator piCancelLatent;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtLatentId, "\\d*", "[^\\d]", 20);
		
		BooleanBinding idNumberEmptyBinding = txtPersonId.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtLatentId.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelLatent.visibleProperty();
		
		btnCancelLatent.disableProperty().bind(idNumberEmptyBinding.or(latentNumberEmptyBinding)
				                                                   .or(progressVisibility));
		
		txtPersonId.requestFocus();
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		txtPersonId.requestFocus();
		
		String personId = txtPersonId.getText();
		String latentId = txtLatentId.getText();
		
		if(success != null && success)
		{
			String message = String.format(resources.getString("cancelLatent.success"), latentId, personId);
			showSuccessNotification(message);
		}
		else
		{
			String message = String.format(resources.getString("cancelLatent.failure"), latentId, personId);
			showWarningNotification(message);
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		txtPersonId.setDisable(bShow);
		txtLatentId.setDisable(bShow);
		
		GuiUtils.showNode(piCancelLatent, bShow);
		GuiUtils.showNode(btnCancelLatent, !bShow);
	}
	
	@FXML
	private void onCancelLatentButtonClicked(ActionEvent actionEvent)
	{
		String personId = txtPersonId.getText().trim();
		String latentId = txtLatentId.getText().trim();
		
		String headerText = resources.getString("cancelLatent.confirmation.header");
		String contentText = String.format(resources.getString("cancelLatent.confirmation.message"),
		                                   latentId, personId);
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(!confirmed) return;
		
		this.personId = Long.parseLong(personId);
		this.latentId = latentId;
		
		continueWorkflow();
	}
}