package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.beans.FingerprintCardIdentificationRecordReport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BuildFingerprintCardIdentificationRecordReportTask extends Task<JasperPrint>
{
	private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
	private static final String PARAMETER_NAME = "NAME";
	private static final String PARAMETER_NATIONALITY = "NATIONALITY";
	private static final String PARAMETER_OCCUPATION = "OCCUPATION";
	private static final String PARAMETER_GENDER = "GENDER";
	private static final String PARAMETER_BIOMETRICS_ID = "BIOMETRICS_ID";
	private static final String PARAMETER_GENERAL_FILE_NUMBER = "GENERAL_FILE_NUMBER";
	private static final String PARAMETER_SAMIS_ID = "SAMIS_ID";
	private static final String PARAMETER_SAMIS_ID_TYPE = "SAMIS_ID_TYPE";
	private static final String PARAMETER_DOCUMENT_ID = "DOCUMENT_ID";
	private static final String PARAMETER_DOCUMENT_TYPE = "DOCUMENT_TYPE";
	private static final String PARAMETER_DOCUMENT_ISSUANCE_DATE = "DOCUMENT_ISSUANCE_DATE";
	private static final String PARAMETER_DOCUMENT_EXPIRY_DATE = "DOCUMENT_EXPIRY_DATE";
	private static final String PARAMETER_BIRTH_OF_DATE = "BIRTH_OF_DATE";
	private static final String PARAMETER_BIRTH_PLACE = "BIRTH_PLACE";
	private static final String PARAMETER_INQUIRER_ID = "INQUIRER_ID";
	private static final String PARAMETER_INQUIRY_TIME = "INQUIRY_TIME";
	private static final String PARAMETER_LOGO = "LOGO";
	
	private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/features" +
								"/fingerprintcardidentification/reports/fingerprint_card_identification_report.jrxml";
	
	private FingerprintCardIdentificationRecordReport fingerprintCardIdentificationRecordReport;
	
	public BuildFingerprintCardIdentificationRecordReportTask(
									FingerprintCardIdentificationRecordReport fingerprintCardIdentificationRecordReport)
	{
		this.fingerprintCardIdentificationRecordReport = fingerprintCardIdentificationRecordReport;
	}
	
	@Override
	protected JasperPrint call() throws Exception
	{
		InputStream reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
		
		HashMap<String, Object> params = new HashMap<>();
		
		String facePhotoBase64 = fingerprintCardIdentificationRecordReport.getFaceBase64();
		
		if(facePhotoBase64 != null)
		{
			byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
			params.put(PARAMETER_FACE_IMAGE, new ByteArrayInputStream(bytes));
		}
		else
		{
			params.put(PARAMETER_FACE_IMAGE, CommonImages.PLACEHOLDER_AVATAR.getAsInputStream());
		}
		
		Map<Integer, String> fingerprintImages = fingerprintCardIdentificationRecordReport.getFingerprintsImages();
		
		fingerprintImages.forEach((position, fingerprintImage) ->
		{
			byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
			params.put("FINGER_" + position, new ByteArrayInputStream(bytes));
		});
		
		String firstName = fingerprintCardIdentificationRecordReport.getFirstName();
		String fatherName = fingerprintCardIdentificationRecordReport.getFatherName();
		String grandfatherName = fingerprintCardIdentificationRecordReport.getGrandfatherName();
		String familyName = fingerprintCardIdentificationRecordReport.getFamilyName();
		String fullName = AppUtils.constructName(firstName, fatherName, grandfatherName, familyName);
		params.put(PARAMETER_NAME, fullName);
		
		params.put(PARAMETER_NATIONALITY, fingerprintCardIdentificationRecordReport.getNationality());
		params.put(PARAMETER_OCCUPATION, fingerprintCardIdentificationRecordReport.getOccupation());
		params.put(PARAMETER_GENDER, fingerprintCardIdentificationRecordReport.getGender());
		params.put(PARAMETER_BIOMETRICS_ID, fingerprintCardIdentificationRecordReport.getCivilBiometricsId());
		params.put(PARAMETER_GENERAL_FILE_NUMBER, fingerprintCardIdentificationRecordReport.getCriminalBiometricsId());
		params.put(PARAMETER_SAMIS_ID, fingerprintCardIdentificationRecordReport.getPersonId());
		params.put(PARAMETER_SAMIS_ID_TYPE, fingerprintCardIdentificationRecordReport.getPersonType());
		params.put(PARAMETER_DOCUMENT_ID, fingerprintCardIdentificationRecordReport.getDocumentId());
		params.put(PARAMETER_DOCUMENT_TYPE, fingerprintCardIdentificationRecordReport.getDocumentType());
		params.put(PARAMETER_DOCUMENT_ISSUANCE_DATE,
		           fingerprintCardIdentificationRecordReport.getDocumentIssuanceDate());
		params.put(PARAMETER_BIRTH_OF_DATE, fingerprintCardIdentificationRecordReport.getBirthDate());
		params.put(PARAMETER_BIRTH_PLACE, fingerprintCardIdentificationRecordReport.getBirthPlace());
		params.put(PARAMETER_DOCUMENT_EXPIRY_DATE, fingerprintCardIdentificationRecordReport.getDocumentExpiryDate());
		params.put(PARAMETER_LOGO, CommonImages.LOGO_SAUDI_SECURITY.getAsInputStream());
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		String inquirerId = AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()));
		params.put(PARAMETER_INQUIRER_ID, inquirerId);
		
		params.put(PARAMETER_INQUIRY_TIME, AppUtils.formatHijriGregorianDateTime(
							LocalDateTime.now().atZone(AppConstants.SAUDI_ZONE).toEpochSecond() * 1000));
		
		return JasperFillManager.fillReport(jasperReport, params);
	}
}