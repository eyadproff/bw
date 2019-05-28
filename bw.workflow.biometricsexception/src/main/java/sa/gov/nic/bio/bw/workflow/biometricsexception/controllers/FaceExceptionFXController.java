package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
    private MenuButton SelectCause;


    @Override
    protected void onAttachedToScene() {
        AddItemsToMenu();
        if (Reason != null)
            uploadReason();


    }

    private void AddItemsToMenu() {
        MenuItem item = new MenuItem();
        item.setText(resources.getString("Other"));
        item.setOnAction(e -> OnActionMenuItemOtherCouse(item.getText()));
        SelectCause.getItems().add(item);

        MenuItem item2 = new MenuItem();
        item2.setText("جرح");
        item2.setOnAction(e -> OnActionMenuItem(item2.getText()));
        //  item2.getGraphic().prefWidth(SelectCause.getWidth());
        SelectCause.getItems().add(item2);
    }

    //    private MenuItem prepareMenuItem(String text) {
//        MenuItem item = new MenuItem();
//        Label label = new Label();
//        label.prefWidthProperty().bind(SelectCause.widthProperty());
//        label.setText(text);
//        item.setGraphic(label);
//        item.setOnAction(e -> OnActionMenuItemOtherCouse(item.getText()));
//        return item;
//    }
    private void OnActionMenuItem(String reason) {
        SelectCause.setText(reason);
        TxtfaceExcReason.setVisible(false);
    }

    private void OnActionMenuItemOtherCouse(String reason) {

        SelectCause.setText(resources.getString("Other"));
        TxtfaceExcReason.setVisible(true);


    }

    private void uploadReason() {
        for (MenuItem item : SelectCause.getItems()) {
            if (item.getText().equals(Reason)) {
                SelectCause.setText(Reason);
                return;
            }

        }
        SelectCause.setText(resources.getString("Other"));
        TxtfaceExcReason.setVisible(true);
        TxtfaceExcReason.setText(Reason);
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (SelectCause.getText().equals(resources.getString("Cause"))) {
            showWarningNotification(resources.getString("SelectCause"));
            return;
        }
        if (SelectCause.getText().equals(resources.getString("Other"))) {
            if (TxtfaceExcReason.getText().trim().isEmpty()) {
                showWarningNotification(resources.getString("WriteCause"));
                return;
            } else Reason = TxtfaceExcReason.getText();
        } else Reason = SelectCause.getText();


        super.onNextButtonClicked(actionEvent);
    }

}
