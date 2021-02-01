package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.iris.beans.CaptureIrisResponse;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@FxmlFile("irisCapturing.fxml")
public class IrisCapturingFxController extends WizardStepFxControllerBase
{
	private static final int LEFT_IRIS_ONLY = 0;
	private static final int RIGHT_IRIS_ONLY = 1;
	private static final int LEFT_AND_RIGHT_IRIS = 2;

	@Input (alwaysRequired = true)
	private Request irisCapturingRequest;
	@Input private Boolean hidePreviousButton;
	@Input private Boolean hideStartOverButton;
	@Input private Boolean showSkipButton;
	@Input private Boolean showPersonInfo;
	@Input private PersonInfo personInfo;
	@Output private String capturedRightIrisBase64;
	@Output private String capturedLeftIrisBase64;
	@Output private String capturedRightIrisCompressedBase64;
	@Output private String capturedLeftIrisCompressedBase64;


	@FXML private TitledPane tpPersonInfo;
	@FXML private Label lblPersonId;
	@FXML private Label lblName;
	@FXML private VBox paneControlsInnerContainer;
	@FXML private ScrollPane paneControlsOuterContainer;
	@FXML private ProgressIndicator piProgress;
	@FXML private Label lblStatus;
	@FXML private FourStateTitledPane tpCapturedFirstIris;
	@FXML private FourStateTitledPane tpCapturedSecondIris;
	@FXML private ImageView ivSuccess;
	@FXML private ImageView ivSkippedFirstIris;
	@FXML private ImageView ivSkippedSecondIris;
	@FXML private ImageView ivCapturedFirstIrisPlaceholder;
	@FXML private ImageView ivCapturedSecondIrisPlaceholder;
	@FXML private ImageView ivCapturedFirstIris;
	@FXML private ImageView ivCapturedSecondIris;
	@FXML private CheckBox cbSkippedFirstIris;
	@FXML private CheckBox cbSkippedSecondIris;
	@FXML private Button btnCancel;
	@FXML private Button btnCaptureIris;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	@FXML private Button btnSkip;

	private boolean irisDeviceInitializedAtLeastOnce = false;
	private FourStateTitledPane tpCapturedRightIris;
	private FourStateTitledPane tpCapturedLeftIris;
	private ImageView ivRightIris;
	private ImageView ivLeftIris;
	private ImageView ivSkippedRightIris;
	private ImageView ivSkippedLeftIris;
	private CheckBox cbSkippedRightIris;
	private CheckBox cbSkippedLeftIris;
	private Integer rightIrisMinScore;
	private Integer leftIrisMinScore;
	private int rightIrisScore;
	private int leftIrisScore;

	public enum Request{
		ENROLLMENT,
		IDENTIFICATION

	}
	@Override
	protected void onAttachedToScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();

		btnNext.disableProperty().bind(ivSuccess.visibleProperty().not());

		if( irisCapturingRequest == Request.ENROLLMENT) {
			rightIrisMinScore = Integer.parseInt(Context.getConfigManager().getProperty("enrollment.rightIris.minimumScore"));
			leftIrisMinScore = Integer.parseInt(Context.getConfigManager().getProperty("enrollment.leftIris.minimumScore"));
		}
		else {
			rightIrisMinScore = Integer.parseInt(Context.getConfigManager().getProperty("identification.rightIris.minimumScore"));
			leftIrisMinScore = Integer.parseInt(Context.getConfigManager().getProperty("identification.leftIris.minimumScore"));
		}

		if(showPersonInfo != null) {
			GuiUtils.showNode(tpPersonInfo, showPersonInfo);

			if(personInfo != null && showPersonInfo) {
				Name name = personInfo.getName();
				StringBuilder sb = new StringBuilder();

				String firstName = name.getFirstName();
				String fatherName = name.getFatherName();
				String grandFatherName = name.getGrandfatherName();
				String familyName = name.getFamilyName();

				if (firstName != null) { sb.append(firstName).append(" "); }
				if (fatherName != null) { sb.append(fatherName).append(" "); }
				if (grandFatherName != null) { sb.append(grandFatherName).append(" "); }
				if (familyName != null) { sb.append(familyName); }

				String fullName = sb.toString().stripTrailing();

				lblName.setText(fullName);
				lblPersonId.setText(AppUtils.localizeNumbers(personInfo.getSamisId().toString()));
			}

		}
		paneControlsInnerContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
		{
			paneControlsOuterContainer.requestLayout();
			return paneControlsOuterContainer.getViewportBounds().getHeight();
		}, paneControlsOuterContainer.viewportBoundsProperty()));

		// show the image placeholder if and only if there is no image
		ivCapturedFirstIrisPlaceholder.visibleProperty().bind(ivCapturedFirstIris.imageProperty().isNull()
                                                                 .or(ivSkippedFirstIris.visibleProperty().not()));
		ivCapturedSecondIrisPlaceholder.visibleProperty().bind(ivCapturedSecondIris.imageProperty().isNull()
                                                                 .or(ivSkippedSecondIris.visibleProperty().not()));

		var rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		var rightIrisLabel = resources.getString("label.rightIris");
		var leftIrisLabel = resources.getString("label.leftIris");

		if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
		if(hideStartOverButton != null) GuiUtils.showNode(btnStartOver, !hideStartOverButton);
		if (showSkipButton != null) { GuiUtils.showNode(btnSkip, showSkipButton); }

		ivRightIris = rtl ? ivCapturedSecondIris : ivCapturedFirstIris;
		ivLeftIris = rtl ? ivCapturedFirstIris : ivCapturedSecondIris;

		ivSkippedRightIris = rtl ? ivSkippedSecondIris : ivSkippedFirstIris;
		ivSkippedLeftIris = rtl ? ivSkippedFirstIris : ivSkippedSecondIris;

		cbSkippedRightIris = rtl ? cbSkippedSecondIris : cbSkippedFirstIris;
		cbSkippedLeftIris = rtl ? cbSkippedFirstIris : cbSkippedSecondIris;

//		attachIrisLegendTooltip(tpCapturedFirstIris);
//		attachIrisLegendTooltip(tpCapturedSecondIris);

		tpCapturedRightIris = rtl ? tpCapturedSecondIris : tpCapturedFirstIris;
		tpCapturedLeftIris = rtl ? tpCapturedFirstIris : tpCapturedSecondIris ;

		tpCapturedRightIris.setText(rightIrisLabel);
		tpCapturedLeftIris.setText(leftIrisLabel);

//		tpCapturedFirstIris.setText(rtl ? leftIrisLabel : rightIrisLabel);
//		tpCapturedSecondIris.setText(rtl ? rightIrisLabel : leftIrisLabel);

		cbSkippedRightIris.disableProperty().bind(btnCaptureIris.visibleProperty().not());
		cbSkippedLeftIris.disableProperty().bind(btnCaptureIris.visibleProperty().not());

		class CustomEventHandler implements EventHandler<ActionEvent>
		{
			private String irisLabel;

			private CustomEventHandler(String irisLabel)
			{
				this.irisLabel = irisLabel;
			}

			@Override
			public void handle(ActionEvent event)
			{
				CheckBox checkBox = (CheckBox) event.getSource();
				if(checkBox.isSelected())
				{
					capturedRightIrisBase64 = null;
					capturedLeftIrisBase64 = null;
					ivCapturedFirstIris.setImage(null);
					ivCapturedSecondIris.setImage(null);
					GuiUtils.showNode(ivSuccess, false);
					GuiUtils.showNode(piProgress, false);
					GuiUtils.showNode(lblStatus, false);
					GuiUtils.showNode(btnCaptureIris, true);

					if(checkBox == cbSkippedLeftIris) GuiUtils.showNode(ivSkippedLeftIris, false);
					if(checkBox == cbSkippedRightIris) GuiUtils.showNode(ivSkippedRightIris, false);

					btnCaptureIris.setText(resources.getString("button.captureIris"));
					return;
				}

				if((checkBox == cbSkippedLeftIris && !cbSkippedRightIris.isSelected()) ||
				   (checkBox == cbSkippedRightIris && !cbSkippedLeftIris.isSelected()))
				{
					checkBox.setSelected(true);

					String message = resources.getString("iris.skippingTwoEye");
					showWarningNotification(message);
					event.consume();
					return;
				}

				@SuppressWarnings("unchecked")
				List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");

				String skipIrisRoles = Context.getConfigManager().getProperty("iris.roles.skipOneEye");

				List<String> skipIrisRolesList = Arrays.stream(skipIrisRoles.split(",")).map(String::strip).collect(Collectors.toList());

				if(!Collections.disjoint(userRoles,skipIrisRolesList)) // has permission
				{
					String headerText = resources.getString("iris.skippingOneEye.confirmation.header");
					String contentText = String.format(resources.getString("iris.skippingOneEye.confirmation.message"),
					                                   irisLabel);

					boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText,
					                                                                                contentText);

					if(confirmed)
					{
						capturedRightIrisBase64 = null;
						capturedLeftIrisBase64 = null;
						ivCapturedFirstIris.setImage(null);
						ivCapturedSecondIris.setImage(null);
						GuiUtils.showNode(ivSuccess, false);
						GuiUtils.showNode(piProgress, false);
						GuiUtils.showNode(lblStatus, false);
						GuiUtils.showNode(btnCaptureIris, true);

						if(checkBox == cbSkippedLeftIris) GuiUtils.showNode(ivSkippedLeftIris, true);
						if(checkBox == cbSkippedRightIris) GuiUtils.showNode(ivSkippedRightIris, true);

						btnCaptureIris.setText(resources.getString("button.captureIris"));
					}
					else
					{
						checkBox.setSelected(true);
						event.consume();
					}
				}
				else
				{
					checkBox.setSelected(true);

					String message = resources.getString("iris.skippingOneEye.notAuthorized");
					showWarningNotification(message);
					event.consume();
				}
			}
		}

		cbSkippedRightIris.setOnAction(new CustomEventHandler(rightIrisLabel));
		cbSkippedLeftIris.setOnAction(new CustomEventHandler(leftIrisLabel));

		btnNext.disabledProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(!newValue) btnNext.requestFocus();
		});

		// load the persisted captured iris, if any
		if(capturedRightIrisBase64 != null || capturedLeftIrisBase64 != null) showIris();

		// register a listener to the event of the devices-runner being running or not
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    if(running && !deviceManagerGadgetPaneController.isIrisScannerInitialized())
		    {
		        deviceManagerGadgetPaneController.initializeIrisScanner();
		    }
		});

		// register a listener to the event of the iris device being initialized or disconnected
		deviceManagerGadgetPaneController.setIrisScannerInitializationListener(initialized -> Platform.runLater(() ->
		{
		    GuiUtils.showNode(piProgress, false);

		    if(initialized)
		    {
		    	if(capturedRightIrisBase64 != null || capturedLeftIrisBase64 != null)
			    {
				    btnCaptureIris.setText(resources.getString("button.recaptureIris"));
			    }
		    	else btnCaptureIris.setText(resources.getString("button.captureIris"));

		        GuiUtils.showNode(btnCaptureIris, true);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.irisScannerInitializedSuccessfully"));
		        irisDeviceInitializedAtLeastOnce = true;
		        LOGGER.info("The iris scanner is initialized!");

			    btnCaptureIris.requestFocus();
		    }
		    else if(irisDeviceInitializedAtLeastOnce)
		    {
		        GuiUtils.showNode(btnCaptureIris, false);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.irisScannerDisconnected"));
		        LOGGER.info("The iris scanner is disconnected!");
		    }
		    else
		    {
			    GuiUtils.showNode(btnCaptureIris, false);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.irisScannerNotInitialized"));
		    }
		}));

		if(deviceManagerGadgetPaneController.isIrisScannerInitialized())
		{
			GuiUtils.showNode(btnCaptureIris, true);

			if(capturedRightIrisBase64 != null || capturedLeftIrisBase64 != null)
			{
				btnCaptureIris.setText(resources.getString("button.recaptureIris"));
			}
			else btnCaptureIris.setText(resources.getString("button.captureIris"));

			btnCaptureIris.requestFocus();
		}
		else if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			GuiUtils.showNode(btnCaptureIris, false);
			GuiUtils.showNode(lblStatus, true);
			lblStatus.setText(resources.getString("label.status.irisScannerNotInitialized"));
			deviceManagerGadgetPaneController.initializeIrisScanner();
		}
		else
		{
			GuiUtils.showNode(btnCaptureIris, false);
			GuiUtils.showNode(lblStatus, true);
			lblStatus.setText(resources.getString("label.status.irisScannerNotInitialized"));
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
	}

	@Override
	protected void onDetachingFromScene()
	{
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setIrisScannerInitializationListener(null);
	}

	@FXML
	private void onCaptureIrisButtonClicked(ActionEvent event)
	{
		capturedRightIrisBase64 = null;
		capturedLeftIrisBase64 = null;
		capturedRightIrisCompressedBase64 = null;
		capturedLeftIrisCompressedBase64 = null;
		ivCapturedFirstIris.setImage(null);
		ivCapturedSecondIris.setImage(null);
		tpCapturedRightIris.setActive(cbSkippedRightIris.isSelected());
		tpCapturedLeftIris.setActive(cbSkippedLeftIris.isSelected());
		tpCapturedRightIris.setCaptured(false);
		tpCapturedLeftIris.setCaptured(false);
		tpCapturedRightIris.setValid(false);
		tpCapturedLeftIris.setValid(false);
		tpCapturedRightIris.setMissing(false);
		tpCapturedLeftIris.setMissing(false);
		GuiUtils.showNode(ivSuccess, false);
		GuiUtils.showNode(piProgress, false);
		GuiUtils.showNode(btnCaptureIris, false);
		GuiUtils.showNode(lblStatus, true);
		lblStatus.setText(resources.getString("label.status.capturingTheIris"));
		LOGGER.info("capturing the iris...");

		Task<TaskResponse<CaptureIrisResponse>> capturingIrisTask = new Task<>()
		{
			@Override
			protected TaskResponse<CaptureIrisResponse> call() throws Exception
			{
				// start the real capturing
				String irisDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																	 .getIrisScannerDeviceName();

				int position = LEFT_AND_RIGHT_IRIS;
				if(!cbSkippedRightIris.isSelected()) position = LEFT_IRIS_ONLY;
				if(!cbSkippedLeftIris.isSelected()) position = RIGHT_IRIS_ONLY;

				Future<TaskResponse<CaptureIrisResponse>> future = Context.getBioKitManager()
																		  .getIrisService()
																	      .capture(irisDeviceName, position);
				return future.get();
			}
		};
		capturingIrisTask.setOnSucceeded(e ->
        {
	        GuiUtils.showNode(piProgress, false);
        	GuiUtils.showNode(btnCaptureIris, true);
        	GuiUtils.showNode(lblStatus, true);
	        btnCaptureIris.setText(resources.getString("button.recaptureIris"));

	        TaskResponse<CaptureIrisResponse> taskResponse = capturingIrisTask.getValue();

	        if(taskResponse.isSuccess())
	        {
		        CaptureIrisResponse result = taskResponse.getResult();

		        if(result.getReturnCode() == CaptureIrisResponse.SuccessCodes.SUCCESS)
		        {
			        if(cbSkippedRightIris.isSelected()) {
			        	capturedRightIrisBase64 = result.getRightIrisImageBase64();
			        	capturedRightIrisCompressedBase64 = result.getRightIrisCompressedImageBase64();
			        	String rightIrisScore = result.getRightIrisScore();
						if (rightIrisScore != null) { this.rightIrisScore = Integer.parseInt(rightIrisScore); }
						else { this.rightIrisScore = 0; }
			        }
			        if(cbSkippedLeftIris.isSelected()) {
			        	capturedLeftIrisBase64 = result.getLeftIrisImageBase64();
			        	capturedLeftIrisCompressedBase64 = result.getLeftIrisCompressedImageBase64();
			        	String leftIrisScore = result.getLeftIrisScore();
						if (leftIrisScore != null) { this.leftIrisScore = Integer.parseInt(leftIrisScore); }
						else { this.leftIrisScore = 0; }
			        }
			        showIris();
		        }
		        else
		        {
			        if(result.getReturnCode() == CaptureIrisResponse.FailureCodes.EXCEPTION_WHILE_CAPTURING)
			        {
				        lblStatus.setText(resources.getString("label.status.exceptionWhileCapturingIris"));
			        }
			        else if(result.getReturnCode() == CaptureIrisResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
			        {
				        lblStatus.setText(resources.getString("label.status.irisDeviceNotFoundOrUnplugged"));
				        GuiUtils.showNode(btnCaptureIris, false);
				        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
						                        Context.getCoreFxController().getDeviceManagerGadgetPaneController();
				        deviceManagerGadgetPaneController.initializeIrisScanner();
			        }
			        else if(result.getReturnCode() == CaptureIrisResponse.FailureCodes.DEVICE_BUSY)
			        {
				        lblStatus.setText(resources.getString("label.status.irisDeviceBusy"));
			        }
			        else
			        {
				        lblStatus.setText(String.format(
			                resources.getString("label.status.failedToCaptureIrisWithErrorCode"),
                            result.getReturnCode()));
			        }
		        }
	        }
	        else
	        {
		        lblStatus.setText(String.format(
				        resources.getString("label.status.failedToCaptureIrisWithErrorCode"),
		                                        taskResponse.getErrorCode()));

		        String errorCode = CommonsErrorCodes.C008_00056.getCode();
		        String[] errorDetails = {"failed while capturing the iris!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
	        }
        });
		capturingIrisTask.setOnFailed(e ->
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnCaptureIris, true);
			btnCaptureIris.setText(resources.getString("button.recaptureIris"));

			Throwable exception = capturingIrisTask.getException();

			if(exception instanceof ExecutionException) exception = exception.getCause();

			if(exception instanceof TimeoutException)
			{
				lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
			}
			else if(exception instanceof NotConnectedException)
			{
				lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
			}
			else if(exception instanceof CancellationException)
			{
				lblStatus.setText(resources.getString("label.status.capturingIrisCancelled"));
			}
			else
			{
				lblStatus.setText(resources.getString("label.status.failedToCaptureIris"));

				String errorCode = CommonsErrorCodes.C008_00057.getCode();
				String[] errorDetails = {"failed while capturing the iris!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			}
		});

		Context.getExecutorService().submit(capturingIrisTask);
	}

	private void showIris()
	{
		Image capturedRightIrisImage = null;
		Image capturedLeftIrisImage = null;

		if(capturedRightIrisBase64 != null) capturedRightIrisImage =
				new Image(new ByteArrayInputStream(Base64.getDecoder().decode(capturedRightIrisBase64)));
		if(capturedLeftIrisBase64 != null) capturedLeftIrisImage =
				new Image(new ByteArrayInputStream(Base64.getDecoder().decode(capturedLeftIrisBase64)));

		var contextMenuLabel = resources.getString("label.contextMenu.showImage");
		var rightIrisLabel = resources.getString("label.rightIris");
		var leftIrisLabel = resources.getString("label.leftIris");

		Boolean isRightIrisAcceptable = null;
		Boolean isLeftIrisAcceptable = null;

		if(capturedRightIrisImage != null)
		{
			ivRightIris.setImage(capturedRightIrisImage);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivRightIris, rightIrisLabel,
			                           contextMenuLabel, false);

			isRightIrisAcceptable = rightIrisScore >= rightIrisMinScore;
			checkIrisValidation(tpCapturedRightIris, rightIrisScore, isRightIrisAcceptable);

		}else {
			isRightIrisAcceptable = !cbSkippedRightIris.isSelected();
			if(!isRightIrisAcceptable)
				irisMissing(tpCapturedRightIris, rightIrisScore);
		}

		if(capturedLeftIrisImage != null)
		{
			ivLeftIris.setImage(capturedLeftIrisImage);
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivLeftIris, leftIrisLabel,
			                           contextMenuLabel, false);
			isLeftIrisAcceptable = leftIrisScore > leftIrisMinScore ;
			checkIrisValidation(tpCapturedLeftIris, leftIrisScore, isLeftIrisAcceptable);

		}else {
			isLeftIrisAcceptable = !cbSkippedLeftIris.isSelected();
			if(!isLeftIrisAcceptable)
				irisMissing(tpCapturedLeftIris, leftIrisScore);
		}

		GuiUtils.showNode(ivSuccess, isRightIrisAcceptable && isLeftIrisAcceptable);
		GuiUtils.showNode(lblStatus, true);
		lblStatus.setText(isRightIrisAcceptable && isLeftIrisAcceptable ? resources.getString("label.status.successfullyCapturedTheIris") :
                          resources.getString("label.status.theCapturedIrisHasBadQuality") );
		btnCaptureIris.setText(resources.getString("button.recaptureIris"));
	}

	private void checkIrisValidation(FourStateTitledPane titledPane, int score , boolean isAcceptable){

		titledPane.setCaptured(true);
		titledPane.setValid(isAcceptable);
		StackPane titleRegion = (StackPane) titledPane.lookup(".title");
		attachIrisResultTooltip(titleRegion,titledPane,score, isAcceptable);

	}

	// missing iris mean it's not skipped but the device can't see it ..
	private void irisMissing(FourStateTitledPane titledPane, int score){
		titledPane.setCaptured(true);
		titledPane.setMissing(true);
		StackPane titleRegion = (StackPane) titledPane.lookup(".title");
		attachIrisResultTooltip(titleRegion,titledPane,score, false);

	}


//	private void attachIrisLegendTooltip(Node targetNode)
//	{
//		TextField txtCapturingIris = new TextField(
//				resources.getString("label.iris.legend.captureInProgress"));
//		TextField txtMissingIris = new TextField(
//				resources.getString("label.iris.legend.missingIris"));
//		TextField txtLowQualityIris = new TextField(
//				resources.getString("label.iris.legend.lowQualityIris"));
//		TextField txtHighQualityIris = new TextField(
//				resources.getString("label.iris.legend.highQualityIris"));
//		TextField txtSkippedIris = new TextField(
//				resources.getString("label.iris.legend.skippedIris"));
//
//		txtCapturingIris.setEditable(false);
//		txtMissingIris.setEditable(false);
//		txtLowQualityIris.setEditable(false);
//		txtHighQualityIris.setEditable(false);
//		txtSkippedIris.setEditable(false);
//		txtCapturingIris.setFocusTraversable(false);
//		txtMissingIris.setFocusTraversable(false);
//		txtLowQualityIris.setFocusTraversable(false);
//		txtHighQualityIris.setFocusTraversable(false);
//		txtSkippedIris.setFocusTraversable(false);
//		txtCapturingIris.getStyleClass().add("legend-blue");
//		txtMissingIris.getStyleClass().add("legend-red");
//		txtLowQualityIris.getStyleClass().add("legend-yellow");
//		txtHighQualityIris.getStyleClass().add("legend-green");
//		txtSkippedIris.getStyleClass().add("legend-gray");
//		txtSkippedIris.setDisable(true);
//
//		VBox vBox = new VBox(5.0, txtCapturingIris, txtMissingIris, txtLowQualityIris,
//				txtHighQualityIris, txtSkippedIris);
//		vBox.getStylesheets().setAll("/sa/gov/nic/bio/bw/workflow/commons/css/style.css");
//		vBox.setPadding(new Insets(10.0));
//
//		// hardcoded :(
//		if(Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT)
//		{
//			vBox.setPrefWidth(210.0);
//		}
//		else vBox.setPrefWidth(263.0);
//
//		BorderPane root = new BorderPane(vBox);
//
//		PopOver popOver = new PopOver(root);
//		popOver.setDetachable(false);
//		popOver.setConsumeAutoHidingEvents(false);
//		popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
//
//		targetNode.setOnMouseClicked(event ->
//		{
//			if(popOver.isShowing()) popOver.hide();
//			else popOver.show(targetNode);
//		});
//	}

	private void attachIrisResultTooltip(Node sourceNode, Node targetNode, int score , boolean isAcceptable){

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10.0));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);

		Label lblScore = new Label(resources.getString("label.tooltip.score"));

		Image successImage = new Image(CommonImages.ICON_SUCCESS_16PX.getAsInputStream());
		Image warningImage = new Image(CommonImages.ICON_WARNING_16PX.getAsInputStream());
//		Image errorImage = new Image(CommonImages.ICON_ERROR_16PX.getAsInputStream());

		lblScore.setGraphic(new ImageView(isAcceptable ? successImage : warningImage));

		gridPane.add(lblScore, 0, 0);

		String sScore = AppUtils.localizeNumbers(String.valueOf(score))+ "%";

		TextField txtScore = new TextField(sScore);

		txtScore.setFocusTraversable(false);
		txtScore.setEditable(false);
		txtScore.setPrefColumnCount(3);

		gridPane.add(txtScore, 1, 0);

		PopOver popOver = new PopOver(gridPane);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

		sourceNode.setOnMouseClicked(mouseEvent ->
		{
			if(popOver.isShowing()) popOver.hide();
			else popOver.show(targetNode);
		});

	}

	@FXML
	private void onSkipButtonClicked() {
		capturedRightIrisBase64 = null;
		capturedLeftIrisBase64 = null;
		capturedRightIrisCompressedBase64 = null;
		capturedLeftIrisCompressedBase64 = null;
		goNext();

	}
}