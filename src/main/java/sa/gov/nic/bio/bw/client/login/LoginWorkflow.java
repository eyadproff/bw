package sa.gov.nic.bio.bw.client.login;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WorkflowBase;
import sa.gov.nic.bio.bw.client.login.controllers.LoginPaneFxController;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;
import sa.gov.nic.bio.bw.client.login.workflow.LoginService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

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
		while(true)
		{
			renderUi(LoginPaneFxController.class);
			waitForUserInput();
			
			String username = (String) uiInputData.get("username");
			String password = (String) uiInputData.get("password");
			
			ServiceResponse<LoginBean> response;
			
			if(password != null) response = LoginService.execute(username, password);
			else
			{
				int fingerPosition = (int) uiInputData.get("fingerPosition");
				String fingerprint = (String) uiInputData.get("fingerprint");
				response = LoginService.execute(username, fingerPosition, fingerprint);
			}
			
			if(response.isSuccess()) return response.getResult();
			else uiInputData.put(KEY_WEBSERVICE_RESPONSE, response);
		}
	}
}