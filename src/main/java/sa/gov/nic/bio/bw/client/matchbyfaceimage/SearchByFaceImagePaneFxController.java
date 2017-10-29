package sa.gov.nic.bio.bw.client.matchbyfaceimage;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;

import java.util.HashMap;
import java.util.Map;

public class SearchByFaceImagePaneFxController extends BodyFxControllerBase
{
	@FXML private ImageView ivUploadedImage;
	@FXML private ImageView ivPersonImage;
	@FXML private TextField txtPersonId;
	@FXML private Button btnUploadImage;
	@FXML private Button btnGetPersonImageAndMatch;
	@FXML private ProgressIndicator piGetPersonImageAndMatch;
	@FXML private TextArea txtPersonDetails;
	
	@FXML
	private void initialize()
	{
	
	}
	
	@Override
	public void onControllerReady()
	{
		// request focus once the scene is attached to txtPersonId
		txtPersonId.sceneProperty().addListener((observable, oldValue, newValue) -> txtPersonId.requestFocus());
	}
	
	@Override
	public void onReturnFromTask()
	{
		/*disableUiControls(false);*/
		
		/*Boolean successResponse = (Boolean) inputData.get("successResponse");
		if(successResponse != null && successResponse)
		{
			String idNumber = txtPersonId.getText();
			String latentNumber = txtCriminalId.getText();
			
			Boolean resultBean = (Boolean) inputData.get("resultBean");
			if(resultBean != null && resultBean)
			{
				String message = String.format(messagesBundle.getString("cancelCriminal.success"), latentNumber, idNumber);
				showSuccessNotification(message);
			}
			else
			{
				String message = String.format(messagesBundle.getString("cancelCriminal.failure"), latentNumber, idNumber);
				showWarningNotification(message);
			}
		}
		else super.onReturnFromTask();*/
	}
	
	
}