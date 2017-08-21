package sa.gov.nic.bio.bw.client.cancellatent;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import retrofit2.Call;
import retrofit2.Response;
import sa.gov.nic.bio.bw.client.cancellatent.webservice.CancelLatentAPI;
import sa.gov.nic.bio.bw.client.core.BodyFxControllerBase;
import sa.gov.nic.bio.bw.client.core.Context;

import java.io.IOException;
import java.util.Random;

public class CancelLatentPaneFxController extends BodyFxControllerBase
{
	@FXML private TextField txtIdNumber;
	@FXML private TextField txtLatentNumber;
	@FXML private Button btnCancelLatent;
	@FXML private ProgressIndicator piCancelLatent;
	
	@FXML
	private void initialize()
	{
		BooleanBinding idNumberEmptyBinding = txtIdNumber.textProperty().isEmpty();
		BooleanBinding latentNumberEmptyBinding = txtLatentNumber.textProperty().isEmpty();
		BooleanProperty progressVisibility = piCancelLatent.visibleProperty();
		
		btnCancelLatent.disableProperty().bind(idNumberEmptyBinding.or(
											latentNumberEmptyBinding).or(progressVisibility));
		txtIdNumber.disableProperty().bind(progressVisibility);
		txtLatentNumber.disableProperty().bind(progressVisibility);
	}
	
	@Override
	public void onControllerReady()
	{
		// request focus once the scene is attached to txtUsername
		txtIdNumber.sceneProperty().addListener((observable, oldValue, newValue) -> txtIdNumber.requestFocus());
	}
	
	@FXML
	private void onEnterPressed(ActionEvent event)
	{
		btnCancelLatent.fire();
	}
	
	public void onCancelLatentButtonClicked(ActionEvent actionEvent)
	{
		// TODO: confirmation dialog
		
		hideNotification();
		piCancelLatent.setVisible(true);
		
		Context.getExecutorService().execute(() ->
		{
			CancelLatentAPI api = Context.getWebserviceManager().getApi(CancelLatentAPI.class);
			Call<Boolean> call = api.cancelLatent();
			Response<Boolean> response;
			try
			{
				response = call.execute();
			}
			catch(IOException e)
			{
				// TODO: report error
				e.printStackTrace();
				Platform.runLater(() -> piCancelLatent.setVisible(false));
				return;
			}
			
			int statusCode = response.code();
			System.out.println("statusCode = " + statusCode);
			
			if(statusCode == 200)
			{
				Boolean result = response.body();
				
				if(result != null && result)
				{
					showSuccessNotification("Success");
				}
				else
				{
					showWarningNotification("User has no latent");
				}
			}
			else if(statusCode == 498)
			{
				if(new Random().nextBoolean()) showWarningNotification("Wrong id");
				else showSuccessNotification("Correct");
			}
			else if(statusCode == 404)
			{
			
			}
			
			Platform.runLater(() -> piCancelLatent.setVisible(false));
		});
	}
}