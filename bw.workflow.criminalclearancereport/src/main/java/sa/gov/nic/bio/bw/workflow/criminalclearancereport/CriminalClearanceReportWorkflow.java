package sa.gov.nic.bio.bw.workflow.criminalclearancereport;

import javafx.scene.image.Image;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.*;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.ScanFingerprintCardPaneFxController;
import sa.gov.nic.bio.bw.workflow.civilcriminalfingerprintsinquiry.controllers.SpecifyFingerprintCoordinatesPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.*;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.ShowingFingerprintsQualityPaneFxController.ServiceType;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.SlapFingerprintsCapturingFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.*;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.FingerprintInquiryCriminalStatusCheckerWorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.FingerprintInquiryCriminalWorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.tasks.SegmentWsqFingerprintsWorkflowTask;

import java.util.Map;

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

                InquiryByFingerprintsPaneFxController.ServiceType serviceType = getData(InquiryByFingerprintsPaneFxController.class, "serviceType");
                if (serviceType == InquiryByFingerprintsPaneFxController.ServiceType.TAKEFINGERPRINTS) {
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

                status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");

                if (status == FingerprintInquiryStatusCheckerWorkflowTask.Status.HIT) {
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
                }


                break;
            }
            case 4: {
                ServiceType serviceType = getData(ShowingFingerprintsQualityPaneFxController.class, "serviceType");

                if (serviceType == ServiceType.INQUIRY) {
                    incrementNSteps(2);
                }
                renderUiAndWaitForUserInput(ShowingFingerprintsQualityPaneFxController.class);

                break;
            }
            case 5: {
                FingerprintInquiryStatusCheckerWorkflowTask.Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");

                if (status == FingerprintInquiryStatusCheckerWorkflowTask.Status.NOT_HIT) {

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
            case 6: {

                passData(SlapFingerprintsCapturingFxController.class,
                        ShowingFingerprintsPaneFxController.class,
                        "fingerprintBase64Images");
                setData(ShowingFingerprintsPaneFxController.class, "hideGenerateNistFileButton", Boolean.TRUE);
                renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);


                break;
            }
            case 7: {
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
                    passData(FetchingFingerprintsWorkflowTask.class, FingerprintInquiryCriminalWorkflowTask.class,
                            "fingerprints");
                    passData(ShowingFingerprintsPaneFxController.class, FingerprintInquiryCriminalWorkflowTask.class,
                            "missingFingerprints");
                    executeWorkflowTask(FingerprintInquiryCriminalWorkflowTask.class);
                }

                passData(FingerprintInquiryCriminalWorkflowTask.class,
                        FingerprintInquiryCriminalStatusCheckerWorkflowTask.class,
                        "inquiryId");
//                setData(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "ignoreCriminalFingerprintsInquiryResult", Boolean.TRUE);
                executeWorkflowTask(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class);

                status = getData(FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "status");
                if (status == FingerprintInquiryCriminalStatusCheckerWorkflowTask.Status.HIT) {
                    Long criminalBiometricsId = getData(
                            FingerprintInquiryCriminalStatusCheckerWorkflowTask.class, "criminalBiometricsId");
                    setData(InquiryCriminalByFingerprintsPaneFxController.class, "criminalBiometricsId", criminalBiometricsId);
                }
                break;
            }
            case 8: {
                renderUiAndWaitForUserInput(CriminalClearanceDetailsPaneFxController.class);
                break;
            }
            case 9: {

                InquiryByFingerprintsPaneFxController.ServiceType serviceType = getData(InquiryByFingerprintsPaneFxController.class, "serviceType");
                if (serviceType == InquiryByFingerprintsPaneFxController.ServiceType.TAKEFINGERPRINTS) {
                    passData(SlapFingerprintsCapturingFxController.class, ReviewAndSubmitPaneFxController.class,
                            "fingerprintBase64Images");
                }else{
                    passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                            ReviewAndSubmitPaneFxController.class,
                            "fingerprintBase64Images");
                }

                passData(ShowingPersonInfoFxController.class, ReviewAndSubmitPaneFxController.class,
                        "normalizedPersonInfo");

                passData(CriminalClearanceDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class, "whoRequestedTheReport");
                passData(CriminalClearanceDetailsPaneFxController.class, ReviewAndSubmitPaneFxController.class, "purposeOfTheReport");
                renderUiAndWaitForUserInput(ReviewAndSubmitPaneFxController.class);
                break;
            }
            case 10: {
                //                renderUiAndWaitForUserInput();
                break;
            }
        }
    }
}
