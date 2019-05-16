package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;


import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;

@FxmlFile("showResult.fxml")
public class ShowResultFXController extends WizardStepFxControllerBase {
    @Input
    private boolean success;

    @FXML
    private VBox VFailed;
    @FXML
    private VBox VSuccess;

    @Override
    protected void onAttachedToScene() {
        if (success) {
            VSuccess.setVisible(true);
            VSuccess.setManaged(true);

        } else {
            VFailed.setVisible(true);
            VFailed.setManaged(true);
        }


    }
}
