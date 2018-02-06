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
	protected ResourceBundle stringsBundle;
	
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
		return () -> getClass().getPackage().getName().replace(".", "/") + "/bundles/strings";
	}
	
	/**
	 * Attach resource bundles to this controller.
	 *
	 * @param stringsBundle resource bundle for the strings
	 */
	public final void attachResourceBundles(ResourceBundle stringsBundle)
	{
		this.stringsBundle = stringsBundle;
	}
}