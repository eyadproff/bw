package sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import sa.gov.nic.bio.bw.core.controllers.FxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.workflow.commons.ui.PTableColumn;

@FxmlFile("adjudicator.fxml")
public class AdjudicatorDialogFxController extends FxControllerBase
{
	@FXML private Dialog<ButtonType> dialog;
	@FXML private WebView wvAdjudicator;
	@FXML private Pane paneTop;
	@FXML private Pane paneLatentAssociated;
	@FXML private Pane paneLatentNotAssociated;
	@FXML private Pane paneLoadingInProgress;
	@FXML private Pane paneLoadingError;
	@FXML private Pane paneAdjudicator;
	@FXML private Pane paneButtons;
	@FXML private TableView tvLatentList;
	@FXML private TableView tvDecisionsHistory;
	@FXML private PTableColumn tcLatentNumber;
	@FXML private PTableColumn tcHitScore;
	@FXML private PTableColumn tcDecisionSequence;
	@FXML private PTableColumn tcDecision;
	@FXML private PTableColumn tcDecisionLatentNumber;
	@FXML private PTableColumn tcDecisionFingerType;
	@FXML private PTableColumn tcOperatorId;
	@FXML private PTableColumn tcDecisionDateTime;
	@FXML private ProgressIndicator piSubmitting;
	@FXML private ComboBox cboSelectedFinger;
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtAssociatedLatentNumber;
	@FXML private TextField txtTransactionNumber;
	@FXML private TextField txtStatus;
	@FXML private ImageView ivLatentAssociationSuccessIcon;
	@FXML private ImageView ivLatentAssociationWarningIcon;
	@FXML private Button btnLinkLatent;
	@FXML private Button btnFinishWithoutLinkingLatent;
	
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