package sa.gov.nic.bio.bw.client.home;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SignalType;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.home.controllers.HomePaneFxController;
import sa.gov.nic.bio.bw.client.home.utils.HomeErrorCodes;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.lang.reflect.Constructor;
import java.util.Map;

public class HomeWorkflow extends WorkflowBase<LoginBean, Void>
{
	public static final String KEY_MENU_WORKFLOW_CLASS = "MENU_WORKFLOW_CLASS";
	
	@Override
	public void onProcess() throws InterruptedException, Signal
	{
		while(true)
		{
			try
			{
				renderUiAndWaitForUserInput(HomePaneFxController.class); // render home page on first load only
			}
			catch(Signal signal)
			{
				SignalType signalType = signal.getSignalType();
				
				outerLoop: while(true) switch(signalType)
				{
					default: throw signal;
					case MENU_NAVIGATION:
					{
						Map<String, Object> payload = signal.getPayload();
						Class<?> menuWorkflowClass = (Class<?>) payload.get(KEY_MENU_WORKFLOW_CLASS);
						
						Workflow<?, ?> subWorkflow;
						try
						{
							Constructor<?> declaredConstructor = menuWorkflowClass.getDeclaredConstructor();
							subWorkflow = (Workflow<?, ?>) declaredConstructor.newInstance();
						}
						catch(Exception e)
						{
							String errorCode = HomeErrorCodes.C004_00003.getCode();
							String[] errorDetails = {"Failed to load the menu workflow class!",
													 "menuWorkflowClass = " +
													(menuWorkflowClass == null ? null : menuWorkflowClass.getName())};
							
							Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
							break outerLoop;
						}
						
						try
						{
							subWorkflow.onProcess();
						}
						catch(Signal subWorkflowSignal)
						{
							signal = subWorkflowSignal;
							signalType = subWorkflowSignal.getSignalType();
							continue outerLoop;
						}
						catch(Throwable t)
						{
							String errorCode = HomeErrorCodes.C004_00004.getCode();
							String[] errorDetails = {"The subWorkflow throws uncaught exception!",
													 "subWorkflow type = " + subWorkflow.getClass().getName()};
							Context.getCoreFxController().showErrorDialog(errorCode, t, errorDetails);
							break outerLoop;
						}
						
						// shouldn't reach here
						String errorCode = HomeErrorCodes.C004_00005.getCode();
						String[] errorDetails = {"The subWorkflow returns normally without a signal!",
															"subWorkflow type = " + subWorkflow.getClass().getName()};
						
						Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
						break outerLoop;
					}
				}
			}
		}
	}
}