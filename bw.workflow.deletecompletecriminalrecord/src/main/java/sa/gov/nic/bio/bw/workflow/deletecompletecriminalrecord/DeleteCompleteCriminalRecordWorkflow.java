package sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord;

import sa.gov.nic.bio.bw.core.utils.AppConstants;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.DisCriminalReport;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryBySearchCriteriaWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryFromDisWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ResetWorkflowStepWorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers.EnterCriminalBiometricsIdPaneFxController;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers.ShowReportsPaneFxController;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.controllers.ShowResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.tasks.DeleteCompleteCriminalRecordWorkflowTask;
import sa.gov.nic.bio.bw.workflow.deletecompletecriminalrecord.utils.DeleteCompleteCriminalRecordErrorCodes;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1014, menuId = "menu.cancel.deleteCompleteCriminalRecord",
				menuTitle = "menu.title", menuOrder = 4, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
@Wizard({@Step(iconId = "search", title = "wizard.enterCriminalBiometricsId"),
		 @Step(iconId = "th_list", title = "wizard.showReports"),
		 @Step(iconId = "chain_broken", title = "wizard.result")})
public class DeleteCompleteCriminalRecordWorkflow extends WizardWorkflowBase
{
	@Override
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(EnterCriminalBiometricsIdPaneFxController.class);
				passData(EnterCriminalBiometricsIdPaneFxController.class,
				         ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
				         "criminalBiometricsId");
				setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class, "recordsPerPage",
				        AppConstants.TABLE_PAGINATION_RECORDS_PER_PAGE);
				setData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class, "pageIndex", 0);
				executeWorkflowTask(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class);
				
				Integer resultsTotalCount = getData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
				                                    "resultsTotalCount");
				if(resultsTotalCount == null || resultsTotalCount == 0)
				{
					// search in DIS
					passData(EnterCriminalBiometricsIdPaneFxController.class,
					         ConvictedReportInquiryFromDisWorkflowTask.class, "criminalBiometricsId");
					setData(ConvictedReportInquiryFromDisWorkflowTask.class,
					        "returnNullResultInCaseNotFound", Boolean.TRUE);
					executeWorkflowTask(ConvictedReportInquiryFromDisWorkflowTask.class);
					
					List<DisCriminalReport> disCriminalReports = getData(
									ConvictedReportInquiryFromDisWorkflowTask.class, "disCriminalReports");
					
					if(disCriminalReports == null) // criminalBiometricsId doesn't exist
					{
						setData(ResetWorkflowStepWorkflowTask.class, "taskResponse",
						        TaskResponse.failure(DeleteCompleteCriminalRecordErrorCodes.N015_00001.getCode()));
						executeWorkflowTask(ResetWorkflowStepWorkflowTask.class);
					}
				}
				
				break;
			}
			case 1:
			{
				passData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class, ShowReportsPaneFxController.class,
				         "convictedReports");
				passData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class, ShowReportsPaneFxController.class,
				         "resultsTotalCount");
				passData(EnterCriminalBiometricsIdPaneFxController.class, ShowReportsPaneFxController.class,
				         "criminalBiometricsId");
				renderUiAndWaitForUserInput(ShowReportsPaneFxController.class);
				
				Boolean deletionSubmission = getData(ShowReportsPaneFxController.class,
				                                     "deletionSubmission");
				
				if(deletionSubmission != null && deletionSubmission)
				{
					passData(EnterCriminalBiometricsIdPaneFxController.class,
					         DeleteCompleteCriminalRecordWorkflowTask.class,
					         "criminalBiometricsId");
					executeWorkflowTask(DeleteCompleteCriminalRecordWorkflowTask.class);
				}
				else
				{
					passData(ShowReportsPaneFxController.class,
					         ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
					         "recordsPerPage", "pageIndex");
					passData(EnterCriminalBiometricsIdPaneFxController.class,
					         ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
					         "criminalBiometricsId");
					
					executeWorkflowTask(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class);
					
					passData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
					         ShowReportsPaneFxController.class, "convictedReports");
					passData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
					         ShowReportsPaneFxController.class, "resultsTotalCount");
				}
				
				break;
			}
			case 2:
			{
				passData(EnterCriminalBiometricsIdPaneFxController.class, ShowResultPaneFxController.class,
				         "criminalBiometricsId");
				renderUiAndWaitForUserInput(ShowResultPaneFxController.class);
				
				break;
			}
		}
	}
}