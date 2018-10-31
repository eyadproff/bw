package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.features.commons.beans.Gender;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Country;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaTypeBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BuildForeignEnrollmentReceiptTask extends Task<JasperPrint>
{
	private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
	private static final String PARAMETER_MOFA_LOGO = "MOFA_LOGO";
	private static final String PARAMETER_REGISTRATION_NUMBER = "REGISTRATION_NUMBER";
	private static final String PARAMETER_RECEIPT_DATE = "RECEIPT_DATE";
	private static final String PARAMETER_FIRST_NAME = "FIRST_NAME";
	private static final String PARAMETER_SECOND_NAME = "SECOND_NAME";
	private static final String PARAMETER_OTHER_NAME = "OTHER_NAME";
	private static final String PARAMETER_FAMILY_NAME = "FAMILY_NAME";
	private static final String PARAMETER_NATIONALITY = "NATIONALITY";
	private static final String PARAMETER_GENDER = "GENDER";
	private static final String PARAMETER_BIRTH_PLACE = "BIRTH_PLACE";
	private static final String PARAMETER_BIRTH_DATE = "BIRTH_DATE";
	private static final String PARAMETER_VISA_TYPE = "VISA_TYPE";
	private static final String PARAMETER_PASSPORT_NUMBER = "PASSPORT_NUMBER";
	private static final String PARAMETER_ISSUE_DATE = "ISSUE_DATE";
	private static final String PARAMETER_EXPIRATION_DATE = "EXPIRATION_DATE";
	private static final String PARAMETER_ISSUANCE_COUNTRY = "ISSUANCE_COUNTRY";
	private static final String PARAMETER_PASSPORT_TYPE = "PASSPORT_TYPE";
	private static final String PARAMETER_MOBILE_NUMBER = "MOBILE_NUMBER";
	
	private static final String REPORT_EN_TEMPLATE_FILE = "sa/gov/nic/bio/bw/client/features/" +
										"visaapplicantsenrollment/reports/visa_applicants_enrollment_receipt_en.jrxml";
	private static final String REPORT_AR_TEMPLATE_FILE = "sa/gov/nic/bio/bw/client/features/" +
										"visaapplicantsenrollment/reports/visa_applicants_enrollment_receipt_ar.jrxml";
	private static final String LOGO_FILE = "sa/gov/nic/bio/bw/client/core/images/nic_logo.png";
	private static final String IMAGE_PLACEHOLDER_FILE = "sa/gov/nic/bio/bw/client/core/images/avatar_placeholder.jpg";
	
	private VisaApplicantInfo visaApplicantInfo;
	private Map<Integer, String> fingerprintImages;
	
	public BuildForeignEnrollmentReceiptTask(VisaApplicantInfo visaApplicantInfo,
	                                         Map<Integer, String> fingerprintImages)
	{
		this.visaApplicantInfo = visaApplicantInfo;
		this.fingerprintImages = fingerprintImages;
	}
	
	@Override
	protected JasperPrint call() throws Exception
	{
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		InputStream reportStream = Thread.currentThread().getContextClassLoader()
									.getResourceAsStream(arabic ? REPORT_AR_TEMPLATE_FILE : REPORT_EN_TEMPLATE_FILE);
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
		
		HashMap<String, Object> params = new HashMap<>();
		
		String faceImageBase64 = visaApplicantInfo.getFaceImage();
		
		if(faceImageBase64 != null)
		{
			byte[] bytes = Base64.getDecoder().decode(faceImageBase64);
			params.put(PARAMETER_FACE_IMAGE, new ByteArrayInputStream(bytes));
		}
		else
		{
			params.put(PARAMETER_FACE_IMAGE, Thread.currentThread().getContextClassLoader()
																   .getResourceAsStream(IMAGE_PLACEHOLDER_FILE));
		}
		
		fingerprintImages.forEach((position, fingerprintImage) ->
		{
			byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
			params.put("FINGER_" + position, new ByteArrayInputStream(bytes));
		});
		
		params.put(PARAMETER_MOFA_LOGO, Thread.currentThread().getContextClassLoader().getResourceAsStream(LOGO_FILE));
		params.put(PARAMETER_REGISTRATION_NUMBER, String.valueOf(visaApplicantInfo.getApplicantId()));
		params.put(PARAMETER_RECEIPT_DATE, AppUtils.formatHijriGregorianDateTime(
										AppUtils.gregorianDateToMilliSeconds(visaApplicantInfo.getEnrollmentDate())));
		params.put(PARAMETER_FIRST_NAME, visaApplicantInfo.getFirstName());
		params.put(PARAMETER_SECOND_NAME, visaApplicantInfo.getSecondName());
		params.put(PARAMETER_OTHER_NAME, visaApplicantInfo.getOtherName());
		params.put(PARAMETER_FAMILY_NAME, visaApplicantInfo.getFamilyName());
		
		Country country = visaApplicantInfo.getNationality();
		
		if(country != null) params.put(PARAMETER_NATIONALITY, arabic ? country.getDescriptionAR() :
																		   country.getDescriptionEN());
		
		
		params.put(PARAMETER_GENDER, visaApplicantInfo.getGender() == Gender.MALE ? (arabic ? "ذكر" : "Male") :
																		(arabic ? "أنثى" : "Female")); // TODO: TEMP
		
		country = visaApplicantInfo.getBirthPlace();
		
		if(country != null) params.put(PARAMETER_BIRTH_PLACE, arabic ? country.getDescriptionAR() :
																		   country.getDescriptionEN());
		
		params.put(PARAMETER_BIRTH_DATE, AppUtils.formatHijriGregorianDate(
											AppUtils.gregorianDateToMilliSeconds(visaApplicantInfo.getBirthDate())));
		
		VisaTypeBean visaTypeBean = visaApplicantInfo.getVisaType();
		
		if(visaTypeBean != null) params.put(PARAMETER_VISA_TYPE, arabic ? visaTypeBean.getDescriptionAR() :
																			visaTypeBean.getDescriptionEN());
		
		params.put(PARAMETER_PASSPORT_NUMBER, visaApplicantInfo.getPassportNumber());
		params.put(PARAMETER_ISSUE_DATE, AppUtils.formatHijriGregorianDate(
											AppUtils.gregorianDateToMilliSeconds(visaApplicantInfo.getIssueDate())));
		params.put(PARAMETER_EXPIRATION_DATE, AppUtils.formatHijriGregorianDate(
										AppUtils.gregorianDateToMilliSeconds(visaApplicantInfo.getExpirationDate())));
		
		country = visaApplicantInfo.getIssuanceCountry();
		
		if(country != null) params.put(PARAMETER_ISSUANCE_COUNTRY, arabic ? country.getDescriptionAR() :
																				country.getDescriptionEN());
		
		PassportTypeBean passportTypeBean = visaApplicantInfo.getPassportType();
		
		if(passportTypeBean != null) params.put(PARAMETER_PASSPORT_TYPE, arabic ? passportTypeBean.getDescriptionAR() :
																				  passportTypeBean.getDescriptionEN());
		
		params.put(PARAMETER_MOBILE_NUMBER, "+" + visaApplicantInfo.getMobileNumber());
		
		return JasperFillManager.fillReport(jasperReport, params);
	}
}