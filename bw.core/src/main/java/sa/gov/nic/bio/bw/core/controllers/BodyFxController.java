package sa.gov.nic.bio.bw.core.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.NotificationPane;

public class BodyFxController extends FxControllerBase
{
	@FXML private NotificationPane notificationPane;
	@FXML private VBox wizardPaneContainer;
	@FXML private BorderPane bodyPane;
	@FXML private StackPane paneTransitionOverlay;
	
	public NotificationPane getNotificationPane(){return notificationPane;}
	public VBox getWizardPaneContainer(){return wizardPaneContainer;}
	public BorderPane getBodyPane(){return bodyPane;}
	public StackPane getPaneTransitionOverlay(){return paneTransitionOverlay;}
}