package sa.gov.nic.bio.bw.client.core;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.util.ResourceBundle;

/**
 * JavaFX controller for the header. Shown only after login. It contains information about the logged in
 * user and also a button to logout from the current session.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class HeaderPaneFxController extends RegionFxControllerBase
{
	@FXML private ResourceBundle resources;
	@FXML private Pane rootPane;
	@FXML private ImageView ivAvatar;
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
	protected void initialize()
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
	public Pane getRegionRootPane()
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
	
	public void setAvatarImage(Image image)
	{
		if(image != null) ivAvatar.setImage(image);
	}
	
	@FXML
	private void onLogoutButtonClicked(ActionEvent actionEvent)
	{
		coreFxController.getNotificationPane().hide();
		String message = coreFxController.getStringsBundle().getString("logout.confirm");
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(null, message);
		
		if(confirmed) coreFxController.logout();
	}
}