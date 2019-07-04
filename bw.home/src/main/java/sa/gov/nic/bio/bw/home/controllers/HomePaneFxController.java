package sa.gov.nic.bio.bw.home.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bcl.utils.BclUtils;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.MenuItem;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.home.utils.HomeErrorCodes;
import sa.gov.nic.bio.bw.workflow.commons.beans.HomeBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FxmlFile("home.fxml")
public class HomePaneFxController extends ContentFxControllerBase
{
	@Input private boolean showErrorOnHome;
	
	@FXML private Pane paneErrorState;
	@FXML private Pane paneLoginBox;
	@FXML private Label lblPlaceholder;
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
		if(showErrorOnHome)
		{
			GuiUtils.showNode(lblPlaceholder, false);
			GuiUtils.showNode(paneErrorState, true);
		}
		else
		{
			Context.getCoreFxController().getFooterPaneController().hideRegion();
			Context.getCoreFxController().getHeaderPaneController().showRegion();
			Context.getCoreFxController().getMenuPaneController().showRegion();
			Context.getCoreFxController().getDeviceManagerGadgetPaneController().showRegion();
			Context.getCoreFxController().startIdleMonitor();
			
			UserSession userSession = Context.getUserSession();
			if(userSession == null) return;
			HomeBean homeBean = (HomeBean) userSession.getAttribute("homeBean");
			
			Image facePhoto = homeBean.getFacePhoto();
			if(facePhoto != null) Context.getCoreFxController().getHeaderPaneController().setAvatarImage(facePhoto);
			
			@SuppressWarnings("unchecked")
			List<MenuItem> menus = (List<MenuItem>) Context.getUserSession().getAttribute("menus");
			Map<String, MenuItem> topMenus = Context.getTopMenus();
			Context.getCoreFxController().getMenuPaneController().setMenus(menus, topMenus);
			
			@SuppressWarnings("unchecked")
			Set<Device> devices = (Set<Device>) Context.getUserSession().getAttribute("devices");
			Context.getCoreFxController().getDeviceManagerGadgetPaneController().showDeviceControls(devices);
			
			if(menus.isEmpty())
			{
				Context.getCoreFxController().onLogin(false);
				
				String errorCode = HomeErrorCodes.N004_00001.getCode();
				reportNegativeTaskResponse(errorCode, null, null);
			}
			else
			{
				Context.getCoreFxController().onLogin(true);
				
				DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
						Context.getCoreFxController().getDeviceManagerGadgetPaneController();
				if(!deviceManagerGadgetPaneController.isDevicesRunnerRunning())
				{
					Task<Boolean> task = new Task<>()
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
			
			// The following causes ui lagging (progress indicator) at first login if we take it out of runLater()
			Platform.runLater(() ->
			{
			    Context.getCoreFxController().getHeaderPaneController().setUsername(homeBean.getUsername());
			    Context.getCoreFxController().getHeaderPaneController().setOperatorName(homeBean.getOperatorName());
			    Context.getCoreFxController().getHeaderPaneController().setLocation(homeBean.getLocation());
				
				setLabelsText(homeBean.getsLoginTime(), lblLoginTimeText, lblLoginTime);
				setLabelsText(homeBean.getsLastLogonTime(), lblLastSuccessLoginText, lblLastSuccessLogin);
				setLabelsText(homeBean.getsLastFailedLoginTime(), lblLastFailedLoginText, lblLastFailedLogin);
				setLabelsText(homeBean.getsFailedLoginCount(), lblFailedLoginCountText, lblFailedLoginCount);
				setLabelsText(homeBean.getsLastPasswordChangeTime(), lblLastPasswordChangeTimeText,
				              lblLastPasswordChangeTime);
				setLabelsText(homeBean.getsPasswordExpirationTime(), lblPasswordExpirationTimeText,
				              lblPasswordExpirationTime);
				
				GuiUtils.showNode(lblPlaceholder, false);
				GuiUtils.showNode(paneLoginBox, true);
			});
		}
	}
	
	private static void setLabelsText(String text, Label textLabel, Label valueLabel)
	{
		if(text != null) valueLabel.setText(text);
		else
		{
			GuiUtils.showNode(textLabel, false);
			textLabel.setPadding(new Insets(0));
			GuiUtils.showNode(valueLabel, false);
			valueLabel.setPadding(new Insets(0));
		}
	}
}