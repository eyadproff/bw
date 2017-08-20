package sa.gov.nic.bio.bw.client.core.interfaces;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.client.core.CoreFxController;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Fouad on 17-Jul-17.
 */
public interface FormTaskController
{
	void attachInputData(Map<String, Object> inputData);
	Map<String, Object> getInputData();
	void onReturnFromTask(Map<String, Object> inputData); // background thread
	void onReturnFromTask(); // UI thread
}