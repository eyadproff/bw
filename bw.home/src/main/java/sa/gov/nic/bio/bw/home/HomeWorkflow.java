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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeWorkflow extends SinglePageWorkflowBase
{
	@Override
	public String getId()
	{
		return KEY_WORKFLOW_HOME;
	}
	
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
	@Override
	public void onStep() throws InterruptedException, Signal
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
						
						Workflow subWorkflow;
						try
						{
							Constructor<?> declaredConstructor = menuWorkflowClass.getDeclaredConstructor();
							subWorkflow = (Workflow) AppUtils.instantiateClassByReflection(declaredConstructor);
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