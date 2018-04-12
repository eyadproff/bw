package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.PersonInfo;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	@FXML private HBox imagePane;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label txtFirstName;
	@FXML private Label txtFatherName;
	@FXML private Label txtGrandfatherName;
	@FXML private Label txtFamilyName;
	@FXML private Button btnNext;
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiryResult.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnNext.setOnAction(actionEvent -> goNext());
		btnStartOver.setOnAction(actionEvent -> startOver());
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
			
			txtFirstName.setText(personInfo.getName().getFirstName());
			txtFatherName.setText(personInfo.getName().getFatherName());
			txtGrandfatherName.setText(personInfo.getName().getGrandfatherName());
			txtFamilyName.setText(personInfo.getName().getFamilyName());
		}
	}
}