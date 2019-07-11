package sa.gov.nic.bio.bw.home;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SignalType;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.home.controllers.HomePaneFxController;
import sa.gov.nic.bio.bw.home.utils.HomeErrorCodes;

import java.lang.reflect.Constructor;
import java.util.Map;

public class HomeWorkflow extends SinglePageWorkflowBase
{
	@Override
	public String getId()
	{
		return KEY_WORKFLOW_HOME;
	}
	
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		while(true)
		{
			try
			{
				renderUiAndWaitForUserInput(HomePaneFxController.class);
			}
			catch(Signal signal)
			{
				if(signal.getSignalType() == SignalType.EXIT_WORKFLOW) throw signal;
				
				Context.getWorkflowManager().setCurrentWorkflow(null, getTabIndex());
				Context.getWorkflowManager().getUserTasks(getTabIndex()).clear();
				SignalType signalType = signal.getSignalType();
				
				outerLoop: while(true) switch(signalType)
				{
					default: throw signal;
					case MENU_NAVIGATION:
					{
						Map<String, Object> payload = signal.getPayload();
						Class<?> menuWorkflowClass = (Class<?>) payload.get(KEY_MENU_WORKFLOW_CLASS);
						
						Workflow subWorkflow;
						try
						{
							Constructor<?> declaredConstructor = menuWorkflowClass.getDeclaredConstructor();
							subWorkflow = (Workflow) AppUtils.instantiateClassByReflection(declaredConstructor);
							subWorkflow.setTabIndex(getTabIndex());
							Context.getWorkflowManager().setCurrentWorkflow(subWorkflow, getTabIndex());
						}
						catch(Exception e)
						{
							String errorCode = HomeErrorCodes.C004_00001.getCode();
							String[] errorDetails = {"Failed to load the menu workflow class!",
													 "menuWorkflowClass = " +
													(menuWorkflowClass == null ? null : menuWorkflowClass.getName())};
							
							Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
							break outerLoop;
						}
						
						try
						{
							subWorkflow.onProcess(null);
						}
						catch(Signal subWorkflowSignal)
						{
							signal = subWorkflowSignal;
							signalType = subWorkflowSignal.getSignalType();
							continue outerLoop;
						}
						catch(Throwable t)
						{
							String errorCode = HomeErrorCodes.C004_00002.getCode();
							String[] errorDetails = {"The subWorkflow throws uncaught exception!",
													 "subWorkflow type = " + subWorkflow.getClass().getName()};
							Context.getCoreFxController().showErrorDialog(errorCode, t, errorDetails, getTabIndex());
							break outerLoop;
						}
						
						// shouldn't reach here
						String errorCode = HomeErrorCodes.C004_00003.getCode();
						String[] errorDetails = {"The subWorkflow returns normally without a signal!",
															"subWorkflow type = " + subWorkflow.getClass().getName()};
						
						Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails, getTabIndex());
						break outerLoop;
					}
				}
			}
		}
	}
}