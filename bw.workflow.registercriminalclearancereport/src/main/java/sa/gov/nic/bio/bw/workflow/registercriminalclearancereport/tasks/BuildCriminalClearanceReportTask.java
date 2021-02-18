package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppConstants.Locales;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.beans.CriminalClearanceReport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class BuildCriminalClearanceReportTask extends Task<JasperPrint> {
    private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
    private static final String PARAMETER_REPORT_NUMBER = "REPORT_NUMBER";
    private static final String PARAMETER_ISSUE_DATE_H = "ISSUE_DATE_H";
    private static final String PARAMETER_ISSUE_DATE_G = "ISSUE_DATE_G";
    private static final String PARAMETER_BIRTH_DATE_H = "BIRTH_DATE_H";
    private static final String PARAMETER_BIRTH_DATE_G = "BIRTH_DATE_G";
    private static final String PARAMETER_EXPIRY_DATE_H = "EXPIRY_DATE_H";
    private static final String PARAMETER_EXPIRY_DATE_G = "EXPIRY_DATE_G";
    private static final String PARAMETER_ENGLISH_NAME = "ENGLISH_NAME";
    private static final String PARAMETER_ARABIC_NAME = "ARABIC_NAME";
    private static final String PARAMETER_ARABIC_NATIONALITY = "ARABIC_NATIONALITY";
    private static final String PARAMETER_ENGLISH_NATIONALITY = "ENGLISH_NATIONALITY";
    private static final String PARAMETER_SAMIS_ID = "SAMIS_ID";
    private static final String PARAMETER_PASSPORT_ID = "PASSPORT_ID";
    private static final String PARAMETER_OPERATOR_ID = "OPERATOR_ID";
    private static final String PARAMETER_LOCATION_ID = "LOCATION_ID";
    private static final String PARAMETER_REQUESTED_BY = "REQUESTED_BY";
    private static final String PARAMETER_PURPOSE_OF_CERTIFICATE = "PURPOSE_OF_CERTIFICATE";
    private static final String PARAMETER_LOGO = "LOGO";

    private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/workflow/registercriminalclearancereport/reports/" +
                                                       "criminal_clearance_certificate.jrxml";
    private CriminalClearanceReport criminalClearanceReport;
    private Map<Integer, String> fingerprintBase64Images;

    public BuildCriminalClearanceReportTask(CriminalClearanceReport criminalClearanceReport, Map<Integer, String> fingerprintBase64Images) {
        this.criminalClearanceReport = criminalClearanceReport;
        this.fingerprintBase64Images = fingerprintBase64Images;
    }

    @Override
    protected JasperPrint call() throws Exception {
        InputStream reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        HashMap<String, Object> params = new HashMap<>();

        String facePhotoBase64 = criminalClearanceReport.getFace();

        if (facePhotoBase64 != null) {
            byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
            params.put(PARAMETER_FACE_IMAGE, new ByteArrayInputStream(bytes));
        }
        else {
            params.put(PARAMETER_FACE_IMAGE, CommonImages.PLACEHOLDER_AVATAR.getAsInputStream());
        }

        fingerprintBase64Images.forEach((position, fingerprintImage) ->
        {
            byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
            params.put("FINGER_" + position, new ByteArrayInputStream(bytes));
        });

        params.put(PARAMETER_REPORT_NUMBER,
                AppUtils.localizeNumbers(String.valueOf(criminalClearanceReport.getReportNumber()), Locale.getDefault(),
                        true));
        params.put(PARAMETER_REQUESTED_BY, criminalClearanceReport.getRequestedName());
        params.put(PARAMETER_PURPOSE_OF_CERTIFICATE, criminalClearanceReport.getReason());

        Name Name = criminalClearanceReport.getFullName();
        StringBuilder sb = new StringBuilder();

        String firstArabicName = Name.getFirstName();
        String fatherArabicName = Name.getFatherName();
        String grandfatherArabicName = Name.getGrandfatherName();
        String familyArabicName = Name.getFamilyName();

        if (firstArabicName != null) { sb.append(firstArabicName).append(" "); }
        if (fatherArabicName != null) { sb.append(fatherArabicName).append(" "); }
        if (grandfatherArabicName != null) { sb.append(grandfatherArabicName).append(" "); }
        if (familyArabicName != null) { sb.append(familyArabicName); }
        String fullArabicName = sb.toString().stripTrailing();

        params.put(PARAMETER_ARABIC_NAME, fullArabicName);

        StringBuilder sb2 = new StringBuilder();

        String firstEnglishName = Name.getTranslatedFirstName();
        String fatherEnglishName = Name.getTranslatedFatherName();
        String grandfatherEnglishName = Name.getTranslatedGrandFatherName();
        String familyEnglishName = Name.getTranslatedFamilyName();

        if (firstEnglishName != null) { sb2.append(firstEnglishName).append(" "); }
        if (fatherEnglishName != null) { sb2.append(fatherEnglishName).append(" "); }
        if (grandfatherEnglishName != null) { sb2.append(grandfatherEnglishName).append(" "); }
        if (familyEnglishName != null) { sb2.append(familyEnglishName); }
        String fullEnglishName = sb2.toString().stripTrailing();

        params.put(PARAMETER_ENGLISH_NAME, fullEnglishName);

        @SuppressWarnings("unchecked")
        List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);

        Country countryBean = null;

        for (Country country : countries) {
            if (country.getCode() == criminalClearanceReport.getNationality()) {
                countryBean = country;
                break;
            }
        }

        if (countryBean != null) {
            String arabicNationalityText = countryBean.getArabicText();
            String englishNationalityText = countryBean.getEnglishText();

            if (countryBean.getCode() > 0 && !"SAU".equalsIgnoreCase(countryBean.getMofaNationalityCode()) &&
                String.valueOf(criminalClearanceReport.getSamisId()).startsWith("1")) {
                // \u202B is used to render the brackets correctly
                arabicNationalityText += " \u202B" + "(سعودي مجنس)";
                englishNationalityText += " \u202B" + "(Naturalized Saudi)";
            }

            params.put(PARAMETER_ARABIC_NATIONALITY, arabicNationalityText);
            params.put(PARAMETER_ENGLISH_NATIONALITY, englishNationalityText);
        }
        else {
            params.put(PARAMETER_ARABIC_NATIONALITY, "جنسية مجهولة");
            params.put(PARAMETER_ENGLISH_NATIONALITY, "Unknown Nationality");
        }

        Long subjSamisId = criminalClearanceReport.getSamisId();
        if (subjSamisId != null && subjSamisId != 0) {
            params.put(PARAMETER_SAMIS_ID, AppUtils.localizeNumbers(String.valueOf(subjSamisId),
                    Locale.getDefault(),
                    true));
        }

        String subjPassportId = criminalClearanceReport.getPassportNumber();
        if (subjPassportId != null) {
            params.put(PARAMETER_PASSPORT_ID, subjPassportId);
        }


        Long subjIssueDate = criminalClearanceReport.getCreateDate();
        if (subjIssueDate != null) {
            params.put(PARAMETER_ISSUE_DATE_H,
                    AppUtils.formatDate(AppUtils.secondsToHijriDate(subjIssueDate), Locales.SAUDI_AR_LOCALE));
            params.put(PARAMETER_ISSUE_DATE_G,
                    AppUtils.formatDate(AppUtils.secondsToGregorianDate(subjIssueDate), Locales.SAUDI_EN_LOCALE));
        }


        Long subjExpireDate = criminalClearanceReport.getExpireDate();
        if (subjExpireDate != null) {
            params.put(PARAMETER_EXPIRY_DATE_H,
                    AppUtils.formatDate(AppUtils.secondsToHijriDate(subjExpireDate), Locales.SAUDI_AR_LOCALE));
            params.put(PARAMETER_EXPIRY_DATE_G,
                    AppUtils.formatDate(AppUtils.secondsToGregorianDate(subjExpireDate), Locales.SAUDI_EN_LOCALE));
        }


        Long subjBirthDate= criminalClearanceReport.getDateOfBirth();
        if (subjBirthDate != null) {
            LocalDate birthDate = AppUtils.secondsToGregorianDate(subjBirthDate);
            if (birthDate != null && birthDate.atStartOfDay(AppConstants.SAUDI_ZONE).toInstant().toEpochMilli() > AppConstants.SAMIS_DB_DATE_EPOCH_MS_NOT_SET_VALUE) {
                if (subjBirthDate > AppConstants.SAMIS_DB_DATE_EPOCH_MS_NOT_SET_VALUE) {
                    params.put(PARAMETER_BIRTH_DATE_H, AppUtils.formatDate(AppUtils.secondsToHijriDate(subjBirthDate), Locales.SAUDI_AR_LOCALE));
                    params.put(PARAMETER_BIRTH_DATE_G,
                            AppUtils.formatDate(birthDate, Locales.SAUDI_EN_LOCALE));
                }
            }
        }

        params.put(PARAMETER_LOGO, CommonImages.LOGO_SAUDI_SECURITY.getAsInputStream());


        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
        long subjOperatorId = userInfo.getOperatorId();
        params.put(PARAMETER_OPERATOR_ID, AppUtils.localizeNumbers(String.valueOf(subjOperatorId),
                Locale.getDefault(),
                true));

        String subjLocationId = userInfo.getLocationId();
        if (subjLocationId != null) {
            params.put(PARAMETER_LOCATION_ID, AppUtils.localizeNumbers(subjLocationId));
        }

        return JasperFillManager.fillReport(jasperReport, params);
    }
}