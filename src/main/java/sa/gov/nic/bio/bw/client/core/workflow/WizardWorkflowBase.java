package sa.gov.nic.bio.bw.client.core.workflow;

import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.wizard.WizardPane;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStep;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public abstract class WizardWorkflowBase extends WorkflowBase<Void, Void> implements WizardWorkflow
{
	public static final String KEY_WORKFLOW_DIRECTION = "WORKFLOW_DIRECTION";
	public static final String VALUE_WORKFLOW_DIRECTION_BACKWARD = "WORKFLOW_DIRECTION_BACKWARD";
	public static final String VALUE_WORKFLOW_DIRECTION_FORWARD = "WORKFLOW_DIRECTION_FORWARD";
	public static final String VALUE_WORKFLOW_DIRECTION_START_OVER = "WORKFLOW_DIRECTION_START_OVER";
	
	private final Map<Class<? extends Callable<ServiceResponse<Void>>>,
											Callable<ServiceResponse<Void>>> lookupInstancesCache = new HashMap<>();
	
	public WizardWorkflowBase(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(Void input) throws InterruptedException, Signal
	{
		String basePackage = getClass().getPackage().getName().replace(".", "/");
		basePackage = basePackage.substring(0, basePackage.lastIndexOf('/'));
		
		ResourceBundle stringsBundle;
		try
		{
			stringsBundle = ResourceBundle.getBundle(basePackage + "/bundles/strings",
			                                         Context.getGuiLanguage().getLocale(), new UTF8Control());
		}
		catch(MissingResourceException e)
		{
			e.printStackTrace();
			return null;
		}
		
		WithLookups withLookups = getClass().getAnnotation(WithLookups.class);
		if(withLookups != null)
		{
			Class<? extends Callable<ServiceResponse<Void>>>[] lookupClasses = withLookups.value();
			try
			{
				outer: while(true)
				{
					renderUi(LookupFxController.class);
					waitForUserInput();
					
					for(Class<? extends Callable<ServiceResponse<Void>>> lookupClass : lookupClasses)
					{
						Callable<ServiceResponse<Void>> instance = lookupInstancesCache.get(lookupClass);
						if(instance == null)
						{
							instance = lookupClass.newInstance();
							lookupInstancesCache.put(lookupClass, instance);
						}
						
						ServiceResponse<Void> serviceResponse = instance.call();
						if(!serviceResponse.isSuccess())
						{
							uiInputData.put(KEY_WEBSERVICE_RESPONSE, serviceResponse);
							continue outer;
						}
					}
					
					break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(SinglePageWorkflowBase.class.isAssignableFrom(getClass()))
		{
			Context.getCoreFxController().clearWizardBar();
		}
		else
		{
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
		}
		
		int step = 0;
		
		while(true)
		{
			try
			{
				boolean handled = onStep(step);
				
				if(!handled)
				{
					Map<String, Object> payload = new HashMap<>();
					payload.put(KEY_ERROR_DETAILS, new String[]{"Step number (" + step +
													") was not handled in workflow (" + getClass().getName() + ")!"});
					throw new Signal(SignalType.INVALID_STATE, payload);
				}
			}
			catch(Signal signal)
			{
				if(signal.getSignalType() != SignalType.WIZARD_NAVIGATION &&
						signal.getSignalType() != SignalType.INTERRUPT_SEQUENT_TASKS) throw signal;
			}
			
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
	
	@Override
	public void waitForUserInput() throws InterruptedException, Signal
	{
		super.waitForUserInput();
		
		if(isLeaving()) throw new Signal(SignalType.WIZARD_NAVIGATION, null);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getData(Class<?> outputClass, String outputName)
	{
		return (T) uiInputData.get(outputClass.getName() + "#" + outputName);
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
	
	protected boolean isGoingBackward()
	{
		Object direction = uiInputData.get(KEY_WORKFLOW_DIRECTION);
		return VALUE_WORKFLOW_DIRECTION_BACKWARD.equals(direction);
	}
	
	protected boolean isGoingForward()
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