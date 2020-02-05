package sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.UploadNistFileFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.tasks.ExtractingDataFromNistFileWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintsWsqToFingerConverter;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.FingerprintsSourceFxController;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.FingerprintsSourceFxController.Source;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.MiscreantIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.RegisteringFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.RegisteringFingerprintsPaneFxController.Request;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.controllers.ShowingMiscreantInfoFxController;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.tasks.GetMiscreantInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.tasks.MiscreantFingerprintsStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.miscreantfingerprintsenrollment.tasks.SubmitMiscreantFingerprintsWorkflowTask;

@AssociatedMenu(workflowId = 1025, menuId = "menu.register.miscreantFingerprintsEnrollment",
				menuTitle = "menu.title", menuOrder = 8, devices = {Device.BIO_UTILITIES})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.InquiryByMiscreantId"),
		 @Step(iconId = "database", title = "wizard.inquiryResult"),
		 @Step(iconId = "question", title = "wizard.selectFingerprintsSource"),
		 @Step(iconId = "upload", title = "wizard.uploadNistFile"),
		 @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
		 @Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
		 @Step(iconId = "save", title = "wizard.registerFingerprints")})
public class MiscreantFingerprintsEnrollmentWorkflow extends WizardWorkflowBase
{
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(MiscreantIdPaneFxController.class);
				
				passData(MiscreantIdPaneFxController.class, GetMiscreantInfoByIdWorkflowTask.class,
				         "miscreantId");
				
				executeWorkflowTask(GetMiscreantInfoByIdWorkflowTask.class);
				
				break;
			}
			case 1:
			{
				renderUiAndWaitForUserInput(ShowingMiscreantInfoFxController.class);
				break;
			}
			case 2:
			{
				renderUiAndWaitForUserInput(FingerprintsSourceFxController.class);
				break;
			}
			case 3:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
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
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				}
				
				break;
			}
			case 4:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					passData(ExtractingDataFromNistFileWorkflowTask.class, ShowingPersonInfoFxController.class,
					         "personInfo");
					
					renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);
				}
				else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
				{
					passData(ScanFingerprintCardPaneFxController.class,
					         SpecifyFingerprintCoordinatesPaneFxController.class, "cardImage");
					
					renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
				}
				
				break;
			}
			case 5:
			{
				Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
				                                    "fingerprintsSource");
				
				if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
				{
					PersonInfo personInfo = getData(ExtractingDataFromNistFileWorkflowTask.class,
					                                "personInfo");
					if(personInfo != null) setData(ShowingFingerprintsPaneFxController.class,
					                               "facePhotoBase64", personInfo.getFace());
					
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
				
				renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
				
				break;
			}
			case 6:
			{
				renderUiAndWaitForUserInput(RegisteringFingerprintsPaneFxController.class);
				
				Request request = getData(RegisteringFingerprintsPaneFxController.class, "request");
				
				if(request == Request.SUBMIT_FINGERPRINTS)
				{
					passData(MiscreantIdPaneFxController.class, SubmitMiscreantFingerprintsWorkflowTask.class,
					         "miscreantId");
					
					Source fingerprintsSource = getData(FingerprintsSourceFxController.class, "fingerprintsSource");
					
					if(fingerprintsSource == Source.UPLOADING_NIST_FILE)
					{
						passData(ExtractingDataFromNistFileWorkflowTask.class,
						         SubmitMiscreantFingerprintsWorkflowTask.class, "fingerprints");
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitMiscreantFingerprintsWorkflowTask.class, "missingFingerprints");
					}
					else if(fingerprintsSource == Source.SCANNING_FINGERPRINTS_CARD)
					{
						passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
						         "fingerprintWsqImages", SubmitMiscreantFingerprintsWorkflowTask.class,
						         "fingerprints", new FingerprintsWsqToFingerConverter());
						passData(ShowingFingerprintsPaneFxController.class,
						         SubmitMiscreantFingerprintsWorkflowTask.class, "missingFingerprints");
					}
					
					executeWorkflowTask(SubmitMiscreantFingerprintsWorkflowTask.class);
				}
				else if(request == Request.CHECK_FINGERPRINTS)
				{
					passData(SubmitMiscreantFingerprintsWorkflowTask.class,
					         MiscreantFingerprintsStatusCheckerWorkflowTask.class, "tcn");
					executeWorkflowTask(MiscreantFingerprintsStatusCheckerWorkflowTask.class);
					
					passData(MiscreantFingerprintsStatusCheckerWorkflowTask.class, "status",
					         RegisteringFingerprintsPaneFxController.class,
					         "criminalFingerprintsRegistrationStatus");
				}
				
				break;
			}
		}
	}
}