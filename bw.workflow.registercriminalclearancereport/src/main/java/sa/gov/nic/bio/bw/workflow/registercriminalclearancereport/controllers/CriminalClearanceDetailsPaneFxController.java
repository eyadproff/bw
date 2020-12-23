package sa.gov.nic.bio.bw.workflow.registercriminalclearancereport.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Output;

@FxmlFile("criminalclearancedetails.fxml")
public class CriminalClearanceDetailsPaneFxController extends WizardStepFxControllerBase {

    @Output
    private String whoRequestedTheReport;
    @Output
    private String purposeOfTheReport;

    @FXML private TextField txtWhoRequestedTheReport;
    @FXML private TextField txtPurposeOfTheReport;
    @FXML private Button btnNext;

    @Override
    protected void onAttachedToScene() {

        btnNext.disableProperty().bind((GuiUtils.textFieldBlankBinding(txtWhoRequestedTheReport).or(txtWhoRequestedTheReport.disabledProperty())).or
                (GuiUtils.textFieldBlankBinding(txtPurposeOfTheReport).or(txtPurposeOfTheReport.disabledProperty())));

        GuiUtils.applyValidatorToTextField(txtWhoRequestedTheReport, 100);
        GuiUtils.applyValidatorToTextField(txtPurposeOfTheReport, 100);

        if (whoRequestedTheReport != null) { txtWhoRequestedTheReport.setText(whoRequestedTheReport); }
        if (purposeOfTheReport != null) { txtPurposeOfTheReport.setText(purposeOfTheReport); }

        EventHandler<? super KeyEvent> keyReleasedEventHandler = keyEvent ->
        {
            if (keyEvent.getCode() == KeyCode.ENTER && !btnNext.isDisable()) { btnNext.fire(); }
        };

        txtPurposeOfTheReport.setOnKeyReleased(keyReleasedEventHandler);
        txtWhoRequestedTheReport.setOnKeyReleased(keyReleasedEventHandler);

        btnNext.requestFocus();
    }

    @FXML
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        whoRequestedTheReport = txtWhoRequestedTheReport.getText();
        purposeOfTheReport = txtPurposeOfTheReport.getText();
        goNext();
    }


}
