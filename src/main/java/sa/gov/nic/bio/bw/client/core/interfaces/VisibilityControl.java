package sa.gov.nic.bio.bw.client.core.interfaces;

import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

/**
 * Created by Fouad on 16-Jul-17.
 */
public interface VisibilityControl
{
	default void showRootPane()
	{
		setRootPaneVisibility(true);
	}
	
	default void hideRootPane()
	{
		setRootPaneVisibility(false);
	}
	
	default void setRootPaneVisibility(boolean bVisible)
	{
		GuiUtils.showNode(getRootPane(), bVisible);
	}
	
	Pane getRootPane();
}