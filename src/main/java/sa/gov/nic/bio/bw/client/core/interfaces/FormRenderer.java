package sa.gov.nic.bio.bw.client.core.interfaces;

import java.util.Map;

/**
 * Used as a UI proxy to delegate the UI rendering tasks from the workflow manager to the UI manager.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public interface FormRenderer
{
	/**
	 * A callback method is called whenever we need to render a form on the GUI.
	 *
	 * @param controllerClass the class of JavaFX controller that is responsible for rendering the form
	 * @param inputDataMap the input data used to populate data on the form
	 */
	void renderForm(Class<?> controllerClass, Map<String, Object> inputDataMap);
}