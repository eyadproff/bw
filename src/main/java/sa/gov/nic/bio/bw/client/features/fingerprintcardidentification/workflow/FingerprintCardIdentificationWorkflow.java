package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.WithLookups;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.FingerprintsAfterCroppingPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow.FingerprintInquiryStatusCheckerWorkflowTask.Status;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@WithLookups({SamisIdTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "file", title = "wizard.scanFingerprintCard"),
		@Step(iconId = "\\uf247", title = "wizard.specifyFingerprintCoordinates"),
		@Step(iconId = "scissors", title = "wizard.fingerprintsAfterCropping"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult")})
public class FingerprintCardIdentificationWorkflow extends WizardWorkflowBase
{
	public FingerprintCardIdentificationWorkflow(AtomicReference<FormRenderer> formRenderer,
	                                             BlockingQueue<Map<String, Object>> userTasks)
	{
		super(formRenderer, userTasks);
	}
	
	@Override
	public boolean onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUi(ScanFingerprintCardPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 1:
			{
				passData(ScanFingerprintCardPaneFxController.class, "cardImage",
				         SpecifyFingerprintCoordinatesPaneFxController.class, "cardImage");
				
				renderUi(SpecifyFingerprintCoordinatesPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 2:
			{
				passData(SpecifyFingerprintCoordinatesPaneFxController.class,
				         FingerprintsAfterCroppingPaneFxController.class,
				         "fingerprintImages");
				
				renderUi(FingerprintsAfterCroppingPaneFxController.class);
				waitForUserInput();
				return true;
			}
			case 3:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsPaneFxController.class,
				         "status");
				
				renderUi(InquiryByFingerprintsPaneFxController.class);
				waitForUserInput();
				
				Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");
				
				if(inquiryId == null)
				{
					passData(SpecifyFingerprintCoordinatesPaneFxController.class,
					         ConvertFingerprintImagesToBase64WorkflowTask.class,
					         "fingerprintImages");
					executeTask(ConvertFingerprintImagesToBase64WorkflowTask.class);
					
					passData(ConvertFingerprintImagesToBase64WorkflowTask.class,
					         ConvertFingerprintBase64ImagesToWsqWorkflowTask.class,
					         "fingerprintBase64Images");
					executeTask(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class);
					
					passData(ConvertFingerprintBase64ImagesToWsqWorkflowTask.class, "fingerprintWsqImages",
					         FingerprintInquiryWorkflowTask.class, "fingerprints",
					         new FingerprintsWsqToFingerConverter());
					passData(SpecifyFingerprintCoordinatesPaneFxController.class, FingerprintInquiryWorkflowTask.class,
					         "missingFingerprints");
					executeTask(FingerprintInquiryWorkflowTask.class);
				}
				
				passData(FingerprintInquiryWorkflowTask.class,
				         FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				
				Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");
				if(status == Status.HIT || status == Status.NOT_HIT)
									setData(FingerprintInquiryWorkflowTask.class, "inquiryId", null);
				
				return true;
			}
			case 4:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryResultPaneFxController.class,
				         "status");
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryResultPaneFxController.class,
				         "samisId");
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryResultPaneFxController.class,
				         "civilBioId");
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryResultPaneFxController.class,
				         "personInfo");
				passData(SpecifyFingerprintCoordinatesPaneFxController.class, InquiryResultPaneFxController.class,
				         "fingerprintImages");
				passData(ConvertFingerprintImagesToBase64WorkflowTask.class, InquiryResultPaneFxController.class,
				         "fingerprintBase64Images");
				
				renderUi(InquiryResultPaneFxController.class);
				waitForUserInput();
				
				return true;
			}
			default: return false;
		}
	}
}