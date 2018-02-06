package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.interfaces.RegionHideableController;

/**
 * A JavaFX controller that is associated with a region of the primary stage.
 * The primary stage currently consists of 4 regions:
 * <ul>
 *     <li>body</li>
 *     <li>header</li>
 *     <li>footer</li>
 *     <li>menu</li>
 * </ul>
 *
 * @author Fouad Almalki
 * @since 1.2.1
 */
public abstract class RegionFxControllerBase extends FxControllerBase implements RegionHideableController
{
	@FXML protected Pane rootPane;
	protected CoreFxController coreFxController;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pane getRegionRootPane()
	{
		return rootPane;
	}
	
	/**
	 * Attach the core JavaFX controller that is associated with the whole primary stage to this controller.
	 *
	 * @param coreFxController the core controller to attach
	 */
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
}