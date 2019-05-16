package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Output;

@FxmlFile("faceException.fxml")
public class FaceExceptionFXController extends WizardStepFxControllerBase {

    @Output
    private String Reason;

    @FXML
    private TextField TxtfaceExcReason;


    @Override
    protected void onAttachedToScene() {
        if (Reason != null)
            TxtfaceExcReason.setText(Reason);


    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        Reason = TxtfaceExcReason.getText();
        super.onNextButtonClicked(actionEvent);
    }
}
