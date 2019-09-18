package sa.gov.nic.bio.bw.workflow.irisinquiry;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;

@AssociatedMenu(workflowId = 1022, menuId = "menu.query.irisInquiry",
				menuTitle = "menu.title", menuOrder = 7, devices = {Device.FINGERPRINT_SCANNER,
				                                                    Device.IRIS_SCANNER, Device.CAMERA})
@Wizard({@Step(iconId = "eye", title = "wizard.irisCapturing"),
		@Step(iconId = "search", title = "wizard.inquiryByIris"),
		@Step(iconId = "database", title = "wizard.inquiryResult")})
public class IrisInquiryWorkflow extends WizardWorkflowBase
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
		}
	}
}