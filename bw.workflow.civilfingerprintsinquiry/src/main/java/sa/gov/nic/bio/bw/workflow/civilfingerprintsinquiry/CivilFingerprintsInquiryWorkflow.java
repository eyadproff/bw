package sa.gov.nic.bio.bw.workflow.civilfingerprintsinquiry;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.CivilBiometricsIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.FingerprintsSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.UploadNistFileFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.ExtractingDataFromNistFileWorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.RetrieveFingerprintsAvailabilityByCivilBiometricIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.RetrieveFingerprintsByCivilBiometricIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.DeporteeInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.DeporteeInfoToPersonInfoConverter;
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

@AssociatedMenu(workflowId = 1024, menuId = "menu.query.civilfingerprintsinquiry", menuTitle = "menu.title",
				menuOrder = 5, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
		@Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		@Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.showInquiryResult")})
public class CivilFingerprintsInquiryWorkflow extends WizardWorkflowBase
{
	private static final String FIELD_CIVIL_HIT = "CIVIL_HIT";
	private static final String FIELD_CIVIL_PERSON_INFO_MAP = "CIVIL_PERSON_INFO_MAP";
	
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				setData(FingerprintsSourceFxController.class, "showLiveScanOption", Boolean.TRUE);
				setData(FingerprintsSourceFxController.class, "hideCriminalBiometricsIdOption", Boolean.TRUE);
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
				else if(fingerprintsSource == Source.ENTERING_CIVIL_BIOMETRICS_ID)
				{
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
					
					setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
					        Boolean.TRUE);
					setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
					        0);
					renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);
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
				else if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
				{
					incrementNSteps(-1); // to skip step #2 on going previous
					passData(FaceCapturingFxController.class,
							ShowingFingerprintsPaneFxController.class,
							"facePhotoBase64");
					passData(SlapFingerprintsCapturingFxController.class,
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
					else if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
					{
						passData(SlapFingerprintsCapturingFxController.class, "slapFingerprints",
						         FingerprintInquiryWorkflowTask.class, "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class, FingerprintInquiryWorkflowTask.class,
						         "missingFingerprints");
					}
					
					executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
				}
				
				setData(FingerprintInquiryStatusCheckerWorkflowTask.class, "ignoreCriminalFingerprintsInquiryResult", Boolean.TRUE);
				passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				
				Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");
				if(status == Status.HIT)
				{
					Long civilBiometricsId = getData(FingerprintInquiryStatusCheckerWorkflowTask.class,
					                                 "civilBiometricsId");
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
				}
				
				break;
			}
			case 5:
			{
				passData(getClass(), FIELD_CIVIL_PERSON_INFO_MAP, InquiryByFingerprintsResultPaneFxController.class,
				         "civilPersonInfoMap");
				passData(FingerprintInquiryWorkflowTask.class, InquiryByFingerprintsResultPaneFxController.class,
						"inquiryId");
				setData(InquiryByFingerprintsResultPaneFxController.class, "hideRegisterUnknownButton",
				        Boolean.TRUE);
				setData(InquiryByFingerprintsResultPaneFxController.class, "hideConfirmationButton",
				        Boolean.TRUE);
				setData(InquiryByFingerprintsResultPaneFxController.class, "ignoreCriminalFingerprintsInquiryResult",
				        Boolean.TRUE);
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsResultPaneFxController.class, "status", "civilBiometricsId");

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
				else if(fingerprintsSource == Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)
				{
					passData(SlapFingerprintsCapturingFxController.class, InquiryByFingerprintsResultPaneFxController.class,
					         "fingerprintBase64Images");
				}
				
				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				
				break;
			}
		}
	}
}