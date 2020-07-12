package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Fingerprint;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.PersonFingerprints;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.biometricsexception.controllers.ServiceTypeFXController.ServiceType;

import java.util.ArrayList;
import java.util.List;

@FxmlFile("reviewAndSubmit2.fxml")
public class ReviewAndSubmitFXController extends WizardStepFxControllerBase {

    @Input
    private PersonFingerprints Editedpersonfingerprints;
    @Input(alwaysRequired = true)
    private NormalizedPersonInfo normalizedPersonInfo;
    @Input(alwaysRequired = true)
    private ServiceType serviceType;
    @Input
    private List<BioExclusion> BioExclusionsList;

    @Output
    private List<BioExclusion> EditedBioExclusionsList;
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
        PersonName.setText(normalizedPersonInfo.getFirstName() + " " + normalizedPersonInfo.getFatherName() + " " +
                           normalizedPersonInfo.getFamilyName());
        PersonID.setText(normalizedPersonInfo.getPersonId().toString());


        checkMissingfingers();


    }

    private void checkMissingfingers() {
        int right = 0;
        int left = 0;
        if (Editedpersonfingerprints.getRThumb().isMissOrNot()) {
            right++;
            displayMissingFinger(Editedpersonfingerprints.getRThumb(), VRightThumb, svgRightThumb, RThumbCouse,
                    RThumbStatus);
        }
        if (Editedpersonfingerprints.getRIndex().isMissOrNot()) {
            right++;

            displayMissingFinger(Editedpersonfingerprints.getRIndex(), VRightIndexFinger, svgRightIndex, RIndexCouse,
                    RIndexStatus);
        }
        if (Editedpersonfingerprints.getRMiddle().isMissOrNot()) {
            right++;
            displayMissingFinger(Editedpersonfingerprints.getRMiddle(), VRightMiddleFinger, svgRightMiddle,
                    RMiddleCouse, RMiddleStatus);
        }
        if (Editedpersonfingerprints.getRRing().isMissOrNot()) {
            right++;
            displayMissingFinger(Editedpersonfingerprints.getRRing(), VRightRingFinger, svgRightRing, RRingCouse,
                    RRingStatus);
        }
        if (Editedpersonfingerprints.getRLittle().isMissOrNot()) {
            right++;
            displayMissingFinger(Editedpersonfingerprints.getRLittle(), VRightLittleFinger, svgRightLittle,
                    RLittleCouse, RLittleStatus);
        }

        if (Editedpersonfingerprints.getLThumb().isMissOrNot()) {
            left++;
            displayMissingFinger(Editedpersonfingerprints.getLThumb(), VLeftThumb, svgLeftThumb, LThumbCouse,
                    LThumbStatus);
        }
        if (Editedpersonfingerprints.getLIndex().isMissOrNot()) {
            left++;
            displayMissingFinger(Editedpersonfingerprints.getLIndex(), VLeftIndexFinger, svgLeftIndex, LIndexCouse,
                    LIndexStatus);
        }
        if (Editedpersonfingerprints.getLMiddle().isMissOrNot()) {
            left++;
            displayMissingFinger(Editedpersonfingerprints.getLMiddle(), VLeftMiddleFinger, svgLeftMiddle, LMiddleCouse,
                    LMiddleStatus);
        }
        if (Editedpersonfingerprints.getLRing().isMissOrNot()) {
            left++;
            displayMissingFinger(Editedpersonfingerprints.getLRing(), VLeftRingFinger, svgLeftRing, LRingCouse,
                    LRingStatus);
        }
        if (Editedpersonfingerprints.getLLittle().isMissOrNot()) {
            left++;
            displayMissingFinger(Editedpersonfingerprints.getLLittle(), VLeftLittleFinger, svgLeftLittle, LLittleCouse,
                    LLittleStatus);
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

        if (finger.getCause().getCauseId() == 1) { Couse.setText(finger.getDescription()); }
        else {
            if (Context.getGuiLanguage() == GuiLanguage.ARABIC) { Couse.setText(finger.getCause().getArabicText()); }
            else { Couse.setText(finger.getCause().getEnglishText()); }
        }

        if (finger.getStatus() == 0) { Status.setText(resources.getString("Permanent")); }
        else if (finger.getStatus() == 3) { Status.setText(resources.getString("3months")); }
        else if (finger.getStatus() == 6) { Status.setText(resources.getString("6months")); }
        else { Status.setText(resources.getString("oneYear")); }

    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        if (serviceType.equals(ServiceType.ADD_OR_EDIT_FINGERPRINTS)) {
            EditedBioExclusionsList = new ArrayList<BioExclusion>();
            for (BioExclusion bioex : BioExclusionsList) {
                bioex.setSamisId(normalizedPersonInfo.getPersonId());
                EditedBioExclusionsList.add(bioex);
            }
        }
        // super.onNextButtonClicked(actionEvent);
        continueWorkflow();
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) { goNext(); }
    }
}
