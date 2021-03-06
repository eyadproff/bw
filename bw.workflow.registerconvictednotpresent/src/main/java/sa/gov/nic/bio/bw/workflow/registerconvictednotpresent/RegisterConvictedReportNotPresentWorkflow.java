package sa.gov.nic.bio.bw.workflow.registerconvictednotpresent;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.CivilBiometricsIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.CriminalBiometricsIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.FingerprintsSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.UploadNistFileFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.ExtractingDataFromNistFileWorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.RetrieveFingerprintsAvailabilityByCivilBiometricIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.RetrieveFingerprintsByCivilBiometricIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.RetrieveFingerprintsByCriminalBiometricIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeDecision;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangeCrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangePartiesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.BasicConvictedReportInquiryByGeneralFileNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryFromDisWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DeporteeInfoToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DisCriminalReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingMissingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintsWsqToFingerConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetDeporteeInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SegmentWsqFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalWorkflowSource;
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

@AssociatedMenu(workflowId = 1007, menuId = "menu.register.registerConvictedNotPresent", menuTitle = "menu.title",
				menuOrder = 3, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class,
			  BiometricsExchangeCrimeTypesLookup.class, BiometricsExchangePartiesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
		@Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		@Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.showInquiryResult"),
		@Step(iconId = "user", title = "wizard.updatePersonInformation"),
		@Step(iconId = "gavel", title = "wizard.addJudgementDetails"),
		@Step(iconId = "university", title = "wizard.addPunishmentDetails"),
		@Step(iconId = "share_alt", title = "wizard.shareInformation"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "save", title = "wizard.registerReport"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class RegisterConvictedReportNotPresentWorkflow extends WizardWorkflowBase
{
	private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
	private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
	private static final String FIELD_OLD_CRIMINAL_PERSON_INFO_MAP = "OLD_CRIMINAL_PERSON_INFO_MAP";
	private static final String FIELD_NEW_CRIMINAL_PERSON_INFO_MAP = "NEW_CRIMINAL_PERSON_INFO_MAP";
	private static final String FIELD_FINGERPRINT_SOURCE = "FINGERPRINT_SOURCE";
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(FingerprintsSourceFxController.class);
				break;
			}
			case 1:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
		
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					setData(getClass(), FIELD_FINGERPRINT_SOURCE, CriminalFingerprintSource.IMPORT_BY_SAMIS_ID);
					
					renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
					
					passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class,
					         "personId");
					
					executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);
					
					passData(PersonIdPaneFxController.class, FetchingFingerprintsWorkflowTask.class,
					         "personId");
					
					executeWorkflowTask(FetchingFingerprintsWorkflowTask.class);
					
					passData(PersonIdPaneFxController.class, FetchingMissingFingerprintsWorkflowTask.class,
					         "personId");
					executeWorkflowTask(FetchingMissingFingerprintsWorkflowTask.class);
					
					passData(FetchingFingerprintsWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "fingerprints");
					passData(FetchingMissingFingerprintsWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "missingFingerprints");
					executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				}
				else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
				{
					setData(getClass(), FIELD_FINGERPRINT_SOURCE, CriminalFingerprintSource.IMPORT_BY_BIO_ID);
					
					incrementNSteps(1); // to skip step #2 on going next
					
					renderUiAndWaitForUserInput(CivilBiometricsIdPaneFxController.class);
					
					passData(CivilBiometricsIdPaneFxController.class,
					         RetrieveFingerprintsByCivilBiometricIdWorkflowTask.class,
					         "civilBiometricsId");
					executeWorkflowTask(RetrieveFingerprintsByCivilBiometricIdWorkflowTask.class);
					
					passData(CivilBiometricsIdPaneFxController.class,
					         RetrieveFingerprintsAvailabilityByCivilBiometricIdWorkflowTask.class,
					         "civilBiometricsId");
					executeWorkflowTask(RetrieveFingerprintsAvailabilityByCivilBiometricIdWorkflowTask.class);
					
					passData(RetrieveFingerprintsByCivilBiometricIdWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "fingerprints");
					passData(RetrieveFingerprintsAvailabilityByCivilBiometricIdWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "missingFingerprints");
					executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				}
				else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
				{
					setData(getClass(), FIELD_FINGERPRINT_SOURCE, CriminalFingerprintSource.IMPORT_BY_CRIMINAL_ID);
					
					incrementNSteps(1); // to skip step #2 on going next
					
					renderUiAndWaitForUserInput(CriminalBiometricsIdPaneFxController.class);
					
					passData(CriminalBiometricsIdPaneFxController.class,
					         RetrieveFingerprintsByCriminalBiometricIdWorkflowTask.class,
					         "criminalBiometricsId");
					executeWorkflowTask(RetrieveFingerprintsByCriminalBiometricIdWorkflowTask.class);
					
					passData(RetrieveFingerprintsByCriminalBiometricIdWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "fingerprints");
					passData(RetrieveFingerprintsByCriminalBiometricIdWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "missingFingerprints");
					executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					setData(getClass(), FIELD_FINGERPRINT_SOURCE, CriminalFingerprintSource.IMPORT_BY_FINGERPRINTS_CARD_SCANNER);
					
					renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					setData(getClass(), FIELD_FINGERPRINT_SOURCE, CriminalFingerprintSource.IMPORT_BY_NIST_FILE);
					
					renderUiAndWaitForUserInput(UploadNistFileFxController.class);
					
					passData(UploadNistFileFxController.class, "filePath",
					         ExtractingDataFromNistFileWorkflowTask.class, "nistFilePath");
					executeWorkflowTask(ExtractingDataFromNistFileWorkflowTask.class);
					
					passData(ExtractingDataFromNistFileWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "fingerprints");
					passData(ExtractingDataFromNistFileWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "missingFingerprints");
					executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				}
				
				break;
			}
			case 2:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					passData(GetPersonInfoByIdWorkflowTask.class, ShowingPersonInfoFxController.class,
					         "personInfo");
					
					renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(ScanFingerprintCardPaneFxController.class,
					         SpecifyFingerprintCoordinatesPaneFxController.class, "cardImage");
					
					renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					passData(ExtractingDataFromNistFileWorkflowTask.class, ShowingPersonInfoFxController.class,
					         "personInfo");
					
					renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);
				}
				
				break;
			}
			case 3:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					PersonInfo personInfo = getData(GetPersonInfoByIdWorkflowTask.class,
													"personInfo");
					if(personInfo != null) setData(ShowingFingerprintsPaneFxController.class,
												  "facePhotoBase64", personInfo.getFace());
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
				{
					incrementNSteps(-1); // to skip step #2 on going previous
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
				{
					incrementNSteps(-1); // to skip step #2 on going previous
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					PersonInfo personInfo = getData(ExtractingDataFromNistFileWorkflowTask.class,
							"personInfo");
					if(personInfo != null) setData(ShowingFingerprintsPaneFxController.class,
												   "facePhotoBase64", personInfo.getFace());
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
				}
				
				renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
				
				break;
			}
			case 4:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryByFingerprintsPaneFxController.class,
				         "status");
				
				renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
				
				Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");
				
				if(inquiryId == null)
				{
					Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
					                                    "fingerprintsSource");
					
					if(fingerprintsSource == Source.ENTERING_PERSON_ID)
					{
						passData(FetchingFingerprintsWorkflowTask.class, FingerprintInquiryWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class, FingerprintInquiryWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
					{
						passData(RetrieveFingerprintsByCivilBiometricIdWorkflowTask.class,
						         FingerprintInquiryWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         FingerprintInquiryWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
					{
						passData(RetrieveFingerprintsByCriminalBiometricIdWorkflowTask.class,
						         FingerprintInquiryWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         FingerprintInquiryWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
					{
						passData(SpecifyFingerprintCoordinatesPaneFxController.class,
						         ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
						         "fingerprintBase64Images");
						executeWorkflowTask(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class);
						
						passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
						         "fingerprintWsqImages", FingerprintInquiryWorkflowTask.class,
						         "fingerprints", new FingerprintsWsqToFingerConverter());
						passData(ShowingFingerprintsPaneFxController.class,
						         FingerprintInquiryWorkflowTask.class, "missingFingerprints");
					}
					else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
					{
						passData(ExtractingDataFromNistFileWorkflowTask.class, FingerprintInquiryWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class, FingerprintInquiryWorkflowTask.class,
						         "missingFingerprints");
					}
					
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
			case 5:
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

				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
						"fingerprintsSource");

				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
							InquiryByFingerprintsResultPaneFxController.class,
							"fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
							InquiryByFingerprintsResultPaneFxController.class,
							"fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
							InquiryByFingerprintsResultPaneFxController.class,
							"fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
							InquiryByFingerprintsResultPaneFxController.class,
							"fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
							InquiryByFingerprintsResultPaneFxController.class,
							"fingerprintBase64Images");
				}

				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				
				break;
			}
			case 6:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					PersonInfo personInfo = getData(ExtractingDataFromNistFileWorkflowTask.class,
					                                "personInfo");
					if(personInfo != null) setData(UpdatePersonInfoPaneFxController.class,
					                               "facePhotoBase64FromAnotherSource",
					                               personInfo.getFace());
				}
				
				passData(getClass(), FIELD_CIVIL_HIT, UpdatePersonInfoPaneFxController.class, "civilHit");
				passData(InquiryByFingerprintsResultPaneFxController.class, UpdatePersonInfoPaneFxController.class,
				         "normalizedPersonInfo");
				renderUiAndWaitForUserInput(UpdatePersonInfoPaneFxController.class);
				break;
			}
			case 7:
			{
				renderUiAndWaitForUserInput(JudgmentDetailsPaneFxController.class);
				break;
			}
			case 8:
			{
				renderUiAndWaitForUserInput(PunishmentDetailsPaneFxController.class);
				break;
			}
			case 9:
			{
				passData(UpdatePersonInfoPaneFxController.class, ShareInformationPaneFxController.class,
				         "facePhotoBase64", "firstName", "familyName", "gender", "nationality",
				         "birthDate", "documentId", "documentType", "documentIssuanceDate", "documentExpiryDate");
				passData(JudgmentDetailsPaneFxController.class, ShareInformationPaneFxController.class,
				         "crimes", "judgmentDate");
				renderUiAndWaitForUserInput(ShareInformationPaneFxController.class);
				break;
			}
			case 10:
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
				
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprintBase64Images");
					passData(FetchingFingerprintsWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprints");
					passData(ShowingFingerprintsPaneFxController.class,
					         ReviewAndSubmitPaneFxController.class,
					         "missingFingerprints");
					setData(ReviewAndSubmitPaneFxController.class, "fingerprintsSourceSystem",
					        ConvictedReport.FingerprintsSource.ARCHIVE_DB);
				}
				else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprintBase64Images");
					passData(RetrieveFingerprintsByCivilBiometricIdWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprints");
					passData(ShowingFingerprintsPaneFxController.class,
					         ReviewAndSubmitPaneFxController.class,
					         "missingFingerprints");
					setData(ReviewAndSubmitPaneFxController.class, "fingerprintsSourceSystem",
					        ConvictedReport.FingerprintsSource.ARCHIVE_DB);
				}
				else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprintBase64Images");
					passData(RetrieveFingerprintsByCriminalBiometricIdWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprints");
					passData(ShowingFingerprintsPaneFxController.class,
					         ReviewAndSubmitPaneFxController.class,
					         "missingFingerprints");
					setData(ReviewAndSubmitPaneFxController.class, "fingerprintsSourceSystem",
					        ConvictedReport.FingerprintsSource.BIOMETRICS_DB);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         ReviewAndSubmitPaneFxController.class,
					         "fingerprintBase64Images");
					passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
					         "fingerprintWsqImages", ReviewAndSubmitPaneFxController.class,
					         "fingerprints", new FingerprintsWsqToFingerConverter());
					passData(ShowingFingerprintsPaneFxController.class,
					         ReviewAndSubmitPaneFxController.class, "missingFingerprints");
					setData(ReviewAndSubmitPaneFxController.class, "fingerprintsSourceSystem",
					        ConvictedReport.FingerprintsSource.CARD_SCAN);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ReviewAndSubmitPaneFxController.class, "fingerprintBase64Images");
					passData(ExtractingDataFromNistFileWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
					         "fingerprints");
					passData(ShowingFingerprintsPaneFxController.class, ReviewAndSubmitPaneFxController.class,
					         "missingFingerprints");
					setData(ReviewAndSubmitPaneFxController.class, "fingerprintsSourceSystem",
					        ConvictedReport.FingerprintsSource.NIST_FILE);
				}
				
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
				
				break;
			}
			case 11:
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
					
					Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
					                                    "fingerprintsSource");
					
					if(fingerprintsSource == Source.ENTERING_PERSON_ID)
					{
						passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
						         "combinedFingerprints",
						         SubmitCriminalFingerprintsWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitCriminalFingerprintsWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
					{
						passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
						         "combinedFingerprints",
						         SubmitCriminalFingerprintsWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitCriminalFingerprintsWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
					{
						passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
						         "combinedFingerprints",
						         SubmitCriminalFingerprintsWorkflowTask.class,
						         "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitCriminalFingerprintsWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
					{
						passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
						         "fingerprintWsqImages", SubmitCriminalFingerprintsWorkflowTask.class,
						         "fingerprints", new FingerprintsWsqToFingerConverter());
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitCriminalFingerprintsWorkflowTask.class, "missingFingerprints");
					}
					else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
					{
						passData(ExtractingDataFromNistFileWorkflowTask.class,
						         SubmitCriminalFingerprintsWorkflowTask.class, "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitCriminalFingerprintsWorkflowTask.class, "missingFingerprints");
					}
					
					setData(SubmitCriminalFingerprintsWorkflowTask.class, "criminalWorkflowSource", CriminalWorkflowSource.CRIMINAL_NOT_PRESENT);
					passData(getClass(), FIELD_FINGERPRINT_SOURCE, SubmitCriminalFingerprintsWorkflowTask.class, "criminalFingerprintSource");
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
					setData(SubmitConvictedReportWorkflowTask.class, "criminalWorkflowSource", CriminalWorkflowSource.CRIMINAL_NOT_PRESENT);
					passData(getClass(), FIELD_FINGERPRINT_SOURCE, SubmitConvictedReportWorkflowTask.class, "criminalFingerprintSource");
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
			case 12:
			{
				passData(ReviewAndSubmitPaneFxController.class, ShowReportPaneFxController.class,
				         "convictedReport");
				passData(SubmitConvictedReportWorkflowTask.class, ShowReportPaneFxController.class,
				         "convictedReportResponse");
				
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowReportPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowReportPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.ENTERING_CRIMINAL_BIOMETRICS_ID)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowReportPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         ShowReportPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowReportPaneFxController.class,
					         "fingerprintBase64Images");
				}
				
				renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
				break;
			}
		}
	}
}