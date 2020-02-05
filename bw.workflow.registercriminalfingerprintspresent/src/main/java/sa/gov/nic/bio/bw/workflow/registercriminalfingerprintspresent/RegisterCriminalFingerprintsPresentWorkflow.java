package sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalFingerprintSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CriminalWorkflowSource;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers.PalmCapturingFxController;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.CriminalFingerprintsStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.GenerateNewCriminalBiometricsIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.SubmitCriminalFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.InquiryByFingerprintsResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.RegisteringFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.registercriminalfingerprintspresent.controllers.RegisteringFingerprintsPaneFxController.Request;

@AssociatedMenu(workflowId = 1018, menuId = "menu.register.registerCriminalFingerprintsPresent",
				menuTitle = "menu.title", menuOrder = 4, devices = {Device.FINGERPRINT_SCANNER})
@Wizard({@Step(iconId = "\\uf256", title = "wizard.fingerprintCapturing"),
		@Step(iconId = "\\uf255", title = "wizard.palmCapturing"),
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
				
				setData(SlapFingerprintsCapturingFxController.class, "hidePreviousButton", Boolean.TRUE);
				setData(SlapFingerprintsCapturingFxController.class, "allow9MissingWithNoRole", Boolean.TRUE);
				setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
				        acceptBadQualityFingerprint);
				setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
				        acceptBadQualityFingerprintMinRetries);
		
				renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);
				break;
			}
			case 1:
			{
				renderUiAndWaitForUserInput(PalmCapturingFxController.class);
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
					passData(SlapFingerprintsCapturingFxController.class, "slapFingerprints",
					         FingerprintInquiryWorkflowTask.class, "fingerprints");
					passData(SlapFingerprintsCapturingFxController.class, FingerprintInquiryWorkflowTask.class,
					         "missingFingerprints");
					
					executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
				}
				
				passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				break;
			}
			case 3:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsResultPaneFxController.class, "criminalBiometricsId");
				renderUiAndWaitForUserInput(InquiryByFingerprintsResultPaneFxController.class);
				break;
			}
			case 4:
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
					passData(SlapFingerprintsCapturingFxController.class, "combinedFingerprints",
					         SubmitCriminalFingerprintsWorkflowTask.class, "fingerprints");
					passData(PalmCapturingFxController.class, SubmitCriminalFingerprintsWorkflowTask.class,
					         "palms");
					passData(SlapFingerprintsCapturingFxController.class, SubmitCriminalFingerprintsWorkflowTask.class,
					         "missingFingerprints");
					setData(SubmitCriminalFingerprintsWorkflowTask.class, "criminalWorkflowSource", CriminalWorkflowSource.CRIMINAL_PRESENT);
					setData(SubmitCriminalFingerprintsWorkflowTask.class, "criminalFingerprintSource", CriminalFingerprintSource.LIVE_CAPTURE);
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