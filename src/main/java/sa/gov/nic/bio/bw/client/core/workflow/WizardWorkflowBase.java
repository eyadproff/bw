package sa.gov.nic.bio.bw.client.core.workflow;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class WizardWorkflowBase<I, O> extends WorkflowBase<I, O> implements WizardWorkflow<I, O>
{
	protected Map<String, Object> uiInputData = new HashMap<>();
	
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
		FXMLLoader wizardPaneLoader = new FXMLLoader(wizardFxmlLocation, stringsBundle);
		wizardPaneLoader.setClassLoader(Context.getFxClassLoader());
		Context.getCoreFxController().loadWizardBar(wizardPaneLoader);
		
		init();
		int step = 0;
		
		while(true)
		{
			Map<String, Object> uiOutputData = onStep(step);
			
			if(uiOutputData != null)
			{
				Object direction = uiOutputData.get("direction");
				if("backward".equals(direction))
				{
					Platform.runLater(() -> Context.getCoreFxController().moveWizardBackward());
					step--;
				}
				else if("forward".equals(direction))
				{
					Platform.runLater(() -> Context.getCoreFxController().moveWizardForward());
					step++;
				}
				else if("startOver".equals(direction))
				{
					Platform.runLater(() -> Context.getCoreFxController().moveWizardToTheBeginning());
					uiInputData.clear();
					step = 0;
				}
			}
		}
	}
}