package sa.gov.nic.bio.bw.client.home.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.home.HomePaneFxController;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class HomeWorkflow extends WorkflowBase<LoginBean, Void>
{
	private static final Logger LOGGER = Logger.getLogger(HomeWorkflow.class.getName());
	
	public static final String KEY_LOGOUT_REQUESTED = "LOGOUT_REQUESTED";
	public static final String KEY_MENU_WORKFLOW_CLASS = "MENU_WORKFLOW_CLASS";
	
	public HomeWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public Void onProcess(LoginBean input) throws InterruptedException
	{
		while(true)
		{
			formRenderer.renderForm(HomePaneFxController.class, null); // render home page
			Map<String, Object> userTakDataMap = userTasks.take(); // user selects a menu or logout
			
			if(userTakDataMap.get(KEY_LOGOUT_REQUESTED) != null) return null;
			
			while(true)
			{
				Class<?> menuWorkflowClass = (Class<?>) userTakDataMap.get(KEY_MENU_WORKFLOW_CLASS);
				
				if(menuWorkflowClass == null)
				{
					LOGGER.severe(KEY_MENU_WORKFLOW_CLASS + " is null!");
					break;
				}
				
				Constructor<?> declaredConstructor = null;
				try
				{
					declaredConstructor = menuWorkflowClass.getDeclaredConstructor(FormRenderer.class, BlockingQueue.class);
				}
				catch(NoSuchMethodException e)
				{
					e.printStackTrace();
				}
				Workflow<?, ?> workflow = null;
				try
				{
					workflow = (Workflow<?, ?>) declaredConstructor.newInstance(formRenderer, userTasks);
				}
				catch(InstantiationException e)
				{
					e.printStackTrace();
				}
				catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch(InvocationTargetException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					workflow.onProcess(null);
				}
				catch(Signal signal)
				{
					Map<String, Object> payload = signal.getPayload();
				}
			}
		}
	}
}