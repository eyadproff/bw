package sa.gov.nic.bio.bw.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import sa.gov.nic.bio.bw.core.Context;
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
	
	@Override
	protected void initialize()
	{
		if(Context.getRuntimeEnvironment() == RuntimeEnvironment.LOCAL ||
		   Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
		{
			GuiUtils.showNode(chbMockTasks, true);
		}
	}
	
	CheckBox getMockTasksCheckBox()
	{
		return chbMockTasks;
	}
}