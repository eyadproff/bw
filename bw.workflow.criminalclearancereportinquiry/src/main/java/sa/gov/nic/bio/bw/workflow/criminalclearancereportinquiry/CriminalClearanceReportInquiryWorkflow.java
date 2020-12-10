package sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry;


import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.beans.CriminalClearanceReport;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.controllers.CriminalClearanceReportInquiryPaneFxController;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.tasks.RetrieveCriminalClearanceReportByReportNumber;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.tasks.RetrieveCriminalClearanceReportBySamisId;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportinquiry.controllers.CriminalClearanceReportInquiryPaneFxController.ServiceType;

import java.util.ArrayList;
import java.util.List;

@AssociatedMenu(workflowId = 1033, menuId = "menu.query.criminalclearancereportinquiry",
                menuTitle = "menu.title", menuOrder = 15, devices = {Device.BIO_UTILITIES})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})

public class CriminalClearanceReportInquiryWorkflow extends WizardWorkflowBase {

    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        renderUiAndWaitForUserInput(CriminalClearanceReportInquiryPaneFxController.class);

        ServiceType serviceType = getData(CriminalClearanceReportInquiryPaneFxController.class, "serviceType");
        if (serviceType == ServiceType.BY_ID_NUMBER) {

            passData(CriminalClearanceReportInquiryPaneFxController.class, RetrieveCriminalClearanceReportBySamisId.class, "personId");
            executeWorkflowTask(RetrieveCriminalClearanceReportBySamisId.class);
            passData(RetrieveCriminalClearanceReportBySamisId.class, CriminalClearanceReportInquiryPaneFxController.class, "criminalClearanceReports");
        }
        else {

            passData(CriminalClearanceReportInquiryPaneFxController.class, RetrieveCriminalClearanceReportByReportNumber.class, "reportNumber");
            executeWorkflowTask(RetrieveCriminalClearanceReportByReportNumber.class);

            CriminalClearanceReport criminalClearanceReport = getData(RetrieveCriminalClearanceReportByReportNumber.class, "criminalClearanceReport");
            List<CriminalClearanceReport> criminalClearanceReports = new ArrayList<>();
            criminalClearanceReports.add(criminalClearanceReport);

            setData(CriminalClearanceReportInquiryPaneFxController.class, "criminalClearanceReports", criminalClearanceReports);

        }

    }
}
