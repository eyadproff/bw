package sa.gov.nic.bio.bw.workflow.criminalclearancereport;

import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.*;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.ShowingPersonInfoFxController;
import sa.gov.nic.bio.bw.workflow.criminalclearancereport.controllers.PersonIdPaneFxController;

@AssociatedMenu(workflowId = 1031, menuId = "menu.query.criminalclearancereport",
                menuTitle = "menu.title", menuOrder = 8, devices = {Device.FINGERPRINT_SCANNER})
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
         @Step(iconId = "\\uf2b9", title = "wizard.showPersonInformation"),
         @Step(iconId = "\\uf256", title = "wizard.showFingerprintsView"),
         @Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
         @Step(iconId = "search", title = "wizard.inquiryByFingerprints"),
         @Step(iconId = "save", title = "wizard.registerFingerprints")})
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

                break;
            }
            case 2: {
                PersonInfo personInfo = getData(GetPersonInfoByIdWorkflowTask.class,
                        "personInfo");
                if(personInfo != null) setData(ShowingFingerprintsPaneFxController.class,
                        "facePhotoBase64", personInfo.getFace());

                passData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
                        ShowingFingerprintsPaneFxController.class,
                        "fingerprintBase64Images");

                renderUiAndWaitForUserInput(ShowingFingerprintsPaneFxController.class);
                break;
            }
            case 3: {
                passData(FingerprintInquiryStatusCheckerWorkflowTask.class, InquiryByFingerprintsPaneFxController.class,
                        "status");

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

                FingerprintInquiryStatusCheckerWorkflowTask.Status status = getData(FingerprintInquiryStatusCheckerWorkflowTask.class, "status");
                if (status == FingerprintInquiryStatusCheckerWorkflowTask.Status.HIT) {


                }
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
            case 6: {
                break;
            }

        }
    }
}
