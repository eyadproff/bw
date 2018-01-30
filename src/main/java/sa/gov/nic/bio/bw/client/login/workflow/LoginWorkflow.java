package sa.gov.nic.bio.bw.client.login.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.login.LoginPaneFxController;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class LoginWorkflow extends WorkflowBase<Void, LoginBean>
{
	public LoginWorkflow(FormRenderer formRenderer, BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public LoginBean onProcess(Void input) throws InterruptedException
	{
		formRenderer.renderForm(LoginPaneFxController.class, null);
		
		while(true)
		{
			Map<String, Object> userTaskDataMap = userTasks.take();
			
			String username = (String) userTaskDataMap.get("username");
			String password = (String) userTaskDataMap.get("password");
			
			WebServiceResponse<LoginBean> response = new LoginService2().execute(username, password);
			
			if(response.isSuccess()) return response.getResult();
			else
			{
				Map<String, Object> loginWorkflowResponse = new HashMap<>();
				loginWorkflowResponse.put(KEY_WEBSERVICE_RESPONSE, response);
				formRenderer.renderForm(LoginPaneFxController.class, loginWorkflowResponse);
			}
		}
	}
}