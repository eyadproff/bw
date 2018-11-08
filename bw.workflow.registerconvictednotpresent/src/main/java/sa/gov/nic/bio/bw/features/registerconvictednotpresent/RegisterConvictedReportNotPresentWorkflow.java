package sa.gov.nic.bio.bw.features.registerconvictednotpresent;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.features.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.features.commons.controllers.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.features.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.features.commons.workflow.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.FetchingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.FetchingMissingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.features.commons.workflow.FingerprintsWsqToFingerConverter;
import sa.gov.nic.bio.bw.features.commons.workflow.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.features.fingerprintcardidentification.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.features.fingerprintcardidentification.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictednotpresent.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.features.registerconvictednotpresent.controllers.FingerprintsSourceFxController.Source;
import sa.gov.nic.bio.bw.features.registerconvictednotpresent.controllers.PersonIdPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers.JudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers.PersonInfoPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers.PunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.controllers.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.workflow.ConvictedReportResponse;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.workflow.GeneratingGeneralFileNumberService;
import sa.gov.nic.bio.bw.features.registerconvictedpresent.workflow.SubmittingConvictedReportService;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(id = "menu.register.registerConvictedNotPresent", title = "menu.title", order = 3,
				devices = Device.BIO_UTILITIES)
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
		@Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		@Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.showInquiryResult"),
		@Step(iconId = "user", title = "wizard.updatePersonInformation"),
		@Step(iconId = "gavel", title = "wizard.addJudgementDetails"),
		@Step(iconId = "university", title = "wizard.addPunishmentDetails"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class RegisterConvictedReportNotPresentWorkflow extends WizardWorkflowBase
{
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
					
					executeTask(GetPersonInfoByIdWorkflowTask.class);
					
					passData(PersonIdPaneFxController.class, FetchingFingerprintsWorkflowTask.class,
					         "personId");
					
					executeTask(FetchingFingerprintsWorkflowTask.class);
					
					passData(PersonIdPaneFxController.class, FetchingMissingFingerprintsWorkflowTask.class,
					         "personId");
					executeTask(FetchingMissingFingerprintsWorkflowTask.class);
					
					passData(FetchingFingerprintsWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "fingerprints");
					passData(FetchingMissingFingerprintsWorkflowTask.class,
					         ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					         "missingFingerprints");
					executeTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					//renderUiAndWaitForUserInput(???);
				}
				
				break;
			}
			case 2:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.ENTERING_PERSON_ID)
				{
					passData(PersonIdPaneFxController.class, InquiryResultPaneFxController.class,
					         "personId");
					passData(GetPersonInfoByIdWorkflowTask.class, InquiryResultPaneFxController.class,
					         "personInfo");
					
					renderUiAndWaitForUserInput(InquiryResultPaneFxController.class);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(ScanFingerprintCardPaneFxController.class,
					         SpecifyFingerprintCoordinatesPaneFxController.class, "cardImage");
					
					renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					//renderUiAndWaitForUserInput(???);
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
					
					renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         ShowingFingerprintsPaneFxController.class,
					         "fingerprintBase64Images");
					
					renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
				}
				else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					//renderUiAndWaitForUserInput(???);
				}
				
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
						executeTask(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class);
						
						passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
						         "fingerprintWsqImages", FingerprintInquiryWorkflowTask.class,
						         "fingerprints", new FingerprintsWsqToFingerConverter());
						passData(SpecifyFingerprintCoordinatesPaneFxController.class,
						         FingerprintInquiryWorkflowTask.class, "missingFingerprints");
					}
					else if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
					{
						//renderUiAndWaitForUserInput(???);
					}
					
					executeTask(FingerprintInquiryWorkflowTask.class);
				}
				
				passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				
				break;
			}
			case 5:
			{
				setData(InquiryResultPaneFxController.class, "hideRegisterUnknown",
				        Boolean.TRUE);
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryResultPaneFxController.class,
				         "status", "personId", "civilBiometricsId", "criminalBiometricsId",
				         "personInfo");
				
				renderUiAndWaitForUserInput(InquiryResultPaneFxController.class);
				
				break;
			}
			case 6:
			{
				renderUiAndWaitForUserInput(PersonInfoPaneFxController.class);
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
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
		
				ConvictedReport convictedReport = (ConvictedReport)
						uiInputData.get(ReviewAndSubmitPaneFxController.KEY_FINAL_CONVICTED_REPORT);
		
				Long generalFileNumber = (Long)
						uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
		
				if(generalFileNumber == null)
				{
					Long samisId = convictedReport.getSubjSamisId();
					Long civilHitBioId = convictedReport.getSubjBioId();
		
					TaskResponse<Long> taskResponse =
							GeneratingGeneralFileNumberService.execute(samisId, civilHitBioId);
		
					if(!taskResponse.isSuccess())
					{
						uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
						break;
					}
		
					generalFileNumber = taskResponse.getResult();
					uiInputData.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
					                generalFileNumber);
				}
		
				convictedReport.setGeneralFileNum(generalFileNumber);
		
				TaskResponse<ConvictedReportResponse> taskResponse =
															SubmittingConvictedReportService.execute(convictedReport);
				uiInputData.put(KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE, taskResponse);
				
				break;
			}
			case 10:
			{
				renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
				break;
			}
		}
	}
}