package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;

@FxmlFile("editMissingFingerprint.fxml")
public class EditMissingFingerprintFXController extends WizardStepFxControllerBase {

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


    @Override
    protected void onAttachedToScene() {
//        VRightThumb.getChildren().
        super.onAttachedToScene();
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


}
