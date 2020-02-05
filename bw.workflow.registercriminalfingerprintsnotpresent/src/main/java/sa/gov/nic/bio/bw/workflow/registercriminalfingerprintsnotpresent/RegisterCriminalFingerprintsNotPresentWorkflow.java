package sa.gov.nic.bio.bw.workflow.registercriminalfingerprintsnotpresent;

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
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingMissingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintsWsqToFingerConverter;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalWorkflowSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.CriminalFingerprintsStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.GenerateNewCriminalBiometricsIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.SubmitCriminalFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.RegisteringFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.RegisteringFingerprintsPaneFxController.Request;

@AssociatedMenu(workflowId = 1019, menuId = "menu.register.registerCriminalFingerprintsNotPresent",
				menuTitle = "menu.title", menuOrder = 5, devices = {Device.FINGERPRINT_SCANNER})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
		@Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		@Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult"),
		@Step(iconId = "save", title = "wizard.registerFingerprints")})
public class RegisterCriminalFingerprintsNotPresentWorkflow extends WizardWorkflowBase
{
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
				break;
			}
			case 5:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsResultPaneFxController.class, "criminalBiometricsId");
				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				break;
			}
			case 6:
			{
				renderUiAndWaitForUserInput(RegisteringFingerprintsPaneFxController.class);
				
				Request request = getData(RegisteringFingerprintsPaneFxController.class, "request");
				if(request == Request.GENERATE_NEW_CRIMINAL_BIOMETRICS_ID)
				{
					executeWorkflowTask(GenerateNewCriminalBiometricsIdWorkflowTask.class);
					passData(GenerateNewCriminalBiometricsIdWorkflowTask.class,
					         RegisteringFingerprintsPaneFxController.class, "criminalBiometricsId");
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
					         RegisteringFingerprintsPaneFxController.class,
					         "criminalFingerprintsRegistrationStatus");
				}
				
				break;
			}
		}
	}
}