package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants.Locales;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.IdType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.JudgementInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildConvictedReportTask extends Task<JasperPrint>
{
	private static final String PARAMETER_FACE_IMAGE = "FACE_IMAGE";
	private static final String PARAMETER_REPORT_NUMBER = "REPORT_NUMBER";
	private static final String PARAMETER_REF_NUMBER = "REF_NUMBER";
	private static final String PARAMETER_REPORT_DATE = "REPORT_DATE";
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
	private static final String PARAMETER_POLICE_FILE_NUMBER = "POLICE_FILE_NUMBER";
	private static final String PARAMETER_ARREST_DATE = "ARREST_DATE";
	private static final String PARAMETER_CRIMINAL_CLASS1 = "CRIMINAL_CLASS1";
	private static final String PARAMETER_CRIMINAL_CLASS2 = "CRIMINAL_CLASS2";
	private static final String PARAMETER_CRIMINAL_CLASS3 = "CRIMINAL_CLASS3";
	private static final String PARAMETER_JUDGMENT_ISSUER = "JUDGMENT_ISSUER";
	private static final String PARAMETER_JUDGMENT_DATE = "JUDGMENT_DATE";
	private static final String PARAMETER_JUDGMENT_NUMBER = "JUDGMENT_NUMBER";
	private static final String PARAMETER_OPERATOR_ID = "OPERATOR_ID";
	private static final String PARAMETER_JAIL_YEARS = "JAIL_YEARS";
	private static final String PARAMETER_JAIL_MONTHS = "JAIL_MONTHS";
	private static final String PARAMETER_JAIL_DAYS = "JAIL_DAYS";
	private static final String PARAMETER_TAZEER_LASHES_COUNT = "TAZEER_LASHES_COUNT";
	private static final String PARAMETER_HAD_LASHES_COUNT = "HAD_LASHES_COUNT";
	private static final String PARAMETER_FINE = "FINE";
	private static final String PARAMETER_TRAVEL_BAN_YEARS = "TRAVEL_BAN_YEARS";
	private static final String PARAMETER_TRAVEL_BAN_MONTHS = "TRAVEL_BAN_MONTHS";
	private static final String PARAMETER_TRAVEL_BAN_DAYS = "TRAVEL_BAN_DAYS";
	private static final String PARAMETER_DEPORTATION_YEARS = "DEPORTATION_YEARS";
	private static final String PARAMETER_DEPORTATION_MONTHS = "DEPORTATION_MONTHS";
	private static final String PARAMETER_DEPORTATION_DAYS = "DEPORTATION_DAYS";
	private static final String PARAMETER_EXILING_YEARS = "EXILING_YEARS";
	private static final String PARAMETER_EXILING_MONTHS = "EXILING_MONTHS";
	private static final String PARAMETER_EXILING_DAYS = "EXILING_DAYS";
	private static final String PARAMETER_OTHER = "OTHER";
	private static final String PARAMETER_IS_CRIMINAL_LIBEL = "IS_CRIMINAL_LIBEL";
	private static final String PARAMETER_IS_COVENANT = "IS_COVENANT";
	private static final String PARAMETER_IS_DEPORTATION_FINAL = "IS_DEPORTATION_FINAL";
	private static final String PARAMETER_LOGO = "LOGO";
	
	private static final String REPORT_TEMPLATE_FILE = "sa/gov/nic/bio/bw/client/features/registerconvictedpresent" +
													   "/reports/convicted_record.jrxml";
	private static final String LOGO_FILE = "sa/gov/nic/bio/bw/client/features/registerconvictedpresent" +
											"/images/saudi_security_logo.jpg";
	private static final String IMAGE_PLACEHOLDER_FILE = "sa/gov/nic/bio/bw/client/core/images/avatar_placeholder.jpg";
	
	private ConvictedReport convictedReport;
	private Map<Integer, String> fingerprintImages;
	private Map<Integer, String> crimeEventTitles = new HashMap<>();
	private Map<Integer, String> crimeClassTitles = new HashMap<>();
	
	public BuildConvictedReportTask(ConvictedReport convictedReport, Map<Integer, String> fingerprintImages)
	{
		this.convictedReport = convictedReport;
		this.fingerprintImages = fingerprintImages;
	}
	
	@Override
	protected JasperPrint call() throws Exception
	{
		InputStream reportStream = Thread.currentThread().getContextClassLoader()
														 .getResourceAsStream(REPORT_TEMPLATE_FILE);
		JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
		
		HashMap<String, Object> params = new HashMap<>();
		
		String faceImageBase64 = convictedReport.getSubjFace();
		
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
		
		params.put(PARAMETER_REPORT_NUMBER,
	           AppUtils.replaceNumbersOnly(String.valueOf(convictedReport.getReportNumber()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_REF_NUMBER,
           AppUtils.replaceNumbersOnly(String.valueOf(convictedReport.getGeneralFileNum()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_REPORT_DATE,
		           AppUtils.formatHijriGregorianDateTime(convictedReport.getReportDate() * 1000));
		
		Name name = convictedReport.getSubjtName();
		String fullName = name.getFirstName() + " " + name.getFatherName() + " " + name.getGrandfatherName() +
						  " " + name.getFamilyName();
		fullName = fullName.trim().replaceAll("\\s+", " "); // remove extra spaces
		
		params.put(PARAMETER_NAME, fullName);
		
		@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
													Context.getUserSession().getAttribute("lookups.countries");
		
		CountryBean countryBean = null;
		
		for(CountryBean country : countries)
		{
			if(country.getCode() == convictedReport.getSubjNationalityCode())
			{
				countryBean = country;
				break;
			}
		}
		
		if(countryBean != null) params.put(PARAMETER_NATIONALITY, countryBean.getDescriptionAR());
		
		params.put(PARAMETER_JOB, convictedReport.getSubjOccupation());
		params.put(PARAMETER_SEX, "F".equals(convictedReport.getSubjGender()) ? "أنثى" : "ذكر");
		
		String subjDocId = convictedReport.getSubjDocId();
		
		if(subjDocId != null) params.put(PARAMETER_ID, AppUtils.replaceNumbersOnly(subjDocId, Locales.SAUDI_AR_LOCALE));
		
		@SuppressWarnings("unchecked") List<IdType> idTypes = (List<IdType>)
														Context.getUserSession().getAttribute("lookups.idTypes");
		
		Integer subjDocType = convictedReport.getSubjDocType();
		if(subjDocType != null)
		{
			IdType it = null;
			for(IdType type : idTypes)
			{
				if(type.getCode() == subjDocType)
				{
					it = type;
					break;
				}
			}
			
			if(it != null) params.put(PARAMETER_ID_TYPE, it.getDesc());
		}
		
		Long subjDocIssDate = convictedReport.getSubjDocIssDate();
		if(subjDocIssDate != null) params.put(PARAMETER_ID_ISSUANCE,
		                                      AppUtils.formatHijriGregorianDate(subjDocIssDate * 1000));
		
		Long subjBirthDate = convictedReport.getSubjBirthDate();
		if(subjBirthDate != null) params.put(PARAMETER_BIRTH_OF_DATE,
		                                     AppUtils.formatHijriGregorianDate(subjBirthDate * 1000));
		params.put(PARAMETER_BIRTH_PLACE, convictedReport.getSubjBirthPlace());
		
		Long subjDocExpDate = convictedReport.getSubjDocExpDate();
		if(subjDocExpDate != null) params.put(PARAMETER_ID_EXPIRY,
		                                      AppUtils.formatHijriGregorianDate(subjDocExpDate * 1000));
		
		JudgementInfo judgementInfo = convictedReport.getSubjJudgementInfo();
		
		String policeFileNum = judgementInfo.getPoliceFileNum();
		if(policeFileNum != null) params.put(PARAMETER_POLICE_FILE_NUMBER,
	                                         AppUtils.replaceNumbersOnly(policeFileNum, Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_ARREST_DATE,
		           AppUtils.formatHijriGregorianDate(judgementInfo.getArrestDate() * 1000));
		
		@SuppressWarnings("unchecked") List<CrimeType> crimeTypes = (List<CrimeType>)
													Context.getUserSession().getAttribute("lookups.crimeTypes");
		
		crimeTypes.forEach(crimeType ->
		{
		    int eventCode = crimeType.getEventCode();
		    int classCode = crimeType.getClassCode();
		    String eventTitle = crimeType.getEventDesc();
		    String classTitle = crimeType.getClassDesc();
		
		    crimeEventTitles.putIfAbsent(eventCode, eventTitle);
		    crimeClassTitles.putIfAbsent(classCode, classTitle);
		});
		
		int counter = 0;
		List<CrimeCode> crimeCodes = judgementInfo.getCrimeCodes();
		for(CrimeCode crimeCode : crimeCodes)
		{
			String criminalClass = crimeEventTitles.get(crimeCode.getCrimeEvent()) +
								   ": " + crimeClassTitles.get(crimeCode.getCrimeClass());
			
			switch(counter++)
			{
				case 0:
				{
					params.put(PARAMETER_CRIMINAL_CLASS1, criminalClass);
					break;
				}
				case 1:
				{
					params.put(PARAMETER_CRIMINAL_CLASS2, criminalClass);
					break;
				}
				case 2:
				{
					params.put(PARAMETER_CRIMINAL_CLASS3, criminalClass);
					break;
				}
			}
		}
		
		params.put(PARAMETER_JUDGMENT_ISSUER,
	               AppUtils.replaceNumbersOnly(judgementInfo.getJudgIssuer(), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_JUDGMENT_DATE,
		           AppUtils.formatHijriGregorianDate(judgementInfo.getJudgDate() * 1000));
		params.put(PARAMETER_JUDGMENT_NUMBER,
		           AppUtils.replaceNumbersOnly(judgementInfo.getJudgNum(), Locales.SAUDI_AR_LOCALE));
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		params.put(PARAMETER_OPERATOR_ID,
		           AppUtils.replaceNumbersOnly(String.valueOf(userInfo.getOperatorId()), Locales.SAUDI_AR_LOCALE));
		
		params.put(PARAMETER_JAIL_YEARS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getJailYearCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_JAIL_MONTHS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getJailMonthCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_JAIL_DAYS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getJailDayCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_TAZEER_LASHES_COUNT,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getJudgTazeerLashesCount()),
                                       Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_HAD_LASHES_COUNT,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getJudgHadLashesCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_FINE,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getJudgFine()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_TRAVEL_BAN_YEARS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getTrvlBanYearCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_TRAVEL_BAN_MONTHS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getTrvlBanMonthCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_TRAVEL_BAN_DAYS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getTrvlBanDayCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_DEPORTATION_YEARS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getDeportYearCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_DEPORTATION_MONTHS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getDeportMonthCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_DEPORTATION_DAYS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getDeportDayCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_EXILING_YEARS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getExileYearCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_EXILING_MONTHS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getExileMonthCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_EXILING_DAYS,
           AppUtils.replaceNumbersOnly(String.valueOf(judgementInfo.getExileDayCount()), Locales.SAUDI_AR_LOCALE));
		params.put(PARAMETER_OTHER, judgementInfo.getJudgOthers());
		params.put(PARAMETER_IS_CRIMINAL_LIBEL, judgementInfo.isLibel());
		params.put(PARAMETER_IS_COVENANT, judgementInfo.isCovenant());
		params.put(PARAMETER_IS_DEPORTATION_FINAL, judgementInfo.isFinalDeport());
		params.put(PARAMETER_LOGO, Thread.currentThread().getContextClassLoader().getResourceAsStream(LOGO_FILE));
		
		return JasperFillManager.fillReport(jasperReport, params);
	}
}