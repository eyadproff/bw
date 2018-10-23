package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import sa.gov.nic.bio.bw.client.core.interfaces.FormRenderer;
import sa.gov.nic.bio.bw.client.core.utils.Device;
import sa.gov.nic.bio.bw.client.core.wizard.Step;
import sa.gov.nic.bio.bw.client.core.wizard.Wizard;
import sa.gov.nic.bio.bw.client.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.client.core.workflow.Signal;
import sa.gov.nic.bio.bw.client.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.client.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.client.features.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.controllers.FingerprintsAfterCroppingPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.controllers.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow.ConvertFingerprintImagesToBase64WorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryStatusCheckerWorkflowTask.Status;
import sa.gov.nic.bio.bw.client.features.commons.workflow.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.workflow.FingerprintsWsqToFingerConverter;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@AssociatedMenu(id = "menu.query.fingerprintCardIdentification", title = "menu.title", order = 5,
				devices = Device.BIO_UTILITIES)
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
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				break;
			}
			case 1:
			{
				passData(ScanFingerprintCardPaneFxController.class, "cardImage",
				         SpecifyFingerprintCoordinatesPaneFxController.class, "cardImage");
				
				renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
				break;
			}
			case 2:
			{
				passData(SpecifyFingerprintCoordinatesPaneFxController.class,
				         FingerprintsAfterCroppingPaneFxController.class,
				         "fingerprintImages");
				
				renderUiAndWaitForUserInput(FingerprintsAfterCroppingPaneFxController.class);
				break;
			}
			case 3:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class,
				         InquiryByFingerprintsPaneFxController.class,
				         "status");
				
				renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);
				
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
				
				break;
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
				
				renderUiAndWaitForUserInput(InquiryResultPaneFxController.class);
				
				break;
			}
		}
	}
}