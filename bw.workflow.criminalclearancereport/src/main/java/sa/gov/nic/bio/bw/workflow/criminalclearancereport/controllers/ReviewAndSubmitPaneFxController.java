package sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase {

    @Input(alwaysRequired = true)
    private Map<Integer, String> fingerprintBase64Images;
    @Input(alwaysRequired = true)
    private String whoRequestedTheReport;
    @Input(alwaysRequired = true)
    private String purposeOfTheReport;

    @Input private NormalizedPersonInfo normalizedPersonInfo;
    @Input private String facePhotoBase64;
    @Input private String firstName;
    @Input private String fatherName;
    @Input private String grandfatherName;
    @Input private String familyName;
    @Input private Gender gender;
    @Input private Country nationality;
    @Input private String occupation;
    @Input private String birthPlace;
    @Input private LocalDate birthDate;
    @Input private Boolean birthDateUseHijri;
    @Input private Long personId;
    @Input private PersonType personType;
    @Input private String documentId;
    @Input private DocumentType documentType;
    @Input private LocalDate documentIssuanceDate;
    @Input private Boolean documentIssuanceDateUseHijri;
    @Input private LocalDate documentExpiryDate;
    @Input private Boolean documentExpiryDateUseHijri;

    @Output private CriminalClearanceReport criminalClearanceReport;

    //    @Output
    //    private CitizenEnrollmentInfo citizenEnrollmentInfo;

    @FXML private VBox paneImage;
    @FXML private ImageViewPane paneImageView;
    @FXML private ImageView ivPersonPhoto;
    @FXML private Label lblFirstName;
    @FXML private Label lblSecondName;
    @FXML private Label lblOtherName;
    @FXML private Label lblFamilyName;
    @FXML private Label lblNationality;
    @FXML private Label lblGender;
    @FXML private Label lblBirthPlace;
    @FXML private Label lblBirthDate;
    @FXML private Label lblPersonId;
    @FXML private Label lblPersonType;
    @FXML private Label lblDocumentId;
    @FXML private Label lblDocumentType;
    @FXML private Label lblDocumentIssuanceDate;
    @FXML private Label lblDocumentExpiryDate;
    @FXML private Label lblWhoRequestedTheReport;
    @FXML private Label lblPurposeOfTheReport;

    @FXML private ImageView ivRightThumb;
    @FXML private ImageView ivRightIndex;
    @FXML private ImageView ivRightMiddle;
    @FXML private ImageView ivRightRing;
    @FXML private ImageView ivRightLittle;
    @FXML private ImageView ivLeftLittle;
    @FXML private ImageView ivLeftRing;
    @FXML private ImageView ivLeftMiddle;
    @FXML private ImageView ivLeftIndex;
    @FXML private ImageView ivLeftThumb;

    @FXML private ProgressIndicator piProgress;
    @FXML private Button btnPrevious;
    @FXML private Button btnSubmit;


    @Override
    protected void onAttachedToScene() {

        criminalClearanceReport = new CriminalClearanceReport(normalizedPersonInfo.getPersonId(), whoRequestedTheReport, purposeOfTheReport);
        paneImageView.maxWidthProperty().bind(paneImage.widthProperty());

        //        citizenEnrollmentInfo = new CitizenEnrollmentInfo(personInfo.getSamisId(),
        //                normalizedPersonInfo.getPersonType().getCode(),
        //                combinedFingerprints, missingFingerprints, facePhotoBase64,
        //                personInfo.getBirthDate(), personInfo.getGender(),
        //                capturedRightIrisBase64, capturedLeftIrisBase64);

        String notAvailable = resources.getString("label.notAvailable");
        Consumer<Label> consumer = label ->
        {
            label.setText(notAvailable);
            label.setTextFill(Color.RED);
        };

        if (normalizedPersonInfo != null) {

            String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
            Gender gender = normalizedPersonInfo.getGender();
            GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);

            GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
                    resources.getString("label.personPhoto"),
                    resources.getString("label.contextMenu.showImage"), false);

            GuiUtils.setLabelText(lblFirstName, normalizedPersonInfo.getFirstNameLabel()).orElse(consumer);
            GuiUtils.setLabelText(lblSecondName, normalizedPersonInfo.getFatherNameLabel()).orElse(consumer);
            GuiUtils.setLabelText(lblOtherName, normalizedPersonInfo.getGrandfatherNameLabel()).orElse(consumer);
            GuiUtils.setLabelText(lblFamilyName, normalizedPersonInfo.getFamilyNameLabel()).orElse(consumer);
            GuiUtils.setLabelText(lblGender, normalizedPersonInfo.getGender()).orElse(consumer);
            GuiUtils.setLabelText(lblNationality, normalizedPersonInfo.getNationality()).orElse(consumer);
            //            GuiUtils.setLabelText(lblOccupation, normalizedPersonInfo.getOccupation()).orElse(consumer);
            GuiUtils.setLabelText(lblBirthPlace, normalizedPersonInfo.getBirthPlace()).orElse(consumer);
            GuiUtils.setLabelText(lblBirthDate, normalizedPersonInfo.getBirthDate()).orElse(consumer);
            GuiUtils.setLabelText(lblPersonId, normalizedPersonInfo.getPersonId()).orElse(consumer);
            GuiUtils.setLabelText(lblPersonType, normalizedPersonInfo.getPersonType()).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentId, normalizedPersonInfo.getDocumentId()).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentType, normalizedPersonInfo.getDocumentType()).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentIssuanceDate, normalizedPersonInfo.getDocumentIssuanceDate()).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentExpiryDate, normalizedPersonInfo.getDocumentExpiryDate()).orElse(consumer);

        }
        else {
            GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);

            GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
                    resources.getString("label.personPhoto"),
                    resources.getString("label.contextMenu.showImage"), false);

            GuiUtils.setLabelText(lblFirstName, firstName).orElse(consumer);
            GuiUtils.setLabelText(lblSecondName, fatherName).orElse(consumer);
            GuiUtils.setLabelText(lblOtherName, grandfatherName).orElse(consumer);
            GuiUtils.setLabelText(lblFamilyName, familyName).orElse(consumer);
            GuiUtils.setLabelText(lblGender, gender).orElse(consumer);
            GuiUtils.setLabelText(lblNationality, nationality).orElse(consumer);
            //            GuiUtils.setLabelText(lblOccupation, normalizedPersonInfo.getOccupation()).orElse(consumer);
            GuiUtils.setLabelText(lblBirthPlace, birthPlace).orElse(consumer);
            GuiUtils.setLabelText(lblBirthDate, birthDate).orElse(consumer);
            GuiUtils.setLabelText(lblPersonId, personId).orElse(consumer);
            GuiUtils.setLabelText(lblPersonType, personType).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentId, documentId).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentType, documentType).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentIssuanceDate, documentIssuanceDate).orElse(consumer);
            GuiUtils.setLabelText(lblDocumentExpiryDate, documentExpiryDate).orElse(consumer);
        }

        lblWhoRequestedTheReport.setText(whoRequestedTheReport);
        lblPurposeOfTheReport.setText(purposeOfTheReport);

        GuiUtils.attachFingerprintImages(fingerprintBase64Images, null, ivRightThumb, ivRightIndex, ivRightMiddle,
                ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle, ivLeftRing,
                ivLeftLittle, null, null, null, null, null, null);

    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) {goNext();}
    }

    @Override
    public void onShowingProgress(boolean bShow) {
        GuiUtils.showNode(btnPrevious, !bShow);
        GuiUtils.showNode(btnSubmit, !bShow);
        GuiUtils.showNode(piProgress, bShow);
    }

    @FXML
    private void onSubmitButtonClicked(ActionEvent actionEvent) {
        String headerText = resources.getString("CriminalClearanceReport.registering.confirmation.header");
        String contentText = resources.getString("CriminalClearanceReport.registering.confirmation.message");
        boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);

        if (confirmed) {goNext();}
    }
}
