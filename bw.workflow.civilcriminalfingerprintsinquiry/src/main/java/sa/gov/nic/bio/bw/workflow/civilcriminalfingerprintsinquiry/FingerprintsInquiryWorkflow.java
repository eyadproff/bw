package sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.FingerprintsSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.UploadNistFileFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.ExtractingDataFromNistFileWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FingerprintCapturingFxController;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AssociatedMenu(workflowId = 1012, menuId = "menu.query.civilcriminalfingerprintsinquiry", menuTitle = "menu.title",
				menuOrder = 6, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class,
			  BiometricsExchangeCrimeTypesLookup.class, BiometricsExchangePartiesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
		@Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		@Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.showInquiryResult")})
public class FingerprintsInquiryWorkflow extends WizardWorkflowBase
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
				setData(FingerprintsSourceFxController.class, "showLiveScanOption", Boolean.TRUE);
				renderUiAndWaitForUserInput(FingerprintsSourceFxController.class);
				break;
			}
			case 1:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
		
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
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
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
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
				else if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
				{
					incrementNSteps(1); // to skip step #2 on going next
					
					setData(FingerprintCapturingFxController.class, "acceptBadQualityFingerprint",
					        Boolean.TRUE);
					setData(FingerprintCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
					        0);
					renderUiAndWaitForUserInput(FingerprintCapturingFxController.class);
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
					passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
				}
				else if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
				{
					incrementNSteps(-1); // to skip step #2 on going previous
					passData(FingerprintCapturingFxController.class,
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
						passData(FetchingMissingFingerprintsWorkflowTask.class, FingerprintInquiryWorkflowTask.class,
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
						passData(SpecifyFingerprintCoordinatesPaneFxController.class,
						         FingerprintInquiryWorkflowTask.class, "missingFingerprints");
					}
					else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
					{
						passData(ExtractingDataFromNistFileWorkflowTask.class, FingerprintInquiryWorkflowTask.class,
						         "fingerprints");
						passData(ExtractingDataFromNistFileWorkflowTask.class, FingerprintInquiryWorkflowTask.class,
						         "missingFingerprints");
					}
					else if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
					{
						passData(FingerprintCapturingFxController.class, "slapFingerprints",
						         FingerprintInquiryWorkflowTask.class, "fingerprints");
						passData(FingerprintCapturingFxController.class, FingerprintInquiryWorkflowTask.class,
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
				setData(InquiryByFingerprintsResultPaneFxController.class, "hideRegisterUnknownButton",
				        Boolean.TRUE);
				setData(InquiryByFingerprintsResultPaneFxController.class, "hideConfirmationButton",
				        Boolean.TRUE);
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsResultPaneFxController.class,
				         "status", "civilBiometricsId", "criminalBiometricsId");
				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				
				break;
			}
		}
	}
}