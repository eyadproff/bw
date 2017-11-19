package sa.gov.nic.bio.bw.client.home;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
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
		
		List<String> allMenus = new ArrayList<>();
		
		String topMenus = Context.getConfigManager().getProperty("menus");
		if(topMenus == null)
		{
			String errorCode = "C004-00001";
			String message = errorsBundle.getString(errorCode);
			coreFxController.showErrorDialogAndWait(message, new ConfigurationException("Top menus are not configured!"));
			return;
		}
		
		String[] topMenusArray = topMenus.split("[,\\s]+");
		Map<String, Node> icons = new HashMap<>();
		
		for(String topMenu : topMenusArray)
		{
			String subMenus = Context.getConfigManager().getProperty("menu." + topMenu + ".submenus");
			if(subMenus == null)
			{
				String errorCode = "C004-00002";
				String message = String.format(errorsBundle.getString(errorCode), "menu." + topMenu + ".submenus");
				coreFxController.showErrorDialogAndWait(message, new ConfigurationException("The subMenus (menu." + topMenu + ".submenus) are not configured!"));
				return;
			}
			
			String iconId = Context.getConfigManager().getProperty("menu." + topMenu + ".icon");
			if(iconId == null)
			{
				String errorCode = "C004-00003";
				String message = String.format(errorsBundle.getString(errorCode), "menu." + topMenu + ".icon");
				coreFxController.showErrorDialogAndWait(message, new ConfigurationException("The icon (menu." + topMenu + ".icon) is not configured!"));
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
			Arrays.stream(subMenusArray).map(s -> "menu." + topMenu + "." + s.trim()).forEach(allMenus::add);
		}
		
		List<String> menus = new ArrayList<>();
		
		for(String menuId : allMenus)
		{
			Set<String> menuRoles = loginBean.getMenuRoles().get(menuId);
			
			if(menuRoles != null && !Collections.disjoint(userRoles, menuRoles))
			{
				menus.add(menuId);
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