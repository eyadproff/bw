package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Fingerprint;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.PersonFingerprints;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitFXController extends WizardStepFxControllerBase {

    @Input
    private PersonFingerprints personfingerprints;
    @Input
    private NormalizedPersonInfo normalizedPersonInfo;

    @FXML
    private Label PersonID, PersonName;

    @FXML
    private SVGPath svgRightLittle;
    @FXML
    private SVGPath svgRightRing;
    @FXML
    private SVGPath svgRightMiddle;
    @FXML
    private SVGPath svgRightIndex;
    @FXML
    private SVGPath svgRightThumb;

    @FXML
    private SVGPath svgLeftLittle;
    @FXML
    private SVGPath svgLeftRing;
    @FXML
    private SVGPath svgLeftMiddle;
    @FXML
    private SVGPath svgLeftIndex;
    @FXML
    private SVGPath svgLeftThumb;

    @FXML
    private VBox VRightThumb;
    @FXML
    private VBox VRightIndexFinger;
    @FXML
    private VBox VRightMiddleFinger;
    @FXML
    private VBox VRightRingFinger;
    @FXML
    private VBox VRightLittleFinger;

    @FXML
    private VBox VLeftThumb;
    @FXML
    private VBox VLeftIndexFinger;
    @FXML
    private VBox VLeftMiddleFinger;
    @FXML
    private VBox VLeftRingFinger;
    @FXML
    private VBox VLeftLittleFinger;

    @FXML
    private Label RThumbCouse, RThumbStatus;
    @FXML
    private Label RIndexCouse, RIndexStatus;
    @FXML
    private Label RMiddleCouse, RMiddleStatus;
    @FXML
    private Label RRingCouse, RRingStatus;
    @FXML
    private Label RLittleCouse, RLittleStatus;

    @FXML
    private Label LThumbCouse, LThumbStatus;
    @FXML
    private Label LIndexCouse, LIndexStatus;
    @FXML
    private Label LMiddleCouse, LMiddleStatus;
    @FXML
    private Label LRingCouse, LRingStatus;
    @FXML
    private Label LLittleCouse, LLittleStatus;

    @FXML
    private Label RightMExist, LeftMExist;

    @Override
    protected void onAttachedToScene() {
        PersonName.setText(normalizedPersonInfo.getFirstName() + " " + normalizedPersonInfo.getFatherName() + " " + normalizedPersonInfo.getFamilyName());
        PersonID.setText(normalizedPersonInfo.getPersonId().toString());


        checkMissingfingers();

    }

    private void checkMissingfingers() {
        int right = 0;
        int left = 0;
        if (personfingerprints.getRThumb().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRThumb(), VRightThumb, svgRightThumb, RThumbCouse, RThumbStatus);
        }
        if (personfingerprints.getRIndex().isMissOrNot()) {
            right++;

            displayMissingFinger(personfingerprints.getRIndex(), VRightIndexFinger, svgRightIndex, RIndexCouse, RIndexStatus);
        }
        if (personfingerprints.getRMiddle().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRMiddle(), VRightMiddleFinger, svgRightMiddle, RMiddleCouse, RMiddleStatus);
        }
        if (personfingerprints.getRRing().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRRing(), VRightRingFinger, svgRightRing, RRingCouse, RRingStatus);
        }
        if (personfingerprints.getRLittle().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRLittle(), VRightLittleFinger, svgRightLittle, RLittleCouse, RLittleStatus);
        }

        if (personfingerprints.getLThumb().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLThumb(), VLeftThumb, svgLeftThumb, LThumbCouse, LThumbStatus);
        }
        if (personfingerprints.getLIndex().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLIndex(), VLeftIndexFinger, svgLeftIndex, LIndexCouse, LIndexStatus);
        }
        if (personfingerprints.getLMiddle().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLMiddle(), VLeftMiddleFinger, svgLeftMiddle, LMiddleCouse, LMiddleStatus);
        }
        if (personfingerprints.getLRing().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLRing(), VLeftRingFinger, svgLeftRing, LRingCouse, LRingStatus);
        }
        if (personfingerprints.getLLittle().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLLittle(), VLeftLittleFinger, svgLeftLittle, LLittleCouse, LLittleStatus);
        }
        if (right == 0) {
            RightMExist.setVisible(true);
            RightMExist.setManaged(true);

        }
        if (left == 0) {
            LeftMExist.setVisible(true);
            LeftMExist.setManaged(true);
        }


    }

    private void displayMissingFinger(Fingerprint finger, VBox vbox, SVGPath svg, Label Couse, Label Status) {
        vbox.setVisible(true);
        vbox.setManaged(true);

        svg.setVisible(true);
        svg.setManaged(true);

        Couse.setText(finger.getCouse());

        if (finger.getStatus() == 0)
            Status.setText(resources.getString("Permanent"));
        else
            Status.setText(resources.getString("Temporary"));

    }

}
