package sa.gov.nic.bio.bw.client.home;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.beans.MenuItem;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.Device;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.utils.UTF8Control;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.home.utils.HomeErrorCodes;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePaneFxController extends BodyFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(HomePaneFxController.class.getName());
	
	@FXML private VBox paneError;
	@FXML private VBox paneLoginTimestampBox;
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
	
	@Override
	protected void onAttachedToScene()
	{
		Context.getCoreFxController().getHeaderPaneController().showRegion();
		Context.getCoreFxController().getFooterPaneController().hideRegion();
		Context.getCoreFxController().getMenuPaneController().showRegion();
		
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.showRegion();
		
		if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			Task<Boolean> task = new Task<Boolean>()
			{
				@Override
				protected Boolean call()
				{
					return BclUtils.isLocalhostPortListening(Context.getBioKitManager().getWebsocketPort());
				}
			};
			task.setOnSucceeded(event ->
			{
			    Boolean listening = task.getValue();
			
			    if(listening != null && listening)
			    {
			        deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			    }
			});
			Context.getExecutorService().submit(task);
		}
	}
	
	private void setLabelsText(long value, boolean isDate, Label textLabel, Label valueLabel)
	{
		boolean hideIt = false;
		
		if(value > 0)
		{
			try
			{
				String sDateTime = isDate ? AppUtils.formatHijriGregorianDateTime(value) :
											AppUtils.localizeNumbers(String.valueOf(value));
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
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		String errorCode = (String) uiInputData.get(Workflow.KEY_ERROR_CODE);
		
		if(errorCode != null)
		{
			GuiUtils.showNode(paneLoginTimestampBox, false);
			GuiUtils.showNode(paneError, true);
			
			String[] errorDetails = (String[]) uiInputData.get(Workflow.KEY_ERROR_DETAILS);
			Exception exception = (Exception) uiInputData.get(Workflow.KEY_EXCEPTION);
			
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		}
		else firstLoadAfterLogin();
	}
	
	private void firstLoadAfterLogin()
	{
		UserSession userSession = Context.getUserSession();
		UserInfo userInfo = (UserInfo) userSession.getAttribute("userInfo");
		String userToken = (String) userSession.getAttribute("userToken");
		
		Context.getWebserviceManager().scheduleRefreshToken(userToken);
		
		String username = userInfo.getUserName();
		String operatorName = userInfo.getOperatorName();
		long operatorId = userInfo.getOperatorId();
		String locationName = userInfo.getLocationName();
		String locationId = userInfo.getLocationId();
		
		String operator;
		String location;
		
		if(operatorName == null) operatorName = "-";
		if(locationName == null) locationName = "-";
		
		operator = operatorName + (operatorId > 0 ? " (" + userInfo.getOperatorId() + ")" : "");
		location = locationName + (locationId != null && !locationId.isEmpty() &&
												!locationId.equals("0") ? " (" + userInfo.getLocationId() + ")" : "");
		
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
				}
				catch(Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed to load the avatar image!", e);
				}
				
				if(image != null) Context.getCoreFxController().getHeaderPaneController().setAvatarImage(image);
			}
		}
		
		// remove extra spaces in between and on edges
		username = username.trim().replaceAll("\\s+", " ");
		operator = operator.trim().replaceAll("\\s+", " ");
		location = location.trim().replaceAll("\\s+", " ");
		
		// localize numbers
		operator = AppUtils.localizeNumbers(operator, Locale.getDefault(), false);
		location = AppUtils.localizeNumbers(location, Locale.getDefault(), false);
		
		Context.getCoreFxController().getHeaderPaneController().setUsername(username);
		Context.getCoreFxController().getHeaderPaneController().setOperatorName(operator);
		Context.getCoreFxController().getHeaderPaneController().setLocation(location);
		
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
			String errorCode = HomeErrorCodes.C004_00001.getCode();
			String[] errorDetails = {"Failed to load menu.properties files!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
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
			
			    switch(key)
			    {
				    case "menu.id":
				    {
					    menuItem.setMenuId(value);
					
					    String topMenu = value.substring(0, value.lastIndexOf('.'));
					
					    if(!topMenus.containsKey(topMenu))
					    {
						    String label = Context.getCoreFxController().getResourceBundle().getString(topMenu);
						    String icon = Context.getConfigManager().getProperty(topMenu + ".icon");
						    int order = Integer.parseInt(Context.getConfigManager().getProperty(topMenu + ".order"));
						
						    MenuItem topMenuItem = new MenuItem();
						    topMenuItem.setMenuId(topMenu);
						    topMenuItem.setLabel(label);
						    topMenuItem.setIconId(icon);
						    topMenuItem.setOrder(order);
						
						    topMenus.put(topMenu, topMenuItem);
					    }
					    break;
				    }
				    case "menu.label":
				    {
					    menuItem.setLabel(value);
					    break;
				    }
				    case "menu.lines":
				    {
					    int lines = Integer.parseInt(value);
					    menuItem.setLines(lines);
					    break;
				    }
				    case "menu.order":
				    {
					    int order = Integer.parseInt(value);
					    menuItem.setOrder(order);
					    break;
				    }
				    case "menu.workflow.class":
				    {
					    try
					    {
						    Class<?> workflowClass = Class.forName(value);
						    menuItem.setWorkflowClass(workflowClass);
					    }
					    catch(ClassNotFoundException | NoClassDefFoundError e)
					    {
						    String errorCode = HomeErrorCodes.C004_00002.getCode();
						    String[] errorDetails = {"The menu workflow class (" + value + ") is not found!"};
						    Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
					    }
					    break;
				    }
				    case "menu.devices":
				    {
				    	String[] arrDevices = value.split(",");
					    Set<Device> devices = new HashSet<>();
				    	
					    for(String sDevice : arrDevices)
					    {
						    Device device = Device.byName(sDevice.trim());
						    if(device != null) devices.add(device);
					    }
					    
					    menuItem.setDevices(devices);
					    
					    break;
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
		
		Context.getCoreFxController().getMenuPaneController().setMenus(menus, topMenus);
		
		Set<Device> devices = new HashSet<>();
		for(MenuItem menuItem : menus)
		{
			Set<Device> menuDevices = menuItem.getDevices();
			if(menuDevices != null) devices.addAll(menuDevices);
		}
		
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().showDeviceControls(devices);
		
		if(menus.size() == 0)
		{
			String errorCode = HomeErrorCodes.N004_00001.getCode();
			
			String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
			String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
			
			LOGGER.info(logErrorMessage);
			showWarningNotification(guiErrorMessage);
		}
		
		Context.getCoreFxController().startIdleMonitor();
	}
}