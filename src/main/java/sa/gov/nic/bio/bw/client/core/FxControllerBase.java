package sa.gov.nic.bio.bw.client.core;

import sa.gov.nic.bio.bw.client.core.interfaces.ControllerResourcesLocator;
import sa.gov.nic.bio.bw.client.core.interfaces.ResourceBundleCollection;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class FxControllerBase implements ControllerResourcesLocator
{
	protected ResourceBundle labelsBundle;
	protected ResourceBundle errorsBundle;
	protected ResourceBundle messagesBundle;
	
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
	
	public void attachBundleResources(ResourceBundle labelsBundle, ResourceBundle errorsBundle,
	                                  ResourceBundle messagesBundle)
	{
		this.labelsBundle = labelsBundle;
		this.errorsBundle = errorsBundle;
		this.messagesBundle = messagesBundle;
	}
}