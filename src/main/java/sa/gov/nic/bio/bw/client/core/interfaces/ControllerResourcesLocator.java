package sa.gov.nic.bio.bw.client.core.interfaces;

import java.net.URL;

/**
 * Every JavaFX controller that implements this interface will provide the ability to locate its FXML file
 * and bundle resources.
 *
 * @author Fouad Almalki
 */
public interface ControllerResourcesLocator
{
	/**
	 * @return the location of the FXML file
	 */
	URL getFxmlLocation();
	
	/**
	 * @return path to the resource bundle that are associated with this controller
	 */
	String getStringsResourceBundle();
}