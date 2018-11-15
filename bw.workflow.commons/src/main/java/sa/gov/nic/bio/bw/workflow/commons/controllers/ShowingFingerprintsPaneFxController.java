package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;

import java.util.Map;

@FxmlFile("showingFingerprints.fxml")
public class ShowingFingerprintsPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	
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
	@FXML private Button btnInquiry;
	
	@Override
	protected void onAttachedToScene()
	{
		Map<Integer, String> dialogTitleMap = GuiUtils.constructFingerprintDialogTitles(resources);
		Map<Integer, ImageView> imageViewMap = GuiUtils.constructFingerprintImageViewMap(ivRightThumb, ivRightIndex,
		                                                                                 ivRightMiddle, ivRightRing,
		                                                                                 ivRightLittle, ivLeftThumb,
		                                                                                 ivLeftIndex, ivLeftMiddle,
		                                                                                 ivLeftRing, ivLeftLittle);
		boolean disableInquiry = true;
		
		for(int i = 0; i <= 10; i++)
		{
			String base64Image = fingerprintBase64Images.get(i + 1);
			
			if(base64Image != null)
			{
				disableInquiry = false;
				
				ImageView imageView = imageViewMap.get(i + 1);
				String title = dialogTitleMap.get(i + 1);
				
				if(imageView != null)
				{
					imageView.setImage(AppUtils.imageFromBase64(base64Image));
					GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
					                           title, resources.getString("label.contextMenu.showImage"),
					                           false);
				}
			}
		}
		
		btnInquiry.setDisable(disableInquiry);
		if(!disableInquiry) btnInquiry.requestFocus();
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		goNext();
	}
}