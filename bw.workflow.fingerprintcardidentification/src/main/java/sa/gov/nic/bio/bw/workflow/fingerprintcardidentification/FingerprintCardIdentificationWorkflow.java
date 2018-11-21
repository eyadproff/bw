package sa.gov.nic.bio.bw.workflow.fingerprintcardidentification;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintBase64ImagesToWsqWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertFingerprintImagesToBase64WorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintsWsqToFingerConverter;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers.InquiryResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.fingerprintcardidentification.controllers.SpecifyFingerprintCoordinatesPaneFxController;

import java.util.Locale;
import java.util.ResourceBundle;

@AssociatedMenu(workflowId = 1005, menuId = "menu.query.fingerprintCardIdentification", menuTitle = "menu.title", menuOrder = 5,
				devices = Device.BIO_UTILITIES)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "file", title = "wizard.scanFingerprintCard"),
		@Step(iconId = "\\uf247", title = "wizard.specifyFingerprintCoordinates"),
		@Step(iconId = "scissors", title = "wizard.fingerprintsAfterCropping"),
		@Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
		@Step(iconId = "database", title = "wizard.inquiryResult")})
public class FingerprintCardIdentificationWorkflow extends WizardWorkflowBase
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
	public void onStep(int step) throws InterruptedException, Signal
	{
		switch(step)
		{
			case 0:
			{
				setData(ScanFingerprintCardPaneFxController.class, "hidePreviousButton", Boolean.TRUE);
				renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
				break;
			}
			case 1:
			{
				passData(ScanFingerprintCardPaneFxController.class, SpecifyFingerprintCoordinatesPaneFxController.class,
				         "cardImage");
				
				renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
				break;
			}
			case 2:
			{
				passData(SpecifyFingerprintCoordinatesPaneFxController.class,
				         ShowingFingerprintsPaneFxController.class,
				         "fingerprintBase64Images");
				
				renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
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
				
				passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
				         "inquiryId");
				executeTask(FingerprintInquiryStatusCheckerWorkflowTask.class);
				
				break;
			}
			case 4:
			{
				passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryResultPaneFxController.class,
				         "status", "personId", "civilBiometricsId", "criminalBiometricsId",
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