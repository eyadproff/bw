package sa.gov.nic.bio.bw.client.core.interfaces;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.client.core.CoreFxController;

import java.util.ResourceBundle;

/**
 * Created by Fouad on 17-Jul-17.
 */
public interface AttachableController
{
	void attachCoreFxController(CoreFxController coreFxController);
	void attachInitialResources(ResourceBundle errorsBundle, ResourceBundle messagesBundle, Image appIcon);
}