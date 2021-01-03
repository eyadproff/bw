package sa.gov.nic.bio.bw.workflow.commons.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
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
import sa.gov.nic.bio.bw.core.beans.FingerCoordinate;
import sa.gov.nic.bio.bw.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.core.beans.FingerprintQualityThreshold;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.*;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.beans.FingerprintUiComponents;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BuildNumberOfCaptureAttemptsReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;
import sa.gov.nic.bio.bw.workflow.commons.utils.CommonsErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FxmlFile("slapFingerprintsCapturing.fxml")
public class SlapFingerprintsCapturingFxController extends WizardStepFxControllerBase
{
	@Input private Boolean hidePreviousButton;
	@Input private Boolean hideFingerprintQualityFromTooltip;
	@Input private Boolean showPrintAndSaveNumOfTriesReportButton;
	@Input private Boolean showStartOverButton;
	@Input private Boolean allow9MissingWithNoRole;
	@Input private Boolean acceptBadQualityFingerprint;
	@Input private Integer acceptBadQualityFingerprintMinRetires;
	@Input private Boolean hideCheckBoxOfMissing;
	@Input private List<Integer> exceptionOfFingerprints;
	@Input private PersonInfo personInfo;
	@Output private Map<Integer, Fingerprint> capturedFingerprints;
	@Output private List<Finger> segmentedFingerprints;
	@Output private List<Finger> slapFingerprints;
	@Output private List<Finger> combinedFingerprints; // segmented and unsegmented
	@Output private Map<Integer, String> fingerprintBase64Images;
	@Output private List<Integer> missingFingerprints;
	@Output private Map<Integer, Integer> fingerprintNumOfTriesMapOutput;
	
	@FXML private VBox paneControlsInnerContainer;
	@FXML private ScrollPane paneControlsOuterContainer;
	@FXML private ProgressIndicator piProgress;
	@FXML private ProgressIndicator piFingerprintDeviceLivePreview;
	@FXML private Label lblStatus;
	@FXML private TitledPane tpRightHand;
	@FXML private TitledPane tpLeftHand;
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
	@FXML private Button btnStartOverFingerprintCapturing;
	@FXML private Button btnLivePreviewLegend;
	@FXML private Button btnRightHandLegend;
	@FXML private Button btnLeftHandLegend;
	@FXML private Button btnPrevious;
	@FXML private Button btnNext;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;
	
	private int currentSlapPosition = FingerPosition.RIGHT_SLAP.getPosition();
	private Map<Integer, FingerprintQualityThreshold> fingerprintQualityThresholdMap;
	private Map<Integer, FingerprintUiComponents> fingerprintUiComponentsMap = new HashMap<>();
	private Map<Integer, Integer> fingerprintNumOfTriesMap = new HashMap<>();
	private List<List<Fingerprint>> currentSlapAttempts = new ArrayList<>();
	private CaptureFingerprintResponse wrongSlapCapturedFingerprints;
	private boolean fingerprintDeviceInitializedAtLeastOnce = false;
	private boolean skipRightSlap = false;
	private boolean skipLeftSlap = false;
	private boolean skipThumbs = false;
	private boolean workflowStarted = false;
	private boolean workflowUserTaskLoaded = false;
	private AtomicBoolean stopCapturingIsInProgress = new AtomicBoolean();

	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
				resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);

		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		deviceManagerGadgetPaneController.setNextFingerprintDeviceType(FingerprintDeviceType.SLAP);
		
		btnNext.disableProperty().bind(ivCompleted.visibleProperty().not());
		
		paneControlsInnerContainer.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
		{
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


		if (showPrintAndSaveNumOfTriesReportButton != null && showPrintAndSaveNumOfTriesReportButton) {
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);
//			btnPrintRecord.disableProperty().bind(btnNext.disableProperty());
//			btnSaveRecordAsPDF.disableProperty().bind(btnNext.disableProperty());

		}
		if (fingerprintNumOfTriesMapOutput != null) { fingerprintNumOfTriesMap.putAll(fingerprintNumOfTriesMapOutput); }
		else {
			IntStream.rangeClosed(1, 10).boxed().forEach(x -> fingerprintNumOfTriesMap.put(x, 0));
		}

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
				
				if(allow9MissingWithNoRole != null && allow9MissingWithNoRole) userSkipAbility = 9;
				else for(int i = 10; i >= 1; i--)
				{
					String skipNRole = Context.getConfigManager().getProperty(
						String.format(AppConstants.Locales.SAUDI_EN_LOCALE, "fingerprint.roles.skip%02d", i));
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
                            totalSkipCount[0] - 1);
					
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
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				if(!workflowUserTaskLoaded) return;
				
				skipRightSlap = !cbRightIndex.isSelected() && !cbRightMiddle.isSelected() &&
								!cbRightRing.isSelected() && !cbRightLittle.isSelected();
				skipLeftSlap = !cbLeftIndex.isSelected() && !cbLeftMiddle.isSelected() &&
								!cbLeftRing.isSelected() && !cbLeftLittle.isSelected();
				skipThumbs = !cbRightThumb.isSelected() && !cbLeftThumb.isSelected();
				
				if(skipRightSlap && skipLeftSlap && skipThumbs)
				{
					btnStartFingerprintCapturing.setText(resources.getString("button.skipAllFingerprints"));
					currentSlapPosition = FingerPosition.TWO_THUMBS.getPosition() + 1;
				}
				else if(skipRightSlap && skipLeftSlap)
				{
					btnStartFingerprintCapturing.setText(resources.getString("button.captureThumbsFingerprints"));
					currentSlapPosition = FingerPosition.TWO_THUMBS.getPosition();
				}
				else if(skipRightSlap)
				{
					btnStartFingerprintCapturing.setText(
													resources.getString("button.captureLeftSlapFingerprints"));
					currentSlapPosition = FingerPosition.LEFT_SLAP.getPosition();
				}
				else
				{
					btnStartFingerprintCapturing.setText(
													resources.getString("button.captureRightSlapFingerprints"));
					currentSlapPosition = FingerPosition.RIGHT_SLAP.getPosition();
				}
				
				
				// activate/deactivate the fingerprint based on whether its slap is skipped or not
				fingerprintUiComponentsMap.forEach((position, components) ->
				{
					if(components.getSlapPosition() == FingerPosition.RIGHT_SLAP)
									components.getTitledPane().setActive(!skipRightSlap);
					else if(components.getSlapPosition() == FingerPosition.LEFT_SLAP)
									components.getTitledPane().setActive(skipRightSlap && !skipLeftSlap);
					else if(components.getSlapPosition() == FingerPosition.TWO_THUMBS)
									components.getTitledPane().setActive(skipRightSlap && skipLeftSlap && !skipThumbs);
					
				});
			}
		}
		
		cbRightIndex.selectedProperty().addListener(new CustomChangeListener());
		cbRightMiddle.selectedProperty().addListener(new CustomChangeListener());
		cbRightRing.selectedProperty().addListener(new CustomChangeListener());
		cbRightLittle.selectedProperty().addListener(new CustomChangeListener());
		cbLeftIndex.selectedProperty().addListener(new CustomChangeListener());
		cbLeftMiddle.selectedProperty().addListener(new CustomChangeListener());
		cbLeftRing.selectedProperty().addListener(new CustomChangeListener());
		cbLeftLittle.selectedProperty().addListener(new CustomChangeListener());
		cbRightThumb.selectedProperty().addListener(new CustomChangeListener());
		cbLeftThumb.selectedProperty().addListener(new CustomChangeListener());
		
		btnNext.disabledProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(!newValue) btnNext.requestFocus();
		});
		
		if(hidePreviousButton != null) GuiUtils.showNode(btnPrevious, !hidePreviousButton);
		if(showStartOverButton != null) GuiUtils.showNode(btnStartOver, showStartOverButton);
		
		// load the persisted captured fingerprints, if any
		if(capturedFingerprints != null && !capturedFingerprints.isEmpty())
		{
			workflowStarted = true;
			
			// disable all the checkboxes
			fingerprintUiComponentsMap.forEach((position, components) -> components.getCheckBox().setDisable(true));
			int[] skippedFingersCount = {0};
			
			capturedFingerprints.forEach((position, fingerprint) ->
			{
			    FingerprintUiComponents components = fingerprintUiComponentsMap.get(position);
			    currentSlapPosition = Math.max(currentSlapPosition, components.getSlapPosition().getPosition());
			
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
				GuiUtils.showNode(ivCompleted, true);
				GuiUtils.showNode(lblStatus, true);
				
				if(skippedFingersCount[0] == 10) lblStatus.setText(
						resources.getString("label.status.allFingerprintsAreSkipped"));
				else lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
			}
		}
		else capturedFingerprints = new HashMap<>();
		
		// register a listener to the event of the devices-runner being running or not
		deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
		{
		    if(running &&
		            !deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.SLAP))
		    {
		        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SLAP);
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
		        if(currentSlapPosition <= FingerPosition.TWO_THUMBS.getPosition()) GuiUtils.showNode(
		                btnStartFingerprintCapturing, true);
		        GuiUtils.showNode(lblStatus, true);
		        lblStatus.setText(resources.getString(
		                "label.status.fingerprintScannerInitializedSuccessfully"));
		        activateFingerIndicatorsForNextCapturing(currentSlapPosition);
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
		if(deviceManagerGadgetPaneController.isFingerprintScannerInitialized(FingerprintDeviceType.SLAP))
		{
			if(currentSlapPosition <= FingerPosition.TWO_THUMBS.getPosition()) GuiUtils.showNode(
					btnStartFingerprintCapturing, true);
			activateFingerIndicatorsForNextCapturing(currentSlapPosition);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, workflowStarted);
		}
		else if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
		{
			deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SLAP);
		}
		else
		{
			GuiUtils.showNode(lblStatus, true);
			lblStatus.setText(resources.getString("label.status.fingerprintScannerNotInitialized"));
			deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
		}
		
		workflowUserTaskLoaded = true;


		// disable all the checkboxes if We do not have the choice to select MissingFingerPrint from the scene
		// make Missing finger unselected
		if (hideCheckBoxOfMissing != null && hideCheckBoxOfMissing) {

			if (exceptionOfFingerprints != null) {
				if (exceptionOfFingerprints.size() >= 10) { showWarningNotification(resources.getString("AllFingerMissingWarning")); }
				else if (exceptionOfFingerprints.size() > 0) { showWarningNotification(resources.getString("ExceptionsWarning")); }
			}
			fingerprintUiComponentsMap.forEach((position, components) -> {
				components.getCheckBox().setVisible(false);
				components.getCheckBox().setSelected(exceptionOfFingerprints == null || !exceptionOfFingerprints.contains(position));
			});
		}
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
		
		// remove incomplete slaps
		boolean allOk = true;
		for(int i = FingerPosition.RIGHT_INDEX.getPosition(); i <= FingerPosition.RIGHT_LITTLE.getPosition(); i++)
		{
			Fingerprint fingerprint = capturedFingerprints.get(i);
			if(fingerprint == null)
			{
				if(currentSlapPosition > FingerPosition.RIGHT_SLAP.getPosition())
								capturedFingerprints.put(i, new Fingerprint(null, null,
								                                            null, false,
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
								capturedFingerprints.put(i, new Fingerprint(null, null,
								                                            null, false,
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
					capturedFingerprints.put(i, new Fingerprint(null, null, null,
					                                            false,
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
		fingerprintNumOfTriesMapOutput = fingerprintNumOfTriesMap;
		prepareFingerprintsForBackend(uiDataMap);
	}
	
	private void prepareFingerprintsForBackend(Map<String, Object> uiDataMap)
	{
		segmentedFingerprints = new ArrayList<>();
		slapFingerprints = new ArrayList<>();
		fingerprintBase64Images = new HashMap<>();
		missingFingerprints = new ArrayList<>();
		
		Map<Integer, Finger> collectedFingerprintsMap = new HashMap<>();
		Map<Integer, Integer> slapPositions = new HashMap<>();
		
		slapPositions.put(FingerPosition.RIGHT_THUMB.getPosition(),
		                  FingerPosition.RIGHT_THUMB_SLAP.getPosition());
		slapPositions.put(FingerPosition.RIGHT_INDEX.getPosition(),
		                  FingerPosition.RIGHT_SLAP.getPosition());
		slapPositions.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
		                  FingerPosition.RIGHT_SLAP.getPosition());
		slapPositions.put(FingerPosition.RIGHT_RING.getPosition(),
		                  FingerPosition.RIGHT_SLAP.getPosition());
		slapPositions.put(FingerPosition.RIGHT_LITTLE.getPosition(),
		                  FingerPosition.RIGHT_SLAP.getPosition());
		slapPositions.put(FingerPosition.LEFT_THUMB.getPosition(),
		                  FingerPosition.LEFT_THUMB_SLAP.getPosition());
		slapPositions.put(FingerPosition.LEFT_INDEX.getPosition(),
		                  FingerPosition.LEFT_SLAP.getPosition());
		slapPositions.put(FingerPosition.LEFT_MIDDLE.getPosition(),
		                  FingerPosition.LEFT_SLAP.getPosition());
		slapPositions.put(FingerPosition.LEFT_RING.getPosition(),
		                  FingerPosition.LEFT_SLAP.getPosition());
		slapPositions.put(FingerPosition.LEFT_LITTLE.getPosition(),
		                  FingerPosition.LEFT_SLAP.getPosition());
		
		capturedFingerprints.forEach((position, fingerprint) ->
		{
		    DMFingerData fingerData = fingerprint.getDmFingerData();
		    if(fingerData == null) // skipped fingerprint
		    {
		        missingFingerprints.add(position);
		        return;
		    }
		
		    String roundingBox = fingerData.getRoundingBox();
		    FingerCoordinate fingerCoordinate = AppUtils.constructFingerCoordinates(roundingBox);
		
		    fingerprintBase64Images.put(position, fingerData.getFinger());
		    segmentedFingerprints.add(new Finger(position, fingerData.getFingerWsqImage(), null));
		
		    int slapPosition = slapPositions.get(position);
		    if(collectedFingerprintsMap.containsKey(slapPosition))
		    {
		        Finger finger = collectedFingerprintsMap.get(slapPosition);
		        finger.getFingerCoordinates().add(fingerCoordinate);
		    }
		    else
		    {
		        Finger finger;
		        
		        if(slapPosition == FingerPosition.RIGHT_THUMB_SLAP.getPosition() ||
				   slapPosition == FingerPosition.LEFT_THUMB_SLAP.getPosition())
		        {
			        finger = new Finger(slapPosition, fingerData.getFingerWsqImage(), null);
		        }
		        else
		        {
			        List<FingerCoordinate> fingerCoordinates = new ArrayList<>();
			        fingerCoordinates.add(fingerCoordinate);
			        
		        	finger = new Finger(slapPosition, fingerprint.getSlapWsq(), fingerCoordinates);
		        }
		        
		        slapFingerprints.add(finger);
		        collectedFingerprintsMap.put(slapPosition, finger);
		    }
		});
		
		combinedFingerprints = new ArrayList<>();
		combinedFingerprints.addAll(slapFingerprints);
		combinedFingerprints.addAll(segmentedFingerprints);
		
		LOGGER.fine(new Gson().toJson(slapFingerprints,
		                                     TypeToken.getParameterized(List.class, Finger.class).getType()));
		LOGGER.fine(new Gson().toJson(missingFingerprints,
		                                     TypeToken.getParameterized(List.class, Integer.class).getType()));
	}
	
	@FXML
	private void onStartFingerprintCapturingButtonClicked(ActionEvent event)
	{
		workflowStarted = true;
		GuiUtils.showNode(btnStartFingerprintCapturing, false);
		GuiUtils.showNode(lblStatus, true);
		
		// disable all the checkboxes
		fingerprintUiComponentsMap.forEach((position, components) -> components.getCheckBox().setDisable(true));
		
		if(skipRightSlap && skipLeftSlap && skipThumbs)
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
		GuiUtils.showNode(btnAcceptCurrentSlap, false);
		GuiUtils.showNode(btnAcceptBestAttemptFingerprints, false);
		
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
			if(components.getSlapPosition().getPosition() == currentSlapPosition)
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
		
		Task<TaskResponse<CaptureFingerprintResponse>> capturingFingerprintTask =
																new Task<TaskResponse<CaptureFingerprintResponse>>()
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
				Future<TaskResponse<CaptureFingerprintResponse>> future = Context.getBioKitManager()
						.getFingerprintService().startPreviewAndAutoCapture(fingerprintDeviceName,
						                                                    currentSlapPosition,
						                                                    expectedFingersCount[0], missingFingers,
						                                                    true, true, true, responseProcessor);
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
				        GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
				        
				        // save the captured fingerprints
				        wrongSlapCapturedFingerprints = result;
				
				        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivFingerprintDeviceLivePreview,
				                                   resources.getString("label.contextMenu.slapFingerprints"),
				                                   resources.getString("label.contextMenu.showImage"), false);
			        }
			        else processCaptureFingerprintResponse(result);
		        }
		        // we skip FAILED_TO_CAPTURE_FINAL_IMAGE because it happens only when we send "stop" command
		        else
		        {
			        GuiUtils.showNode(piProgress, false);
			        GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			        GuiUtils.showNode(btnStartFingerprintCapturing, true);
			        GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
			        
			        fingerprintUiComponentsMap.forEach((integer, components) ->
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
				        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SLAP);
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
					        deviceManagerGadgetPaneController.initializeFingerprintScanner(FingerprintDeviceType.SLAP);
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
		        
		        fingerprintUiComponentsMap.forEach((integer, components) ->
				                                           GuiUtils.showNode(components.getSvgPath(), false));
		        
		        lblStatus.setText(String.format(firstLivePreviewingResponse[0] ?
				        resources.getString("label.status.failedToStartFingerprintCapturingWithErrorCode") :
				        resources.getString("label.status.failedToCaptureFingerprintsWithErrorCode"),
		                                        taskResponse.getErrorCode()));
		
		        String errorCode = firstLivePreviewingResponse[0] ? CommonsErrorCodes.C008_00008.getCode() :
			                                                        CommonsErrorCodes.C008_00009.getCode();
		        String[] errorDetails = {firstLivePreviewingResponse[0] ?
				        "failed while starting the fingerprint capturing!" :
				        "failed while capturing the fingerprints!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
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
			
			fingerprintUiComponentsMap.forEach((integer, components) ->
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
				
				String errorCode = firstLivePreviewingResponse[0] ? CommonsErrorCodes.C008_00010.getCode() :
																	CommonsErrorCodes.C008_00011.getCode();
				String[] errorDetails = {firstLivePreviewingResponse[0] ?
										"failed while starting the fingerprint capturing!" :
										"failed while capturing the fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			}
		});
		
		Context.getExecutorService().submit(capturingFingerprintTask);
	}
	
	private void processCaptureFingerprintResponse(CaptureFingerprintResponse captureFingerprintResponse)
	{
		tpFingerprintDeviceLivePreview.setValid(true);
		
		String slapWsq = captureFingerprintResponse.getCapturedWsq();
		String slapImage = captureFingerprintResponse.getCapturedImage();
		
		List<DMFingerData> fingerData = captureFingerprintResponse.getFingerData();
		Task<TaskResponse<DuplicatedFingerprintsResponse>> findDuplicatesTask =
															new Task<TaskResponse<DuplicatedFingerprintsResponse>>()
		{
			@Override
			protected TaskResponse<DuplicatedFingerprintsResponse> call() throws Exception
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
			
			TaskResponse<DuplicatedFingerprintsResponse> taskResponse = findDuplicatesTask.getValue();
			
			List<Fingerprint> fingerprints = fingerData.stream()
											   .map(dmFingerData -> new Fingerprint(dmFingerData, slapWsq, slapImage))
											   .collect(Collectors.toList());
			
			if(taskResponse == null) // no duplicates
			{
				processSlapFingerprints(fingerprints, null);
			}
			else if(taskResponse.isSuccess())
			{
				DuplicatedFingerprintsResponse result = taskResponse.getResult();
				
				if(result.getReturnCode() == DuplicatedFingerprintsResponse.SuccessCodes.SUCCESS)
				{
					// it could be empty
				    Map<Integer, Boolean> duplicatedFingers = result.getDuplicatedFingers();
				    processSlapFingerprints(fingerprints, duplicatedFingers);
				}
				else
				{
					GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
					
					fingerprintUiComponentsMap.forEach((integer, components) ->
						                                   GuiUtils.showNode(components.getSvgPath(), false));
					
					lblStatus.setText(String.format(
							resources.getString("label.status.failedToFindDuplicatedFingerprintsWithErrorCode"),
                            result.getReturnCode()));
				}
			}
			else
			{
				GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
				
				fingerprintUiComponentsMap.forEach((integer, components) ->
					                                        GuiUtils.showNode(components.getSvgPath(), false));
				
				lblStatus.setText(String.format(
						resources.getString("label.status.failedToFindDuplicatedFingerprintsWithErrorCode"),
						taskResponse.getErrorCode()));
				
				String errorCode = CommonsErrorCodes.C008_00012.getCode();
				String[] errorDetails = {"failed while finding duplicated fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
			}
		});
		findDuplicatesTask.setOnFailed(e ->
		{
			GuiUtils.showNode(piFingerprintDeviceLivePreview, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
			
			fingerprintUiComponentsMap.forEach((integer, components) ->
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
				
				String errorCode = CommonsErrorCodes.C008_00013.getCode();
				String[] errorDetails = {"failed while finding duplicated fingerprints!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
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
		
		Task<TaskResponse<FingerprintStopPreviewResponse>> task =
															new Task<TaskResponse<FingerprintStopPreviewResponse>>()
		{
			@Override
			protected TaskResponse<FingerprintStopPreviewResponse> call() throws Exception
			{
				return future.get();
			}
		};
		
		task.setOnSucceeded(e ->
		{
			fingerprintUiComponentsMap.forEach((integer, components) ->
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
		        Context.getCoreFxController().showErrorDialog(errorCode, taskResponse.getException(), errorDetails, getTabIndex());
		    }
		});
		task.setOnFailed(e ->
		{
			fingerprintUiComponentsMap.forEach((integer, components) ->
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
		
		        String errorCode = CommonsErrorCodes.C008_00015.getCode();
		        String[] errorDetails = {"failed while stopping the fingerprint capturing!"};
		        Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		    }
		});
		
		stopCapturingIsInProgress.set(true);
		Context.getExecutorService().submit(task);
	}
	
	@FXML
	private void onAcceptCurrentSlapButtonClicked(ActionEvent event)
	{
		hideNotification();
		
		// check if the user has the permission to accept wrong slap
		@SuppressWarnings("unchecked")
		List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
		String acceptWrongHandRole = Context.getConfigManager().getProperty("fingerprint.roles.acceptWrongHand");
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
				                           resources.getString("label.contextMenu.showImage"), false);
				GuiUtils.showNode(btnAcceptCurrentSlap, false);
				processCaptureFingerprintResponse(wrongSlapCapturedFingerprints);
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
		
		List<Fingerprint> bestAttemptFingerprints = findBestAttemptFingerprints();
		currentSlapAttempts.clear();
		
		List<Integer> nonSkippedFingerprintPositions = new ArrayList<>(); // TODO: ???
		bestAttemptFingerprints.forEach(fp -> nonSkippedFingerprintPositions.add(fp.getDmFingerData().getPosition()));
		
		showSlapFingerprints(bestAttemptFingerprints, null, null, null,
		                    true);

		acceptFingerprintsAndGoNext();
	}
	
	private void logCurrentSlapAttempts(String prefix)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("\n\n");
		currentSlapAttempts.forEach(fingerprints ->
		{
		    fingerprints.forEach(fingerprint ->
		    {
		        DMFingerData dmFingerData = fingerprint.getDmFingerData();
		        sb.append("[");
			    sb.append(dmFingerData.getPosition());
			    sb.append(" ");
		        sb.append(fingerprint.isAcceptableQuality() ? 1 : 0);
		        sb.append(" ");
		        sb.append(dmFingerData.getNfiqQuality());
		        sb.append(" ");
		        sb.append(dmFingerData.getMinutiaeCount());
		        sb.append(" ");
		        sb.append(dmFingerData.getIntensity());
		        sb.append("]");
		        sb.append("\n");
		    });
		    sb.append("\n");
		});
		LOGGER.info(sb.toString());
	}
	
	private List<Fingerprint> findBestAttemptFingerprints()
	{
		logCurrentSlapAttempts("Before sorting: CurrentSlapAttempts = ");
		
		Predicate<Fingerprint> acceptanceFilter = fingerprint ->
		{
			DMFingerData dmFingerData = fingerprint.getDmFingerData();
			
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
		
		currentSlapAttempts.sort((o1, o2) ->
		{
		    int sum1 = (int) o1.stream().filter(acceptanceFilter).count();
		    int sum2 = (int) o2.stream().filter(acceptanceFilter).count();
		
		    int compare = -Integer.compare(sum1, sum2);
		    if(compare != 0) return compare;
		    else // if both have same amount of acceptable fingerprints
		    {
		        sum1 = o1.stream().filter(acceptanceFilter).mapToInt(fp -> fp.getDmFingerData().getPosition()).sum();
		        sum2 = o2.stream().filter(acceptanceFilter).mapToInt(fp -> fp.getDmFingerData().getPosition()).sum();
		
		        compare = Integer.compare(sum1, sum2);
		        if(compare != 0) return compare;
		        else // if both have same amount of acceptable fingerprints and they are the same fingerprints
		        {
		            Comparator<Fingerprint> prioritizingComparator = Comparator.comparingInt(
		                                                                    fp -> fp.getDmFingerData().getPosition());
		
		            // get list of fingerprints sorted by lowest positions first
		            List<Fingerprint> list1 = o1.stream().sorted(prioritizingComparator).collect(Collectors.toList());
		            List<Fingerprint> list2 = o2.stream().sorted(prioritizingComparator).collect(Collectors.toList());
		
		            for(int i = 0; i < list1.size(); i++)
		            {
		                DMFingerData fp1 = list1.get(i).getDmFingerData();
		                DMFingerData fp2 = list2.get(i).getDmFingerData();
		                compare = Integer.compare(fp1.getNfiqQuality(), fp2.getNfiqQuality());
		
		                if(compare != 0) return compare;
		                else // both have the same NFIQ
		                {
		                    compare = -Integer.compare(fp1.getMinutiaeCount(), fp2.getMinutiaeCount());
		
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
		                           fp2AcceptableFingerprintImageIntensity)
			                                        return -Integer.compare(fp1.getIntensity(), fp2.getIntensity());
		                        if(fp1AcceptableFingerprintImageIntensity) return -1;
		                        if(fp2AcceptableFingerprintImageIntensity) return 1;
		
		                        // here, both have bad image intensity
		
		                        if(fp1.getIntensity() >
				                         qualityThreshold.getMaximumAcceptableImageIntensity() &&
				                         fp2.getIntensity() <
						                         qualityThreshold.getMinimumAcceptableImageIntensity())
		                        {
			                         return -1;
		                        }
		                        if(fp1.getIntensity() <
				                         qualityThreshold.getMinimumAcceptableImageIntensity() &&
				                         fp2.getIntensity() >
						                         qualityThreshold.getMaximumAcceptableImageIntensity())
		                        {
			                         return 1;
		                        }
		                        else // both on the same side
		                        {
			                         // both above MaximumAcceptableImageIntensity
			                         if(fp1.getIntensity() >
					                         qualityThreshold.getMaximumAcceptableImageIntensity() &&
					                         fp2.getIntensity() >
							                         qualityThreshold.getMaximumAcceptableImageIntensity())
			                         {
				                         // take the lowest
				                         return Integer.compare(fp1.getIntensity(), fp2.getIntensity());
			                         }
			                         else // both under MinimumAcceptableImageIntensity
			                         {
				                         // take the highest
				                         return -Integer.compare(fp1.getIntensity(), fp2.getIntensity());
			                         }
		                        }
		                    }
		                }
		            }
		
		            return 0; // both identical
		        }
		    }
		});
		
		logCurrentSlapAttempts("After sorting: CurrentSlapAttempts = ");
		
		return currentSlapAttempts.get(0);
	}
	
	private void acceptFingerprintsAndGoNext()
	{
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
		    boolean currentSlap = currentSlapPosition == components.getSlapPosition().getPosition();
			Fingerprint fingerprint = capturedFingerprints.get(position);
			if(fingerprint != null && currentSlap && fingerprint.isSkipped())
		    {
			    fingerprint.setDmFingerData(null);
		    }
		});
		
		if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedRightSlapFingerprints"));
			
			if(skipLeftSlap)
			{
				currentSlapPosition++;
				if(skipThumbs) currentSlapPosition++;
			}
		}
		else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.acceptedLeftSlapFingerprints"));
			
			if(skipThumbs) currentSlapPosition++;
		}
		
		currentSlapPosition++;
		currentSlapAttempts.clear();
		
		if(currentSlapPosition > FingerPosition.TWO_THUMBS.getPosition())
		{
			lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
			GuiUtils.showNode(btnStartFingerprintCapturing, false);
			GuiUtils.showNode(ivCompleted, true);
		}
		else renameCaptureFingerprintsButton(false);
		
		activateFingerIndicatorsForNextCapturing(currentSlapPosition);
		GuiUtils.showNode(btnStartOverFingerprintCapturing, true);
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
			GuiUtils.showNode(btnAcceptCurrentSlap, false);
			GuiUtils.showNode(btnAcceptBestAttemptFingerprints, false);
			GuiUtils.showNode(lblStatus, false);
			GuiUtils.showNode(ivCompleted, false);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			fingerprintUiComponentsMap.forEach((position, components) ->
			{
				components.getImageView().setImage(null);
				components.getCheckBox().setDisable(false);
//				components.getCheckBox().setSelected(true);
				components.getCheckBox().setSelected(exceptionOfFingerprints == null || !exceptionOfFingerprints.contains(position));
				components.getTitledPane().setActive(false);
				components.getTitledPane().setCaptured(false);
				components.getTitledPane().setValid(false);
				components.getTitledPane().setDuplicated(false);
			});
			
			capturedFingerprints.clear();
			currentSlapAttempts.clear();
			fingerprintNumOfTriesMap.clear();
			IntStream.rangeClosed(1, 10).boxed().forEach(x -> fingerprintNumOfTriesMap.put(x, 0));
			currentSlapPosition = FingerPosition.RIGHT_SLAP.getPosition();
			activateFingerIndicatorsForNextCapturing(currentSlapPosition);
			renameCaptureFingerprintsButton(false);
		}
	}
	
	private void processSlapFingerprints(List<Fingerprint> fingerprints, Map<Integer, Boolean> duplicatedFingers)
	{
		// To save Number of Tries for each Finger
		fingerprints.forEach( fingerprint ->
		{
			Integer numOfTries = fingerprintNumOfTriesMap.get(fingerprint.getDmFingerData().getPosition());
			fingerprintNumOfTriesMap.put(fingerprint.getDmFingerData().getPosition(), ++numOfTries);
		});

		boolean[] allAcceptableQuality = {true};
		boolean[] noDuplicates = {true};
		showSlapFingerprints(fingerprints, duplicatedFingers, allAcceptableQuality, noDuplicates,
		                    false);
		GuiUtils.showNode(btnStartOverFingerprintCapturing, true);

		if(allAcceptableQuality[0])
		{
			if(currentSlapPosition == FingerPosition.RIGHT_SLAP.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedRightSlap"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
				
				if(skipLeftSlap)
				{
					currentSlapPosition++;
					if(skipThumbs) currentSlapPosition++;
				}
			}
			else if(currentSlapPosition == FingerPosition.LEFT_SLAP.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedLeftSlap"));
				GuiUtils.showNode(btnStartFingerprintCapturing, true);
				
				if(skipThumbs) currentSlapPosition++;
			}
			
			currentSlapPosition++;
			currentSlapAttempts.clear();
			
			if(currentSlapPosition > FingerPosition.TWO_THUMBS.getPosition())
			{
				lblStatus.setText(resources.getString("label.status.successfullyCapturedAllFingers"));
				GuiUtils.showNode(btnStartFingerprintCapturing, false);
				GuiUtils.showNode(ivCompleted, true);
			}
			else renameCaptureFingerprintsButton(false);
		}
		else
		{
			if(noDuplicates[0])
				lblStatus.setText(resources.getString("label.status.someFingerprintsAreNotAcceptable"));
			else lblStatus.setText(resources.getString("label.status.someFingerprintsAreDuplicated"));
			
			renameCaptureFingerprintsButton(true);
			GuiUtils.showNode(btnStartFingerprintCapturing, true);
			
			// in case we have no duplicates and we can accept bad quality fingerprints, we will save this attempt
			if(noDuplicates[0] && acceptBadQualityFingerprint != null && acceptBadQualityFingerprintMinRetires != null
																						&& acceptBadQualityFingerprint)
			{
				currentSlapAttempts.add(fingerprints);
				
				GuiUtils.showNode(btnAcceptBestAttemptFingerprints,
			                     currentSlapAttempts.size() >= acceptBadQualityFingerprintMinRetires);
			}
		}
		
		activateFingerIndicatorsForNextCapturing(currentSlapPosition);
	}
	
	private void showSlapFingerprints(List<Fingerprint> fingerprints, Map<Integer, Boolean> duplicatedFingers,
	                                  boolean[] allAcceptableQuality, boolean[] noDuplicates,
	                                  boolean forceAcceptedQuality)
	{
		fingerprints.forEach(fingerprint ->
		{
			DMFingerData dmFingerData = fingerprint.getDmFingerData();
			
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
			
			fingerprint.setAcceptableFingerprintNfiq(acceptableFingerprintNfiq);
			fingerprint.setAcceptableFingerprintMinutiaeCount(acceptableFingerprintMinutiaeCount);
			fingerprint.setAcceptableFingerprintImageIntensity(acceptableFingerprintImageIntensity);
			fingerprint.setAcceptableQuality(acceptableQuality);
			fingerprint.setDuplicated(duplicated);
			fingerprint.setSkipped(false);
			
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
		                               fingerprint.getDmFingerData().getIntensity(),fingerprintNumOfTriesMap.get(fingerprint.getDmFingerData().getPosition()),
		                               fingerprint.isAcceptableFingerprintNfiq(),
		                               fingerprint.isAcceptableFingerprintMinutiaeCount(),
		                               fingerprint.isAcceptableFingerprintImageIntensity(),
		                               fingerprint.isDuplicated());
		
		String dialogTitle = fingerLabel + " (" + handLabel + ")";
		GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, dialogTitle,
		                           resources.getString("label.contextMenu.showImage"), false);
	}
	
	private void activateFingerIndicatorsForNextCapturing(int slapPosition)
	{
		fingerprintUiComponentsMap.forEach((position, components) ->
		{
			boolean currentSlap = slapPosition == components.getSlapPosition().getPosition();
			
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
	                                            int imageIntensity, Integer numOfTries, boolean acceptableNfiq,
	                                            boolean acceptableMinutiaeCount, boolean acceptableImageIntensity,
	                                            boolean duplicated)
	{
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10.0));
		gridPane.setVgap(5.0);
		gridPane.setHgap(5.0);

		Image successImage = new Image(CommonImages.ICON_SUCCESS_16PX.getAsInputStream());
		Image warningImage = new Image(CommonImages.ICON_WARNING_16PX.getAsInputStream());
		Image errorImage = new Image(CommonImages.ICON_ERROR_16PX.getAsInputStream());
		FontAwesome.Glyph slackGlyph = FontAwesome.Glyph.REPEAT;
		Glyph slackIcon = AppUtils.createFontAwesomeIcon(slackGlyph);

		if(hideFingerprintQualityFromTooltip != null && hideFingerprintQualityFromTooltip)
		{
			Label lblDuplicatedFingerprint = new Label(resources.getString("label.tooltip.duplicatedFinger"));
			Label lblNTry = new Label(resources.getString("label.tooltip.ntry"));

			lblDuplicatedFingerprint.setGraphic(new ImageView(duplicated ? errorImage : successImage));
			lblNTry.setGraphic(slackIcon);

			gridPane.add(lblDuplicatedFingerprint, 0, 0);
			gridPane.add(lblNTry, 0, 1);

			String sDuplicatedFingerprint = resources.getString(duplicated ? "label.tooltip.yes" : "label.tooltip.no");
			String sNTry = AppUtils.localizeNumbers(String.valueOf(numOfTries));

			TextField txtDuplicatedFingerprint = new TextField(sDuplicatedFingerprint);
			TextField txtNTry = new TextField(sNTry);

			txtDuplicatedFingerprint.setFocusTraversable(false);
			txtNTry.setFocusTraversable(false);
			txtDuplicatedFingerprint.setEditable(false);
			txtNTry.setEditable(false);
			txtDuplicatedFingerprint.setPrefColumnCount(3);
			txtNTry.setPrefColumnCount(3);

			gridPane.add(txtDuplicatedFingerprint, 1, 0);
			gridPane.add(txtNTry, 1, 1);

		} else
		{
			Label lblNfiq = new Label(resources.getString("label.tooltip.nfiq"));
			Label lblMinutiaeCount = new Label(resources.getString("label.tooltip.minutiaeCount"));
			Label lblIntensity = new Label(resources.getString("label.tooltip.imageIntensity"));
			Label lblDuplicatedFingerprint = new Label(resources.getString("label.tooltip.duplicatedFinger"));
			Label lblNTry = new Label(resources.getString("label.tooltip.ntry"));

			lblNfiq.setGraphic(new ImageView(acceptableNfiq ? successImage : warningImage));
			lblMinutiaeCount.setGraphic(new ImageView(acceptableMinutiaeCount ? successImage : warningImage));
			lblIntensity.setGraphic(new ImageView(acceptableImageIntensity ? successImage : warningImage));
			lblDuplicatedFingerprint.setGraphic(new ImageView(duplicated ? errorImage : successImage));
			lblNTry.setGraphic(slackIcon);

			gridPane.add(lblNfiq, 0, 0);
			gridPane.add(lblMinutiaeCount, 0, 1);
			gridPane.add(lblIntensity, 0, 2);
			gridPane.add(lblDuplicatedFingerprint, 0, 3);
			gridPane.add(lblNTry, 0, 4);

			String sNfiq = AppUtils.localizeNumbers(String.valueOf(nfiq));
			String sMinutiaeCount = AppUtils.localizeNumbers(String.valueOf(minutiaeCount));
			String sIntensity = AppUtils.localizeNumbers(String.valueOf(imageIntensity)) + "%";
			String sDuplicatedFingerprint = resources.getString(duplicated ? "label.tooltip.yes" : "label.tooltip.no");
			String sNTry = AppUtils.localizeNumbers(String.valueOf(numOfTries));

			TextField txtNfiq = new TextField(sNfiq);
			TextField txtMinutiaeCount = new TextField(sMinutiaeCount);
			TextField txtIntensity = new TextField(sIntensity);
			TextField txtDuplicatedFingerprint = new TextField(sDuplicatedFingerprint);
			TextField txtNTry = new TextField(sNTry);

			txtNfiq.setFocusTraversable(false);
			txtMinutiaeCount.setFocusTraversable(false);
			txtIntensity.setFocusTraversable(false);
			txtDuplicatedFingerprint.setFocusTraversable(false);
			txtNTry.setFocusTraversable(false);
			txtNfiq.setEditable(false);
			txtMinutiaeCount.setEditable(false);
			txtIntensity.setEditable(false);
			txtDuplicatedFingerprint.setEditable(false);
			txtNTry.setEditable(false);
			txtNfiq.setPrefColumnCount(3);
			txtMinutiaeCount.setPrefColumnCount(3);
			txtIntensity.setPrefColumnCount(3);
			txtDuplicatedFingerprint.setPrefColumnCount(3);
			txtNTry.setPrefColumnCount(3);

			gridPane.add(txtNfiq, 1, 0);
			gridPane.add(txtMinutiaeCount, 1, 1);
			gridPane.add(txtIntensity, 1, 2);
			gridPane.add(txtDuplicatedFingerprint, 1, 3);
			gridPane.add(txtNTry, 1, 4);
		}
		
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
		vBox.getStylesheets().setAll("/sa/gov/nic/bio/bw/workflow/commons/css/style.css");
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

	@FXML
	private void onPrintRecordButtonClicked(ActionEvent actionEvent) {

		hideNotification();
		GuiUtils.showNode(btnPrintRecord, false);
		GuiUtils.showNode(btnSaveRecordAsPDF, false);

		BuildNumberOfCaptureAttemptsReportTask buildNumberOfCaptureAttemptsReportTask =
				new BuildNumberOfCaptureAttemptsReportTask(fingerprintNumOfTriesMap, personInfo);


		buildNumberOfCaptureAttemptsReportTask.setOnSucceeded(event ->
		{
			JasperPrint value = buildNumberOfCaptureAttemptsReportTask.getValue();
			jasperPrint.set(value);
			printReport(value);
		});
		buildNumberOfCaptureAttemptsReportTask.setOnFailed(event ->
		{
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);

			Throwable exception = buildNumberOfCaptureAttemptsReportTask.getException();

			String errorCode = CommonsErrorCodes.C008_00061.getCode();
			String[] errorDetails = {"failed while building the number of capture attempts report!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		});
		Context.getExecutorService().submit(buildNumberOfCaptureAttemptsReportTask);

	}

	@FXML
	private void onSaveRecordAsPdfButtonClicked(ActionEvent actionEvent) {
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());

		if (selectedFile != null) {
			GuiUtils.showNode(btnPrintRecord, false);
			GuiUtils.showNode(btnSaveRecordAsPDF, false);

			BuildNumberOfCaptureAttemptsReportTask buildNumberOfCaptureAttemptsReportTask =
					new BuildNumberOfCaptureAttemptsReportTask(fingerprintNumOfTriesMap, personInfo);

			buildNumberOfCaptureAttemptsReportTask.setOnSucceeded(event ->
			{
				JasperPrint value = buildNumberOfCaptureAttemptsReportTask.getValue();
				jasperPrint.set(value);
				try {
					saveReportAsPDF(value, selectedFile);
				} catch (Exception e) {
					GuiUtils.showNode(btnStartOver, true);
					GuiUtils.showNode(btnPrintRecord, true);
					GuiUtils.showNode(btnSaveRecordAsPDF, true);

					String errorCode = CommonsErrorCodes.C008_00062.getCode();
					String[] errorDetails = {"failed while saving the number of capture attempts report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
				}
			});
			buildNumberOfCaptureAttemptsReportTask.setOnFailed(event ->
			{
				GuiUtils.showNode(btnPrintRecord, true);
				GuiUtils.showNode(btnSaveRecordAsPDF, true);

				Throwable exception = buildNumberOfCaptureAttemptsReportTask.getException();

				String errorCode = CommonsErrorCodes.C008_00063.getCode();
				String[] errorDetails = {"failed while building the number of capture attempts report!"};
				Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
			});
			Context.getExecutorService().submit(buildNumberOfCaptureAttemptsReportTask);
		}
	}


	private void printReport(JasperPrint jasperPrint) {

		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);

			Throwable exception = printReportTask.getException();

			String errorCode = CommonsErrorCodes.C008_00064.getCode();
			String[] errorDetails = {"failed while printing the number of capture attempts report!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		});
		Context.getExecutorService().submit(printReportTask);
	}

	private void saveReportAsPDF(JasperPrint jasperPrint, File selectedFile)
			throws FileNotFoundException {
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
				new FileOutputStream(selectedFile));
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);

			showSuccessNotification(resources.getString("fingerprintNumberOfCaptureAttempts.savingAsPDF.success.message"));
			try {
				Desktop.getDesktop().open(selectedFile);
			} catch (Exception e) {
				LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
			}
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
			GuiUtils.showNode(btnPrintRecord, true);
			GuiUtils.showNode(btnSaveRecordAsPDF, true);

			Throwable exception = printReportTaskAsPdfTask.getException();

			String errorCode = CommonsErrorCodes.C008_00065.getCode();
			String[] errorDetails = {"failed while saving the number of capture attempts report as PDF!"};
			Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails, getTabIndex());
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}