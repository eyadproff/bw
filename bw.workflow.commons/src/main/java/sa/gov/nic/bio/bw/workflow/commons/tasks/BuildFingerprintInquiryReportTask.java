package sa.gov.nic.bio.bw.workflow.commons.tasks;

import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildFingerprintInquiryReportTask extends Task<JasperPrint>
{
	private static final Integer VALUE_FINGERPRINT_SOURCE_SYSTEM_CIVIL = 1;
	private static final Integer VALUE_FINGERPRINT_SOURCE_SYSTEM_OLD_CRIMINAL = 2;
	private static final Integer VALUE_FINGERPRINT_SOURCE_SYSTEM_NEW_CRIMINAL = 3;
	private static final String PARAMETER_LOGO = "LOGO";
	private static final String PARAMETER_INQUIRY_TIMESTAMP = "INQUIRY_TIMESTAMP";
	private static final String PARAMETER_INQUIRER_ID = "INQUIRER_ID";
	private static final String PARAMETER_CIVIL_HIT = "CIVIL_HIT";
	private static final String PARAMETER_CRIMINAL_HIT = "CRIMINAL_HIT";
	private static final String PARAMETER_CIVIL_BIOMETRICS_ID = "CIVIL_BIOMETRICS_ID";
	private static final String PARAMETER_CRIMINAL_BIOMETRICS_ID = "CRIMINAL_BIOMETRICS_ID";
	private static final String PARAMETER_PERSON_IDS_COUNT = "PERSON_IDS_COUNT";
	private static final String PARAMETER_OLD_CRIMINAL_SYSTEM_RECORDS_COUNT = "OLD_CRIMINAL_SYSTEM_RECORDS_COUNT";
	private static final String PARAMETER_NEW_CRIMINAL_SYSTEM_RECORDS_COUNT = "NEW_CRIMINAL_SYSTEM_RECORDS_COUNT";
	private static final String PARAMETER_FIRST_PAGE_COUNTER = "FIRST_PAGE_COUNTER";

	private static final String REPORT_TEMPLATE_FILE = "/sa/gov/nic/bio/bw/workflow/commons/reports/" +
													   "fingerprint_inquiry_report.jrxml";

	private String inquirerId;
	private boolean civilHit;
	private boolean criminalHit;
	private Long civilBiometricsId;
	private Long criminalBiometricsId;
	private Map<Long, PersonInfo> civilPersonInfoMap;
	private Map<Integer, PersonInfo> oldCriminalPersonInfoMap;
	private Map<Long, PersonInfo> newCriminalPersonInfoMap;
	private Map<Integer, String> fingerprintBase64Images;

	public BuildFingerprintInquiryReportTask(String inquirerId, boolean civilHit, boolean criminalHit,
											 Long civilBiometricsId, Long criminalBiometricsId,
											 Map<Long, PersonInfo> civilPersonInfoMap,
											 Map<Integer, PersonInfo> oldCriminalPersonInfoMap,
											 Map<Long, PersonInfo> newCriminalPersonInfoMap,
											 Map<Integer, String> fingerprintBase64Images)
	{
		this.inquirerId = inquirerId;
		this.civilHit = civilHit;
		this.criminalHit = criminalHit;
		this.civilBiometricsId = civilBiometricsId;
		this.criminalBiometricsId = criminalBiometricsId;
		this.civilPersonInfoMap = civilPersonInfoMap;
		this.oldCriminalPersonInfoMap = oldCriminalPersonInfoMap;
		this.newCriminalPersonInfoMap = newCriminalPersonInfoMap;
		this.fingerprintBase64Images = fingerprintBase64Images;
	}
	
	@Override
	protected JasperPrint call() throws Exception
	{
		String personIdsCount = civilPersonInfoMap != null ?
									AppUtils.localizeNumbers(String.valueOf(civilPersonInfoMap.size())) : null;
		String oldCriminalSystemRecordsCount = oldCriminalPersonInfoMap != null ?
									AppUtils.localizeNumbers(String.valueOf(oldCriminalPersonInfoMap.size())) : null;
		String newCriminalSystemRecordsCount = newCriminalPersonInfoMap != null ?
									AppUtils.localizeNumbers(String.valueOf(newCriminalPersonInfoMap.size())) : null;

		var reportStream = getClass().getResourceAsStream(REPORT_TEMPLATE_FILE);
		var jasperReport = JasperCompileManager.compileReport(reportStream);

		List<FingerprintInquiryRecord> records = new ArrayList<>();

		int pagesCount = 0;
		if(civilPersonInfoMap != null) pagesCount += civilPersonInfoMap.size();
		if(oldCriminalPersonInfoMap != null) pagesCount += oldCriminalPersonInfoMap.size();
		if(newCriminalPersonInfoMap != null) pagesCount += newCriminalPersonInfoMap.size();

		int finalPagesCount = pagesCount + 1;

		AtomicInteger counter = new AtomicInteger(2);

		if(civilPersonInfoMap != null) civilPersonInfoMap.forEach((recordId, personInfo) ->
			records.add(newFingerprintInquiryRecord(VALUE_FINGERPRINT_SOURCE_SYSTEM_CIVIL,
													AppUtils.localizeNumbers(String.valueOf(recordId)), personInfo,
													counter.getAndIncrement(), finalPagesCount)));

		if(oldCriminalPersonInfoMap != null) oldCriminalPersonInfoMap.forEach((recordId, personInfo) ->
			records.add(newFingerprintInquiryRecord(VALUE_FINGERPRINT_SOURCE_SYSTEM_OLD_CRIMINAL,
													AppUtils.localizeNumbers(String.valueOf(recordId)), personInfo,
													counter.getAndIncrement(), finalPagesCount)));

		if(newCriminalPersonInfoMap != null) newCriminalPersonInfoMap.forEach((recordId, personInfo) ->
			records.add(newFingerprintInquiryRecord(VALUE_FINGERPRINT_SOURCE_SYSTEM_NEW_CRIMINAL,
													AppUtils.localizeNumbers(String.valueOf(recordId)), personInfo,
													counter.getAndIncrement(), finalPagesCount)));
		
		HashMap<String, Object> params = new HashMap<>();
		params.put(PARAMETER_LOGO, CommonImages.LOGO_SAUDI_SECURITY.getAsInputStream());
		params.put(PARAMETER_INQUIRY_TIMESTAMP, AppUtils.get3LinesTimestampInArabic());
		params.put(PARAMETER_INQUIRER_ID, inquirerId);
		params.put(PARAMETER_CIVIL_HIT, civilHit);
		params.put(PARAMETER_CRIMINAL_HIT, criminalHit);
		params.put(PARAMETER_CIVIL_BIOMETRICS_ID, AppUtils.localizeNumbers(String.valueOf(civilBiometricsId)));
		params.put(PARAMETER_CRIMINAL_BIOMETRICS_ID, AppUtils.localizeNumbers(String.valueOf(criminalBiometricsId)));
		params.put(PARAMETER_PERSON_IDS_COUNT, personIdsCount);
		params.put(PARAMETER_OLD_CRIMINAL_SYSTEM_RECORDS_COUNT, oldCriminalSystemRecordsCount);
		params.put(PARAMETER_NEW_CRIMINAL_SYSTEM_RECORDS_COUNT, newCriminalSystemRecordsCount);
		params.put(PARAMETER_FIRST_PAGE_COUNTER, GuiUtils.getPageCounterFooterInArabic(1,
																					 records.size() + 1));

		if(fingerprintBase64Images != null) fingerprintBase64Images.forEach((position, fingerprintImage) ->
		{
			if(position < 1 || position > 10) return;

			byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
			params.put("FINGER_" + position, new ByteArrayInputStream(bytes));
		});

		var dataSource = new JRBeanCollectionDataSource(records);
		
		return JasperFillManager.fillReport(jasperReport, params, dataSource);
	}

	private static FingerprintInquiryRecord newFingerprintInquiryRecord(Integer fingerprintSourceSystem,
																		String recordId, PersonInfo personInfo,
																		int pageNumber, int pagesCount)
	{
		var normalizedPersonInfo = new NormalizedPersonInfo(personInfo);
		var record = new FingerprintInquiryRecord();

		record.setRecordSourceSystem(fingerprintSourceSystem);
		record.setRecordId(recordId);

		var facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();

		if(facePhotoBase64 != null)
		{
			byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
			record.setFaceImage(new ByteArrayInputStream(bytes));
		}
		else record.setFaceImage(CommonImages.PLACEHOLDER_AVATAR.getAsInputStream());

		Name name = personInfo.getName();
		StringBuilder sb = new StringBuilder();

		String firstName = name.getFirstName();
		String fatherName = name.getFatherName();
		String grandfatherName = name.getGrandfatherName();
		String familyName = name.getFamilyName();

		if(firstName != null) sb.append(firstName).append(" ");
		if(fatherName != null) sb.append(fatherName).append(" ");
		if(grandfatherName != null) sb.append(grandfatherName).append(" ");
		if(familyName != null) sb.append(familyName);
		String fullName = sb.toString().stripTrailing();

		record.setName(fullName);

		sb = new StringBuilder();

		firstName = name.getTranslatedFirstName();
		fatherName = name.getTranslatedFatherName();
		grandfatherName = name.getTranslatedGrandFatherName();
		familyName = name.getTranslatedFamilyName();

		if(firstName != null) sb.append(firstName).append(" ");
		if(fatherName != null) sb.append(fatherName).append(" ");
		if(grandfatherName != null) sb.append(grandfatherName).append(" ");
		if(familyName != null) sb.append(familyName);
		fullName = sb.toString().stripTrailing();

		record.setTranslatedName(fullName);

		var nationality = normalizedPersonInfo.getNationality();
		var occupation = normalizedPersonInfo.getOccupation();
		var gender = normalizedPersonInfo.getGender();
		var personId = normalizedPersonInfo.getPersonId();
		var personType = normalizedPersonInfo.getPersonType();
		var documentId = normalizedPersonInfo.getDocumentId();
		var documentType = normalizedPersonInfo.getDocumentType();
		var documentIssuanceDate = normalizedPersonInfo.getDocumentIssuanceDate();
		var documentExpiryDate = normalizedPersonInfo.getDocumentExpiryDate();
		var birthDate = normalizedPersonInfo.getBirthDate();
		var birthPlace = normalizedPersonInfo.getBirthPlace();

		String nationalityText = nationality != null ? nationality.getArabicText() : null;

		if(nationality != null && nationality.getCode() > 0 &&
		   !"SAU".equalsIgnoreCase(nationality.getMofaNationalityCode()) &&
		   String.valueOf(normalizedPersonInfo.getPersonId()).startsWith("1"))
		{
			// \u202B is used to render the brackets correctly
			nationalityText += " \u202B" + AppUtils.getCoreStringsResourceBundle(AppConstants.Locales.SAUDI_AR_LOCALE)
												   .getString("label.naturalizedSaudi");
		}

		record.setNationality(nationalityText);
		record.setOccupation(occupation);
		if(gender != null) record.setGender(gender.toString(AppConstants.Locales.SAUDI_AR_LOCALE));
		if(personId != null) record.setPersonId(AppUtils.localizeNumbers(String.valueOf(personId)));
		if(personType != null) record.setPersonIdType(personType.getArabicText());
		if(documentId != null) record.setDocumentId(AppUtils.localizeNumbers(documentId));
		if(documentType != null) record.setDocumentIdType(documentType.getArabicText());
		if(documentIssuanceDate != null) record.setDocumentIssuanceDate(
															AppUtils.formatHijriGregorianDate(documentIssuanceDate));
		if(documentExpiryDate != null) record.setDocumentExpiryDate(
															AppUtils.formatHijriGregorianDate(documentExpiryDate));
		if(birthDate != null) record.setDateOfBirth(AppUtils.formatHijriGregorianDate(birthDate));
		if(birthPlace != null) record.setBirthPlace(birthPlace);

		record.setPageCounter(GuiUtils.getPageCounterFooterInArabic(pageNumber, pagesCount));

		return record;
	}
}