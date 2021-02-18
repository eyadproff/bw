package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.biokit.websocket.beans.DMFingerData;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Fingerprint;
import sa.gov.nic.bio.bw.core.beans.FingerprintQualityThreshold;
import sa.gov.nic.bio.bw.core.beans.UserSession;
import sa.gov.nic.bio.bw.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.FingerprintUiComponents;
import sa.gov.nic.bio.bw.workflow.commons.ui.FourStateTitledPane;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FxmlFile("showingFingerprintsQuality.fxml")
public class ShowingFingerprintsQualityPaneFxController extends WizardStepFxControllerBase {

    @Input(alwaysRequired = true) private List<Fingerprint> fingerprints;
    @Output private List<Integer> missingFingerprints;
    @Output private ServiceType serviceType;

    @FXML private ImageView ivRightThumb;
    @FXML private ImageView ivRightIndex;
    @FXML private ImageView ivRightMiddle;
    @FXML private ImageView ivRightRing;
    @FXML private ImageView ivRightLittle;
    @FXML private ImageView ivLeftThumb;
    @FXML private ImageView ivLeftIndex;
    @FXML private ImageView ivLeftMiddle;
    @FXML private ImageView ivLeftRing;
    @FXML private ImageView ivLeftLittle;
    @FXML private ProgressIndicator piProgress;
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
    @FXML private Button btnRightHandLegend;
    @FXML private Button btnLeftHandLegend;
    @FXML private Button btnStartOver;
    @FXML private Button btnInquiry;
    @FXML private Button btnReTakeFingerprints;

    private Map<Integer, FingerprintQualityThreshold> fingerprintQualityThresholdMap;
    private Map<Integer, FingerPosition> fingerprintsPosition = new HashMap<>();
    private Map<Integer, FingerprintUiComponents> fingerprintUiComponentsMap = new HashMap<>();

    private boolean addTwoSteps = false;

    public enum ServiceType {
        RETAKE_FINGERPRINT,
        INQUIRY
    }

    @Override
    protected void onAttachedToScene() {

        if (serviceType == ServiceType.RETAKE_FINGERPRINT) { addTwoSteps = true; }

        @SuppressWarnings("unchecked")
        List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
        String recaptureRole = Context.getConfigManager().getProperty("criminalClearance.roles.fingerprints.recapture");
        boolean authorized = userRoles.contains(recaptureRole);
        GuiUtils.showNode(btnReTakeFingerprints, authorized);

        Glyph glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
        btnRightHandLegend.setGraphic(glyph);
        glyph = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.INFO_CIRCLE);
        btnLeftHandLegend.setGraphic(glyph);

        attachFingerprintLegendTooltip(btnRightHandLegend);
        attachFingerprintLegendTooltip(btnLeftHandLegend);

        missingFingerprints = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        fingerprints.stream().mapToInt(x -> x.getDmFingerData().getPosition()).boxed().forEach(missingFingerprints::remove);

        fingerprintUiComponentsMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
                new FingerprintUiComponents(FingerPosition.RIGHT_THUMB, FingerPosition.TWO_THUMBS, ivRightThumb,
                        null, tpRightThumb, null,
                        resources.getString("label.fingers.thumb"),
                        resources.getString("label.rightHand")));
        fingerprintUiComponentsMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
                new FingerprintUiComponents(FingerPosition.RIGHT_INDEX, FingerPosition.RIGHT_SLAP, ivRightIndex,
                        null, tpRightIndex, null,
                        resources.getString("label.fingers.index"),
                        resources.getString("label.rightHand")));
        fingerprintUiComponentsMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
                new FingerprintUiComponents(FingerPosition.RIGHT_MIDDLE, FingerPosition.RIGHT_SLAP, ivRightMiddle,
                        null, tpRightMiddle, null,
                        resources.getString("label.fingers.middle"),
                        resources.getString("label.rightHand")));
        fingerprintUiComponentsMap.put(FingerPosition.RIGHT_RING.getPosition(),
                new FingerprintUiComponents(FingerPosition.RIGHT_RING, FingerPosition.RIGHT_SLAP, ivRightRing,
                        null, tpRightRing, null,
                        resources.getString("label.fingers.ring"),
                        resources.getString("label.rightHand")));
        fingerprintUiComponentsMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
                new FingerprintUiComponents(FingerPosition.RIGHT_LITTLE, FingerPosition.RIGHT_SLAP, ivRightLittle,
                        null, tpRightLittle, null,
                        resources.getString("label.fingers.little"),
                        resources.getString("label.rightHand")));
        fingerprintUiComponentsMap.put(FingerPosition.LEFT_THUMB.getPosition(),
                new FingerprintUiComponents(FingerPosition.LEFT_THUMB, FingerPosition.TWO_THUMBS, ivLeftThumb,
                        null, tpLeftThumb, null,
                        resources.getString("label.fingers.thumb"),
                        resources.getString("label.leftHand")));
        fingerprintUiComponentsMap.put(FingerPosition.LEFT_INDEX.getPosition(),
                new FingerprintUiComponents(FingerPosition.LEFT_INDEX, FingerPosition.LEFT_SLAP, ivLeftIndex,
                        null, tpLeftIndex, null,
                        resources.getString("label.fingers.index"),
                        resources.getString("label.leftHand")));
        fingerprintUiComponentsMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
                new FingerprintUiComponents(FingerPosition.LEFT_MIDDLE, FingerPosition.LEFT_SLAP, ivLeftMiddle,
                        null, tpLeftMiddle, null,
                        resources.getString("label.fingers.middle"),
                        resources.getString("label.leftHand")));
        fingerprintUiComponentsMap.put(FingerPosition.LEFT_RING.getPosition(),
                new FingerprintUiComponents(FingerPosition.LEFT_RING, FingerPosition.LEFT_SLAP, ivLeftRing,
                        null, tpLeftRing, null,
                        resources.getString("label.fingers.ring"),
                        resources.getString("label.leftHand")));
        fingerprintUiComponentsMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
                new FingerprintUiComponents(FingerPosition.LEFT_LITTLE, FingerPosition.LEFT_SLAP, ivLeftLittle,
                        null, tpLeftLittle, null,
                        resources.getString("label.fingers.little"),
                        resources.getString("label.leftHand")));


        // retrieve the quality threshold for each finger from the user session cache, or construct it and cache it
        UserSession userSession = Context.getUserSession();
        @SuppressWarnings("unchecked")
        Map<Integer, FingerprintQualityThreshold> persistedMap = (Map<Integer, FingerprintQualityThreshold>)
                userSession.getAttribute("lookups.fingerprint.qualityThresholdMap");
        if (persistedMap != null) { fingerprintQualityThresholdMap = persistedMap; }
        else {
            fingerprintQualityThresholdMap = new HashMap<>();
            fingerprintUiComponentsMap.forEach((position, components) ->
                    fingerprintQualityThresholdMap.put(components.getFingerPosition().getPosition(),
                            new FingerprintQualityThreshold(components.getFingerPosition())));
            userSession.setAttribute("lookups.fingerprint.qualityThresholdMap", fingerprintQualityThresholdMap);
        }

        boolean disableInquiry = fingerprints.isEmpty();
        btnInquiry.setDisable(disableInquiry);
        if (!disableInquiry) { btnInquiry.requestFocus(); }

        showSlapFingerprints(fingerprints);
    }

    private void showProgress(boolean bShow) {
        GuiUtils.showNode(btnStartOver, !bShow);
        GuiUtils.showNode(btnInquiry, !bShow);
        GuiUtils.showNode(piProgress, bShow);
    }

    private void attachFingerprintLegendTooltip(Node targetNode) {

        TextField txtLowQualityFingerprint = new TextField(
                resources.getString("label.fingerprint.legend.lowQualityFingerprint"));
        TextField txtHighQualityFingerprint = new TextField(
                resources.getString("label.fingerprint.legend.highQualityFingerprint"));
        TextField txtSkippedFingerprint = new TextField(
                resources.getString("label.fingerprint.legend.skippedFingerprint"));

        txtLowQualityFingerprint.setEditable(false);
        txtHighQualityFingerprint.setEditable(false);
        txtSkippedFingerprint.setEditable(false);
        txtLowQualityFingerprint.setFocusTraversable(false);
        txtHighQualityFingerprint.setFocusTraversable(false);
        txtSkippedFingerprint.setFocusTraversable(false);
        txtLowQualityFingerprint.getStyleClass().add("legend-yellow");
        txtHighQualityFingerprint.getStyleClass().add("legend-green");
        txtSkippedFingerprint.getStyleClass().add("legend-gray");
        txtSkippedFingerprint.setDisable(true);

        VBox vBox = new VBox(5.0, txtLowQualityFingerprint,
                txtHighQualityFingerprint, txtSkippedFingerprint);
        vBox.getStylesheets().setAll("/sa/gov/nic/bio/bw/workflow/commons/css/style.css");
        vBox.setPadding(new Insets(10.0));

        // hardcoded :(
        if (Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
            vBox.setPrefWidth(210.0);
        }
        else { vBox.setPrefWidth(263.0); }

        BorderPane root = new BorderPane(vBox);

        PopOver popOver = new PopOver(root);
        popOver.setDetachable(false);
        popOver.setConsumeAutoHidingEvents(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

        targetNode.setOnMouseClicked(event ->
        {
            if (popOver.isShowing()) { popOver.hide(); }
            else { popOver.show(targetNode); }
        });
    }

    private void showSlapFingerprints(List<Fingerprint> fingerprints) {

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

            fingerprint.setAcceptableFingerprintNfiq(acceptableFingerprintNfiq);
            fingerprint.setAcceptableFingerprintMinutiaeCount(acceptableFingerprintMinutiaeCount);
            fingerprint.setAcceptableFingerprintImageIntensity(acceptableFingerprintImageIntensity);
            fingerprint.setAcceptableQuality(acceptableQuality);

            fingerprint.setSkipped(false);

            showFingerprint(fingerprint, components.getImageView(), components.getTitledPane(),
                    components.getHandLabel(), components.getFingerLabel());
        });
    }

    private void showFingerprint(Fingerprint fingerprint, ImageView imageView, FourStateTitledPane titledPane,
            String handLabel, String fingerLabel) {

        String fingerprintImageBase64 = fingerprint.getDmFingerData().getFinger();
        byte[] fingerprintImageBytes = Base64.getDecoder().decode(fingerprintImageBase64);
        imageView.setImage(new Image(new ByteArrayInputStream(fingerprintImageBytes)));

        titledPane.setActive(true);
        titledPane.setValid(fingerprint.isAcceptableQuality());


        StackPane titleRegion = (StackPane) titledPane.lookup(".title");
        attachFingerprintResultTooltip(titleRegion, titledPane,
                fingerprint.getDmFingerData().getNfiqQuality(),
                fingerprint.getDmFingerData().getMinutiaeCount(),
                fingerprint.getDmFingerData().getIntensity(),
                fingerprint.isAcceptableFingerprintNfiq(),
                fingerprint.isAcceptableFingerprintMinutiaeCount(),
                fingerprint.isAcceptableFingerprintImageIntensity());

        String dialogTitle = fingerLabel + " (" + handLabel + ")";
        GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView, dialogTitle,
                resources.getString("label.contextMenu.showImage"), false);
    }


    private void attachFingerprintResultTooltip(Node sourceNode, Node targetNode, int nfiq, int minutiaeCount,
            int imageIntensity, boolean acceptableNfiq,
            boolean acceptableMinutiaeCount, boolean acceptableImageIntensity) {

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10.0));
        gridPane.setVgap(5.0);
        gridPane.setHgap(5.0);

        Label lblNfiq = new Label(resources.getString("label.tooltip.nfiq"));
        Label lblMinutiaeCount = new Label(resources.getString("label.tooltip.minutiaeCount"));
        Label lblIntensity = new Label(resources.getString("label.tooltip.imageIntensity"));

        Image successImage = new Image(CommonImages.ICON_SUCCESS_16PX.getAsInputStream());
        Image warningImage = new Image(CommonImages.ICON_WARNING_16PX.getAsInputStream());


        lblNfiq.setGraphic(new ImageView(acceptableNfiq ? successImage : warningImage));
        lblMinutiaeCount.setGraphic(new ImageView(acceptableMinutiaeCount ? successImage : warningImage));
        lblIntensity.setGraphic(new ImageView(acceptableImageIntensity ? successImage : warningImage));


        gridPane.add(lblNfiq, 0, 0);
        gridPane.add(lblMinutiaeCount, 0, 1);
        gridPane.add(lblIntensity, 0, 2);


        String sNfiq = AppUtils.localizeNumbers(String.valueOf(nfiq));
        String sMinutiaeCount = AppUtils.localizeNumbers(String.valueOf(minutiaeCount));
        String sIntensity = AppUtils.localizeNumbers(String.valueOf(imageIntensity)) + "%";


        TextField txtNfiq = new TextField(sNfiq);
        TextField txtMinutiaeCount = new TextField(sMinutiaeCount);
        TextField txtIntensity = new TextField(sIntensity);


        txtNfiq.setFocusTraversable(false);
        txtMinutiaeCount.setFocusTraversable(false);
        txtIntensity.setFocusTraversable(false);

        txtNfiq.setEditable(false);
        txtMinutiaeCount.setEditable(false);
        txtIntensity.setEditable(false);

        txtNfiq.setPrefColumnCount(3);
        txtMinutiaeCount.setPrefColumnCount(3);
        txtIntensity.setPrefColumnCount(3);


        gridPane.add(txtNfiq, 1, 0);
        gridPane.add(txtMinutiaeCount, 1, 1);
        gridPane.add(txtIntensity, 1, 2);


        PopOver popOver = new PopOver(gridPane);
        popOver.setDetachable(false);
        popOver.setConsumeAutoHidingEvents(false);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);

        sourceNode.setOnMouseClicked(mouseEvent ->
        {
            if (popOver.isShowing()) { popOver.hide(); }
            else { popOver.show(targetNode); }
        });
    }

    @FXML
    private void onReTakeFingerprintsClicked(ActionEvent actionEvent) {

        serviceType = ServiceType.RETAKE_FINGERPRINT;

        String inquiryByFingerprintsInCriminal = resources.getString("wizard.inquiryByFingerprintsInCriminal");
        String showingFingerprintsView = resources.getString("wizard.showFingerprintsView");
        String fingerprintCapturing = resources.getString("wizard.fingerprintCapturing");

        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(fingerprintCapturing);

        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(inquiryByFingerprintsInCriminal);
        }
        final int finalStepIndex = stepIndex;


        if (!addTwoSteps) {
            Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex,
                    fingerprintCapturing,
                    "\\uf256");
            Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex + 1,
                    showingFingerprintsView,
                    "\\uf256");

            addTwoSteps = true;
        }
        continueWorkflow();
    }

    @FXML
    protected void onNextButtonClicked(ActionEvent actionEvent) {

        serviceType = ServiceType.INQUIRY;

        String inquiryByFingerprintsInCriminal = resources.getString("wizard.inquiryByFingerprintsInCriminal");
        String fingerprintCapturing = resources.getString("wizard.fingerprintCapturing");

        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(fingerprintCapturing);

        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(inquiryByFingerprintsInCriminal);
//            addTwoSteps = false;
        }
        final int finalStepIndex = stepIndex;


        if (addTwoSteps) {
            Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);
            Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);

            Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, inquiryByFingerprintsInCriminal, "search");
            addTwoSteps = false;
        }
        else {
            Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, inquiryByFingerprintsInCriminal, "search");
        }

        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) { goNext(); }
    }
}