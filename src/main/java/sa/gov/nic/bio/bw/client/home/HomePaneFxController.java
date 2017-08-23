package sa.gov.nic.bio.bw.client.home;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import java.util.*;

/**
 * Created by Fouad on 16-Jul-17.
 */
public class HomePaneFxController extends BodyFxControllerBase
{
	@FXML private Label lblLoginTimeText;
	@FXML private Label lblLoginTime;
	@FXML private Label lblLastSuccessLoginText;
	@FXML private Label lblLastSuccessLogin;
	@FXML private Label lblLastFailedLoginText;
	@FXML private Label lblLastFailedLogin;
	@FXML private Label lblFailedLoginCountText;
	@FXML private Label lblFailedLoginCount;
	@FXML private Label lblLastPasswordChangeTimeText;
	@FXML private Label lblLastPasswordChangeTime;
	@FXML private Label lblPasswordExpirationTimeText;
	@FXML private Label lblPasswordExpirationTime;
	
	@FXML
	private void initialize()
	{
	
	}
	
	@Override
	public void onControllerReady()
	{
		coreFxController.getHeaderPaneController().showRootPane();
		coreFxController.getFooterPaneController().hideRootPane();
		coreFxController.getMenuPaneController().showRootPane();
		
		LoginBean loginBean = (LoginBean) inputData.get("resultBean");
		Context.getUserData().setLoginBean(loginBean);
		LoginBean.UserInfo userInfo = loginBean.getUserInfo();
		
		String username = userInfo.getUserName();
		String operatorName = userInfo.getOperatorName() + " (" + userInfo.getOperatorId() + ")";
		String location = userInfo.getLocationName() + " (" + userInfo.getLocationId() + ")";
		
		// remove extra spaces in between and on edges
		username = username.trim().replaceAll("\\s+", " ");
		operatorName = operatorName.trim().replaceAll("\\s+", " ");
		location = location.trim().replaceAll("\\s+", " ");
		
		// localize numbers
		operatorName = AppUtils.replaceNumbers(operatorName, Locale.getDefault());
		location = AppUtils.replaceNumbers(location, Locale.getDefault());
		
		coreFxController.getHeaderPaneController().setUsername(username);
		coreFxController.getHeaderPaneController().setOperatorName(operatorName);
		coreFxController.getHeaderPaneController().setLocation(location);
		
		long loginTime = System.currentTimeMillis();
		long lastLogonTime = userInfo.getLastLogonTime();
		long lastFailedLoginTime = userInfo.getBadPasswordTime();
		int failedLoginCount = userInfo.getBadPasswordCount();
		long lastPasswordChangeTime = userInfo.getPasswordLastSet();
		long passwordExpirationTime = userInfo.getAccountExperiyDate();
		
		setLabelsText(loginTime, true, lblLoginTimeText, lblLoginTime);
		setLabelsText(lastLogonTime, true, lblLastSuccessLoginText, lblLastSuccessLogin);
		setLabelsText(lastFailedLoginTime, true, lblLastFailedLoginText, lblLastFailedLogin);
		setLabelsText(failedLoginCount, false, lblFailedLoginCountText, lblFailedLoginCount);
		setLabelsText(lastPasswordChangeTime, true, lblLastPasswordChangeTimeText, lblLastPasswordChangeTime);
		setLabelsText(passwordExpirationTime, true, lblPasswordExpirationTimeText, lblPasswordExpirationTime);
		
		List<String> userRoles = Arrays.asList(loginBean.getUserInfo().getOriginalStringRoles());
		Context.getUserData().addRoles(userRoles);
		
		List<String> allMenus = new ArrayList<>();
		
		String topMenus = Context.getConfigManager().getProperty("menus");
		if(topMenus == null)
		{
			String errorCode = "C004-00001";
			String message = errorsBundle.getString(errorCode);
			coreFxController.showErrorDialogAndWait(message, null);
			return;
		}
		
		String[] topMenusArray = topMenus.split(",");
		Map<String, Node> icons = new HashMap<>();
		
		for(String topMenu : topMenusArray)
		{
			String subMenus = Context.getConfigManager().getProperty("menu." + topMenu + ".submenus");
			if(subMenus == null)
			{
				String errorCode = "C004-00002";
				String message = String.format(errorsBundle.getString(errorCode), "menu." + topMenu + ".submenus");
				coreFxController.showErrorDialogAndWait(message, null);
				return;
			}
			
			String iconId = Context.getConfigManager().getProperty("menu." + topMenu + ".icon");
			if(iconId == null)
			{
				String errorCode = "C004-00003";
				String message = String.format(errorsBundle.getString(errorCode), "menu." + topMenu + ".icon");
				coreFxController.showErrorDialogAndWait(message, null);
				return;
			}
			
			try
			{
				FontAwesome.Glyph glyph = FontAwesome.Glyph.valueOf(iconId.toUpperCase());
				Glyph icon = AppUtils.createFontAwesomeIcon(glyph);
				icons.put("menu." + topMenu, icon);
			}
			catch(IllegalArgumentException e)
			{
				String errorCode = "C004-00004";
				String message = String.format(errorsBundle.getString(errorCode), iconId.toUpperCase());
				coreFxController.showErrorDialogAndWait(message, e);
				return;
			}
			
			String[] subMenusArray = subMenus.split(",");
			Arrays.stream(subMenusArray).map(s -> "menu." + topMenu.trim() + "." + s.trim()).forEach(allMenus::add);
		}
		
		List<String> menus = new ArrayList<>();
		
		for(String menuId : allMenus)
		{
			if(coreFxController.getBusinessData().userHasMenuAccess(userRoles, menuId))
			{
				menus.add(menuId);
			}
		}
		
		menus.sort(String.CASE_INSENSITIVE_ORDER);
		coreFxController.getMenuPaneController().setMenus(menus, icons);
		
		if(menus.size() == 0)
		{
			String message = errorsBundle.getString("B004-00000");
			showWarningNotification(message);
		}
	}
	
	private void setLabelsText(long value, boolean isDate, Label textLabel, Label valueLabel)
	{
		if(value > 0)
		{
			String sDateTime = isDate ? AppUtils.formatHijriGregorianDateTime(value) :
										AppUtils.replaceNumbers(value, Locale.getDefault());
			valueLabel.setText(sDateTime);
		}
		else
		{
			textLabel.setVisible(false);
			textLabel.setManaged(false);
			textLabel.setPadding(new Insets(0));
			valueLabel.setVisible(false);
			valueLabel.setManaged(false);
			valueLabel.setPadding(new Insets(0));
		}
	}
}