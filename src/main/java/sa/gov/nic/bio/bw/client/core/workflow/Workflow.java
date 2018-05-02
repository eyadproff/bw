package sa.gov.nic.bio.bw.client.core.workflow;

import java.util.Map;

/**
 * The workflow that manages business processes and monitors their state. The workflow starts by starting its
 * <code>onProcess()</code> method. <code>waitForUserTask()</code> is called from within <code>onProcess()</code>
 * to wait for user tasks which can be submitted by a different thread/context using <code>submitUserTask()</code>.
 * Workflows can be nested, i.e. workflow A's onProcess() can invoke workflow B's onProcess().
 * <code>waitForUserTask()</code> can throw a signal which is used to carry and propagate a message to
 * a parent workflow.
 *
 * @param <I> type of the workflow's input. Use <code>Void</code> in case of not input
 * @param <O> type of the workflow's output. Use <code>Void</code> in case of no output
 *
 * @author Fouad Almalki
 */
public interface Workflow<I, O>
{
	String KEY_WEBSERVICE_RESPONSE = "WEBSERVICE_RESPONSE";
	String KEY_SIGNAL_TYPE = "SIGNAL_TYPE";
	String KEY_ERROR_CODE = "ERROR_CODE";
	String KEY_ERROR_DETAILS = "ERROR_DETAILS";
	String KEY_EXCEPTION = "EXCEPTION";
	
	/**
	 * The body of the workflow.
	 *
	 * @param input the workflow's input if any, otherwise <code>null</code>
	 *
	 * @return the workflow's output if any, otherwise <code>null</code>
	 * @throws InterruptedException thrown upon interrupting the caller thread
	 * @throws Signal thrown to carry and propagate a message to a parent workflow
	 */
	O onProcess(I input) throws InterruptedException, Signal;
	
	/**
	 * Submit a user task to the workflow.
	 *
	 * @param uiDataMap the data submitted by the user when filling the form
	 */
	void submitUserTask(Map<String, Object> uiDataMap);
	
	/**
	 * Waits until some other thread submits a user task.
	 *
	 * @return the submitted user task
	 * @throws InterruptedException thrown upon interrupting the workflow thread
	 * @throws Signal thrown in case the submitted user task is a signal
	 */
	Map<String, Object> waitForUserTask() throws InterruptedException, Signal;
}