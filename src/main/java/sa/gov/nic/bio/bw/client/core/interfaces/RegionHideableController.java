package sa.gov.nic.bio.bw.client.core.interfaces;

import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;

/**
 * A controller that is capable of hiding its region in the primary stage.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public interface RegionHideableController
{
	/**
	 * Show the region associated with this controller on the primary stage.
	 */
	default void showRegion()
	{
		setRegionVisibility(true);
	}
	
	/**
	 * Hide the region associated with this controller from the primary stage.
	 */
	default void hideRegion()
	{
		setRegionVisibility(false);
	}
	
	/**
	 * Set the visibility of the region associated with this controller on the primary stage.
	 *
	 * @param bVisible <code>true</code> to show the region, <code>false</code> to hide it
	 */
	default void setRegionVisibility(boolean bVisible)
	{
		GuiUtils.showNode(getRegionRootPane(), bVisible);
	}
	
	/**
	 * @return the root pane that represents the region associated with this controller on the primary stage.
	 */
	Pane getRegionRootPane();
}