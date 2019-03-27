package sa.gov.nic.bio.bw.workflow.registerconvictedpresent;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeDecision;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangeCrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangePartiesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryFromDisWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DeporteeInfoToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DisCriminalReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetDeporteeInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SegmentWsqFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.RegisteringConvictedReportPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.RegisteringConvictedReportPaneFxController.Request;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.ShareInformationPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.UpdatePersonInfoPaneFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.CriminalFingerprintsStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.ExchangeConvictedReportWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.GenerateNewCriminalBiometricsIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.SubmitConvictedReportWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.SubmitCriminalFingerprintsWorkflowTask;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
		@Step(iconId = "save", title = "wizard.registerReport"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class RegisterConvictedReportPresentWorkflow extends WizardWorkflowBase
{
	private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
	private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
	private static final String FIELD_OLD_CRIMINAL_PERSON_INFO_MAP = "OLD_CRIMINAL_PERSON_INFO_MAP";
	private static final String FIELD_NEW_CRIMINAL_PERSON_INFO_MAP = "NEW_CRIMINAL_PERSON_INFO_MAP";
	
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
								if(civilPersonId == null) continue;
								
								PersonInfo personInfo;
								
								String sCivilPersonId = String.valueOf(civilPersonId);
								if(sCivilPersonId.length() == 10 && sCivilPersonId.startsWith("9"))
								{
									setData(GetDeporteeInfoByIdWorkflowTask.class, "deporteeId",
									        civilPersonId);
									setData(GetDeporteeInfoByIdWorkflowTask.class,
									        "returnNullResultInCaseNotFound", Boolean.TRUE);
									executeWorkflowTask(GetDeporteeInfoByIdWorkflowTask.class);
									DeporteeInfo deporteeInfo = getData(GetDeporteeInfoByIdWorkflowTask.class,
									                                    "deporteeInfo");
									personInfo = new DeporteeInfoToPersonInfoConverter().convert(deporteeInfo);
								}
								else
								{
									setData(GetPersonInfoByIdWorkflowTask.class, "personId", civilPersonId);
									setData(GetPersonInfoByIdWorkflowTask.class,
									        "returnNullResultInCaseNotFound", Boolean.TRUE);
									executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
									personInfo = getData(GetPersonInfoByIdWorkflowTask.class, "personInfo");
								}
								
								civilPersonInfoMap.put(civilPersonId, personInfo);
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
						
						setData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
						        "criminalBiometricsId", criminalBiometricsId);
						setData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
						        "returnNullResultInCaseNotFound", Boolean.TRUE);
						executeWorkflowTask(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class);
						List<ConvictedReport> convictedReports =
								getData(BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask.class,
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
				passData(FaceCapturingFxController.class, "facePhotoBase64",
				         UpdatePersonInfoPaneFxController.class, "cameraFacePhotoBase64");
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
				passData(UpdatePersonInfoPaneFxController.class, ShareInformationPaneFxController.class,
				         "facePhotoBase64", "firstName", "familyName", "gender", "nationality",
				         "birthDate", "documentId", "documentType", "documentIssuanceDate", "documentExpiryDate");
				passData(JudgmentDetailsPaneFxController.class, ShareInformationPaneFxController.class,
				         "crimes", "judgmentDate");
				renderUiAndWaitForUserInput(ShareInformationPaneFxController.class);
				break;
			}
			case 8:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
				         "civilBiometricsId", "criminalBiometricsId");
				
				passData(UpdatePersonInfoPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "facePhotoBase64", "firstName", "fatherName", "grandfatherName",
				         "familyName", "gender", "nationality", "occupation", "birthPlace", "birthDate",
				         "birthDateUseHijri", "personId", "personType", "documentId", "documentType",
				         "documentIssuanceDate", "documentExpiryDate");
				
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
				
				setData(ReviewAndSubmitPaneFxController.class, "fingerprintsSourceSystem",
				        ConvictedReport.FingerprintsSource.LIVE_SCAN);
				
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
				
				break;
			}
			case 9:
			{
				ConvictedReport convictedReport = getData(ReviewAndSubmitPaneFxController.class,
				                                          "convictedReport");
				boolean sharable = false;
				List<CrimeCode> crimeCodes = convictedReport.getCrimeCodes();
				if(crimeCodes != null)
				{
					outerLoop: for(CrimeCode crimeCode : crimeCodes)
					{
						if(crimeCode == null) continue;
						List<BiometricsExchangeDecision> criminalBioExchange = crimeCode.getCriminalBioExchange();
						if(criminalBioExchange != null)
						{
							for(BiometricsExchangeDecision biometricsExchangeDecision : criminalBioExchange)
							{
								if(biometricsExchangeDecision == null) continue;
								boolean operatorDecision = biometricsExchangeDecision.getOperatorDecision();
								if(operatorDecision)
								{
									sharable = true;
									break outerLoop;
								}
							}
						}
					}
				}
				
				Long criminalBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				                                    "criminalBiometricsId");
				
				setData(RegisteringConvictedReportPaneFxController.class, "registerFingerprints",
				        criminalBiometricsId == null);
				setData(RegisteringConvictedReportPaneFxController.class, "exchangeConvictedReport",
				        sharable);
				renderUiAndWaitForUserInput(RegisteringConvictedReportPaneFxController.class);
				
				Request request = getData(RegisteringConvictedReportPaneFxController.class, "request");
				if(request == Request.GENERATE_NEW_CRIMINAL_BIOMETRICS_ID)
				{
					executeWorkflowTask(GenerateNewCriminalBiometricsIdWorkflowTask.class);
				}
				else if(request == Request.SUBMIT_FINGERPRINTS)
				{
					passData(GenerateNewCriminalBiometricsIdWorkflowTask.class,
					         SubmitCriminalFingerprintsWorkflowTask.class, "criminalBiometricsId");
					passData(FingerprintCapturingFxController.class, "slapFingerprints",
					         SubmitCriminalFingerprintsWorkflowTask.class, "fingerprints");
					passData(FingerprintCapturingFxController.class, SubmitCriminalFingerprintsWorkflowTask.class,
					         "missingFingerprints");
					executeWorkflowTask(SubmitCriminalFingerprintsWorkflowTask.class);
				}
				else if(request == Request.CHECK_FINGERPRINTS)
				{
					passData(SubmitCriminalFingerprintsWorkflowTask.class,
					         CriminalFingerprintsStatusCheckerWorkflowTask.class, "tcn");
					executeWorkflowTask(CriminalFingerprintsStatusCheckerWorkflowTask.class);
					
					passData(CriminalFingerprintsStatusCheckerWorkflowTask.class, "status",
					         RegisteringConvictedReportPaneFxController.class,
					         "criminalFingerprintsRegistrationStatus");
				}
				else if(request == Request.SUBMIT_CONVICTED_REPORT)
				{
					if(criminalBiometricsId == null)
					{
						criminalBiometricsId = getData(GenerateNewCriminalBiometricsIdWorkflowTask.class,
						                               "criminalBiometricsId");
						convictedReport.setGeneralFileNumber(criminalBiometricsId);
					}
					
					setData(SubmitConvictedReportWorkflowTask.class, "convictedReport", convictedReport);
					executeWorkflowTask(SubmitConvictedReportWorkflowTask.class);
				}
				else if(request == Request.EXCHANGE_CONVICTED_REPORT)
				{
					setData(SegmentWsqFingerprintsWorkflowTask.class,
					        "fingerprints", convictedReport.getSubjFingers());
					setData(SegmentWsqFingerprintsWorkflowTask.class,
					        "missingFingerprints", convictedReport.getSubjMissingFingers());
					executeWorkflowTask(SegmentWsqFingerprintsWorkflowTask.class);
					
					setData(ExchangeConvictedReportWorkflowTask.class, "convictedReport", convictedReport);
					executeWorkflowTask(ExchangeConvictedReportWorkflowTask.class);
				}
				
				break;
			}
			case 10:
			{
				passData(ReviewAndSubmitPaneFxController.class, ShowReportPaneFxController.class,
				         "convictedReport");
				passData(SubmitConvictedReportWorkflowTask.class, ShowReportPaneFxController.class,
				         "convictedReportResponse");
				passData(FingerprintCapturingFxController.class, ShowReportPaneFxController.class,
				         "fingerprintBase64Images");
				renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
				break;
			}
		}
	}
}