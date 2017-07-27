package sa.gov.nic.bio.bw.client.preloader;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Fouad on 10-Jul-17.
 */
public class SplashScreenFxController
{
	private static final Logger LOGGER = LogManager.getLogger();
	
	static final String FXML_FILE = "sa/gov/nic/bio/bw/client/preloader/fxml/splash_screen.fxml";
	static final String APP_ICON_FILE = "sa/gov/nic/bio/bw/client/preloader/images/app_icon.png";
	static final String RB_LABELS_FILE = "sa/gov/nic/bio/bw/client/preloader/bundles/labels";
	static final String RB_ERRORS_FILE = "sa/gov/nic/bio/bw/client/preloader/bundles/errors";
	
	@FXML private ProgressBar progressBar;
	@FXML private Label txtProgress;
	
	void setProgress(double progress)
	{
		Timeline timeline = new Timeline();
		
		KeyValue keyValue = new KeyValue(progressBar.progressProperty(), progress);
		KeyFrame keyFrame = new KeyFrame(new Duration(500), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		
		timeline.play();
	}
	
	void setProgressMessage(String message)
	{
		txtProgress.setText(message);
	}
}