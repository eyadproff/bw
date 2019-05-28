package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

@FxmlFile("reviewAndSubmitFaceException.fxml")
public class ReviewAndSubmitFaceExceptionFXController extends WizardStepFxControllerBase {
    @Input
    private NormalizedPersonInfo normalizedPersonInfo;
    @Input
    private String Reason;

    @FXML
    private Label LblfaceExcReason;
    @FXML
    private Label PersonID, PersonName;

    @Override
    protected void onAttachedToScene() {
        PersonName.setText(normalizedPersonInfo.getFirstName() + " " + normalizedPersonInfo.getFatherName() + " " + normalizedPersonInfo.getFamilyName());
        PersonID.setText(normalizedPersonInfo.getPersonId().toString());

        LblfaceExcReason.setText(Reason);
    }
}
