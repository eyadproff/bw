package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.tasks;

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
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans.Candidate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;


public class BuildIdentifyByFaceReportTask extends Task<JasperPrint>
{
	private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
	private static final String CAPTURED_FACE_IMAGE = "FACE_CAPTURED_IMAGE";
	private static final String DATE = "DATE";
	private static final String LOCATION = "LOCATION";
	private static final String PARAMETER_NAME = "NAME";
	private static final String PARAMETER_SAMIS_ID = "SAMIS_ID";
	private static final String PARAMETER_SCORE_ID = "SCORE";
	private static final String PARAMETER_BIO_ID = "BIO_ID";
	private static final String OPERATOR_ID = "OPERATOR_ID";
	private static final String PARAMETER_LOGO = "LOGO";
	private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/workflow/searchbyfaceimage/reports/" +
													   "identificationByFace_report.jrxml";

	private Candidate candidate;
	private String faceCapturedImage;


	public BuildIdentifyByFaceReportTask(Candidate candidate,String faceCapturedImage)
	{
		this.candidate = candidate;
        this.faceCapturedImage=faceCapturedImage;

	}

	@Override
	protected JasperPrint call() throws Exception
	{
		InputStream reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

		HashMap<String, Object> params = new HashMap<>();

		String facePhotoBase64 = candidate.getImage();

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

		String firstName = candidate.getFirstName();
		String fatherName = candidate.getFatherName();
		String familyName = candidate.getFamilyName();

		if(firstName != null) sb.append(firstName).append(" ");
		if(fatherName != null) sb.append(fatherName).append(" ");
		if(familyName != null) sb.append(familyName);
		String fullName = sb.toString().stripTrailing();

		params.put(PARAMETER_NAME, fullName);


		Long subjSamisId = candidate.getSamisId();
		params.put(PARAMETER_SAMIS_ID, AppUtils.localizeNumbers(String.valueOf(subjSamisId),
		                                                                                Locales.SAUDI_AR_LOCALE,
		                                                                                true));

		Long subjBioId = candidate.getBioId();
		params.put(PARAMETER_BIO_ID, AppUtils.localizeNumbers(String.valueOf(subjBioId),
				Locales.SAUDI_AR_LOCALE,
				true));

		Integer subjScore = candidate.getScore();
		params.put(PARAMETER_SCORE_ID, "%"+AppUtils.localizeNumbers(String.valueOf(subjScore),
				Locales.SAUDI_AR_LOCALE,
				true));

        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");

        params.put(OPERATOR_ID, AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()),
                Locales.SAUDI_AR_LOCALE, true));

		params.put(LOCATION, AppUtils.localizeNumbers(String.valueOf(userInfo.getLocationName()),
				Locales.SAUDI_AR_LOCALE, true));

		params.put(PARAMETER_LOGO, CommonImages.LOGO_CIVIL_AFFAIRS.getAsInputStream());

		return JasperFillManager.fillReport(jasperReport, params);
	}
}