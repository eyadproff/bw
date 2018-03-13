package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

/**
 * JavaFX controller for the footer. The footer is used only at login screen.
 *
 * @author Fouad Almalki
 */
public class FooterPaneFxController extends RegionFxControllerBase
{
	@FXML private ImageView ivLogoRTL;
	@FXML private ImageView ivLogoLTR;
	
	public void showLogo()
	{
		boolean rtl = Context.getCoreFxController().getCurrentLanguage().getNodeOrientation()
																					== NodeOrientation.RIGHT_TO_LEFT;
		
		GuiUtils.showNode(ivLogoRTL, rtl);
		GuiUtils.showNode(ivLogoLTR, !rtl);
	}
}