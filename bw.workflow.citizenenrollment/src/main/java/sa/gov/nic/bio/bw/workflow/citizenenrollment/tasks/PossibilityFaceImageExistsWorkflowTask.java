package sa.gov.nic.bio.bw.workflow.citizenenrollment.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.core.workflow.Signal;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;
import sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans.Candidate;

import java.util.List;

public class PossibilityFaceImageExistsWorkflowTask extends WorkflowTask {
    @Input(alwaysRequired = true)
    private List<Candidate> candidates;
    @Output
    private Boolean IsPossibleFaceImageExists;


    @Override
    public void execute() throws Signal {

        int score = Integer.parseInt(
                Context.getConfigManager().getProperty("citizenEnrollment.faceSearch.score"));
        if (candidates != null) {
            for (Candidate candidate : candidates) {
                if (score < candidate.getScore()) {
                    IsPossibleFaceImageExists = true;
                    break;
                }
            }
        }
    }

}