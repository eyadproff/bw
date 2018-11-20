package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
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
		GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb, ivRightIndex, ivRightMiddle,
		                                 ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle,
		                                 ivLeftRing, ivLeftLittle);
		
		boolean disableInquiry = fingerprintBase64Images.isEmpty();
		btnInquiry.setDisable(disableInquiry);
		if(!disableInquiry) btnInquiry.requestFocus();
	}
	
	@FXML
	private void onInquiryButtonClicked(ActionEvent actionEvent)
	{
		goNext();
	}
}