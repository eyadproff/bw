package sa.gov.nic.bio.bw.client.login;

import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SignalType;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.login.controllers.LoginPaneFxController;
import sa.gov.nic.bio.bw.client.login.controllers.LoginPaneFxController.LoginMethod;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;
import sa.gov.nic.bio.bw.client.login.workflow.CheckForNewUpdatesWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.LoginByUsernameAndFingerprintWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.LoginByUsernameAndPasswordWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.MenuRolesLookupWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.PrepareHomeBeanWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.PrepareUserMenusWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.ScheduleRefreshTokenWorkflowTask;
import sa.gov.nic.bio.bw.client.login.workflow.UserSessionCreationWorkflowTask;

import java.util.Arrays;

/**
 * The login workflow.
 *
 * @author Fouad Almalki
 */
public class LoginWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(LoginPaneFxController.class);
		
		executeTask(CheckForNewUpdatesWorkflowTask.class);
		
		LoginBean loginBean;
		LoginMethod loginMethod = getData(LoginPaneFxController.class, "loginMethod");
		
		switch(loginMethod)
		{
			default:
			case USERNAME_AND_PASSWORD:
			{
				passData(LoginPaneFxController.class, LoginByUsernameAndPasswordWorkflowTask.class,
				         "username", "password");
				
				executeTask(LoginByUsernameAndPasswordWorkflowTask.class);
				loginBean = getData(LoginByUsernameAndPasswordWorkflowTask.class, "loginBean");
				break;
			}
			case USERNAME_AND_FINGERPRINT:
			{
				passData(LoginPaneFxController.class, LoginByUsernameAndFingerprintWorkflowTask.class,
				         "username", "fingerPosition", "fingerprint");
				
				executeTask(LoginByUsernameAndFingerprintWorkflowTask.class);
				loginBean = getData(LoginByUsernameAndFingerprintWorkflowTask.class, "loginBean");
				break;
			}
		}
		
		executeTask(UserSessionCreationWorkflowTask.class);
		executeTask(MenuRolesLookupWorkflowTask.class);
		
		setData(PrepareHomeBeanWorkflowTask.class, "userInfo", loginBean.getUserInfo());
		executeTask(PrepareHomeBeanWorkflowTask.class);
		
		passData(MenuRolesLookupWorkflowTask.class, PrepareUserMenusWorkflowTask.class, "menusRoles");
		setData(PrepareUserMenusWorkflowTask.class, "userRoles",
                Arrays.asList(loginBean.getUserInfo().getOriginalStringRoles()));
		executeTask(PrepareUserMenusWorkflowTask.class);
		
		setData(ScheduleRefreshTokenWorkflowTask.class, "userToken", loginBean.getUserToken());
		executeTask(ScheduleRefreshTokenWorkflowTask.class);
		
		throw new Signal(SignalType.SUCCESS_LOGIN);
	}
}