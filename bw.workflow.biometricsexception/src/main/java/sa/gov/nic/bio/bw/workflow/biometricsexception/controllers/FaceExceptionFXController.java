package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.BioExclusion;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Cause;
import sa.gov.nic.bio.bw.workflow.biometricsexception.lookups.CausesLookup;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@FxmlFile("faceException2.fxml")
public class FaceExceptionFXController extends WizardStepFxControllerBase {


    @Input
    private BioExclusion FaceException;


    @Output
    private BioExclusion EditFaceException;
    @Output
    private List<Integer> SeqNumbersList;
    @Output
    private TypeFaceService typeFaceService;
    @Output
    private List<Cause> causesFC;


    @FXML
    private TextField TxtfaceExcReason;
    @FXML
    private ComboBox<ComboBoxItem<Cause>> ComboMenu;
    @FXML
    private Label LblfaceExcReason;
    @FXML
    private Button DeleteButton;
    @FXML
    private HBox RBStatusFaceEx;
    @FXML
    private ToggleGroup RBStatus;
    @FXML
    private Label StatusLabel;
    @FXML
    private Label LblfaceExcStatus;

    @FXML
    private Label LblDelfaceExcStatus;
    @FXML
    private Label LblDelfaceExcCouse;
    @FXML
    private Label lblExcpiredExc;

    @SuppressWarnings("unchecked")
    private List<Cause> causes = (List<Cause>) Context.getUserSession().getAttribute(CausesLookup.KEY);
    private Integer excpiredExceptionSeqNum;

    @Override
    protected void onAttachedToScene() {

        SeqNumbersList = new ArrayList<>();

        AddItemsToMenu();
        // do not show if expired
        if (FaceException != null && FaceException.getExpireDate() != null &&
            FaceException.getExpireDate() < Instant.now().getEpochSecond()) {
            excpiredExceptionSeqNum = FaceException.getSeqNum();
            FaceException = null;
            lblExcpiredExc.setVisible(true);

        }

        RBStatusFaceEx.setVisible(true);
        StatusLabel.setVisible(true);


        //if there is old state show or make new
        if (EditFaceException == null && FaceException != null) {
            for (Cause causeFC : causes) {

                if (causeFC.getCauseId().equals( FaceException.getCasueId())) {
                    EditFaceException = new BioExclusion();
                    EditFaceException.setCasueId(causeFC.getCauseId());
                    EditFaceException.setMonth(FaceException.getMonth());
                    if (FaceException.getCasueId() == 1) {
                        EditFaceException.setDescription(FaceException.getDescription());
                    }

                    break;
                }
            }

        }


        // upload old state
        if (EditFaceException != null) { uploadReason(); }


        // for delete pane
        if (FaceException != null) {
            for (Cause causeFC : causes) {

                if (causeFC.getCauseId().equals( FaceException.getCasueId())) {
                    if (FaceException.getCasueId() == 1) {
                        LblfaceExcReason.setText(FaceException.getDescription());
                    }
                    else {
                        if (Context.getGuiLanguage() == GuiLanguage.ARABIC) {
                            LblfaceExcReason.setText(causeFC.getArabicText());
                        }
                        else { LblfaceExcReason.setText(causeFC.getEnglishText()); }
                    }

                    //duration

                    if (FaceException.getMonth() == 3) { LblfaceExcStatus.setText(resources.getString("3months")); }
                    else if (FaceException.getMonth() == 6) {
                        LblfaceExcStatus.setText(resources.getString("6months"));
                    }
                    else { LblfaceExcStatus.setText(resources.getString("oneYear")); }
                    DeleteButton.setDisable(false);
                    break;
                }
            }

        }
        else {
            LblDelfaceExcStatus.setVisible(false);
            LblDelfaceExcCouse.setVisible(false);
            LblfaceExcReason.setText(resources.getString("NoFaceException"));
        }

        //  continueWorkflow();

    }

    private void AddItemsToMenu() {
        List<Cause> CauseFEx = new ArrayList<>();
        CauseFEx.addAll(causes);
        CauseFEx.removeIf(cause -> cause.getCauseId() == 2);
        GuiUtils.addAutoCompletionSupportToComboBox(ComboMenu, CauseFEx);

        Consumer<ComboBoxItem<Cause>> consumer = item ->
        {
            Cause cause = item.getItem();

            String text;
            if (Context.getGuiLanguage() == GuiLanguage.ARABIC) { text = cause.getArabicText(); }
            else { text = cause.getEnglishText(); }

            String resultText = text.trim();
            item.setText(resultText);
        };

        ComboMenu.setConverter(new StringConverter<>() {
            @Override
            public String toString(ComboBoxItem<Cause> object) {
                if (object == null) { return ""; }
                else { return object.getText(); }
            }

            @Override
            public ComboBoxItem<Cause> fromString(String string) {
                if (string == null || string.trim().isEmpty()) { return null; }

                for (ComboBoxItem<Cause> causeComboBoxItem : ComboMenu.getItems()) {
                    if (string.equals(causeComboBoxItem.getText())) { return causeComboBoxItem; }
                }

                return null;
            }
        });

        ComboMenu.getItems().forEach(consumer);

        ComboMenu.setOnAction(e -> OnActionComboMenu());

    }

    private void OnActionComboMenu() {
        if (ComboMenu.getValue().getItem().getCauseId() == 1) {
            TxtfaceExcReason.setVisible(true);

        }
        else { TxtfaceExcReason.setVisible(false); }


    }


    private void uploadReason() {

        ComboMenu.getItems()
                .stream()
                .filter(item -> item.getItem().getCauseId().equals(EditFaceException.getCasueId()))
                .findFirst()
                .ifPresent(ComboMenu::setValue);

        if (EditFaceException.getCasueId() == 1) {
            TxtfaceExcReason.setVisible(true);
            TxtfaceExcReason.setText(EditFaceException.getDescription());

        }
        else { TxtfaceExcReason.setVisible(false); }

        if (EditFaceException.getMonth() == 12) { RBStatus.getToggles().get(2).setSelected(true); }
        else if (EditFaceException.getMonth() == 6) { RBStatus.getToggles().get(1).setSelected(true); }
        else { RBStatus.getToggles().get(0).setSelected(true); }

    }


    @FXML
    private void onAddButtonClicked(ActionEvent actionEvent) {

        EditFaceException = new BioExclusion();

        if (ComboMenu.getValue() == null) {
            showWarningNotification(resources.getString("SelectCause"));
            return;
        }
        else { EditFaceException.setCasueId(ComboMenu.getValue().getItem().getCauseId()); }

        if (RBStatus.getSelectedToggle() == null) {
            showWarningNotification(resources.getString("SelectStatus"));
            return;

        }
        if (EditFaceException.getCasueId() == 1) {
            if (TxtfaceExcReason.getText().trim().isEmpty()) {
                showWarningNotification(resources.getString("WriteCause"));
                return;
            }
            else {
                EditFaceException.setDescription(TxtfaceExcReason.getText());
            }
        }

        if (((RadioButton) RBStatus.getSelectedToggle()).getText().equals(resources.getString("3months"))) {
            EditFaceException.setMonth(3);
            EditFaceException.setExpireDate(Instant.now().getEpochSecond() + 7889238);
            EditFaceException.setCreateDate(Instant.now().getEpochSecond());
        }
        else if (((RadioButton) RBStatus.getSelectedToggle()).getText().equals(resources.getString("6months"))) {
            EditFaceException.setMonth(6);
            EditFaceException.setExpireDate(Instant.now().getEpochSecond() + 15778476);
            EditFaceException.setCreateDate(Instant.now().getEpochSecond());
        }
        else {
            EditFaceException.setMonth(12);
            EditFaceException.setExpireDate(Instant.now().getEpochSecond() + 31556952);
            EditFaceException.setCreateDate(Instant.now().getEpochSecond());
        }


        if (FaceException != null) {
            if (FaceException.getCasueId().equals( ComboMenu.getValue().getItem().getCauseId())) {
                if (FaceException.getCasueId() == 1) {
                    if (FaceException.getDescription().equals(EditFaceException.getDescription())) {
                        if (FaceException.getMonth().equals( EditFaceException.getMonth())) {
                            showWarningNotification(resources.getString("NoEditOnFaceExc"));
                            return;
                        }
                        else {

                            SeqNumbersList.add(FaceException.getSeqNum());
                        }
                    }
                    else {

                        SeqNumbersList.add(FaceException.getSeqNum());
                    }
                }
                else {
                    if (FaceException.getMonth().equals( EditFaceException.getMonth())) {
                        showWarningNotification(resources.getString("NoEditOnFaceExc"));
                        return;
                    }
                    else {

                        SeqNumbersList.add(FaceException.getSeqNum());
                    }

                }
            }
            else {

                SeqNumbersList.add(FaceException.getSeqNum());
            }

        }


        typeFaceService = TypeFaceService.ADD_OR_EDIT;

        causesFC = causes;

        if (excpiredExceptionSeqNum != null) { SeqNumbersList.add(excpiredExceptionSeqNum); }

        onNextButtonClicked(actionEvent);

    }


    @FXML
    private void onDeleteButtonClicked(ActionEvent actionEvent) {

        EditFaceException = null;

        SeqNumbersList.add(FaceException.getSeqNum());
        typeFaceService = TypeFaceService.DELETE;

        causesFC = causes;
        onNextButtonClicked(actionEvent);

    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        //
        EditFaceException = null;
    }

    @Override
    public void onReturnFromWorkflow(boolean successfulResponse) {
        if (successfulResponse) { goNext(); }
    }

    public enum TypeFaceService {
        ADD_OR_EDIT,
        DELETE

    }
}
