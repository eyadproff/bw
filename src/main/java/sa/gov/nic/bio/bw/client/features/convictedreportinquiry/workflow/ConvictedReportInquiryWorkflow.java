package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.workflow;

import javafx.util.Pair;
import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.SinglePageWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.workflow.ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.ConvictedReportInquiryPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CrimeTypesLookup.class})
public class ConvictedReportInquiryWorkflow extends SinglePageWorkflowBase
{
	public ConvictedReportInquiryWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                      BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public boolean onStep() throws InterruptedException, Signal
	{
		renderUi(ConvictedReportInquiryPaneFxController.class);
		waitForUserInput();
		
		passData(ConvictedReportInquiryPaneFxController.class, ConvictedReportInquiryWorkflowTask.class,
		         "generalFileNumber");
		
		executeTask(ConvictedReportInquiryWorkflowTask.class);
		
		List<ConvictedReport> convictedReports = getData(ConvictedReportInquiryWorkflowTask.class,
		                                                 "convictedReports");
		
		List<Pair<ConvictedReport, Map<Integer, String>>> convictedReportPairs = new ArrayList<>();
		
		for(ConvictedReport convictedReport : convictedReports)
		{
			setData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
			        "fingerprints", convictedReport.getSubjFingers());
			setData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
			        "missingFingerprints", convictedReport.getSubjMissingFingers());
			
			executeTask(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class);
			
			Map<Integer, String> fingerprintImages =
										getData(ConvertWsqFingerprintsToSegmentedFingerprintImagesWorkflowTask.class,
	                                            "fingerprintImages");
			
			convictedReportPairs.add(new Pair<>(convictedReport, fingerprintImages));
		}
		
		setData(ConvictedReportInquiryPaneFxController.class, "convictedReports", convictedReportPairs);
		
		return true;
	}
}