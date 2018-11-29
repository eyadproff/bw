package sa.gov.nic.bio.bw.core.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;

/**
 * JavaFX controller for the header. Shown only after login. It contains information about the logged in
 * user and also a button to logout from the current session.
 *
 * @author Fouad Almalki
 */
public class HeaderPaneFxController extends RegionFxControllerBase
{
	@FXML private ImageView ivAvatar;
	@FXML private Label lblUsername;
	@FXML private Label txtUsername;
	@FXML private Label lblOperatorName;
	@FXML private Label txtOperatorName;
	@FXML private Label lblLocation;
	@FXML private Label txtLocation;
	@FXML private CheckBox chbMockTasks;
	@FXML private Button btnMyTransactions;
	@FXML private Button btnLogout;

	@Override
	protected void initialize()
	{
		Glyph atIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.AT);
		Glyph userIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.USER);
		Glyph locationIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.LOCATION_ARROW);
		Glyph myTransactionsIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.BELL_O);
		Glyph logoutIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.POWER_OFF);
		
		lblUsername.setGraphic(atIcon);
		lblOperatorName.setGraphic(userIcon);
		lblLocation.setGraphic(locationIcon);
		btnMyTransactions.setGraphic(myTransactionsIcon);
		btnLogout.setGraphic(logoutIcon);
		
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
		   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			chbMockTasks.setVisible(true);
		}
	}
	
	CheckBox getMockTasksCheckBox()
	{
		return chbMockTasks;
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
		if(image != null)
		{
			ivAvatar.setImage(image);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivAvatar,
			                           resources.getString("label.operatorPhoto"),
			                           resources.getString("label.contextMenu.showImage"), false);
		}
		else GuiUtils.detachFacePhotoBase64(ivAvatar);
	}
	
	@FXML
	private void onMyTransactionsButtonClicked(ActionEvent actionEvent)
	{
	
	}
	
	@FXML
	private void onLogoutButtonClicked(ActionEvent actionEvent)
	{
		String message = Context.getCoreFxController().getResourceBundle().getString("logout.confirm");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(null, message);
		
		if(confirmed)
		{
			Context.getCoreFxController().prepareToLogout();
			Context.getCoreFxController().logout();
		}
	}
}