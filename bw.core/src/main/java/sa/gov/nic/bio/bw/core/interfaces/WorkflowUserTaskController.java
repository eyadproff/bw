package sa.gov.nic.bio.bw.core.interfaces;

import java.util.Map;

/**
 * Every JavaFX controller that implements this interface will act as a controller for a user task in the workflow.
 *
 * @author Fouad Almalki
 */
public interface WorkflowUserTaskController
{
	/**
	 * A callback that is called when the workflow begins a user task.
	 *
	 * @param newForm whether this is a new form or the existing form
	 * @param uiInputData data passed by the workflow to the controller
	 */
	default void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData){} // TODO: remove it
	
	default void onReturnFromWorkflow(boolean successfulResponse){}
	default void onShowingProgress(boolean bShow){}
}