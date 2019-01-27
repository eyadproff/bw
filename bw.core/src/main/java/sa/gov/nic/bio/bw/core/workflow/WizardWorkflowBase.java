package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.controllers.LookupFxController;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.CoreErrorCodes;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.wizard.WizardPane;
import sa.gov.nic.bio.bw.core.wizard.WizardStep;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class WizardWorkflowBase extends WorkflowBase implements ResourceBundleProvider, DataConveyor
{
	public static final String KEY_WORKFLOW_DIRECTION = "WORKFLOW_DIRECTION";
	public static final String VALUE_WORKFLOW_DIRECTION_BACKWARD = "WORKFLOW_DIRECTION_BACKWARD";
	public static final String VALUE_WORKFLOW_DIRECTION_FORWARD = "WORKFLOW_DIRECTION_FORWARD";
	public static final String VALUE_WORKFLOW_DIRECTION_START_OVER = "WORKFLOW_DIRECTION_START_OVER";
	
	private final Map<Class<? extends Callable<TaskResponse<?>>>,
											Callable<TaskResponse<?>>> lookupInstancesCache = new HashMap<>();
	
	private Future<? extends TaskResponse<?>> future;
	protected Map<String, String> configurations;
	
	public abstract void onStep(int step) throws InterruptedException, Signal;
	
	@Override
	public void interrupt(Signal interruptionSignal)
	{
		super.interrupt(interruptionSignal);
		
		if(future != null) future.cancel(true);
	}
		
	@Override
	public void onProcess(Map<String, String> configurations) throws Signal
	{
		this.configurations = configurations;
		Context.getWorkflowManager().getUserTasks().clear();
		
		ResourceBundle stringsBundle = Context.getModuleResourceBundleProviders().get(getClass().getModule().getName())
																		.getStringsResourceBundle(Locale.getDefault());
		
		WithLookups withLookups = getClass().getAnnotation(WithLookups.class);
		if(withLookups != null)
		{
			Class<? extends Callable<TaskResponse<?>>>[] lookupClasses = withLookups.value();
			try
			{
				outer: while(true)
				{
					renderUiAndWaitForUserInput(LookupFxController.class);
					
					for(Class<? extends Callable<TaskResponse<?>>> lookupClass : lookupClasses)
					{
						Callable<TaskResponse<?>> instance = lookupInstancesCache.get(lookupClass);
						if(instance == null)
						{
							instance = lookupClass.getConstructor().newInstance();
							lookupInstancesCache.put(lookupClass, instance);
						}
						
						Thread[] taskThread = new Thread[1];
						Callable<TaskResponse<?>> finalInstance = instance;
						future = Context.getExecutorService().submit(() ->
						{
							taskThread[0] = Thread.currentThread();
							return finalInstance.call();
						});
						
						TaskResponse<?> taskResponse;
						try
						{
							taskResponse = future.get();
						}
						catch(ExecutionException e)
						{
							throw e.getCause();
						}
						catch(CancellationException e)
						{
							LOGGER.info("The lookup task (" + lookupClass.getSimpleName() + ") is cancelled!");
							taskThread[0].interrupt();
							if(getInterruptionSignal() != null) throwInterruptionSignal();
							return;
						}
						catch(InterruptedException e)
						{
							if(getInterruptionSignal() != null) throwInterruptionSignal();
							return;
						}
						
						if(getInterruptionSignal() != null) throwInterruptionSignal();
						
						if(!taskResponse.isSuccess())
						{
							uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
							continue outer;
						}
					}
					
					break;
				}
			}
			catch(Throwable e)
			{
				if(e instanceof Signal) throw (Signal) e;
				
				String errorCode = CoreErrorCodes.C002_00020.getCode();
				String[] errorDetails = {"Failed during calling lookups of the workflow (" + getClass().getName() +
																												")!"};
				Workflow.throwInvalidStateSignal(errorCode, errorDetails, e);
			}
		}
		
		Wizard wizard = getClass().getAnnotation(Wizard.class);
		if(wizard != null)
		{
			Step[] steps = wizard.value();
			WizardStep[] wizardSteps = new WizardStep[steps.length];
			
			for(int i = 0; i < steps.length; i++)
			{
				Step step = steps[i];
				String iconId = step.iconId();
				String title = step.title();
				
				WizardStep wizardStep = new WizardStep(iconId, stringsBundle.getString(title));
				wizardSteps[i] = wizardStep;
			}
			
			WizardPane wizardPane = new WizardPane(wizardSteps);
			Context.getCoreFxController().setWizardPane(wizardPane);
		}
		else Context.getCoreFxController().clearWizardBar();
		
		Integer workflowId = getWorkflowId();
		if(workflowId != null)
		{
			tcn = AppUtils.getRandom18DigitsNumber();
			LOGGER.info("Generating TCN (" + tcn + ") for the workflow (" + workflowId + ": " +
					            getClass().getSimpleName() + ")");
		}
		
		int step = 0;
		
		while(true)
		{
			try
			{
				onStep(step);
				
				if(!renderedAtLeastOnceInTheStep)
				{
					String errorCode = CoreErrorCodes.C002_00021.getCode();
					String[] errorDetails = {"The workflow (" + getClass().getName() +
													") has no renderUiAndWaitForUserInput() at step (" + step + ")!"};
					Workflow.throwInvalidStateSignal(errorCode, errorDetails);
				}
				else renderedAtLeastOnceInTheStep = false;
			}
			catch(Signal signal)
			{
				switch(signal.getSignalType())
				{
					default: throw signal;
					case RESET_WORKFLOW_STEP:
					{
						Map<String, Object> payload = signal.getPayload();
						uiInputData.putAll(payload);
						continue;
					}
					case WIZARD_NAVIGATION:
					{
						if(isGoingBackward())
						{
							uiInputData.remove(KEY_WORKFLOW_DIRECTION);
							Context.getCoreFxController().moveWizardBackward();
							step--;
						}
						else if(isGoingForward())
						{
							uiInputData.remove(KEY_WORKFLOW_DIRECTION);
							Context.getCoreFxController().moveWizardForward();
							step++;
						}
						else if(isStartingOver())
						{
							if(workflowId != null)
							{
								tcn = AppUtils.getRandom18DigitsNumber();
								LOGGER.info("Regenerating TCN (" + tcn + ") for the workflow (" + workflowId +
									                                        ": " + getClass().getSimpleName() + ")");
							}
							
							uiInputData.clear();
							Context.getCoreFxController().moveWizardToTheBeginning();
							step = 0;
						}
					}
				}
			}
			catch(Throwable t)
			{
				String errorCode = CoreErrorCodes.C002_00022.getCode();
				String[] errorDetails = {"An error occurs in the workflow (" + getClass().getName() + ") at step (" +
										 step + ")!"};
				Workflow.throwInvalidStateSignal(errorCode, errorDetails, t);
			}
		}
	}
	
	@Override
	public void renderUiAndWaitForUserInput(Class<? extends BodyFxControllerBase> controllerClass)
																					throws InterruptedException, Signal
	{
		super.renderUiAndWaitForUserInput(controllerClass);
		
		if(isLeaving()) throw new Signal(SignalType.WIZARD_NAVIGATION, null);
	}
	
	@Override
	public Map<String, Object> getDataMap()
	{
		return uiInputData;
	}
	
	private boolean isGoingBackward()
	{
		Object direction = uiInputData.get(KEY_WORKFLOW_DIRECTION);
		return VALUE_WORKFLOW_DIRECTION_BACKWARD.equals(direction);
	}
	
	private boolean isGoingForward()
	{
		Object direction = uiInputData.get(KEY_WORKFLOW_DIRECTION);
		return VALUE_WORKFLOW_DIRECTION_FORWARD.equals(direction);
	}
	
	protected boolean isStartingOver()
	{
		Object direction = uiInputData.get(KEY_WORKFLOW_DIRECTION);
		return VALUE_WORKFLOW_DIRECTION_START_OVER.equals(direction);
	}
	
	protected boolean isLeaving()
	{
		return isGoingBackward() || isGoingForward() || isStartingOver();
	}
}