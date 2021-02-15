package sa.gov.nic.bio.bw.workflow.registeriris.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;

@FxmlFile("registeringIris.fxml")
public class RegisteringIrisPaneFxController extends WizardStepFxControllerBase {

    @Input protected Long personId;
    @Input protected String rightIrisBase64;
    @Input protected String leftIrisBase64;

    @FXML private ProgressIndicator piProgress;
    @FXML private ImageView ivSuccess;
    @FXML private ImageView ivFailure;
    @FXML private Label lblStatus;
    @FXML private Button btnRetry;
    @FXML private Button btnStartOver;


    @Override
    protected void onAttachedToScene() {
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {

        if (successfulResponse) {
            lblStatus.setText(resources.getString("label.successIrisRegistration"));
            piProgress.setVisible(false);
            ivSuccess.setVisible(true);
            btnStartOver.setVisible(true);
        }
        else {
            lblStatus.setText(resources.getString("label.failedToRegisterIris"));
            piProgress.setVisible(false);
            ivFailure.setVisible(true);
            btnStartOver.setVisible(true);
            btnRetry.setVisible(true);
        }
    }

    @FXML
    private void onRetryButtonClicked(ActionEvent actionEvent) {
        btnRetry.setVisible(false);
        btnStartOver.setVisible(false);
        ivFailure.setVisible(false);
        piProgress.setVisible(true);

        lblStatus.setText(resources.getString("label.submittingIris"));

        continueWorkflow();
    }
}