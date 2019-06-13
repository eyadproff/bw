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
    private ComboBox CMenuRThumb, CMenuRIndex, CMenuRMiddle, CMenuRRing, CMenuRLittle;
    @FXML
    private ComboBox CMenuLThumb, CMenuLIndex, CMenuLMiddle, CMenuLRing, CMenuLLittle;

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

        AddItemsToMenu(CMenuRThumb, VOtherRThumb);
        AddItemsToMenu(CMenuRIndex, VOtherRIndex);
        AddItemsToMenu(CMenuRMiddle, VOtherRMiddle);
        AddItemsToMenu(CMenuRRing, VOtherRRing);
        AddItemsToMenu(CMenuRLittle, VOtherRLittle);

        AddItemsToMenu(CMenuLThumb, VOtherLThumb);
        AddItemsToMenu(CMenuLIndex, VOtherLIndex);
        AddItemsToMenu(CMenuLMiddle, VOtherLMiddle);
        AddItemsToMenu(CMenuLRing, VOtherLRing);
        AddItemsToMenu(CMenuLLittle, VOtherLLittle);

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

    private void AddItemsToMenu(ComboBox menu, VBox VOther) {

        menu.getItems().addAll(resources.getString("Other"), "جرح");
        menu.setValue(resources.getString("Cause"));
        menu.setOnAction(e -> OnActionComboMenu(menu, VOther));


    }

    private void OnActionComboMenu(ComboBox menu, VBox VOther) {
        if (menu.getValue().toString().equals(resources.getString("Other"))) {
            VOther.setVisible(true);
        } else
            VOther.setVisible(false);

    }


    private void addMFToPersonFPs(Fingerprint finger, ComboBox Couse, ToggleGroup TG, TextField CouseOther) {
        finger.setMissOrNot(true);
        if (!Couse.getValue().toString().equals(resources.getString("Other"))) {
            finger.setCouse(Couse.getValue().toString());
            finger.setStatus(1);
        } else {
            finger.setCouse(CouseOther.getText());
            if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("Temporary")))
                finger.setStatus(1);
            else
                finger.setStatus(0);
        }
    }

    private void uploadMissingFPIfAny() {

        if (personfingerprints.getRThumb().isMissOrNot())
            setDataToScene(personfingerprints.getRThumb(), CMenuRThumb, TGRThumb, CouseTRThumb, chbRightThumb, VOtherRThumb);
        if (personfingerprints.getRIndex().isMissOrNot())
            setDataToScene(personfingerprints.getRIndex(), CMenuRIndex, TGRIndex, CouseTRIndex, chbRightIndex, VOtherRIndex);
        if (personfingerprints.getRMiddle().isMissOrNot())
            setDataToScene(personfingerprints.getRMiddle(), CMenuRMiddle, TGRMiddle, CouseTRMiddle, chbRightMiddle, VOtherRMiddle);
        if (personfingerprints.getRRing().isMissOrNot())
            setDataToScene(personfingerprints.getRRing(), CMenuRRing, TGRRing, CouseTRRing, chbRightRing, VOtherRRing);
        if (personfingerprints.getRLittle().isMissOrNot())
            setDataToScene(personfingerprints.getRLittle(), CMenuRLittle, TGRLittle, CouseTRLittle, chbRightLittle, VOtherRLittle);

        if (personfingerprints.getLThumb().isMissOrNot())
            setDataToScene(personfingerprints.getLThumb(), CMenuLThumb, TGLThumb, CouseTLThumb, chbLeftThumb, VOtherLThumb);
        if (personfingerprints.getLIndex().isMissOrNot())
            setDataToScene(personfingerprints.getLIndex(), CMenuLIndex, TGLIndex, CouseTLIndex, chbLeftIndex, VOtherLIndex);
        if (personfingerprints.getLMiddle().isMissOrNot())
            setDataToScene(personfingerprints.getLMiddle(), CMenuLMiddle, TGLMiddle, CouseTLMiddle, chbLeftMiddle, VOtherLMiddle);
        if (personfingerprints.getLRing().isMissOrNot())
            setDataToScene(personfingerprints.getLRing(), CMenuLRing, TGLRing, CouseTLRing, chbLeftRing, VOtherLRing);
        if (personfingerprints.getLLittle().isMissOrNot())
            setDataToScene(personfingerprints.getLLittle(), CMenuLLittle, TGLLittle, CouseTLLittle, chbLeftLittle, VOtherLLittle);


    }

    private void setDataToScene(Fingerprint finger, ComboBox Couse, ToggleGroup TG, TextField CouseOther, CheckBox chBox, VBox VOther) {
        chBox.setSelected(true);
        if (finger.getCouse().equals("جرح")) {
            Couse.getSelectionModel().select(finger.getCouse());
        } else {
            Couse.getSelectionModel().select(resources.getString("Other"));
            VOther.setVisible(true);
            if (finger.getStatus() == 0)
                TG.getToggles().get(0).setSelected(true);
            else
                TG.getToggles().get(1).setSelected(true);

            CouseOther.setText(finger.getCouse());
        }

    }

    private boolean isEmpty(ComboBox menu, ToggleGroup TG, TextField Couse) {
        if (menu.getValue().toString().equals(resources.getString("Cause"))) {
            showWarningNotification(resources.getString("SelectCause"));
            return true;
        } else if (menu.getValue().toString().equals(resources.getString("Other"))) {

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
            if (isEmpty(CMenuRThumb, TGRThumb, CouseTRThumb))
                return;

            addMFToPersonFPs(personfingerprints.getRThumb(), CMenuRThumb, TGRThumb, CouseTRThumb);
        }
        if (chbRightIndex.isSelected()) {
            if (isEmpty(CMenuRIndex, TGRIndex, CouseTRIndex))
                return;
            addMFToPersonFPs(personfingerprints.getRIndex(), CMenuRIndex, TGRIndex, CouseTRIndex);
        }
        if (chbRightMiddle.isSelected()) {
            if (isEmpty(CMenuRMiddle, TGRMiddle, CouseTRMiddle))
                return;
            addMFToPersonFPs(personfingerprints.getRMiddle(), CMenuRMiddle, TGRMiddle, CouseTRMiddle);
        }
        if (chbRightRing.isSelected()) {
            if (isEmpty(CMenuRRing, TGRRing, CouseTRRing))
                return;
            addMFToPersonFPs(personfingerprints.getRRing(), CMenuRRing, TGRRing, CouseTRRing);
        }
        if (chbRightLittle.isSelected()) {
            if (isEmpty(CMenuRLittle, TGRLittle, CouseTRLittle))
                return;
            addMFToPersonFPs(personfingerprints.getRLittle(), CMenuRLittle, TGRLittle, CouseTRLittle);
        }


        if (chbLeftThumb.isSelected()) {
            if (isEmpty(CMenuLThumb, TGLThumb, CouseTLThumb))
                return;
            addMFToPersonFPs(personfingerprints.getLThumb(), CMenuLThumb, TGLThumb, CouseTLThumb);
        }
        if (chbLeftIndex.isSelected()) {
            if (isEmpty(CMenuLIndex, TGLIndex, CouseTLIndex))
                return;
            addMFToPersonFPs(personfingerprints.getLIndex(), CMenuLIndex, TGLIndex, CouseTLIndex);
        }
        if (chbLeftMiddle.isSelected()) {
            if (isEmpty(CMenuLMiddle, TGLMiddle, CouseTLMiddle))
                return;
            addMFToPersonFPs(personfingerprints.getLMiddle(), CMenuLMiddle, TGLMiddle, CouseTLMiddle);
        }
        if (chbLeftRing.isSelected()) {
            if (isEmpty(CMenuLRing, TGLRing, CouseTLRing))
                return;
            addMFToPersonFPs(personfingerprints.getLRing(), CMenuLRing, TGLRing, CouseTLRing);
        }
        if (chbLeftLittle.isSelected()) {
            if (isEmpty(CMenuLLittle, TGLLittle, CouseTLLittle))
                return;
            addMFToPersonFPs(personfingerprints.getLLittle(), CMenuLLittle, TGLLittle, CouseTLLittle);
        }

        super.onNextButtonClicked(actionEvent);
    }
}
