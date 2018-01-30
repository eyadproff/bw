package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.interfaces.AttachableController;
import sa.gov.nic.bio.bw.client.core.interfaces.VisibilityControl;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class FooterPaneFxController implements VisibilityControl, AttachableController
{
	@FXML private ResourceBundle resources;
	@FXML private Pane rootPane;
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
	
	@Override
	public Pane getRootPane()
	{
		return rootPane;
	}
}