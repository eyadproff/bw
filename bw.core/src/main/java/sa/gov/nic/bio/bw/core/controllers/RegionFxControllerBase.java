package sa.gov.nic.bio.bw.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.interfaces.RegionHideableController;

/**
 * A JavaFX controller that is associated with a region of the primary stage.
 * The primary stage currently consists of 5 regions:
 * <ul>
 *     <li>body</li>
 *     <li>header</li>
 *     <li>footer</li>
 *     <li>menu</li>
 *     <li>devices-runner-gadget</li>
 * </ul>
 *
 * @author Fouad Almalki
 */
public abstract class RegionFxControllerBase extends FxControllerBase implements RegionHideableController
{
	@FXML protected Pane rootPane;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pane getRegionRootPane()
	{
		return rootPane;
	}
}