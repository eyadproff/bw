package sa.gov.nic.bio.bw.client.features.commons;

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
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
	@FXML private ImageView ivLeftLittleSkip;
	@FXML private ImageView ivLeftRingSkip;
	@FXML private ImageView ivLeftMiddleSkip;
	@FXML private ImageView ivLeftIndexSkip;
	@FXML private ImageView ivLeftThumbSkip;
	@FXML private ImageView ivRightThumbSkip;
	@FXML private ImageView ivRightIndexSkip;
	@FXML private ImageView ivRightMiddleSkip;
	@FXML private ImageView ivRightRingSkip;
	@FXML private ImageView ivRightLittleSkip;
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
	@FXML private Button btnAcceptBestAttemptFingerprints;
	@FXML private Button btnAcceptSelectedFingerprints;
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
	private List<List<DMFingerData>> currentSlapAttempts = new ArrayList<>();
	private CaptureFingerprintResponse wrongSlapCapturedFingerprints;
	private boolean retryActive = false;
	private boolean skippingCurrentSlap = false;
	private boolean acceptBadQualityFingerprint = false;
	private int acceptedBadQualityFingerprintMinRetires = Integer.MAX_VALUE;
	
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
		
		// show the skip icon if and only if the checkbox is not selected
		ivLeftLittleSkip.visibleProperty().bind(cbLeftLittle.selectedProperty().not());
		ivLeftRingSkip.visibleProperty().bind(cbLeftRing.selectedProperty().not());
		ivLeftMiddleSkip.visibleProperty().bind(cbLeftMiddle.selectedProperty().not());
		ivLeftIndexSkip.visibleProperty().bind(cbLeftIndex.selectedProperty().not());
		ivLeftThumbSkip.visibleProperty().bind(cbLeftThumb.selectedProperty().not());
		ivRightLittleSkip.visibleProperty().bind(cbRightLittle.selectedProperty().not());
		ivRightRingSkip.visibleProperty().bind(cbRightRing.selectedProperty().not());
		ivRightMiddleSkip.visibleProperty().bind(cbRightMiddle.selectedProperty().not());
		ivRightIndexSkip.visibleProperty().bind(cbRightIndex.selectedProperty().not());
		ivRightThumbSkip.visibleProperty().bind(cbRightThumb.selectedProperty().not());
		
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
		
		class CustomEventHandler implements EventHandler<ActionEvent>
		{
			private FingerprintUiComponents components;
			
			private CustomEventHandler(FingerprintUiComponents components)
			{
				this.components = components;
			}
			
			@Override
			public void handle(ActionEvent event)
			{
				CheckBox checkBox = (CheckBox) event.getSource();
				if(checkBox.isSelected()) return;
				
				@SuppressWarnings("unchecked")
				List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
				
				int userSkipAbility = 0;
				
				for(int i = 10; i >= 1; i--)
				{
					String skipNRole = System.getProperty(String.format(AppConstants.Locales.SAUDI_EN_LOCALE,
	                                                    "jnlp.bio.bw.fingerprint.roles.skip%02d", i));
					if(userRoles.contains(skipNRole))
					{
						userSkipAbility = i;
						break;
					}
				}
				
				int[] totalSkipCount = {0};
				
				fingerprintUiComponentsMap.forEach((position, comps) ->
				{
				    if(!comps.getCheckBox().isSelected()) totalSkipCount[0]++;
				});
				
				if(userSkipAbility >= totalSkipCount[0]) // has permission
				{
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
				else
				{
					String message;
					if(totalSkipCount[0] == 1) message =
							resources.getString("fingerprint.skippingFingerprint.notAuthorized.atAll");
					else if(totalSkipCount[0] == 2) message =
							resources.getString("fingerprint.skippingFingerprint.notAuthorized.noMore1");
					else if(totalSkipCount[0] == 3) message =
							resources.getString("fingerprint.skippingFingerprint.notAuthorized.noMore2");
					else message = String.format(AppConstants.Locales.SAUDI_EN_LOCALE,
                            resources.getString("fingerprint.skippingFingerprint.notAuthorized.noMoreN"),
                            totalSkipCount[0]);
					
					components.getCheckBox().setSelected(true);
					showWarningNotification(message);
					event.consume();
				}
			}
		}
		
		cbRightIndex.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_INDEX.getPosition())));
		cbRightMiddle.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_MIDDLE.getPosition())));
		cbRightRing.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_RING.getPosition())));
		cbRightLittle.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_LITTLE.getPosition())));
		cbLeftIndex.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_INDEX.getPosition())));
		cbLeftMiddle.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_MIDDLE.getPosition())));
		cbLeftRing.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_RING.getPosition())));
		cbLeftLittle.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_LITTLE.getPosition())));
		cbRightThumb.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_THUMB.getPosition())));
		cbLeftThumb.setOnAction(new CustomEventHandler(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_THUMB.getPosition())));
		
		class CustomChangeListener implements ChangeListener<Boolean>
		{
			private FingerprintUiComponents components;
			
			private CustomChangeListener(FingerprintUiComponents components)
			{
				this.components = components;
			}
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				skippingCurrentSlap = false;
				
				if(components.getSlapPosition() == FingerPosition.RIGHT_SLAP)
				{
					if(!cbRightIndex.isSelected() && !cbRightMiddle.isSelected() && !cbRightRing.isSelected() &&
							!cbRightLittle.isSelected())
					{
						skippingCurrentSlap = true;
						btnStartFingerprintCapturing.setText(resources.getString("button.skipRightSlap"));
					}
					else
					{
						if(retryActive) btnStartFingerprintCapturing.setText(
								resources.getString("button.recaptureRightSlapFingerprints"));
						else btnStartFingerprintCapturing.setText(
								resources.getString("button.captureRightSlapFingerprints"));
					}
				}
				else if(components.getSlapPosition() == FingerPosition.LEFT_SLAP)
				{
					if(!cbLeftIndex.isSelected() && !cbLeftMiddle.isSelected() && !cbLeftRing.isSelected() &&
							!cbLeftLittle.isSelected())
					{
						skippingCurrentSlap = true;
						btnStartFingerprintCapturing.setText(resources.getString("button.skipLeftSlap"));
					}
					else
					{
						if(retryActive) btnStartFingerprintCapturing.setText(
								resources.getString("button.recaptureLeftSlapFingerprints"));
						else btnStartFingerprintCapturing.setText(
								resources.getString("button.captureLeftSlapFingerprints"));
					}
				}
				else if(components.getSlapPosition() == FingerPosition.TWO_THUMBS)
				{
					if(!cbRightThumb.isSelected() && !cbLeftThumb.isSelected())
					{
						skippingCurrentSlap = true;
						btnStartFingerprintCapturing.setText(resources.getString("button.skipThumbs"));
					}
					else
					{
						if(retryActive) btnStartFingerprintCapturing.setText(
								resources.getString("button.recaptureThumbsFingerprints"));
						else btnStartFingerprintCapturing.setText(
								resources.getString("button.captureThumbsFingerprints"));
					}
				}
				
				Fingerprint fingerprint = capturedFingerprints.get(components.getFingerPosition().getPosition());
				if(fingerprint == null) return;
				
				fingerprint.setSkipped(!newValue);
				
				if(fingerprint.getDmFingerData() == null) return;
				
				if(newValue)
				{
					showFingerprint(fingerprint, components.getImageView(), components.getTitledPane(),
					                components.getHandLabel(), components.getFingerLabel());
				}
				else
				{
					components.getImageView().setImage(null);
					components.getTitledPane().setActive(false);
					components.getTitledPane().setCaptured(false);
					components.getTitledPane().setValid(false);
					components.getTitledPane().setDuplicated(false);
				}
				
				if(skippingCurrentSlap)
				{
					GuiUtils.showNode(btnAcceptSelectedFingerprints, false);
					return;
				}
				
				boolean[] allAcceptable = {true};
				List<Integer> positions = new ArrayList<>();
				
				if(components.getSlapPosition() == FingerPosition.RIGHT_SLAP)
				{
					positions.add(FingerPosition.RIGHT_INDEX.getPosition());
					positions.add(FingerPosition.RIGHT_MIDDLE.getPosition());
					positions.add(FingerPosition.RIGHT_RING.getPosition());
					positions.add(FingerPosition.RIGHT_LITTLE.getPosition());
				}
				else if(components.getSlapPosition() == FingerPosition.LEFT_SLAP)
				{
					positions.add(FingerPosition.LEFT_INDEX.getPosition());
					positions.add(FingerPosition.LEFT_MIDDLE.getPosition());
					positions.add(FingerPosition.LEFT_RING.getPosition());
					positions.add(FingerPosition.LEFT_LITTLE.getPosition());
				}
				else if(components.getSlapPosition() == FingerPosition.TWO_THUMBS)
				{
					positions.add(FingerPosition.RIGHT_THUMB.getPosition());
					positions.add(FingerPosition.LEFT_THUMB.getPosition());
				}
				
				positions.forEach(position ->
				{
					Fingerprint fp = capturedFingerprints.get(position);
					allAcceptable[0] = allAcceptable[0] && (fp.isAcceptableQuality() && !fp.isSkipped()) ||
									   fp.isSkipped();
				});
				
				if(allAcceptable[0])
				{
					GuiUtils.showNode(btnAcceptBestAttemptFingerprints, false);
					GuiUtils.showNode(btnAcceptSelectedFingerprints, true);
					lblStatus.setText(resources.getString("label.status.allFingerprintsAreAcceptable"));
				}
				else
				{
					boolean[] noDuplicates = {true};
					positions.forEach(position ->
					{
					    Fingerprint fp = capturedFingerprints.get(position);
						noDuplicates[0] = noDuplicates[0] && !fp.isDuplicated();
					});
					
					GuiUtils.showNode(btnAcceptSelectedFingerprints, false);
					GuiUtils.showNode(btnAcceptBestAttemptFingerprints, noDuplicates[0] &&
							acceptBadQualityFingerprint &&
							currentSlapAttempts.size() >= acceptedBadQualityFingerprintMinRetires);
					lblStatus.setText(resources.getString("label.status.someFingerprintsAreNotAcceptable"));
				}
			}
		}
		
		cbRightIndex.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_INDEX.getPosition())));
		cbRightMiddle.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_MIDDLE.getPosition())));
		cbRightRing.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_RING.getPosition())));
		cbRightLittle.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_LITTLE.getPosition())));
		cbLeftIndex.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_INDEX.getPosition())));
		cbLeftMiddle.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_MIDDLE.getPosition())));
		cbLeftRing.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_RING.getPosition())));
		cbLeftLittle.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_LITTLE.getPosition())));
		cbRightThumb.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.RIGHT_THUMB.getPosition())));
		cbLeftThumb.selectedProperty().addListener(new CustomChangeListener(fingerprintUiComponentsMap.get(
																		FingerPosition.LEFT_THUMB.getPosition())));
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
					showFingerprint(fingerprint, components.getImageView(), components.getTitledPane(),
					                components.getHandLabel(), components.getFingerLabel());
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
								capturedFingerprints.put(i, new Fingerprint(null,
								                                            false,
								                                            false,
								                                            false,
								                                            false, false,
								                                            true));
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
								capturedFingerprints.put(i, new Fingerprint(null,
								                                            false,
								                                            false,
								                                            false,
								                                            false, false,
								                                            true));
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
					                                            false,
					                                            false,
					                                            false, false, true));
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
		GuiUtils.showNode(btnAcceptBestAttemptFingerprints, false);
		GuiUtils.showNode(lblStatus, true);
		
		if(skippingCurrentSlap)
		{
			skippingCurrentSlap = false;
			
			fingerprintUiComponentsMap.forEach((position, components) ->
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
				
				if(currentSlapPosition == components.getSlapPosition().getPosition())
				{
					components.getTitledPane().setActive(false);
					components.getTitledPane().setCaptured(false);
					components.getTitledPane().setValid(false);
					components.getTitledPane().setDuplicated(false);
					components.getImageView().setImage(null);
					capturedFingerprints.put(components.getFingerPosition().getPosition(),
					                         new Fingerprint(null, false,
					                                         false,
					                                         false,
					                                         false, false, true));
				}
			});
			
			activateFingerIndicatorsForNextCapturing(++currentSlapPosition);
			renameCaptureFingerprintsButton(false);
			GuiUtils.showNode(btnStartOver, true);
			
			return;
		}
		
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(piProgress, true);
		GuiUtils.showNode(piFingerprintDeviceLivePreview, true);
		
		// disable all the checkboxes
		fingerprintUiComponentsMap.forEach((position, components) -> components.getCheckBox().setDisable(true));
		
		// prepare the current titledPane
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
		    boolean currentSlap = currentSlapPosition == components.getSlapPosition().getPosition();
		    if(currentSlap)
		    {
			    components.getTitledPane().setActive(true);
		        components.getTitledPane().setCaptured(false);
		        components.getTitledPane().setValid(false);
		        components.getTitledPane().setDuplicated(false);
			    components.getImageView().setImage(null);
		    }
		});
		
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
						
						// highlight the current finger SVG
						fingerprintUiComponentsMap.forEach((position, components) ->
						{
						    boolean currentSlap = currentSlapPosition == components.getSlapPosition().getPosition();
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
			
			        // show the final slap image in place of the live preview image
			        String capturedImageBase64 = result.getCapturedImage();
			        byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
			        ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
			
			        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
			                                   resources.getString("label.contextMenu.slapFingerprints"),
			                                   resources.getString("label.contextMenu.showImage"));
		        	
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
		        // we skip FAILED_TO_CAPTURE_FINAL_IMAGE because it happens only when we send "stop" command
		        else if(result.getReturnCode() != CaptureFingerprintResponse.FailureCodes.FAILED_TO_CAPTURE_FINAL_IMAGE)
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
						if(capturedFingerprints.containsKey(i) && !capturedFingerprints.get(i).isSkipped())
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
						if(capturedFingerprints.containsKey(i) && !capturedFingerprints.get(i).isSkipped())
										gallery.put(i, capturedFingerprints.get(i).getDmFingerData().getTemplate());
					}
					
					for(int i = FingerPosition.LEFT_INDEX.getPosition();
					                                                i <= FingerPosition.LEFT_LITTLE.getPosition(); i++)
					{
						if(capturedFingerprints.containsKey(i) && !capturedFingerprints.get(i).isSkipped())
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
				// show the final slap image in place of the live preview image
				String capturedImageBase64 = wrongSlapCapturedFingerprints.getCapturedImage();
				byte[] bytes = Base64.getDecoder().decode(capturedImageBase64);
				ivFingerprintDeviceLivePreview.setImage(new Image(new ByteArrayInputStream(bytes)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
				                           resources.getString("label.contextMenu.slapFingerprints"),
				                           resources.getString("label.contextMenu.showImage"));
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
	private void onAcceptBestAttemptFingerprintsButtonClicked(ActionEvent event)
	{
		GuiUtils.showNode(btnAcceptBestAttemptFingerprints, false);
		
		List<DMFingerData> bestAttemptFingerData = findBestAttemptFingerprints();
		currentSlapAttempts.clear();
		
		showFingerprints(bestAttemptFingerData, null, null, null,
		                 true);
		
		acceptFingerprintsAndGoNext();
	}
	
	private List<DMFingerData> findBestAttemptFingerprints()
	{
		currentSlapAttempts.sort((o1, o2) ->
		{
			int compare = Integer.compare(o1.size(), o2.size());
			if(compare != 0) return compare;
			else // if both have same amount of non-skipped fingerprints
			{
				int sum1 = o1.stream().mapToInt(DMFingerData::getPosition).sum();
				int sum2 = o2.stream().mapToInt(DMFingerData::getPosition).sum();
				
				compare = -Integer.compare(sum1, sum2);
				if(compare != 0) return compare;
				else // if both have same amount of non-skipped fingerprints and they are the same fingerprints
				{
					Predicate<DMFingerData> acceptanceFilter = dmFingerData ->
					{
						FingerprintQualityThreshold qualityThreshold = fingerprintQualityThresholdMap.get(
								dmFingerData.getPosition());
						boolean acceptableFingerprintNfiq =
								qualityThreshold.getMaximumAcceptableNFIQ() >= dmFingerData.getNfiqQuality();
						
						boolean acceptableFingerprintMinutiaeCount =
								qualityThreshold.getMinimumAcceptableMinutiaeCount() <= dmFingerData.getMinutiaeCount();
						
						boolean acceptableFingerprintImageIntensity =
								qualityThreshold.getMinimumAcceptableImageIntensity() <= dmFingerData.getIntensity() &&
								qualityThreshold.getMaximumAcceptableImageIntensity() >= dmFingerData.getIntensity();
						
						return acceptableFingerprintNfiq && acceptableFingerprintMinutiaeCount &&
							   acceptableFingerprintImageIntensity;
					};
					
					sum1 = (int) o1.stream().filter(acceptanceFilter).count();
					sum2 = (int) o2.stream().filter(acceptanceFilter).count();
					
					compare = Integer.compare(sum1, sum2);
					if(compare != 0) return compare;
					else // if both have same amount of acceptable fingerprints
					{
						sum1 = o1.stream().mapToInt(DMFingerData::getPosition).sum();
						sum2 = o2.stream().mapToInt(DMFingerData::getPosition).sum();
						
						compare = -Integer.compare(sum1, sum2);
						if(compare != 0) return compare;
						else // if both have same amount of acceptable fingerprints and they are the same fingerprints
						{
							Comparator<DMFingerData> prioritizingComparator = (fp1, fp2) ->
																-Integer.compare(fp1.getPosition(), fp2.getPosition());
							List<DMFingerData> list1 = o1.stream().filter(acceptanceFilter)
																  .sorted(prioritizingComparator)
																  .collect(Collectors.toList());
							List<DMFingerData> list2 = o1.stream().filter(acceptanceFilter)
																  .sorted(prioritizingComparator)
																  .collect(Collectors.toList());
							
							for(int i = 0; i < list1.size(); i++)
							{
								DMFingerData fp1 = list1.get(i);
								DMFingerData fp2 = list2.get(i);
								compare = -Integer.compare(fp1.getNfiqQuality(), fp2.getNfiqQuality());
								if(compare != 0) return compare;
								else // both have the same NFIQ
								{
									compare = Integer.compare(fp1.getMinutiaeCount(), fp2.getMinutiaeCount());
									if(compare != 0) return compare;
									else // both have the same minutiae count
									{
										if(fp1.getIntensity() == fp2.getIntensity()) continue;
										
										FingerprintQualityThreshold qualityThreshold =
																fingerprintQualityThresholdMap.get(fp1.getPosition());
										
										boolean fp1AcceptableFingerprintImageIntensity =
												qualityThreshold.getMinimumAcceptableImageIntensity() <=
													fp1.getIntensity() &&
													qualityThreshold.getMaximumAcceptableImageIntensity() >=
													fp1.getIntensity();
										
										boolean fp2AcceptableFingerprintImageIntensity =
												qualityThreshold.getMinimumAcceptableImageIntensity() <=
													fp2.getIntensity() &&
													qualityThreshold.getMaximumAcceptableImageIntensity() >=
													fp2.getIntensity();
										
										if(fp1AcceptableFingerprintImageIntensity &&
											!fp2AcceptableFingerprintImageIntensity) return 1;
										if(!fp1AcceptableFingerprintImageIntensity &&
											fp2AcceptableFingerprintImageIntensity) return -1;
										else // both have bad image intensity
										{
											if(fp1.getIntensity() >
												qualityThreshold.getMaximumAcceptableImageIntensity() &&
												fp2.getIntensity() <
												qualityThreshold.getMinimumAcceptableImageIntensity())
											{
												return 1;
											}
											if(fp1.getIntensity() <
												qualityThreshold.getMinimumAcceptableImageIntensity() &&
												fp2.getIntensity() >
												qualityThreshold.getMaximumAcceptableImageIntensity())
											{
												return -1;
											}
											else // both on the same side
											{
												if(fp1.getIntensity() >
													qualityThreshold.getMaximumAcceptableImageIntensity() &&
													fp2.getIntensity() >
													qualityThreshold.getMaximumAcceptableImageIntensity())
												{
													return 1;
												}
												else return -1;
											}
										}
									}
								}
							}
							
							return compare;
						}
					}
				}
			}
		});
		
		return currentSlapAttempts.get(0);
	}
	
	@FXML
	private void onAcceptSelectedFingerprintsButtonClicked(ActionEvent event)
	{
		GuiUtils.showNode(btnAcceptSelectedFingerprints, false);
		acceptFingerprintsAndGoNext();
	}
	
	private void acceptFingerprintsAndGoNext()
	{
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
		    boolean currentSlap = currentSlapPosition == components.getSlapPosition().getPosition();
			Fingerprint fingerprint = capturedFingerprints.get(position);
			if(currentSlap && fingerprint.isSkipped())
		    {
			    fingerprint.setDmFingerData(null);
		    }
		});
		
		if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedRightSlapFingerprints"));
		}
		else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedLeftSlapFingerprints"));
		}
		else if(currentSlapPosition == FingerPosition.TWO_THUMBS.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
		}
		
		activateFingerIndicatorsForNextCapturing(++currentSlapPosition);
		renameCaptureFingerprintsButton(false);
		GuiUtils.showNode(btnStartOver, true);
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
			GuiUtils.showNode(btnAcceptBestAttemptFingerprints, false);
			GuiUtils.showNode(lblStatus, false);
			GuiUtils.showNode(ivCompleted, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			fingerprintUiComponentsMap.forEach((position, components) ->
			{
				components.getImageView().setImage(null);
				components.getCheckBox().setDisable(false);
				components.getCheckBox().setSelected(true);
				components.getTitledPane().setActive(false);
				components.getTitledPane().setCaptured(false);
				components.getTitledPane().setValid(false);
				components.getTitledPane().setDuplicated(false);
			});
			
			capturedFingerprints.clear();
			retryActive = false;
			currentSlapPosition = FingerPosition.RIGHT_SLAP.getPosition();
			activateFingerIndicatorsForNextCapturing(currentSlapPosition);
			renameCaptureFingerprintsButton(false);
		}
	}
	
	private void showSegmentedFingerprints(List<DMFingerData> fingerData, Map<Integer, Boolean> duplicatedFingers)
	{
		retryActive = false;
		boolean[] allAcceptableQuality = {true};
		boolean[] noDuplicates = {true};
		showFingerprints(fingerData, duplicatedFingers, allAcceptableQuality, noDuplicates, false);
		
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
			
			currentSlapAttempts.clear();
			currentSlapPosition++;
			renameCaptureFingerprintsButton(false);
		}
		else
		{
			GuiUtils.showNode(btnStartOver, currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition());
			lblStatus.setText(resources.getString("label.status.someFingerprintsAreNotAcceptable"));
			
			renameCaptureFingerprintsButton(true);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			// in case we have no duplicates and we can accept bad quality fingerprints, we will save this attempt
			if(noDuplicates[0] && acceptBadQualityFingerprint)
			{
				currentSlapAttempts.add(fingerData);
				
				if(currentSlapAttempts.size() >= acceptedBadQualityFingerprintMinRetires)
													GuiUtils.showNode(btnAcceptBestAttemptFingerprints, true);
			}
		}
		
		activateFingerIndicatorsForNextCapturing(currentSlapPosition);
	}
	
	private void showFingerprints(List<DMFingerData> fingerData, Map<Integer, Boolean> duplicatedFingers,
	                              boolean[] allAcceptableQuality, boolean[] noDuplicates, boolean forceAcceptedQuality)
	{
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
		
		    boolean acceptableQuality = acceptableFingerprintNfiq && acceptableFingerprintMinutiaeCount &&
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
		
		    if(allAcceptableQuality != null) allAcceptableQuality[0] = allAcceptableQuality[0] && acceptableQuality;
			if(noDuplicates != null) noDuplicates[0] = noDuplicates[0] && !duplicated;
			if(forceAcceptedQuality) acceptableQuality = true;
			
			Fingerprint fingerprint = new Fingerprint(dmFingerData, acceptableFingerprintNfiq,
											acceptableFingerprintMinutiaeCount, acceptableFingerprintImageIntensity,
								            acceptableQuality, duplicated, false);
			capturedFingerprints.put(dmFingerData.getPosition(), fingerprint);
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
		titledPane.setValid(fingerprint.isAcceptableQuality());
		titledPane.setDuplicated(fingerprint.isDuplicated());
		
		StackPane titleRegion = (StackPane) titledPane.lookup(".title");
		attachFingerprintResultTooltip(titleRegion, titledPane,
		                               fingerprint.getDmFingerData().getNfiqQuality(),
		                               fingerprint.getDmFingerData().getMinutiaeCount(),
		                               fingerprint.getDmFingerData().getIntensity(),
		                               fingerprint.isAcceptableFingerprintNfiq(),
		                               fingerprint.isAcceptableFingerprintMinutiaeCount(),
		                               fingerprint.isAcceptableFingerprintImageIntensity(),
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
		retryActive = retry;
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