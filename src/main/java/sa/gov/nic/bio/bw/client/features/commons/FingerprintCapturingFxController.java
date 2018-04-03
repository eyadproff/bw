package sa.gov.nic.bio.bw.client.features.commons;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.fingerprint.beans.CaptureFingerprintResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.DuplicatedFingerprintsResponse;
import sa.gov.nic.bio.biokit.fingerprint.beans.FingerprintStopPreviewResponse;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.client.core.beans.FingerprintQualityThreshold;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.beans.FingerprintUiComponents;
import sa.gov.nic.bio.bw.client.features.commons.ui.AutoScalingStackPane;
import sa.gov.nic.bio.bw.client.features.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.client.features.commons.utils.CommonsErrorCodes;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class FingerprintCapturingFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(FingerprintCapturingFxController.class.getName());
	
	public static final String KEY_HIDE_PREVIOUS_BUTTON = "HIDE_PREVIOUS_BUTTON";
	public static final String KEY_ACCEPT_BAD_QUALITY_FINGERPRINT = "ACCEPT_BAD_QUALITY_FINGERPRINT";
	public static final String KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES =
																		"ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES";
	
	private static final String KEY_CAPTURED_FINGERPRINTS = "CAPTURED_FINGERPRINTS";
	
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
	@FXML private ImageView ivLeftLittlePlaceholder;
	@FXML private ImageView ivLeftRingPlaceholder;
	@FXML private ImageView ivLeftMiddlePlaceholder;
	@FXML private ImageView ivLeftIndexPlaceholder;
	@FXML private ImageView ivLeftThumbPlaceholder;
	@FXML private ImageView ivRightThumbPlaceholder;
	@FXML private ImageView ivRightIndexPlaceholder;
	@FXML private ImageView ivRightMiddlePlaceholder;
	@FXML private ImageView ivRightRingPlaceholder;
	@FXML private ImageView ivRightLittlePlaceholder;
	@FXML private ImageView ivFingerprintDeviceLivePreview;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivLeftLittle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftThumb;
	@FXML private SVGPath svgRightLittle;
	@FXML private SVGPath svgRightRing;
	@FXML private SVGPath svgRightMiddle;
	@FXML private SVGPath svgRightIndex;
	@FXML private SVGPath svgRightThumb;
	@FXML private SVGPath svgLeftLittle;
	@FXML private SVGPath svgLeftRing;
	@FXML private SVGPath svgLeftMiddle;
	@FXML private SVGPath svgLeftIndex;
	@FXML private SVGPath svgLeftThumb;
	@FXML private FourStateTitledPane tpFingerprintDeviceLivePreview;
	@FXML private FourStateTitledPane tpRightLittle;
	@FXML private FourStateTitledPane tpRightRing;
	@FXML private FourStateTitledPane tpRightMiddle;
	@FXML private FourStateTitledPane tpRightIndex;
	@FXML private FourStateTitledPane tpRightThumb;
	@FXML private FourStateTitledPane tpLeftLittle;
	@FXML private FourStateTitledPane tpLeftRing;
	@FXML private FourStateTitledPane tpLeftMiddle;
	@FXML private FourStateTitledPane tpLeftIndex;
	@FXML private FourStateTitledPane tpLeftThumb;
	@FXML private CheckBox cbRightLittle;
	@FXML private CheckBox cbRightRing;
	@FXML private CheckBox cbRightMiddle;
	@FXML private CheckBox cbRightIndex;
	@FXML private CheckBox cbRightThumb;
	@FXML private CheckBox cbLeftLittle;
	@FXML private CheckBox cbLeftRing;
	@FXML private CheckBox cbLeftMiddle;
	@FXML private CheckBox cbLeftIndex;
	@FXML private CheckBox cbLeftThumb;
	@FXML private Button btnCancel;
	@FXML private Button btnStartFingerprintCapturing;
	@FXML private Button btnStopFingerprintCapturing;
	@FXML private Button btnAcceptCurrentSlap;
	@FXML private Button btnAcceptCurrentFingerprints;
	@FXML private Button btnStartOver;
	@FXML private Button btnLivePreviewLegend;
	@FXML private Button btnRightHandLegend;
	@FXML private Button btnLeftHandLegend;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	
	private int currentSlapPosition = FingerPosition.RIGHT_SLAP.getPosition();
	private Map<Integer, Fingerprint> capturedFingerprints = new HashMap<>();
	private Map<Integer, FingerprintQualityThreshold> fingerprintQualityThresholdMap;
	private Map<Integer, FingerprintUiComponents> fingerprintUiComponentsMap = new HashMap<>();
	private CaptureFingerprintResponse wrongSlapCapturedFingerprints;
	private boolean skippingCurrentSlap;
	private boolean captureInProgress;
	private boolean acceptBadQualityFingerprint;
	private int acceptedBadQualityFingerprintMinRetires;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/fingerprintCapturing.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnPrevious.setOnAction(event -> goPrevious());
		btnNext.setOnAction(event -> goNext());
		btnNext.disableProperty().bind(ivCompleted.visibleProperty().not());
		
		paneControlsInnerContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			paneControlsOuterContainer.requestLayout();
			return paneControlsOuterContainer.getViewportBounds().getHeight();
		}, paneControlsOuterContainer.viewportBoundsProperty()));
		
		Glyph glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
		btnLivePreviewLegend.setGraphic(glyph);
		glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
		btnRightHandLegend.setGraphic(glyph);
		glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
		btnLeftHandLegend.setGraphic(glyph);
		
		attachSlapFingerprintsLegendTooltip(btnLivePreviewLegend);
		attachFingerprintLegendTooltip(btnRightHandLegend);
		attachFingerprintLegendTooltip(btnLeftHandLegend);
		
		// reverse the orientation so that the button will appear next to the text
		NodeOrientation reverse = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT ?
														NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT;
		tpFingerprintDeviceLivePreview.setNodeOrientation(reverse);
		tpRightHand.setNodeOrientation(reverse);
		tpLeftHand.setNodeOrientation(reverse);
		
		// show the image placeholder if and only if there is no image and the checkbox is selected
		ivLeftLittlePlaceholder.visibleProperty().bind(ivLeftLittle.imageProperty().isNull()
				                                                                .and(cbLeftLittle.selectedProperty()));
		ivLeftRingPlaceholder.visibleProperty().bind(ivLeftRing.imageProperty().isNull()
			                                                                    .and(cbLeftRing.selectedProperty()));
		ivLeftMiddlePlaceholder.visibleProperty().bind(ivLeftMiddle.imageProperty().isNull()
				                                                                .and(cbLeftMiddle.selectedProperty()));
		ivLeftIndexPlaceholder.visibleProperty().bind(ivLeftIndex.imageProperty().isNull()
				                                                                .and(cbLeftIndex.selectedProperty()));
		ivLeftThumbPlaceholder.visibleProperty().bind(ivLeftThumb.imageProperty().isNull()
				                                                                .and(cbLeftThumb.selectedProperty()));
		ivRightLittlePlaceholder.visibleProperty().bind(ivRightLittle.imageProperty().isNull()
				                                                                .and(cbRightLittle.selectedProperty()));
		ivRightRingPlaceholder.visibleProperty().bind(ivRightRing.imageProperty().isNull()
				                                                                .and(cbRightRing.selectedProperty()));
		ivRightMiddlePlaceholder.visibleProperty().bind(ivRightMiddle.imageProperty().isNull()
				                                                                .and(cbRightMiddle.selectedProperty()));
		ivRightIndexPlaceholder.visibleProperty().bind(ivRightIndex.imageProperty().isNull()
				                                                                .and(cbRightIndex.selectedProperty()));
		ivRightThumbPlaceholder.visibleProperty().bind(ivRightThumb.imageProperty().isNull()
				                                                                .and(cbRightThumb.selectedProperty()));
		
		// show the image placeholder if and only if there is no image and the progress is not visible
		ivFingerprintDeviceLivePreviewPlaceholder.visibleProperty().bind(ivFingerprintDeviceLivePreview.imageProperty()
                                                .isNull().and(piFingerprintDeviceLivePreview.visibleProperty().not()));
		
		// accumulate all the fingerprint-related components in a map so that we can apply actions on them easily
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
                   new FingerprintUiComponents(FingerPosition.RIGHT_THUMB, FingerPosition.TWO_THUMBS, ivRightThumb,
                                                   svgRightThumb, tpRightThumb, cbRightThumb,
                                                   resources.getString("label.fingers.thumb"),
                                                   resources.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
                   new FingerprintUiComponents(FingerPosition.RIGHT_INDEX, FingerPosition.RIGHT_SLAP, ivRightIndex,
                                                   svgRightIndex, tpRightIndex, cbRightIndex,
                                                   resources.getString("label.fingers.index"),
                                                   resources.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
                   new FingerprintUiComponents(FingerPosition.RIGHT_MIDDLE, FingerPosition.RIGHT_SLAP, ivRightMiddle,
                                                   svgRightMiddle, tpRightMiddle, cbRightMiddle,
                                                   resources.getString("label.fingers.middle"),
                                                   resources.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_RING.getPosition(),
                   new FingerprintUiComponents(FingerPosition.RIGHT_RING, FingerPosition.RIGHT_SLAP, ivRightRing,
                                                   svgRightRing, tpRightRing, cbRightRing,
                                                   resources.getString("label.fingers.ring"),
                                                   resources.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
                   new FingerprintUiComponents(FingerPosition.RIGHT_LITTLE, FingerPosition.RIGHT_SLAP, ivRightLittle,
                                                   svgRightLittle, tpRightLittle, cbRightLittle,
                                                   resources.getString("label.fingers.little"),
                                                   resources.getString("label.rightHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_THUMB.getPosition(),
                   new FingerprintUiComponents(FingerPosition.LEFT_THUMB, FingerPosition.TWO_THUMBS, ivLeftThumb,
                                                   svgLeftThumb, tpLeftThumb, cbLeftThumb,
                                                   resources.getString("label.fingers.thumb"),
                                                   resources.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_INDEX.getPosition(),
                   new FingerprintUiComponents(FingerPosition.LEFT_INDEX, FingerPosition.LEFT_SLAP, ivLeftIndex,
                                                   svgLeftIndex, tpLeftIndex, cbLeftIndex,
                                                   resources.getString("label.fingers.index"),
                                                   resources.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
                   new FingerprintUiComponents(FingerPosition.LEFT_MIDDLE, FingerPosition.LEFT_SLAP, ivLeftMiddle,
                                                   svgLeftMiddle, tpLeftMiddle, cbLeftMiddle,
                                                   resources.getString("label.fingers.middle"),
                                                   resources.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_RING.getPosition(),
                   new FingerprintUiComponents(FingerPosition.LEFT_RING, FingerPosition.LEFT_SLAP, ivLeftRing,
                                                   svgLeftRing, tpLeftRing, cbLeftRing,
                                                   resources.getString("label.fingers.ring"),
                                                   resources.getString("label.leftHand")));
		fingerprintUiComponentsMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
                   new FingerprintUiComponents(FingerPosition.LEFT_LITTLE, FingerPosition.LEFT_SLAP, ivLeftLittle,
                                                   svgLeftLittle, tpLeftLittle, cbLeftLittle,
                                                   resources.getString("label.fingers.little"),
                                                   resources.getString("label.leftHand")));
		
		// disable the finger's titledPane whenever its checkbox is unselected
		fingerprintUiComponentsMap.forEach((position, components) ->
               components.getTitledPane().disableProperty().bind(components.getCheckBox().selectedProperty().not()));
		
		// disable the finger's vector hand highlight whenever its checkbox is unselected
		fingerprintUiComponentsMap.forEach((position, components) ->
               components.getSvgPath().disableProperty().bind(components.getCheckBox().selectedProperty().not()));
		
		// retrieve the quality threshold for each finger from the user session cache, or construct it and cache it
		UserSession userSession = Context.getUserSession();
		@SuppressWarnings("unchecked")
		Map<Integer, FingerprintQualityThreshold> persistedMap = (Map<Integer, FingerprintQualityThreshold>)
				userSession.getAttribute("lookups.fingerprint.qualityThresholdMap");
		if(persistedMap != null) fingerprintQualityThresholdMap = persistedMap;
		else
		{
			fingerprintQualityThresholdMap = new HashMap<>();
			fingerprintUiComponentsMap.forEach((position, components) ->
                                   fingerprintQualityThresholdMap.put(components.getFingerPosition().getPosition(),
                                                  new FingerprintQualityThreshold(components.getFingerPosition())));
			userSession.setAttribute("lookups.fingerprint.qualityThresholdMap", fingerprintQualityThresholdMap);
		}
		
		// change btnStartFingerprintCapturing's text depends on the selected fingers dynamically
		ChangeListener<Boolean> rightSlapChangeListener = (observable, oldValue, newValue) ->
		{
			skippingCurrentSlap = false;
			
			if(!newValue && !cbRightIndex.isSelected() && !cbRightMiddle.isSelected() &&
					!cbRightRing.isSelected() && !cbRightLittle.isSelected())
			{
				skippingCurrentSlap = true;
				btnStartFingerprintCapturing.setText(resources.getString("button.skipRightSlap"));
			}
			else btnStartFingerprintCapturing.setText(resources.getString("button.captureRightSlapFingerprints"));
		};
		ChangeListener<Boolean> leftSlapChangeListener = (observable, oldValue, newValue) ->
		{
			skippingCurrentSlap = false;
			
			if(!newValue && !cbLeftIndex.isSelected() && !cbLeftMiddle.isSelected() &&
					!cbLeftRing.isSelected() && !cbLeftLittle.isSelected())
			{
				skippingCurrentSlap = true;
				btnStartFingerprintCapturing.setText(resources.getString("button.skipLeftSlap"));
			}
			else btnStartFingerprintCapturing.setText(resources.getString("button.captureLeftSlapFingerprints"));
		};
		ChangeListener<Boolean> thumbsChangeListener = (observable, oldValue, newValue) ->
		{
			skippingCurrentSlap = false;
			
			if(!newValue && !cbRightThumb.isSelected() && !cbLeftThumb.isSelected())
			{
				skippingCurrentSlap = true;
				btnStartFingerprintCapturing.setText(resources.getString("button.skipThumbs"));
			}
			else btnStartFingerprintCapturing.setText(resources.getString("button.captureThumbsFingerprints"));
		};
		cbRightIndex.selectedProperty().addListener(rightSlapChangeListener);
		cbRightMiddle.selectedProperty().addListener(rightSlapChangeListener);
		cbRightRing.selectedProperty().addListener(rightSlapChangeListener);
		cbRightLittle.selectedProperty().addListener(rightSlapChangeListener);
		cbLeftIndex.selectedProperty().addListener(leftSlapChangeListener);
		cbLeftMiddle.selectedProperty().addListener(leftSlapChangeListener);
		cbLeftRing.selectedProperty().addListener(leftSlapChangeListener);
		cbLeftLittle.selectedProperty().addListener(leftSlapChangeListener);
		cbRightThumb.selectedProperty().addListener(thumbsChangeListener);
		cbLeftThumb.selectedProperty().addListener(thumbsChangeListener);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			// collect configurations from the workflow
			Boolean hidePreviousButton = (Boolean) uiInputData.get(KEY_HIDE_PREVIOUS_BUTTON);
			if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
			Boolean bool = (Boolean) uiInputData.get(KEY_ACCEPT_BAD_QUALITY_FINGERPRINT);
			if(bool != null) acceptBadQualityFingerprint = bool;
			Integer i = (Integer) uiInputData.get(KEY_ACCEPTED_BAD_QUALITY_FINGERPRINT_MIN_RETIRES);
			if(bool != null) acceptedBadQualityFingerprintMinRetires = i;
			
			// load the persisted captured fingerprints, if any
			@SuppressWarnings("unchecked")
			Map<Integer, Fingerprint> capturedFingerprints = (Map<Integer, Fingerprint>)
																	uiInputData.get(KEY_CAPTURED_FINGERPRINTS);
			if(capturedFingerprints != null && !capturedFingerprints.isEmpty())
			{
				this.capturedFingerprints = capturedFingerprints;
				
				capturedFingerprints.forEach((position, fingerprint) ->
				{
					FingerprintUiComponents components = fingerprintUiComponentsMap.get(position);
					currentSlapPosition = Math.max(currentSlapPosition, components.getSlapPosition().getPosition());
					
					if(fingerprint.isSkipped())
					{
						components.getCheckBox().setSelected(false);
						return;
					}
					
					// show fingerprint
					showSegmentedFingerprints(fingerprint, components.getImageView(),
					                          components.getTitledPane(), components.getHandLabel(),
					                          components.getFingerLabel(), true,
					                         true,
					                         true);
					components.getTitledPane().setActive(true);
					components.getTitledPane().setCaptured(true);
					components.getTitledPane().setValid(true);
				});
				
				// increment to the next slap position
				currentSlapPosition++;
				
				// update the controls based on the current slap position
				if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
				{
					btnStartFingerprintCapturing.setText(
													resources.getString("button.captureRightSlapFingerprints"));
				}
				else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
				{
					btnStartFingerprintCapturing.setText(
													resources.getString("button.captureLeftSlapFingerprints"));
				}
				else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
				{
					btnStartFingerprintCapturing.setText(
													resources.getString("button.captureThumbsFingerprints"));
				}
				else
				{
					GuiUtils.showNode(btnStartFingerprintCapturing, false);
					GuiUtils.showNode(lblStatus, true);
					GuiUtils.showNode(ivCompleted, true);
					lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
				}
			}
			
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
			
			// register a listener to the event of the devices-runner being running or not
			deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
			{
				boolean autoInitialize = "true".equals(System.getProperty("jnlp.bio.bw.fingerprint.autoInitialize"));
				
				if(running && autoInitialize && !deviceManagerGadgetPaneController.isFingerprintScannerInitialized())
				{
					deviceManagerGadgetPaneController.initializeFingerprintScanner();
				}
			});
			
			// register a listener to the event of the fingerprint device being initialized or disconnected
			deviceManagerGadgetPaneController.setFingerprintScannerInitializationListener(
																				initialized -> Platform.runLater(() ->
			{
			    GuiUtils.showNode(lblStatus, true);
				GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStopFingerprintCapturing, false);
				
				tpFingerprintDeviceLivePreview.setActive(false);
				ivFingerprintDeviceLivePreview.setImage(null);
				
				if(initialized)
			    {
				    LOGGER.info("The fingerprint scanner is initialized!");
				    if(currentSlapPosition <= FingerPosition.TWO_THUMBS.getPosition()) GuiUtils.showNode(
				    		                                                btnStartFingerprintCapturing, true);
				    lblStatus.setText(resources.getString(
			        		                        "label.status.fingerprintScannerInitializedSuccessfully"));
				    activateFingerIndicatorsForNextCapturing(currentSlapPosition);
				    GuiUtils.showNode(btnStartOver,
				                      currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			    }
			    else
			    {
				    LOGGER.info("The fingerprint scanner is disconnected!");
				    GuiUtils.showNode(btnStartFingerprintCapturing, false);
				    lblStatus.setText(resources.getString("label.status.fingerprintScannerDisconnected"));
			    }
			}));
			
			// prepare for next fingerprint capturing if the fingerprint device is connected and initialized, otherwise
			// auto-run and auto-initialize as configured
			if(deviceManagerGadgetPaneController.isFingerprintScannerInitialized())
			{
				if(currentSlapPosition <= FingerPosition.TWO_THUMBS.getPosition()) GuiUtils.showNode(
																			btnStartFingerprintCapturing, true);
				activateFingerIndicatorsForNextCapturing(currentSlapPosition);
				GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			}
			else if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
			{
				boolean autoInitialize = "true".equals(
						System.getProperty("jnlp.bio.bw.fingerprint.autoInitialize"));
				
				if(autoInitialize)
				{
					deviceManagerGadgetPaneController.initializeFingerprintScanner();
				}
				else
				{
					lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
					GuiUtils.showNode(lblStatus, true);
				}
			}
			else
			{
				boolean devicesRunnerAutoRun = "true".equals(System.getProperty("jnlp.bio.bw.devicesRunner.autoRun"));
				
				if(devicesRunnerAutoRun)
				{
					deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
				}
				else
				{
					lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
					GuiUtils.showNode(lblStatus, true);
				}
			}
		}
	}
	
	@Override
	public void onLeaving(Map<String, Object> uiDataMap)
	{
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController()
									 .setFingerprintScannerInitializationListener(null);
		
		if(btnStopFingerprintCapturing.isVisible()) btnStopFingerprintCapturing.fire();
		
		// remove incomplete slaps
		boolean allOk = true;
		for(int i = FingerPosition.RIGHT_INDEX.getPosition(); i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
		{
			Fingerprint fingerprint = capturedFingerprints.get(i);
			if(fingerprint == null)
			{
				if(currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition())
								capturedFingerprints.put(i, new Fingerprint(null, false,
							                                                false, true));
				continue;
			}
			
			allOk = fingerprint.isSkipped() || (fingerprint.isAcceptableQuality() && !fingerprint.isDuplicated());
			if(!allOk) break;
		}
		if(!allOk)
		{
			for(int i = FingerPosition.RIGHT_INDEX.getPosition(); i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
			{
				capturedFingerprints.remove(i);
			}
		}
		
		allOk = true;
		for(int i = FingerPosition.LEFT_INDEX.getPosition(); i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
		{
			Fingerprint fingerprint = capturedFingerprints.get(i);
			if(fingerprint == null)
			{
				if(currentSlapPosition > FingerPosition.LEFT_SLAP.getPosition())
								capturedFingerprints.put(i, new Fingerprint(null, false,
					                                                        false, true));
				continue;
			}
			
			allOk = fingerprint.isSkipped() || (fingerprint.isAcceptableQuality() && !fingerprint.isDuplicated());
			if(!allOk) break;
		}
		if(!allOk)
		{
			for(int i = FingerPosition.LEFT_INDEX.getPosition(); i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
			{
				capturedFingerprints.remove(i);
			}
		}
		
		allOk = true;
		for(int i : new int[]{FingerPosition.RIGHT_THUMB.getPosition(), FingerPosition.LEFT_THUMB.getPosition()})
		{
			Fingerprint fingerprint = capturedFingerprints.get(i);
			if(fingerprint == null)
			{
				if(currentSlapPosition > FingerPosition.TWO_THUMBS.getPosition())
					capturedFingerprints.put(i, new Fingerprint(null, false,
					                                            false, true));
				continue;
			}
			
			allOk = fingerprint.isSkipped() || (fingerprint.isAcceptableQuality() && !fingerprint.isDuplicated());
			if(!allOk) break;
		}
		if(!allOk)
		{
			capturedFingerprints.remove(FingerPosition.RIGHT_THUMB.getPosition());
			capturedFingerprints.remove(FingerPosition.LEFT_THUMB.getPosition());
		}
		
		// save the complete slaps only
		uiDataMap.put(KEY_CAPTURED_FINGERPRINTS, capturedFingerprints);
	}
	
	@FXML
	private void onStartFingerprintCapturingButtonClicked(ActionEvent event)
	{
		hideNotification();
		tpFingerprintDeviceLivePreview.setActive(false);
		tpFingerprintDeviceLivePreview.setCaptured(false);
		ivFingerprintDeviceLivePreview.setImage(null);
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(btnStopFingerprintCapturing, false);
		GuiUtils.showNode(btnAcceptCurrentSlap, false);
		GuiUtils.showNode(btnAcceptCurrentFingerprints, false);
		GuiUtils.showNode(lblStatus, true);
		
		if(skippingCurrentSlap)
		{
			if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.skippedRightSlap"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
			}
			else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.skippedLeftSlap"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
			}
			else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
				GuiUtils.showNode(btnStartFingerprintCapturing, false);
				GuiUtils.showNode(ivCompleted, true);
			}
			
			activateFingerIndicatorsForNextCapturing(++currentSlapPosition);
			renameCaptureFingerprintsButton(false);
			
			return;
		}
		
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(piFingerprintDeviceLivePreview, true);
		
		// disable all the checkboxes
		fingerprintUiComponentsMap.forEach((position, components) -> components.getCheckBox().setDisable(true));
		
		LOGGER.info("capturing the fingerprints (position = " + currentSlapPosition + ")...");
		lblStatus.setText(resources.getString("label.status.waitingDeviceResponse"));
		boolean[] firstLivePreviewingResponse = {true};
		
		Task<ServiceResponse<CaptureFingerprintResponse>> capturingFingerprintTask =
																new Task<ServiceResponse<CaptureFingerprintResponse>>()
		{
			@Override
			protected ServiceResponse<CaptureFingerprintResponse> call() throws Exception
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
						
						// prepare the current titledPane and highlight the current finger SVG
						fingerprintUiComponentsMap.forEach((position, components) ->
						{
						    boolean currentSlap = currentSlapPosition == components.getSlapPosition().getPosition();
						    if(currentSlap)
						    {
						        components.getTitledPane().setCaptured(false);
						        components.getTitledPane().setDuplicated(false);
						    }
						
						    GuiUtils.showNode(components.getSvgPath(), currentSlap);
						});
					}
					
					String previewImageBase64 = response.getPreviewImage();
					byte[] bytes = Base64.getDecoder().decode(previewImageBase64);
					ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				});
				
				// detect the missing fingers and calculate the expected fingers count
				List<Integer> missingFingers = new ArrayList<>();
				int[] expectedFingersCount = {0};
				fingerprintUiComponentsMap.forEach((position, components) ->
				{
				    if(currentSlapPosition == components.getSlapPosition().getPosition())
				    {
				        if(components.getCheckBox().isSelected()) expectedFingersCount[0]++;
				        else missingFingers.add(components.getFingerPosition().getPosition());
				    }
				});
				
				// start the real capturing
				String fingerprintDeviceName = Context.getCoreFxController().getDeviceManagerGadgetPaneController()
						.getFingerprintScannerDeviceName();
				Future<ServiceResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
						.getFingerprintService().startPreviewAndAutoCapture(fingerprintDeviceName,
						                                                    currentSlapPosition,
						                                                    expectedFingersCount[0], missingFingers,
						                                                    true, responseProcessor);
				return future.get();
			}
		};
		capturingFingerprintTask.setOnSucceeded(e ->
        {
        	// hide all SVGs of the fingers
	        fingerprintUiComponentsMap.forEach((integer, components) ->
			                                           GuiUtils.showNode(components.getSvgPath(), false));
	        GuiUtils.showNode(btnStopFingerprintCapturing, false);
	        GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
	        
	        // get the response from the BioKit for the captured fingerprints
	        ServiceResponse<CaptureFingerprintResponse> serviceResponse = capturingFingerprintTask.getValue();
	        
	        if(serviceResponse.isSuccess())
	        {
		        CaptureFingerprintResponse result = serviceResponse.getResult();
	        	
		        if(result.getReturnCode() == CaptureFingerprintResponse.SuccessCodes.SUCCESS)
		        {
			        tpFingerprintDeviceLivePreview.setCaptured(true);
		        	
			        if(result.isWrongSlap())
			        {
				        tpFingerprintDeviceLivePreview.setValid(false);
				        
				        if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
				        {
					        lblStatus.setText(resources.getString("label.status.notRightSlap"));
					        btnStartFingerprintCapturing.setText(
					        		        resources.getString("button.recaptureRightSlapFingerprints"));
				        }
				        else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
				        {
					        lblStatus.setText(resources.getString("label.status.notLeftSlap"));
					        btnStartFingerprintCapturing.setText(
					        		        resources.getString("button.recaptureLeftSlapFingerprints"));
				        }
				        
				        GuiUtils.showNode(btnStartFingerprintCapturing, true);
				        GuiUtils.showNode(btnAcceptCurrentSlap, true);
				        GuiUtils.showNode(btnStartOver,
				                          currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
				        
				        // save the captured fingerprints
				        wrongSlapCapturedFingerprints = result;
				
				        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
				                                   resources.getString("label.contextMenu.slapFingerprints"),
				                                   resources.getString("label.contextMenu.showImage"));
			        }
			        else processSlapFingerprints(result);
		        }
		        else
		        {
			        GuiUtils.showNode(btnStartFingerprintCapturing, true);
			        GuiUtils.showNode(btnStartOver,
			                          currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			        
			        fingerprintUiComponentsMap.forEach((integer, components) ->
			        {
				        GuiUtils.showNode(components.getSvgPath(), false);
				        if(currentSlapPosition == components.getSlapPosition().getPosition())
					                                                components.getCheckBox().setDisable(false);
			        });
		        	
			        lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
					        resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
					        resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
					        result.getReturnCode()));
		        }
	        }
	        else
	        {
		        GuiUtils.showNode(btnStartFingerprintCapturing, true);
		        GuiUtils.showNode(btnStartOver,
		                          currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
		        
		        fingerprintUiComponentsMap.forEach((integer, components) ->
		        {
			        GuiUtils.showNode(components.getSvgPath(), false);
			        if(currentSlapPosition == components.getSlapPosition().getPosition())
			                                                    components.getCheckBox().setDisable(false);
		        });
		        
		        lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
				        resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
				        resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
				        serviceResponse.getErrorCode()));
		
		        String errorCode = firstLivePreviewingResponse[0] ? CommonsErrorCodes.C008_00008.getCode() :
			                                                        CommonsErrorCodes.C008_00009.getCode();
		        String[] errorDetails = {firstLivePreviewingResponse[0] ?
				        "failed while starting the fingerprint capturing!" :
				        "failed while capturing the fingerprints!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
	        }
        });
		capturingFingerprintTask.setOnFailed(e ->
		{
			GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			
			fingerprintUiComponentsMap.forEach((integer, components) ->
			{
			    GuiUtils.showNode(components.getSvgPath(), false);
			    if(currentSlapPosition == components.getSlapPosition().getPosition())
			                                                components.getCheckBox().setDisable(false);
			});
			
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
				
				String errorCode = firstLivePreviewingResponse[0] ? CommonsErrorCodes.C008_00010.getCode() :
																	CommonsErrorCodes.C008_00011.getCode();
				String[] errorDetails = {firstLivePreviewingResponse[0] ?
										"failed while starting the fingerprint capturing!" :
										"failed while capturing the fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			}
		});
		
		Context.getExecutorService().submit(capturingFingerprintTask);
	}
	
	private void processSlapFingerprints(CaptureFingerprintResponse captureFingerprintResponse)
	{
		tpFingerprintDeviceLivePreview.setValid(true);
		
		// show the final slap image in place of the live preview image
		String capturedImageBase64 = captureFingerprintResponse.getCapturedImage();
		byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
		ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
		
		GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
		                           resources.getString("label.contextMenu.slapFingerprints"),
		                           resources.getString("label.contextMenu.showImage"));
		
		List<DMFingerData> fingerData = captureFingerprintResponse.getFingerData();
		
		Task<ServiceResponse<DuplicatedFingerprintsResponse>> findDuplicatesTask =
															new Task<ServiceResponse<DuplicatedFingerprintsResponse>>()
		{
			@Override
			protected ServiceResponse<DuplicatedFingerprintsResponse> call() throws Exception
			{
				Map<Integer, String> gallery = new HashMap<>();
				Map<Integer, String> probes = new HashMap<>();
				
				if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
				{
					for(int i = FingerPosition.RIGHT_INDEX.getPosition();
					                                            i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
					{
						if(capturedFingerprints.containsKey(i))
									gallery.put(i, capturedFingerprints.get(i).getDmFingerData().getTemplate());
					}
					
					fingerData.forEach(dmFingerData -> probes.put(dmFingerData.getPosition(),
					                                              dmFingerData.getTemplate()));
				}
				else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
				{
					for(int i = FingerPosition.RIGHT_INDEX.getPosition();
					                                                i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
					{
						if(capturedFingerprints.containsKey(i))
										gallery.put(i, capturedFingerprints.get(i).getDmFingerData().getTemplate());
					}
					
					for(int i = FingerPosition.LEFT_INDEX.getPosition();
					                                                i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
					{
						if(capturedFingerprints.containsKey(i))
										gallery.put(i, capturedFingerprints.get(i).getDmFingerData().getTemplate());
					}
					
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
			
			ServiceResponse<DuplicatedFingerprintsResponse> serviceResponse = findDuplicatesTask.getValue();
			
			if(serviceResponse == null) // no duplicates
			{
				showSegmentedFingerprints(fingerData, null);
			}
			else if(serviceResponse.isSuccess())
			{
				DuplicatedFingerprintsResponse result = serviceResponse.getResult();
				
				if(result.getReturnCode() == DuplicatedFingerprintsResponse.SuccessCodes.SUCCESS)
				{
					// it could be empty
				    Map<Integer, Boolean> duplicatedFingers = result.getDuplicatedFingers();
				    showSegmentedFingerprints(fingerData, duplicatedFingers);
				}
				else
				{
					GuiUtils.showNode(btnStartOver,
					                  currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
					
					fingerprintUiComponentsMap.forEach((integer, components) ->
					{
					    GuiUtils.showNode(components.getSvgPath(), false);
					    if(currentSlapPosition == components.getSlapPosition().getPosition())
					                                                components.getCheckBox().setDisable(false);
					});
					
					lblStatus.setText(String.format(
							resources.getString("label.status.failedToFindDuplicatedFingerprintsWithErrorCode"),
                            result.getReturnCode()));
				}
			}
			else
			{
				GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
				
				fingerprintUiComponentsMap.forEach((integer, components) ->
				{
				    GuiUtils.showNode(components.getSvgPath(), false);
				    if(currentSlapPosition == components.getSlapPosition().getPosition())
				                                                components.getCheckBox().setDisable(false);
				});
				
				lblStatus.setText(String.format(
							resources.getString("label.status.failedToFindDuplicatedFingerprintsWithErrorCode"),
                            serviceResponse.getErrorCode()));
				
				String errorCode = CommonsErrorCodes.C008_00012.getCode();
				String[] errorDetails = {"failed while finding duplicated fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
			}
		});
		findDuplicatesTask.setOnFailed(e ->
		{
			GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			
			fingerprintUiComponentsMap.forEach((integer, components) ->
			{
			    GuiUtils.showNode(components.getSvgPath(), false);
			    if(currentSlapPosition == components.getSlapPosition().getPosition())
			                                                components.getCheckBox().setDisable(false);
			});
			
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
				
				String errorCode = CommonsErrorCodes.C008_00013.getCode();
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
		Future<ServiceResponse<FingerprintStopPreviewResponse>> future = Context.getBioKitManager()
									.getFingerprintService().cancelCapture(fingerprintDeviceName,
															               FingerPosition.RIGHT_SLAP.getPosition());
		
		Task<ServiceResponse<FingerprintStopPreviewResponse>> task =
															new Task<ServiceResponse<FingerprintStopPreviewResponse>>()
		{
			@Override
			protected ServiceResponse<FingerprintStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			fingerprintUiComponentsMap.forEach((integer, components) ->
			{
			    GuiUtils.showNode(components.getSvgPath(), false);
			    if(currentSlapPosition == components.getSlapPosition().getPosition())
			                                                components.getCheckBox().setDisable(false);
			});
			
		    tpFingerprintDeviceLivePreview.setActive(false);
			ivFingerprintDeviceLivePreview.setImage(null);
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
		
		    ServiceResponse<FingerprintStopPreviewResponse> serviceResponse = task.getValue();
		
		    if(serviceResponse.isSuccess())
		    {
			    FingerprintStopPreviewResponse result = serviceResponse.getResult();
		
		        if(result.getReturnCode() == FingerprintStopPreviewResponse.SuccessCodes.SUCCESS)
		        {
		            lblStatus.setText(
		                    resources.getString("label.status.successfullyStoppedFingerprintCapturing"));
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
		                serviceResponse.getErrorCode()));
		
		        String errorCode = CommonsErrorCodes.C008_00014.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, serviceResponse.getException(), errorDetails);
		    }
		});
		task.setOnFailed(e ->
		{
			fingerprintUiComponentsMap.forEach((integer, components) ->
			{
			    GuiUtils.showNode(components.getSvgPath(), false);
			    if(currentSlapPosition == components.getSlapPosition().getPosition())
		                                                    components.getCheckBox().setDisable(false);
			});
			
		    tpFingerprintDeviceLivePreview.setActive(false);
			ivFingerprintDeviceLivePreview.setImage(null);
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			
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
		
		        String errorCode = CommonsErrorCodes.C008_00015.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		    }
		});
		
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onAcceptCurrentSlapButtonClicked(ActionEvent event)
	{
		hideNotification();
		
		// check if the user has the permission to accept wrong slap
		@SuppressWarnings("unchecked")
		List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
		String acceptWrongHandRole = System.getProperty("jnlp.bio.bw.fingerprint.roles.acceptWrongHand");
		boolean authorized = userRoles.contains(acceptWrongHandRole);
		
		if(authorized)
		{
			String headerText = resources.getString("fingerprint.acceptWrongSlap.confirmation.header");
			String contentText = null;
			if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
				contentText = resources.getString("fingerprint.acceptWrongSlap.right.confirmation.message");
			else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
				contentText = resources.getString("fingerprint.acceptWrongSlap.left.confirmation.message");
			
			boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
			
			if(confirmed)
			{
				GuiUtils.showNode(btnAcceptCurrentSlap, false);
				processSlapFingerprints(wrongSlapCapturedFingerprints);
			}
		}
		else
		{
			String message = resources.getString("fingerprint.acceptWrongSlap.notAuthorized");
			showWarningNotification(message);
		}
	}
	
	@FXML
	private void onAcceptCurrentFingerprintsButtonClicked(ActionEvent event)
	{
		GuiUtils.showNode(btnAcceptCurrentFingerprints, false);
		
		if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedRightSlapFingerprints"));
			btnStartFingerprintCapturing.setText(resources.getString("button.captureLeftSlapFingerprints"));
			
			for(int i = FingerPosition.RIGHT_INDEX.getPosition();
			    i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
			{
				capturedFingerprints.get(i).setAcceptableQuality(true);
				fingerprintUiComponentsMap.get(i).getTitledPane().setValid(true);
			}
		}
		else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedLeftSlapFingerprints"));
			btnStartFingerprintCapturing.setText(resources.getString("button.captureThumbsFingerprints"));
			
			for(int i = FingerPosition.LEFT_INDEX.getPosition();
			    i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
			{
				capturedFingerprints.get(i).setAcceptableQuality(true);
				fingerprintUiComponentsMap.get(i).getTitledPane().setValid(true);
			}
		}
		else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedThumbsFingerprints"));
			capturedFingerprints.get(FingerPosition.RIGHT_THUMB.getPosition()).setAcceptableQuality(true);
			capturedFingerprints.get(FingerPosition.LEFT_THUMB.getPosition()).setAcceptableQuality(true);
			fingerprintUiComponentsMap.get(FingerPosition.RIGHT_THUMB.getPosition()).getTitledPane().setValid(true);
			fingerprintUiComponentsMap.get(FingerPosition.LEFT_THUMB.getPosition()).getTitledPane().setValid(true);
		}
		
		activateFingerIndicatorsForNextCapturing(++currentSlapPosition);
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent event)
	{
		String headerText = resources.getString("fingerprint.startingOver.confirmation.header");
		String contentText = resources.getString("fingerprint.startingOver.confirmation.message");
		
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed)
		{
			hideNotification();
			tpFingerprintDeviceLivePreview.setActive(false);
			tpFingerprintDeviceLivePreview.setCaptured(false);
			ivFingerprintDeviceLivePreview.setImage(null);
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnStopFingerprintCapturing, false);
			GuiUtils.showNode(btnAcceptCurrentSlap, false);
			GuiUtils.showNode(btnAcceptCurrentFingerprints, false);
			GuiUtils.showNode(lblStatus, false);
			GuiUtils.showNode(ivCompleted, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			fingerprintUiComponentsMap.forEach((position, components) ->
			{
				components.getImageView().setImage(null);
				components.getCheckBox().setDisable(false);
				components.getTitledPane().setActive(false);
				components.getTitledPane().setCaptured(false);
				components.getTitledPane().setValid(false);
				components.getTitledPane().setDuplicated(false);
			});
			
			capturedFingerprints.clear();
			currentSlapPosition = FingerPosition.RIGHT_SLAP.getPosition();
			activateFingerIndicatorsForNextCapturing(currentSlapPosition);
			renameCaptureFingerprintsButton(false);
		}
	}
	
	private void showSegmentedFingerprints(List<DMFingerData> fingerData, Map<Integer, Boolean> duplicatedFingers)
	{
		boolean[] allAcceptableQuality = {true};
		boolean[] noDuplicates = {true};
		fingerData.forEach(dmFingerData ->
		{
			FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(
																						dmFingerData.getPosition());
			boolean acceptableFingerprintNfiq = qualityThreshold.getMaximumAcceptableNFIQ() >=
																						dmFingerData.getNfiqQuality();
			
		    boolean acceptableFingerprintMinutiaeCount = qualityThreshold.getMinimumAcceptableMinutiaeCount() <=
				                                                                        dmFingerData.getMinutiaeCount();
		
		    boolean acceptableFingerprintImageIntensity = qualityThreshold.getMinimumAcceptableImageIntensity() <=
				                                          dmFingerData.getIntensity() &&
				                                          qualityThreshold.getMaximumAcceptableImageIntensity() >=
				                                          dmFingerData.getIntensity();
		
		    boolean acceptableQuality = acceptableFingerprintNfiq &&
		            acceptableFingerprintMinutiaeCount &&
		            acceptableFingerprintImageIntensity;
			
			FingerprintUiComponents components = fingerprintUiComponentsMap.get(dmFingerData.getPosition());
		 
			boolean duplicated = false;
		    if(duplicatedFingers != null)
		    {
			    Boolean b = duplicatedFingers.get(dmFingerData.getPosition());
			    if(b != null && b)
			    {
				    duplicated = true;
				    acceptableQuality = false;
				    components.getTitledPane().setDuplicated(true);
			    }
		    }
		
		    allAcceptableQuality[0] = allAcceptableQuality[0] && acceptableQuality;
			noDuplicates[0] = noDuplicates[0] && !duplicated;
			
			Fingerprint fingerprint = new Fingerprint(dmFingerData, acceptableQuality, duplicated, false);
			capturedFingerprints.put(dmFingerData.getPosition(), fingerprint);
			showSegmentedFingerprints(fingerprint, components.getImageView(), components.getTitledPane(),
			                          components.getHandLabel(), components.getFingerLabel(), acceptableFingerprintNfiq,
			                          acceptableFingerprintMinutiaeCount, acceptableFingerprintImageIntensity);
		});
		
		if(allAcceptableQuality[0])
		{
			GuiUtils.showNode(btnStartOver, true);
			
			if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedRightSlap"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
			}
			else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedLeftSlap"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
			}
			else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
				GuiUtils.showNode(btnStartFingerprintCapturing, false);
				GuiUtils.showNode(ivCompleted, true);
			}
			
			currentSlapPosition++;
			renameCaptureFingerprintsButton(false);
		}
		else
		{
			GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			lblStatus.setText(resources.getString("label.status.someFingerprintsAreNotAcceptable"));
			
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnAcceptCurrentFingerprints, noDuplicates[0]);
			renameCaptureFingerprintsButton(true);
		}
		
		activateFingerIndicatorsForNextCapturing(currentSlapPosition);
	}
	
	private void showSegmentedFingerprints(Fingerprint fingerprint, ImageView imageView, FourStateTitledPane titledPane,
	                                       String handLabel, String fingerLabel, boolean acceptableFingerprintNfiq,
	                                       boolean acceptableFingerprintMinutiaeCount,
	                                       boolean acceptableFingerprintImageIntensity)
	{
		String fingerprintImageBase64 = fingerprint.getDmFingerData().getFinger();
		byte[] fingerprintImageBytes = Base64.getDecoder().decode(fingerprintImageBase64);
		imageView.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));
		
		titledPane.setCaptured(true);
		titledPane.setValid(fingerprint.isAcceptableQuality());
		
		StackPane titleRegion = (StackPane) titledPane.lookup(".title");
		attachFingerprintResultTooltip(titleRegion, titledPane,
		                               fingerprint.getDmFingerData().getNfiqQuality(),
		                               fingerprint.getDmFingerData().getMinutiaeCount(),
		                               fingerprint.getDmFingerData().getIntensity(),
		                               acceptableFingerprintNfiq,
		                               acceptableFingerprintMinutiaeCount,
		                               acceptableFingerprintImageIntensity,
		                               fingerprint.isDuplicated());
		
		String dialogTitle = fingerLabel + " (" + handLabel + ")";
		GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, dialogTitle,
		                           resources.getString("label.contextMenu.showImage"));
	}
	
	private void activateFingerIndicatorsForNextCapturing(int slapPosition)
	{
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
			boolean currentSlap = slapPosition == components.getSlapPosition().getPosition();
			components.getCheckBox().setDisable(!currentSlap);
			
			if(!capturedFingerprints.containsKey(position))
			{
				boolean selected = components.getCheckBox().isSelected();
				components.getTitledPane().setActive(currentSlap && selected);
			}
		});
	}
	
	private void renameCaptureFingerprintsButton(boolean retry)
	{
		String buttonLabel = null;
		
		if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureRightSlapFingerprints");
			else buttonLabel = resources.getString("button.captureRightSlapFingerprints");
		}
		else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureLeftSlapFingerprints");
			else buttonLabel = resources.getString("button.captureLeftSlapFingerprints");
		}
		else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
		{
			if(retry) buttonLabel = resources.getString("button.recaptureThumbsFingerprints");
			else buttonLabel = resources.getString("button.captureThumbsFingerprints");
		}
		
		btnStartFingerprintCapturing.setText(buttonLabel);
	}
	
	private void attachFingerprintResultTooltip(Node sourceNode, Node targetNode, int nfiq, int minutiaeCount,
	                                            int imageIntensity, boolean acceptableNfiq,
	                                            boolean acceptableMinutiaeCount, boolean acceptableImageIntensity,
	                                            boolean duplicated)
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10.0));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);
		
		Label lblNfiq = new Label(resources.getString("label.tooltip.nfiq"));
		Label lblMinutiaeCount = new Label(resources.getString("label.tooltip.minutiaeCount"));
		Label lblIntensity = new Label(resources.getString("label.tooltip.imageIntensity"));
		Label lblDuplicatedFingerprint = new Label(resources.getString("label.tooltip.duplicatedFinger"));
		
		Image successImage = new Image(Thread.currentThread()
				                             .getContextClassLoader()
				                             .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                  "commons/images/success.png"));
		Image warningImage = new Image(Thread.currentThread()
				                             .getContextClassLoader()
				                             .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                  "commons/images/warning.png"));
		
		Image errorImage = new Image(Thread.currentThread()
				                               .getContextClassLoader()
				                               .getResourceAsStream("sa/gov/nic/bio/bw/client/features/" +
						                                                    "commons/images/error.png"));
		
		lblNfiq.setGraphic(new ImageView(acceptableNfiq ? successImage : warningImage));
		lblMinutiaeCount.setGraphic(new ImageView(acceptableMinutiaeCount ? successImage : warningImage));
		lblIntensity.setGraphic(new ImageView(acceptableImageIntensity ? successImage : warningImage));
		lblDuplicatedFingerprint.setGraphic(new ImageView(duplicated ? errorImage : successImage));
		
		gridPane.add(lblNfiq, 0, 0);
		gridPane.add(lblMinutiaeCount, 0, 1);
		gridPane.add(lblIntensity, 0, 2);
		gridPane.add(lblDuplicatedFingerprint, 0, 3);
		
		String sNfiq = AppUtils.replaceNumbersOnly(String.valueOf(nfiq), Locale.getDefault());
		String sMinutiaeCount = AppUtils.replaceNumbersOnly(String.valueOf(minutiaeCount), Locale.getDefault());
		String sIntensity = AppUtils.replaceNumbersOnly(String.valueOf(imageIntensity), Locale.getDefault()) + "%";
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
	
	private void attachSlapFingerprintsLegendTooltip(Node targetNode)
	{
		TextField txtCapturingSlap = new TextField(resources.getString("label.slap.legend.captureInProgress"));
		TextField txtWrongSlap = new TextField(resources.getString("label.slap.legend.wrongSlap"));
		TextField txtAcceptedSlap = new TextField(resources.getString("label.slap.legend.acceptedSlap"));
		
		txtCapturingSlap.setEditable(false);
		txtWrongSlap.setEditable(false);
		txtAcceptedSlap.setEditable(false);
		txtCapturingSlap.setFocusTraversable(false);
		txtWrongSlap.setFocusTraversable(false);
		txtAcceptedSlap.setFocusTraversable(false);
		txtCapturingSlap.getStyleClass().add("legend-blue");
		txtWrongSlap.getStyleClass().add("legend-yellow");
		txtAcceptedSlap.getStyleClass().add("legend-green");
		
		VBox vBox = new VBox(5.0, txtCapturingSlap, txtWrongSlap, txtAcceptedSlap);
		vBox.getStylesheets().setAll("sa/gov/nic/bio/bw/client/features/commons/css/style.css");
		vBox.setPadding(new Insets(10.0));
		
		// hardcoded :(
		if(Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT)
		{
			vBox.setPrefWidth(153.0);
		}
		else vBox.setPrefWidth(266.0);
		
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
		vBox.getStylesheets().setAll("sa/gov/nic/bio/bw/client/features/commons/css/style.css");
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