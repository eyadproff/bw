package sa.gov.nic.bio.bw.workflow.citizenenrollment.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.CitizenEnrollmentInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase {

    @Input(alwaysRequired = true)
    private NormalizedPersonInfo normalizedPersonInfo;
    @Input(alwaysRequired = true)
    private PersonInfo personInfo;
    @Input(alwaysRequired = true)
    private Image facePhoto;
    @Input(alwaysRequired = true)
    private String facePhotoBase64;
    @Input(alwaysRequired = true)
    private Map<Integer, String> fingerprintBase64Images;
    @Input(alwaysRequired = true)
    private List<Finger> slapFingerprints;
    @Input(alwaysRequired = true)
    private List<Integer> missingFingerprints;
    @Input
    private List<Finger> combinedFingerprints;
    @Input
    private String capturedRightIrisBase64;
    @Input
    private String capturedLeftIrisBase64;
    @Input
    private Boolean ignorePossibilityAndCompleteWorkflow;

    @Output
    private CitizenEnrollmentInfo citizenEnrollmentInfo;

    @FXML
    private VBox paneImage;
    @FXML
    private ImageViewPane paneImageView;
    @FXML
    private ImageView ivPersonPhoto;
    @FXML
    private Label lblFirstName;
    @FXML
    private Label lblSecondName;
    @FXML
    private Label lblOtherName;
    @FXML
    private Label lblFamilyName;
    @FXML
    private Label lblNationality;
    @FXML
    private Label lblGender;
    @FXML
    private Label lblBirthPlace;
    @FXML
    private Label lblBirthDate;
    @FXML
    private ImageView ivRightThumb;
    @FXML
    private ImageView ivRightIndex;
    @FXML
    private ImageView ivRightMiddle;
    @FXML
    private ImageView ivRightRing;
    @FXML
    private ImageView ivRightLittle;
    @FXML
    private ImageView ivLeftLittle;
    @FXML
    private ImageView ivLeftRing;
    @FXML
    private ImageView ivLeftMiddle;
    @FXML
    private ImageView ivLeftIndex;
    @FXML
    private ImageView ivLeftThumb;

    @FXML
    private ImageView ivRightIris;
    @FXML
    private ImageView ivLeftIris;

    @FXML
    private ProgressIndicator piProgress;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnSubmit;


    @Override
    protected void onAttachedToScene() {
        paneImageView.maxWidthProperty().bind(paneImage.widthProperty());

        citizenEnrollmentInfo = new CitizenEnrollmentInfo(personInfo.getSamisId(),
                normalizedPersonInfo.getPersonType().getCode(),
                combinedFingerprints, missingFingerprints, facePhotoBase64,
                personInfo.getBirthDate(), personInfo.getGender(),
                capturedRightIrisBase64, capturedLeftIrisBase64);

        ivPersonPhoto.setImage(facePhoto);
        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
                resources.getString("label.personPhoto"),
                resources.getString("label.contextMenu.showImage"), false);

        lblFirstName.setText(normalizedPersonInfo.getFirstName());
        lblSecondName.setText(normalizedPersonInfo.getFatherName());
        lblOtherName.setText(normalizedPersonInfo.getGrandfatherName());
        lblFamilyName.setText(normalizedPersonInfo.getFamilyName());

        boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
        lblNationality.setText(arabic ? normalizedPersonInfo.getNationality().getDescriptionAR() :
                               normalizedPersonInfo.getNationality().getDescriptionEN());
        lblBirthPlace.setText(normalizedPersonInfo.getBirthPlace());

        lblGender.setText(normalizedPersonInfo.getGender() == Gender.FEMALE ? resources.getString("label.female") :
                          resources.getString("label.male"));

        lblBirthDate.setText(AppUtils.formatHijriGregorianDate(
                AppUtils.gregorianDateToSeconds(normalizedPersonInfo.getBirthDate())));


        GuiUtils.attachFingerprintImages(fingerprintBase64Images, null, ivRightThumb, ivRightIndex, ivRightMiddle,
                ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle, ivLeftRing,
                ivLeftLittle, null, null, null, null, null, null);


        Image capturedRightIrisImage = null;
        Image capturedLeftIrisImage = null;

        if (capturedRightIrisBase64 != null) {
            capturedRightIrisImage = new Image(
                    new ByteArrayInputStream(Base64.getDecoder().decode(capturedRightIrisBase64)));
        }
        if (capturedLeftIrisBase64 != null) {
            capturedLeftIrisImage = new Image(
                    new ByteArrayInputStream(Base64.getDecoder().decode(capturedLeftIrisBase64)));
        }

        if (capturedRightIrisImage != null) {
            ivRightIris.setImage(capturedRightIrisImage);

        }
        if (capturedLeftIrisImage != null) {
            ivLeftIris.setImage(capturedLeftIrisImage);
        }
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {

        if (ignorePossibilityAndCompleteWorkflow != null && !ignorePossibilityAndCompleteWorkflow) { return; }

        if (successfulResponse) {
            goNext();
        }
    }

    @Override
    public void onShowingProgress(boolean bShow) {
        GuiUtils.showNode(btnPrevious, !bShow);
        GuiUtils.showNode(btnSubmit, !bShow);
        GuiUtils.showNode(piProgress, bShow);
    }

    @FXML
    private void onSubmitButtonClicked(ActionEvent actionEvent) {
        String headerText = resources.getString("CitizenEnrollment.registering.confirmation.header");
        String contentText = resources.getString("CitizenEnrollment.registering.confirmation.message");
        boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);

        if (confirmed) {
            continueWorkflow();
        }
    }
}