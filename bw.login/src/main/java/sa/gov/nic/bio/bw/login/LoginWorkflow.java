package sa.gov.nic.bio.bw.login;

import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SignalType;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.login.controllers.LoginPaneFxController;
import sa.gov.nic.bio.bw.login.controllers.LoginPaneFxController.LoginMethod;
import sa.gov.nic.bio.bw.login.tasks.CheckForNewUpdatesWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.LoginByUsernameAndFingerprintWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.LoginByUsernameAndPasswordWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.MenuRolesLookupWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.PrepareHomeBeanWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.PrepareUserMenusWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.ScheduleRefreshTokenWorkflowTask;
import sa.gov.nic.bio.bw.login.tasks.UserSessionCreationWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.LoginBean;

import java.util.Arrays;

/**
 * The login workflow.
 *
 * @author Fouad Almalki
 */
public class LoginWorkflow extends SinglePageWorkflowBase
{
	@Override
	public String getId()
	{
		return KEY_WORKFLOW_LOGIN;
	}
	
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(LoginPaneFxController.class);
		
		executeWorkflowTask(CheckForNewUpdatesWorkflowTask.class);
		
		LoginBean loginBean;
		LoginMethod loginMethod = getData(LoginPaneFxController.class, "loginMethod");
		
		switch(loginMethod)
		{
			default:
			case USERNAME_AND_PASSWORD:
			{
				passData(LoginPaneFxController.class, LoginByUsernameAndPasswordWorkflowTask.class,
				         "username", "password");
				
				executeWorkflowTask(LoginByUsernameAndPasswordWorkflowTask.class);
				loginBean = getData(LoginByUsernameAndPasswordWorkflowTask.class, "loginBean");
				break;
			}
			case USERNAME_AND_FINGERPRINT:
			{
				passData(LoginPaneFxController.class, LoginByUsernameAndFingerprintWorkflowTask.class,
				         "username", "fingerPosition", "fingerprint");
				
				executeWorkflowTask(LoginByUsernameAndFingerprintWorkflowTask.class);
				loginBean = getData(LoginByUsernameAndFingerprintWorkflowTask.class, "loginBean");
				break;
			}
		}
		
		executeWorkflowTask(UserSessionCreationWorkflowTask.class);
		executeWorkflowTask(MenuRolesLookupWorkflowTask.class);
		
		setData(PrepareHomeBeanWorkflowTask.class, "userInfo", loginBean.getUserInfo());
		executeWorkflowTask(PrepareHomeBeanWorkflowTask.class);
		
		passData(MenuRolesLookupWorkflowTask.class, PrepareUserMenusWorkflowTask.class, "menusRoles");
		setData(PrepareUserMenusWorkflowTask.class, "userRoles",
                Arrays.asList(loginBean.getUserInfo().getOriginalStringRoles()));
		executeWorkflowTask(PrepareUserMenusWorkflowTask.class);
		
		setData(ScheduleRefreshTokenWorkflowTask.class, "userToken", loginBean.getUserToken());
		executeWorkflowTask(ScheduleRefreshTokenWorkflowTask.class);
		
		removeData(LoginPaneFxController.class, "username");
		removeData(LoginPaneFxController.class, "password");
		
		throw new Signal(SignalType.SUCCESS_LOGIN);
	}
}