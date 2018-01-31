package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.login.LoginPaneFxController;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * The login workflow.
 *
 * @author Fouad Almalki
 * @since 1.0.0
 */
public class LoginWorkflow extends WorkflowBase<Void, LoginBean>
{
	public LoginWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public LoginBean onProcess(Void input) throws InterruptedException, Signal
	{
		Map<String, Object> loginWorkflowResponse = new HashMap<>();
		
		while(true)
		{
			formRenderer.renderForm(LoginPaneFxController.class, loginWorkflowResponse);
			loginWorkflowResponse.clear();
			
			Map<String, Object> userTaskDataMap = waitForUserTask();
			
			String username = (String) userTaskDataMap.get("username");
			String password = (String) userTaskDataMap.get("password");
			
			WebServiceResponse<LoginBean> response = new LoginService().execute(username, password);
			
			if(response.isSuccess()) return response.getResult();
			else loginWorkflowResponse.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}