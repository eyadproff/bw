package sa.gov.nic.bio.bw.workflow.criminalclearancereport;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.*;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.*;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.SegmentWsqFingerprintsWorkflowTask;

@AssociatedMenu(workflowId = 1031, menuId = "menu.query.criminalclearancereport",
                menuTitle = "menu.title", menuOrder = 8, devices = {Device.FINGERPRINT_SCANNER})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
                @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
                @Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
                @Step(iconId = "search", title = "wizard.inquiryByFingerprintsInCivil"),
                @Step(iconId = "\\uf256", title = "wizard.showQualityFingerprintsView"),
                @Step(iconId = "search", title = "wizard.inquiryByFingerprintsInCriminal"),
                @Step(iconId = "\\uf022", title = "wizard.addCriminalClearanceDetails"),
                @Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
                @Step(iconId = "save", title = "wizard.registerReport"),
                @Step(iconId = "file_pdf_alt", title = "wizard.showReport")})
public class CriminalClearanceReportWorkflow extends WizardWorkflowBase {

    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0: {
                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
                passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class, "personId");
                executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);

                break;
            }
            case 1: {
                passData(GetPersonInfoByIdWorkflowTask.class, ShowingPersonInfoFxController.class, "personInfo");
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

                passData(PersonIdPaneFxController.class, FetchingMissingFingerprintsWorkflowTask.class,
                        "personId");
                executeWorkflowTask(FetchingMissingFingerprintsWorkflowTask.class);

                passData(FetchingFingerprintsWorkflowTask.class,
                        ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                        "fingerprints");
                passData(FetchingMissingFingerprintsWorkflowTask.class,
                        ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                        "missingFingerprints");
                executeWorkflowTask(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class);

                passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                        ShowingFingerprintsPaneFxController.class,
                        "fingerprintBase64Images");


                break;
            }
            case 2: {
                setData(ShowingFingerprintsPaneFxController.class, "hideGenerateNistFileButton", Boolean.TRUE);
                renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);

                break;
            }
            case 3: {
                FingerprintInquiryStatusCheckerWorkflowTask.Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");

                if (status == FingerprintInquiryStatusCheckerWorkflowTask.Status.NOT_HIT) {
                    incrementNSteps(1);
                }
                setData(InquiryByFingerprintsPaneFxController.class, "status", status);

                renderUiAndWaitForUserInput(InquiryByFingerprintsPaneFxController.class);

                Integer inquiryId = getData(FingerprintInquiryWorkflowTask.class, "inquiryId");

                if (inquiryId == null) {
                    passData(FetchingFingerprintsWorkflowTask.class, FingerprintInquiryWorkflowTask.class,
                            "fingerprints");
                    passData(ShowingFingerprintsPaneFxController.class, FingerprintInquiryWorkflowTask.class,
                            "missingFingerprints");
                    executeWorkflowTask(FingerprintInquiryWorkflowTask.class);
                }

                setData(FingerprintInquiryStatusCheckerWorkflowTask.class, "ignoreCriminalFingerprintsInquiryResult", Boolean.TRUE);
                passData(FingerprintInquiryWorkflowTask.class, FingerprintInquiryStatusCheckerWorkflowTask.class,
                        "inquiryId");
                executeWorkflowTask(FingerprintInquiryStatusCheckerWorkflowTask.class);

                break;
            }
            case 4: {

                passData(FetchingFingerprintsWorkflowTask.class,
                        SegmentWsqFingerprintsWorkflowTask.class,
                        "fingerprints");
                passData(FetchingMissingFingerprintsWorkflowTask.class,
                        SegmentWsqFingerprintsWorkflowTask.class,
                        "missingFingerprints");
                executeWorkflowTask(SegmentWsqFingerprintsWorkflowTask.class);

                passData(
                        SegmentWsqFingerprintsWorkflowTask.class,
                        "segmentedFingerPrints", ShowingFingerprintsQualityPaneFxController.class, "fingerprints");
                renderUiAndWaitForUserInput(ShowingFingerprintsQualityPaneFxController.class);

                break;
            }
            case 5: {
                setData(FingerprintsSourceFxController.class, "showLiveScanOption", Boolean.TRUE);
                renderUiAndWaitForUserInput(FingerprintsSourceFxController.class);
                break;
            }
            case 6: {
                FingerprintsSourceFxController.Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
                        "fingerprintsSource");
                if (fingerprintsSource.equals(FingerprintsSourceFxController.Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)) {
                    renderUiAndWaitForUserInput(SlapFingerprintsCapturingFxController.class);
                }
                if (fingerprintsSource.equals(FingerprintsSourceFxController.Source.SCANNING_FINGERPRINTS_CARD)) {
                    renderUiAndWaitForUserInput(ScanFingerprintCardPaneFxController.class);
                }

                break;
            }
            case 7: {

                FingerprintsSourceFxController.Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
                        "fingerprintsSource");
                if (fingerprintsSource.equals(FingerprintsSourceFxController.Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)) {
                    incrementNSteps(1);
                    passData(SlapFingerprintsCapturingFxController.class,
                            ShowingFingerprintsPaneFxController.class,
                            "fingerprintBase64Images");
                    setData(ShowingFingerprintsPaneFxController.class, "hideGenerateNistFileButton", Boolean.TRUE);
                    renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
                }
                if (fingerprintsSource.equals(FingerprintsSourceFxController.Source.SCANNING_FINGERPRINTS_CARD)) {
                    passData(ScanFingerprintCardPaneFxController.class,
                            SpecifyFingerprintCoordinatesPaneFxController.class, "cardImage");

                    renderUiAndWaitForUserInput(SpecifyFingerprintCoordinatesPaneFxController.class);
                }

                break;
            }
            case 8: {
                passData(SpecifyFingerprintCoordinatesPaneFxController.class,
                        ShowingFingerprintsPaneFxController.class,
                        "fingerprintBase64Images");
                setData(ShowingFingerprintsPaneFxController.class, "hideGenerateNistFileButton", Boolean.TRUE);
                renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
                break;
            }
            case 9: {
                FingerprintsSourceFxController.Source fingerprintsSource = getData(FingerprintsSourceFxController.class,
                        "fingerprintsSource");
                if (fingerprintsSource.equals(FingerprintsSourceFxController.Source.CAPTURING_FINGERPRINTS_VIA_FINGERPRINT_SCANNER)) {
                    incrementNSteps(-1);
                }
                //                renderUiAndWaitForUserInput();
                break;
            }
            case 10: {
                //                renderUiAndWaitForUserInput();
                break;
            }
            case 11: {
                //                renderUiAndWaitForUserInput();
                break;
            }
            case 12: {
                //                renderUiAndWaitForUserInput();
                break;
            }
        }
    }
}
