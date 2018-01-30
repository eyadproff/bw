package sa.gov.nic.bio.bw.client.core.interfaces;

/**
 * Created by Fouad on 17-Jul-17.
 */
public interface BodyFxController extends FormTaskController, AttachableController, ControllerResourcesLocator
{
	void onControllerReady();
	void hideNotification();
	void showSuccessNotification(String message);
	void showWarningNotification(String message);
	void showErrorNotification(String message);
}