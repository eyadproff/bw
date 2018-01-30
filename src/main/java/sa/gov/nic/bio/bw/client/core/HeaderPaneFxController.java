package sa.gov.nic.bio.bw.client.core;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.interfaces.AttachableController;
import sa.gov.nic.bio.bw.client.core.interfaces.VisibilityControl;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.util.ResourceBundle;
import java.util.logging.Logger;

public class HeaderPaneFxController implements VisibilityControl, AttachableController
{
	private static final Logger LOGGER = Logger.getLogger(HeaderPaneFxController.class.getName());
	
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
	
	@FXML
	private void initialize()
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
	
	public void setAvatarImage(Image image)
	{
		if(image != null) ivAvatar.setImage(image);
	}
	
	@FXML
	private void onLogoutButtonClicked(ActionEvent actionEvent)
	{
		coreFxController.getNotificationPane().hide();
		String message = coreFxController.getMessagesBundle().getString("logout.confirm");
		boolean confirmed = coreFxController.showConfirmationDialogAndWait(null, message);
		
		if(confirmed) logout();
	}
	
	public void logout()
	{
		// close all opened dialogs (except the primary one)
		ObservableList<Stage> stages = StageHelper.getStages();
		for(int i = 1; i <= stages.size(); i++)
		{
			Stage stage = stages.get(i - 1);
			if(stage != null && stage != coreFxController.getPrimaryStage())
			{
				LOGGER.fine("Closing stage #" + i + ": " + stage.getTitle());
				stage.close();
			}
		}
		
		Context.getWebserviceManager().cancelRefreshTokenScheduler();
		
		coreFxController.getNotificationPane().hide();
		coreFxController.stopIdleMonitor();
		//coreFxController.logout();
	}
}