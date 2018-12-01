package sa.gov.nic.bio.bw.core.interfaces;

/**
 * Every JavaFX controller that implements this interface will be able to show a notification on its region.
 *
 * @author Fouad Almalki
 */
public interface NotificationController
{
	/**
	 * Hide the notification bar.
	 */
	void hideNotification();
	
	/**
	 * Show a notification message with a success icon.
	 *
	 * @param message the message to show
	 */
	void showSuccessNotification(String message);
	
	/**
	 * Show a notification message with a warning icon.
	 *
	 * @param message the message to show
	 */
	void showWarningNotification(String message);
	
	/**
	 * Show a notification message with an error icon.
	 *
	 * @param message the message to show
	 */
	void showErrorNotification(String message);
}