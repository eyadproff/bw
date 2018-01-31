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
			
			try
			{
				waitForUserTask(); // user selects a menu or logout
			}
			catch(Signal signal)
			{
				Map<String, Object> payload = signal.getPayload();
				
				switch(signal.getSignalType())
				{
					case LOGOUT:
					{
						break;
					}
					case MENU_NAVIGATION:
					{
						Class<?> menuWorkflowClass = (Class<?>) payload.get(KEY_MENU_WORKFLOW_CLASS);
						
						Constructor<?> declaredConstructor = null;
						try
						{
							declaredConstructor = menuWorkflowClass.getDeclaredConstructor(FormRenderer.class, BlockingQueue.class);
						}
						catch(NoSuchMethodException e)
						{
							// TODO:
							e.printStackTrace();
							continue;
						}
						
						Workflow<?, ?> workflow = null;
						try
						{
							workflow = (Workflow<?, ?>) declaredConstructor.newInstance(formRenderer, userTasks);
						}
						catch(InstantiationException e)
						{
							// TODO:
							e.printStackTrace();
							continue;
						}
						catch(IllegalAccessException e)
						{
							// TODO:
							e.printStackTrace();
							continue;
						}
						catch(InvocationTargetException e)
						{
							// TODO:
							e.printStackTrace();
							continue;
						}
						
						try
						{
							workflow.onProcess(null);
						}
						catch(Signal subWorkflowSignal)
						{
							// TODO:
						}
						
						break;
					}
					default:
					{
						// TODO
					}
				}
			}
		}
	}
}