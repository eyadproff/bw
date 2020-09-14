package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.utils.CitizenEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans.Candidate;
import sa.gov.nic.bio.commons.TaskResponse;

import java.util.List;

public class PossibilityFaceImageExistsWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private List<Candidate> candidates;
    @Output
    private Boolean ignorePossibilityAndCompleteWorkflow;


    @Override
    public void execute() throws Signal {

        int score = Integer.parseInt(
                Context.getConfigManager().getProperty("registerCitizen.faceSearch.score"));
        if (candidates != null) {
            for (Candidate candidate : candidates) {
                if (score < candidate.getScore()) {
                    String headerText =
                            Context.getGuiLanguage() == GuiLanguage.ARABIC ? "تنبيه لإحتمالية وجود تطابق في صورة الوجه في قواعد البيانات !!" : "Alert ! there is a Possibility the FaceImage Exists";
                    String contentText = Context.getGuiLanguage() == GuiLanguage.ARABIC ? " هل أنت متأكد من أنك تريد إكمال عملية التسجيل" : "Are you sure you want to complete?";
                    boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);

                    if (confirmed) {
                        ignorePossibilityAndCompleteWorkflow = true;
                    }
                    else { ignorePossibilityAndCompleteWorkflow = false; }
                    break;
                }
            }
        }
        else { ignorePossibilityAndCompleteWorkflow = true; }


    }
}