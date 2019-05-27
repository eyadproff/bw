package sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.controllers.EnterCriminalBiometricsIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.controllers.ShowResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.controllers.ShowResultPaneFxController.Request;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.tasks.CriminalFingerprintsDeletionStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecriminalfingerprints.tasks.SubmitCriminalFingerprintsDeletionWorkflowTask;

@AssociatedMenu(workflowId = 1020, menuId = "menu.cancel.deleteCriminalFingerprints",
				menuTitle = "menu.title", menuOrder = 5, devices = Device.BIO_UTILITIES)
@Wizard({@Step(iconId = "search", title = "wizard.enterCriminalBiometricsId"),
		 @Step(iconId = "chain_broken", title = "wizard.result")})
public class DeleteCriminalFingerprintsWorkflow extends WizardWorkflowBase
{
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(EnterCriminalBiometricsIdPaneFxController.class);
				break;
			}
			case 1:
			{
				passData(EnterCriminalBiometricsIdPaneFxController.class, ShowResultPaneFxController.class,
				         "criminalBiometricsId");
				renderUiAndWaitForUserInput(ShowResultPaneFxController.class);
				
				Request request = getData(ShowResultPaneFxController.class, "request");
				if(request == Request.DELETE_CRIMINAL_FINGERPRINTS)
				{
					passData(EnterCriminalBiometricsIdPaneFxController.class,
					         SubmitCriminalFingerprintsDeletionWorkflowTask.class,
					         "criminalBiometricsId");
					executeWorkflowTask(SubmitCriminalFingerprintsDeletionWorkflowTask.class);
					passData(SubmitCriminalFingerprintsDeletionWorkflowTask.class,
					         ShowResultPaneFxController.class, "noFingerprintsFound");
				}
				else if(request == Request.CHECK_CRIMINAL_FINGERPRINTS_DELETION)
				{
					passData(SubmitCriminalFingerprintsDeletionWorkflowTask.class,
					         CriminalFingerprintsDeletionStatusCheckerWorkflowTask.class, "tcn");
					executeWorkflowTask(CriminalFingerprintsDeletionStatusCheckerWorkflowTask.class);
					passData(CriminalFingerprintsDeletionStatusCheckerWorkflowTask.class,
					         ShowResultPaneFxController.class, "noFingerprintsFound");
					passData(CriminalFingerprintsDeletionStatusCheckerWorkflowTask.class, "status",
					         ShowResultPaneFxController.class,
					         "criminalFingerprintsDeletionStatus");
				}
				
				break;
			}
		}
	}
}