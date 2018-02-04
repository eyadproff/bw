package sa.gov.nic.bio.bw.client.home.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SignalType;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.home.HomePaneFxController;
import sa.gov.nic.bio.bw.client.home.utils.HomeErrorCodes;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class HomeWorkflow extends WorkflowBase<LoginBean, Void>
{
	public static final String KEY_MENU_WORKFLOW_CLASS = "MENU_WORKFLOW_CLASS";
	
	public HomeWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(LoginBean input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		
		while(true)
		{
			formRenderer.renderForm(HomePaneFxController.class, uiInputData); // render home page
			
			try
			{
				waitForUserTask(); // user selects a menu or logout
			}
			catch(Signal signal)
			{
				handleSignal(signal, uiInputData);
			}
		}
	}
	
	private void handleSignal(Signal signal, Map<String, Object> uiInputData) throws InterruptedException, Signal
	{
		outerLoop: while(true)
		{
			Map<String, Object> payload = signal.getPayload();
			SignalType signalType = signal.getSignalType();
			
			switch(signalType)
			{
				case LOGOUT:
				{
					throw signal;
				}
				case MENU_NAVIGATION:
				{
					Class<?> menuWorkflowClass = (Class<?>) payload.get(KEY_MENU_WORKFLOW_CLASS);
					
					Workflow<?, ?> subWorkflow;
					try
					{
						Constructor<?> declaredConstructor = menuWorkflowClass
								.getDeclaredConstructor(FormRenderer.class, BlockingQueue.class);
						subWorkflow = (Workflow<?, ?>) declaredConstructor.newInstance(formRenderer, userTasks);
					}
					catch(Exception e)
					{
						String errorCode = HomeErrorCodes.C004_00003.getCode();
						String[] errorDetails = {"Failed to load the menu workflow class!",
								"menuWorkflowClass = " +
										(menuWorkflowClass == null ? null : menuWorkflowClass.getName())};
						
						uiInputData.put(KEY_ERROR_CODE, errorCode);
						uiInputData.put(KEY_ERROR_DETAILS, errorDetails);
						uiInputData.put(KEY_EXCEPTION, e);
						
						break outerLoop;
					}
					
					try
					{
						subWorkflow.onProcess(null);
					}
					catch(Signal subWorkflowSignal)
					{
						signal = subWorkflowSignal;
						continue outerLoop;
					}
					
					// shouldn't reach here
					String errorCode = HomeErrorCodes.C004_00004.getCode();
					String[] errorDetails = {"The subWorkflow returns normally without a signal!",
											 "subWorkflow type = " + subWorkflow.getClass().getName()};
					
					uiInputData.put(KEY_ERROR_CODE, errorCode);
					uiInputData.put(KEY_ERROR_DETAILS, errorDetails);
					uiInputData.put(KEY_EXCEPTION, null);
					
					break outerLoop;
				}
				default:
				{
					String errorCode = HomeErrorCodes.C004_00005.getCode();
					String[] errorDetails = {"Unknown signal type!", "signalType = " + signalType};
					
					uiInputData.put(KEY_ERROR_CODE, errorCode);
					uiInputData.put(KEY_ERROR_DETAILS, errorDetails);
					uiInputData.put(KEY_EXCEPTION, null);
					
					break outerLoop;
				}
			}
		}
	}
}