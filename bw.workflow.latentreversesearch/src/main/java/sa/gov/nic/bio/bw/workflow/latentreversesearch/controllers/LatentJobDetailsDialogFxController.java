package sa.gov.nic.bio.bw.workflow.latentreversesearch.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
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
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.Workflow;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Decision;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.DecisionHistory;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.FingerHitDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.Latent;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentHitsDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJob;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobDetails;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.beans.LatentJobStatus;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.tasks.GetLatentHitDetailsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.latentreversesearch.utils.LatentReverseSearchErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@FxmlFile("latentJobDetailsDialog.fxml")
public class LatentJobDetailsDialogFxController extends ContentFxControllerBase
{
	@FXML private Dialog<DecisionHistory> dialog;
	@FXML private WebView wvAdjudicator;
	@FXML private TitledPane tpVisualVerification;
	@FXML private Pane paneTop;
	@FXML private Pane paneLatentAssociated;
	@FXML private Pane paneLatentNotAssociated;
	@FXML private Pane paneLoadingInProgress;
	@FXML private Pane paneLoadingError;
	@FXML private Pane paneLoadingWarning;
	@FXML private Pane paneAdjudicator;
	@FXML private Pane paneButtons;
	@FXML private TableView<Latent> tvLatentList;
	@FXML private TableView<DecisionHistory> tvDecisionsHistory;
	@FXML private TableColumn<Latent, String> tcLatentNumber;
	@FXML private TableColumn<Latent, String> tcHitScore;
	@FXML private TableColumn<DecisionHistory, DecisionHistory> tcDecisionSequence;
	@FXML private TableColumn<DecisionHistory, String> tcDecision;
	@FXML private TableColumn<DecisionHistory, String> tcDecisionLatentNumber;
	@FXML private TableColumn<DecisionHistory, String> tcDecisionFingerPosition;
	@FXML private TableColumn<DecisionHistory, String> tcOperatorId;
	@FXML private TableColumn<DecisionHistory, String> tcDecisionDateTime;
	@FXML private ComboBox<ComboBoxItem<FingerHitDetails>> cboSelectedFinger;
	@FXML private TextField txtJobId;
	@FXML private TextField txtStatus;
	@FXML private TextField txtOperatorId;
	@FXML private TextField txtCivilBiometricsId;
	@FXML private TextField txtAssociatedLatentNumber;
	@FXML private ImageView ivAnotherOperatorLockSuccessIcon;
	@FXML private ImageView ivAnotherOperatorLockWarningIcon;
	@FXML private ImageView ivLatentAssociationSuccessIcon;
	@FXML private ImageView ivLatentAssociationWarningIcon;
	@FXML private ImageView ivFingerprintImage;
	@FXML private ImageView ivLatentImage;
	@FXML private Label lblLoadingWarning;
	@FXML private Button btnOpenAdjudicator;
	@FXML private Button btnLinkLatent;
	@FXML private Button btnFinishWithoutLinkingLatent;
	@FXML private Button btnCloseWithoutAction;
	
	private long jobId;
	private long tcn;
	private long civilBiometricsId;
	private Map<Integer, String> fingerImagesWsq = new HashMap<>();
	private Map<Integer, String> fingerImagesBase64;
	private boolean errorOnceAtLeast = false;
	private BooleanProperty disableCompleteButton = new SimpleBooleanProperty(false);
	
	@Override
	protected void initialize()
	{
		Glyph glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.SEARCH_PLUS);
		btnOpenAdjudicator.setGraphic(glyph);
		
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
			var latentId = latent.getLatentId();
			return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(latentId)));
		});
		tcHitScore.setCellValueFactory(param ->
        {
            var latent = param.getValue();
	        Integer generalScore = latent.getGeneralScore();
	        if(generalScore == null) return null;
	        return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(generalScore)));
        });
		
		GuiUtils.initSequenceTableColumn(tcDecisionSequence);
		tcDecisionSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tcDecision.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
			var decisionCode = decisionRecord.getDecision();
			if(decisionCode == null || decisionCode == 0) return null;
			
			Decision decision = Decision.findByCode(decisionCode);
			if(decision == null) return null;
			
			String decisionText;
			
			switch(decision)
			{
				case ASSOCIATE_LATENT: decisionText = resources.getString("label.linkLatent"); break;
				case COMPLETE_WITHOUT_ASSOCIATING_LATENT: decisionText = resources.getString("label.completeWithoutLinkingLatent"); break;
				case VIEW_ONLY: decisionText = resources.getString("label.viewOnly"); break;
				default: decisionText = "";
			}
			
			return new SimpleStringProperty(decisionText);
		});
		tcDecisionLatentNumber.setCellValueFactory(param ->
		{
			var decisionRecord = param.getValue();
			var latentNumber = decisionRecord.getLinkedLatentBioId();
			if(latentNumber == null || latentNumber == 0L) return null;
			
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(latentNumber)));
		});
		tcDecisionFingerPosition.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
		    Integer fingerPosition = decisionRecord.getFingerPos();
		    if(fingerPosition == null || fingerPosition == 0) return null;
			
			String[] labels = fingerprintLabelMap.get(fingerPosition);
			
			return new SimpleStringProperty(labels[0] + " (" + labels[1] + ")");
		});
		tcOperatorId.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
		    Long operatorId = decisionRecord.getOperatorId();
		    if(operatorId == null || operatorId == 0L) return null;
		
		    return new SimpleStringProperty(AppUtils.localizeNumbers(String.valueOf(operatorId)));
		});
		tcDecisionDateTime.setCellValueFactory(param ->
		{
		    var decisionRecord = param.getValue();
		    Long decisionTimestamp = decisionRecord.getDecisionDate();
		    if(decisionTimestamp == null) return null;
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
				List<FingerHitDetails> fingerHits = newValue.getFingerHitDetails();
				if(fingerHits == null)
				{
					cboSelectedFinger.getItems().clear();
					return;
				}
				
				ObservableList<ComboBoxItem<FingerHitDetails>> statusItems = FXCollections.observableArrayList();
				for(var fingerHit : fingerHits)
				{
					int position = fingerHit.getPosition();
					int score = fingerHit.getScore();
					String[] labels = fingerprintLabelMap.get(position);
					
					statusItems.add(new ComboBoxItem<>(fingerHit, labels[0] + " (" + labels[1] + ") [" + resources.getString("label.score") + " " + 
																  AppUtils.localizeNumbers(String.valueOf(score)) + "]"));
				}
				
				cboSelectedFinger.setItems(statusItems);
				if(!statusItems.isEmpty()) cboSelectedFinger.getSelectionModel().selectFirst();
			}
		});
		
		cboSelectedFinger.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
		{
			ivLatentImage.setImage(null);
			ivFingerprintImage.setImage(null);
			
			if(newValue != null)
			{
				Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
				
				var latent = tvLatentList.getSelectionModel().getSelectedItem();
				String latentImageBase64 = latent != null ? latent.getLatentImageBase64() : null;
				String fingerImageBase64 = fingerImagesBase64.get(newValue.getItem().getPosition());
				
				if(latentImageBase64 != null)
				{
					byte[] bytes = Base64.getDecoder().decode(latentImageBase64);
					var image = new Image(new ByteArrayInputStream(bytes));
					ivLatentImage.setImage(image);
					GuiUtils.attachImageDialog(stage, ivLatentImage,
					                           resources.getString("label.latentImage"),
					                           resources.getString("label.contextMenu.showImage"), false);
				}
				
				if(fingerImageBase64 != null)
				{
					byte[] bytes = Base64.getDecoder().decode(fingerImageBase64);
					var image = new Image(new ByteArrayInputStream(bytes));
					ivFingerprintImage.setImage(image);
					GuiUtils.attachImageDialog(stage, ivFingerprintImage,
					                           resources.getString("label.fingerprintImage"),
					                           resources.getString("label.contextMenu.showImage"), false);
				}
			}
		});
		
		btnOpenAdjudicator.disableProperty().bind(ivFingerprintImage.imageProperty().isNull().or(ivLatentImage.imageProperty().isNull()));
		btnLinkLatent.disableProperty().bind(cboSelectedFinger.getSelectionModel().selectedItemProperty().isNull().or(ivAnotherOperatorLockWarningIcon.visibleProperty())
		                                                                                                          .or(paneLatentAssociated.visibleProperty()
		                                                                                                          .or(ivFingerprintImage.imageProperty().isNull()
                                                                                                                  .or(ivLatentImage.imageProperty().isNull()))));
		btnFinishWithoutLinkingLatent.disableProperty().bind(ivAnotherOperatorLockWarningIcon.visibleProperty().or(disableCompleteButton));
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
			stage.setOnCloseRequest(windowEvent ->
			{
				if(paneLoadingInProgress.isVisible()) windowEvent.consume();
				else if(!paneLoadingError.isVisible() && !paneLoadingWarning.isVisible() && !errorOnceAtLeast)
				{
					windowEvent.consume();
					onCloseWithoutActionButtonClicked(null);
				}
			});
			dialog.getDialogPane().setMinWidth(dialog.getDialogPane().getPrefWidth());
			dialog.getDialogPane().setMinHeight(dialog.getDialogPane().getPrefHeight());
			stage.setMinWidth(dialog.getDialogPane().getPrefWidth() + 16.0);
			stage.setMinHeight(dialog.getDialogPane().getPrefHeight() + 39.0);
			
			setData(GetLatentHitDetailsWorkflowTask.class, "tcn", tcn);
			
			boolean success = executeUiTask(GetLatentHitDetailsWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					LatentJobDetails latentJobDetails = getData("latentJobDetails");
					showLatentHitDetails(latentJobDetails);
					GuiUtils.showNode(paneLoadingInProgress, false);
                }
            },
            throwable ->
            {
	            errorOnceAtLeast = true;
	            GuiUtils.showNode(paneLoadingInProgress, false);
            	
            	if(throwable instanceof Signal)
	            {
	            	var signal = (Signal) throwable;
		            var payload = signal.getPayload();
		            TaskResponse<?> taskResponse = (TaskResponse<?>) payload.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
		
		            if(taskResponse != null)
		            {
			            String errorCode = taskResponse.getErrorCode();
			            if(errorCode.startsWith("B") || errorCode.startsWith("N")) // business error
			            {
				            // no exceptions/errorDetails in case of business error
				            try
				            {
					            String guiErrorMessage = resources.getString(errorCode);
					            String logErrorMessage = resources.getString(errorCode + ".internal");
					            
					            LOGGER.info(logErrorMessage);
					            
					            lblLoadingWarning.setText(guiErrorMessage);
					            GuiUtils.showNode(paneLoadingWarning, true);
					            return;
				            }
				            catch(Exception e)
				            {
					            LOGGER.log(Level.WARNING, errorCode, e);
					            showWarningNotification(errorCode);
				            }
			            }
		            }
	            }
            	
                GuiUtils.showNode(paneLoadingError, true);

                String errorCode = LatentReverseSearchErrorCodes.C018_00002.getCode();
                String[] errorDetails = {"failed to load latent hit details! transactionNumber = " + tcn};
                Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails, getTabIndex());
            });
			
			if(!success)
			{
				GuiUtils.showNode(paneLoadingInProgress, false);
				GuiUtils.showNode(paneLoadingError, true);
			}
		});
	}
	
	public void setJobId(long jobId)
	{
		this.jobId = jobId;
	}
	
	public void setTcn(long tcn)
	{
		this.tcn = tcn;
	}
	
	public void setCivilBiometricsId(long civilBiometricsId)
	{
		this.civilBiometricsId = civilBiometricsId;
	}
	
	public Optional<DecisionHistory> showAndWait()
	{
		return dialog.showAndWait();
	}
	
	private void showLatentHitDetails(LatentJobDetails latentJobDetails)
	{
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		
		LatentHitsDetails latentHitsDetails = latentJobDetails.getLatentHitsDetails();
		List<DecisionHistory> decisionHistoryList = latentJobDetails.getDecisionHistoryList();
		LatentJob latentJob = latentJobDetails.getLatentJob();
		Long linkedLatentHit = latentJobDetails.getLinkedLatentHit();
		
		LatentJobStatus status = null;
		Long operatorId = null;
		
		if(latentJob != null)
		{
			status = LatentJobStatus.findByCode(latentJob.getStatus());
			if(status == LatentJobStatus.NEW) status = LatentJobStatus.IN_PROGRESS;
			
			operatorId = latentJob.getOperatorId();
			if(operatorId == null || operatorId == 0L)
			{
				operatorId = userInfo.getOperatorId();
			}
			
			txtOperatorId.setText(String.valueOf(operatorId));
		}
		
		txtJobId.setText(String.valueOf(jobId));
		txtCivilBiometricsId.setText(String.valueOf(civilBiometricsId));
		
		List<Latent> latents = new ArrayList<>();
		
		if(latentHitsDetails != null)
		{
			Map<Long, List<FingerHitDetails>> latentHitDetails = latentHitsDetails.getLatentHitDetails();
			Map<Long, String> latentImagesBase64 = latentHitsDetails.getLatentImagesBase64();
			Map<Long, String> latentImagesWsq = latentHitsDetails.getLatentImagesWsq();
			List<Finger> civilFingers = latentHitsDetails.getCivilFingers();
			
			if(latentHitDetails != null)
			{
				for(var entry : latentHitDetails.entrySet())
				{
					List<FingerHitDetails> fingerHitDetails = entry.getValue();
					
					Integer generalScore = null;
					if(fingerHitDetails != null && !fingerHitDetails.isEmpty()) generalScore = fingerHitDetails.get(0).getGeneralScore();
					
					Latent latent = new Latent();
					latent.setLatentId(entry.getKey());
					latent.setFingerHitDetails(fingerHitDetails);
					latent.setGeneralScore(generalScore);
					latent.setLatentImageWsq(latentImagesWsq.get(entry.getKey()));
					latent.setLatentImageBase64(latentImagesBase64.get(entry.getKey()));
					latents.add(latent);
				}
			}
			
			if(civilFingers != null)
			{
				for(Finger finger : civilFingers)
				{
					fingerImagesWsq.put(finger.getType(), finger.getImage());
				}
			}
			
			fingerImagesBase64 = latentHitsDetails.getFingerImagesBase64();
		}
		
		tvLatentList.getItems().setAll(latents);
		if(decisionHistoryList != null) tvDecisionsHistory.getItems().setAll(decisionHistoryList);
		
		String statusText;
		
		if(status != null) switch(status)
		{
			case IN_PROGRESS: statusText = resources.getString("label.inProgress"); break;
			case COMPLETED: statusText = resources.getString("label.done"); break;
			default: statusText = "";
		}
		else statusText = "";
		
		txtStatus.setText(statusText);
		
		if(status == LatentJobStatus.IN_PROGRESS || status == LatentJobStatus.NEW)
		{
			boolean lockedByAnotherUser = operatorId != userInfo.getOperatorId();
			
			if(lockedByAnotherUser)
			{
				GuiUtils.showNode(ivAnotherOperatorLockWarningIcon, true);
			}
			else
			{
				GuiUtils.showNode(ivAnotherOperatorLockSuccessIcon, true);
			}
			
			if(linkedLatentHit != null)
			{
				txtAssociatedLatentNumber.setText(String.valueOf(linkedLatentHit));
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
			GuiUtils.showNode(ivAnotherOperatorLockSuccessIcon, true);
			disableCompleteButton.setValue(true);
			
			if(linkedLatentHit != null)
			{
				txtAssociatedLatentNumber.setText(String.valueOf(linkedLatentHit));
				GuiUtils.showNode(paneLatentAssociated, true);
			}
			else
			{
				GuiUtils.showNode(paneLatentNotAssociated, true);
			}
			
			GuiUtils.showNode(ivLatentAssociationSuccessIcon, true);
		}
		
		GuiUtils.showNode(paneTop, true);
		GuiUtils.showNode(paneAdjudicator, true);
		GuiUtils.showNode(paneButtons, true);
	}
	
	@FXML
	private void onOpenAdjudicatorButtonClicked(ActionEvent actionEvent)
	{
		try
		{
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			AdjudicatorDialogFxController controller = DialogUtils.buildCustomDialogByFxml(stage, AdjudicatorDialogFxController.class, true);
			
			if(controller != null)
			{
				var latent = tvLatentList.getSelectionModel().getSelectedItem();
				var selectedItem = cboSelectedFinger.getSelectionModel().getSelectedItem();
				String latentImageBase64 = latent != null ? latent.getLatentImageWsq() : null;
				String fingerImageBase64 = selectedItem != null ? fingerImagesWsq.get(selectedItem.getItem().getPosition()) : null;
				
				controller.setFingerImageBase64(fingerImageBase64);
				controller.setLatentImageBase64(latentImageBase64);
				controller.show();
			}
		}
		catch(Exception e)
		{
			String errorCode = LatentReverseSearchErrorCodes.C018_00003.getCode();
			String[] errorDetails = {"Failed to load (" + AdjudicatorDialogFxController.class.getName() + ")!"};
			Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
		}
	}
	
	@FXML
	private void onLinkLatentButtonClicked(ActionEvent actionEvent)
	{
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		var latent = tvLatentList.getSelectionModel().getSelectedItem();
		String latentNumber = AppUtils.localizeNumbers(String.valueOf(latent.getLatentId()));
		String civilBiometricsId = AppUtils.localizeNumbers(String.valueOf(this.civilBiometricsId));
		
		String headerText = resources.getString("linkingLatent.confirmation.header");
		String contentText = String.format(resources.getString("linkingLatent.confirmation.message"), latentNumber, civilBiometricsId);
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(stage, headerText, contentText);
		if(!confirmed) return;
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		ComboBoxItem<FingerHitDetails> selectedItem = cboSelectedFinger.getSelectionModel().getSelectedItem();
		FingerHitDetails fingerHitDetails = selectedItem != null ? selectedItem.getItem() : null;
		
		DecisionHistory decisionHistory = new DecisionHistory();
		decisionHistory.setJobId(jobId);
		decisionHistory.setTcn(tcn);
		decisionHistory.setOperatorId(userInfo.getOperatorId());
		decisionHistory.setDecision(Decision.ASSOCIATE_LATENT.getCode());
		decisionHistory.setDecisionDate(System.currentTimeMillis() / 1000L);
		decisionHistory.setLinkedLatentBioId(latent.getLatentId());
		decisionHistory.setLinkedCivilBioID(this.civilBiometricsId);
		
		if(fingerHitDetails != null)
		{
			decisionHistory.setScore(fingerHitDetails.getScore());
			decisionHistory.setFingerPos(fingerHitDetails.getPosition());
		}
		
		dialog.setResult(decisionHistory);
		dialog.close();
	}
	
	@FXML
	private void onFinishWithoutLinkingLatentButtonClicked(ActionEvent actionEvent)
	{
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		String transactionNumber = AppUtils.localizeNumbers(String.valueOf(this.tcn));
		
		String headerText = resources.getString("finishWithoutLinkingLatent.confirmation.header");
		String contentText = String.format(resources.getString("finishWithoutLinkingLatent.confirmation.message"), transactionNumber);
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(stage, headerText, contentText);
		if(!confirmed) return;
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		
		DecisionHistory decisionHistory = new DecisionHistory();
		decisionHistory.setJobId(jobId);
		decisionHistory.setTcn(tcn);
		decisionHistory.setOperatorId(userInfo.getOperatorId());
		decisionHistory.setDecision(Decision.COMPLETE_WITHOUT_ASSOCIATING_LATENT.getCode());
		decisionHistory.setDecisionDate(System.currentTimeMillis() / 1000L);
		decisionHistory.setLinkedCivilBioID(this.civilBiometricsId);
		
		dialog.setResult(decisionHistory);
		dialog.close();
	}
	
	@FXML
	private void onCloseWithoutActionButtonClicked(ActionEvent actionEvent)
	{
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		
		DecisionHistory decisionHistory = new DecisionHistory();
		decisionHistory.setJobId(jobId);
		decisionHistory.setTcn(tcn);
		decisionHistory.setOperatorId(userInfo.getOperatorId());
		decisionHistory.setDecision(Decision.VIEW_ONLY.getCode());
		decisionHistory.setDecisionDate(System.currentTimeMillis() / 1000L);
		decisionHistory.setLinkedCivilBioID(this.civilBiometricsId);
		
		dialog.setResult(decisionHistory);
		dialog.close();
	}
}