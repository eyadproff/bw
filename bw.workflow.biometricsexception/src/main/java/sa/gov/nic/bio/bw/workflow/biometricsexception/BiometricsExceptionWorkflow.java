package sa.gov.nic.bio.bw.workflow.biometricsexception;

import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WithLookups;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.*;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.BiometricsExceptionTypeFXController.Type;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.FaceExceptionFXController.TypeFaceService;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.ServiceTypeFXController.ServiceType;
import sa.gov.nic.bio.bw.workflow.biometricsexception.lookups.CausesLookup;
import sa.gov.nic.bio.bw.workflow.biometricsexception.tasks.DeleteBioExclusionsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsexception.tasks.RetrieveBioExclusionsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.biometricsexception.tasks.SubmitBioExclusionsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FetchingMissingFingerprintsWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;

import java.time.Instant;
import java.util.Collections;
import java.util.List;


@AssociatedMenu(workflowId = 1017, menuId = "menu.edit.biometricsException", menuTitle = "menu.title", menuOrder = 2)
@WithLookups({PersonTypesLookup.class, DocumentTypesLookup.class, CountriesLookup.class, CausesLookup.class})
@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.enterPersonId"),
                @Step(iconId = "database", title = "wizard.showingPersonInfo"),
                @Step(iconId = "question", title = "wizard.biometricsExceptionType"),
                @Step(iconId = "question", title = "wizard.serviceType"),
                @Step(iconId = "\\uf256", title = "wizard.addOrEditMissingFingerPrint"),
                @Step(iconId = "th_list", title = "wizard.reviewAndSubmit"),
                @Step(iconId = "\\uf0f6", title = "wizard.submissionResult")})


public class BiometricsExceptionWorkflow extends WizardWorkflowBase {


    @Override
    public void onStep(int step) throws InterruptedException, Signal {

        switch (step) {
            case 0: {
                renderUiAndWaitForUserInput(PersonIdPaneFxController.class);
                passData(PersonIdPaneFxController.class, GetPersonInfoByIdWorkflowTask.class,
                        "personId");
                //  setData(GetPersonInfoByIdWorkflowTask.class, "returnNullResultInCaseNotFound", true);

                executeWorkflowTask(GetPersonInfoByIdWorkflowTask.class);

                break;
            }
            case 1: {
                passData(GetPersonInfoByIdWorkflowTask.class, ShowingPersonInfoFxController.class,
                        "personInfo");

                renderUiAndWaitForUserInput(ShowingPersonInfoFxController.class);

                break;
            }
            case 2: {
                renderUiAndWaitForUserInput(BiometricsExceptionTypeFXController.class);

                Type type = getData(BiometricsExceptionTypeFXController.class, "exceptionType");
                if (Type.FACE.equals(type)) {

                    setData(RetrieveBioExclusionsWorkflowTask.class, "samisId",
                            (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class,
                                    "normalizedPersonInfo")).getPersonId()).intValue());
                    executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);

                    List<BioExclusion> bioEx = getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");
                    BioExclusion FaceException = null;
                    if (bioEx != null) {

                        //to get last tuples
                        Collections.reverse(bioEx);
                        for (BioExclusion bioex : bioEx) {
                            if (bioex.getBioType() == 3 && bioex.getStatus() == 0) {
                                FaceException = bioex;

                                break;

                            }
                        }
                    }
                    setData(FaceExceptionFXController.class, "FaceException", FaceException);


                }
                break;
            }
            case 3: {
                Type type = getData(BiometricsExceptionTypeFXController.class, "exceptionType");

                if (Type.FINGERPRINTS.equals(type)) {
                    renderUiAndWaitForUserInput(ServiceTypeFXController.class);

                    ServiceType serviceType = getData(ServiceTypeFXController.class, "serviceType");

                    List<Integer> MissingFingerPrints = null;
                    setData(RetrieveBioExclusionsWorkflowTask.class, "samisId",
                            (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class,
                                    "normalizedPersonInfo")).getPersonId()).intValue());
                    executeWorkflowTask(RetrieveBioExclusionsWorkflowTask.class);

                    List<BioExclusion> BioExclusionsList =
                            getData(RetrieveBioExclusionsWorkflowTask.class, "bioExclusionList");


                    if (BioExclusionsList != null) {

                        //to get last tuples
                        BioExclusionsList.removeIf(bioex -> bioex.getBioType() != 1);
                        Collections.reverse(BioExclusionsList);


                        //if there is ExpiredExc then get Available fingerprints
                        for (BioExclusion bioEx : BioExclusionsList) {
                            if (bioEx.getExpireDate() != null &&
                                bioEx.getExpireDate() < Instant.now().getEpochSecond()) {
                                setData(FetchingMissingFingerprintsWorkflowTask.class, "personId",
                                        (((NormalizedPersonInfo) getData(ShowingPersonInfoFxController.class,
                                                "normalizedPersonInfo")).getPersonId()).intValue());
                                executeWorkflowTask(FetchingMissingFingerprintsWorkflowTask.class);
                                MissingFingerPrints =
                                        getData(FetchingMissingFingerprintsWorkflowTask.class, "missingFingerprints");
                                break;
                            }
                        }
                    }


                    if (ServiceType.ADD_OR_EDIT_FINGERPRINTS.equals(serviceType)) {

                        setData(EditMissingFingerprintFXController.class, "personMissinfingerprints",
                                BioExclusionsList);
                        setData(EditMissingFingerprintFXController.class, "MissingFingerPrints", MissingFingerPrints);


                    }
                    else {
                        setData(DeleteMissingFingerprintFXController.class, "BioExclusionsList", BioExclusionsList);
                        setData(DeleteMissingFingerprintFXController.class, "MissingFingerPrints", MissingFingerPrints);


                    }
                }
                else {

                    incrementNSteps(1); // to skip step #4 on going next
                    renderUiAndWaitForUserInput(FaceExceptionFXController.class);

                }


                break;
            }
            case 4: {
                ServiceType serviceType = getData(ServiceTypeFXController.class, "serviceType");
                Type type = getData(BiometricsExceptionTypeFXController.class, "exceptionType");

                if (Type.FINGERPRINTS.equals(type)) {
                    if (ServiceType.ADD_OR_EDIT_FINGERPRINTS.equals(serviceType)) {
                        renderUiAndWaitForUserInput(EditMissingFingerprintFXController.class);
                    }
                    else {
                        renderUiAndWaitForUserInput(DeleteMissingFingerprintFXController.class);

                    }

                }
                break;
            }
            case 5: {
                ServiceType serviceType = getData(ServiceTypeFXController.class, "serviceType");
                Type type = getData(BiometricsExceptionTypeFXController.class, "exceptionType");

                if (Type.FINGERPRINTS.equals(type)) {


                    if (ServiceType.ADD_OR_EDIT_FINGERPRINTS.equals(serviceType)) {

                        passData(ShowingPersonInfoFxController.class, ReviewAndSubmitFXController.class,
                                "normalizedPersonInfo");
                        passData(EditMissingFingerprintFXController.class, ReviewAndSubmitFXController.class,
                                "Editedpersonfingerprints");
                        passData(EditMissingFingerprintFXController.class, ReviewAndSubmitFXController.class,
                                "BioExclusionsList");
                        setData(ReviewAndSubmitFXController.class, "serviceType", ServiceType.ADD_OR_EDIT_FINGERPRINTS);


                    }
                    else {

                        passData(ShowingPersonInfoFxController.class, ReviewAndSubmitFXController.class,
                                "normalizedPersonInfo");
                        passData(DeleteMissingFingerprintFXController.class, ReviewAndSubmitFXController.class,
                                "Editedpersonfingerprints", "personfingerprints");
                        setData(ReviewAndSubmitFXController.class, "serviceType", ServiceType.DELETE_FINGERPRINTS);


                    }

                    renderUiAndWaitForUserInput(ReviewAndSubmitFXController.class);

                    if (ServiceType.ADD_OR_EDIT_FINGERPRINTS.equals(serviceType)) {

                        @SuppressWarnings("unchecked")
                        List<Integer> SeqNumbersList =
                                getData(EditMissingFingerprintFXController.class, "SeqNumbersList");

                        if (SeqNumbersList.size() != 0) {

                            passData(EditMissingFingerprintFXController.class, DeleteBioExclusionsWorkflowTask.class,
                                    "SeqNumbersList");
                            executeWorkflowTask(DeleteBioExclusionsWorkflowTask.class);

                        }
                        //add or edit task
                        passData(ReviewAndSubmitFXController.class, SubmitBioExclusionsWorkflowTask.class,
                                "EditedBioExclusionsList");
                        executeWorkflowTask(SubmitBioExclusionsWorkflowTask.class);


                    }
                    else {

                        //delete task
                        passData(DeleteMissingFingerprintFXController.class, DeleteBioExclusionsWorkflowTask.class,
                                "SeqNumbersList");
                        executeWorkflowTask(DeleteBioExclusionsWorkflowTask.class);

                    }
                }
                else {
                    incrementNSteps(-1); // to skip step #4 on going previous

                    passData(ShowingPersonInfoFxController.class, ReviewAndSubmitFaceExceptionFXController.class,
                            "normalizedPersonInfo");
                    passData(FaceExceptionFXController.class, ReviewAndSubmitFaceExceptionFXController.class,
                            "EditFaceException");
                    passData(FaceExceptionFXController.class, ReviewAndSubmitFaceExceptionFXController.class,
                            "typeFaceService");
                    passData(FaceExceptionFXController.class, ReviewAndSubmitFaceExceptionFXController.class,
                            "causesFC");
                    renderUiAndWaitForUserInput(ReviewAndSubmitFaceExceptionFXController.class);

                    TypeFaceService typeFaceService = getData(FaceExceptionFXController.class, "typeFaceService");

                    if (typeFaceService.equals(TypeFaceService.ADD_OR_EDIT)) {


                        if ((getData(FaceExceptionFXController.class, "SeqNumbersList")) != null) {

                            passData(FaceExceptionFXController.class, DeleteBioExclusionsWorkflowTask.class,
                                    "SeqNumbersList");
                            executeWorkflowTask(DeleteBioExclusionsWorkflowTask.class);

                        }
                        //add or edit task
                        passData(ReviewAndSubmitFaceExceptionFXController.class, SubmitBioExclusionsWorkflowTask.class,
                                "EditedBioExclusionsList");
                        executeWorkflowTask(SubmitBioExclusionsWorkflowTask.class);


                    }
                    else {
                        //delete task
                        passData(FaceExceptionFXController.class, DeleteBioExclusionsWorkflowTask.class,
                                "SeqNumbersList");
                        executeWorkflowTask(DeleteBioExclusionsWorkflowTask.class);

                    }

                }

                break;
            }
            case 6: {
                setData(ShowResultFXController.class, "success", true);
                renderUiAndWaitForUserInput(ShowResultFXController.class);

                break;
            }
            default:
                break;
        }


    }
}
