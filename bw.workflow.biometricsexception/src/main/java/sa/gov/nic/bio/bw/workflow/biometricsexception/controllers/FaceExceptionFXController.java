package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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
    @FXML
    private ComboBox ComboMenu;


    @Override
    protected void onAttachedToScene() {
        AddItemsToMenu();
        if (Reason != null)
            uploadReason();


    }

    private void AddItemsToMenu() {

        ComboMenu.getItems().addAll(resources.getString("Other"), "جرح");
        ComboMenu.setValue(resources.getString("Cause"));
        ComboMenu.setOnAction(e -> OnActionComboMenu());

    }

    private void OnActionComboMenu() {
        if (ComboMenu.getValue().toString().equals(resources.getString("Other"))) {
            TxtfaceExcReason.setVisible(true);
        } else
            TxtfaceExcReason.setVisible(false);

    }


    private void uploadReason() {
        for (Object item : ComboMenu.getItems()) {
            if (item.toString().equals(Reason)) {
                ComboMenu.getSelectionModel().select(Reason);
                return;
            }

        }
        ComboMenu.getSelectionModel().select(resources.getString("Other"));
        TxtfaceExcReason.setVisible(true);
        TxtfaceExcReason.setText(Reason);
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (ComboMenu.getValue().toString().equals(resources.getString("Cause"))) {
            showWarningNotification(resources.getString("SelectCause"));
            return;
        }
        if (ComboMenu.getValue().toString().equals(resources.getString("Other"))) {
            if (TxtfaceExcReason.getText().trim().isEmpty()) {
                showWarningNotification(resources.getString("WriteCause"));
                return;
            } else Reason = TxtfaceExcReason.getText();
        } else Reason = ComboMenu.getValue().toString();


        super.onNextButtonClicked(actionEvent);
    }

}
