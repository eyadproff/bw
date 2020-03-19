package sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.DecisionRecord;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.FingerHit;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Latent;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitProcessingStatus;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.GetLatentHitDetailsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.utils.LatentReverseSearchErrorCodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FxmlFile("adjudicator.fxml")
public class AdjudicatorDialogFxController extends ContentFxControllerBase
{
	private static final String URL_ADJUDICATOR = Context.getServerUrl() + "/innovatrics/tools/embedded_adjudicator";
	private static final String JAVASCRIPT_COMMAND_INIT_ADJUDICATOR = "window.adjudicatorApp.init({probe:{image:\"%s\"},reference:{image:\"%s\"}})";
	
	@FXML private Dialog<ButtonType> dialog;
	@FXML private WebView wvAdjudicator;
	@FXML private TitledPane tpVisualVerification;
	@FXML private Pane paneTop;
	@FXML private Pane paneLockedByOperator;
	@FXML private Pane paneLatentAssociated;
	@FXML private Pane paneLatentNotAssociated;
	@FXML private Pane paneLoadingInProgress;
	@FXML private Pane paneLoadingError;
	@FXML private Pane paneAdjudicator;
	@FXML private Pane paneButtons;
	@FXML private TableView<Latent> tvLatentList;
	@FXML private TableView<DecisionRecord> tvDecisionsHistory;
	@FXML private TableColumn<Latent, String> tcLatentNumber;
	@FXML private TableColumn<Latent, String> tcHitScore;
	@FXML private TableColumn<DecisionRecord, DecisionRecord> tcDecisionSequence;
	@FXML private TableColumn<DecisionRecord, String> tcDecision;
	@FXML private TableColumn<DecisionRecord, String> tcDecisionLatentNumber;
	@FXML private TableColumn<DecisionRecord, String> tcDecisionFingerPosition;
	@FXML private TableColumn<DecisionRecord, String> tcOperatorId;
	@FXML private TableColumn<DecisionRecord, String> tcDecisionDateTime;
	@FXML private ProgressIndicator piSubmitting;
	@FXML private ComboBox<ComboBoxItem<FingerHit>> cboSelectedFinger;
	@FXML private TextField txtTransactionNumber;
	@FXML private TextField txtStatus;
	@FXML private TextField txtOperatorId;
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtAssociatedLatentNumber;
	@FXML private ImageView ivAnotherOperatorLockSuccessIcon;
	@FXML private ImageView ivAnotherOperatorLockWarningIcon;
	@FXML private ImageView ivLatentAssociationSuccessIcon;
	@FXML private ImageView ivLatentAssociationWarningIcon;
	@FXML private Button btnVisualVerificationFullScreen;
	@FXML private Button btnLinkLatent;
	@FXML private Button btnFinishWithoutLinkingLatent;
	@FXML private Button btnViewWithoutAction;
	
	private long transactionNumber;
	private long civilBiometricsId;
	
	@Override
	protected void initialize()
	{
		Glyph glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.SEARCH_PLUS);
		btnVisualVerificationFullScreen.setGraphic(glyph);
		
		NodeOrientation reverse = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT ?
		                          NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT;
		tpVisualVerification.setNodeOrientation(reverse);
		
		Map<Integer, String[]> fingerprintLabelMap = new HashMap<>();
		fingerprintLabelMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
		                        new String[]{resources.getString("label.fingers.thumb"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
		                        new String[]{resources.getString("label.fingers.index"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
		                        new String[]{resources.getString("label.fingers.middle"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_RING.getPosition(),
		                        new String[]{resources.getString("label.fingers.ring"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
		                        new String[]{resources.getString("label.fingers.little"),
		                                     resources.getString("label.rightHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_THUMB.getPosition(),
		                        new String[]{resources.getString("label.fingers.thumb"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_INDEX.getPosition(),
		                        new String[]{resources.getString("label.fingers.index"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
		                        new String[]{resources.getString("label.fingers.middle"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_RING.getPosition(),
		                        new String[]{resources.getString("label.fingers.ring"),
		                                     resources.getString("label.leftHand")});
		fingerprintLabelMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
		                        new String[]{resources.getString("label.fingers.little"),
		                                     resources.getString("label.leftHand")});
		
		tcLatentNumber.setCellValueFactory(param ->
		{
			var latent = param.getValue();
		    return new SimpleStringProperty(AppUtils.localizeNumbers(latent.getNumber()));
		});
		tcHitScore.setCellValueFactory(param ->
        {
            var latent = param.getValue();
            return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(latent.getScore())));
        });
		
		GuiUtils.initSequenceTableColumn(tcDecisionSequence);
		tcDecisionSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcDecision.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
			var decision = decisionRecord.getDecision();
			if(decision == null) return null;
			
			String decisionText;
			
			switch(decision)
			{
				case LATENT_ASSOCIATED: decisionText = resources.getString("label.latentAssociated"); break;
				case FINISHED_WITH_ASSOCIATING_LATENT: decisionText = resources.getString("label.linkLatent"); break;
				case VIEW_WITHOUT_ACTION: decisionText = resources.getString("label.finishWithoutLinkingLatent"); break;
				default: decisionText = "";
			}
			
			return new SimpleStringProperty(decisionText);
		});
		tcDecisionLatentNumber.setCellValueFactory(param ->
		{
			var decisionRecord = param.getValue();
			var latentNumber = decisionRecord.getLatentNumber();
			if(latentNumber == null) return null;
			
		    return new SimpleStringProperty(AppUtils.localizeNumbers(latentNumber));
		});
		tcDecisionFingerPosition.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
		    Integer fingerPosition = decisionRecord.getFingerPosition();
		    if(fingerPosition == null) return null;
			
			String[] labels = fingerprintLabelMap.get(fingerPosition);
			
			return new SimpleStringProperty(labels[0] + " (" + labels[1] + ")");
		});
		tcOperatorId.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
		    long operatorId = decisionRecord.getOperatorId();
		
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(operatorId)));
		});
		tcDecisionDateTime.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
		    long decisionTimestamp = decisionRecord.getDecisionTimestamp();
		    return new SimpleStringProperty(AppUtils.formatHijriGregorianDateTime(decisionTimestamp));
		});
		
		tvLatentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue == null)
			{
				cboSelectedFinger.getItems().clear();
			}
			else
			{
				List<FingerHit> fingerHits = newValue.getFingerHits();
				if(fingerHits == null)
				{
					cboSelectedFinger.getItems().clear();
					return;
				}
				
				ObservableList<ComboBoxItem<FingerHit>> statusItems = FXCollections.observableArrayList();
				for(var fingerHit : fingerHits)
				{
					int position = fingerHit.getPosition();
					int score = fingerHit.getScore();
					String[] labels = fingerprintLabelMap.get(position);
					
					statusItems.add(new ComboBoxItem<>(fingerHit, labels[0] + " (" + labels[1] + ") [" +
																  AppUtils.localizeNumbers(String.valueOf(score)) + "]"));
				}
				
				cboSelectedFinger.setItems(statusItems);
				if(!statusItems.isEmpty()) cboSelectedFinger.getSelectionModel().selectFirst();
			}
		});
		
		cboSelectedFinger.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			var latent = tvLatentList.getSelectionModel().getSelectedItem();
			
			if(newValue == null)
			{
				wvAdjudicator.getEngine().loadContent("");
			}
			else
			{
				String latentImageBase64 = latent.getImageBase64();
				String fingerImageBase64 = newValue.getItem().getImageBase64();
				if(oldValue == null) wvAdjudicator.getEngine().load(URL_ADJUDICATOR);
				wvAdjudicator.getEngine().executeScript(String.format(JAVASCRIPT_COMMAND_INIT_ADJUDICATOR, latentImageBase64, fingerImageBase64));
			}
		});
		
		btnLinkLatent.disableProperty().bind(cboSelectedFinger.getSelectionModel().selectedItemProperty().isNull().or(ivAnotherOperatorLockWarningIcon.visibleProperty())
		                                                                                                          .or(ivLatentAssociationWarningIcon.visibleProperty()));
		btnFinishWithoutLinkingLatent.disableProperty().bind(ivAnotherOperatorLockWarningIcon.visibleProperty());
		cboSelectedFinger.disableProperty().bind(tvLatentList.getSelectionModel().selectedItemProperty().isNull());
		
		dialog.setOnShown(event ->
		{
			// workaround to center buttons and remove extra spaces
			ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
			buttonBar.getButtons().setAll(paneButtons);
			HBox hBox = (HBox) buttonBar.lookup(".container");
			hBox.setPadding(new Insets(0.0, 10.0, 10.0, 10.0));
			hBox.getChildren().remove(0);
			
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			dialog.getDialogPane().setMinWidth(dialog.getDialogPane().getPrefWidth());
			dialog.getDialogPane().setMinHeight(dialog.getDialogPane().getPrefHeight());
			stage.setMinWidth(dialog.getDialogPane().getPrefWidth() + 16.0);
			stage.setMinHeight(dialog.getDialogPane().getPrefHeight() + 39.0);
			
			setData(GetLatentHitDetailsWorkflowTask.class, "transactionNumber", transactionNumber);
			
			boolean success = executeUiTask(GetLatentHitDetailsWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					LatentHitDetails latentHitDetails = getData("latentHitDetails");
					showLatentHitDetails(latentHitDetails);
					GuiUtils.showNode(paneLoadingInProgress, false);
                }
            },
            throwable ->
            {
                GuiUtils.showNode(paneLoadingInProgress, false);
                GuiUtils.showNode(paneLoadingError, true);

                String errorCode = LatentReverseSearchErrorCodes.C018_00002.getCode();
                String[] errorDetails = {"failed to load latent hit details! transactionNumber = " + transactionNumber};
                Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
            });
			
			if(!success)
			{
				GuiUtils.showNode(paneLoadingInProgress, false);
				GuiUtils.showNode(paneLoadingError, true);
			}
		});
	}
	
	public void setTransactionNumber(long transactionNumber)
	{
		this.transactionNumber = transactionNumber;
	}
	
	public void setCivilBiometricsId(long civilBiometricsId)
	{
		this.civilBiometricsId = civilBiometricsId;
	}
	
	public void show()
	{
		dialog.showAndWait();
	}
	
	private void showLatentHitDetails(LatentHitDetails latentHitDetails)
	{
		List<Latent> latents = latentHitDetails.getLatents();
		List<DecisionRecord> decisionRecords = latentHitDetails.getDecisionRecords();
		LatentHitProcessingStatus status = latentHitDetails.getStatus();
		Long lockedByOperatorId = latentHitDetails.getLockedByOperatorId();
		String associatedLatentNumber = latentHitDetails.getAssociatedLatentNumber();
		
		txtTransactionNumber.setText(String.valueOf(transactionNumber));
		txtCivilBiometricsId.setText(String.valueOf(civilBiometricsId));
		
		if(latents != null) tvLatentList.getItems().setAll(latents);
		if(decisionRecords != null) tvDecisionsHistory.getItems().setAll(decisionRecords);
		
		String statusText;
		
		switch(status)
		{
			case NEW: statusText = resources.getString("label.new"); break;
			case IN_PROGRESS: statusText = resources.getString("label.inProgress"); break;
			case DONE: statusText = resources.getString("label.done"); break;
			default: statusText = "";
		}
		
		txtStatus.setText(statusText);
		
		if(status == LatentHitProcessingStatus.IN_PROGRESS)
		{
			if(lockedByOperatorId != null) txtOperatorId.setText(String.valueOf(lockedByOperatorId));
			GuiUtils.showNode(paneLockedByOperator, true);
			
			UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
			boolean lockedByAnotherUser = lockedByOperatorId != null && lockedByOperatorId != userInfo.getOperatorId();
			
			if(lockedByAnotherUser)
			{
				GuiUtils.showNode(ivAnotherOperatorLockWarningIcon, true);
			}
			else
			{
				GuiUtils.showNode(ivAnotherOperatorLockSuccessIcon, true);
			}
			
			if(associatedLatentNumber != null)
			{
				txtAssociatedLatentNumber.setText(associatedLatentNumber);
				GuiUtils.showNode(paneLatentAssociated, true);
				GuiUtils.showNode(ivLatentAssociationWarningIcon, true);
			}
			else
			{
				GuiUtils.showNode(paneLatentNotAssociated, true);
				GuiUtils.showNode(ivLatentAssociationSuccessIcon, true);
			}
		}
		else //if(status == LatentHitProcessingStatus.DONE) 
		{
			if(associatedLatentNumber != null)
			{
				txtAssociatedLatentNumber.setText(associatedLatentNumber);
				GuiUtils.showNode(paneLatentAssociated, true);
				GuiUtils.showNode(ivLatentAssociationSuccessIcon, true);
			}
			else
			{
				GuiUtils.showNode(paneLatentNotAssociated, true);
				GuiUtils.showNode(ivLatentAssociationWarningIcon, true);
			}
		}
		
		GuiUtils.showNode(paneTop, true);
		GuiUtils.showNode(paneAdjudicator, true);
		GuiUtils.showNode(paneButtons, true);
	}
}