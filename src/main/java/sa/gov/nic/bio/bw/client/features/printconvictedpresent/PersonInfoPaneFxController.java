package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.PersonInfo;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

public class PersonInfoPaneFxController extends WizardStepFxControllerBase
{
	@FXML private HBox imagePane;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Button btnNext;
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/personInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnNext.setOnAction(actionEvent -> goNext());
		btnStartOver.setOnAction(actionEvent -> startOver());
	}
	
	@Override
	protected void onAttachedToScene()
	{
		imagePane.maxWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty());
		imagePane.maxHeightProperty().bind(Context.getCoreFxController().getBodyPane().heightProperty());
		ivPersonPhoto.fitWidthProperty().bind(imagePane.widthProperty().divide(1.8));
		ivPersonPhoto.fitHeightProperty().bind(imagePane.heightProperty().divide(1.8));
		
		ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) ->
		{
			if(!newValue) // on un-maximize (workaround to fix JavaFX bug)
			{
				Platform.runLater(() ->
				{
				    imagePane.autosize();
				    Context.getCoreFxController().getBodyPane().autosize();
				});
			}
		};
		Context.getCoreFxController().getStage().maximizedProperty().addListener(changeListener);
		imagePane.sceneProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null) Context.getCoreFxController().getStage().maximizedProperty()
		            .removeListener(changeListener);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			FingerprintInquiryStatusResult result = (FingerprintInquiryStatusResult)
					uiInputData.get(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT_RESULT);
			PersonInfo personInfo = result.getPersonInfo().values().iterator().next();
			String faceImageBase64 = personInfo.getFace();
			byte[] bytes = Base64.getDecoder().decode(faceImageBase64);
			ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
		}
	}
}