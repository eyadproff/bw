package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

import java.util.function.Consumer;

@FxmlFile("showingPersonInfo.fxml")
public class ShowingPersonInfoFxController extends WizardStepFxControllerBase {
    @Input private PersonInfo personInfo;
    @Input private String passportId;
    @Input private Boolean fingerprintsExist;
    @Output private NormalizedPersonInfo normalizedPersonInfo;

    @FXML private ScrollPane infoPane;
    @FXML private VBox infoVBox;
    @FXML private ImageView ivPersonPhoto;
    @FXML private Label lblFirstName;
    @FXML private Label lblFatherName;
    @FXML private Label lblGrandfatherName;
    @FXML private Label lblFamilyName;
    @FXML private Label lblGender;
    @FXML private Label lblNationality;
    @FXML private Label lblOccupation;
    @FXML private Label lblBirthPlace;
    @FXML private Label lblBirthDate;
    @FXML private Label lblPersonId;
    @FXML private Label lblPassportId;
    @FXML private Label lblPersonType;
    @FXML private Label lblDocumentId;
    @FXML private Label lblDocumentType;
    @FXML private Label lblDocumentIssuanceDate;
    @FXML private Label lblDocumentExpiryDate;
    @FXML private Label lblNaturalizedSaudi;
    @FXML private ProgressIndicator piProgress;
    @FXML private Button btnStartOver;
    @FXML private Button btnConfirmPersonInfo;

    private boolean incrementOneStep = false;

    @Override
    protected void onAttachedToScene() {
        normalizedPersonInfo = new NormalizedPersonInfo(personInfo);

        GuiUtils.showNode(lblNaturalizedSaudi, normalizedPersonInfo.getNationality() != null &&
                                               normalizedPersonInfo.getNationality().getCode() > 0 &&
                                               !"SAU".equalsIgnoreCase(normalizedPersonInfo.getNationality().getMofaNationalityCode()) &&
                                               String.valueOf(normalizedPersonInfo.getPersonId()).startsWith("1"));

        String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
        Gender gender = normalizedPersonInfo.getGender();
        GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);

        String notAvailable = resources.getString("label.notAvailable");
        Consumer<Label> consumer = label ->
        {
            label.setText(notAvailable);
            label.setTextFill(Color.RED);
        };

        GuiUtils.setLabelText(lblFirstName, normalizedPersonInfo.getFirstNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblFatherName, normalizedPersonInfo.getFatherNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblGrandfatherName, normalizedPersonInfo.getGrandfatherNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblFamilyName, normalizedPersonInfo.getFamilyNameLabel()).orElse(consumer);
        GuiUtils.setLabelText(lblGender, normalizedPersonInfo.getGender()).orElse(consumer);
        GuiUtils.setLabelText(lblNationality, normalizedPersonInfo.getNationality()).orElse(consumer);
        GuiUtils.setLabelText(lblOccupation, normalizedPersonInfo.getOccupation()).orElse(consumer);
        GuiUtils.setLabelText(lblBirthPlace, normalizedPersonInfo.getBirthPlace()).orElse(consumer);
        GuiUtils.setLabelText(lblBirthDate, true, normalizedPersonInfo.getBirthDate()).orElse(consumer);
        GuiUtils.setLabelText(lblPersonId, normalizedPersonInfo.getPersonId()).orElse(consumer);
        GuiUtils.setLabelText(lblPassportId, passportId).orElse(consumer);
        GuiUtils.setLabelText(lblPersonType, normalizedPersonInfo.getPersonType()).orElse(consumer);
        GuiUtils.setLabelText(lblDocumentId, normalizedPersonInfo.getDocumentId()).orElse(consumer);
        GuiUtils.setLabelText(lblDocumentType, normalizedPersonInfo.getDocumentType()).orElse(consumer);
        GuiUtils.setLabelText(lblDocumentIssuanceDate, true, normalizedPersonInfo.getDocumentIssuanceDate()).orElse(consumer);
        GuiUtils.setLabelText(lblDocumentExpiryDate, true, normalizedPersonInfo.getDocumentExpiryDate()).orElse(consumer);

        infoPane.autosize();
        btnConfirmPersonInfo.requestFocus();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {

        String showingQualityFingerprintsView = resources.getString("wizard.showQualityFingerprintsView");
        String showingFingerprintsView = resources.getString("wizard.showFingerprintsView");
        String fingerprintCapturing = resources.getString("wizard.fingerprintCapturing");

        int stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex()).getStepIndexByTitle(showingQualityFingerprintsView);

        if (stepIndex < 0) {
            stepIndex = Context.getCoreFxController().getWizardPane(getTabIndex())
                    .getStepIndexByTitle(fingerprintCapturing);
        }
        final int finalStepIndex = stepIndex;


        if (successfulResponse) {
            if (fingerprintsExist != null && fingerprintsExist) {
                if (incrementOneStep) {
                    Context.getCoreFxController().getWizardPane(getTabIndex()).removeStep(finalStepIndex);
                    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, showingQualityFingerprintsView, "\\uf256");
                    incrementOneStep = false;
                }
                else {

                    Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, showingQualityFingerprintsView, "\\uf256");
                }

                goNext();
            }
            else {

                String headerText = resources.getString("CriminalClearanceReport.capturingFingerprints.confirmation.header");
                String contentText = resources.getString("CriminalClearanceReport.capturingFingerprints.confirmation.message");
                boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);

                if (confirmed) {
                    if (!incrementOneStep) {
                        Context.getCoreFxController().getWizardPane(getTabIndex()).updateStep(finalStepIndex, fingerprintCapturing, "\\uf256");
                        Context.getCoreFxController().getWizardPane(getTabIndex()).addStep(finalStepIndex + 1,
                                showingFingerprintsView,
                                "\\uf256");
                        incrementOneStep = true;
                    }
                    goNext();
                }
            }
        }
    }

    @Override
    public void onShowingProgress(boolean bShow) {
        GuiUtils.showNode(piProgress, bShow);
        btnConfirmPersonInfo.setDisable(bShow);
        btnStartOver.setDisable(bShow);
        infoVBox.setDisable(bShow);
    }

    @FXML
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        continueWorkflow();
    }
}