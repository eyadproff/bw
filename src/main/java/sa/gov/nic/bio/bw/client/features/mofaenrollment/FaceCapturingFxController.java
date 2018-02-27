package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.client.core.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class FaceCapturingFxController extends WizardStepFxControllerBase
{
	@FXML private AutoScalingStackPane subScenePane;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/faceCapturing.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
	}
	
	@Override
	public void onControllerReady()
	{
		Group shape3D = null;
		try
		{
			shape3D = FXMLLoader.load(getClass().getResource("fxml/face3DModel.fxml"), stringsBundle);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		AutoScalingStackPane shapePane = new AutoScalingStackPane(shape3D);
		
		SubScene subScene = new SubScene(shapePane, 100.0, 100.0, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.TRANSPARENT);
		subScene.setCamera(new PerspectiveCamera());
		
		subScenePane.getChildren().setAll(subScene);
		
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
	
	}
}