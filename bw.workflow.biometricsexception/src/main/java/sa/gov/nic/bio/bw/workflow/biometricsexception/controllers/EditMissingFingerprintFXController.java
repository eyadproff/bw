package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
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
import java.util.function.Consumer;

@FxmlFile("editMissingFingerprint22.fxml")
public class EditMissingFingerprintFXController extends WizardStepFxControllerBase {


    @Input
    private List<BioExclusion> personMissinfingerprints;
    @Input
    private List<Integer> MissingFingerPrints;

    @Output
    private List<BioExclusion> BioExclusionsList;
    //delete old after edit
    @Output
    private List<Integer> SeqNumbersList;

    //To Review
    @Output
    private PersonFingerprints Editedpersonfingerprints;


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
    private ComboBox<ComboBoxItem<Cause>> CMenuRThumb, CMenuRIndex, CMenuRMiddle, CMenuRRing, CMenuRLittle;
    @FXML
    private ComboBox<ComboBoxItem<Cause>> CMenuLThumb, CMenuLIndex, CMenuLMiddle, CMenuLRing, CMenuLLittle;

    @FXML
    private ToggleGroup TGRThumb, TGRIndex, TGRMiddle, TGRRing, TGRLittle;
    @FXML
    private ToggleGroup TGLThumb, TGLIndex, TGLMiddle, TGLRing, TGLLittle;

    @FXML
    private TextField CouseTRThumb, CouseTRIndex, CouseTRMiddle, CouseTRRing, CouseTRLittle;
    @FXML
    private TextField CouseTLThumb, CouseTLIndex, CouseTLMiddle, CouseTLRing, CouseTLLittle;
    @FXML
    private Button btnNext;
    @FXML
    private Label lblExcpiredExc;

    @SuppressWarnings("unchecked")
    private List<Cause> causes;
    private List<BioExclusion> expiredException;

    @Override
    protected void onAttachedToScene() {
        SeqNumbersList = new ArrayList<Integer>();
        expiredException = new ArrayList<BioExclusion>();

        if (personMissinfingerprints != null)
            for (BioExclusion bioEx : personMissinfingerprints) {
                if (bioEx.getExpireDate() != null && bioEx.getExpireDate() < Instant.now().getEpochSecond())
                    if (MissingFingerPrints != null && MissingFingerPrints.contains(bioEx.getPosition())) {
                        expiredException.add(bioEx);
                        personMissinfingerprints.remove(bioEx);
                    } else
                        SeqNumbersList.add(bioEx.getSeqNum());

            }
        if (!expiredException.isEmpty()) {
            lblExcpiredExc.setVisible(true);
            expiredException.forEach(x -> ShowExpiredException(x.getPosition()));
        }


        causes = (List<Cause>) Context.getUserSession().getAttribute(CausesLookup.KEY);

        AddItemsToMenu(CMenuRThumb, VOtherRThumb, CouseTRThumb, causes, TGRThumb);
        AddItemsToMenu(CMenuRIndex, VOtherRIndex, CouseTRIndex, causes, TGRIndex);
        AddItemsToMenu(CMenuRMiddle, VOtherRMiddle, CouseTRMiddle, causes, TGRMiddle);
        AddItemsToMenu(CMenuRRing, VOtherRRing, CouseTRRing, causes, TGRRing);
        AddItemsToMenu(CMenuRLittle, VOtherRLittle, CouseTRLittle, causes, TGRLittle);

        AddItemsToMenu(CMenuLThumb, VOtherLThumb, CouseTLThumb, causes, TGLThumb);
        AddItemsToMenu(CMenuLIndex, VOtherLIndex, CouseTLIndex, causes, TGLIndex);
        AddItemsToMenu(CMenuLMiddle, VOtherLMiddle, CouseTLMiddle, causes, TGLMiddle);
        AddItemsToMenu(CMenuLRing, VOtherLRing, CouseTLRing, causes, TGLRing);
        AddItemsToMenu(CMenuLLittle, VOtherLLittle, CouseTLLittle, causes, TGLLittle);

        if (isFirstLoad()) {
            Upload();
        }

        uploadMissingFPIfAny();
        CheckBoxAction();

    }

    private void Upload() {
        if (personMissinfingerprints != null) {
            Editedpersonfingerprints = new PersonFingerprints();

            Editedpersonfingerprints.setRThumb(getLast(personMissinfingerprints, 1));
            Editedpersonfingerprints.setRIndex(getLast(personMissinfingerprints, 2));
            Editedpersonfingerprints.setRMiddle(getLast(personMissinfingerprints, 3));
            Editedpersonfingerprints.setRRing(getLast(personMissinfingerprints, 4));
            Editedpersonfingerprints.setRLittle(getLast(personMissinfingerprints, 5));

            Editedpersonfingerprints.setLThumb(getLast(personMissinfingerprints, 6));
            Editedpersonfingerprints.setLIndex(getLast(personMissinfingerprints, 7));
            Editedpersonfingerprints.setLMiddle(getLast(personMissinfingerprints, 8));
            Editedpersonfingerprints.setLRing(getLast(personMissinfingerprints, 9));
            Editedpersonfingerprints.setLLittle(getLast(personMissinfingerprints, 10));


        } else {
            Editedpersonfingerprints = new PersonFingerprints();

            Editedpersonfingerprints.setRThumb(new Fingerprint());
            Editedpersonfingerprints.setRIndex(new Fingerprint());
            Editedpersonfingerprints.setRMiddle(new Fingerprint());
            Editedpersonfingerprints.setRRing(new Fingerprint());
            Editedpersonfingerprints.setRLittle(new Fingerprint());

            Editedpersonfingerprints.setLThumb(new Fingerprint());
            Editedpersonfingerprints.setLIndex(new Fingerprint());
            Editedpersonfingerprints.setLMiddle(new Fingerprint());
            Editedpersonfingerprints.setLLittle(new Fingerprint());
            Editedpersonfingerprints.setLRing(new Fingerprint());
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

            if (bioEx.getStatus() == 0 && bioEx.getPosition() == position) {
                fingerprint.setMissOrNot(true);
                for (Cause cause : causes) {
                    if (cause.getCauseId() == bioEx.getCasueId()) {
                        fingerprint.setCause(cause);
                        break;
                    }
                }
                if (bioEx.getCasueId() == 1) {
                    fingerprint.setDescription(bioEx.getDescription());
                }
              /*  if (bioEx.getExpireDate() > 0)
                    fingerprint.setStatus(1);//Temporary
                else
                    fingerprint.setStatus(0);*/
                //duration int bioex ==status in fingerprint
                if (bioEx.getMonth() == 0)
                    fingerprint.setStatus(0);
                else if (bioEx.getMonth() == 3)
                    fingerprint.setStatus(3);
                else if (bioEx.getMonth() == 6)
                    fingerprint.setStatus(6);
                else
                    fingerprint.setStatus(12);

                fingerprint.setPosition(position);
                fingerprint.setSeqNum(bioEx.getSeqNum());
                fingerprint.setAlreadyAdded(true);

                return fingerprint;
            }

        }

        return fingerprint;

    }

    @FXML
    private void CheckBoxAction() {

        int numChBox = 0;

        if (chbRightThumb.isSelected()) {
            numChBox++;
            VRightThumb.setDisable(false);
        } else {
            VRightThumb.setDisable(true);
        }
        if (chbRightIndex.isSelected()) {
            numChBox++;

            VRightIndexFinger.setDisable(false);
        } else {
            VRightIndexFinger.setDisable(true);
        }
        if (chbRightMiddle.isSelected()) {
            numChBox++;
            VRightMiddleFinger.setDisable(false);
        } else {
            VRightMiddleFinger.setDisable(true);
        }
        if (chbRightRing.isSelected()) {
            numChBox++;
            VRightRingFinger.setDisable(false);
        } else {
            VRightRingFinger.setDisable(true);
        }
        if (chbRightLittle.isSelected()) {
            numChBox++;
            VRightLittleFinger.setDisable(false);
        } else {
            VRightLittleFinger.setDisable(true);
        }

        if (chbLeftThumb.isSelected()) {
            numChBox++;
            VLeftThumb.setDisable(false);
        } else {
            VLeftThumb.setDisable(true);
        }
        if (chbLeftIndex.isSelected()) {
            numChBox++;
            VLeftIndexFinger.setDisable(false);
        } else {
            VLeftIndexFinger.setDisable(true);
        }
        if (chbLeftMiddle.isSelected()) {
            numChBox++;
            VLeftMiddleFinger.setDisable(false);
        } else {
            VLeftMiddleFinger.setDisable(true);
        }
        if (chbLeftRing.isSelected()) {
            numChBox++;
            VLeftRingFinger.setDisable(false);
        } else {
            VLeftRingFinger.setDisable(true);
        }
        if (chbLeftLittle.isSelected()) {
            numChBox++;
            VLeftLittleFinger.setDisable(false);
        } else {
            VLeftLittleFinger.setDisable(true);
        }

        if (numChBox > 0)
            btnNext.setDisable(false);


    }

    private void AddItemsToMenu(ComboBox<ComboBoxItem<Cause>> menu, VBox VOther, TextField TextCouse, List<Cause> causes, ToggleGroup TG) {
        List<Cause> CauseFEx = new ArrayList<Cause>();
        CauseFEx.addAll(causes);
        //
        CauseFEx.removeIf(cause -> cause.getCauseId() == 4);

        GuiUtils.addAutoCompletionSupportToComboBox(menu, CauseFEx);

        Consumer<ComboBoxItem<Cause>> consumer = item ->
        {
            Cause cause = item.getItem();

            String text;
            if (Context.getGuiLanguage() == GuiLanguage.ARABIC) text = cause.getArabicText();
            else text = cause.getEnglishText();

            String resultText = text.trim();
            item.setText(resultText);
        };

        menu.setConverter(new StringConverter<>() {
            @Override
            public String toString(ComboBoxItem<Cause> object) {
                if (object == null) return "";
                else return object.getText();
            }

            @Override
            public ComboBoxItem<Cause> fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;

                for (ComboBoxItem<Cause> causeComboBoxItem : menu.getItems()) {
                    if (string.equals(causeComboBoxItem.getText())) return causeComboBoxItem;
                }

                return null;
            }
        });

        menu.getItems().forEach(consumer);
        menu.setOnAction(e -> OnActionComboMenu(menu, VOther, TextCouse, TG));


    }

    private void OnActionComboMenu(ComboBox<ComboBoxItem<Cause>> menu, VBox VOther, TextField TextCouse, ToggleGroup TG) {
        if (menu.getValue().getItem().getCauseId() == 1) {
            TextCouse.setVisible(true);
            VOther.setVisible(true);
        } else if (menu.getValue().getItem().getCauseId() == 2) {
            TextCouse.setVisible(false);
            VOther.setVisible(false);
        } else {
            TextCouse.setVisible(false);
            VOther.setVisible(true);

        }

        // Low Quality never be Permanent
        if (menu.getValue().getItem().getCauseId() == 4) {
            ((RadioButton) TG.getToggles().get(3)).setDisable(true);
        } else
            ((RadioButton) TG.getToggles().get(3)).setDisable(false);


    }


    private void addMFToPersonFPs(Fingerprint finger, ComboBox<ComboBoxItem<Cause>> Couse, ToggleGroup TG, TextField CouseOther) {
//        finger.setMissOrNot(true);
//        if (Couse.getValue().getItem().getCauseId() != 1) {
//            finger.setCause(Couse.getValue().getItem());
//            //if Turncate then status =Permanent else one year
//            if (Couse.getValue().getItem().getCauseId() == 2) {
//                finger.setStatus(0);
//            } else
//                finger.setStatus(12);
//        }
//
//        else {
//            finger.setCause(Couse.getValue().getItem());
//            finger.setDescription(CouseOther.getText());
//            if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("3months")))
//                finger.setStatus(3);
//            else if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("6months")))
//                finger.setStatus(6);
//            else if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("oneYear")))
//                finger.setStatus(12);
//            else
//                finger.setStatus(0);
//           // finger.setStatus(((RadioButton) TG.getSelectedToggle()).getText());
//        }

        finger.setMissOrNot(true);
        finger.setCause(Couse.getValue().getItem());
        if (Couse.getValue().getItem().getCauseId() != 2) {

            if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("3months")))
                finger.setStatus(3);
            else if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("6months")))
                finger.setStatus(6);
            else if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("oneYear")))
                finger.setStatus(12);
            else
                finger.setStatus(0);

            if (Couse.getValue().getItem().getCauseId() == 1)
                finger.setDescription(CouseOther.getText());

        } else {
            finger.setStatus(0);

        }


    }

    private void uploadMissingFPIfAny() {

        if (Editedpersonfingerprints.getRThumb().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getRThumb(), CMenuRThumb, TGRThumb, CouseTRThumb, chbRightThumb, VOtherRThumb);
        if (Editedpersonfingerprints.getRIndex().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getRIndex(), CMenuRIndex, TGRIndex, CouseTRIndex, chbRightIndex, VOtherRIndex);
        if (Editedpersonfingerprints.getRMiddle().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getRMiddle(), CMenuRMiddle, TGRMiddle, CouseTRMiddle, chbRightMiddle, VOtherRMiddle);
        if (Editedpersonfingerprints.getRRing().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getRRing(), CMenuRRing, TGRRing, CouseTRRing, chbRightRing, VOtherRRing);
        if (Editedpersonfingerprints.getRLittle().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getRLittle(), CMenuRLittle, TGRLittle, CouseTRLittle, chbRightLittle, VOtherRLittle);

        if (Editedpersonfingerprints.getLThumb().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getLThumb(), CMenuLThumb, TGLThumb, CouseTLThumb, chbLeftThumb, VOtherLThumb);
        if (Editedpersonfingerprints.getLIndex().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getLIndex(), CMenuLIndex, TGLIndex, CouseTLIndex, chbLeftIndex, VOtherLIndex);
        if (Editedpersonfingerprints.getLMiddle().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getLMiddle(), CMenuLMiddle, TGLMiddle, CouseTLMiddle, chbLeftMiddle, VOtherLMiddle);
        if (Editedpersonfingerprints.getLRing().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getLRing(), CMenuLRing, TGLRing, CouseTLRing, chbLeftRing, VOtherLRing);
        if (Editedpersonfingerprints.getLLittle().isMissOrNot())
            setDataToScene(Editedpersonfingerprints.getLLittle(), CMenuLLittle, TGLLittle, CouseTLLittle, chbLeftLittle, VOtherLLittle);


    }

    private void setDataToScene(Fingerprint finger, ComboBox<ComboBoxItem<Cause>> Couse, ToggleGroup TG, TextField TextCouse, CheckBox chBox, VBox VOther) {

        chBox.setSelected(true);
        if (finger.getAlreadyAdded())
            chBox.setDisable(true);

        Couse.getItems()
                .stream()
                .filter(item -> item.getItem().equals(finger.getCause()))
                .findFirst()
                .ifPresent(Couse::setValue);

        if (finger.getCause().getCauseId() != 2) {
            VOther.setVisible(true);

            if (finger.getStatus() == 0)
                TG.getToggles().get(3).setSelected(true);
            else if (finger.getStatus() == 12)
                TG.getToggles().get(2).setSelected(true);
            else if (finger.getStatus() == 6)
                TG.getToggles().get(1).setSelected(true);
            else
                TG.getToggles().get(0).setSelected(true);


            if (finger.getCause().getCauseId() == 1) {
                TextCouse.setVisible(true);
                TextCouse.setText(finger.getDescription());
            }

        }


    }

    private boolean isEmpty(ComboBox<ComboBoxItem<Cause>> menu, ToggleGroup TG, TextField Couse) {
        if (menu.getValue() == null) {
            showWarningNotification(resources.getString("SelectCause"));
            return true;
        } else if (menu.getValue().getItem().getCauseId() != 2) {

            if (TG.getSelectedToggle() == null) {
                showWarningNotification(resources.getString("SelectStatus"));
                return true;

            }
            if (menu.getValue().getItem().getCauseId() == 1) {
                if (Couse.getText().trim().isEmpty()) {
                    showWarningNotification(resources.getString("WriteCause"));
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean canAddToList(Fingerprint finger, Integer position) {

        if (finger.getAlreadyAdded()) {
            if ((getLast(personMissinfingerprints, position)).getCause().equals(finger.getCause()))
                if (finger.getCause().getCauseId() == 1) {
                    if (finger.getDescription().equals((getLast(personMissinfingerprints, position)).getDescription()))
                        if ((getLast(personMissinfingerprints, position)).getStatus().equals(finger.getStatus()))
                            return false;
                } else if ((getLast(personMissinfingerprints, position)).getStatus().equals(finger.getStatus())) {
                    return false;
                }

        }


        return true;

    }

    private BioExclusion addToList(Fingerprint finger, Integer position) {
        BioExclusion bioEx = new BioExclusion();
        bioEx.setBioType(1);
        bioEx.setPosition(position);
        // -- epoch time by Second
        if (finger.getStatus() == 0) {
            // bioEx.setExpireDate(new Long(0));
            bioEx.setCreateDate(Instant.now().getEpochSecond());
            // bioEx.setMonth(0);
        } else if (finger.getStatus() == 3) {
            bioEx.setExpireDate(Instant.now().getEpochSecond() + new Long(7889238));
            bioEx.setCreateDate(Instant.now().getEpochSecond());
            bioEx.setMonth(3);
        } else if (finger.getStatus() == 6) {
            bioEx.setExpireDate(Instant.now().getEpochSecond() + new Long(15778476));
            bioEx.setCreateDate(Instant.now().getEpochSecond());
            bioEx.setMonth(6);
        } else {
            bioEx.setExpireDate(Instant.now().getEpochSecond() + new Long(31556952));
            bioEx.setCreateDate(Instant.now().getEpochSecond());
            bioEx.setMonth(12);
        }

        //need to edit
        bioEx.setCasueId(finger.getCause().getCauseId());
        if (bioEx.getCasueId() == 1)
            bioEx.setDescription(finger.getDescription());

        UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
        bioEx.setOperatorId(userInfo.getOperatorId());

        return bioEx;


    }

    @Override
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        BioExclusionsList = new ArrayList<BioExclusion>();


        if (chbRightThumb.isSelected()) {
            if (isEmpty(CMenuRThumb, TGRThumb, CouseTRThumb))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRThumb(), CMenuRThumb, TGRThumb, CouseTRThumb);
            if (canAddToList(Editedpersonfingerprints.getRThumb(), 1)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRThumb(), 1));
                if (Editedpersonfingerprints.getRThumb().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getRThumb().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setRThumb(new Fingerprint());
        if (chbRightIndex.isSelected()) {
            if (isEmpty(CMenuRIndex, TGRIndex, CouseTRIndex))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRIndex(), CMenuRIndex, TGRIndex, CouseTRIndex);
            if (canAddToList(Editedpersonfingerprints.getRIndex(), 2)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRIndex(), 2));
                if (Editedpersonfingerprints.getRIndex().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getRIndex().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setRIndex(new Fingerprint());
        if (chbRightMiddle.isSelected()) {
            if (isEmpty(CMenuRMiddle, TGRMiddle, CouseTRMiddle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRMiddle(), CMenuRMiddle, TGRMiddle, CouseTRMiddle);
            if (canAddToList(Editedpersonfingerprints.getRMiddle(), 3)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRMiddle(), 3));
                if (Editedpersonfingerprints.getRMiddle().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getRMiddle().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setRMiddle(new Fingerprint());
        if (chbRightRing.isSelected()) {
            if (isEmpty(CMenuRRing, TGRRing, CouseTRRing))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRRing(), CMenuRRing, TGRRing, CouseTRRing);
            if (canAddToList(Editedpersonfingerprints.getRRing(), 4)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRRing(), 4));
                if (Editedpersonfingerprints.getRRing().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getRRing().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setRRing(new Fingerprint());
        if (chbRightLittle.isSelected()) {
            if (isEmpty(CMenuRLittle, TGRLittle, CouseTRLittle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRLittle(), CMenuRLittle, TGRLittle, CouseTRLittle);
            if (canAddToList(Editedpersonfingerprints.getRLittle(), 5)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRLittle(), 5));
                if (Editedpersonfingerprints.getRLittle().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getRLittle().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setRLittle(new Fingerprint());


        if (chbLeftThumb.isSelected()) {
            if (isEmpty(CMenuLThumb, TGLThumb, CouseTLThumb))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLThumb(), CMenuLThumb, TGLThumb, CouseTLThumb);
            if (canAddToList(Editedpersonfingerprints.getLThumb(), 6)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLThumb(), 6));
                if (Editedpersonfingerprints.getLThumb().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getLThumb().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setLThumb(new Fingerprint());
        if (chbLeftIndex.isSelected()) {
            if (isEmpty(CMenuLIndex, TGLIndex, CouseTLIndex))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLIndex(), CMenuLIndex, TGLIndex, CouseTLIndex);
            if (canAddToList(Editedpersonfingerprints.getLIndex(), 7)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLIndex(), 7));
                if (Editedpersonfingerprints.getLIndex().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getLIndex().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setLIndex(new Fingerprint());
        if (chbLeftMiddle.isSelected()) {
            if (isEmpty(CMenuLMiddle, TGLMiddle, CouseTLMiddle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLMiddle(), CMenuLMiddle, TGLMiddle, CouseTLMiddle);
            if (canAddToList(Editedpersonfingerprints.getLMiddle(), 8)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLMiddle(), 8));
                if (Editedpersonfingerprints.getLMiddle().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getLMiddle().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setLMiddle(new Fingerprint());
        if (chbLeftRing.isSelected()) {
            if (isEmpty(CMenuLRing, TGLRing, CouseTLRing))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLRing(), CMenuLRing, TGLRing, CouseTLRing);
            if (canAddToList(Editedpersonfingerprints.getLRing(), 9)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLRing(), 9));
                if (Editedpersonfingerprints.getLRing().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getLRing().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setLRing(new Fingerprint());
        if (chbLeftLittle.isSelected()) {
            if (isEmpty(CMenuLLittle, TGLLittle, CouseTLLittle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLLittle(), CMenuLLittle, TGLLittle, CouseTLLittle);
            if (canAddToList(Editedpersonfingerprints.getLLittle(), 10)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLLittle(), 10));
                if (Editedpersonfingerprints.getLLittle().getAlreadyAdded()) {
                    SeqNumbersList.add(Editedpersonfingerprints.getLLittle().getSeqNum());
                }
            }
        } else Editedpersonfingerprints.setLLittle(new Fingerprint());


        if (BioExclusionsList.size() == 0) {
            showWarningNotification(resources.getString("NoEditOrAddMissingFP"));
            return;
        }

        BioExclusionsList.forEach(x -> {
            for (BioExclusion bio : expiredException) {
                if (bio.getPosition() == x.getPosition()) {
                    SeqNumbersList.add(bio.getSeqNum());
                    break;
                }
            }

        });


        super.onNextButtonClicked(actionEvent);
    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {

        Upload();

    }
}
