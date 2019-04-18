package sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryBySearchCriteriaWorkflowTask;
import sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.controllers.ConvictedReportInquiryPaneFxController;

@AssociatedMenu(workflowId = 1011, menuId = "menu.query.convictedReportInquiryBySearchCriteria",
				menuTitle = "menu.title", menuOrder = 5, devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
public class ConvictedReportInquiryBySearchCriteriaWorkflow extends SinglePageWorkflowBase
{
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(ConvictedReportInquiryPaneFxController.class);
		
		passData(ConvictedReportInquiryPaneFxController.class, ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
		         "recordsPerPage", "reportNumber", "criminalBiometricsId", "location", "personId",
		         "documentId", "firstName", "fatherName", "grandfatherName", "familyName", "judgementNumber",
		         "prisonerNumber", "operatorId", "judgmentDateFrom", "judgmentDateTo", "showOldReports",
		         "showDeletedReports", "recordsPerPage", "pageIndex");
		
		executeWorkflowTask(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class);
		
		passData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
		         ConvictedReportInquiryPaneFxController.class, "convictedReports");
		passData(ConvictedReportInquiryBySearchCriteriaWorkflowTask.class,
		         ConvictedReportInquiryPaneFxController.class, "resultsTotalCount");
	}
}