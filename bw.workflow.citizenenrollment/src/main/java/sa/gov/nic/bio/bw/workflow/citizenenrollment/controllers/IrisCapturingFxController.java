package sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.iris.beans.CaptureIrisResponse;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@FxmlFile("irisCapturing.fxml")
public class IrisCapturingFxController extends WizardStepFxControllerBase {
    private static final int LEFT_IRIS_ONLY = 0;
    private static final int RIGHT_IRIS_ONLY = 1;
    private static final int LEFT_AND_RIGHT_IRIS = 2;

    @Input
    private Boolean hidePreviousButton;
    @Input
    private Boolean hideStartOverButton;
    @Input
    private Boolean hideSkipButton;
    @Output
    private String capturedRightIrisBase64;
    @Output
    private String capturedLeftIrisBase64;
    @Output
    private Boolean Skip ;

    @FXML
    private VBox paneControlsInnerContainer;
    @FXML
    private ScrollPane paneControlsOuterContainer;
    @FXML
    private ProgressIndicator piProgress;
    @FXML
    private Label lblStatus;
    @FXML
    private TitledPane tpCapturedFirstIris;
    @FXML
    private TitledPane tpCapturedSecondIris;
    @FXML
    private ImageView ivSuccess;
    @FXML
    private ImageView ivSkippedFirstIris;
    @FXML
    private ImageView ivSkippedSecondIris;
    @FXML
    private ImageView ivCapturedFirstIrisPlaceholder;
    @FXML
    private ImageView ivCapturedSecondIrisPlaceholder;
    @FXML
    private ImageView ivCapturedFirstIris;
    @FXML
    private ImageView ivCapturedSecondIris;
    @FXML
    private CheckBox cbSkippedFirstIris;
    @FXML
    private CheckBox cbSkippedSecondIris;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnCaptureIris;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnStartOver;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnSkip;

    private boolean irisDeviceInitializedAtLeastOnce = false;
    private ImageView ivRightIris;
    private ImageView ivLeftIris;
    private ImageView ivSkippedRightIris;
    private ImageView ivSkippedLeftIris;
    private CheckBox cbSkippedRightIris;
    private CheckBox cbSkippedLeftIris;


    @Override
    protected void onAttachedToScene() {
        Skip = false;

        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
                Context.getCoreFxController().getDeviceManagerGadgetPaneController();

        btnNext.disableProperty().bind(ivSuccess.visibleProperty().not());

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

        if (hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
        if (hideStartOverButton != null) GuiUtils.showNode(btnStartOver, !hideStartOverButton);
        if (hideSkipButton != null) GuiUtils.showNode(btnSkip, !hideSkipButton);

        ivRightIris = rtl ? ivCapturedSecondIris : ivCapturedFirstIris;
        ivLeftIris = rtl ? ivCapturedFirstIris : ivCapturedSecondIris;

        ivSkippedRightIris = rtl ? ivSkippedSecondIris : ivSkippedFirstIris;
        ivSkippedLeftIris = rtl ? ivSkippedFirstIris : ivSkippedSecondIris;

        cbSkippedRightIris = rtl ? cbSkippedSecondIris : cbSkippedFirstIris;
        cbSkippedLeftIris = rtl ? cbSkippedFirstIris : cbSkippedSecondIris;

        tpCapturedFirstIris.setText(rtl ? leftIrisLabel : rightIrisLabel);
        tpCapturedSecondIris.setText(rtl ? rightIrisLabel : leftIrisLabel);

        cbSkippedRightIris.disableProperty().bind(btnCaptureIris.visibleProperty().not());
        cbSkippedLeftIris.disableProperty().bind(btnCaptureIris.visibleProperty().not());

        class CustomEventHandler implements EventHandler<ActionEvent> {
            private String irisLabel;

            private CustomEventHandler(String irisLabel) {
                this.irisLabel = irisLabel;
            }

            @Override
            public void handle(ActionEvent event) {
                CheckBox checkBox = (CheckBox) event.getSource();
                if (checkBox.isSelected()) {
                    capturedRightIrisBase64 = null;
                    capturedLeftIrisBase64 = null;
                    ivCapturedFirstIris.setImage(null);
                    ivCapturedSecondIris.setImage(null);
                    GuiUtils.showNode(ivSuccess, false);
                    GuiUtils.showNode(piProgress, false);
                    GuiUtils.showNode(lblStatus, false);
                    GuiUtils.showNode(btnCaptureIris, true);

                    if (checkBox == cbSkippedLeftIris) GuiUtils.showNode(ivSkippedLeftIris, false);
                    if (checkBox == cbSkippedRightIris) GuiUtils.showNode(ivSkippedRightIris, false);

                    btnCaptureIris.setText(resources.getString("button.captureIris"));
                    return;
                }

                if ((checkBox == cbSkippedLeftIris && !cbSkippedRightIris.isSelected()) ||
                        (checkBox == cbSkippedRightIris && !cbSkippedLeftIris.isSelected())) {
                    checkBox.setSelected(true);

                    String message = resources.getString("iris.skippingTwoEye");
                    showWarningNotification(message);
                    event.consume();
                    return;
                }

                @SuppressWarnings("unchecked")
                List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");

                String skipIrisRole = Context.getConfigManager().getProperty("iris.roles.skipOneEye");

                if (userRoles.contains(skipIrisRole)) // has permission
                {
                    String headerText = resources.getString("iris.skippingOneEye.confirmation.header");
                    String contentText = String.format(resources.getString("iris.skippingOneEye.confirmation.message"),
                            irisLabel);

                    boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText,
                            contentText);

                    if (confirmed) {
                        capturedRightIrisBase64 = null;
                        capturedLeftIrisBase64 = null;
                        ivCapturedFirstIris.setImage(null);
                        ivCapturedSecondIris.setImage(null);
                        GuiUtils.showNode(ivSuccess, false);
                        GuiUtils.showNode(piProgress, false);
                        GuiUtils.showNode(lblStatus, false);
                        GuiUtils.showNode(btnCaptureIris, true);

                        if (checkBox == cbSkippedLeftIris) GuiUtils.showNode(ivSkippedLeftIris, true);
                        if (checkBox == cbSkippedRightIris) GuiUtils.showNode(ivSkippedRightIris, true);

                        btnCaptureIris.setText(resources.getString("button.captureIris"));
                    } else {
                        checkBox.setSelected(true);
                        event.consume();
                    }
                } else {
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
            if (!newValue) btnNext.requestFocus();
        });

        // load the persisted captured iris, if any
        if (capturedRightIrisBase64 != null || capturedLeftIrisBase64 != null) showIris();

        // register a listener to the event of the devices-runner being running or not
        deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
        {
            if (running && !deviceManagerGadgetPaneController.isIrisScannerInitialized()) {
                deviceManagerGadgetPaneController.initializeIrisScanner();
            }
        });

        // register a listener to the event of the iris device being initialized or disconnected
        deviceManagerGadgetPaneController.setIrisScannerInitializationListener(initialized -> Platform.runLater(() ->
        {
            GuiUtils.showNode(piProgress, false);

            if (initialized) {
                if (capturedRightIrisBase64 != null || capturedLeftIrisBase64 != null) {
                    btnCaptureIris.setText(resources.getString("button.recaptureIris"));
                } else btnCaptureIris.setText(resources.getString("button.captureIris"));

                GuiUtils.showNode(btnCaptureIris, true);
                GuiUtils.showNode(lblStatus, true);
                lblStatus.setText(resources.getString("label.status.irisScannerInitializedSuccessfully"));
                irisDeviceInitializedAtLeastOnce = true;
                LOGGER.info("The iris scanner is initialized!");

                btnCaptureIris.requestFocus();
            } else if (irisDeviceInitializedAtLeastOnce) {
                GuiUtils.showNode(btnCaptureIris, false);
                GuiUtils.showNode(lblStatus, true);
                lblStatus.setText(resources.getString("label.status.irisScannerDisconnected"));
                LOGGER.info("The iris scanner is disconnected!");
            } else {
                GuiUtils.showNode(btnCaptureIris, false);
                GuiUtils.showNode(lblStatus, true);
                lblStatus.setText(resources.getString("label.status.irisScannerNotInitialized"));
            }
        }));

        if (deviceManagerGadgetPaneController.isIrisScannerInitialized()) {
            GuiUtils.showNode(btnCaptureIris, true);

            if (capturedRightIrisBase64 != null || capturedLeftIrisBase64 != null) {
                btnCaptureIris.setText(resources.getString("button.recaptureIris"));
            } else btnCaptureIris.setText(resources.getString("button.captureIris"));

            btnCaptureIris.requestFocus();
        } else if (deviceManagerGadgetPaneController.isDevicesRunnerRunning()) {
            GuiUtils.showNode(btnCaptureIris, false);
            GuiUtils.showNode(lblStatus, true);
            lblStatus.setText(resources.getString("label.status.irisScannerNotInitialized"));
            deviceManagerGadgetPaneController.initializeIrisScanner();
        } else {
            GuiUtils.showNode(btnCaptureIris, false);
            GuiUtils.showNode(lblStatus, true);
            lblStatus.setText(resources.getString("label.status.irisScannerNotInitialized"));
            deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
        }
    }

    @Override
    protected void onDetachingFromScene() {
        Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
        Context.getCoreFxController().getDeviceManagerGadgetPaneController().setIrisScannerInitializationListener(null);
    }

    @FXML
    private void onCaptureIrisButtonClicked(ActionEvent event) {
        capturedRightIrisBase64 = null;
        capturedLeftIrisBase64 = null;
        ivCapturedFirstIris.setImage(null);
        ivCapturedSecondIris.setImage(null);
        GuiUtils.showNode(ivSuccess, false);
        GuiUtils.showNode(piProgress, false);
        GuiUtils.showNode(btnCaptureIris, false);
        GuiUtils.showNode(lblStatus, true);
        lblStatus.setText(resources.getString("label.status.capturingTheIris"));
        LOGGER.info("capturing the iris...");

        Task<TaskResponse<CaptureIrisResponse>> capturingIrisTask = new Task<>() {
            @Override
            protected TaskResponse<CaptureIrisResponse> call() throws Exception {
                // start the real capturing
                String irisDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
                        .getIrisScannerDeviceName();

                int position = LEFT_AND_RIGHT_IRIS;
                if (!cbSkippedRightIris.isSelected()) position = LEFT_IRIS_ONLY;
                if (!cbSkippedLeftIris.isSelected()) position = RIGHT_IRIS_ONLY;

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

            if (taskResponse.isSuccess()) {
                CaptureIrisResponse result = taskResponse.getResult();

                if (result.getReturnCode() == CaptureIrisResponse.SuccessCodes.SUCCESS) {
                    if (cbSkippedRightIris.isSelected()) capturedRightIrisBase64 = result.getRightIrisImageBase64();
                    if (cbSkippedLeftIris.isSelected()) capturedLeftIrisBase64 = result.getLeftIrisImageBase64();
                    showIris();
                } else {
                    if (result.getReturnCode() == CaptureIrisResponse.FailureCodes.EXCEPTION_WHILE_CAPTURING) {
                        lblStatus.setText(resources.getString("label.status.exceptionWhileCapturingIris"));
                    } else if (result.getReturnCode() == CaptureIrisResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED) {
                        lblStatus.setText(resources.getString("label.status.irisDeviceNotFoundOrUnplugged"));
                        GuiUtils.showNode(btnCaptureIris, false);
                        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
                                Context.getCoreFxController().getDeviceManagerGadgetPaneController();
                        deviceManagerGadgetPaneController.initializeIrisScanner();
                    } else if (result.getReturnCode() == CaptureIrisResponse.FailureCodes.DEVICE_BUSY) {
                        lblStatus.setText(resources.getString("label.status.irisDeviceBusy"));
                    } else {
                        lblStatus.setText(String.format(
                                resources.getString("label.status.failedToCaptureIrisWithErrorCode"),
                                result.getReturnCode()));
                    }
                }
            } else {
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

            if (exception instanceof ExecutionException) exception = exception.getCause();

            if (exception instanceof TimeoutException) {
                lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
            } else if (exception instanceof NotConnectedException) {
                lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
            } else if (exception instanceof CancellationException) {
                lblStatus.setText(resources.getString("label.status.capturingIrisCancelled"));
            } else {
                lblStatus.setText(resources.getString("label.status.failedToCaptureIris"));

                String errorCode = CommonsErrorCodes.C008_00057.getCode();
                String[] errorDetails = {"failed while capturing the iris!"};
                Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
            }
        });

        Context.getExecutorService().submit(capturingIrisTask);
    }

    private void showIris() {
        Image capturedRightIrisImage = null;
        Image capturedLeftIrisImage = null;

        if (capturedRightIrisBase64 != null) capturedRightIrisImage =
                new Image(new ByteArrayInputStream(Base64.getDecoder().decode(capturedRightIrisBase64)));
        if (capturedLeftIrisBase64 != null) capturedLeftIrisImage =
                new Image(new ByteArrayInputStream(Base64.getDecoder().decode(capturedLeftIrisBase64)));

        var contextMenuLabel = resources.getString("label.contextMenu.showImage");
        var rightIrisLabel = resources.getString("label.rightIris");
        var leftIrisLabel = resources.getString("label.leftIris");

        if (capturedRightIrisImage != null) {
            ivRightIris.setImage(capturedRightIrisImage);
            GuiUtils.attachImageDialog(Context.getCoreFxController(), ivRightIris, rightIrisLabel,
                    contextMenuLabel, false);
        }
        if (capturedLeftIrisImage != null) {
            ivLeftIris.setImage(capturedLeftIrisImage);
            GuiUtils.attachImageDialog(Context.getCoreFxController(), ivLeftIris, leftIrisLabel,
                    contextMenuLabel, false);
        }

        GuiUtils.showNode(lblStatus, true);
        GuiUtils.showNode(ivSuccess, true);
        lblStatus.setText(resources.getString("label.status.successfullyCapturedTheIris"));
        btnCaptureIris.setText(resources.getString("button.recaptureIris"));
    }


    @FXML
    private void onSkipButtonClicked() {
        Skip = true;
        goNext();

    }
}