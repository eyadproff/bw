package sa.gov.nic.bio.bw.core.workflow;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.controllers.LookupFxController;
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

public abstract class WizardWorkflowBase extends WorkflowBase implements ResourceBundleProvider
{
	public static final String KEY_WORKFLOW_DIRECTION = "WORKFLOW_DIRECTION";
	public static final String VALUE_WORKFLOW_DIRECTION_BACKWARD = "WORKFLOW_DIRECTION_BACKWARD";
	public static final String VALUE_WORKFLOW_DIRECTION_FORWARD = "WORKFLOW_DIRECTION_FORWARD";
	public static final String VALUE_WORKFLOW_DIRECTION_START_OVER = "WORKFLOW_DIRECTION_START_OVER";
	
	private final Map<Class<? extends Callable<TaskResponse<Void>>>,
											Callable<TaskResponse<Void>>> lookupInstancesCache = new HashMap<>();
	
	public abstract void onStep(int step) throws InterruptedException, Signal;
		
	@Override
	public void onProcess() throws InterruptedException, Signal
	{
		ResourceBundle stringsBundle = Context.getModuleResourceBundleProviders().get(getClass().getModule().getName())
																		.getStringsResourceBundle(Locale.getDefault());
		
		WithLookups withLookups = getClass().getAnnotation(WithLookups.class);
		if(withLookups != null)
		{
			Class<? extends Callable<TaskResponse<Void>>>[] lookupClasses = withLookups.value();
			try
			{
				outer: while(true)
				{
					renderUiAndWaitForUserInput(LookupFxController.class);
					
					for(Class<? extends Callable<TaskResponse<Void>>> lookupClass : lookupClasses)
					{
						Callable<TaskResponse<Void>> instance = lookupInstancesCache.get(lookupClass);
						if(instance == null)
						{
							instance = lookupClass.getConstructor().newInstance();
							lookupInstancesCache.put(lookupClass, instance);
						}
						
						TaskResponse<Void> taskResponse = instance.call();
						if(!taskResponse.isSuccess())
						{
							uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
							continue outer;
						}
					}
					
					break;
				}
			}
			catch(Exception e)
			{
				String errorCode = CoreErrorCodes.C002_00025.getCode();
				String errorMessage = "Failed during calling lookups of the workflow (" + getClass().getName() + ")!";
				Map<String, Object> payload = new HashMap<>();
				payload.put(KEY_ERROR_CODE, errorCode);
				payload.put(KEY_EXCEPTION, e);
				payload.put(KEY_ERROR_DETAILS, new String[]{errorMessage});
				throw new Signal(SignalType.INVALID_STATE, payload);
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
		
		int step = 0;
		
		while(true)
		{
			try
			{
				onStep(step);
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
						if(!renderedAtLeastOnceInTheStep)
						{
							String errorCode = CoreErrorCodes.C002_00026.getCode();
							String[] errorDetails = {"The workflow (" + getClass().getName() +
												     ") has no renderUiAndWaitForUserInput() at step (" + step + ")!"};
							Context.getCoreFxController().showErrorDialog(errorCode, null, errorDetails);
							continue;
						}
						else renderedAtLeastOnceInTheStep = false;
						
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
							uiInputData.clear();
							Context.getCoreFxController().moveWizardToTheBeginning();
							step = 0;
						}
					}
				}
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
	
	@SuppressWarnings("unchecked")
	protected <T> T getData(Class<?> outputClass, String outputName)
	{
		return (T) uiInputData.get(outputClass.getName() + "#" + outputName);
	}
	
	protected void passData(Class<?> outputClass, Class<?> inputClass, String... inputOutputNames)
	{
		for(String inputOutputName : inputOutputNames) passData(outputClass, inputClass, inputOutputName);
	}
	
	protected void passData(Class<?> outputClass, Class<?> inputClass, String inputOutputName)
	{
		passData(outputClass, inputOutputName, inputClass, inputOutputName, null);
	}
	
	protected void passData(Class<?> outputClass, String outputName, Class<?> inputClass, String inputName)
	{
		passData(outputClass, outputName, inputClass, inputName, null);
	}
	
	@SuppressWarnings("unchecked")
	protected <T1, T2> void passData(Class<?> outputClass, String outputName, Class<?> inputClass, String inputName,
                                     Converter<T1, T2> converter)
	{
		Object value = uiInputData.get(outputClass.getName() + "#" + outputName);
		if(converter != null) value = converter.convert((T1) value);
		setData(inputClass, inputName, value);
	}
	
	protected void setData(Class<?> inputClass, String inputName, Object value)
	{
		uiInputData.put(inputClass.getName() + "#" + inputName, value);
	}
	
	protected void removeData(Class<?> inputClass, String inputName)
	{
		uiInputData.remove(inputClass.getName() + "#" + inputName);
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