package sa.gov.nic.bio.bw.client.features.faceverification;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.searchbyfaceimage.ui.ToggleTitledPane;

import java.net.URL;
import java.util.Map;

public class ShowResultFxController extends WizardStepFxControllerBase
{
	@FXML private SplitPane splitPane;
	@FXML private HBox imagePane;
	@FXML private ImageView ivCenterImage;
	@FXML private VBox detailsPane;
	@FXML private Label lblBioId;
	@FXML private Label lblScore;
	@FXML private Label lblSamisId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblFamilyName;
	@FXML private Button btnCompareWithUploadedImage;
	@FXML private ScrollPane spCandidates;
	@FXML private ToggleTitledPane tpFinalImage;
	@FXML private HBox hbCandidatesContainer;
	@FXML private HBox hbCandidatesImages;
	@FXML private Button btnStartOver;
	
	private Image finalImage;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showResult.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}