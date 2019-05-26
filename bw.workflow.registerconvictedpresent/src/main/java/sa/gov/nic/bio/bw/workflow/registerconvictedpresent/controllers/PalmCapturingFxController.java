package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.biokit.ResponseProcessor;
import sa.gov.nic.bio.biokit.beans.LivePreviewingResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.fingerprint.beans.CaptureFingerprintResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.DuplicatedFingerprintsResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.FingerprintStopPreviewResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FingerprintDeviceType;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.PalmUiComponents;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@FxmlFile("palmCapturing.fxml")
public class PalmCapturingFxController extends WizardStepFxControllerBase
{
	@Input private Boolean hidePreviousButton;
	@Output private Map<Integer, Fingerprint> capturedPalm;
	@Output private List<Finger> palms;
	@Output private Map<Integer, String> palmBase64Images;
	
	@FXML private VBox paneControlsInnerContainer;
	@FXML private ScrollPane paneControlsOuterContainer;
	@FXML private AutoScalingStackPane spRightHand;
	@FXML private ProgressIndicator piProgress;
	@FXML private ProgressIndicator piFingerprintDeviceLivePreview;
	@FXML private Label lblStatus;
	@FXML private TitledPane tpRightHand;
	@FXML private TitledPane tpLeftHand;
	@FXML private SplitPane spFingerprints;
	@FXML private ImageView ivCompleted;
	@FXML private ImageView ivFingerprintDeviceLivePreviewPlaceholder;
	@FXML private ImageView ivLeftWritersPalmPlaceholder;
	@FXML private ImageView ivLeftLowerPalmPlaceholder;
	@FXML private ImageView ivRightWritersPalmPlaceholder;
	@FXML private ImageView ivRightLowerPalmPlaceholder;
	@FXML private ImageView ivLeftWritersPalmSkip;
	@FXML private ImageView ivLeftLowerPalmSkip;
	@FXML private ImageView ivRightWritersPalmSkip;
	@FXML private ImageView ivRightLowerPalmSkip;
	@FXML private ImageView ivFingerprintDeviceLivePreview;
	@FXML private ImageView ivRightWritersPalm;
	@FXML private ImageView ivRightLowerPalm;
	@FXML private ImageView ivLeftWritersPalm;
	@FXML private ImageView ivLeftLowerPalm;
	@FXML private SVGPath svgRightWritersPalm;
	@FXML private SVGPath svgRightLowerPalm;
	@FXML private SVGPath svgLeftWritersPalm;
	@FXML private SVGPath svgLeftLowerPalm;
	@FXML private FourStateTitledPane tpFingerprintDeviceLivePreview;
	@FXML private FourStateTitledPane tpRightWritersPalm;
	@FXML private FourStateTitledPane tpRightLowerPalm;
	@FXML private FourStateTitledPane tpLeftWritersPalm;
	@FXML private FourStateTitledPane tpLeftLowerPalm;
	@FXML private CheckBox cbRightWritersPalm;
	@FXML private CheckBox cbRightLowerPalm;
	@FXML private CheckBox cbLeftWritersPalm;
	@FXML private CheckBox cbLeftLowerPalm;
	@FXML private Button btnCancel;
	@FXML private Button btnStartFingerprintCapturing;
	@FXML private Button btnStopFingerprintCapturing;
	@FXML private Button btnStartOverFingerprintCapturing;
	@FXML private Button btnRightHandLegend;
	@FXML private Button btnLeftHandLegend;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private Map<Integer, PalmUiComponents> palmUiComponentsMap = new HashMap<>();
	private int currentPalmPosition = FingerPosition.RIGHT_LOWER_PALM.getPosition();
	private boolean fingerprintDeviceInitializedAtLeastOnce = false;
	private boolean workflowStarted = false;
	private boolean workflowUserTaskLoaded = false;
	private AtomicBoolean stopCapturingIsInProgress = new AtomicBoolean();
	
	@Override
	protected void onAttachedToScene()
	{
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setNextFingerprintDeviceType(FingerprintDeviceType.PALM);
		
		btnNext.disableProperty().bind(ivCompleted.visibleProperty().not());
		
		paneControlsInnerContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
		{
			paneControlsOuterContainer.requestLayout();
			return paneControlsOuterContainer.getViewportBounds().getHeight();
		}, paneControlsOuterContainer.viewportBoundsProperty()));
		
		Glyph glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
		btnRightHandLegend.setGraphic(glyph);
		glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
		btnLeftHandLegend.setGraphic(glyph);
		
		attachFingerprintLegendTooltip(btnRightHandLegend);
		attachFingerprintLegendTooltip(btnLeftHandLegend);
		
		// reverse the orientation so that the button will appear next to the text
		NodeOrientation reverse = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT ?
														NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT;
		tpRightHand.setNodeOrientation(reverse);
		tpLeftHand.setNodeOrientation(reverse);
		
		// show the image placeholder if and only if there is no image and the checkbox is selected
		ivRightLowerPalmPlaceholder.visibleProperty().bind(ivRightLowerPalm.imageProperty().isNull()
			                                                            .and(cbRightLowerPalm.selectedProperty()));
		ivRightWritersPalmPlaceholder.visibleProperty().bind(ivRightWritersPalm.imageProperty().isNull()
				                                                        .and(cbRightWritersPalm.selectedProperty()));
		ivLeftLowerPalmPlaceholder.visibleProperty().bind(ivLeftLowerPalm.imageProperty().isNull()
			                                                            .and(cbLeftLowerPalm.selectedProperty()));
		ivLeftWritersPalmPlaceholder.visibleProperty().bind(ivLeftWritersPalm.imageProperty().isNull()
				                                                        .and(cbLeftWritersPalm.selectedProperty()));
		
		// show the skip icon if and only if the checkbox is not selected
		ivRightLowerPalmSkip.visibleProperty().bind(cbRightLowerPalm.selectedProperty().not());
		ivRightWritersPalmSkip.visibleProperty().bind(cbRightWritersPalm.selectedProperty().not());
		ivLeftLowerPalmSkip.visibleProperty().bind(cbLeftLowerPalm.selectedProperty().not());
		ivLeftWritersPalmSkip.visibleProperty().bind(cbLeftWritersPalm.selectedProperty().not());
		
		// show the image placeholder if and only if there is no image and the progress is not visible
		ivFingerprintDeviceLivePreviewPlaceholder.visibleProperty().bind(ivFingerprintDeviceLivePreview.imageProperty()
                                                .isNull().and(piFingerprintDeviceLivePreview.visibleProperty().not()));
		
		// accumulate all the fingerprint-related components in a map so that we can apply actions on them easily
		palmUiComponentsMap.put(FingerPosition.RIGHT_LOWER_PALM.getPosition(),
		                        new PalmUiComponents(FingerPosition.RIGHT_LOWER_PALM, ivRightLowerPalm,
                                                     svgRightLowerPalm, tpRightLowerPalm, cbRightLowerPalm,
                                                     resources.getString("label.lowerPalm"),
                                                     resources.getString("label.rightHand")));
		palmUiComponentsMap.put(FingerPosition.RIGHT_WRITERS_PALM.getPosition(),
		                        new PalmUiComponents(FingerPosition.RIGHT_WRITERS_PALM, ivRightWritersPalm,
	                                                 svgRightWritersPalm, tpRightWritersPalm, cbRightWritersPalm,
                                                     resources.getString("label.writersPalm"),
                                                     resources.getString("label.rightHand")));
		palmUiComponentsMap.put(FingerPosition.LEFT_LOWER_PALM.getPosition(),
		                        new PalmUiComponents(FingerPosition.LEFT_LOWER_PALM, ivLeftLowerPalm,
                                                     svgLeftLowerPalm, tpLeftLowerPalm, cbLeftLowerPalm,
                                                     resources.getString("label.lowerPalm"),
                                                     resources.getString("label.leftHand")));
		palmUiComponentsMap.put(FingerPosition.LEFT_WRITERS_PALM.getPosition(),
		                        new PalmUiComponents(FingerPosition.LEFT_WRITERS_PALM, ivLeftWritersPalm,
		                                             svgLeftWritersPalm, tpLeftWritersPalm, cbLeftWritersPalm,
		                                             resources.getString("label.writersPalm"),
		                                             resources.getString("label.leftHand")));
		
		// disable the finger's titledPane whenever its checkbox is unselected
		palmUiComponentsMap.forEach((position, components) ->
               components.getTitledPane().disableProperty().bind(components.getCheckBox().selectedProperty().not()));
		
		// disable the finger's vector hand highlight whenever its checkbox is unselected
		palmUiComponentsMap.forEach((position, components) ->
               components.getSvgPath().disableProperty().bind(components.getCheckBox().selectedProperty().not()));
		
		class CustomEventHandler implements EventHandler<ActionEvent>
		{
			private PalmUiComponents components;
			
			private CustomEventHandler(PalmUiComponents components)
			{
				this.components = components;
			}
			
			@Override
			public void handle(ActionEvent event)
			{
				CheckBox checkBox = (CheckBox) event.getSource();
				if(checkBox.isSelected()) return;
				
				String headerText = resources.getString("fingerprint.skippingFingerprint.confirmation.header");
				String contentText = String.format(
						resources.getString("fingerprint.skippingFingerprint.confirmation.message"),
						components.getFingerLabel(), components.getHandLabel());
				
				boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText,
				                                                                                contentText);
				if(!confirmed)
				{
					components.getCheckBox().setSelected(true);
					event.consume();
				}
			}
		}
		
		cbRightLowerPalm.setOnAction(new CustomEventHandler(palmUiComponentsMap.get(
																FingerPosition.RIGHT_LOWER_PALM.getPosition())));
		cbRightWritersPalm.setOnAction(new CustomEventHandler(palmUiComponentsMap.get(
																FingerPosition.RIGHT_WRITERS_PALM.getPosition())));
		cbLeftLowerPalm.setOnAction(new CustomEventHandler(palmUiComponentsMap.get(
																FingerPosition.LEFT_LOWER_PALM.getPosition())));
		cbLeftWritersPalm.setOnAction(new CustomEventHandler(palmUiComponentsMap.get(
																FingerPosition.LEFT_WRITERS_PALM.getPosition())));
		
		class CustomChangeListener implements ChangeListener<Boolean>
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				if(!workflowUserTaskLoaded) return;
				
				if(cbRightLowerPalm.isSelected())
				{
					btnStartFingerprintCapturing.setText(
												resources.getString("button.captureRightLowerPalmFingerprint"));
					currentPalmPosition = FingerPosition.RIGHT_LOWER_PALM.getPosition();
				}
				else if(cbRightWritersPalm.isSelected())
				{
					btnStartFingerprintCapturing.setText(
												resources.getString("button.captureRightWritersPalmFingerprint"));
					currentPalmPosition = FingerPosition.RIGHT_WRITERS_PALM.getPosition();
				}
				else if(cbLeftLowerPalm.isSelected())
				{
					btnStartFingerprintCapturing.setText(
												resources.getString("button.captureLeftLowerPalmFingerprint"));
					currentPalmPosition = FingerPosition.LEFT_LOWER_PALM.getPosition();
				}
				else if(cbLeftWritersPalm.isSelected())
				{
					btnStartFingerprintCapturing.setText(
												resources.getString("button.captureLeftWritersPalmFingerprint"));
					currentPalmPosition = FingerPosition.LEFT_WRITERS_PALM.getPosition();
				}
				else
				{
					btnStartFingerprintCapturing.setText(resources.getString("button.skipAllFingerprints"));
					currentPalmPosition = -1; // completed
				}
				
				// activate/deactivate the fingerprint based on whether it is skipped or not
				palmUiComponentsMap.forEach((position, components) ->
				{
					if(components.getPalmPosition() == FingerPosition.RIGHT_LOWER_PALM)
									components.getTitledPane().setActive(!cbRightLowerPalm.isSelected());
					else if(components.getPalmPosition() == FingerPosition.RIGHT_WRITERS_PALM)
									components.getTitledPane().setActive(!cbRightWritersPalm.isSelected());
					else if(components.getPalmPosition() == FingerPosition.LEFT_LOWER_PALM)
									components.getTitledPane().setActive(!cbLeftLowerPalm.isSelected());
					else if(components.getPalmPosition() == FingerPosition.LEFT_WRITERS_PALM)
									components.getTitledPane().setActive(!cbLeftWritersPalm.isSelected());
				});
			}
		}
		
		cbRightLowerPalm.selectedProperty().addListener(new CustomChangeListener());
		cbRightWritersPalm.selectedProperty().addListener(new CustomChangeListener());
		cbLeftLowerPalm.selectedProperty().addListener(new CustomChangeListener());
		cbLeftWritersPalm.selectedProperty().addListener(new CustomChangeListener());
		
		btnNext.disabledProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(!newValue) btnNext.requestFocus();
		});
		
		if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
		
		// load the persisted captured fingerprints, if any
		if(capturedPalm != null && !capturedPalm.isEmpty())
		{
			workflowStarted = true;
			
			// disable all the checkboxes
			palmUiComponentsMap.forEach((position, components) -> components.getCheckBox().setDisable(true));
			int[] skippedFingersCount = {0};
			
			capturedPalm.forEach((position, fingerprint) ->
			{
			    PalmUiComponents components = palmUiComponentsMap.get(position);
			    currentPalmPosition = Math.max(currentPalmPosition, components.getPalmPosition().getPosition());
			
			    if(fingerprint.isSkipped())
			    {
			        skippedFingersCount[0]++;
			        components.getCheckBox().setSelected(false);
			        return;
			    }
			
			    // show fingerprint
			    showFingerprint(fingerprint, components.getImageView(), components.getTitledPane(),
			                    components.getHandLabel(), components.getFingerLabel());
			    components.getTitledPane().setActive(true);
			    components.getTitledPane().setCaptured(true);
			    components.getTitledPane().setValid(true);
			});
			
			// increment to the next palm position
			if(currentPalmPosition == FingerPosition.RIGHT_LOWER_PALM.getPosition())
									currentPalmPosition = FingerPosition.RIGHT_WRITERS_PALM.getPosition();
			else if(currentPalmPosition == FingerPosition.RIGHT_WRITERS_PALM.getPosition())
									currentPalmPosition = FingerPosition.LEFT_LOWER_PALM.getPosition();
			else if(currentPalmPosition == FingerPosition.LEFT_LOWER_PALM.getPosition())
									currentPalmPosition = FingerPosition.LEFT_WRITERS_PALM.getPosition();
			else if(currentPalmPosition == FingerPosition.LEFT_WRITERS_PALM.getPosition())
									currentPalmPosition = -1; // completed
			
			// update the controls based on the current slap position
			if(currentPalmPosition == FingerPosition.RIGHT_LOWER_PALM.getPosition())
			{
				btnStartFingerprintCapturing.setText(
						resources.getString("button.captureRightLowerPalmFingerprint"));
			}
			else if(currentPalmPosition == FingerPosition.RIGHT_WRITERS_PALM.getPosition())
			{
				btnStartFingerprintCapturing.setText(
						resources.getString("button.captureRightWritersPalmFingerprint"));
			}
			else if(currentPalmPosition == FingerPosition.LEFT_LOWER_PALM.getPosition())
			{
				btnStartFingerprintCapturing.setText(
						resources.getString("button.captureLeftLowerPalmFingerprint"));
			}
			else if(currentPalmPosition == FingerPosition.LEFT_WRITERS_PALM.getPosition())
			{
				btnStartFingerprintCapturing.setText(
						resources.getString("button.captureLeftWritersPalmFingerprint"));
			}
			else
			{
				GuiUtils.showNode(btnStartFingerprintCapturing, false);
				GuiUtils.showNode(ivCompleted, true);
				GuiUtils.showNode(lblStatus, true);
				
				if(skippedFingersCount[0] == 4) lblStatus.setText(
													resources.getString("label.status.allFingerprintsAreSkipped"));
				else lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
			}
		}
		else capturedPalm = new HashMap<>();
		
		// register a listener to the event of the devices-runner being running or not
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    if(running &&
		            !deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.PALM))
		    {
		        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.PALM);
		    }
		});
		
		// register a listener to the event of the fingerprint device being initialized or disconnected
		deviceManagerGadgetPaneController.setFingerprintScannerInitializationListener(
																				initialized -> Platform.runLater(() ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStopFingerprintCapturing, false);
		    tpFingerprintDeviceLivePreview.setActive(false);
		    ivFingerprintDeviceLivePreview.setImage(null);
		
		    if(initialized)
		    {
		        if(currentPalmPosition > 0) GuiUtils.showNode(btnStartFingerprintCapturing, true);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.fingerprintScannerInitializedSuccessfully"));
		        activateFingerIndicatorsForNextCapturing(currentPalmPosition);
		        GuiUtils.showNode(btnStartOverFingerprintCapturing, workflowStarted);
		        fingerprintDeviceInitializedAtLeastOnce = true;
		        LOGGER.info("The fingerprint scanner is initialized!");
		    }
		    else if(fingerprintDeviceInitializedAtLeastOnce)
		    {
		        GuiUtils.showNode(btnStartFingerprintCapturing, false);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.fingerprintScannerDisconnected"));
		        LOGGER.info("The fingerprint scanner is disconnected!");
		    }
		    else
		    {
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
		    }
		}));
		
		// prepare for next fingerprint capturing if the fingerprint device is connected and initialized, otherwise
		// auto-run and auto-initialize as configured
		if(deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.PALM))
		{
			if(currentPalmPosition > 0) GuiUtils.showNode(btnStartFingerprintCapturing, true);
			activateFingerIndicatorsForNextCapturing(currentPalmPosition);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, workflowStarted);
		}
		else if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.PALM);
		}
		else
		{
			GuiUtils.showNode(lblStatus, true);
			lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
		
		workflowUserTaskLoaded = true;
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		Platform.runLater(btnStopFingerprintCapturing::fire);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController()
									 .setFingerprintScannerInitializationListener(null);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(!workflowStarted) return;
		
		if(btnStopFingerprintCapturing.isVisible() || piProgress.isVisible())
		{
			Platform.runLater(btnStopFingerprintCapturing::fire);
		}
		
		prepareFingerprintsForBackend();
	}
	
	private void prepareFingerprintsForBackend()
	{
		palms = new ArrayList<>();
		palmBase64Images = new HashMap<>();
		
		capturedPalm.forEach((position, fingerprint) ->
		{
		    DMFingerData fingerData = fingerprint.getDmFingerData();
		    if(fingerData == null) return; // skipped fingerprint
			
		    palmBase64Images.put(position, fingerData.getFinger());
			palms.add(new Finger(position, fingerData.getFingerWsqImage(), null));
		});
		
		LOGGER.fine(AppUtils.toJson(palms));
	}
	
	@FXML
	private void onStartFingerprintCapturingButtonClicked(ActionEvent event)
	{
		workflowStarted = true;
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(lblStatus, true);
		
		// disable all the checkboxes
		palmUiComponentsMap.forEach((position, components) -> components.getCheckBox().setDisable(true));
		
		if(!cbRightLowerPalm.isSelected() && !cbRightWritersPalm.isSelected() &&
		   !cbLeftLowerPalm.isSelected() && !cbLeftWritersPalm.isSelected())
		{
			GuiUtils.showNode(ivCompleted, true);
			lblStatus.setText(resources.getString("label.status.allFingerprintsAreSkipped"));
			return;
		}
		
		hideNotification();
		tpFingerprintDeviceLivePreview.setActive(false);
		tpFingerprintDeviceLivePreview.setCaptured(false);
		ivFingerprintDeviceLivePreview.setImage(null);
		GuiUtils.showNode(btnStartOverFingerprintCapturing, false);
		GuiUtils.showNode(btnStopFingerprintCapturing, false);
		
		palmUiComponentsMap.forEach((position, components) ->
		{
			if(components.getPalmPosition().getPosition() == currentPalmPosition)
			{
				components.getTitledPane().setCaptured(false);
				components.getTitledPane().setValid(false);
				components.getTitledPane().setDuplicated(false);
				components.getImageView().setImage(null);
			}
		});
		
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(piFingerprintDeviceLivePreview, true);
		
		// prepare the current titledPane
		palmUiComponentsMap.forEach((position, components) ->
		{
		    boolean currentPalm = currentPalmPosition == components.getPalmPosition().getPosition();
		    if(currentPalm)
		    {
			    components.getTitledPane().setActive(true);
		        components.getTitledPane().setCaptured(false);
		        components.getTitledPane().setValid(false);
		        components.getTitledPane().setDuplicated(false);
			    components.getImageView().setImage(null);
		    }
		});
		
		LOGGER.info("capturing the fingerprints (position = " + currentPalmPosition + ")...");
		lblStatus.setText(resources.getString("label.status.waitingDeviceResponse"));
		boolean[] firstLivePreviewingResponse = {true};
		
		Task<TaskResponse<CaptureFingerprintResponse>> capturingFingerprintTask = new Task<>()
		{
			@Override
			protected TaskResponse<CaptureFingerprintResponse> call() throws Exception
			{
				// the processor that will process every frame of the live previewing
				ResponseProcessor<LivePreviewingResponse> responseProcessor = response -> Platform.runLater(() ->
				{
					if(firstLivePreviewingResponse[0])
					{
						firstLivePreviewingResponse[0] = false;
						lblStatus.setText(resources.getString("label.status.capturingFingerprints"));
						
						GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
						GuiUtils.showNode(piProgress, false);
						GuiUtils.showNode(btnStopFingerprintCapturing, true);
						tpFingerprintDeviceLivePreview.setActive(true);
						ivFingerprintDeviceLivePreview.setOnMouseClicked(null);
						ivFingerprintDeviceLivePreview.setOnContextMenuRequested(null);
						
						// highlight the current palm SVG
						palmUiComponentsMap.forEach((position, components) ->
						{
						    boolean currentPalm = currentPalmPosition == components.getPalmPosition().getPosition();
						    GuiUtils.showNode(components.getSvgPath(), currentPalm);
						});
					}
					
					String previewImageBase64 = response.getPreviewImage();
					byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
					ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				// start the real capturing
				String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																			.getFingerprintScannerDeviceName();
				Future<TaskResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
						.getFingerprintService().startPreviewAndAutoCapture(fingerprintDeviceName, currentPalmPosition,
						                                                    1, new ArrayList<>(),
						                                                    true, true,
						                                                    responseProcessor);
				return future.get();
			}
		};
		capturingFingerprintTask.setOnSucceeded(e ->
        {
        	// hide all SVGs of the palms
	        palmUiComponentsMap.forEach((integer, components) ->
			                                           GuiUtils.showNode(components.getSvgPath(), false));
	        GuiUtils.showNode(btnStopFingerprintCapturing, false);
	        GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
	        
	        // get the response from the BioKit for the captured fingerprints
	        TaskResponse<CaptureFingerprintResponse> taskResponse = capturingFingerprintTask.getValue();
	        
	        if(taskResponse.isSuccess())
	        {
		        CaptureFingerprintResponse result = taskResponse.getResult();
	        	
		        if(result.getReturnCode() == CaptureFingerprintResponse.SuccessCodes.SUCCESS)
		        {
			        String capturedImageBase64 = result.getCapturedImage();
			        if(capturedImageBase64 == null) return; // it happens if we stop the preview
			        
			        tpFingerprintDeviceLivePreview.setCaptured(true);
			
			        // show the final slap image in place of the live preview image
			        byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
			        ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
			
			        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
			                                   resources.getString("label.contextMenu.slapFingerprints"),
			                                   resources.getString("label.contextMenu.showImage"), false);
		        	
			        /*if(result.isWrongSlap())
			        {
				        tpFingerprintDeviceLivePreview.setValid(false);
				        
				        if(currentPalmPosition == FingerPosition.RIGHT_SLAP.getPosition())
				        {
					        lblStatus.setText(resources.getString("label.status.notRightSlap"));
					        btnStartFingerprintCapturing.setText(
					        		        resources.getString("button.recaptureRightSlapFingerprints"));
				        }
				        else if(currentPalmPosition == FingerPosition.LEFT_SLAP.getPosition())
				        {
					        lblStatus.setText(resources.getString("label.status.notLeftSlap"));
					        btnStartFingerprintCapturing.setText(
					        		        resources.getString("button.recaptureLeftSlapFingerprints"));
				        }
				        
				        GuiUtils.showNode(btnStartFingerprintCapturing, true);
				        GuiUtils.showNode(btnAcceptCurrentSlap, true);
				        GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
				        
				        // save the captured fingerprints
				        wrongSlapCapturedFingerprints = result;
				
				        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
				                                   resources.getString("label.contextMenu.slapFingerprints"),
				                                   resources.getString("label.contextMenu.showImage"), false);
			        }
			        else*/ processCaptureFingerprintResponse(result);
		        }
		        // we skip FAILED_TO_CAPTURE_FINAL_IMAGE because it happens only when we send "stop" command
		        else
		        {
			        GuiUtils.showNode(piProgress, false);
			        GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			        GuiUtils.showNode(btnStartFingerprintCapturing, true);
			        GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
			        
			        palmUiComponentsMap.forEach((integer, components) ->
				                                           GuiUtils.showNode(components.getSvgPath(), false));
			        
			        if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.EXCEPTION_WHILE_CAPTURING)
			        {
				        lblStatus.setText(resources.getString("label.status.exceptionWhileCapturing"));
			        }
			        else if(result.getReturnCode() ==
					                            CaptureFingerprintResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
			        {
				        lblStatus.setText(resources.getString("label.status.fingerprintDeviceNotFoundOrUnplugged"));
				
				        GuiUtils.showNode(btnStartFingerprintCapturing,false);
				        GuiUtils.showNode(btnStartOverFingerprintCapturing, false);
				        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
						                        Context.getCoreFxController().getDeviceManagerGadgetPaneController();
				        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.PALM);
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.DEVICE_BUSY)
			        {
				        lblStatus.setText(resources.getString("label.status.fingerprintDeviceBusy"));
			        }
			        else if(result.getReturnCode() ==
					                        CaptureFingerprintResponse.FailureCodes.WRONG_NUMBER_OF_EXPECTED_FINGERS)
			        {
				        lblStatus.setText(resources.getString("label.status.wrongNumberOfExpectedFingers"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.SEGMENTATION_FAILED)
			        {
				        lblStatus.setText(resources.getString("label.status.segmentationFailed"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.WSQ_CONVERSION_FAILED)
			        {
				        lblStatus.setText(resources.getString("label.status.wsqConversionFailed"));
			        }
			        else if(result.getReturnCode() ==
				                                CaptureFingerprintResponse.FailureCodes.FAILED_TO_CAPTURE_FINAL_IMAGE)
			        {
				        if(!stopCapturingIsInProgress.get())
				        {
					        lblStatus.setText(resources.getString("label.status.failedToCaptureFinalImage"));
					
					        GuiUtils.showNode(btnStartFingerprintCapturing,false);
					        GuiUtils.showNode(btnStartOverFingerprintCapturing, false);
					        DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
							        Context.getCoreFxController().getDeviceManagerGadgetPaneController();
					        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.PALM);
				        }
			        }
			        else if(result.getReturnCode() ==
					                        CaptureFingerprintResponse.FailureCodes.EXCEPTION_IN_FINGER_HANDLER_CAPTURE)
			        {
				        lblStatus.setText(resources.getString("label.status.exceptionInFingerHandlerCapture"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.POOR_IMAGE_QUALITY)
			        {
				        lblStatus.setText(resources.getString("label.status.poorImageQuality"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.SENSOR_IS_DIRTY)
			        {
				        lblStatus.setText(resources.getString("label.status.sensorIsDirty"));
			        }
			        else if(result.getReturnCode() == CaptureFingerprintResponse.FailureCodes.CAPTURE_TIMEOUT)
			        {
				        lblStatus.setText(resources.getString("label.status.captureTimeout"));
			        }
			        else
			        {
				        lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
                            resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
                            resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
                            result.getReturnCode()));
			        }
		        	
		        }
	        }
	        else
	        {
		        GuiUtils.showNode(piProgress, false);
		        GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
		        GuiUtils.showNode(btnStartFingerprintCapturing, true);
		        GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
		        
		        palmUiComponentsMap.forEach((integer, components) ->
				                                           GuiUtils.showNode(components.getSvgPath(), false));
		        
		        lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
				        resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
				        resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
		                                        taskResponse.getErrorCode()));
		
		        String errorCode = firstLivePreviewingResponse[0] ?
				                                            RegisterConvictedPresentErrorCodes.C007_00014.getCode() :
				                                            RegisterConvictedPresentErrorCodes.C007_00015.getCode();
		        String[] errorDetails = {firstLivePreviewingResponse[0] ?
				        "failed while starting the fingerprint capturing!" :
				        "failed while capturing the fingerprints!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails);
	        }
	
	        stopCapturingIsInProgress.set(false);
        });
		capturingFingerprintTask.setOnFailed(e ->
		{
			stopCapturingIsInProgress.set(false);
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
			
			palmUiComponentsMap.forEach((integer, components) ->
					                                   GuiUtils.showNode(components.getSvgPath(), false));
			
			Throwable exception = capturingFingerprintTask.getException();
			
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
				lblStatus.setText(firstLivePreviewingResponse[0] ?
				                  resources.getString("label.status.startingFingerprintCapturingCancelled") :
					              resources.getString("label.status.capturingFingerprintsCancelled"));
			}
			else
			{
				lblStatus.setText(firstLivePreviewingResponse[0] ?
				                  resources.getString("label.status.failedToStartFingerprintCapturing") :
				                  resources.getString("label.status.failedToCaptureFingerprints"));
				
				String errorCode = firstLivePreviewingResponse[0] ?
															RegisterConvictedPresentErrorCodes.C007_00016.getCode() :
															RegisterConvictedPresentErrorCodes.C007_00017.getCode();
				String[] errorDetails = {firstLivePreviewingResponse[0] ?
										"failed while starting the fingerprint capturing!" :
										"failed while capturing the fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(capturingFingerprintTask);
	}
	
	private void processCaptureFingerprintResponse(CaptureFingerprintResponse captureFingerprintResponse)
	{
		tpFingerprintDeviceLivePreview.setValid(true);
		
		String palmWsq = captureFingerprintResponse.getCapturedWsq();
		String palmImage = captureFingerprintResponse.getCapturedImage();
		
		List<DMFingerData> fingerData = captureFingerprintResponse.getFingerData();
		Task<TaskResponse<DuplicatedFingerprintsResponse>> findDuplicatesTask = new Task<>()
		{
			@Override
			protected TaskResponse<DuplicatedFingerprintsResponse> call() throws Exception
			{
				Map<Integer, String> gallery = new HashMap<>();
				Map<Integer, String> probes = new HashMap<>();
				
				int rightLowerPalmPosition = FingerPosition.RIGHT_LOWER_PALM.getPosition();
				int rightWritersPalmPosition = FingerPosition.RIGHT_WRITERS_PALM.getPosition();
				int leftLowerPalmPosition = FingerPosition.LEFT_LOWER_PALM.getPosition();
				
				if(currentPalmPosition == FingerPosition.RIGHT_WRITERS_PALM.getPosition())
				{
					if(capturedPalm.containsKey(rightLowerPalmPosition) &&
					  !capturedPalm.get(rightLowerPalmPosition).isSkipped())
								gallery.put(rightLowerPalmPosition,
								            capturedPalm.get(rightLowerPalmPosition).getDmFingerData().getTemplate());
					
					fingerData.forEach(dmFingerData -> probes.put(dmFingerData.getPosition(),
					                                              dmFingerData.getTemplate()));
				}
				else if(currentPalmPosition == FingerPosition.LEFT_LOWER_PALM.getPosition())
				{
					if(capturedPalm.containsKey(rightLowerPalmPosition) &&
					  !capturedPalm.get(rightLowerPalmPosition).isSkipped())
								gallery.put(rightLowerPalmPosition,
						                    capturedPalm.get(rightLowerPalmPosition).getDmFingerData().getTemplate());
					
					if(capturedPalm.containsKey(rightWritersPalmPosition) &&
					  !capturedPalm.get(rightWritersPalmPosition).isSkipped())
								gallery.put(rightWritersPalmPosition,
						                    capturedPalm.get(rightWritersPalmPosition).getDmFingerData().getTemplate());
					
					fingerData.forEach(dmFingerData -> probes.put(dmFingerData.getPosition(),
					                                              dmFingerData.getTemplate()));
				}
				else if(currentPalmPosition == FingerPosition.LEFT_WRITERS_PALM.getPosition())
				{
					if(capturedPalm.containsKey(rightLowerPalmPosition) &&
					  !capturedPalm.get(rightLowerPalmPosition).isSkipped())
								gallery.put(rightLowerPalmPosition,
						                    capturedPalm.get(rightLowerPalmPosition).getDmFingerData().getTemplate());
					
					if(capturedPalm.containsKey(rightWritersPalmPosition) &&
					  !capturedPalm.get(rightWritersPalmPosition).isSkipped())
								gallery.put(rightWritersPalmPosition,
						                    capturedPalm.get(rightWritersPalmPosition).getDmFingerData().getTemplate());
					
					if(capturedPalm.containsKey(leftLowerPalmPosition) &&
					  !capturedPalm.get(leftLowerPalmPosition).isSkipped())
								gallery.put(leftLowerPalmPosition,
						                    capturedPalm.get(leftLowerPalmPosition).getDmFingerData().getTemplate());
					
					fingerData.forEach(dmFingerData -> probes.put(dmFingerData.getPosition(),
					                                              dmFingerData.getTemplate()));
				}
				
				if(gallery.isEmpty()) return null;
				else return Context.getBioKitManager().getFingerprintUtilitiesService()
													  .findDuplicatedFingerprints(gallery, probes).get();
			}
		};
		findDuplicatesTask.setOnSucceeded(e ->
		{
			GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			TaskResponse<DuplicatedFingerprintsResponse> taskResponse = findDuplicatesTask.getValue();
			
			List<Fingerprint> fingerprints = fingerData.stream()
											   .map(dmFingerData -> new Fingerprint(dmFingerData, palmWsq, palmImage))
											   .collect(Collectors.toList());
			
			if(taskResponse == null) // no duplicates
			{
				processPalmFingerprints(fingerprints, null);
			}
			else if(taskResponse.isSuccess())
			{
				DuplicatedFingerprintsResponse result = taskResponse.getResult();
				
				if(result.getReturnCode() == DuplicatedFingerprintsResponse.SuccessCodes.SUCCESS)
				{
					// it could be empty
				    Map<Integer, Boolean> duplicatedFingers = result.getDuplicatedFingers();
				    processPalmFingerprints(fingerprints, duplicatedFingers);
				}
				else
				{
					GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
					
					palmUiComponentsMap.forEach((integer, components) ->
						                                   GuiUtils.showNode(components.getSvgPath(), false));
					
					lblStatus.setText(String.format(
							resources.getString("label.status.failedToFindDuplicatedFingerprintsWithErrorCode"),
                            result.getReturnCode()));
				}
			}
			else
			{
				GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
				
				palmUiComponentsMap.forEach((integer, components) ->
					                                        GuiUtils.showNode(components.getSvgPath(), false));
				
				lblStatus.setText(String.format(
						resources.getString("label.status.failedToFindDuplicatedFingerprintsWithErrorCode"),
						taskResponse.getErrorCode()));
				
				String errorCode = RegisterConvictedPresentErrorCodes.C007_00018.getCode();
				String[] errorDetails = {"failed while finding duplicated fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails);
			}
		});
		findDuplicatesTask.setOnFailed(e ->
		{
			GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
			
			palmUiComponentsMap.forEach((integer, components) ->
					                                   GuiUtils.showNode(components.getSvgPath(), false));
			
			Throwable exception = findDuplicatesTask.getException();
			
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
				lblStatus.setText(resources.getString("label.status.findingDuplicatedFingerprintsCancelled"));
			}
			else
			{
				lblStatus.setText(resources.getString("label.status.failedToFindDuplicatedFingerprints"));
				
				String errorCode = RegisterConvictedPresentErrorCodes.C007_00019.getCode();
				String[] errorDetails = {"failed while finding duplicated fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(findDuplicatesTask);
	}
	
	@FXML
	private void onStopFingerprintCapturingButtonClicked(ActionEvent event)
	{
		LOGGER.info("stopping fingerprint capturing...");
		
		GuiUtils.showNode(btnStopFingerprintCapturing, false);
		GuiUtils.showNode(piProgress, true);
		
		ivFingerprintDeviceLivePreview.setImage(null);
		lblStatus.setText(resources.getString("label.status.stoppingFingerprintCapturing"));
		
		String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
																	.getFingerprintScannerDeviceName();
		Future<TaskResponse<FingerprintStopPreviewResponse>> future = Context.getBioKitManager()
									.getFingerprintService().cancelCapture(fingerprintDeviceName,
															               FingerPosition.RIGHT_SLAP.getPosition());
		
		Task<TaskResponse<FingerprintStopPreviewResponse>> task = new Task<>()
		{
			@Override
			protected TaskResponse<FingerprintStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			palmUiComponentsMap.forEach((integer, components) ->
					                                    GuiUtils.showNode(components.getSvgPath(), false));
			
		    tpFingerprintDeviceLivePreview.setActive(false);
			ivFingerprintDeviceLivePreview.setImage(null);
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
		
		    TaskResponse<FingerprintStopPreviewResponse> taskResponse = task.getValue();
		
		    if(taskResponse.isSuccess())
		    {
			    FingerprintStopPreviewResponse result = taskResponse.getResult();
			
			    if(result.getReturnCode() == FingerprintStopPreviewResponse.SuccessCodes.SUCCESS ||
				   result.getReturnCode() == FingerprintStopPreviewResponse.FailureCodes.NOT_CAPTURING_NOW)
		        {
		            lblStatus.setText(resources.getString("label.status.successfullyStoppedFingerprintCapturing"));
		        }
		        else
		        {
		            lblStatus.setText(String.format(
		                    resources.getString("label.status.failedToStopFingerprintCapturingWithErrorCode"),
		                    result.getReturnCode()));
		        }
		    }
		    else
		    {
		        lblStatus.setText(String.format(
				        resources.getString("label.status.failedToStopFingerprintCapturingWithErrorCode"),
				        taskResponse.getErrorCode()));
		
		        String errorCode = CommonsErrorCodes.C008_00014.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			palmUiComponentsMap.forEach((integer, components) ->
					                                   GuiUtils.showNode(components.getSvgPath(), false));
			
		    tpFingerprintDeviceLivePreview.setActive(false);
			ivFingerprintDeviceLivePreview.setImage(null);
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
			
			Throwable exception = task.getException();
		
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
		        lblStatus.setText(resources.getString("label.status.stoppingFingerprintCapturingCancelled"));
		    }
		    else
		    {
		        lblStatus.setText(resources.getString("label.status.failedToStopFingerprintCapturing"));
		
		        String errorCode = RegisterConvictedPresentErrorCodes.C007_00020.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		stopCapturingIsInProgress.set(true);
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onStartOverFingerprintCapturingButtonClicked(ActionEvent event)
	{
		String headerText = resources.getString("fingerprint.startingOver.confirmation.header");
		String contentText = resources.getString("fingerprint.startingOver.confirmation.message");
		
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		if(confirmed)
		{
			workflowStarted = false;
			hideNotification();
			tpFingerprintDeviceLivePreview.setActive(false);
			tpFingerprintDeviceLivePreview.setCaptured(false);
			ivFingerprintDeviceLivePreview.setImage(null);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, false);
			GuiUtils.showNode(btnStopFingerprintCapturing, false);
			GuiUtils.showNode(lblStatus, false);
			GuiUtils.showNode(ivCompleted, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			palmUiComponentsMap.forEach((position, components) ->
			{
				components.getImageView().setImage(null);
				components.getCheckBox().setDisable(false);
				components.getCheckBox().setSelected(true);
				components.getTitledPane().setActive(false);
				components.getTitledPane().setCaptured(false);
				components.getTitledPane().setValid(false);
				components.getTitledPane().setDuplicated(false);
			});
			
			capturedPalm.clear();
			currentPalmPosition = FingerPosition.RIGHT_SLAP.getPosition();
			activateFingerIndicatorsForNextCapturing(currentPalmPosition);
			renameCaptureFingerprintsButton(false);
		}
	}
	
	private void processPalmFingerprints(List<Fingerprint> fingerprints, Map<Integer, Boolean> duplicatedFingers)
	{
		boolean[] noDuplicates = {true};
		showPalmFingerprints(fingerprints, duplicatedFingers, noDuplicates);
		GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
		
		if(noDuplicates[0])
		{
			if(currentPalmPosition == FingerPosition.RIGHT_LOWER_PALM.getPosition())
			{
				lblStatus.setText(
						resources.getString("label.status.successfullyCapturedRightLowerPalmFingerprint"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
				
				currentPalmPosition = FingerPosition.RIGHT_WRITERS_PALM.getPosition();
				if(!cbRightWritersPalm.isSelected())
				{
					currentPalmPosition = FingerPosition.LEFT_LOWER_PALM.getPosition();
					if(!cbLeftLowerPalm.isSelected())
					{
						currentPalmPosition = FingerPosition.LEFT_WRITERS_PALM.getPosition();
						if(!cbLeftWritersPalm.isSelected())
						{
							currentPalmPosition = -1;
						}
					}
				}
				
				renameCaptureFingerprintsButton(false);
			}
			else if(currentPalmPosition == FingerPosition.RIGHT_WRITERS_PALM.getPosition())
			{
				lblStatus.setText(
						resources.getString("label.status.successfullyCapturedRightWritersPalmFingerprint"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
				
				currentPalmPosition = FingerPosition.LEFT_LOWER_PALM.getPosition();
				if(!cbLeftLowerPalm.isSelected())
				{
					currentPalmPosition = FingerPosition.LEFT_WRITERS_PALM.getPosition();
					if(!cbLeftWritersPalm.isSelected())
					{
						currentPalmPosition = -1;
					}
				}
				
				renameCaptureFingerprintsButton(false);
			}
			else if(currentPalmPosition == FingerPosition.LEFT_LOWER_PALM.getPosition())
			{
				lblStatus.setText(
						resources.getString("label.status.successfullyCapturedLeftLowerPalmFingerprint"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
				
				currentPalmPosition = FingerPosition.LEFT_WRITERS_PALM.getPosition();
				if(!cbLeftWritersPalm.isSelected())
				{
					currentPalmPosition = -1;
				}
				
				renameCaptureFingerprintsButton(false);
			}
			else
			{
				currentPalmPosition = -1;
				lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
				GuiUtils.showNode(btnStartFingerprintCapturing, false);
				GuiUtils.showNode(ivCompleted, true);
			}
		}
		else
		{
			lblStatus.setText(resources.getString("label.status.someFingerprintsAreDuplicated"));
			renameCaptureFingerprintsButton(true);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
		}
		
		activateFingerIndicatorsForNextCapturing(currentPalmPosition);
	}
	
	private void showPalmFingerprints(List<Fingerprint> fingerprints, Map<Integer, Boolean> duplicatedFingers,
	                                  boolean[] noDuplicates)
	{
		fingerprints.forEach(fingerprint ->
		{
			DMFingerData dmFingerData = fingerprint.getDmFingerData();
			
			PalmUiComponents components = palmUiComponentsMap.get(dmFingerData.getPosition());
		 
			boolean duplicated = false;
		    if(duplicatedFingers != null)
		    {
			    Boolean b = duplicatedFingers.get(dmFingerData.getPosition());
			    if(b != null && b)
			    {
				    duplicated = true;
				    components.getTitledPane().setDuplicated(true);
			    }
		    }
		
			if(noDuplicates != null) noDuplicates[0] = noDuplicates[0] && !duplicated;
			
			fingerprint.setDuplicated(duplicated);
			fingerprint.setSkipped(false);
			
			capturedPalm.put(dmFingerData.getPosition(), fingerprint);
			showFingerprint(fingerprint, components.getImageView(), components.getTitledPane(),
	                        components.getHandLabel(), components.getFingerLabel());
		});
	}
	
	private void showFingerprint(Fingerprint fingerprint, ImageView imageView, FourStateTitledPane titledPane,
	                             String handLabel, String fingerLabel)
	{
		String fingerprintImageBase64 = fingerprint.getDmFingerData().getFinger();
		byte[] fingerprintImageBytes = Base64.getDecoder().decode(fingerprintImageBase64);
		imageView.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
		
		titledPane.setActive(true);
		titledPane.setCaptured(true);
		titledPane.setValid(!fingerprint.isDuplicated());
		titledPane.setDuplicated(fingerprint.isDuplicated());
		
		StackPane titleRegion = (StackPane) titledPane.lookup(".title");
		attachFingerprintResultTooltip(titleRegion, titledPane,
		                               fingerprint.getDmFingerData().getNfiqQuality(),
		                               fingerprint.getDmFingerData().getMinutiaeCount(),
		                               fingerprint.getDmFingerData().getIntensity(),
		                               fingerprint.isDuplicated());
		
		String dialogTitle = fingerLabel + " (" + handLabel + ")";
		GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, dialogTitle,
		                           resources.getString("label.contextMenu.showImage"), false);
	}
	
	private void activateFingerIndicatorsForNextCapturing(int slapPosition)
	{
		palmUiComponentsMap.forEach((position, components) ->
		{
			boolean currentPalm = slapPosition == components.getPalmPosition().getPosition();
			
			if(!capturedPalm.containsKey(position))
			{
				boolean selected = components.getCheckBox().isSelected();
				components.getTitledPane().setActive(currentPalm && selected);
			}
		});
	}
	
	private void renameCaptureFingerprintsButton(boolean retry)
	{
		String buttonLabel = null;
		
		if(currentPalmPosition == FingerPosition.RIGHT_LOWER_PALM.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureRightLowerPalmFingerprint");
			else buttonLabel = resources.getString("button.captureRightLowerPalmFingerprint");
		}
		else if(currentPalmPosition == FingerPosition.RIGHT_WRITERS_PALM.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureRightWritersPalmFingerprint");
			else buttonLabel = resources.getString("button.captureRightWritersPalmFingerprint");
		}
		else if(currentPalmPosition == FingerPosition.LEFT_LOWER_PALM.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureLeftLowerPalmFingerprint");
			else buttonLabel = resources.getString("button.captureLeftLowerPalmFingerprint");
		}
		else if(currentPalmPosition == FingerPosition.LEFT_WRITERS_PALM.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureLeftWritersPalmFingerprint");
			else buttonLabel = resources.getString("button.captureLeftWritersPalmFingerprint");
		}
		
		btnStartFingerprintCapturing.setText(buttonLabel);
	}
	
	private void attachFingerprintResultTooltip(Node sourceNode, Node targetNode, int nfiq, int minutiaeCount,
	                                            int imageIntensity, boolean duplicated)
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10.0));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);
		
		Label lblNfiq = new Label(resources.getString("label.tooltip.nfiq"));
		Label lblMinutiaeCount = new Label(resources.getString("label.tooltip.minutiaeCount"));
		Label lblIntensity = new Label(resources.getString("label.tooltip.imageIntensity"));
		Label lblDuplicatedFingerprint = new Label(resources.getString("label.tooltip.duplicatedFinger"));
		
		Image successImage = new Image(CommonImages.ICON_SUCCESS_16PX.getAsInputStream());
		Image warningImage = new Image(CommonImages.ICON_WARNING_16PX.getAsInputStream());
		Image errorImage = new Image(CommonImages.ICON_ERROR_16PX.getAsInputStream());
		
		lblNfiq.setGraphic(new ImageView(successImage));
		lblMinutiaeCount.setGraphic(new ImageView(successImage));
		lblIntensity.setGraphic(new ImageView(successImage));
		lblDuplicatedFingerprint.setGraphic(new ImageView(duplicated ? errorImage : successImage));
		
		gridPane.add(lblNfiq, 0, 0);
		gridPane.add(lblMinutiaeCount, 0, 1);
		gridPane.add(lblIntensity, 0, 2);
		gridPane.add(lblDuplicatedFingerprint, 0, 3);
		
		String sNfiq = AppUtils.localizeNumbers(String.valueOf(nfiq));
		String sMinutiaeCount = AppUtils.localizeNumbers(String.valueOf(minutiaeCount));
		String sIntensity = AppUtils.localizeNumbers(String.valueOf(imageIntensity)) + "%";
		String sDuplicatedFingerprint = resources.getString(duplicated ? "label.tooltip.yes" : "label.tooltip.no");
		
		TextField txtNfiq = new TextField(sNfiq);
		TextField txtMinutiaeCount = new TextField(sMinutiaeCount);
		TextField txtIntensity = new TextField(sIntensity);
		TextField txtDuplicatedFingerprint = new TextField(sDuplicatedFingerprint);
		
		txtNfiq.setFocusTraversable(false);
		txtMinutiaeCount.setFocusTraversable(false);
		txtIntensity.setFocusTraversable(false);
		txtDuplicatedFingerprint.setFocusTraversable(false);
		txtNfiq.setEditable(false);
		txtMinutiaeCount.setEditable(false);
		txtIntensity.setEditable(false);
		txtDuplicatedFingerprint.setEditable(false);
		txtNfiq.setPrefColumnCount(3);
		txtMinutiaeCount.setPrefColumnCount(3);
		txtIntensity.setPrefColumnCount(3);
		txtDuplicatedFingerprint.setPrefColumnCount(3);
		
		gridPane.add(txtNfiq, 1, 0);
		gridPane.add(txtMinutiaeCount, 1, 1);
		gridPane.add(txtIntensity, 1, 2);
		gridPane.add(txtDuplicatedFingerprint, 1, 3);
		
		PopOver popOver = new PopOver(gridPane);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		
		sourceNode.setOnMouseClicked(mouseEvent ->
		{
			if(popOver.isShowing()) popOver.hide();
			else popOver.show(targetNode);
		});
	}
	
	private void attachFingerprintLegendTooltip(Node targetNode)
	{
		TextField txtCapturingFingerprint = new TextField(
										resources.getString("label.fingerprint.legend.captureInProgress"));
		TextField txtDuplicatedFingerprint = new TextField(
										resources.getString("label.fingerprint.legend.duplicatedFingerprint"));
		TextField txtLowQualityFingerprint = new TextField(
										resources.getString("label.fingerprint.legend.lowQualityFingerprint"));
		TextField txtHighQualityFingerprint = new TextField(
										resources.getString("label.fingerprint.legend.highQualityFingerprint"));
		TextField txtSkippedFingerprint = new TextField(
										resources.getString("label.fingerprint.legend.skippedFingerprint"));
		
		txtCapturingFingerprint.setEditable(false);
		txtDuplicatedFingerprint.setEditable(false);
		txtLowQualityFingerprint.setEditable(false);
		txtHighQualityFingerprint.setEditable(false);
		txtSkippedFingerprint.setEditable(false);
		txtCapturingFingerprint.setFocusTraversable(false);
		txtDuplicatedFingerprint.setFocusTraversable(false);
		txtLowQualityFingerprint.setFocusTraversable(false);
		txtHighQualityFingerprint.setFocusTraversable(false);
		txtSkippedFingerprint.setFocusTraversable(false);
		txtCapturingFingerprint.getStyleClass().add("legend-blue");
		txtDuplicatedFingerprint.getStyleClass().add("legend-red");
		txtLowQualityFingerprint.getStyleClass().add("legend-yellow");
		txtHighQualityFingerprint.getStyleClass().add("legend-green");
		txtSkippedFingerprint.getStyleClass().add("legend-gray");
		txtSkippedFingerprint.setDisable(true);
		
		VBox vBox = new VBox(5.0, txtCapturingFingerprint, txtDuplicatedFingerprint, txtLowQualityFingerprint,
		                     txtHighQualityFingerprint, txtSkippedFingerprint);
		vBox.getStylesheets().setAll("/sa/gov/nic/bio/bw/workflow/commons/css/style.css");
		vBox.setPadding(new Insets(10.0));
		
		// hardcoded :(
		if(Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT)
		{
			vBox.setPrefWidth(210.0);
		}
		else vBox.setPrefWidth(263.0);
		
		BorderPane root = new BorderPane(vBox);
		
		PopOver popOver = new PopOver(root);
		popOver.setDetachable(false);
		popOver.setConsumeAutoHidingEvents(false);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		
		targetNode.setOnMouseClicked(event ->
        {
	        if(popOver.isShowing()) popOver.hide();
	        else popOver.show(targetNode);
        });
	}
}