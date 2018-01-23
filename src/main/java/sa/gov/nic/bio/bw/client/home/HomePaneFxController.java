package sa.gov.nic.bio.bw.client.home;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.MenuItem;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.RuntimeEnvironment;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.Normalizer;
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
		
		UserSession userSession = Context.getUserSession();
		UserInfo userInfo = (UserInfo) userSession.getAttribute("userInfo");
		String userToken = (String) userSession.getAttribute("userToken");
		
		coreFxController.scheduleRefreshToken(userToken);
		
		String username = userInfo.getUserName();
		String operatorName = Normalizer.normalize(userInfo.getOperatorName(), Normalizer.Form.NFKC) + " (" + userInfo.getOperatorId() + ")"; // we normalize because the backend characters are not the standard ones
		String location = Normalizer.normalize(userInfo.getLocationName(), Normalizer.Form.NFKC) + " (" + userInfo.getLocationId() + ")";
		
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
					BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
					try {
						ImageIO.write(bImage, "png", new File("C:/bio/test.png"));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					
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
		
		List<String> userRoles = Arrays.asList(userInfo.getOriginalStringRoles());
		userSession.setAttribute("userRoles", userRoles);
		
		List<MenuItem> allMenus = new ArrayList<>();
		
		List<String> menuFiles;
		try
		{
			menuFiles = AppUtils.listResourceFiles(getClass().getProtectionDomain(), "^.*/menu.properties$", Context.getRuntimeEnvironment());
		}
		catch(Exception e)
		{
			String errorCode = "C004-00001";
			String message = errorsBundle.getString(errorCode);
			LOGGER.log(Level.SEVERE, "Failed to load the menu properties files!", e);
			coreFxController.showErrorDialogAndWait(message, e);
			return;
		}
		
		UTF8Control utf8Control = new UTF8Control();
		Map<String, MenuItem> icons = new HashMap<>();
		
		menuFiles.forEach(menuFile ->
		{
			LOGGER.fine("menuFile = " + menuFile);
			
			ResourceBundle rb;
			
			if(Context.getRuntimeEnvironment() == RuntimeEnvironment.DEV)
			{
				File file = new File(menuFile.substring(0, menuFile.lastIndexOf("/")));
				URL[] urls = null;
				try
				{
					urls = new URL[]{file.toURI().toURL()};
				}
				catch(MalformedURLException e)
				{
					e.printStackTrace();
					return;
				}
				
				ClassLoader loader = new URLClassLoader(urls);
				rb = ResourceBundle.getBundle(menuFile.substring(menuFile.lastIndexOf("/") + 1, menuFile.lastIndexOf('.')), Locale.getDefault(), loader, utf8Control);
			}
			else
			{
				rb = ResourceBundle.getBundle(menuFile.substring(0, menuFile.lastIndexOf('.')), Locale.getDefault(), utf8Control);
			}
			
			
			MenuItem menuItem = new MenuItem();
			allMenus.add(menuItem);
			
			Set<String> keys = rb.keySet();
			if(keys.size() > 2)
			{
				String errorCode = "C004-00002";
				String message = String.format(errorsBundle.getString(errorCode), menuFile);
				LOGGER.severe("The menu properties file (" + menuFile + ") has more than 2 lines!");
				coreFxController.showErrorDialogAndWait(message, null);
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
		
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> menusRoles = (Map<String, Set<String>>) userSession.getAttribute("menusRoles");
		
		for(MenuItem menuItem : allMenus)
		{
			Set<String> menuRoles = menusRoles.get(menuItem.getMenuId());
			
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
			
			GuiUtils.showNode(textLabel, false);
			textLabel.setPadding(new Insets(0));
			GuiUtils.showNode(valueLabel, false);
			valueLabel.setPadding(new Insets(0));
		}
	}
}