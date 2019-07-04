package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;

import java.util.Map;

@FxmlFile("imageSource.fxml")
public class ImageSourceFxController extends WizardStepFxControllerBase
{
	public enum Source
	{
		UPLOAD,
		CAMERA
	}
	
	@Input private Boolean hidePreviousButton;
	@Output private Source imageSource;
	
	@FXML private RadioButton rbByUploadingImage;
	@FXML private RadioButton rbByCamera;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		// go next on pressing ENTER on the radio buttons
		EventHandler<KeyEvent> eventHandler = event ->
		{
			if(event.getCode() == KeyCode.ENTER)
			{
				btnNext.fire();
				event.consume();
			}
		};
		rbByUploadingImage.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		rbByCamera.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
		
		String uploadImageTitle = resources.getString("wizard.uploadImage");
		String capturePhotoByCameraTitle = resources.getString("wizard.capturePhotoByCamera");
		
		// change the wizard-step-indicator upon changing the image source
		int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(uploadImageTitle);
		if(stepIndex < 0) stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
				.getStepIndexByTitle(capturePhotoByCameraTitle);
		
		final int finalStepIndex = stepIndex;
		
		rbByUploadingImage.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue) Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, uploadImageTitle,
		                                                                          "upload");
		    else Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, capturePhotoByCameraTitle,
		                                                                  "camera");
		});
		
		if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
		
		// load the old state, if exists
		if(Source.CAMERA.equals(imageSource))
		{
			rbByCamera.setSelected(true);
			rbByCamera.requestFocus();
		}
		else
		{
			rbByUploadingImage.setSelected(true);
			rbByUploadingImage.requestFocus();
		}
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(rbByCamera.isSelected()) imageSource = Source.CAMERA;
		else imageSource = Source.UPLOAD;
	}
}