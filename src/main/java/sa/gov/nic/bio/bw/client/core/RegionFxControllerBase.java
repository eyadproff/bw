package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.client.core.interfaces.RegionHideableController;

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
		onPostAttachingCoreFxController();
	}

	/**
	 * A callback that is called after <code>attachCoreFxController(CoreFxController coreFxController)</code>
	 */
	protected void onPostAttachingCoreFxController(){};
}