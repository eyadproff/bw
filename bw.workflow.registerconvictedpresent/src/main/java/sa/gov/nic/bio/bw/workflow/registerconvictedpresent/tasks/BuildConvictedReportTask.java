package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.utils.AppConstants.Locales;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.webservice.Country;
import sa.gov.nic.bio.bw.workflow.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PersonType;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.JudgementInfo;

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
	private static final String PARAMETER_OCCUPATION = "OCCUPATION";
	private static final String PARAMETER_GENDER = "GENDER";
	private static final String PARAMETER_BIOMETRICS_ID = "BIOMETRICS_ID";
	private static final String PARAMETER_SAMIS_ID = "SAMIS_ID";
	private static final String PARAMETER_SAMIS_ID_TYPE = "SAMIS_ID_TYPE";
	private static final String PARAMETER_DOCUMENT_ID = "DOCUMENT_ID";
	private static final String PARAMETER_DOCUMENT_TYPE = "DOCUMENT_TYPE";
	private static final String PARAMETER_DOCUMENT_ISSUANCE_DATE = "DOCUMENT_ISSUANCE_DATE";
	private static final String PARAMETER_DOCUMENT_EXPIRY_DATE = "DOCUMENT_EXPIRY_DATE";
	private static final String PARAMETER_BIRTH_OF_DATE = "BIRTH_OF_DATE";
	private static final String PARAMETER_BIRTH_PLACE = "BIRTH_PLACE";
	private static final String PARAMETER_CASE_FILE_NUMBER = "CASE_FILE_NUMBER";
	private static final String PARAMETER_ARREST_DATE = "ARREST_DATE";
	private static final String PARAMETER_CRIMINAL_CLASS1 = "CRIMINAL_CLASS1";
	private static final String PARAMETER_CRIMINAL_CLASS2 = "CRIMINAL_CLASS2";
	private static final String PARAMETER_CRIMINAL_CLASS3 = "CRIMINAL_CLASS3";
	private static final String PARAMETER_CRIMINAL_CLASS4 = "CRIMINAL_CLASS4";
	private static final String PARAMETER_CRIMINAL_CLASS5 = "CRIMINAL_CLASS5";
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
	
	private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/features/registerconvictedpresent" +
													   "/reports/convicted_record.jrxml";
	
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
		InputStream reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
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
			params.put(PARAMETER_FACE_IMAGE, CommonImages.PLACEHOLDER_AVATAR.getAsInputStream());
		}
		
		fingerprintImages.forEach((position, fingerprintImage) ->
		{
			byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
			params.put("FINGER_" + position, new ByteArrayInputStream(bytes));
		});
		
		params.put(PARAMETER_REPORT_NUMBER,
	           AppUtils.localizeNumbers(String.valueOf(convictedReport.getReportNumber()), Locales.SAUDI_AR_LOCALE,
	                                    true));
		params.put(PARAMETER_REF_NUMBER,
           AppUtils.localizeNumbers(String.valueOf(convictedReport.getGeneralFileNum()), Locales.SAUDI_AR_LOCALE,
                                    true));
		params.put(PARAMETER_REPORT_DATE,
		           AppUtils.formatHijriGregorianDateTime(convictedReport.getReportDate() * 1000));
		
		Name name = convictedReport.getSubjtName();
		String fullName = name.getFirstName() + " " + name.getFatherName() + " " + name.getGrandfatherName() +
						  " " + name.getFamilyName();
		fullName = fullName.trim().replaceAll("\\s+", " "); // remove extra spaces
		
		params.put(PARAMETER_NAME, fullName);
		
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		Country countryBean = null;
		
		for(Country country : countries)
		{
			if(country.getCode() == convictedReport.getSubjNationalityCode())
			{
				countryBean = country;
				break;
			}
		}
		
		if(countryBean != null) params.put(PARAMETER_NATIONALITY, countryBean.getDescriptionAR());
		
		params.put(PARAMETER_OCCUPATION, AppUtils.localizeNumbers(convictedReport.getSubjOccupation(),
		                                                          Locales.SAUDI_AR_LOCALE,
		                                                          true));
		params.put(PARAMETER_GENDER, "F".equals(convictedReport.getSubjGender()) ? "أنثى" : "ذكر");
		
		Long subjBioId = convictedReport.getSubjBioId();
		if(subjBioId != null) params.put(PARAMETER_BIOMETRICS_ID, AppUtils.localizeNumbers(String.valueOf(subjBioId),
		                                                                                   Locales.SAUDI_AR_LOCALE,
		                                                                                   true));
		
		Long subjSamisId = convictedReport.getSubjSamisId();
		if(subjSamisId != null) params.put(PARAMETER_SAMIS_ID, AppUtils.localizeNumbers(String.valueOf(subjSamisId),
		                                                                                Locales.SAUDI_AR_LOCALE,
		                                                                                true));
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>)
														Context.getUserSession().getAttribute(SamisIdTypesLookup.KEY);
		
		Integer subjSamisType = convictedReport.getSubjSamisType();
		if(subjSamisType != null)
		{
			PersonType it = null;
			for(PersonType type : personTypes)
			{
				if(type.getCode() == subjSamisType)
				{
					it = type;
					break;
				}
			}
			
			if(it != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				params.put(PARAMETER_SAMIS_ID_TYPE, arabic ? it.getDescriptionAR() : it.getDescriptionEN());
			}
		}
		
		String subjDocId = convictedReport.getSubjDocId();
		if(subjDocId != null) params.put(PARAMETER_DOCUMENT_ID, AppUtils.localizeNumbers(subjDocId,
		                                                                                 Locales.SAUDI_AR_LOCALE,
		                                                                                 true));
		
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
		
		Integer subjDocType = convictedReport.getSubjDocType();
		if(subjDocType != null)
		{
			DocumentType it = null;
			for(DocumentType type : documentTypes)
			{
				if(type.getCode() == subjDocType)
				{
					it = type;
					break;
				}
			}
			
			if(it != null) params.put(PARAMETER_DOCUMENT_TYPE, it.getDesc());
		}
		
		Long subjDocIssDate = convictedReport.getSubjDocIssDate();
		if(subjDocIssDate != null) params.put(PARAMETER_DOCUMENT_ISSUANCE_DATE,
		                                      AppUtils.formatHijriGregorianDate(subjDocIssDate * 1000));
		
		Long subjBirthDate = convictedReport.getSubjBirthDate();
		if(subjBirthDate != null) params.put(PARAMETER_BIRTH_OF_DATE,
		                                     AppUtils.formatHijriGregorianDate(subjBirthDate * 1000));
		params.put(PARAMETER_BIRTH_PLACE, AppUtils.localizeNumbers(convictedReport.getSubjBirthPlace(),
	                                                           Locales.SAUDI_AR_LOCALE, true));
		
		Long subjDocExpDate = convictedReport.getSubjDocExpDate();
		if(subjDocExpDate != null) params.put(PARAMETER_DOCUMENT_EXPIRY_DATE,
		                                      AppUtils.formatHijriGregorianDate(subjDocExpDate * 1000));
		
		JudgementInfo judgementInfo = convictedReport.getSubjJudgementInfo();
		
		String caseFileNum = judgementInfo.getPoliceFileNum();
		if(caseFileNum != null) params.put(PARAMETER_CASE_FILE_NUMBER, AppUtils.localizeNumbers(caseFileNum,
		                                                                                Locales.SAUDI_AR_LOCALE,
	                                                                                    true));
		
		Long arrestDate = judgementInfo.getArrestDate();
		if(arrestDate != null) params.put(PARAMETER_ARREST_DATE,
		                                            AppUtils.formatHijriGregorianDate(arrestDate * 1000));
		
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
		
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
				case 3:
				{
					params.put(PARAMETER_CRIMINAL_CLASS4, criminalClass);
					break;
				}
				case 4:
				{
					params.put(PARAMETER_CRIMINAL_CLASS5, criminalClass);
					break;
				}
			}
		}
		
		params.put(PARAMETER_JUDGMENT_ISSUER,
	               AppUtils.localizeNumbers(judgementInfo.getJudgIssuer(), Locales.SAUDI_AR_LOCALE,
	                                        true));
		params.put(PARAMETER_JUDGMENT_DATE,
		           AppUtils.formatHijriGregorianDate(judgementInfo.getJudgDate() * 1000));
		params.put(PARAMETER_JUDGMENT_NUMBER,
		           AppUtils.localizeNumbers(judgementInfo.getJudgNum(), Locales.SAUDI_AR_LOCALE,
		                                    true));
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		params.put(PARAMETER_OPERATOR_ID,
		           AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()), Locales.SAUDI_AR_LOCALE,
		                                    true));
		
		if(judgementInfo.getJailYearCount() > 0) params.put(PARAMETER_JAIL_YEARS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJailYearCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getJailMonthCount() > 0) params.put(PARAMETER_JAIL_MONTHS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJailMonthCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getJailDayCount() > 0) params.put(PARAMETER_JAIL_DAYS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJailDayCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getJudgTazeerLashesCount() > 0) params.put(PARAMETER_TAZEER_LASHES_COUNT,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJudgTazeerLashesCount()),
                                    Locales.SAUDI_AR_LOCALE, true));
		if(judgementInfo.getJudgHadLashesCount() > 0) params.put(PARAMETER_HAD_LASHES_COUNT,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJudgHadLashesCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getJudgFine() > 0) params.put(PARAMETER_FINE,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJudgFine()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getTrvlBanYearCount() > 0) params.put(PARAMETER_TRAVEL_BAN_YEARS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getTrvlBanYearCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getTrvlBanMonthCount() > 0) params.put(PARAMETER_TRAVEL_BAN_MONTHS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getTrvlBanMonthCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getTrvlBanDayCount() > 0) params.put(PARAMETER_TRAVEL_BAN_DAYS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getTrvlBanDayCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getDeportYearCount() > 0) params.put(PARAMETER_DEPORTATION_YEARS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getDeportYearCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getDeportMonthCount() > 0) params.put(PARAMETER_DEPORTATION_MONTHS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getDeportMonthCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getDeportDayCount() > 0) params.put(PARAMETER_DEPORTATION_DAYS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getDeportDayCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getExileYearCount() > 0) params.put(PARAMETER_EXILING_YEARS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getExileYearCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getExileMonthCount() > 0) params.put(PARAMETER_EXILING_MONTHS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getExileMonthCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		if(judgementInfo.getExileDayCount() > 0) params.put(PARAMETER_EXILING_DAYS,
           AppUtils.localizeNumbers(String.valueOf(judgementInfo.getExileDayCount()), Locales.SAUDI_AR_LOCALE,
                                    true));
		params.put(PARAMETER_OTHER, judgementInfo.getJudgOthers());
		params.put(PARAMETER_IS_CRIMINAL_LIBEL, judgementInfo.isLibel());
		params.put(PARAMETER_IS_COVENANT, judgementInfo.isCovenant());
		params.put(PARAMETER_IS_DEPORTATION_FINAL, judgementInfo.isFinalDeport());
		params.put(PARAMETER_LOGO, CommonImages.LOGO_SAUDI_SECURITY.getAsInputStream());
		
		return JasperFillManager.fillReport(jasperReport, params);
	}
}