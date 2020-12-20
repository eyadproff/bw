package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.List;
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
    @Input(alwaysRequired = true)
    private List<Finger> combinedFingerprints; // segmented + unsegmented
    @Input(alwaysRequired = true)
    private List<Integer> missingFingerprints;

    @Input private NormalizedPersonInfo normalizedPersonInfo;
    @Input private PersonInfo personInfo;

    @Input private String facePhotoBase64;
    @Input private String firstName;
    @Input private String fatherName;
    @Input private String grandfatherName;
    @Input private String familyName;
    @Input private String englishFirstName;
    @Input private String englishFatherName;
    @Input private String englishGrandfatherName;
    @Input private String englishFamilyName;
    @Input private Country nationality;
    @Input private LocalDate birthDate;
    @Input private Long personId;

    @Input private String passportId;


    @Output private CriminalClearanceReport criminalClearanceReport;

    @FXML private VBox paneImage;
    @FXML private ImageViewPane paneImageView;
    @FXML private ImageView ivPersonPhoto;
    @FXML private Label lblFirstName;
    @FXML private Label lblSecondName;
    @FXML private Label lblOtherName;
    @FXML private Label lblFamilyName;
    @FXML private Label lblEnglishFirstName;
    @FXML private Label lblEnglishSecondName;
    @FXML private Label lblEnglishOtherName;
    @FXML private Label lblEnglishFamilyName;
    @FXML private Label lblNationality;
    @FXML private Label lblBirthDate;
    @FXML private Label lblPersonId;
    @FXML private Label lblPassportId;
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

        paneImageView.maxWidthProperty().bind(paneImage.widthProperty());

        String notAvailable = resources.getString("label.notAvailable");
        Consumer<Label> consumer = label ->
        {
            label.setText(notAvailable);
            label.setTextFill(Color.RED);
        };

        if (normalizedPersonInfo != null) {

            facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();

            Name name = personInfo.getName();
            firstName = name.getFirstName();
            fatherName = name.getFatherName();
            grandfatherName = name.getGrandfatherName();
            familyName = name.getFamilyName();
            englishFirstName = name.getTranslatedFirstName();
            englishFatherName = name.getTranslatedFatherName();
            englishGrandfatherName = name.getTranslatedGrandFatherName();
            englishFamilyName = name.getTranslatedFamilyName();

            nationality = normalizedPersonInfo.getNationality();
            birthDate = normalizedPersonInfo.getBirthDate();
            personId = normalizedPersonInfo.getPersonId();

        }

         GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64);

        GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
                resources.getString("label.personPhoto"),
                resources.getString("label.contextMenu.showImage"), false);

        GuiUtils.setLabelText(lblFirstName, firstName).orElse(consumer);
        GuiUtils.setLabelText(lblSecondName, fatherName).orElse(consumer);
        GuiUtils.setLabelText(lblOtherName, grandfatherName).orElse(consumer);
        GuiUtils.setLabelText(lblFamilyName, familyName).orElse(consumer);

        GuiUtils.setLabelText(true, lblEnglishFirstName, englishFirstName).orElse(consumer);
        GuiUtils.setLabelText(true, lblEnglishSecondName, englishFatherName).orElse(consumer);
        GuiUtils.setLabelText(true, lblEnglishOtherName, englishGrandfatherName).orElse(consumer);
        GuiUtils.setLabelText(true, lblEnglishFamilyName, englishFamilyName).orElse(consumer);
        GuiUtils.setLabelText(lblNationality, nationality).orElse(consumer);
        GuiUtils.setLabelText(lblBirthDate, true, birthDate).orElse(consumer);
        GuiUtils.setLabelText(lblPersonId, personId).orElse(consumer);
        GuiUtils.setLabelText(lblPassportId, passportId).orElse(consumer);

        GuiUtils.setLabelText(lblWhoRequestedTheReport, whoRequestedTheReport).orElse(consumer);
        GuiUtils.setLabelText(lblPurposeOfTheReport, purposeOfTheReport).orElse(consumer);


        GuiUtils.attachFingerprintImages(fingerprintBase64Images, null, ivRightThumb, ivRightIndex, ivRightMiddle,
                ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle, ivLeftRing,
                ivLeftLittle, null, null, null, null, null, null);


        Name name = new Name(firstName, fatherName, grandfatherName, familyName,
                englishFirstName, englishFatherName, englishGrandfatherName, englishFamilyName);

        //backend need Hijri date as String!!
        Long dateOfBirth = null;
        HijrahDate dateOfBirthHijri;
        String dateOfBirthHijriString = null;
        if (birthDate != null) {
            dateOfBirth = AppUtils.gregorianDateToSeconds(birthDate);
            dateOfBirthHijri = AppUtils.secondsToHijriDate(dateOfBirth);
            dateOfBirthHijriString = dateOfBirthHijri.getLong(ChronoField.YEAR) + "-"
                                     + dateOfBirthHijri.getLong(ChronoField.MONTH_OF_YEAR) + "-" + dateOfBirthHijri.getLong(ChronoField.DAY_OF_MONTH);
        }

        Integer nationalityInteger = null;
        if (nationality != null) {
            nationalityInteger = nationality.getCode();
        }

        criminalClearanceReport = new CriminalClearanceReport(personId, name, nationalityInteger, facePhotoBase64, combinedFingerprints, missingFingerprints,
                dateOfBirth, dateOfBirthHijriString, whoRequestedTheReport, purposeOfTheReport);

        if (passportId != null) { criminalClearanceReport.setPassportNumber(passportId); }

        btnSubmit.requestFocus();
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
