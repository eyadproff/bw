package sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangeCrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangePartiesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.CriminalFingerprintsStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.GenerateNewCriminalBiometricsIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.SubmitCriminalFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.RegisteringFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.RegisteringFingerprintsPaneFxController.Request;

@AssociatedMenu(workflowId = 1018, menuId = "menu.register.registerCriminalFingerprintsPresent",
				menuTitle = "menu.title", menuOrder = 4, devices = {Device.FINGERPRINT_SCANNER, Device.CAMERA})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class,
			  BiometricsExchangeCrimeTypesLookup.class, BiometricsExchangePartiesLookup.class})
@Wizard({@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult"),
		@Step(iconId = "save", title = "wizard.registerFingerprints")})
public class RegisterCriminalFingerprintsPresentWorkflow extends WizardWorkflowBase
{
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
				break;
			}
			case 2:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsResultPaneFxController.class, "criminalBiometricsId");
				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				break;
			}
			case 3:
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
					passData(FingerprintCapturingFxController.class, "combinedFingerprints",
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
					         RegisteringFingerprintsPaneFxController.class,
					         "criminalFingerprintsRegistrationStatus");
				}
				
				break;
			}
		}
	}
}