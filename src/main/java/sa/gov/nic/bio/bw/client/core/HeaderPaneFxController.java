package sa.gov.nic.bio.bw.client.core;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.interfaces.AttachableController;
import sa.gov.nic.bio.bw.client.core.interfaces.VisibilityControl;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class HeaderPaneFxController implements VisibilityControl, AttachableController
{
	@FXML private ResourceBundle resources;
	@FXML private Pane rootPane;
	@FXML private Label lblUsername;
	@FXML private Label txtUsername;
	@FXML private Label lblOperatorName;
	@FXML private Label txtOperatorName;
	@FXML private Label lblLocation;
	@FXML private Label txtLocation;
	@FXML private Button btnLogout;
	
	private CoreFxController coreFxController;
	
	@Override
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
	
	@Override
	public void attachInitialResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon)
	{
		// Not Used!
	}
	
	@FXML
	private void initialize() throws IOException
	{
		Glyph atIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.AT);
		Glyph userIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.USER);
		Glyph locationIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.LOCATION_ARROW);
		Glyph logoutIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.POWER_OFF);
		
		lblUsername.setGraphic(atIcon);
		lblOperatorName.setGraphic(userIcon);
		lblLocation.setGraphic(locationIcon);
		btnLogout.setGraphic(logoutIcon);
	}
	
	@Override
	public Pane getRootPane()
	{
		return rootPane;
	}
	
	public void setUsername(String username)
	{
		txtUsername.setText(username);
	}
	
	public void setOperatorName(String operatorName)
	{
		txtOperatorName.setText(operatorName);
	}
	
	public void setLocation(String location)
	{
		txtLocation.setText(location);
	}
	
	public void onLogoutButtonClicked(ActionEvent actionEvent)
	{
		String message = coreFxController.getMessagesBundle().getString("logout.confirm");
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(null, message);
		
		if(confirmed) logout();
	}
	
	public void logout()
	{
		coreFxController.cancelRefreshTokenScheduler();
		coreFxController.stopIdleMonitor();
		
		Map<String, String> uiDataMap = new HashMap<>();
		uiDataMap.put("menuId", "logout");
		
		coreFxController.submitFormTask(uiDataMap);
	}
}