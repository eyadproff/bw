package sa.gov.nic.bio.bw.workflow.editconvictedreport;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByReportNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers.EnterReportNumberPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.EditJudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.EditPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.EditPunishmentDetailsPaneFxController;

@AssociatedMenu(workflowId = 1016, menuId = "menu.edit.editConvictedReport",
		menuTitle = "menu.title", menuOrder = 1, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "search", title = "wizard.enterReportNumber"),
		@Step(iconId = "user", title = "wizard.editPersonInfo"),
		@Step(iconId = "gavel", title = "wizard.editJudgementDetails"),
		@Step(iconId = "university", title = "wizard.editPunishmentDetails"),
		@Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
		@Step(iconId = "chain_broken", title = "wizard.result")})
public class EditConvictedReportWorkflow extends WizardWorkflowBase
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
				break;
			}
			case 1:
			{
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				NormalizedPersonInfo normalizedPersonInfo = new NormalizedPersonInfo(
												new ConvictedReportToPersonInfoConverter().convert(convictedReport));
				setData(EditPersonInfoPaneFxController.class, "normalizedPersonInfo", normalizedPersonInfo);
		        renderUiAndWaitForUserInput(EditPersonInfoPaneFxController.class);
		        
				break;
			}
			case 2:
			{
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				setData(EditJudgmentDetailsPaneFxController.class, "judgementInfo",
				        convictedReport.getSubjJudgementInfo());
				renderUiAndWaitForUserInput(EditJudgmentDetailsPaneFxController.class);
				
				break;
			}
			case 3:
			{
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				setData(EditPunishmentDetailsPaneFxController.class, "judgementInfo",
				        convictedReport.getSubjJudgementInfo());
				renderUiAndWaitForUserInput(EditPunishmentDetailsPaneFxController.class);
				
				break;
			}
			case 4:
			{
				
				
				break;
			}
			case 5:
			{
				break;
			}
		}
	}
}