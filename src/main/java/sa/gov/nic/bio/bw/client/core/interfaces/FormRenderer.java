package sa.gov.nic.bio.bw.client.core.interfaces;

import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;

import java.util.Map;

/**
 * Used as a UI proxy to delegate the UI rendering tasks from the workflow manager to the UI manager.
 *
 * @author Fouad Almalki
 */
public interface FormRenderer
{
	/**
	 * A callback method is called whenever we need to render a form on the GUI.
	 *
	 * @param controllerClass the class of JavaFX controller that is responsible for rendering the form
	 * @param inputDataMap the input data used to populate data on the form
	 */
	BodyFxControllerBase renderForm(Class<?> controllerClass, Map<String, Object> inputDataMap) throws Signal;
}