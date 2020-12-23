package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport;


import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.WatchListRecord;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.CheckCWLByBioIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingMissingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPassportIdByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers.*;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers.ShowingFingerprintsQualityPaneFxController.ServiceType;
import sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.tasks.*;

import java.util.List;


@AssociatedMenu(workflowId = 1031, menuId = "menu.query.registercriminalclearancereport",
                menuTitle = "menu.title", menuOrder = 13, devices = {Device.FINGERPRINT_SCANNER})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
                @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
                @Step(iconId = "\\uf256", title = "wizard.showQualityFingerprintsView"),
                @Step(iconId = "search", title = "wizard.inquiryByFingerprintsInCriminal"),
                @Step(iconId = "\\uf022", title = "wizard.addCriminalClearanceDetails"),
                @Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
                @Step(iconId = "save", title = "wizard.registerReport")
        })
public class RegisterCriminalClearanceReportWorkflow extends WizardWorkflowBase {

    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0: {

                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
                passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class, "personId");
                executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);

                setData(GetPassportIdByIdWorkflowTask.class,
                        "returnNullResultInCaseNotFound", Boolean.TRUE);
                passData(PersonIdPaneFxController.class, GetPassportIdByIdWorkflowTask.class, "personId");
                executeWorkflowTask(GetPassportIdByIdWorkflowTask.class);

                break;
            }
            case 1: {

                Boolean fingerprintsExist = getData(FetchingFingerprintsWorkflowTask.class, "fingerprintsExist");
                if (fingerprintsExist != null && !fingerprintsExist) { incrementNSteps(1); }

                passData(GetPersonInfoByIdWorkflowTask.class, ShowingPersonInfoFxController.class, "personInfo");

                passData(GetPassportIdByIdWorkflowTask.class, ShowingPersonInfoFxController.class, "passportId");
                renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);

                PersonInfo personInfo = getData(GetPersonInfoByIdWorkflowTask.class,
                        "personInfo");
                if (personInfo != null) {
                    setData(ShowingFingerprintsPaneFxController.class,
                            "facePhotoBase64", personInfo.getFace());
                }

                passData(PersonIdPaneFxController.class, FetchingFingerprintsWorkflowTask.class,
                        "personId");

                executeWorkflowTask(FetchingFingerprintsWorkflowTask.class);

                fingerprintsExist = getData(FetchingFingerprintsWorkflowTask.class, "fingerprintsExist");
                setData(ShowingPersonInfoFxController.class, "fingerprintsExist", fingerprintsExist);

                if (fingerprintsExist) {
                    passData(PersonIdPaneFxController.class, FetchingMissingFingerprintsWorkflowTask.class,
                            "personId");
                    executeWorkflowTask(FetchingMissingFingerprintsWorkflowTask.class);

                    passData(FetchingFingerprintsWorkflowTask.class,
                            SegmentWsqFingerprintsWorkflowTask.class,
                            "fingerprints");
                    passData(FetchingMissingFingerprintsWorkflowTask.class, SegmentWsqFingerprintsWorkflowTask.class,
                            "missingFingerprints");
                    executeWorkflowTask(SegmentWsqFingerprintsWorkflowTask.class);

                    passData(SegmentWsqFingerprintsWorkflowTask.class,
                            "segmentedFingerPrints", ShowingFingerprintsQualityPaneFxController.class, "fingerprints");
                }

                break;
            }
            case 2: {
                ServiceType serviceType = getData(ShowingFingerprintsQualityPaneFxController.class, "serviceType");

                if (serviceType == ServiceType.INQUIRY) {
                    incrementNSteps(2);
                }
                renderUiAndWaitForUserInput(ShowingFingerprintsQualityPaneFxController.class);

                break;
            }
            case 3: {

                Boolean fingerprintsExist = getData(FetchingFingerprintsWorkflowTask.class, "fingerprintsExist");

                if (!fingerprintsExist) {

                    setData(SlapFingerprintsCapturingFxController.class, "hidePreviousButton",
                            Boolean.TRUE);
                    setData(SlapFingerprintsCapturingFxController.class, "showStartOverButton",
                            Boolean.TRUE);
                    incrementNSteps(-1);
                }

                setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprint",
                        Boolean.TRUE);
                setData(SlapFingerprintsCapturingFxController.class, "acceptBadQualityFingerprintMinRetires",
                        0);

                renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);

                break;
            }
            case 4: {

                passData(SlapFingerprintsCapturingFxController.class,
                        ShowingFingerprintsPaneFxController.class,
                        "fingerprintBase64Images");
                setData(ShowingFingerprintsPaneFxController.class, "hideGenerateNistFileButton", Boolean.TRUE);
                renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);


                break;
            }
            case 5: {

                ServiceType serviceType = getData(ShowingFingerprintsQualityPaneFxController.class, "serviceType");
                if (serviceType == ServiceType.INQUIRY) {
                    incrementNSteps(-2);
                }

                FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status status = getData(
                        FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "status");

                setData(InquiryCriminalByFingerprintsPaneFxController.class, "status", status);
                renderUiAndWaitForUserInput(InquiryCriminalByFingerprintsPaneFxController.class);
                Integer inquiryId = getData(FingerprintInquiryCriminalWorkflowTask.class, "inquiryId");

                if (inquiryId == null) {

                    if (serviceType == ServiceType.INQUIRY) {
                        passData(FetchingFingerprintsWorkflowTask.class, FingerprintInquiryCriminalWorkflowTask.class,
                                "fingerprints");
                        passData(FetchingMissingFingerprintsWorkflowTask.class, FingerprintInquiryCriminalWorkflowTask.class,
                                "missingFingerprints");
                    }
                    else {
                        passData(SlapFingerprintsCapturingFxController.class, "segmentedFingerprints", FingerprintInquiryCriminalWorkflowTask.class,
                                "fingerprints");

                        passData(SlapFingerprintsCapturingFxController.class, FingerprintInquiryCriminalWorkflowTask.class,
                                "missingFingerprints");
                    }
                    executeWorkflowTask(FingerprintInquiryCriminalWorkflowTask.class);
                }

                passData(FingerprintInquiryCriminalWorkflowTask.class,
                        FingerprintInquiryCriminalStatusCheckerWorkflowTask.class,
                        "inquiryId");
                executeWorkflowTask(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class);

                status = getData(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "status");
                if (status == FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status.HIT) {
                    Long criminalBiometricsId = getData(
                            FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "criminalBiometricsId");
                    setData(InquiryCriminalByFingerprintsPaneFxController.class, "criminalBiometricsId", criminalBiometricsId);

                    Long civilBiometricsId = getData(
                            FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "civilBiometricsId");

                    if (civilBiometricsId != null) {
                        setData(CheckCWLByBioIdWorkflowTask.class, "bioId", civilBiometricsId);
                        executeWorkflowTask(CheckCWLByBioIdWorkflowTask.class);

                        List<WatchListRecord> watchListRecordList = getData(
                                CheckCWLByBioIdWorkflowTask.class, "watchListRecordList");
                        setData(InquiryCriminalByFingerprintsPaneFxController.class, "watchListRecordList", watchListRecordList);
                    }
                }
                break;
            }
            case 6: {
                renderUiAndWaitForUserInput(CriminalClearanceDetailsPaneFxController.class);
                break;
            }
            case 7: {

                Boolean fingerprintsExist = getData(FetchingFingerprintsWorkflowTask.class, "fingerprintsExist");
                ShowingFingerprintsQualityPaneFxController.ServiceType serviceType1 = getData(ShowingFingerprintsQualityPaneFxController.class, "serviceType");

                if (!fingerprintsExist || serviceType1 == ServiceType.RETAKE_FINGERPRINT) {
                    passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                            "fingerprintBase64Images");

                    passData(SlapFingerprintsCapturingFxController.class,"segmentedFingerprints", ReviewAndSubmitPaneFxController.class,
                            "fingerprints");

                    passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                            "missingFingerprints");
                }
                else {
                    passData(SegmentWsqFingerprintsWorkflowTask.class,
                            ReviewAndSubmitPaneFxController.class,
                            "fingerprintBase64Images");

                    passData(FetchingFingerprintsWorkflowTask.class, "fingerprints",
                            ReviewAndSubmitPaneFxController.class,
                            "fingerprints");

                    passData(FetchingMissingFingerprintsWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
                            "missingFingerprints");
                }

                passData(GetPersonInfoByIdWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
                        "personInfo");
                passData(GetPassportIdByIdWorkflowTask.class, ReviewAndSubmitPaneFxController.class,
                        "passportId");
                passData(ShowingPersonInfoFxController.class, ReviewAndSubmitPaneFxController.class,
                        "normalizedPersonInfo");

                passData(CriminalClearanceDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class, "whoRequestedTheReport");
                passData(CriminalClearanceDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class, "purposeOfTheReport");
                renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
                break;
            }
            case 8: {

                passData(ReviewAndSubmitPaneFxController.class, SubmitCriminalClearanceReport.class, "criminalClearanceReport");

                renderUiAndWaitForUserInput(RegisteringCriminalClearanceReportPaneFxController.class);

                executeWorkflowTask(SubmitCriminalClearanceReport.class);

                break;
            }
            case 9: {

                Boolean fingerprintsExist = getData(FetchingFingerprintsWorkflowTask.class, "fingerprintsExist");
                ShowingFingerprintsQualityPaneFxController.ServiceType serviceType1 = getData(ShowingFingerprintsQualityPaneFxController.class, "serviceType");

                if (!fingerprintsExist || serviceType1 == ServiceType.RETAKE_FINGERPRINT) {
                    passData(SlapFingerprintsCapturingFxController.class, ShowReportPaneFxController.class,
                            "fingerprintBase64Images");
                }
                else {
                    passData(SegmentWsqFingerprintsWorkflowTask.class,
                            ShowReportPaneFxController.class,
                            "fingerprintBase64Images");
                }

                passData(GetPassportIdByIdWorkflowTask.class, ShowReportPaneFxController.class,
                        "passportId");
                passData(GetPersonInfoByIdWorkflowTask.class, ShowReportPaneFxController.class,
                        "personInfo");
                passData(ReviewAndSubmitPaneFxController.class, ShowReportPaneFxController.class,
                        "criminalClearanceReport");
                passData(SubmitCriminalClearanceReport.class, ShowReportPaneFxController.class,
                        "criminalClearanceResponse");

                renderUiAndWaitForUserInput(ShowReportPaneFxController.class);
                break;
            }

            default: {
                break;
            }
        }
    }
}
