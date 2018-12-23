package sa.gov.nic.bio.bw.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.utils.RuntimeEnvironment;

/**
 * JavaFX controller for the footer. The footer is used only at login screen.
 *
 * @author Fouad Almalki
 */
public class FooterPaneFxController extends RegionFxControllerBase
{
	@FXML private CheckBox chbMockTasks;
	@FXML private Label lblChangeServerShortcut;
	@FXML private Label lblScenicViewShortcut;
	
	@Override
	protected void initialize()
	{
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
		   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			lblChangeServerShortcut.setText(lblChangeServerShortcut.getText() + " " +
			                                AppConstants.CHANGING_SERVER_KEY_COMBINATION.getDisplayText());
			GuiUtils.showNode(lblChangeServerShortcut, true);
			chbMockTasks.setVisible(true);
		}
		
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL)
		{
			
			lblScenicViewShortcut.setText(lblScenicViewShortcut.getText() + " " +
			                              AppConstants.SCENIC_VIEW_KEY_COMBINATION.getDisplayText());
			GuiUtils.showNode(lblScenicViewShortcut, true);
		}
	}
	
	CheckBox getMockTasksCheckBox()
	{
		return chbMockTasks;
	}
}