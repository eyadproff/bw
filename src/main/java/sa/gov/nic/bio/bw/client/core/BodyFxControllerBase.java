package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.client.core.interfaces.BodyFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class BodyFxControllerBase implements BodyFxController
{
	protected CoreFxController coreFxController;
	protected ResourceBundle errorsBundle;
	protected ResourceBundle messagesBundle;
	protected Image appIcon;
	protected String taskId;
	protected Map<String, Object> inputData;
	
	@FXML protected NotificationPane notificationPane;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/gui.fxml");
	}
	
	@Override
	public ResourceBundleCollection getResourceBundleCollection()
	{
		String base = getClass().getPackage().getName().replace(".", "/");
		
		return new ResourceBundleCollection()
		{
			@Override
			public String getLabelsBundlePath()
			{
				return base + "/bundles/labels";
			}
			
			@Override
			public String getErrorsBundlePath()
			{
				return base + "/bundles/errors";
			}
			
			@Override
			public String getMessagesBundlePath()
			{
				return base + "/bundles/messages";
			}
		};
	}
	
	@Override
	public void attachCoreFxController(CoreFxController coreFxController)
	{
		this.coreFxController = coreFxController;
	}
	
	@Override
	public void attachInitialResources(ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon)
	{
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
		this.appIcon = appIcon;
	}
	
	@Override
	public void attachTaskId(String taskId)
	{
		this.taskId = taskId;
	}
	
	@Override
	public String getTaskId()
	{
		return taskId;
	}
	
	@Override
	public void attachInputData(Map<String, Object> inputData)
	{
		this.inputData = inputData;
	}
	
	@Override
	public Map<String, Object> getInputData()
	{
		return inputData;
	}
	
	@Override
	public void onReturnFromTask(String taskId, Map<String, Object> inputData)
	{
		this.taskId = taskId;
		this.inputData = inputData;
		
		String businessErrorCode = (String) inputData.get("businessErrorCode");
		
		Platform.runLater(() ->
        {
        	if(businessErrorCode != null) notificationPane.show(errorsBundle.getString(businessErrorCode));
        	onReturnFromTask();
        });
	}
}