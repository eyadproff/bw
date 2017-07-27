package sa.gov.nic.bio.bw.client.core.interfaces;

import java.net.URL;

/**
 * Created by Fouad on 17-Jul-17.
 */
public interface BodyFxController extends FormTaskController, AttachableController
{
	URL getFxmlLocation();
	ResourceBundleCollection getResourceBundleCollection();
	void onControllerReady();
}