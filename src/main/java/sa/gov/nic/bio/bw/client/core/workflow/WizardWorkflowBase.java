package sa.gov.nic.bio.bw.client.core.workflow;

import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.features.commons.LookupFxController;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public abstract class WizardWorkflowBase<I, O> extends WorkflowBase<I, O> implements WizardWorkflow<I, O>
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
	public O onProcess(I input) throws InterruptedException, Signal
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
		
		URL wizardFxmlLocation = Thread.currentThread().getContextClassLoader()
																.getResource(basePackage + "/fxml/wizard.fxml");
		if(wizardFxmlLocation != null)
		{
			FXMLLoader wizardPaneLoader = new FXMLLoader(wizardFxmlLocation, stringsBundle);
			wizardPaneLoader.setClassLoader(Context.getFxClassLoader());
			Context.getCoreFxController().loadWizardBar(wizardPaneLoader);
		}
		
		WithLookups annotation = getClass().getAnnotation(WithLookups.class);
		if(annotation != null)
		{
			Class<? extends Callable<ServiceResponse<Void>>>[] lookupClasses = annotation.value();
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
		
		int step = 0;
		
		while(true)
		{
			onStep(step);
			
			if(isGoingBackward())
			{
				Context.getCoreFxController().moveWizardBackward();
				step--;
			}
			else if(isGoingForward())
			{
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