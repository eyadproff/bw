package sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@FxmlFile("updatePersonInfo.fxml")
public class UpdatePersonInfoPaneFxController extends WizardStepFxControllerBase {
    //    @Input private String cameraFacePhotoBase64;
    //    @Input private String facePhotoBase64FromAnotherSource;
    @Input private Boolean civilHit;
    @Input private NormalizedPersonInfo normalizedPersonInfo;
    @Input private PersonInfo personInfo;
    @Input protected Map<Long, String> civilPassportMap;
    @Input protected Map<Long, PersonInfo> civilPersonInfoMap;

    @Output private String firstName;
    @Output private String fatherName;
    @Output private String grandfatherName;
    @Output private String familyName;
    @Output private String englishFirstName;
    @Output private String englishFatherName;
    @Output private String englishGrandfatherName;
    @Output private String englishFamilyName;
    @Output private Country nationality;
    @Output private LocalDate birthDate;
    @Output private Boolean birthDateUseHijri;
    @Output private Long personId;
    @Output private String passportId;

    @FXML private TextField txtFirstName;
    @FXML private TextField txtFatherName;
    @FXML private TextField txtGrandfatherName;
    @FXML private TextField txtFamilyName;
    @FXML private TextField txtEnglishFirstName;
    @FXML private TextField txtEnglishFatherName;
    @FXML private TextField txtEnglishGrandfatherName;
    @FXML private TextField txtEnglishFamilyName;
    @FXML private TextField txtPersonId;
    @FXML private TextField txtPassportId;
    @FXML private ComboBox<ComboBoxItem<Country>> cboNationality;
    @FXML private RadioButton rdoBirthDateUseHijri;
    @FXML private RadioButton rdoBirthDateUseGregorian;
    @FXML private DatePicker dpBirthDate;
    @FXML private Button btnStartOver;
    @FXML private Button btnNext;

    private static final Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());

    private BooleanProperty disablePhotoEditing = new SimpleBooleanProperty(true);
    private BooleanProperty photoLoaded = new SimpleBooleanProperty(false);
    private FileChooser fileChooser = new FileChooser();

    @Override
    protected void onAttachedToScene() {
        fileChooser.setTitle(resources.getString("fileChooser.selectImage.title"));
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(
                resources.getString("fileChooser.selectImage.types"), "*.jpg");
        fileChooser.getExtensionFilters().addAll(extFilterJPG);

        @SuppressWarnings("unchecked")
        List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);

        List<Country> countriesPlusUnknown = new ArrayList<>(countries);
        String text = resources.getString("combobox.unknownNationality");
        Country unknownNationality = new Country(0, null, text, text);
        countriesPlusUnknown.add(0, unknownNationality);

        GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, countriesPlusUnknown);

        GuiUtils.makeComboBoxOpenableByPressingEnter(cboNationality);

        GuiUtils.initDatePicker(rdoBirthDateUseHijri, dpBirthDate, birthDateValidator);

        GuiUtils.applyValidatorToTextField(txtFirstName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtFatherName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtGrandfatherName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtFamilyName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtEnglishFirstName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtEnglishFatherName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtEnglishGrandfatherName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtEnglishFamilyName, null, null, 15);
        GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
        GuiUtils.applyValidatorToTextField(txtPassportId, "\\d*", "[^\\d]", 10);

        BooleanBinding txtFirstNameBinding = GuiUtils.textFieldBlankBinding(txtFirstName);
        BooleanBinding txtFamilyNameBinding = GuiUtils.textFieldBlankBinding(txtFamilyName);
        BooleanBinding txtFatherNameBinding = GuiUtils.textFieldBlankBinding(txtFatherName);

        btnNext.disableProperty().bind(txtFirstNameBinding.or(txtFatherNameBinding).or(txtFamilyNameBinding));

        cboNationality.setConverter(new StringConverter<>() {
            @Override
            public String toString(ComboBoxItem<Country> object) {
                if (object == null) { return ""; }
                else { return object.getText(); }
            }

            @Override
            public ComboBoxItem<Country> fromString(String string) {
                if (string == null || string.trim().isEmpty()) { return null; }

                for (ComboBoxItem<Country> nationalityBean : cboNationality.getItems()) {
                    if (string.equals(nationalityBean.getText())) { return nationalityBean; }
                }

                return null;
            }
        });
        cboNationality.getItems().forEach(item ->
        {
            Country country = item.getItem();

            String s;
            if (Context.getGuiLanguage() == GuiLanguage.ARABIC) { s = country.getDescriptionAR(); }
            else { s = country.getDescriptionEN(); }

            if (s != null) { s = s.trim(); }

            String mofaCode = country.getMofaNationalityCode();
            String resultText = mofaCode != null && !mofaCode.trim().isEmpty() ? s + " (" + mofaCode + ")" : s;
            item.setText(resultText);
        });

        //
        Node focusedNode = null;

        String firstName = null;
        String fatherName = null;
        String grandfatherName = null;
        String familyName = null;
        String englishFirstName = null;
        String englishFatherName = null;
        String englishGrandfatherName = null;
        String englishFamilyName = null;
        Country nationality = null;
        LocalDate birthDate = null;
        Long personId = null;
        String passportId = null;

        if (normalizedPersonInfo != null) {

            PersonInfo personInfoFinal = personInfo;
            if (personInfoFinal == null) {
                personInfoFinal = civilPersonInfoMap.get(normalizedPersonInfo.getPersonId());
            }
            Name name = personInfoFinal.getName();
            firstName = normalizedPersonInfo.getFirstName();
            fatherName = normalizedPersonInfo.getFatherName();
            grandfatherName = normalizedPersonInfo.getGrandfatherName();
            familyName = normalizedPersonInfo.getFamilyName();
            englishFirstName = name.getTranslatedFirstName();
            englishFatherName = name.getTranslatedFatherName();
            englishGrandfatherName = name.getTranslatedGrandFatherName();
            englishFamilyName = name.getTranslatedFamilyName();
            nationality = normalizedPersonInfo.getNationality();
            birthDate = normalizedPersonInfo.getBirthDate();
            personId = normalizedPersonInfo.getPersonId();
            passportId = civilPassportMap.get(normalizedPersonInfo.getPersonId());
        }

        // civil hit or present
        boolean disable = (civilHit != null && civilHit);

        if (!disable || !photoLoaded.get()) { disablePhotoEditing.setValue(false); }

        if (isFirstLoad()) {
            if (firstName != null) { txtFirstName.setText(firstName); }
            else { focusedNode = txtFirstName; }
        }
        else if (this.firstName != null) { txtFirstName.setText(this.firstName); }
        else { focusedNode = txtFirstName; }

        if (disable && !txtFirstName.getText().isEmpty()) { txtFirstName.setDisable(true); }

        if (isFirstLoad()) {
            if (fatherName != null) { txtFatherName.setText(fatherName); }
        }
        else if (this.fatherName != null) { txtFatherName.setText(this.fatherName); }

        if (disable && !txtFatherName.getText().isEmpty()) { txtFatherName.setDisable(true); }

        if (isFirstLoad()) {
            if (grandfatherName != null) { txtGrandfatherName.setText(grandfatherName); }
        }
        else if (this.grandfatherName != null) { txtGrandfatherName.setText(this.grandfatherName); }

        if (disable && !txtGrandfatherName.getText().isEmpty()) { txtGrandfatherName.setDisable(true); }

        if (isFirstLoad()) {
            if (familyName != null) { txtFamilyName.setText(familyName); }
            else if (focusedNode == null) { focusedNode = txtFamilyName; }
        }
        else if (this.familyName != null) { txtFamilyName.setText(this.familyName); }
        else if (focusedNode == null) { focusedNode = txtFamilyName; }

        if (disable && !txtFamilyName.getText().isEmpty()) { txtFamilyName.setDisable(true); }


        if (isFirstLoad()) {
            if (englishFirstName != null) { txtEnglishFirstName.setText(englishFirstName); }
        }
        else if (this.englishFirstName != null) { txtEnglishFirstName.setText(this.englishFirstName); }

        if (disable && !txtEnglishFirstName.getText().isEmpty()) { txtEnglishFirstName.setDisable(true); }

        if (isFirstLoad()) {
            if (englishFatherName != null) { txtEnglishFatherName.setText(englishFatherName); }
        }
        else if (this.englishFatherName != null) { txtEnglishFatherName.setText(this.englishFatherName); }

        if (disable && !txtEnglishFatherName.getText().isEmpty()) { txtEnglishFatherName.setDisable(true); }

        if (isFirstLoad()) {
            if (englishGrandfatherName != null) { txtEnglishGrandfatherName.setText(englishGrandfatherName); }
        }
        else if (this.englishGrandfatherName != null) { txtEnglishGrandfatherName.setText(this.englishGrandfatherName); }

        if (disable && !txtEnglishGrandfatherName.getText().isEmpty()) { txtEnglishGrandfatherName.setDisable(true); }

        if (isFirstLoad()) {
            if (englishFamilyName != null) { txtEnglishFamilyName.setText(englishFamilyName); }
        }
        else if (this.englishFamilyName != null) { txtEnglishFamilyName.setText(this.englishFamilyName); }

        if (disable && !txtEnglishFamilyName.getText().isEmpty()) { txtEnglishFamilyName.setDisable(true); }

        if (isFirstLoad()) {
            if (nationality != null) { GuiUtils.selectComboBoxItem(cboNationality, nationality); }
            else { cboNationality.getSelectionModel().select(0); }
        }
        else if (this.nationality != null) { GuiUtils.selectComboBoxItem(cboNationality, this.nationality); }
        else { cboNationality.getSelectionModel().select(0); }

        if (disable && cboNationality.getSelectionModel().getSelectedIndex() > 0) { cboNationality.setDisable(true); }

        if (isFirstLoad()) {
            if (birthDate != null) { dpBirthDate.setValue(birthDate); }
        }
        else if (this.birthDate != null) { dpBirthDate.setValue(this.birthDate); }


        if (disable && dpBirthDate.getValue() != null) { dpBirthDate.setDisable(true); }

        rdoBirthDateUseHijri.setSelected(true);
        if (this.birthDateUseHijri != null && !this.birthDateUseHijri) { rdoBirthDateUseGregorian.setSelected(true); }

        if (isFirstLoad()) {
            if (personId != null) { txtPersonId.setText(String.valueOf(personId)); }
        }
        else if (this.personId != null) { txtPersonId.setText(String.valueOf(this.personId)); }

        if (disable && !txtPersonId.getText().isEmpty()) { txtPersonId.setDisable(true); }

        if (isFirstLoad()) {
            if (passportId != null) { txtPassportId.setText(passportId); }
        }
        else if (this.passportId != null) { txtPassportId.setText(this.passportId); }

        if (disable && !txtPassportId.getText().isEmpty()) { txtPassportId.setDisable(true); }

        if (focusedNode != null) { focusedNode.requestFocus(); }
        else { btnNext.requestFocus(); }
    }

    @Override
    protected void onGoingPrevious(Map<String, Object> uiDataMap) {
        onGoingNext(uiDataMap);
    }

    @Override
    public void onGoingNext(Map<String, Object> uiDataMap) {
        var firstName = txtFirstName.getText().strip();
        if (!firstName.isBlank()) { this.firstName = firstName; }
        else { this.firstName = null; }

        var fatherName = txtFatherName.getText().strip();
        if (!fatherName.isBlank()) { this.fatherName = fatherName; }
        else { this.fatherName = null; }

        var grandfatherName = txtGrandfatherName.getText().strip();
        if (!grandfatherName.isBlank()) { this.grandfatherName = grandfatherName; }
        else { this.grandfatherName = null; }

        var familyName = txtFamilyName.getText().strip();
        if (!familyName.isBlank()) { this.familyName = familyName; }
        else { this.familyName = null; }

        var englishFirstName = txtEnglishFirstName.getText().strip();
        if (!englishFirstName.isBlank()) { this.englishFirstName = englishFirstName; }
        else { this.englishFirstName = null; }

        var englishFatherName = txtEnglishFatherName.getText().strip();
        if (!englishFatherName.isBlank()) { this.englishFatherName = englishFatherName; }
        else { this.englishFatherName = null; }

        var englishGrandfatherName = txtEnglishGrandfatherName.getText().strip();
        if (!englishGrandfatherName.isBlank()) { this.englishGrandfatherName = englishGrandfatherName; }
        else { this.englishGrandfatherName = null; }

        var englishFamilyName = txtEnglishFamilyName.getText().strip();
        if (!englishFamilyName.isBlank()) { this.englishFamilyName = englishFamilyName; }
        else { this.englishFamilyName = null; }


        var nationalityItem = cboNationality.getValue();
        if (nationalityItem != null) { this.nationality = nationalityItem.getItem(); }
        else { this.nationality = null; }


        this.birthDate = dpBirthDate.getValue();
        this.birthDateUseHijri = rdoBirthDateUseHijri.isSelected();

        var sPersonId = txtPersonId.getText().strip();
        if (!sPersonId.isBlank()) { this.personId = Long.parseLong(sPersonId); }
        else { this.personId = null; }

        var sPassportId = txtPassportId.getText().strip();
        if (!sPassportId.isBlank()) { this.passportId = sPassportId; }
        else { this.passportId = null; }

    }

    @FXML
    protected void onNextButtonClicked(ActionEvent actionEvent) {
        String headerText = resources.getString("PersonInfo.confirmation.header");
        String contentText = resources.getString("PersonInfo.confirmation.message");
        boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);

        if (confirmed) {goNext();}
    }
}