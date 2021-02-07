package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@FxmlFile("editPersonInfo.fxml")
public class EditPersonInfoPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private NormalizedPersonInfo normalizedPersonInfo;
	@Output private Boolean fullEditor;
	@Output private String firstNameOldValue;
	@Output private String firstNameNewValue;
	@Output private String fatherNameOldValue;
	@Output private String fatherNameNewValue;
	@Output private String grandfatherNameOldValue;
	@Output private String grandfatherNameNewValue;
	@Output private String familyNameOldValue;
	@Output private String familyNameNewValue;
	@Output private Gender genderOldValue;
	@Output private Gender genderNewValue;
	@Output private Country nationalityOldValue;
	@Output private Country nationalityNewValue;
	@Output private String occupationOldValue;
	@Output private String occupationNewValue;
	@Output private String birthPlaceOldValue;
	@Output private String birthPlaceNewValue;
	@Output private LocalDate birthDateOldValue;
	@Output private LocalDate birthDateNewValue;
	@Output private Boolean birthDateUseHijri;
	@Output private Long personIdOldValue;
	@Output private Long personIdNewValue;
	@Output private PersonType personTypeOldValue;
	@Output private PersonType personTypeNewValue;
	@Output private String documentIdOldValue;
	@Output private String documentIdNewValue;
	@Output private DocumentType documentTypeOldValue;
	@Output private DocumentType documentTypeNewValue;
	@Output private LocalDate documentIssuanceDateOldValue;
	@Output private LocalDate documentIssuanceDateNewValue;
	@Output private Boolean documentIssuanceDateUseHijri;
	@Output private LocalDate documentExpiryDateOldValue;
	@Output private LocalDate documentExpiryDateNewValue;
	@Output private Boolean documentExpiryDateUseHijri;
	
	@FXML private HBox paneNoEditPermission;
	@FXML private Pane panePersonInfo;
	@FXML private Pane paneFirstNameReset;
	@FXML private Pane paneFatherNameReset;
	@FXML private Pane paneGrandfatherNameReset;
	@FXML private Pane paneFamilyNameReset;
	@FXML private Pane paneGenderReset;
	@FXML private Pane paneNationalityReset;
	@FXML private Pane paneOccupationReset;
	@FXML private Pane paneBirthPlaceReset;
	@FXML private Pane paneBirthDateReset;
	@FXML private Pane panePersonIdReset;
	@FXML private Pane panePersonTypeReset;
	@FXML private Pane paneDocumentIdReset;
	@FXML private Pane paneDocumentTypeReset;
	@FXML private Pane paneDocumentIssuanceDateReset;
	@FXML private Pane paneDocumentExpiryDateReset;
	@FXML private TextField txtFirstName;
	@FXML private TextField txtFatherName;
	@FXML private TextField txtGrandfatherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtOccupation;
	@FXML private TextField txtBirthPlace;
	@FXML private TextField txtPersonId;
	@FXML private TextField txtDocumentId;
	@FXML private ComboBox<ComboBoxItem<Gender>> cboGender;
	@FXML private ComboBox<ComboBoxItem<Country>> cboNationality;
	@FXML private ComboBox<ComboBoxItem<PersonType>> cboPersonType;
	@FXML private ComboBox<ComboBoxItem<DocumentType>> cboDocumentType;
	@FXML private RadioButton rdoBirthDateUseHijri;
	@FXML private RadioButton rdoBirthDateUseGregorian;
	@FXML private RadioButton rdoDocumentIssuanceDateUseHijri;
	@FXML private RadioButton rdoDocumentIssuanceDateUseGregorian;
	@FXML private RadioButton rdoDocumentExpiryDateUseHijri;
	@FXML private RadioButton rdoDocumentExpiryDateUseGregorian;
	@FXML private DatePicker dpBirthDate;
	@FXML private DatePicker dpDocumentIssuanceDate;
	@FXML private DatePicker dpDocumentExpiryDate;
	@FXML private Label lblFirstNameOldValue;
	@FXML private Label lblFirstNameNewValue;
	@FXML private Label lblFatherNameOldValue;
	@FXML private Label lblFatherNameNewValue;
	@FXML private Label lblGrandfatherNameOldValue;
	@FXML private Label lblGrandfatherNameNewValue;
	@FXML private Label lblFamilyNameOldValue;
	@FXML private Label lblFamilyNameNewValue;
	@FXML private Label lblGenderOldValue;
	@FXML private Label lblGenderNewValue;
	@FXML private Label lblNationalityOldValue;
	@FXML private Label lblNationalityNewValue;
	@FXML private Label lblOccupationOldValue;
	@FXML private Label lblOccupationNewValue;
	@FXML private Label lblBirthPlaceOldValue;
	@FXML private Label lblBirthPlaceNewValue;
	@FXML private Label lblBirthDateOldValue;
	@FXML private Label lblBirthDateNewValue;
	@FXML private Label lblPersonIdOldValue;
	@FXML private Label lblPersonIdNewValue;
	@FXML private Label lblPersonTypeOldValue;
	@FXML private Label lblPersonTypeNewValue;
	@FXML private Label lblDocumentIdOldValue;
	@FXML private Label lblDocumentIdNewValue;
	@FXML private Label lblDocumentTypeOldValue;
	@FXML private Label lblDocumentTypeNewValue;
	@FXML private Label lblDocumentIssuanceDateOldValue;
	@FXML private Label lblDocumentIssuanceDateNewValue;
	@FXML private Label lblDocumentExpiryDateOldValue;
	@FXML private Label lblDocumentExpiryDateNewValue;
	@FXML private Glyph iconFirstNameArrow;
	@FXML private Glyph iconFatherNameArrow;
	@FXML private Glyph iconGrandfatherNameArrow;
	@FXML private Glyph iconFamilyNameArrow;
	@FXML private Glyph iconGenderArrow;
	@FXML private Glyph iconNationalityArrow;
	@FXML private Glyph iconOccupationArrow;
	@FXML private Glyph iconBirthPlaceArrow;
	@FXML private Glyph iconBirthDateArrow;
	@FXML private Glyph iconPersonIdArrow;
	@FXML private Glyph iconPersonTypeArrow;
	@FXML private Glyph iconDocumentIdArrow;
	@FXML private Glyph iconDocumentTypeArrow;
	@FXML private Glyph iconDocumentIssuanceDateArrow;
	@FXML private Glyph iconDocumentExpiryDateArrow;
	@FXML private Button btnFirstNameReset;
	@FXML private Button btnFatherNameReset;
	@FXML private Button btnGrandfatherNameReset;
	@FXML private Button btnFamilyNameReset;
	@FXML private Button btnGenderReset;
	@FXML private Button btnNationalityReset;
	@FXML private Button btnOccupationReset;
	@FXML private Button btnBirthPlaceReset;
	@FXML private Button btnBirthDateReset;
	@FXML private Button btnPersonIdReset;
	@FXML private Button btnPersonTypeReset;
	@FXML private Button btnDocumentIdReset;
	@FXML private Button btnDocumentTypeReset;
	@FXML private Button btnDocumentIssuanceDateReset;
	@FXML private Button btnDocumentExpiryDateReset;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	private static final Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
	@Override
	protected void onAttachedToScene()
	{
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>) Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
		
		List<Country> countriesPlusUnknown = new ArrayList<>(countries);
		String unknownNationalityText = resources.getString("combobox.unknownNationality");
		Country unknownNationality = new Country(0, null, unknownNationalityText,
		                                         unknownNationalityText);
		countriesPlusUnknown.add(0, unknownNationality);
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, countriesPlusUnknown);
		
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboNationality);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboPersonType);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboDocumentType);
		
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		ObservableList<ComboBoxItem<PersonType>> SamisIdTypeItems = FXCollections.observableArrayList();
		personTypes.forEach(idType -> SamisIdTypeItems.add(new ComboBoxItem<>(idType, arabic ?
							idType.getDescriptionAR() : idType.getDescriptionEN())));
		cboPersonType.setItems(SamisIdTypeItems);
		
		ObservableList<ComboBoxItem<DocumentType>> DocumentTypeItems = FXCollections.observableArrayList();
		documentTypes.forEach(documentType -> DocumentTypeItems.add(new ComboBoxItem<>(documentType,
		                                                                               documentType.getDesc())));
		cboDocumentType.setItems(DocumentTypeItems);
		
		GuiUtils.initDatePicker(rdoBirthDateUseHijri, dpBirthDate, birthDateValidator);
		GuiUtils.initDatePicker(rdoDocumentIssuanceDateUseHijri, dpDocumentIssuanceDate, null);
		GuiUtils.initDatePicker(rdoDocumentExpiryDateUseHijri, dpDocumentExpiryDate, null);
		
		GuiUtils.applyValidatorToTextField(txtFirstName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtFatherName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtGrandfatherName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtFamilyName, null, null, 15);
		GuiUtils.applyValidatorToTextField(txtOccupation, null, null, 20);
		GuiUtils.applyValidatorToTextField(txtBirthPlace, null, null, 20);
		GuiUtils.applyValidatorToTextField(txtPersonId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtDocumentId, null, null, 10);
		
		BooleanBinding txtFirstNameBinding = GuiUtils.textFieldBlankBinding(txtFirstName);
		BooleanBinding txtFamilyNameBinding = GuiUtils.textFieldBlankBinding(txtFamilyName);
		BooleanBinding cboGenderBinding = cboGender.valueProperty().isNull();
		BooleanBinding cboNationalityBinding = cboNationality.valueProperty().isNull();
		
		btnNext.disableProperty().bind(txtFirstNameBinding.or(txtFamilyNameBinding).or(cboGenderBinding)
				                                                                   .or(cboNationalityBinding));
		
		cboNationality.setConverter(new StringConverter<>()
		{
			@Override
			public String toString(ComboBoxItem<Country> object)
			{
				if(object == null) return "";
				else return object.getText();
			}
			
			@Override
			public ComboBoxItem<Country> fromString(String string)
			{
				if(string == null || string.trim().isEmpty()) return null;
				
				for(ComboBoxItem<Country> nationalityBean : cboNationality.getItems())
				{
					if(string.equals(nationalityBean.getText())) return nationalityBean;
				}
				
				return null;
			}
		});
		cboNationality.getItems().forEach(item ->
		{
		    Country country = item.getItem();
		
		    String s;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC) s = country.getDescriptionAR();
		    else s = country.getDescriptionEN();
		    
		    if(s != null) s = s.trim();
			
			String mofaCode = country.getMofaNationalityCode();
			String resultText = mofaCode != null && !mofaCode.trim().isEmpty() ? s + " (" + mofaCode + ")" : s;
		    item.setText(resultText);
		});
		
		firstNameOldValue = normalizedPersonInfo.getFirstName();
		fatherNameOldValue = normalizedPersonInfo.getFatherName();
		grandfatherNameOldValue = normalizedPersonInfo.getGrandfatherName();
		familyNameOldValue = normalizedPersonInfo.getFamilyName();
		genderOldValue = normalizedPersonInfo.getGender();
		nationalityOldValue = normalizedPersonInfo.getNationality();
		occupationOldValue = normalizedPersonInfo.getOccupation();
		birthPlaceOldValue = normalizedPersonInfo.getBirthPlace();
		birthDateOldValue = normalizedPersonInfo.getBirthDate();
		personIdOldValue = normalizedPersonInfo.getPersonId();
		personTypeOldValue = normalizedPersonInfo.getPersonType();
		documentIdOldValue = normalizedPersonInfo.getDocumentId();
		documentTypeOldValue = normalizedPersonInfo.getDocumentType();
		documentIssuanceDateOldValue = normalizedPersonInfo.getDocumentIssuanceDate();
		documentExpiryDateOldValue = normalizedPersonInfo.getDocumentExpiryDate();
		
		Node focusedNode = null;
		String firstName = null;
		String fatherName = null;
		String grandfatherName = null;
		String familyName = null;
		Gender gender = null;
		Country nationality = null;
		String occupation = null;
		String birthPlace = null;
		LocalDate birthDate = null;
		Long personId = null;
		PersonType personType = null;
		String documentId = null;
		DocumentType documentType = null;
		LocalDate documentIssuanceDate = null;
		LocalDate documentExpiryDate = null;
		
		if(isFirstLoad())
		{
			firstName = firstNameOldValue;
			fatherName = fatherNameOldValue;
			grandfatherName = grandfatherNameOldValue;
			familyName = familyNameOldValue;
			gender = genderOldValue;
			nationality = nationalityOldValue;
			occupation = occupationOldValue;
			birthPlace = birthPlaceOldValue;
			birthDate = birthDateOldValue;
			personId = personIdOldValue;
			personType = personTypeOldValue;
			documentId = documentIdOldValue;
			documentType = documentTypeOldValue;
			documentIssuanceDate = documentIssuanceDateOldValue;
			documentExpiryDate = documentExpiryDateOldValue;
		}
		
		@SuppressWarnings("unchecked")
		List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
		String fullEditorRole = Context.getConfigManager().getProperty("editConvictedReport.fullEditorRole");
		boolean authorized = userRoles.contains(fullEditorRole);
		
		fullEditor = authorized;
		boolean disable = !authorized;
		if(disable) GuiUtils.showNode(paneNoEditPermission, true);
		
		txtFirstName.setDisable(disable);
		if(firstName != null) txtFirstName.setText(firstName);
		else if(this.firstNameNewValue != null) txtFirstName.setText(this.firstNameNewValue);
		else focusedNode = txtFirstName;
		
		txtFatherName.setDisable(disable);
		if(fatherName != null) txtFatherName.setText(fatherName);
		else if(this.fatherNameNewValue != null) txtFatherName.setText(this.fatherNameNewValue);
		
		txtGrandfatherName.setDisable(disable);
		if(grandfatherName != null) txtGrandfatherName.setText(grandfatherName);
		else if(this.grandfatherNameNewValue != null) txtGrandfatherName.setText(this.grandfatherNameNewValue);
		
		txtFamilyName.setDisable(disable);
		if(familyName != null) txtFamilyName.setText(familyName);
		else if(this.familyNameNewValue != null) txtFamilyName.setText(this.familyNameNewValue);
		else if(focusedNode == null) focusedNode = txtFamilyName;
		
		cboGender.setDisable(disable);
		if(gender != null) GuiUtils.selectComboBoxItem(cboGender, gender);
		else if(this.genderNewValue != null) GuiUtils.selectComboBoxItem(cboGender, this.genderNewValue);
		else if(focusedNode == null) focusedNode = cboGender;
		
		cboNationality.setDisable(disable);
		if(nationality != null) GuiUtils.selectComboBoxItem(cboNationality, nationality);
		else if(this.nationalityNewValue != null) GuiUtils.selectComboBoxItem(cboNationality, this.nationalityNewValue);
		else cboNationality.getSelectionModel().select(0);
		
		txtOccupation.setDisable(disable);
		if(occupation != null) txtOccupation.setText(occupation);
		else if(this.occupationNewValue != null) txtOccupation.setText(this.occupationNewValue);
		
		txtBirthPlace.setDisable(disable);
		if(birthPlace != null) txtBirthPlace.setText(birthPlace);
		else if(this.birthPlaceNewValue != null) txtBirthPlace.setText(this.birthPlaceNewValue);
		
		dpBirthDate.setDisable(disable);
		if(birthDate != null) dpBirthDate.setValue(birthDate);
		else if(this.birthDateNewValue != null) dpBirthDate.setValue(this.birthDateNewValue);
		
		rdoBirthDateUseHijri.setSelected(true);
		if(this.birthDateUseHijri != null && !this.birthDateUseHijri) rdoBirthDateUseGregorian.setSelected(true);
		
		txtPersonId.setDisable(true);
//		txtPersonId.setDisable(disable);
		if(personId != null) txtPersonId.setText(String.valueOf(personId));
		else if(this.personIdNewValue != null) txtPersonId.setText(String.valueOf(this.personIdNewValue));
		
		cboPersonType.setDisable(disable);
		if(personType != null) GuiUtils.selectComboBoxItem(cboPersonType, personType);
		else if(this.personTypeNewValue != null) GuiUtils.selectComboBoxItem(cboPersonType, this.personTypeNewValue);
		
		txtDocumentId.setDisable(disable);
		if(documentId != null) txtDocumentId.setText(documentId);
		else if(this.documentIdNewValue != null) txtDocumentId.setText(this.documentIdNewValue);
		
		cboDocumentType.setDisable(disable);
		if(documentType != null) GuiUtils.selectComboBoxItem(cboDocumentType, documentType);
		else if(this.documentTypeNewValue != null) GuiUtils.selectComboBoxItem(cboDocumentType,
		                                                                       this.documentTypeNewValue);
		
		dpDocumentIssuanceDate.setDisable(disable);
		if(documentIssuanceDate != null) dpDocumentIssuanceDate.setValue(documentIssuanceDate);
		else if(this.documentIssuanceDateNewValue != null) dpDocumentIssuanceDate.setValue(
																					this.documentIssuanceDateNewValue);
		
		rdoDocumentIssuanceDateUseHijri.setSelected(true);
		if(this.documentIssuanceDateUseHijri != null && !this.documentIssuanceDateUseHijri)
			rdoDocumentIssuanceDateUseGregorian.setSelected(true);
		
		dpDocumentExpiryDate.setDisable(disable);
		if(documentExpiryDate != null) dpDocumentExpiryDate.setValue(documentExpiryDate);
		else if(this.documentExpiryDateNewValue != null) dpDocumentExpiryDate.setValue(this.documentExpiryDateNewValue);
		
		rdoDocumentExpiryDateUseHijri.setSelected(true);
		if(this.documentExpiryDateUseHijri != null && !this.documentExpiryDateUseHijri)
			rdoDocumentExpiryDateUseGregorian.setSelected(true);
		
		if(focusedNode != null) focusedNode.requestFocus();
		else btnNext.requestFocus();
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		FontAwesome.Glyph arrowIcon = rtl ? FontAwesome.Glyph.LONG_ARROW_LEFT : FontAwesome.Glyph.LONG_ARROW_RIGHT;
		iconFirstNameArrow.setIcon(arrowIcon);
		iconFatherNameArrow.setIcon(arrowIcon);
		iconGrandfatherNameArrow.setIcon(arrowIcon);
		iconFamilyNameArrow.setIcon(arrowIcon);
		iconGenderArrow.setIcon(arrowIcon);
		iconNationalityArrow.setIcon(arrowIcon);
		iconOccupationArrow.setIcon(arrowIcon);
		iconBirthPlaceArrow.setIcon(arrowIcon);
		iconBirthDateArrow.setIcon(arrowIcon);
		iconPersonIdArrow.setIcon(arrowIcon);
		iconPersonTypeArrow.setIcon(arrowIcon);
		iconDocumentIdArrow.setIcon(arrowIcon);
		iconDocumentTypeArrow.setIcon(arrowIcon);
		iconDocumentIssuanceDateArrow.setIcon(arrowIcon);
		iconDocumentExpiryDateArrow.setIcon(arrowIcon);
		
		paneFirstNameReset.visibleProperty().bind(
			Bindings.notEqual(lblFirstNameOldValue.textProperty(), lblFirstNameNewValue.textProperty()));
		paneFatherNameReset.visibleProperty().bind(
			Bindings.notEqual(lblFatherNameOldValue.textProperty(), lblFatherNameNewValue.textProperty()));
		paneGrandfatherNameReset.visibleProperty().bind(
			Bindings.notEqual(lblGrandfatherNameOldValue.textProperty(), lblGrandfatherNameNewValue.textProperty()));
		paneFamilyNameReset.visibleProperty().bind(
			Bindings.notEqual(lblFamilyNameOldValue.textProperty(), lblFamilyNameNewValue.textProperty()));
		paneGenderReset.visibleProperty().bind(
			Bindings.notEqual(lblGenderOldValue.textProperty(), lblGenderNewValue.textProperty()));
		paneNationalityReset.visibleProperty().bind(
			Bindings.notEqual(lblNationalityOldValue.textProperty(), lblNationalityNewValue.textProperty()));
		paneOccupationReset.visibleProperty().bind(
			Bindings.notEqual(lblOccupationOldValue.textProperty(), lblOccupationNewValue.textProperty()));
		paneBirthPlaceReset.visibleProperty().bind(
			Bindings.notEqual(lblBirthPlaceOldValue.textProperty(), lblBirthPlaceNewValue.textProperty()));
		paneBirthDateReset.visibleProperty().bind(
			Bindings.notEqual(lblBirthDateOldValue.textProperty(), lblBirthDateNewValue.textProperty()));
		panePersonIdReset.visibleProperty().bind(
			Bindings.notEqual(lblPersonIdOldValue.textProperty(), lblPersonIdNewValue.textProperty()));
		panePersonTypeReset.visibleProperty().bind(
			Bindings.notEqual(lblPersonTypeOldValue.textProperty(), lblPersonTypeNewValue.textProperty()));
		paneDocumentIdReset.visibleProperty().bind(
			Bindings.notEqual(lblDocumentIdOldValue.textProperty(), lblDocumentIdNewValue.textProperty()));
		paneDocumentTypeReset.visibleProperty().bind(
			Bindings.notEqual(lblDocumentTypeOldValue.textProperty(), lblDocumentTypeNewValue.textProperty()));
		paneDocumentIssuanceDateReset.visibleProperty().bind(
			Bindings.notEqual(lblDocumentIssuanceDateOldValue.textProperty(),
			                  lblDocumentIssuanceDateNewValue.textProperty()));
		paneDocumentExpiryDateReset.visibleProperty().bind(
			Bindings.notEqual(lblDocumentExpiryDateOldValue.textProperty(),
			                  lblDocumentExpiryDateNewValue.textProperty()));
		
		Country oldNationality = normalizedPersonInfo.getNationality();
		String oldNationalityText = "";
		
		if(oldNationality != null)
		{
			if(arabic) oldNationalityText = oldNationality.getDescriptionAR();
			else oldNationalityText = oldNationality.getDescriptionEN();
			
			if(oldNationalityText != null) oldNationalityText = oldNationalityText.trim();
			
			String mofaCode = oldNationality.getMofaNationalityCode();
			oldNationalityText = mofaCode != null && !mofaCode.trim().isEmpty() ?
														oldNationalityText + " (" + mofaCode + ")" : oldNationalityText;
		}
		else oldNationalityText = unknownNationalityText;
		
		lblFirstNameOldValue.setText(normalizedPersonInfo.getFirstName() != null ?
		                             normalizedPersonInfo.getFirstName() : "");
		lblFatherNameOldValue.setText(normalizedPersonInfo.getFatherName() != null ?
		                              normalizedPersonInfo.getFatherName() : "");
		lblGrandfatherNameOldValue.setText(normalizedPersonInfo.getGrandfatherName() != null ?
		                                   normalizedPersonInfo.getGrandfatherName() : "");
		lblFamilyNameOldValue.setText(normalizedPersonInfo.getFamilyName() != null ?
		                              normalizedPersonInfo.getFamilyName() : "");
		lblGenderOldValue.setText(normalizedPersonInfo.getGender() != null ?
		                          normalizedPersonInfo.getGender().toString() : "");
		lblNationalityOldValue.setText(oldNationalityText);
		lblOccupationOldValue.setText(normalizedPersonInfo.getOccupation() != null ?
		                              normalizedPersonInfo.getOccupation() : "");
		lblBirthPlaceOldValue.setText(normalizedPersonInfo.getBirthPlace() != null ?
		                              normalizedPersonInfo.getBirthPlace() : "");
		lblBirthDateOldValue.setText(GuiUtils.formatLocalDate(normalizedPersonInfo.getBirthDate(),
		                                                      rdoBirthDateUseHijri.isSelected()));
		lblPersonIdOldValue.setText(normalizedPersonInfo.getPersonId() != null ?
		                            String.valueOf(normalizedPersonInfo.getPersonId()) : "");
		lblPersonTypeOldValue.setText(normalizedPersonInfo.getPersonType() != null ?
				                              (arabic ? normalizedPersonInfo.getPersonType().getArabicText() :
						                                normalizedPersonInfo.getPersonType().getEnglishText()) : "");
		lblDocumentIdOldValue.setText(normalizedPersonInfo.getDocumentId() != null ?
		                              normalizedPersonInfo.getDocumentId() : "");
		lblDocumentTypeOldValue.setText(normalizedPersonInfo.getDocumentType() != null ?
				                              (arabic ? normalizedPersonInfo.getDocumentType().getArabicText() :
						                                normalizedPersonInfo.getDocumentType().getEnglishText()) : "");
		lblDocumentIssuanceDateOldValue.setText(GuiUtils.formatLocalDate(normalizedPersonInfo.getDocumentIssuanceDate(),
		                                                                 rdoDocumentIssuanceDateUseHijri.isSelected()));
		lblDocumentExpiryDateOldValue.setText(GuiUtils.formatLocalDate(normalizedPersonInfo.getDocumentExpiryDate(),
		                                                               rdoDocumentExpiryDateUseHijri.isSelected()));
		
		btnFirstNameReset.setOnAction(actionEvent ->
		{
			txtFirstName.setText(normalizedPersonInfo.getFirstName() != null ?
			                     normalizedPersonInfo.getFirstName() : "");
			panePersonInfo.requestFocus();
		});
		btnFatherNameReset.setOnAction(actionEvent ->
		{
			txtFatherName.setText(normalizedPersonInfo.getFatherName() != null ?
			                      normalizedPersonInfo.getFatherName() : "");
			panePersonInfo.requestFocus();
		});
		btnGrandfatherNameReset.setOnAction(actionEvent ->
		{
			txtGrandfatherName.setText(normalizedPersonInfo.getGrandfatherName() != null ?
			                           normalizedPersonInfo.getGrandfatherName() : "");
			panePersonInfo.requestFocus();
		});
		btnFamilyNameReset.setOnAction(actionEvent ->
		{
			txtFamilyName.setText(normalizedPersonInfo.getFamilyName() != null ?
			                      normalizedPersonInfo.getFamilyName() : "");
			panePersonInfo.requestFocus();
		});
		btnGenderReset.setOnAction(actionEvent ->
		{
			GuiUtils.selectComboBoxItem(cboGender, normalizedPersonInfo.getGender());
			panePersonInfo.requestFocus();
		});
		btnNationalityReset.setOnAction(actionEvent ->
		{
			GuiUtils.selectComboBoxItem(cboNationality, normalizedPersonInfo.getNationality());
			panePersonInfo.requestFocus();
		});
		btnOccupationReset.setOnAction(actionEvent ->
		{
			txtOccupation.setText(normalizedPersonInfo.getOccupation() != null ?
			                      normalizedPersonInfo.getOccupation() : "");
			panePersonInfo.requestFocus();
		});
		btnBirthPlaceReset.setOnAction(actionEvent ->
		{
			txtBirthPlace.setText(normalizedPersonInfo.getBirthPlace() != null ?
			                      normalizedPersonInfo.getBirthPlace() : "");
			panePersonInfo.requestFocus();
		});
		btnBirthDateReset.setOnAction(actionEvent ->
		{
			dpBirthDate.setValue(normalizedPersonInfo.getBirthDate());
			panePersonInfo.requestFocus();
		});
		btnPersonIdReset.setOnAction(actionEvent ->
		{
			txtPersonId.setText(normalizedPersonInfo.getPersonId() != null ?
			                    String.valueOf(normalizedPersonInfo.getPersonId()) : "");
			panePersonInfo.requestFocus();
		});
		btnPersonTypeReset.setOnAction(actionEvent ->
		{
			GuiUtils.selectComboBoxItem(cboPersonType, normalizedPersonInfo.getPersonType());
			panePersonInfo.requestFocus();
		});
		btnDocumentIdReset.setOnAction(actionEvent ->
		{
			txtDocumentId.setText(normalizedPersonInfo.getDocumentId() != null ?
			                      normalizedPersonInfo.getDocumentId() : "");
			panePersonInfo.requestFocus();
		});
		btnDocumentTypeReset.setOnAction(actionEvent ->
		{
			GuiUtils.selectComboBoxItem(cboDocumentType, normalizedPersonInfo.getDocumentType());
			panePersonInfo.requestFocus();
		});
		btnDocumentIssuanceDateReset.setOnAction(actionEvent ->
		{
			dpDocumentIssuanceDate.setValue(normalizedPersonInfo.getDocumentIssuanceDate());
			panePersonInfo.requestFocus();
		});
		btnDocumentExpiryDateReset.setOnAction(actionEvent ->
		{
			dpDocumentExpiryDate.setValue(normalizedPersonInfo.getDocumentExpiryDate());
			panePersonInfo.requestFocus();
		});
		
		rdoBirthDateUseHijri.selectedProperty().addListener((observable, oldValue, newValue) ->
                        lblBirthDateOldValue.setText(GuiUtils.formatLocalDate(normalizedPersonInfo.getBirthDate(),
		                                                                      rdoBirthDateUseHijri.isSelected())));
		rdoDocumentIssuanceDateUseHijri.selectedProperty().addListener((observable, oldValue, newValue) ->
                        lblDocumentIssuanceDateOldValue.setText(GuiUtils.formatLocalDate(
                        		                                        normalizedPersonInfo.getDocumentIssuanceDate(),
				                                                        rdoDocumentIssuanceDateUseHijri.isSelected())));
		rdoDocumentExpiryDateUseHijri.selectedProperty().addListener((observable, oldValue, newValue) ->
		                lblDocumentExpiryDateOldValue.setText(GuiUtils.formatLocalDate(
		                		                                        normalizedPersonInfo.getDocumentExpiryDate(),
                                                                        rdoDocumentExpiryDateUseHijri.isSelected())));
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		var firstName = txtFirstName.getText().strip();
		if(!firstName.isBlank()) this.firstNameNewValue = firstName;
		else this.firstNameNewValue = null;
		
		var fatherName = txtFatherName.getText().strip();
		if(!fatherName.isBlank()) this.fatherNameNewValue = fatherName;
		else this.fatherNameNewValue = null;
		
		var grandfatherName = txtGrandfatherName.getText().strip();
		if(!grandfatherName.isBlank()) this.grandfatherNameNewValue = grandfatherName;
		else this.grandfatherNameNewValue = null;
		
		var familyName = txtFamilyName.getText().strip();
		if(!familyName.isBlank()) this.familyNameNewValue = familyName;
		else this.familyNameNewValue = null;
		
		var genderItem = cboGender.getValue();
		if(genderItem != null) this.genderNewValue = genderItem.getItem();
		else this.genderNewValue = null;
		
		var nationalityItem = cboNationality.getValue();
		if(nationalityItem != null) this.nationalityNewValue = nationalityItem.getItem();
		else this.nationalityNewValue = null;
		
		var occupation = txtOccupation.getText().strip();
		if(!occupation.isBlank()) this.occupationNewValue = occupation;
		else this.occupationNewValue = null;
		
		var birthPlace = txtBirthPlace.getText().strip();
		if(!birthPlace.isBlank()) this.birthPlaceNewValue = birthPlace;
		else this.birthPlaceNewValue = null;
		
		this.birthDateNewValue = dpBirthDate.getValue();
		this.birthDateUseHijri = rdoBirthDateUseHijri.isSelected();
		
		var sPersonId = txtPersonId.getText().strip();
		if(!sPersonId.isBlank()) this.personIdNewValue = Long.parseLong(sPersonId);
		else this.personIdNewValue = null;
		
		var personTypeItem = cboPersonType.getValue();
		if(personTypeItem != null) this.personTypeNewValue = personTypeItem.getItem();
		else this.personTypeNewValue = null;
		
		var documentId = txtDocumentId.getText().strip();
		if(!documentId.isBlank()) this.documentIdNewValue = documentId;
		else this.documentIdNewValue = null;
		
		var documentTypeItem = cboDocumentType.getValue();
		if(documentTypeItem != null) this.documentTypeNewValue = documentTypeItem.getItem();
		else this.documentTypeNewValue = null;
		
		this.documentIssuanceDateNewValue = dpDocumentIssuanceDate.getValue();
		this.documentIssuanceDateUseHijri = rdoDocumentIssuanceDateUseHijri.isSelected();
		
		this.documentExpiryDateNewValue = dpDocumentExpiryDate.getValue();
		this.documentExpiryDateUseHijri = rdoDocumentExpiryDateUseHijri.isSelected();
	}
}