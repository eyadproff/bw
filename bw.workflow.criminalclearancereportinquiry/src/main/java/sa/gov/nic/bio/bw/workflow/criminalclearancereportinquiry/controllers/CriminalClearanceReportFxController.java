package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.ContentFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@FxmlFile("criminalClearanceReportPane.fxml")
public class CriminalClearanceReportFxController extends ContentFxControllerBase {
    @FXML private TitledPane tpEnrollmentDetails;
    @FXML private Pane paneImage;
    @FXML private Label lblReportNumber;
    @FXML private Label lblCreationDate;
    @FXML private Label lblExpireDate;
    @FXML private Label lblWhoRequestedTheReport;
    @FXML private Label lblPurposeOfTheReport;
    @FXML private Label lblFirstName;
    @FXML private Label lblFatherName;
    @FXML private Label lblGrandfatherName;
    @FXML private Label lblFamilyName;
    @FXML private Label lblEnglishFirstName;
    @FXML private Label lblEnglishFatherName;
    @FXML private Label lblEnglishGrandFatherName;
    @FXML private Label lblEnglishFamilyName;
    @FXML private Label lblNationality;
    @FXML private Label lblBirthDate;
    @FXML private Label lblPersonId;
    @FXML private Label lblPassportId;
    @FXML private Label lblNaturalizedSaudi;
    @FXML private ImageViewPane paneImageView;
    @FXML private ImageView ivPersonPhoto;
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

    @Override
    protected void onAttachedToScene() {
        paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
    }

    public void populateCriminalClearanceReportData(CriminalClearanceReport criminalClearanceReport,
            Map<Integer, String> fingerprintBase64Images) {
        String notAvailable = resources.getString("label.notAvailable");
        Consumer<Label> consumer = label ->
        {
            label.setText(notAvailable);
            label.setTextFill(Color.RED);
        };

        if (criminalClearanceReport == null) { return; }

        Long reportNumber = criminalClearanceReport.getReportNumber();
        if (reportNumber != null) { lblReportNumber.setText(AppUtils.localizeNumbers(String.valueOf(reportNumber))); }

        Long enrollmentTimeLong = criminalClearanceReport.getCreateDate();
        if (enrollmentTimeLong != null) {
            lblCreationDate.setText(
                    AppUtils.formatHijriGregorianDate(enrollmentTimeLong));
        }

        Long expireDateLong = criminalClearanceReport.getExpireDate();
        if (expireDateLong != null) {
            lblExpireDate.setText(
                    AppUtils.formatHijriGregorianDate(expireDateLong));
        }

        lblWhoRequestedTheReport.setText(criminalClearanceReport.getRequestedName());
        lblPurposeOfTheReport.setText(criminalClearanceReport.getReason());

        if (reportNumber != null || enrollmentTimeLong != null) {
            GuiUtils.showNode(tpEnrollmentDetails, true);
        }

        String facePhotoBase64 = criminalClearanceReport.getFace();

        GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64);

        Name subjtName = criminalClearanceReport.getFullName();
        if (subjtName != null) {
            GuiUtils.setLabelText(lblFirstName, subjtName.getFirstName()).orElse(consumer);
            GuiUtils.setLabelText(lblFatherName, subjtName.getFatherName()).orElse(consumer);
            GuiUtils.setLabelText(lblGrandfatherName, subjtName.getGrandfatherName()).orElse(consumer);
            GuiUtils.setLabelText(lblFamilyName, subjtName.getFamilyName()).orElse(consumer);

            GuiUtils.setLabelText(true, lblEnglishFirstName, subjtName.getTranslatedFirstName()).orElse(consumer);
            GuiUtils.setLabelText(true, lblEnglishFatherName, subjtName.getTranslatedFatherName()).orElse(consumer);
            GuiUtils.setLabelText(true, lblEnglishGrandFatherName, subjtName.getTranslatedGrandFatherName()).orElse(consumer);
            GuiUtils.setLabelText(true, lblEnglishFamilyName, subjtName.getTranslatedFamilyName()).orElse(consumer);
        }
        else {
            lblFirstName.setText("");
            lblFatherName.setText("");
            lblGrandfatherName.setText("");
            lblFamilyName.setText("");

            lblEnglishFirstName.setText("");
            lblEnglishFatherName.setText("");
            lblEnglishGrandFatherName.setText("");
            lblEnglishFamilyName.setText("");
        }

        Integer subjNationalityCode = criminalClearanceReport.getNationality();

        if (subjNationalityCode != null) {
            @SuppressWarnings("unchecked")
            List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);

            Country countryBean = null;

            for (Country country : countries) {
                if (country.getCode() == subjNationalityCode) {
                    countryBean = country;
                    break;
                }
            }

            if (countryBean != null) {
                boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
                lblNationality.setText(arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN());
                GuiUtils.showNode(lblNaturalizedSaudi,
                        !"SAU".equalsIgnoreCase(countryBean.getMofaNationalityCode()) &&
                        String.valueOf(criminalClearanceReport.getSamisId()).startsWith("1"));
            }
            else { lblNationality.setText(resources.getString("combobox.unknownNationality")); }
        }
        else { lblNationality.setText(""); }

        Long subjBirthDate = criminalClearanceReport.getDateOfBirth();
        String birthDate = null;
        if (subjBirthDate != null) { birthDate = AppUtils.formatHijriGregorianDate(subjBirthDate); }

        GuiUtils.setLabelText(lblBirthDate, birthDate).orElse(consumer);

        Long samisId = criminalClearanceReport.getSamisId();
        GuiUtils.setLabelText(lblPersonId, samisId).orElse(consumer);

        String passportId = criminalClearanceReport.getPassportNumber();

        GuiUtils.setLabelText(lblPassportId, passportId).orElse(consumer);

        if (fingerprintBase64Images != null) {
            GuiUtils.attachFingerprintImages(fingerprintBase64Images, null, ivRightThumb, ivRightIndex,
                    ivRightMiddle, ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex,
                    ivLeftMiddle, ivLeftRing, ivLeftLittle, null, null,
                    null, null, null, null);
        }
    }
}