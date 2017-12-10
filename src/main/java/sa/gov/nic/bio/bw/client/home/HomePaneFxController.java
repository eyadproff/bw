package sa.gov.nic.bio.bw.client.home;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.MenuItem;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.login.webservice.LoginBean;

import javax.naming.ConfigurationException;
import java.io.ByteArrayInputStream;
import java.time.DateTimeException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fouad on 16-Jul-17.
 */
public class HomePaneFxController extends BodyFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(HomePaneFxController.class.getName());
	private static final String AVATAR_PLACEHOLDER_IMAGE = "sa/gov/nic/bio/bw/client/core/images/avatar_placeholder.jpg";
	
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
	private void initialize(){}
	
	@Override
	public void onControllerReady()
	{
		coreFxController.getHeaderPaneController().showRootPane();
		coreFxController.getFooterPaneController().hideRootPane();
		coreFxController.getMenuPaneController().showRootPane();
		
		LoginBean loginBean = (LoginBean) inputData.get("resultBean");
		Context.getUserData().setLoginBean(loginBean);
		LoginBean.UserInfo userInfo = loginBean.getUserInfo();
		
		String userToken = loginBean.getUserToken();
		coreFxController.scheduleRefreshToken(userToken);
		
		String username = userInfo.getUserName();
		String operatorName = userInfo.getOperatorName() + " (" + userInfo.getOperatorId() + ")";
		String location = userInfo.getLocationName() + " (" + userInfo.getLocationId() + ")";
		
		String encodedFaceImage = userInfo.getFaceImage();
		byte[] faceImageByteArray = null;
		Image image = null;
		
		if(encodedFaceImage != null && !encodedFaceImage.isEmpty())
		{
			try
			{
				faceImageByteArray = Base64.getDecoder().decode(encodedFaceImage);
			}
			catch(Exception e)
			{
				LOGGER.log(Level.WARNING, "Failed to decode the Base64 string encodedFaceImage = " + encodedFaceImage, e);
			}
			
			if(faceImageByteArray != null)
			{
				try
				{
					image = new Image(new ByteArrayInputStream(faceImageByteArray));
				}
				catch(Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed to load the avatar image!", e);
				}
				
				if(image != null) coreFxController.getHeaderPaneController().setAvatarImage(image);
			}
		}
		
		if(image == null) coreFxController.getHeaderPaneController().setAvatarImage(new Image(AVATAR_PLACEHOLDER_IMAGE));
		
		// remove extra spaces in between and on edges
		username = username.trim().replaceAll("\\s+", " ");
		operatorName = operatorName.trim().replaceAll("\\s+", " ");
		location = location.trim().replaceAll("\\s+", " ");
		
		// localize numbers
		operatorName = AppUtils.replaceNumbersOnly(operatorName, Locale.getDefault());
		location = AppUtils.replaceNumbersOnly(location, Locale.getDefault());
		
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
		
		List<MenuItem> allMenus = new ArrayList<>();
		
		List<String> menuFiles;
		try
		{
			menuFiles = AppUtils.listResourceFiles(getClass().getProtectionDomain(), "^.*/menu.properties$");
		}
		catch(Exception e)
		{
			String errorCode = "C004-00001";
			String message = errorsBundle.getString(errorCode);
			coreFxController.showErrorDialogAndWait(message, e);
			return;
		}
		
		UTF8Control utf8Control = new UTF8Control();
		Map<String, MenuItem> icons = new HashMap<>();
		
		menuFiles.forEach(menuFile ->
		{
			ResourceBundle rb = ResourceBundle.getBundle(menuFile.substring(0, menuFile.lastIndexOf('.')), Locale.getDefault(), utf8Control);
			MenuItem menuItem = new MenuItem();
			allMenus.add(menuItem);
			
			Set<String> keys = rb.keySet();
			if(keys.size() > 2)
			{
				String errorCode = "C004-00002";
				String message = String.format(errorsBundle.getString(errorCode), menuFile);
				coreFxController.showErrorDialogAndWait(message, new ConfigurationException("The menu properties file (" + menuFile + ") has more than 2 lines!"));
				return;
			}
			
			keys.forEach(key ->
			{
				String value = rb.getString(key);
				if(key.equals("order"))
				{
					int order = Integer.parseInt(value);
					menuItem.setOrder(order);
				}
				else
				{
					menuItem.setMenuId(key);
					menuItem.setLabel(value);
					
					String topMenu = key.substring(0, key.lastIndexOf('.'));
					
					if(!icons.containsKey(topMenu))
					{
						String label = coreFxController.getTopMenusBundle().getString(topMenu);
						String icon = coreFxController.getTopMenusBundle().getString(topMenu + ".icon");
						int order = Integer.parseInt(coreFxController.getTopMenusBundle().getString(topMenu + ".order"));
						
						MenuItem topMenuItem = new MenuItem();
						topMenuItem.setMenuId(topMenu);
						topMenuItem.setLabel(label);
						topMenuItem.setIconId(icon);
						topMenuItem.setOrder(order);
						
						icons.put(topMenu, topMenuItem);
					}
				}
			});
		});
		
		List<MenuItem> menus = new ArrayList<>();
		
		for(MenuItem menuItem : allMenus)
		{
			Set<String> menuRoles = loginBean.getMenuRoles().get(menuItem.getMenuId());
			
			if(menuRoles != null && !Collections.disjoint(userRoles, menuRoles))
			{
				menus.add(menuItem);
			}
		}
		
		coreFxController.getMenuPaneController().setMenus(menus, icons);
		
		if(menus.size() == 0)
		{
			String message = errorsBundle.getString("B004-00000");
			showWarningNotification(message);
		}
		
		coreFxController.startIdleMonitor();
	}
	
	private void setLabelsText(long value, boolean isDate, Label textLabel, Label valueLabel)
	{
		boolean hideIt = false;
		
		if(value > 0)
		{
			try
			{
				String sDateTime = isDate ? AppUtils.formatHijriGregorianDateTime(value) :
						AppUtils.replaceNumbersOnly(String.valueOf(value), Locale.getDefault());
				valueLabel.setText(sDateTime);
			}
			catch(DateTimeException e) // date out of range?
			{
				hideIt = true;
			}
		}
		else hideIt = true;
		
		if(hideIt)
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