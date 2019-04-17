package sa.gov.nic.bio.bw.workflow.biometricsexception;

import sa.gov.nic.bio.bw.core.wizard.Step;
import sa.gov.nic.bio.bw.core.wizard.Wizard;
import sa.gov.nic.bio.bw.core.workflow.AssociatedMenu;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WizardWorkflowBase;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.*;
import sa.gov.nic.bio.bw.workflow.commons.tasks.GetPersonInfoByIdWorkflowTask;

//import sa.gov.nic.bio.bw.workflow.commons.controllers.ShowingPersonInfoFxController;


@AssociatedMenu(workflowId = 1017, menuId = "menu.edit.biometricsException", menuTitle = "menu.title", menuOrder = 2)

@Wizard({@Step(iconId = "\\uf2bb", title = "wizard.InquiryByPersonId"),
        @Step(iconId = "database", title = "wizard.inquiryResult"),
        @Step(iconId = "question", title = "wizard.biometricsExceptionType"),
        @Step(iconId = "\\uf256", title = "wizard.editMissingFingerPrint"),
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

                setData(GetPersonInfoByIdWorkflowTask.class, "returnNullResultInCaseNotFound", true);

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

                break;
            }
            case 3: {
                renderUiAndWaitForUserInput(EditMissingFingerprintFXController.class);
                break;
            }
            case 4: {
                renderUiAndWaitForUserInput(ReviewAndSubmitFXController.class);
                break;
            }
            case 5: {

                renderUiAndWaitForUserInput(ShowResultFXController.class);

                break;
            }

            default:
                break;
        }


    }
}
