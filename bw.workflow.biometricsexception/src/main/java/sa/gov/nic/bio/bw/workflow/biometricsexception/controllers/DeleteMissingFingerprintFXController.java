package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Cause;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Fingerprint;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.PersonFingerprints;
import sa.gov.nic.bio.bw.workflow.biometricsexception.lookups.CausesLookup;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FxmlFile("deleteMissingFingerprint2.fxml")
public class DeleteMissingFingerprintFXController extends WizardStepFxControllerBase {


    @Input
    private List<BioExclusion> BioExclusionsList;

    @Input
    private List<Integer> MissingFingerPrints;


    @Output
    private PersonFingerprints Editedpersonfingerprints;
    @Output
    private List<Integer> SeqNumbersList;

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
    private CheckBox chbRightHand;

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
    private CheckBox chbLeftHand;

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
    private Label RightMExist, LeftMExist, lblExcpiredExc;

    @FXML
    private Button btnNext;

    @SuppressWarnings("unchecked")
    private List<Cause> causes = (List<Cause>) Context.getUserSession().getAttribute(CausesLookup.KEY);
    private PersonFingerprints personfingerprints;
    private List<BioExclusion> expiredException;

    @Override
    protected void onAttachedToScene() {

        SeqNumbersList = new ArrayList<>();
        expiredException = new ArrayList<>();

        // causes = (List<Cause>) Context.getUserSession().getAttribute(CausesLookup.KEY);

        if (BioExclusionsList != null) {

            //remove expired Exception
            BioExclusionsList.removeIf(bioEx -> {
                if (bioEx.getExpireDate() != null && bioEx.getExpireDate() < Instant.now().getEpochSecond()) {
                    if (MissingFingerPrints != null && MissingFingerPrints.contains(bioEx.getPosition())) {
                        expiredException.add(bioEx);
                    }
                    else {
                        SeqNumbersList.add(bioEx.getSeqNum());
                    }
                    return true;

                }
                return false;
            });

            personfingerprints = new PersonFingerprints();
            personfingerprints.setRThumb(getLast(BioExclusionsList, 1));
            personfingerprints.setRIndex(getLast(BioExclusionsList, 2));
            personfingerprints.setRMiddle(getLast(BioExclusionsList, 3));
            personfingerprints.setRRing(getLast(BioExclusionsList, 4));
            personfingerprints.setRLittle(getLast(BioExclusionsList, 5));

            personfingerprints.setLThumb(getLast(BioExclusionsList, 6));
            personfingerprints.setLIndex(getLast(BioExclusionsList, 7));
            personfingerprints.setLMiddle(getLast(BioExclusionsList, 8));
            personfingerprints.setLRing(getLast(BioExclusionsList, 9));
            personfingerprints.setLLittle(getLast(BioExclusionsList, 10));

            checkMissingfingers();

        }
        else {
            RightMExist.setVisible(true);
            RightMExist.setManaged(true);

            LeftMExist.setVisible(true);
            LeftMExist.setManaged(true);
        }

        //To know what fingerException expired
        if (!expiredException.isEmpty()) {
            lblExcpiredExc.setVisible(true);
            expiredException.forEach(x -> ShowExpiredException(x.getPosition()));
        }

        // old state if return from Review
        if (Editedpersonfingerprints != null) {
            checkDeletedMFP();
        }

    }

    private void ShowExpiredException(Integer Position) {
        switch (Position) {
            case 1:
                FillSVG(svgRightThumb);
                break;
            case 2:
                FillSVG(svgRightIndex);
                break;
            case 3:
                FillSVG(svgRightMiddle);
                break;
            case 4:
                FillSVG(svgRightRing);
                break;
            case 5:
                FillSVG(svgRightLittle);
                break;
            case 6:
                FillSVG(svgLeftThumb);
                break;
            case 7:
                FillSVG(svgLeftIndex);
                break;
            case 8:
                FillSVG(svgLeftMiddle);
                break;
            case 9:
                FillSVG(svgLeftRing);
                break;
            case 10:
                FillSVG(svgLeftLittle);
                break;
            default:
                break;
        }
    }

    private void FillSVG(SVGPath svg) {
        svg.setVisible(true);
        svg.setManaged(true);
        svg.setFill(Color.RED);
    }

    private Fingerprint getLast(List<BioExclusion> personMissinfingerprints, Integer position) {
        Fingerprint fingerprint = new Fingerprint();

        for (BioExclusion bioEx : personMissinfingerprints) {

            if (bioEx.getStatus().equals(0) && bioEx.getPosition().equals(position)) {
                fingerprint.setMissOrNot(true);
                //lookup
                for (Cause cause : causes) {
                    if (cause.getCauseId().equals(bioEx.getCasueId())) {
                        fingerprint.setCause(cause);
                        break;
                    }
                }
                if (bioEx.getCasueId() == 1) {
                    fingerprint.setDescription(bioEx.getDescription());
                }
                //                if (bioEx.getExpireDate() > 0)
                //                    fingerprint.setStatus(1);//Temporary
                //                else
                //                    fingerprint.setStatus(0);


                if (bioEx.getMonth() == null || bioEx.getMonth() == 0) { fingerprint.setStatus(0); }
                else if (bioEx.getMonth() == 3) { fingerprint.setStatus(3); }
                else if (bioEx.getMonth() == 6) { fingerprint.setStatus(6); }
                else { fingerprint.setStatus(12); }

                fingerprint.setPosition(position);
                fingerprint.setSeqNum(bioEx.getSeqNum());

                fingerprint.setAlreadyAdded(true);

                return fingerprint;
            }

        }

        return fingerprint;

    }

    private void checkMissingfingers() {
        int right = 0;
        int left = 0;
        if (personfingerprints.getRThumb().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRThumb(), VRightThumb, svgRightThumb, RThumbCouse, RThumbStatus);
            chbRightThumb.setDisable(false);
            chbRightHand.setDisable(false);
        }
        if (personfingerprints.getRIndex().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRIndex(), VRightIndexFinger, svgRightIndex, RIndexCouse,
                    RIndexStatus);
            chbRightIndex.setDisable(false);
            chbRightHand.setDisable(false);
        }
        if (personfingerprints.getRMiddle().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRMiddle(), VRightMiddleFinger, svgRightMiddle, RMiddleCouse,
                    RMiddleStatus);
            chbRightMiddle.setDisable(false);
            chbRightHand.setDisable(false);
        }
        if (personfingerprints.getRRing().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRRing(), VRightRingFinger, svgRightRing, RRingCouse,
                    RRingStatus);
            chbRightRing.setDisable(false);
            chbRightHand.setDisable(false);
        }
        if (personfingerprints.getRLittle().isMissOrNot()) {
            right++;
            displayMissingFinger(personfingerprints.getRLittle(), VRightLittleFinger, svgRightLittle, RLittleCouse,
                    RLittleStatus);
            chbRightLittle.setDisable(false);
            chbRightHand.setDisable(false);
        }

        if (personfingerprints.getLThumb().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLThumb(), VLeftThumb, svgLeftThumb, LThumbCouse, LThumbStatus);
            chbLeftThumb.setDisable(false);
            chbLeftHand.setDisable(false);
        }
        if (personfingerprints.getLIndex().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLIndex(), VLeftIndexFinger, svgLeftIndex, LIndexCouse,
                    LIndexStatus);
            chbLeftIndex.setDisable(false);
            chbLeftHand.setDisable(false);
        }
        if (personfingerprints.getLMiddle().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLMiddle(), VLeftMiddleFinger, svgLeftMiddle, LMiddleCouse,
                    LMiddleStatus);
            chbLeftMiddle.setDisable(false);
            chbLeftHand.setDisable(false);
        }
        if (personfingerprints.getLRing().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLRing(), VLeftRingFinger, svgLeftRing, LRingCouse, LRingStatus);
            chbLeftRing.setDisable(false);
            chbLeftHand.setDisable(false);
        }
        if (personfingerprints.getLLittle().isMissOrNot()) {
            left++;
            displayMissingFinger(personfingerprints.getLLittle(), VLeftLittleFinger, svgLeftLittle, LLittleCouse,
                    LLittleStatus);
            chbLeftLittle.setDisable(false);
            chbLeftHand.setDisable(false);
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

    private void checkDeletedMFP() {

        if (personfingerprints.getRThumb().isMissOrNot() && !Editedpersonfingerprints.getRThumb().isMissOrNot()) {
            chbRightThumb.setSelected(true);
        }
        if (personfingerprints.getRIndex().isMissOrNot() && !Editedpersonfingerprints.getRIndex().isMissOrNot()) {
            chbRightIndex.setSelected(true);
        }
        if (personfingerprints.getRMiddle().isMissOrNot() && !Editedpersonfingerprints.getRMiddle().isMissOrNot()) {

            chbRightMiddle.setSelected(true);
        }
        if (personfingerprints.getRRing().isMissOrNot() && !Editedpersonfingerprints.getRRing().isMissOrNot()) {

            chbRightRing.setSelected(true);
        }
        if (personfingerprints.getRLittle().isMissOrNot() && !Editedpersonfingerprints.getRLittle().isMissOrNot()) {

            chbRightLittle.setSelected(true);
        }

        if (personfingerprints.getLThumb().isMissOrNot() && !Editedpersonfingerprints.getLThumb().isMissOrNot()) {

            chbLeftThumb.setSelected(true);
        }
        if (personfingerprints.getLIndex().isMissOrNot() && !Editedpersonfingerprints.getLIndex().isMissOrNot()) {

            chbLeftIndex.setSelected(true);
        }
        if (personfingerprints.getLMiddle().isMissOrNot() && !Editedpersonfingerprints.getLMiddle().isMissOrNot()) {

            chbLeftMiddle.setSelected(true);
        }
        if (personfingerprints.getLRing().isMissOrNot() && !Editedpersonfingerprints.getLRing().isMissOrNot()) {

            chbLeftRing.setSelected(true);
        }
        if (personfingerprints.getLLittle().isMissOrNot() && !Editedpersonfingerprints.getLLittle().isMissOrNot()) {

            chbLeftLittle.setSelected(true);
        }

        CheckBoxAction();

    }

    @FXML
    private void CheckBoxAction() {
        int numChBox = 0;

        if (chbRightThumb.isSelected()) {
            VRightThumb.setStyle("-fx-border-color:red");
            numChBox++;
        }
        else {
            VRightThumb.setStyle("-fx-border-color:gray");
            chbRightHand.setSelected(false);

        }
        if (chbRightIndex.isSelected()) {
            numChBox++;
            VRightIndexFinger.setStyle("-fx-border-color:red");
        }
        else {
            VRightIndexFinger.setStyle("-fx-border-color:gray");
            chbRightHand.setSelected(false);
        }
        if (chbRightMiddle.isSelected()) {
            numChBox++;
            VRightMiddleFinger.setStyle("-fx-border-color:red");
        }
        else {
            VRightMiddleFinger.setStyle("-fx-border-color:gray");
            chbRightHand.setSelected(false);
        }
        if (chbRightRing.isSelected()) {
            numChBox++;
            VRightRingFinger.setStyle("-fx-border-color:red");
        }
        else {
            VRightRingFinger.setStyle("-fx-border-color:gray");
            chbRightHand.setSelected(false);
        }
        if (chbRightLittle.isSelected()) {
            numChBox++;
            VRightLittleFinger.setStyle("-fx-border-color:red");
        }
        else {
            VRightLittleFinger.setStyle("-fx-border-color:gray");
            chbRightHand.setSelected(false);
        }


        if (chbLeftThumb.isSelected()) {
            numChBox++;
            VLeftThumb.setStyle("-fx-border-color:red");
        }
        else {
            VLeftThumb.setStyle("-fx-border-color:gray");
            chbLeftHand.setSelected(false);
        }
        if (chbLeftIndex.isSelected()) {
            numChBox++;
            VLeftIndexFinger.setStyle("-fx-border-color:red");
        }
        else {
            VLeftIndexFinger.setStyle("-fx-border-color:gray");
            chbLeftHand.setSelected(false);
        }
        if (chbLeftMiddle.isSelected()) {
            numChBox++;
            VLeftMiddleFinger.setStyle("-fx-border-color:red");
        }
        else {
            VLeftMiddleFinger.setStyle("-fx-border-color:gray");
            chbLeftHand.setSelected(false);
        }
        if (chbLeftRing.isSelected()) {
            numChBox++;
            VLeftRingFinger.setStyle("-fx-border-color:red");
        }
        else {
            VLeftRingFinger.setStyle("-fx-border-color:gray");
            chbLeftHand.setSelected(false);
        }
        if (chbLeftLittle.isSelected()) {
            numChBox++;
            VLeftLittleFinger.setStyle("-fx-border-color:red");
        }
        else {
            VLeftLittleFinger.setStyle("-fx-border-color:gray");
            chbLeftHand.setSelected(false);
        }

        if (numChBox > 0) { btnNext.setDisable(false); }
        else { btnNext.setDisable(true); }

    }

    @FXML
    private void CheckFullHandBoxAction() {
        if (chbRightHand.isSelected()) {
            if (!chbRightThumb.isDisable()) { chbRightThumb.setSelected(true); }
            if (!chbRightIndex.isDisable()) { chbRightIndex.setSelected(true); }
            if (!chbRightMiddle.isDisable()) { chbRightMiddle.setSelected(true); }
            if (!chbRightRing.isDisable()) { chbRightRing.setSelected(true); }
            if (!chbRightLittle.isDisable()) { chbRightLittle.setSelected(true); }

        }
        else {
            if (!chbRightThumb.isDisable()) { chbRightThumb.setSelected(false); }
            if (!chbRightIndex.isDisable()) { chbRightIndex.setSelected(false); }
            if (!chbRightMiddle.isDisable()) { chbRightMiddle.setSelected(false); }
            if (!chbRightRing.isDisable()) { chbRightRing.setSelected(false); }
            if (!chbRightLittle.isDisable()) { chbRightLittle.setSelected(false); }
        }

        if (chbLeftHand.isSelected()) {
            if (!chbLeftThumb.isDisable()) { chbLeftThumb.setSelected(true); }
            if (!chbLeftIndex.isDisable()) { chbLeftIndex.setSelected(true); }
            if (!chbLeftMiddle.isDisable()) { chbLeftMiddle.setSelected(true); }
            if (!chbLeftRing.isDisable()) { chbLeftRing.setSelected(true); }
            if (!chbLeftLittle.isDisable()) { chbLeftLittle.setSelected(true); }

        }
        else {
            if (!chbLeftThumb.isDisable()) { chbLeftThumb.setSelected(false); }
            if (!chbLeftIndex.isDisable()) { chbLeftIndex.setSelected(false); }
            if (!chbLeftMiddle.isDisable()) { chbLeftMiddle.setSelected(false); }
            if (!chbLeftRing.isDisable()) { chbLeftRing.setSelected(false); }
            if (!chbLeftLittle.isDisable()) { chbLeftLittle.setSelected(false); }
        }
        CheckBoxAction();
    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {

        Editedpersonfingerprints = new PersonFingerprints();

        if (chbRightThumb.isSelected()) {
            SeqNumbersList.add(personfingerprints.getRThumb().getSeqNum());
            Editedpersonfingerprints.setRThumb(new Fingerprint());
        }
        else { Editedpersonfingerprints.setRThumb(personfingerprints.getRThumb()); }

        if (chbRightIndex.isSelected()) {
            SeqNumbersList.add(personfingerprints.getRIndex().getSeqNum());
            Editedpersonfingerprints.setRIndex(new Fingerprint());
        }
        else { Editedpersonfingerprints.setRIndex(personfingerprints.getRIndex()); }

        if (chbRightMiddle.isSelected()) {
            SeqNumbersList.add(personfingerprints.getRMiddle().getSeqNum());
            Editedpersonfingerprints.setRMiddle(new Fingerprint());
        }
        else { Editedpersonfingerprints.setRMiddle(personfingerprints.getRMiddle()); }

        if (chbRightRing.isSelected()) {
            SeqNumbersList.add(personfingerprints.getRRing().getSeqNum());
            Editedpersonfingerprints.setRRing(new Fingerprint());
        }
        else { Editedpersonfingerprints.setRRing(personfingerprints.getRRing()); }

        if (chbRightLittle.isSelected()) {
            SeqNumbersList.add(personfingerprints.getRLittle().getSeqNum());
            Editedpersonfingerprints.setRLittle(new Fingerprint());
        }
        else { Editedpersonfingerprints.setRLittle(personfingerprints.getRLittle()); }


        if (chbLeftThumb.isSelected()) {
            SeqNumbersList.add(personfingerprints.getLThumb().getSeqNum());
            Editedpersonfingerprints.setLThumb(new Fingerprint());
        }
        else { Editedpersonfingerprints.setLThumb(personfingerprints.getLThumb()); }

        if (chbLeftIndex.isSelected()) {
            SeqNumbersList.add(personfingerprints.getLIndex().getSeqNum());
            Editedpersonfingerprints.setLIndex(new Fingerprint());
        }
        else { Editedpersonfingerprints.setLIndex(personfingerprints.getLIndex()); }

        if (chbLeftMiddle.isSelected()) {
            SeqNumbersList.add(personfingerprints.getLMiddle().getSeqNum());
            Editedpersonfingerprints.setLMiddle(new Fingerprint());
        }
        else { Editedpersonfingerprints.setLMiddle(personfingerprints.getLMiddle()); }

        if (chbLeftRing.isSelected()) {
            SeqNumbersList.add(personfingerprints.getLRing().getSeqNum());
            Editedpersonfingerprints.setLRing(new Fingerprint());
        }
        else { Editedpersonfingerprints.setLRing(personfingerprints.getLRing()); }

        if (chbLeftLittle.isSelected()) {
            SeqNumbersList.add(personfingerprints.getLLittle().getSeqNum());
            Editedpersonfingerprints.setLLittle(new Fingerprint());
        }
        else { Editedpersonfingerprints.setLLittle(personfingerprints.getLLittle()); }


        super.onNextButtonClicked(actionEvent);
    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {

        Editedpersonfingerprints = null;
    }


}
