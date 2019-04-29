package sa.gov.nic.bio.bw.workflow.deleteconvictedreport;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport.Status;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByReportNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers.EnterReportNumberPaneFxController;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers.ShowReportPaneFxController;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers.ShowResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.tasks.DeleteConvictedReportWorkflowTask;

@AssociatedMenu(workflowId = 1013, menuId = "menu.cancel.deleteConvictedReport",
				menuTitle = "menu.title", menuOrder = 3, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "search", title = "wizard.enterReportNumber"),
		 @Step(iconId = "th_list", title = "wizard.showReport"),
		 @Step(iconId = "chain_broken", title = "wizard.result")})
public class DeleteConvictedReportWorkflow extends WizardWorkflowBase
{
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(EnterReportNumberPaneFxController.class);
				passData(EnterReportNumberPaneFxController.class,
				         ConvictedReportInquiryByReportNumberWorkflowTask.class,
				         "reportNumber");
				executeWorkflowTask(ConvictedReportInquiryByReportNumberWorkflowTask.class);
				passData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				         EnterReportNumberPaneFxController.class, "convictedReport");
				
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				
				if(convictedReport.getStatus().equals(Status.ACTIVE))
				{
					setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					        "fingerprints", convictedReport.getSubjFingers());
					setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					        "missingFingerprints", convictedReport.getSubjMissingFingers());
					executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
				}
				
				break;
			}
			case 1:
			{
				passData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				         ShowReportPaneFxController.class,
				         "convictedReport");
				passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				         ShowReportPaneFxController.class,
				         "fingerprintBase64Images");
				renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
				
				passData(EnterReportNumberPaneFxController.class, DeleteConvictedReportWorkflowTask.class,
				         "reportNumber");
				executeWorkflowTask(DeleteConvictedReportWorkflowTask.class);
				
				break;
			}
			case 2:
			{
				passData(EnterReportNumberPaneFxController.class, ShowResultPaneFxController.class,
				         "reportNumber");
				renderUiAndWaitForUserInput(ShowResultPaneFxController.class);
				
				break;
			}
		}
	}
}