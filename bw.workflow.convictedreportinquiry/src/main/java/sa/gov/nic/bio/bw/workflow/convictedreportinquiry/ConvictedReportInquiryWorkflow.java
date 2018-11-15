package sa.gov.nic.bio.bw.workflow.convictedreportinquiry;

import javafx.util.Pair;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.convictedreportinquiry.controllers.ConvictedReportInquiryPaneFxController;
import sa.gov.nic.bio.bw.workflow.convictedreportinquiry.tasks.ConvictedReportInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.webservice.ConvictedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@AssociatedMenu(id = "menu.query.convictedReportInquiry", title = "menu.title", order = 6,
				devices = Device.BIO_UTILITIES)
@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
public class ConvictedReportInquiryWorkflow extends SinglePageWorkflowBase
{
	@Override
	public ResourceBundle getStringsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.strings", locale);
	}
	
	@Override
	public ResourceBundle getErrorsResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle(getClass().getPackageName() + ".bundles.errors", locale);
	}
	
	@Override
	public void onStep() throws InterruptedException, Signal
	{
		renderUiAndWaitForUserInput(ConvictedReportInquiryPaneFxController.class);
		
		passData(ConvictedReportInquiryPaneFxController.class, ConvictedReportInquiryWorkflowTask.class,
		         "generalFileNumber");
		
		executeTask(ConvictedReportInquiryWorkflowTask.class);
		
		List<ConvictedReport> convictedReports = getData(ConvictedReportInquiryWorkflowTask.class,
		                                                 "convictedReports");
		
		List<Pair<ConvictedReport, Map<Integer, String>>> convictedReportPairs = new ArrayList<>();
		
		for(ConvictedReport convictedReport : convictedReports)
		{
			setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
			        "fingerprints", convictedReport.getSubjFingers());
			setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
			        "missingFingerprints", convictedReport.getSubjMissingFingers());
			
			executeTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);
			
			Map<Integer, String> fingerprintImages =
										getData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
										        "fingerprintImages");
			
			convictedReportPairs.add(new Pair<>(convictedReport, fingerprintImages));
		}
		
		setData(ConvictedReportInquiryPaneFxController.class, "convictedReports", convictedReportPairs);
	}
}