package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.login.LoginPaneFxController;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The login workflow.
 *
 * @author Fouad Almalki
 */
public class LoginWorkflow extends WorkflowBase<Void, LoginBean>
{
	public LoginWorkflow(AtomicReference<FormRenderer> formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public LoginBean onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> uiInputData = new HashMap<>();
		
		while(true)
		{
			formRenderer.get().renderForm(LoginPaneFxController.class, uiInputData);
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			String username = (String) userTaskDataMap.get("username");
			String password = (String) userTaskDataMap.get("password");
			
			ServiceResponse<LoginBean> response = LoginService.execute(username, password);
			
			if(response.isSuccess()) return response.getResult();
			else uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}