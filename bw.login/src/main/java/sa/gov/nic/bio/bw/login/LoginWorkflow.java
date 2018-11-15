package sa.gov.nic.bio.bw.login;

import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SignalType;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.features.commons.beans.LoginBean;
import sa.gov.nic.bio.bw.login.controllers.LoginPaneFxController;
import sa.gov.nic.bio.bw.login.controllers.LoginPaneFxController.LoginMethod;
import sa.gov.nic.bio.bw.login.workflow.CheckForNewUpdatesWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.LoginByUsernameAndFingerprintWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.LoginByUsernameAndPasswordWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.MenuRolesLookupWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.PrepareHomeBeanWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.PrepareUserMenusWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.ScheduleRefreshTokenWorkflowTask;
import sa.gov.nic.bio.bw.login.workflow.UserSessionCreationWorkflowTask;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

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
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
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
		
		removeData(LoginPaneFxController.class, "username");
		removeData(LoginPaneFxController.class, "password");
		
		throw new Signal(SignalType.SUCCESS_LOGIN);
	}
}