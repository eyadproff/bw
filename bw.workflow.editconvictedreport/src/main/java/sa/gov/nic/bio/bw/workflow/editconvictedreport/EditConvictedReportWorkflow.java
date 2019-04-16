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
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByReportNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportToPersonInfoConverter;
import sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers.EnterReportNumberPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.EditJudgmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.EditPersonInfoPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.EditPunishmentDetailsPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.ReviewAndSubmitPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers.ShowResultPaneFxController;
import sa.gov.nic.bio.bw.workflow.editconvictedreport.tasks.EditFullConvictedReportWorkflowTask;

@AssociatedMenu(workflowId = 1016, menuId = "menu.edit.editConvictedReport", menuTitle = "menu.title", menuOrder = 1,
				devices = Device.BIO_UTILITIES)
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
				passData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				         EnterReportNumberPaneFxController.class, "convictedReport");
				
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				if(convictedReport != null && ConvictedReport.Status.ACTIVE.equals(convictedReport.getStatus()))
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
				setData(EditJudgmentDetailsPaneFxController.class, "oldCrimes",
				        convictedReport.getCrimeCodes());
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
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				setData(ReviewAndSubmitPaneFxController.class, "oldReportNumber",
				        convictedReport.getReportNumber());
				setData(ReviewAndSubmitPaneFxController.class, "oldEnrollerId",
				        convictedReport.getOperatorId());
				setData(ReviewAndSubmitPaneFxController.class, "oldEnrollmentTime",
				        convictedReport.getReportDate());
				setData(ReviewAndSubmitPaneFxController.class, "facePhotoBase64",
				        convictedReport.getSubjFace());
				setData(ReviewAndSubmitPaneFxController.class, "civilBiometricsId",
				        convictedReport.getSubjBioId());
				setData(ReviewAndSubmitPaneFxController.class, "criminalBiometricsId",
				        convictedReport.getGeneralFileNumber());
				passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
				         ReviewAndSubmitPaneFxController.class, "fingerprintBase64Images");
				passData(EditPersonInfoPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "firstNameOldValue", "firstNameNewValue", "fatherNameOldValue",
				         "fatherNameNewValue", "grandfatherNameOldValue", "grandfatherNameNewValue",
				         "familyNameOldValue", "familyNameNewValue", "genderOldValue", "genderNewValue",
				         "nationalityOldValue", "nationalityNewValue", "occupationOldValue", "occupationNewValue",
				         "birthPlaceOldValue", "birthPlaceNewValue", "birthDateOldValue", "birthDateNewValue",
				         "personIdOldValue", "personIdNewValue", "personTypeOldValue", "personTypeNewValue",
				         "documentIdOldValue", "documentIdNewValue", "documentTypeOldValue", "documentTypeNewValue",
				         "documentIssuanceDateOldValue", "documentIssuanceDateNewValue", "documentExpiryDateOldValue",
				         "documentExpiryDateNewValue");
				passData(EditJudgmentDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "judgmentIssuerOldValue", "judgmentIssuerNewValue", "judgmentNumberOldValue",
				         "judgmentNumberNewValue", "judgmentDateOldValue", "judgmentDateNewValue",
				         "caseFileNumberOldValue", "caseFileNumberNewValue", "prisonerNumberOldValue",
				         "prisonerNumberNewValue", "arrestDateOldValue", "arrestDateNewValue", "oldCrimes",
				         "newCrimes");
				passData(EditPunishmentDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class,
				         "tazeerLashesOldValue", "tazeerLashesNewValue", "hadLashesOldValue",
				         "hadLashesNewValue", "fineOldValue", "fineNewValue", "jailYearsOldValue", "jailYearsNewValue",
				         "jailMonthsOldValue", "jailMonthsNewValue", "jailDaysOldValue", "jailDaysNewValue",
				         "travelBanYearsOldValue", "travelBanYearsNewValue", "travelBanMonthsOldValue",
				         "travelBanMonthsNewValue", "travelBanDaysOldValue", "travelBanDaysNewValue",
				         "exilingYearsOldValue", "exilingYearsNewValue", "exilingMonthsOldValue",
				         "exilingMonthsNewValue", "exilingDaysOldValue", "exilingDaysNewValue",
				         "deportationYearsOldValue", "deportationYearsNewValue", "deportationMonthsOldValue",
				         "deportationMonthsNewValue", "deportationDaysOldValue", "deportationDaysNewValue",
				         "finalDeportationOldValue", "finalDeportationNewValue", "libelOldValue", "libelNewValue",
				         "covenantOldValue", "covenantNewValue", "otherOldValue", "otherNewValue");
				renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
				
				passData(ReviewAndSubmitPaneFxController.class, EditFullConvictedReportWorkflowTask.class,
				         "convictedReport");
				executeWorkflowTask(EditFullConvictedReportWorkflowTask.class);
				
				break;
			}
			case 5:
			{
				ConvictedReport convictedReport = getData(ConvictedReportInquiryByReportNumberWorkflowTask.class,
				                                          "convictedReport");
				setData(ShowResultPaneFxController.class, "oldReportNumber",
				        convictedReport.getReportNumber());
				passData(EditFullConvictedReportWorkflowTask.class, ShowResultPaneFxController.class,
				         "oldReportNumber");
				passData(EditFullConvictedReportWorkflowTask.class, ShowResultPaneFxController.class,
				         "newReportNumber");
				renderUiAndWaitForUserInput(ShowResultPaneFxController.class);
				break;
			}
		}
	}
}