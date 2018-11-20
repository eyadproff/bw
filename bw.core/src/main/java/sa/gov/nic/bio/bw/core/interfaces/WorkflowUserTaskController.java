package sa.gov.nic.bio.bw.core.interfaces;

/**
 * Every JavaFX controller that implements this interface will act as a controller for a user task in the workflow.
 *
 * @author Fouad Almalki
 */
public interface WorkflowUserTaskController
{
	default void onReturnFromWorkflow(boolean successfulResponse){}
	default void onShowingProgress(boolean bShow){}
}