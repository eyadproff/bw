package sa.gov.nic.bio.bw.client.core.interfaces;

import java.net.URL;

/**
 * Every JavaFX controller that implements this interface will provide the ability to locate its FXML file
 * and bundle resources.
 *
 * @author Fouad Almalki
 * @since 1.3.0
 */
public interface ControllerResourcesLocator
{
	URL getFxmlLocation();
	ResourceBundleCollection getResourceBundleCollection();
}