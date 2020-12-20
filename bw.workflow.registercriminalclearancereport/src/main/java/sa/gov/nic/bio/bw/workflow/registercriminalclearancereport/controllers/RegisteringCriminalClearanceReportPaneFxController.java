package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;

@FxmlFile("registeringCriminalClearanceReport.fxml")
public class RegisteringCriminalClearanceReportPaneFxController extends WizardStepFxControllerBase {

    @FXML private ProgressIndicator piProgress;
    @FXML private ImageView ivSuccess;
    @FXML private ImageView ivFailure;
    @FXML private Label lblStatus;
    @FXML private Button btnRetry;
    @FXML private Button btnStartOver;

    private boolean disableRetryButtonForever = false;

    @Override
    protected void onAttachedToScene() {
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {

        if (successfulResponse) {
            lblStatus.setText(resources.getString("label.successCriminalClearanceReportRegistration"));
            goNext();
        }
        else {
            lblStatus.setText(resources.getString("label.failedToSendCriminalClearanceReport"));
            piProgress.setVisible(false);
            ivFailure.setVisible(true);
            btnStartOver.setVisible(true);
            btnRetry.setVisible(!disableRetryButtonForever);
        }
    }

    @FXML
    private void onRetryButtonClicked(ActionEvent actionEvent) {

        btnRetry.setVisible(false);
        btnStartOver.setVisible(false);
        ivFailure.setVisible(false);
        piProgress.setVisible(true);

        lblStatus.setText(resources.getString("label.submitCriminalClearanceReport"));


        continueWorkflow();
    }
}