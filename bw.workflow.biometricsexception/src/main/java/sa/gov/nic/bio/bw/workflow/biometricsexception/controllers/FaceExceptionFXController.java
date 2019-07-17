package sa.gov.nic.bio.bw.workflow.biometricsexception.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import sa.gov.nic.bio.bw.workflow.biometricsexception.lookups.CausesLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@FxmlFile("faceException.fxml")
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

    // to show
    @Output
    private Cause Reason;
    @Output
    private String Descrption;

    @FXML
    private TextField TxtfaceExcReason;
    @FXML
    private ComboBox<ComboBoxItem<Cause>> ComboMenu;
    @FXML
    private Label LblfaceExcReason;
    @FXML
    private Button DeleteButton;

    @SuppressWarnings("unchecked")
    private List<Cause> causes = (List<Cause>) Context.getUserSession().getAttribute(CausesLookup.KEY);


    @Override
    protected void onAttachedToScene() {


        AddItemsToMenu();

        if (isFirstLoad()) {
            if (FaceException != null) {
                for (Cause causeFC : causes) {

                    if (causeFC.getCauseId() == FaceException.getCasueId()) {
                        Reason = new Cause();
                        Reason.setCauseId(causeFC.getCauseId());
                        Reason.setDescriptionAr(causeFC.getArabicText());
                        Reason.setDescriptionEn(causeFC.getEnglishText());
                        if (FaceException.getCasueId() == 1) {
                            Descrption = FaceException.getDescription();
                        }
                        break;
                    }
                }

            }
        }

        if (Reason != null)
            uploadReason();

        // for delete pane
        if (FaceException != null) {
            for (Cause causeFC : causes) {

                if (causeFC.getCauseId() == FaceException.getCasueId()) {
                    if (FaceException.getCasueId() == 1) {
                        LblfaceExcReason.setText(FaceException.getDescription());
                    } else {
                        if (Context.getGuiLanguage() == GuiLanguage.ARABIC)
                            LblfaceExcReason.setText(causeFC.getArabicText());
                        else LblfaceExcReason.setText(causeFC.getEnglishText());
                    }
                    DeleteButton.setDisable(false);
                    break;
                }
            }

        }
    }

    private void AddItemsToMenu() {
        List<Cause> CauseFEx = new ArrayList<Cause>();
        CauseFEx.addAll(causes);
        CauseFEx.removeIf(cause -> cause.getCauseId() == 2);
        GuiUtils.addAutoCompletionSupportToComboBox(ComboMenu, CauseFEx);

        Consumer<ComboBoxItem<Cause>> consumer = item ->
        {
            Cause cause = item.getItem();

            String text;
            if (Context.getGuiLanguage() == GuiLanguage.ARABIC) text = cause.getArabicText();
            else text = cause.getEnglishText();

            String resultText = text.trim();
            item.setText(resultText);
        };

        ComboMenu.setConverter(new StringConverter<>() {
            @Override
            public String toString(ComboBoxItem<Cause> object) {
                if (object == null) return "";
                else return object.getText();
            }

            @Override
            public ComboBoxItem<Cause> fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;

                for (ComboBoxItem<Cause> causeComboBoxItem : ComboMenu.getItems()) {
                    if (string.equals(causeComboBoxItem.getText())) return causeComboBoxItem;
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
        } else
            TxtfaceExcReason.setVisible(false);

    }


    private void uploadReason() {

        ComboMenu.getItems()
                .stream()
                .filter(item -> item.getItem().equals(Reason))
                .findFirst()
                .ifPresent(ComboMenu::setValue);

        if (Reason.getCauseId() == 1) {
            TxtfaceExcReason.setVisible(true);
            TxtfaceExcReason.setText(Descrption);
        }

    }


    @FXML
    private void onAddButtonClicked(ActionEvent actionEvent) {

        EditFaceException = new BioExclusion();

        if (ComboMenu.getValue() == null) {
            showWarningNotification(resources.getString("SelectCause"));
            return;
        }
        if (ComboMenu.getValue().getItem().getCauseId() == 1) {
            if (TxtfaceExcReason.getText().trim().isEmpty()) {
                showWarningNotification(resources.getString("WriteCause"));
                return;
            } else {
                Descrption = TxtfaceExcReason.getText();
                // EditFaceException.setCasueId(1);
            }
        }
        if (FaceException != null) {
            if (FaceException.getCasueId() == ComboMenu.getValue().getItem().getCauseId()) {
                if (FaceException.getCasueId() == 1) {
                    if (FaceException.getDescription().equals(Descrption)) {
                        showWarningNotification(resources.getString("NoEditOnFaceExc"));
                        return;
                    } else {
                        SeqNumbersList = new ArrayList<Integer>();
                        SeqNumbersList.add(FaceException.getSeqNum());
                    }
                } else {
                    showWarningNotification(resources.getString("NoEditOnFaceExc"));
                    return;
                }
            } else {
                SeqNumbersList = new ArrayList<Integer>();
                SeqNumbersList.add(FaceException.getSeqNum());
            }

        }

        Reason = new Cause();
        Reason.setCauseId(ComboMenu.getValue().getItem().getCauseId());
        Reason.setDescriptionEn(ComboMenu.getValue().getItem().getEnglishText());
        Reason.setDescriptionAr(ComboMenu.getValue().getItem().getArabicText());
        EditFaceException.setCasueId(Reason.getCauseId());

        if (EditFaceException.getCasueId() == 1)
            EditFaceException.setDescription(Descrption);


        typeFaceService = TypeFaceService.ADD_OR_EDIT;

        causesFC = causes;


        onNextButtonClicked(actionEvent);

    }

    @FXML
    private void onDeleteButtonClicked(ActionEvent actionEvent) {

        EditFaceException = null;
        SeqNumbersList = new ArrayList<Integer>();
        SeqNumbersList.add(FaceException.getSeqNum());
        typeFaceService = TypeFaceService.DELETE;

        causesFC = causes;
        onNextButtonClicked(actionEvent);

    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        if (FaceException != null) {
            for (Cause causeFC : causes) {

                if (causeFC.getCauseId() == FaceException.getCasueId()) {

                    Reason = new Cause();
                    Reason.setCauseId(causeFC.getCauseId());
                    Reason.setDescriptionAr(causeFC.getArabicText());
                    Reason.setDescriptionEn(causeFC.getEnglishText());
                    if (FaceException.getCasueId() == 1) {
                        Descrption = FaceException.getDescription();
                    }
                    break;
                }
            }

        } else
            Reason = null;


    }

    public enum TypeFaceService {
        ADD_OR_EDIT,
        DELETE

    }
}
