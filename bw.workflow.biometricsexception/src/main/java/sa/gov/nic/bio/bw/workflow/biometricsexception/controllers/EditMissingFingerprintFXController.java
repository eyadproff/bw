package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@FxmlFile("editMissingFingerprint.fxml")
public class EditMissingFingerprintFXController extends WizardStepFxControllerBase {


    @Input
    private List<BioExclusion> personMissinfingerprints;

    @Output
    private List<BioExclusion> BioExclusionsList;

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

    @SuppressWarnings("unchecked")
    private List<Cause> causes;

    @Override
    protected void onAttachedToScene() {

        causes = (List<Cause>) Context.getUserSession().getAttribute(CausesLookup.KEY);


        AddItemsToMenu(CMenuRThumb, VOtherRThumb, causes);
        AddItemsToMenu(CMenuRIndex, VOtherRIndex, causes);
        AddItemsToMenu(CMenuRMiddle, VOtherRMiddle, causes);
        AddItemsToMenu(CMenuRRing, VOtherRRing, causes);
        AddItemsToMenu(CMenuRLittle, VOtherRLittle, causes);

        AddItemsToMenu(CMenuLThumb, VOtherLThumb, causes);
        AddItemsToMenu(CMenuLIndex, VOtherLIndex, causes);
        AddItemsToMenu(CMenuLMiddle, VOtherLMiddle, causes);
        AddItemsToMenu(CMenuLRing, VOtherLRing, causes);
        AddItemsToMenu(CMenuLLittle, VOtherLLittle, causes);

        if (isFirstLoad()) {
            Upload();
        }

        uploadMissingFPIfAny();
        CheckBoxAction();

    }

    private void Upload() {
        if (personMissinfingerprints != null) {
            Editedpersonfingerprints = new PersonFingerprints();

            Editedpersonfingerprints.setRThumb(getLast(personMissinfingerprints, 0));
            Editedpersonfingerprints.setRIndex(getLast(personMissinfingerprints, 1));
            Editedpersonfingerprints.setRMiddle(getLast(personMissinfingerprints, 2));
            Editedpersonfingerprints.setRRing(getLast(personMissinfingerprints, 3));
            Editedpersonfingerprints.setRLittle(getLast(personMissinfingerprints, 4));

            Editedpersonfingerprints.setLThumb(getLast(personMissinfingerprints, 5));
            Editedpersonfingerprints.setLIndex(getLast(personMissinfingerprints, 6));
            Editedpersonfingerprints.setLMiddle(getLast(personMissinfingerprints, 7));
            Editedpersonfingerprints.setLLittle(getLast(personMissinfingerprints, 8));
            Editedpersonfingerprints.setLRing(getLast(personMissinfingerprints, 9));


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
                if (bioEx.getExpireDate() > 0)
                    fingerprint.setStatus(1);//Temporary
                else
                    fingerprint.setStatus(0);

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

    private void AddItemsToMenu(ComboBox<ComboBoxItem<Cause>> menu, VBox VOther, List<Cause> causes) {
        GuiUtils.addAutoCompletionSupportToComboBox(menu, causes);

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
        menu.setOnAction(e -> OnActionComboMenu(menu, VOther));


    }

    private void OnActionComboMenu(ComboBox<ComboBoxItem<Cause>> menu, VBox VOther) {
        if (menu.getValue().getItem().getCauseId() == 1) {
            VOther.setVisible(true);
        } else
            VOther.setVisible(false);

    }


    private void addMFToPersonFPs(Fingerprint finger, ComboBox<ComboBoxItem<Cause>> Couse, ToggleGroup TG, TextField CouseOther) {
        finger.setMissOrNot(true);
        if (Couse.getValue().getItem().getCauseId() != 1) {
            finger.setCause(Couse.getValue().getItem());

            finger.setStatus(1);
        } else {
            finger.setCause(Couse.getValue().getItem());
            finger.setDescription(CouseOther.getText());
            if (((RadioButton) TG.getSelectedToggle()).getText().equals(resources.getString("Temporary")))
                finger.setStatus(1);
            else
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

    private void setDataToScene(Fingerprint finger, ComboBox<ComboBoxItem<Cause>> Couse, ToggleGroup TG, TextField CouseOther, CheckBox chBox, VBox VOther) {

        chBox.setSelected(true);
        if (finger.getAlreadyAdded())
            chBox.setDisable(true);

        Couse.getItems()
                .stream()
                .filter(item -> item.getItem().equals(finger.getCause()))
                .findFirst()
                .ifPresent(Couse::setValue);

        if (finger.getCause().getCauseId() == 1) {
            VOther.setVisible(true);
            if (finger.getStatus() == 0)
                TG.getToggles().get(0).setSelected(true);
            else
                TG.getToggles().get(1).setSelected(true);

            CouseOther.setText(finger.getDescription());
        }


    }

    private boolean isEmpty(ComboBox<ComboBoxItem<Cause>> menu, ToggleGroup TG, TextField Couse) {
        if (menu.getValue() == null) {
            showWarningNotification(resources.getString("SelectCause"));
            return true;
        } else if (menu.getValue().getItem().getCauseId() == 1) {

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

    private Boolean canAddToList(Fingerprint finger, Integer position) {

        if (finger.getAlreadyAdded()) {
            if ((getLast(personMissinfingerprints, position)).getCause().equals(finger.getCause()))
                return false;
        }
        return true;

    }

    private BioExclusion addToList(Fingerprint finger, Integer position) {
        BioExclusion bioEx = new BioExclusion();
        bioEx.setBioType(1);
        bioEx.setPosition(position);
        //if 0 then not Temporary
        if (finger.getStatus() == 0)
            bioEx.setExpireDate(new Long(0));
        else
            bioEx.setExpireDate(new Long(1564475459));

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
            if (canAddToList(Editedpersonfingerprints.getRThumb(), 0)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRThumb(), 0));
            }
        } else Editedpersonfingerprints.setRThumb(new Fingerprint());
        if (chbRightIndex.isSelected()) {
            if (isEmpty(CMenuRIndex, TGRIndex, CouseTRIndex))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRIndex(), CMenuRIndex, TGRIndex, CouseTRIndex);
            if (canAddToList(Editedpersonfingerprints.getRIndex(), 1)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRIndex(), 1));
            }
        } else Editedpersonfingerprints.setRIndex(new Fingerprint());
        if (chbRightMiddle.isSelected()) {
            if (isEmpty(CMenuRMiddle, TGRMiddle, CouseTRMiddle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRMiddle(), CMenuRMiddle, TGRMiddle, CouseTRMiddle);
            if (canAddToList(Editedpersonfingerprints.getRMiddle(), 2)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRMiddle(), 2));
            }
        } else Editedpersonfingerprints.setRMiddle(new Fingerprint());
        if (chbRightRing.isSelected()) {
            if (isEmpty(CMenuRRing, TGRRing, CouseTRRing))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRRing(), CMenuRRing, TGRRing, CouseTRRing);
            if (canAddToList(Editedpersonfingerprints.getRRing(), 3)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRRing(), 3));
            }
        } else Editedpersonfingerprints.setRRing(new Fingerprint());
        if (chbRightLittle.isSelected()) {
            if (isEmpty(CMenuRLittle, TGRLittle, CouseTRLittle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getRLittle(), CMenuRLittle, TGRLittle, CouseTRLittle);
            if (canAddToList(Editedpersonfingerprints.getRLittle(), 4)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getRLittle(), 4));
            }
        } else Editedpersonfingerprints.setRLittle(new Fingerprint());


        if (chbLeftThumb.isSelected()) {
            if (isEmpty(CMenuLThumb, TGLThumb, CouseTLThumb))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLThumb(), CMenuLThumb, TGLThumb, CouseTLThumb);
            if (canAddToList(Editedpersonfingerprints.getLThumb(), 5)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLThumb(), 5));
            }
        } else Editedpersonfingerprints.setLThumb(new Fingerprint());
        if (chbLeftIndex.isSelected()) {
            if (isEmpty(CMenuLIndex, TGLIndex, CouseTLIndex))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLIndex(), CMenuLIndex, TGLIndex, CouseTLIndex);
            if (canAddToList(Editedpersonfingerprints.getLIndex(), 6)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLIndex(), 6));
            }
        } else Editedpersonfingerprints.setLIndex(new Fingerprint());
        if (chbLeftMiddle.isSelected()) {
            if (isEmpty(CMenuLMiddle, TGLMiddle, CouseTLMiddle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLMiddle(), CMenuLMiddle, TGLMiddle, CouseTLMiddle);
            if (canAddToList(Editedpersonfingerprints.getLMiddle(), 7)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLMiddle(), 7));
            }
        } else Editedpersonfingerprints.setLMiddle(new Fingerprint());
        if (chbLeftRing.isSelected()) {
            if (isEmpty(CMenuLRing, TGLRing, CouseTLRing))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLRing(), CMenuLRing, TGLRing, CouseTLRing);
            if (canAddToList(Editedpersonfingerprints.getLRing(), 8)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLRing(), 8));
            }
        } else Editedpersonfingerprints.setLRing(new Fingerprint());
        if (chbLeftLittle.isSelected()) {
            if (isEmpty(CMenuLLittle, TGLLittle, CouseTLLittle))
                return;
            addMFToPersonFPs(Editedpersonfingerprints.getLLittle(), CMenuLLittle, TGLLittle, CouseTLLittle);
            if (canAddToList(Editedpersonfingerprints.getLLittle(), 9)) {
                BioExclusionsList.add(addToList(Editedpersonfingerprints.getLLittle(), 9));
            }
        } else Editedpersonfingerprints.setLLittle(new Fingerprint());


        if (BioExclusionsList.size() == 0) {
            showWarningNotification(resources.getString("NoEditOrAddMissingFP"));
            return;
        }

        super.onNextButtonClicked(actionEvent);
    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {

        Upload();

    }
}
