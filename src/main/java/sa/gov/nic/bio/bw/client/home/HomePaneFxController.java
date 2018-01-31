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
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fouad on 16-Jul-17.
 */
public class HomePaneFxController extends BodyFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(HomePaneFxController.class.getName());
	private static final String AVATAR_PLACEHOLDER_IMAGE =
														"sa/gov/nic/bio/bw/client/core/images/avatar_placeholder.jpg";
	
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
	protected void initialize(){}
	
	@Override
	public void onControllerReady()
	{
		coreFxController.getHeaderPaneController().showRegion();
		coreFxController.getFooterPaneController().hideRegion();
		coreFxController.getMenuPaneController().showRegion();
		
		UserSession userSession = Context.getUserSession();
		UserInfo userInfo = (UserInfo) userSession.getAttribute("userInfo");
		String userToken = (String) userSession.getAttribute("userToken");
		
		Context.getWebserviceManager().scheduleRefreshToken(userToken);
		
		String username = userInfo.getUserName();
		
		// we normalize because the backend characters are not the standard ones
		String operatorName = Normalizer.normalize(userInfo.getOperatorName(), Normalizer.Form.NFKC) + " (" +
												   userInfo.getOperatorId() + ")";
		String location = Normalizer.normalize(userInfo.getLocationName(), Normalizer.Form.NFKC) + " (" +
											   userInfo.getLocationId() + ")";
		
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
				LOGGER.log(Level.WARNING, "Failed to decode the Base64 string encodedFaceImage = " +
						   encodedFaceImage, e);
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
		
		if(image == null) coreFxController.getHeaderPaneController()
										  .setAvatarImage(new Image(AVATAR_PLACEHOLDER_IMAGE));
		
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
			menuFiles = AppUtils.listResourceFiles(getClass().getProtectionDomain(), "^.*/menu.properties$",
			                                       true, Context.getRuntimeEnvironment());
		}
		catch(Exception e)
		{
			String errorCode = "C004-00001";
			String guiErrorMessage = coreFxController.getErrorsBundle().getString(errorCode);
			String logErrorMessage = coreFxController.getErrorsBundle().getString(errorCode + ".internal");
			LOGGER.log(Level.SEVERE, logErrorMessage, e);
			coreFxController.showErrorDialogAndWait(guiErrorMessage, e);
			return;
		}
		
		UTF8Control utf8Control = new UTF8Control();
		Map<String, MenuItem> topMenus = new HashMap<>();
		
		menuFiles.forEach(menuFile ->
		{
			LOGGER.fine("menuFile = " + menuFile);
			
			ResourceBundle rb = ResourceBundle.getBundle(menuFile, Locale.getDefault(), utf8Control);
			
			MenuItem menuItem = new MenuItem();
			allMenus.add(menuItem);
			
			Set<String> keys = rb.keySet();
			
			keys.forEach(key ->
			{
				String value = rb.getString(key);
				
				if(key.equals("menu.id"))
				{
					menuItem.setMenuId(value);
					
					String topMenu = value.substring(0, value.lastIndexOf('.'));
					
					if(!topMenus.containsKey(topMenu))
					{
						String label = coreFxController.getTopMenusBundle().getString(topMenu);
						String icon = coreFxController.getTopMenusBundle().getString(topMenu + ".icon");
						int order = Integer.parseInt(coreFxController.getTopMenusBundle().getString(topMenu +
								                                                                    ".order"));
						
						MenuItem topMenuItem = new MenuItem();
						topMenuItem.setMenuId(topMenu);
						topMenuItem.setLabel(label);
						topMenuItem.setIconId(icon);
						topMenuItem.setOrder(order);
						
						topMenus.put(topMenu, topMenuItem);
					}
				}
				else if(key.equals("menu.label"))
				{
					menuItem.setLabel(value);
				}
				else if(key.equals("menu.order"))
				{
					int order = Integer.parseInt(value);
					menuItem.setOrder(order);
				}
				else if(key.equals("menu.workflow.class"))
				{
					try
					{
						Class<?> workflowClass = Class.forName(value);
						menuItem.setWorkflowClass(workflowClass);
					}
					catch(ClassNotFoundException e)
					{
						String errorCode = "C004-00002";
						String guiErrorMessage = String.format(coreFxController.getErrorsBundle().getString(errorCode), value);
						String logErrorMessage = String.format(coreFxController.getErrorsBundle().getString(errorCode + ".internal"),
						                                       value);
						LOGGER.severe(logErrorMessage);
						coreFxController.showErrorDialogAndWait(guiErrorMessage, null);
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
		
		coreFxController.getMenuPaneController().setMenus(menus, topMenus);
		
		if(menus.size() == 0)
		{
			String message = coreFxController.getErrorsBundle().getString("B004-00000");
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
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> dataMap)
	{
	
	}
}