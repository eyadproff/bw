package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Fingerprint;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.PersonFingerprints;

@FxmlFile("editMissingFingerprint.fxml")
public class EditMissingFingerprintFXController extends WizardStepFxControllerBase {

    @Output
    private PersonFingerprints personfingerprints;


    private PersonFingerprints PerF = new PersonFingerprints();


    @FXML
    private VBox VRightThumb, VOtherRThumb;
    @FXML
    private VBox VRightIndexFinger, VOtherRIndex;
    @FXML
    private VBox VRightMiddleFinger, VOtherRMiddle;
    @FXML
    private VBox VRightRingFinger, VOtherRRing;
    @FXML
    private VBox VRightLittleFinger, VOtherRLittle;

    @FXML
    private VBox VLeftThumb, VOtherLThumb;
    @FXML
    private VBox VLeftIndexFinger, VOtherLIndex;
    @FXML
    private VBox VLeftMiddleFinger, VOtherLMiddle;
    @FXML
    private VBox VLeftRingFinger, VOtherLRing;
    @FXML
    private VBox VLeftLittleFinger, VOtherLLittle;

    @FXML
    private CheckBox chbRightThumb;
    @FXML
    private CheckBox chbRightIndex;
    @FXML
    private CheckBox chbRightMiddle;
    @FXML
    private CheckBox chbRightRing;
    @FXML
    private CheckBox chbRightLittle;

    @FXML
    private CheckBox chbLeftThumb;
    @FXML
    private CheckBox chbLeftIndex;
    @FXML
    private CheckBox chbLeftMiddle;
    @FXML
    private CheckBox chbLeftRing;
    @FXML
    private CheckBox chbLeftLittle;

    @FXML
    private MenuButton MenuRThumb, MenuRIndex, MenuRMiddle, MenuRRing, MenuRLittle;
    @FXML
    private MenuButton MenuLThumb, MenuLIndex, MenuLMiddle, MenuLRing, MenuLLittle;

    @FXML
    private ToggleGroup TGRThumb, TGRIndex, TGRMiddle, TGRRing, TGRLittle;
    @FXML
    private ToggleGroup TGLThumb, TGLIndex, TGLMiddle, TGLRing, TGLLittle;

    @FXML
    private TextField CouseTRThumb, CouseTRIndex, CouseTRMiddle, CouseTRRing, CouseTRLittle;
    @FXML
    private TextField CouseTLThumb, CouseTLIndex, CouseTLMiddle, CouseTLRing, CouseTLLittle;


    @Override
    protected void onAttachedToScene() {
//        VRightThumb.getChildren().


        AddItemsToMenu(MenuRThumb, VOtherRThumb);
        AddItemsToMenu(MenuRIndex, VOtherRIndex);
        AddItemsToMenu(MenuRMiddle, VOtherRMiddle);
        AddItemsToMenu(MenuRRing, VOtherRRing);
        AddItemsToMenu(MenuRLittle, VOtherRLittle);

        AddItemsToMenu(MenuLThumb, VOtherLThumb);
        AddItemsToMenu(MenuLIndex, VOtherLIndex);
        AddItemsToMenu(MenuLMiddle, VOtherLMiddle);
        AddItemsToMenu(MenuLRing, VOtherLRing);
        AddItemsToMenu(MenuLLittle, VOtherLLittle);

        if (personfingerprints != null) {
            uploadMissingFPIfAny();

        }
        CheckBoxAction();


    }

    @FXML
    private void CheckBoxAction() {
        if (chbRightThumb.isSelected()) VRightThumb.setDisable(false);
        else VRightThumb.setDisable(true);
        if (chbRightIndex.isSelected()) VRightIndexFinger.setDisable(false);
        else VRightIndexFinger.setDisable(true);
        if (chbRightMiddle.isSelected()) VRightMiddleFinger.setDisable(false);
        else VRightMiddleFinger.setDisable(true);
        if (chbRightRing.isSelected()) VRightRingFinger.setDisable(false);
        else VRightRingFinger.setDisable(true);
        if (chbRightLittle.isSelected()) VRightLittleFinger.setDisable(false);
        else VRightLittleFinger.setDisable(true);

        if (chbLeftThumb.isSelected()) VLeftThumb.setDisable(false);
        else VLeftThumb.setDisable(true);
        if (chbLeftIndex.isSelected()) VLeftIndexFinger.setDisable(false);
        else VLeftIndexFinger.setDisable(true);
        if (chbLeftMiddle.isSelected()) VLeftMiddleFinger.setDisable(false);
        else VLeftMiddleFinger.setDisable(true);
        if (chbLeftRing.isSelected()) VLeftRingFinger.setDisable(false);
        else VLeftRingFinger.setDisable(true);
        if (chbLeftLittle.isSelected()) VLeftLittleFinger.setDisable(false);
        else VLeftLittleFinger.setDisable(true);


    }

    private void AddItemsToMenu(MenuButton menu, VBox VOther) {
        MenuItem item = new MenuItem();
        item.setText(resources.getString("Other"));
        item.setOnAction(e -> OnActionMenuItemOtherCouse(item.getText(), menu, VOther));
        menu.getItems().add(item);

        MenuItem item2 = new MenuItem();
        item2.setText("جرح");
        item2.setOnAction(e -> OnActionMenuItem(item2.getText(), menu, VOther));
        menu.getItems().add(item2);

    }

    @FXML
    private void OnActionMenuItem(String text, MenuButton menu, VBox VOther) {

        VOther.setVisible(false);
        menu.setText(text);


    }

    @FXML
    private void OnActionMenuItemOtherCouse(String text, MenuButton menu, VBox VOther) {

        VOther.setVisible(true);
        menu.setText(text);

    }


    private void addMFToPersonFPs(Fingerprint finger, MenuButton Couse, ToggleGroup TG, TextField CouseOther) {
        finger.setMissOrNot(true);
        if (!Couse.getText().equals(resources.getString("Other"))) {
            finger.setCouse(Couse.getText());
            finger.setStatus(1);
        } else {
            finger.setCouse(CouseOther.getText());
            if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("Temporary")) )
                finger.setStatus(1);
            else
                finger.setStatus(0);
        }
    }

    private void uploadMissingFPIfAny() {

        if (personfingerprints.getRThumb().isMissOrNot())
            setDataToScene(personfingerprints.getRThumb(), MenuRThumb, TGRThumb, CouseTRThumb, chbRightThumb, VOtherRThumb);
        if (personfingerprints.getRIndex().isMissOrNot())
            setDataToScene(personfingerprints.getRIndex(), MenuRIndex, TGRIndex, CouseTRIndex, chbRightIndex, VOtherRIndex);
        if (personfingerprints.getRMiddle().isMissOrNot())
            setDataToScene(personfingerprints.getRMiddle(), MenuRMiddle, TGRMiddle, CouseTRMiddle, chbRightMiddle, VOtherRMiddle);
        if (personfingerprints.getRRing().isMissOrNot())
            setDataToScene(personfingerprints.getRRing(), MenuRRing, TGRRing, CouseTRRing, chbRightRing, VOtherRRing);
        if (personfingerprints.getRLittle().isMissOrNot())
            setDataToScene(personfingerprints.getRLittle(), MenuRLittle, TGRLittle, CouseTRLittle, chbRightLittle, VOtherRLittle);

        if (personfingerprints.getLThumb().isMissOrNot())
            setDataToScene(personfingerprints.getLThumb(), MenuLThumb, TGLThumb, CouseTLThumb, chbLeftThumb, VOtherLThumb);
        if (personfingerprints.getLIndex().isMissOrNot())
            setDataToScene(personfingerprints.getLIndex(), MenuLIndex, TGLIndex, CouseTLIndex, chbLeftIndex, VOtherLIndex);
        if (personfingerprints.getLMiddle().isMissOrNot())
            setDataToScene(personfingerprints.getLMiddle(), MenuLMiddle, TGLMiddle, CouseTLMiddle, chbLeftMiddle, VOtherLMiddle);
        if (personfingerprints.getLRing().isMissOrNot())
            setDataToScene(personfingerprints.getLRing(), MenuLRing, TGLRing, CouseTLRing, chbLeftRing, VOtherLRing);
        if (personfingerprints.getLLittle().isMissOrNot())
            setDataToScene(personfingerprints.getLLittle(), MenuLLittle, TGLLittle, CouseTLLittle, chbLeftLittle, VOtherLLittle);


    }

    private void setDataToScene(Fingerprint finger, MenuButton Couse, ToggleGroup TG, TextField CouseOther, CheckBox chBox, VBox VOther) {
        chBox.setSelected(true);
        if (finger.getCouse().equals("جرح")) {
            Couse.setText(finger.getCouse());
        } else {
            Couse.setText(resources.getString("Other"));
            VOther.setVisible(true);
            if (finger.getStatus() == 0)
                TG.getToggles().get(0).setSelected(true);
            else
                TG.getToggles().get(1).setSelected(true);

            CouseOther.setText(finger.getCouse());
        }

    }

    private boolean isEmpty(MenuButton menu, ToggleGroup TG, TextField Couse) {
        if (menu.getText().equals(resources.getString("Cause"))) {
            showWarningNotification(resources.getString("SelectCause"));
            return true;
        } else if (menu.getText().equals(resources.getString("Other"))) {

            if (TG.getSelectedToggle() == null) {
                showWarningNotification(resources.getString("SelectStatus"));
                return true;

            }
            if (Couse.getText().trim().isEmpty()) {
                showWarningNotification(resources.getString("WriteCause"));
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        personfingerprints = PerF;
        personfingerprints.setRThumb(new Fingerprint());
        personfingerprints.setRIndex(new Fingerprint());
        personfingerprints.setRMiddle(new Fingerprint());
        personfingerprints.setRRing(new Fingerprint());
        personfingerprints.setRLittle(new Fingerprint());

        personfingerprints.setLThumb(new Fingerprint());
        personfingerprints.setLIndex(new Fingerprint());
        personfingerprints.setLMiddle(new Fingerprint());
        personfingerprints.setLRing(new Fingerprint());
        personfingerprints.setLLittle(new Fingerprint());


        if (chbRightThumb.isSelected()) {
            if (isEmpty(MenuRThumb, TGRThumb, CouseTRThumb))
                return;

            addMFToPersonFPs(personfingerprints.getRThumb(), MenuRThumb, TGRThumb, CouseTRThumb);
        }
        if (chbRightIndex.isSelected()) {
            if (isEmpty(MenuRIndex, TGRIndex, CouseTRIndex))
                return;
            addMFToPersonFPs(personfingerprints.getRIndex(), MenuRIndex, TGRIndex, CouseTRIndex);
        }
        if (chbRightMiddle.isSelected()) {
            if (isEmpty(MenuRMiddle, TGRMiddle, CouseTRMiddle))
                return;
            addMFToPersonFPs(personfingerprints.getRMiddle(), MenuRMiddle, TGRMiddle, CouseTRMiddle);
        }
        if (chbRightRing.isSelected()) {
            if (isEmpty(MenuRRing, TGRRing, CouseTRRing))
                return;
            addMFToPersonFPs(personfingerprints.getRRing(), MenuRRing, TGRRing, CouseTRRing);
        }
        if (chbRightLittle.isSelected()) {
            if (isEmpty(MenuRLittle, TGRLittle, CouseTRLittle))
                return;
            addMFToPersonFPs(personfingerprints.getRLittle(), MenuRLittle, TGRLittle, CouseTRLittle);
        }


        if (chbLeftThumb.isSelected()) {
            if (isEmpty(MenuLThumb, TGLThumb, CouseTLThumb))
                return;
            addMFToPersonFPs(personfingerprints.getLThumb(), MenuLThumb, TGLThumb, CouseTLThumb);
        }
        if (chbLeftIndex.isSelected()) {
            if (isEmpty(MenuLIndex, TGLIndex, CouseTLIndex))
                return;
            addMFToPersonFPs(personfingerprints.getLIndex(), MenuLIndex, TGLIndex, CouseTLIndex);
        }
        if (chbLeftMiddle.isSelected()) {
            if (isEmpty(MenuLMiddle, TGLMiddle, CouseTLMiddle))
                return;
            addMFToPersonFPs(personfingerprints.getLMiddle(), MenuLMiddle, TGLMiddle, CouseTLMiddle);
        }
        if (chbLeftRing.isSelected()) {
            if (isEmpty(MenuLRing, TGLRing, CouseTLRing))
                return;
            addMFToPersonFPs(personfingerprints.getLRing(), MenuLRing, TGLRing, CouseTLRing);
        }
        if (chbLeftLittle.isSelected()) {
            if (isEmpty(MenuLLittle, TGLLittle, CouseTLLittle))
                return;
            addMFToPersonFPs(personfingerprints.getLLittle(), MenuLLittle, TGLLittle, CouseTLLittle);
        }

        super.onNextButtonClicked(actionEvent);
    }
}
