package sa.gov.nic.bio.bw.client.features.foreignenrollment;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.utils.ForeignEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.ForeignInfo;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.net.URL;
import java.util.Map;

public class ShowReceiptFxController extends WizardStepFxControllerBase
{
	@FXML private TextField txtRegistrationNumber;
	@FXML private Pane paneProgress;
	@FXML private Pane paneError;
	@FXML private SwingNode nodeBarcode;
	@FXML private Button btnStartOver;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/showReceipt.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnStartOver.setOnAction(event -> startOver());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			ForeignInfo foreignInfo = (ForeignInfo) uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FOREIGN_INFO);
			Long registrationId = foreignInfo.getCandidateId();
			String sRegistrationId = String.valueOf(registrationId);
			txtRegistrationNumber.setText(sRegistrationId);
			
			new SwingWorker<Barcode, Void>()
			{
				@Override
				protected Barcode doInBackground() throws Exception
				{
					Barcode barcode = BarcodeFactory.createCode128(sRegistrationId);
					barcode.setDrawingQuietSection(false);
					
					return barcode;
				}
				
				@Override
				protected void done()
				{
					Platform.runLater(() ->
					{
						GuiUtils.showNode(paneProgress, false);
						
						try
						{
							Barcode barcode = get();
							GuiUtils.showNode(nodeBarcode, true);
							SwingUtilities.invokeLater(() -> nodeBarcode.setContent(barcode));
						}
						catch(Exception e)
						{
							GuiUtils.showNode(nodeBarcode, false);
							GuiUtils.showNode(paneError, true);
							
							String errorCode = ForeignEnrollmentErrorCodes.C010_00005.getCode();
							String[] errorDetails = {"failed to generate the barcode for the number " +
													 sRegistrationId};
							reportNegativeResponse(errorCode, e, errorDetails);
						}
					});
				}
			}.execute();
		}
	}
}