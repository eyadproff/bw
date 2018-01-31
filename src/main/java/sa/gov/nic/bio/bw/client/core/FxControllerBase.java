package sa.gov.nic.bio.bw.client.core;

import javafx.fxml.FXML;
import sa.gov.nic.bio.bw.client.core.interfaces.ControllerResourcesLocator;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A base class for all JavaFX controllers.
 *
 * @author Fouad Almalki
 * @since 1.2.1
 */
public abstract class FxControllerBase implements ControllerResourcesLocator
{
	protected ResourceBundle labelsBundle;
	protected ResourceBundle messagesBundle;
	
	/**
	 * Called after the FXML is completely processed and the JavaFX nodes are created. All fields annotated with @FXML
	 * is processed before this method is called.
	 */
	@FXML
	protected void initialize(){}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final URL getFxmlLocation()
	{
		return getClass().getResource("fxml/gui.fxml");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ResourceBundleCollection getResourceBundleCollection()
	{
		String base = getClass().getPackage().getName().replace(".", "/");
		
		return new ResourceBundleCollection()
		{
			@Override
			public String getLabelsBundlePath()
			{
				return base + "/bundles/labels";
			}
			
			@Override
			public String getMessagesBundlePath()
			{
				return base + "/bundles/messages";
			}
		};
	}
	
	/**
	 * Attach resource bundles to this controller.
	 *
	 * @param labelsBundle resource bundle for the labels
	 * @param messagesBundle resource bundle for the messages
	 */
	public final void attachResourceBundles(ResourceBundle labelsBundle, ResourceBundle messagesBundle)
	{
		this.labelsBundle = labelsBundle;
		this.messagesBundle = messagesBundle;
	}
}