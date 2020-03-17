package sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.web.WebView;
import sa.gov.nic.bio.bw.core.controllers.FxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;

@FxmlFile("adjudicator.fxml")
public class AdjudicatorDialogFxController extends FxControllerBase
{
	@FXML private Dialog<ButtonType> dialog;
	@FXML private WebView wvAdjudicator;
	
	private long transactionNumber;
	
	@Override
	protected void initialize()
	{
		dialog.setOnShown(event ->
		{
			var engine = wvAdjudicator.getEngine();
			engine.load("http://10.0.73.80/innovatrics/tools/embedded_adjudicator");
		});
	}
	
	public void setTransactionNumber(long transactionNumber)
	{
		this.transactionNumber = transactionNumber;
	}
	
	public void show()
	{
		dialog.showAndWait();
	}
}