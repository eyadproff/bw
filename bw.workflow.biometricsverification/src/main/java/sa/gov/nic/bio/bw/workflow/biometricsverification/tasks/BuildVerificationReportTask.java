package sa.gov.nic.bio.bw.workflow.biometricsverification.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.utils.AppConstants.Locales;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.*;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;



public class BuildVerificationReportTask extends Task<JasperPrint>
{
	private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
	private static final String CAPTURED_FACE_IMAGE = "FACE_CAPTURED_IMAGE";
	private static final String DATE = "DATE";
	private static final String LOCATION = "LOCATION";
	private static final String PARAMETER_NAME = "NAME";
	private static final String PARAMETER_NATIONALITY = "NATIONALITY";
	private static final String PARAMETER_OCCUPATION = "OCCUPATION";
	private static final String PARAMETER_GENDER = "GENDER";
	private static final String PARAMETER_SAMIS_ID = "SAMIS_ID";
	private static final String PARAMETER_DOCUMENT_ISSUANCE_DATE = "DOCUMENT_ISSUANCE_DATE";
	private static final String PARAMETER_DOCUMENT_EXPIRY_DATE = "DOCUMENT_EXPIRY_DATE";
	private static final String PARAMETER_BIRTH_OF_DATE = "BIRTH_OF_DATE";
	private static final String PARAMETER_BIRTH_PLACE = "BIRTH_PLACE";
	private static final String OPERATOR_ID = "OPERATOR_ID";
	private static final String PARAMETER_LOGO = "LOGO";
    private static final String VERIFICATION_METHOD = "VERIFICATION_METHOD";

	private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/workflow/biometricsverification/reports/" +
													   "verification_report.jrxml";

	private NormalizedPersonInfo normalizedPersonInfo;
	private String verificationMethodToString;
	private String faceCapturedImage;


	public BuildVerificationReportTask(NormalizedPersonInfo normalizedPersonInfo,String verificationMethodToString,String faceCapturedImage)
	{
		this.normalizedPersonInfo = normalizedPersonInfo;
        this.verificationMethodToString=verificationMethodToString;
        this.faceCapturedImage=faceCapturedImage;

	}

	@Override
	protected JasperPrint call() throws Exception
	{
		InputStream reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

		HashMap<String, Object> params = new HashMap<>();

		String facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();

		if(facePhotoBase64 != null)
		{
			byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
			params.put(PARAMETER_FACE_IMAGE, new ByteArrayInputStream(bytes));
		}
		else
		{
			params.put(PARAMETER_FACE_IMAGE, CommonImages.PLACEHOLDER_AVATAR.getAsInputStream());
		}

		if(faceCapturedImage != null)
		{
			byte[] bytes = Base64.getDecoder().decode(faceCapturedImage);
			params.put(CAPTURED_FACE_IMAGE, new ByteArrayInputStream(bytes));
		}

		params.put(DATE, AppUtils.formatHijriDateSimple(Instant.now().getEpochSecond(), false));

		StringBuilder sb = new StringBuilder();

		String firstName = normalizedPersonInfo.getFirstName();
		String fatherName = normalizedPersonInfo.getFatherName();
		String grandfatherName = normalizedPersonInfo.getGrandfatherName();
		String familyName = normalizedPersonInfo.getFamilyName();

		if(firstName != null) sb.append(firstName).append(" ");
		if(fatherName != null) sb.append(fatherName).append(" ");
		if(grandfatherName != null) sb.append(grandfatherName).append(" ");
		if(familyName != null) sb.append(familyName);
		String fullName = sb.toString().stripTrailing();

		params.put(PARAMETER_NAME, fullName);


		Country countryBean = normalizedPersonInfo.getNationality();



		if(countryBean != null)
		{
			String nationalityText = countryBean.getArabicText();

			if(countryBean.getCode() > 0 && !"SAU".equalsIgnoreCase(countryBean.getMofaNationalityCode()) &&
			   String.valueOf(normalizedPersonInfo.getPersonId()).startsWith("1"))
			{
				// \u202B is used to render the brackets correctly
				nationalityText += " \u202B" + AppUtils.getCoreStringsResourceBundle(Locales.SAUDI_AR_LOCALE)
												 .getString("label.naturalizedSaudi");
			}

			params.put(PARAMETER_NATIONALITY, nationalityText);
		}
		else params.put(PARAMETER_NATIONALITY, AppUtils.getCoreStringsResourceBundle(Locales.SAUDI_AR_LOCALE)
																		.getString("combobox.unknownNationality"));

		params.put(PARAMETER_OCCUPATION, AppUtils.localizeNumbers(normalizedPersonInfo.getOccupation(),
		                                                          Locales.SAUDI_AR_LOCALE,
		                                                          true));

		params.put(PARAMETER_GENDER, normalizedPersonInfo.getGender().toString(Locales.SAUDI_AR_LOCALE));


		Long subjSamisId = normalizedPersonInfo.getPersonId();
		if(subjSamisId != null) params.put(PARAMETER_SAMIS_ID, AppUtils.localizeNumbers(String.valueOf(subjSamisId),
		                                                                                Locales.SAUDI_AR_LOCALE,
		                                                                                true));

		LocalDate issDate =normalizedPersonInfo.getDocumentIssuanceDate();
		if(issDate!=null) {
			long subjDocIssDate = AppUtils.gregorianDateToSeconds(issDate);
			params.put(PARAMETER_DOCUMENT_ISSUANCE_DATE,
					AppUtils.formatHijriGregorianDate(subjDocIssDate));
		}

		LocalDate expDate=normalizedPersonInfo.getDocumentExpiryDate();
		if(expDate!=null) {
			long subjDocExpDate = AppUtils.gregorianDateToSeconds(expDate);
			params.put(PARAMETER_DOCUMENT_EXPIRY_DATE,
					AppUtils.formatHijriGregorianDate(subjDocExpDate));
		}

		long subjBirthDate = AppUtils.gregorianDateToSeconds(normalizedPersonInfo.getBirthDate());
		params.put(PARAMETER_BIRTH_OF_DATE, AppUtils.formatHijriGregorianDate(subjBirthDate));

		params.put(PARAMETER_BIRTH_PLACE, AppUtils.localizeNumbers(normalizedPersonInfo.getBirthPlace(),
	                                                           Locales.SAUDI_AR_LOCALE, true));

        params.put(VERIFICATION_METHOD,verificationMethodToString);

        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");

        params.put(OPERATOR_ID, AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()),
                Locales.SAUDI_AR_LOCALE, true));

		params.put(LOCATION, AppUtils.localizeNumbers(String.valueOf(userInfo.getLocationName()),
				Locales.SAUDI_AR_LOCALE, true));

		params.put(PARAMETER_LOGO, CommonImages.LOGO_CIVIL_AFFAIRS.getAsInputStream());

		return JasperFillManager.fillReport(jasperReport, params);
	}
}