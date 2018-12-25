package sa.gov.nic.bio.bw.workflow.registerconvictedpresent;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByGeneralFileNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryFromDisWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DisCriminalReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.ShareInformationPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.UpdatePersonInfoPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.BiometricsExchangeCrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.BiometricsExchangePartiesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.SubmittingConvictedReportWorkflowTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@AssociatedMenu(workflowId = 1008, menuId = "menu.register.registerConvictedPresent", menuTitle = "menu.title", menuOrder = 2,
				devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class,
			  BiometricsExchangeCrimeTypesLookup.class, BiometricsExchangePartiesLookup.class})
@Wizard({@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "camera", title = "wizard.facePhotoCapturing"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult"),
		@Step(iconId = "user", title = "wizard.personInfo"),
		@Step(iconId = "gavel", title = "wizard.judgementDetails"),
		@Step(iconId = "university", title = "wizard.punishmentDetails"),
		@Step(iconId = "share_alt", title = "wizard.shareInformation"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class RegisterConvictedReportPresentWorkflow extends WizardWorkflowBase
{
	private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
	private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
	private static final String FIELD_OLD_CRIMINAL_PERSON_INFO_MAP = "OLD_CRIMINAL_PERSON_INFO_MAP";
	private static final String FIELD_NEW_CRIMINAL_PERSON_INFO_MAP = "NEW_CRIMINAL_PERSON_INFO_MAP";
	
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				boolean acceptBadQualityFingerprint = "true".equals(Context.getConfigManager().getProperty(
													"registerConvictedReport.fingerprint.acceptBadQualityFingerprint"));
				int acceptBadQualityFingerprintMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
										"registerConvictedReport.fingerprint.acceptBadQualityFingerprintMinRetries"));
				
				setData(FingerprintCapturingFxController.class, "hidePreviousButton", Boolean.TRUE);
				setData(FingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
				        acceptBadQualityFingerprint);
				setData(FingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
				        acceptBadQualityFingerprintMinRetries);
		
				renderUiAndWaitForUserInput(FingerprintCapturingFxController.class);
				break;
			}
			case 1:
			{
				boolean acceptBadQualityFace = "true".equals(Context.getConfigManager().getProperty(
																"registerConvictedReport.face.acceptBadQualityFace"));
				int acceptBadQualityFaceMinRetries = Integer.parseInt(Context.getConfigManager().getProperty(
														"registerConvictedReport.face.acceptBadQualityFaceMinRetries"));
				
				setData(FaceCapturingFxController.class, "acceptBadQualityFace", acceptBadQualityFace);
				setData(FaceCapturingFxController.class, "acceptBadQualityFaceMinRetries",
				        acceptBadQualityFaceMinRetries);
		
				renderUiAndWaitForUserInput(FaceCapturingFxController.class);
				break;
			}
			case 2:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryByFingerprintsPaneFxController.class,
				         "status");
				
				renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
				
				Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");
				
				if(inquiryId == null)
				{
					passData(FingerprintCapturingFxController.class, "slapFingerprints",
					         FingerprintInquiryWorkflowTask.class, "fingerprints");
					passData(FingerprintCapturingFxController.class, FingerprintInquiryWorkflowTask.class,
					         "missingFingerprints");
					
					executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
				}
				
				passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				
				Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");
				if(status == Status.HIT)
				{
					Long civilBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
					                                 "civilBiometricsId");
					Long criminalBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
					                                    "criminalBiometricsId");
					if(civilBiometricsId != null)
					{
						setData(getClass(), FIELD_CIVIL_HIT, Boolean.TRUE);
						List<Long> civilPersonIds = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
						                                    "civilPersonIds");
						if(!civilPersonIds.isEmpty())
						{
							// LinkedHashMap is ordered
							Map<Long, PersonInfo> civilPersonInfoMap = new LinkedHashMap<>();
							
							for(Long civilPersonId : civilPersonIds)
							{
								setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
								setData(GetPersonInfoByIdWorkflowTask.class,
								        "returnNullResultInCaseNotFound", Boolean.TRUE);
								executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
								civilPersonInfoMap.put(civilPersonId, getData(GetPersonInfoByIdWorkflowTask.class,
								                                              "personInfo"));
							}
							
							setData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, civilPersonInfoMap);
						}
					}
					
					if(criminalBiometricsId != null)
					{
						setData(ConvictedReportInquiryFromDisWorkflowTask.class,
						        "criminalBiometricsId", criminalBiometricsId);
						setData(ConvictedReportInquiryFromDisWorkflowTask.class,
						        "returnNullResultInCaseNotFound", Boolean.TRUE);
						executeWorkflowTask(ConvictedReportInquiryFromDisWorkflowTask.class);
						List<DisCriminalReport> disCriminalReports =
								getData(ConvictedReportInquiryFromDisWorkflowTask.class,
								        "disCriminalReports");
						DisCriminalReportToPersonInfoConverter converter = new DisCriminalReportToPersonInfoConverter();
						Map<Integer, PersonInfo> oldCriminalPersonInfoMap;
						if(disCriminalReports != null) oldCriminalPersonInfoMap = disCriminalReports.stream().collect(
								Collectors.toMap(DisCriminalReport::getSequenceNumber, converter::convert,
								                 (k1, k2) -> k1, LinkedHashMap::new));
						else oldCriminalPersonInfoMap = new LinkedHashMap<>();
						setData(getClass(), FIELD_OLD_CRIMINAL_PERSON_INFO_MAP, oldCriminalPersonInfoMap);
						
						setData(ConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
						        "criminalBiometricsId", criminalBiometricsId);
						executeWorkflowTask(ConvictedReportInquiryByGeneralFileNumberWorkflowTask.class);
						List<ConvictedReport> convictedReports =
								getData(ConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
								        "convictedReports");
						ConvictedReportToPersonInfoConverter converter2 = new ConvictedReportToPersonInfoConverter();
						Map<Long, PersonInfo> newCriminalPersonInfoMap;
						if(convictedReports != null) newCriminalPersonInfoMap = convictedReports.stream().collect(
								Collectors.toMap(ConvictedReport::getReportNumber, converter2::convert,
								                 (k1, k2) -> k1, LinkedHashMap::new));
						else newCriminalPersonInfoMap = new LinkedHashMap<>();
						setData(getClass(), FIELD_NEW_CRIMINAL_PERSON_INFO_MAP, newCriminalPersonInfoMap);
					}
				}
				
				break;
			}
			case 3:
			{
				passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByFingerprintsResultPaneFxController.class,
				         "civilPersonInfoMap");
				passData(getClass(), FIELD_OLD_CRIMINAL_PERSON_INFO_MAP,
				         InquiryByFingerprintsResultPaneFxController.class, "oldCriminalPersonInfoMap");
				passData(getClass(), FIELD_NEW_CRIMINAL_PERSON_INFO_MAP,
				         InquiryByFingerprintsResultPaneFxController.class, "newCriminalPersonInfoMap");
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsResultPaneFxController.class,
				         "status", "civilBiometricsId", "criminalBiometricsId");
				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				break;
			}
			case 4:
			{
				passData(getClass(), FIELD_CIVIL_HIT, UpdatePersonInfoPaneFxController.class, "civilHit");
				passData(InquiryByFingerprintsResultPaneFxController.class, UpdatePersonInfoPaneFxController.class,
				         "normalizedPersonInfo");
				renderUiAndWaitForUserInput(UpdatePersonInfoPaneFxController.class);
				break;
			}
			case 5:
			{
				renderUiAndWaitForUserInput(JudgmentDetailsPaneFxController.class);
				break;
			}
			case 6:
			{
				renderUiAndWaitForUserInput(PunishmentDetailsPaneFxController.class);
				break;
			}
			case 7:
			{
				passData(JudgmentDetailsPaneFxController.class, ShareInformationPaneFxController.class,
				         "crimes");
				renderUiAndWaitForUserInput(ShareInformationPaneFxController.class);
				break;
			}
			case 8:
			{
				passData(FaceCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
				         "facePhotoBase64");
				
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
				         "civilBiometricsId", "criminalBiometricsId");
				
				passData(UpdatePersonInfoPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "firstName", "fatherName" , "grandfatherName" , "familyName" , "gender"
						, "nationality" , "occupation" , "birthPlace" , "birthDate" , "birthDateUseHijri" , "personId"
						, "personType" , "documentId" , "documentType" , "documentIssuanceDate" , "documentExpiryDate");
				
				passData(JudgmentDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "judgmentIssuer" , "judgmentNumber", "judgmentDate", "judgmentDateUseHijri",
				         "caseFileNumber", "prisonerNumber", "arrestDate", "arrestDateUseHijri");
				
				passData(PunishmentDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "tazeerLashes", "hadLashes", "fine", "jailYears", "jailMonths", "jailDays",
				         "travelBanYears", "travelBanMonths", "travelBanDays", "exilingYears", "exilingMonths",
				         "exilingDays", "deportationYears", "deportationMonths", "deportationDays", "finalDeportation",
				         "libel", "covenant", "other");
				
				passData(ShareInformationPaneFxController.class, "crimesWithShares",
				         ReviewAndSubmitPaneFxController.class, "crimes");
				
				passData(FingerprintCapturingFxController.class, "slapFingerprints",
				         ReviewAndSubmitPaneFxController.class, "fingerprints");
				
				passData(FingerprintCapturingFxController.class,
				         ReviewAndSubmitPaneFxController.class,
				         "fingerprintBase64Images", "missingFingerprints");
				
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
				
				passData(ReviewAndSubmitPaneFxController.class, SubmittingConvictedReportWorkflowTask.class,
				         "convictedReport");
				executeWorkflowTask(SubmittingConvictedReportWorkflowTask.class);
				
				break;
			}
			case 9:
			{
				passData(ReviewAndSubmitPaneFxController.class, ShowReportPaneFxController.class,
				         "convictedReport");
				passData(SubmittingConvictedReportWorkflowTask.class, ShowReportPaneFxController.class,
				         "convictedReportResponse");
				passData(FingerprintCapturingFxController.class, ShowReportPaneFxController.class,
				         "fingerprintBase64Images");
				renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
				break;
			}
		}
	}
}