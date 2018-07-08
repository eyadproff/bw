package sa.gov.nic.bio.bw.client.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.RegionFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

public class FingerprintScannerGadgetDialogFxController extends RegionFxControllerBase
{
	@FXML private Dialog<ButtonType> dialog;
	@FXML private HBox paneFingerprintScanner;
	@FXML private Label lblDevicesRunnerNotWorking;
	@FXML private Label lblDevicesRunnerWorking;
	@FXML private Label lblFingerprintScannerNotInitialized;
	@FXML private Label lblFingerprintScannerNotConnected;
	@FXML private Label lblFingerprintScannerInitialized;
	@FXML private ProgressIndicator piFingerprintScanner;
	@FXML private Button btnDevicesRunnerAction;
	@FXML private Button btnFingerprintScannerAction;
	@FXML private ButtonType btClose;
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event ->
		{
			// workaround to center the button
			Platform.runLater(() ->
			{
				Button btnClose = (Button) dialog.getDialogPane().lookupButton(btClose);
				HBox hBox = (HBox) btnClose.getParent();
				double translateAmount = hBox.getWidth() / 2.0 - btnClose.getWidth() / 2.0 - hBox.getPadding().getLeft();
				btnClose.translateXProperty().set(-translateAmount);
			});
			
			Glyph gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
			btnDevicesRunnerAction.setGraphic(gearIcon);
			
			gearIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.GEAR);
			btnFingerprintScannerAction.setGraphic(gearIcon);
		});
	}
	
	public void showDialogAndWait()
	{
		dialog.showAndWait();
	}
}