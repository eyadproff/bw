package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.beans.DeadPersonRecordReport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class BuildDeadPersonRecordReportTask extends Task<JasperPrint>
{
	private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
	private static final String PARAMETER_RECORD_ID = "RECORD_ID";
	private static final String PARAMETER_ENROLLER_ID = "ENROLLER_ID";
	private static final String PARAMETER_ENROLLMENT_TIME = "ENROLLMENT_TIME";
	private static final String PARAMETER_NAME = "NAME";
	private static final String PARAMETER_NATIONALITY = "NATIONALITY";
	private static final String PARAMETER_JOB = "JOB";
	private static final String PARAMETER_SEX = "SEX";
	private static final String PARAMETER_ID = "ID";
	private static final String PARAMETER_ID_TYPE = "ID_TYPE";
	private static final String PARAMETER_ID_ISSUANCE = "ID_ISSUANCE";
	private static final String PARAMETER_BIRTH_OF_DATE = "BIRTH_OF_DATE";
	private static final String PARAMETER_BIRTH_PLACE = "BIRTH_PLACE";
	private static final String PARAMETER_ID_EXPIRY = "ID_EXPIRY";
	private static final String PARAMETER_INQUIRER_ID = "INQUIRER_ID";
	private static final String PARAMETER_LOGO = "LOGO";
	
	private static final String REPORT_TEMPLATE_FILE = "sa/gov/nic/bio/bw/client/features/printdeadpersonrecord" +
													   "/reports/dead_person_record.jrxml";
	private static final String LOGO_FILE = "sa/gov/nic/bio/bw/client/features/commons/images/saudi_security_logo.jpg";
	private static final String IMAGE_PLACEHOLDER_FILE = "sa/gov/nic/bio/bw/client/core/images/avatar_placeholder.jpg";
	
	private DeadPersonRecordReport deadPersonRecordReport;
	
	public BuildDeadPersonRecordReportTask(DeadPersonRecordReport deadPersonRecordReport)
	{
		this.deadPersonRecordReport = deadPersonRecordReport;
	}
	
	@Override
	protected JasperPrint call() throws Exception
	{
		InputStream reportStream = Thread.currentThread().getContextClassLoader()
														 .getResourceAsStream(REPORT_TEMPLATE_FILE);
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
		
		HashMap<String, Object> params = new HashMap<>();
		
		String faceImageBase64 = deadPersonRecordReport.getFaceBase64();
		
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
		
		Map<Integer, String> fingerprintImages = deadPersonRecordReport.getFingerprintsImages();
		
		fingerprintImages.forEach((position, fingerprintImage) ->
		{
			byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
			params.put("FINGER_" + position, new ByteArrayInputStream(bytes));
		});
		
		params.put(PARAMETER_RECORD_ID, deadPersonRecordReport.getRecordId());
		params.put(PARAMETER_ENROLLER_ID, deadPersonRecordReport.getEnrollerId());
		params.put(PARAMETER_ENROLLMENT_TIME, deadPersonRecordReport.getEnrollmentTime());
		
		String firstName = deadPersonRecordReport.getFirstName();
		String fatherName = deadPersonRecordReport.getFatherName();
		String grandfatherName = deadPersonRecordReport.getGrandfatherName();
		String familyName = deadPersonRecordReport.getFamilyName();
		
		String fullName = AppUtils.constructName(firstName, fatherName, grandfatherName, familyName);
		params.put(PARAMETER_NAME, fullName);
		
		params.put(PARAMETER_NATIONALITY, deadPersonRecordReport.getNationality());
		params.put(PARAMETER_JOB, deadPersonRecordReport.getOccupation());
		params.put(PARAMETER_SEX, deadPersonRecordReport.getGender());
		params.put(PARAMETER_ID, deadPersonRecordReport.getIdNumber());
		params.put(PARAMETER_ID_TYPE, deadPersonRecordReport.getIdType());
		params.put(PARAMETER_ID_ISSUANCE, deadPersonRecordReport.getIdIssuanceDate());
		params.put(PARAMETER_BIRTH_OF_DATE, deadPersonRecordReport.getBirthDate());
		params.put(PARAMETER_BIRTH_PLACE, deadPersonRecordReport.getBirthPlace());
		params.put(PARAMETER_ID_EXPIRY, deadPersonRecordReport.getIdExpirationDate());
		params.put(PARAMETER_INQUIRER_ID, deadPersonRecordReport.getInquirerId());
		params.put(PARAMETER_LOGO, Thread.currentThread().getContextClassLoader().getResourceAsStream(LOGO_FILE));
		
		return JasperFillManager.fillReport(jasperReport, params);
	}
}