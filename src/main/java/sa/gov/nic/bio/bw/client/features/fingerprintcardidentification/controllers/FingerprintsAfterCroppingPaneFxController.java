package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Input;

import java.net.URL;
import java.util.Map;

public class FingerprintsAfterCroppingPaneFxController extends WizardStepFxControllerBase
{
	@Input(required = true) private Map<Integer, Image> fingerprintImages;
	
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivLeftThumb;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftLittle;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/fingerprintsAfterCropping.fxml");
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Map<Integer, String> dialogTitleMap = GuiUtils.constructFingerprintDialogTitles(resources);
			Map<Integer, ImageView> imageViewMap = GuiUtils.constructFingerprintImageViewMap(ivRightThumb, ivRightIndex,
			                                                                                 ivRightMiddle, ivRightRing,
			                                                                                 ivRightLittle, ivLeftThumb,
			                                                                                 ivLeftIndex, ivLeftMiddle,
			                                                                                 ivLeftRing, ivLeftLittle);
			
			boolean disableNext = true;
			
			for(int i = 0; i <= 10; i++)
			{
				Image image = fingerprintImages.get(i + 1);
				
				if(image != null)
				{
					disableNext = false;
					
					ImageView imageView = imageViewMap.get(i + 1);
					String title = dialogTitleMap.get(i + 1);
					
					if(imageView != null)
					{
						imageView.setImage(image);
						GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
						                           title, resources.getString("label.contextMenu.showImage"),
						                           false);
					}
				}
			}
			
			btnNext.setDisable(disableNext);
		}
	}
	
	@FXML
	private void onPreviousButtonClicked(ActionEvent actionEvent)
	{
		goPrevious();
	}
	
	@FXML
	private void onNextButtonClicked(ActionEvent actionEvent)
	{
		goNext();
	}
}