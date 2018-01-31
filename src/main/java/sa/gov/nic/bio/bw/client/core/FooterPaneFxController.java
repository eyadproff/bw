package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

import java.util.ResourceBundle;

/**
 * JavaFX controller for the footer. The footer is used only at login screen.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class FooterPaneFxController extends RegionFxControllerBase
{
	@FXML private ResourceBundle resources;
	@FXML private ImageView ivLogoRTL;
	@FXML private ImageView ivLogoLTR;
	
	private CoreFxController coreFxController;
	
	@Override
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
		onPostAttachingCoreFxController();
	}
	
	private void onPostAttachingCoreFxController()
	{
		boolean rtl = coreFxController.getCurrentLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		
		GuiUtils.showNode(ivLogoRTL, rtl);
		GuiUtils.showNode(ivLogoLTR, !rtl);
	}
}