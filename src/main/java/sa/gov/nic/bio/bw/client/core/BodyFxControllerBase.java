package sa.gov.nic.bio.bw.client.core;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.NotificationPane;
import sa.gov.nic.bio.bw.client.core.interfaces.BodyFxController;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class BodyFxControllerBase implements BodyFxController
{
	protected CoreFxController coreFxController;
	protected ResourceBundle errorsBundle;
	protected ResourceBundle messagesBundle;
	protected Image appIcon;
	protected Map<String, Object> inputData;
	
	@FXML protected NotificationPane notificationPane;
	
	private Image successIcon = new Image(AppUtils.getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/success.png"));
	private Image warningIcon = new Image(AppUtils.getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/warning.png"));
	private Image errorIcon = new Image(AppUtils.getResourceAsStream("sa/gov/nic/bio/bw/client/core/images/error.png"));
	
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
	public void onReturnFromTask(Map<String, Object> inputData)
	{
		this.inputData = inputData;
		Platform.runLater(this::onReturnFromTask);
	}
	
	public void onReturnFromTask(){}
	
	public void hideNotification()
	{
		notificationPane.hide();
	}
	
	public void showNotification(String message, Image icon)
	{
		Platform.runLater(() ->
        {
            notificationPane.setGraphic(new ImageView(icon));
            notificationPane.show(message);
        });
	}
	
	public void showSuccessNotification(String message)
	{
		showNotification(message, successIcon);
	}
	
	public void showWarningNotification(String message)
	{
		showNotification(message, warningIcon);
	}
	
	public void showErrorNotification(String message)
	{
		showNotification(message, errorIcon);
	}
}