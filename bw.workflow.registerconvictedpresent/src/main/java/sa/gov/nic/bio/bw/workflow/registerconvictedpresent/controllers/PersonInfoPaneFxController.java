package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Gender;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.webservice.Country;
import sa.gov.nic.bio.bw.workflow.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.webservice.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PersonType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@FxmlFile("personInfo.fxml")
public class PersonInfoPaneFxController extends WizardStepFxControllerBase
{
	@Input private NormalizedPersonInfo normalizedPersonInfo;
	@Output private String firstName;
	@Output private String fatherName;
	@Output private String grandfatherName;
	@Output private String familyName;
	@Output private Gender gender;
	@Output private Country nationality;
	@Output private String occupation;
	@Output private String birthPlace;
	@Output private LocalDate birthDate;
	@Output private Boolean birthDateUseHijri;
	@Output private Long personId;
	@Output private PersonType personType;
	@Output private String documentId;
	@Output private DocumentType documentType;
	@Output private LocalDate documentIssuanceDate;
	@Output private Boolean documentIssuanceDateUseHijri;
	@Output private LocalDate documentExpiryDate;
	@Output private Boolean documentExpiryDateUseHijri;
	
	
	public static final String KEY_PERSON_INFO_FIRST_NAME = "PERSON_INFO_FIRST_NAME";
	public static final String KEY_PERSON_INFO_FATHER_NAME = "PERSON_INFO_FATHER__NAME";
	public static final String KEY_PERSON_INFO_GRANDFATHER_NAME = "PERSON_INFO_GRANDFATHER_NAME";
	public static final String KEY_PERSON_INFO_FAMILY_NAME = "PERSON_INFO_FAMILY_NAME";
	public static final String KEY_PERSON_INFO_CIVIL_BIO_ID = "PERSON_INFO_CIVIL_BIO_ID";
	public static final String KEY_PERSON_INFO_GENERAL_FILE_NUMBER = "PERSON_INFO_GENERAL_FILE_NUMBER";
	public static final String KEY_PERSON_INFO_GENDER = "PERSON_INFO_GENDER";
	public static final String KEY_PERSON_INFO_NATIONALITY = "PERSON_INFO_NATIONALITY";
	public static final String KEY_PERSON_INFO_OCCUPATION = "PERSON_INFO_OCCUPATION";
	public static final String KEY_PERSON_INFO_BIRTH_PLACE = "PERSON_INFO_BIRTH_PLACE";
	public static final String KEY_PERSON_INFO_BIRTH_DATE = "PERSON_INFO_BIRTH_DATE";
	public static final String KEY_PERSON_INFO_SAMIS_ID = "PERSON_INFO_SAMIS_ID";
	public static final String KEY_PERSON_INFO_SAMIS_ID_TYPE = "PERSON_INFO_SAMIS_ID_TYPE";
	public static final String KEY_PERSON_INFO_DOCUMENT_ID = "PERSON_INFO_DOCUMENT_ID";
	public static final String KEY_PERSON_INFO_DOCUMENT_TYPE = "PERSON_INFO_DOCUMENT_TYPE";
	public static final String KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE = "PERSON_INFO_DOCUMENT_ISSUANCE_DATE";
	public static final String KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE = "PERSON_INFO_DOCUMENT_EXPIRY_DATE";
	
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
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	private static final Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
	@Override
	protected void onAttachedToScene()
	{
		btnNext.setOnAction(actionEvent -> goNext());
		
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		String text = resources.getString("combobox.unknownNationality");
		Country unknownNationality = new Country(0, null, text, text);
		countries.add(0, unknownNationality);
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>) Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, countries);
		
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
		
		BooleanBinding txtFirstNameBinding = txtFirstName.textProperty().isEmpty();
		BooleanBinding txtFamilyNameBinding = txtFamilyName.textProperty().isEmpty();
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
			
			String mofaNationalityCode = country.getMofaNationalityCode();
			String resultText = mofaNationalityCode != null ? s.trim() + " (" + mofaNationalityCode + ")" : s.trim();
		    item.setText(resultText);
		});
		
		Node focusedNode = null;
		
		String firstName = normalizedPersonInfo.getFirstName();
		if(firstName != null)
		{
			txtFirstName.setText(firstName);
			txtFirstName.setDisable(true);
		}
		else if(this.firstName != null) txtFirstName.setText(this.firstName);
		else focusedNode = txtFirstName;
		
		String fatherName = normalizedPersonInfo.getFatherName();
		if(fatherName != null)
		{
			txtFatherName.setText(fatherName);
			txtFatherName.setDisable(true);
		}
		else if(this.fatherName != null) txtFatherName.setText(this.fatherName);
		else if(focusedNode == null) focusedNode = txtFatherName;
		
		String grandfatherName = normalizedPersonInfo.getGrandfatherName();
		if(grandfatherName != null)
		{
			txtGrandfatherName.setText(grandfatherName);
			txtGrandfatherName.setDisable(true);
		}
		else if(this.grandfatherName != null) txtGrandfatherName.setText(this.grandfatherName);
		else if(focusedNode == null) focusedNode = txtGrandfatherName;
		
		String familyName = normalizedPersonInfo.getFamilyName();
		if(familyName != null)
		{
			txtFamilyName.setText(familyName);
			txtFamilyName.setDisable(true);
		}
		else if(this.familyName != null) txtFamilyName.setText(this.familyName);
		else if(focusedNode == null) focusedNode = txtFamilyName;
		
		Gender gender = normalizedPersonInfo.getGender();
		if(gender != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboGender, gender);
			if(selected) cboGender.setDisable(true);
		}
		else if(this.gender != null) GuiUtils.selectComboBoxItem(cboGender, this.gender);
		else if(focusedNode == null) focusedNode = cboGender;
		
		Country nationality = normalizedPersonInfo.getNationality();
		if(nationality != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboNationality, nationality);
			if(selected) cboNationality.setDisable(true);
		}
		else if(this.nationality != null) GuiUtils.selectComboBoxItem(cboNationality, this.nationality);
		else cboNationality.getSelectionModel().select(0);
		
		String occupation = normalizedPersonInfo.getOccupation();
		if(occupation != null)
		{
			txtOccupation.setText(occupation);
			txtOccupation.setDisable(true);
		}
		else if(this.occupation != null) txtOccupation.setText(this.occupation);
		
		String birthPlace = normalizedPersonInfo.getBirthPlace();
		if(birthPlace != null)
		{
			txtBirthPlace.setText(birthPlace);
			txtBirthPlace.setDisable(true);
		}
		else if(this.birthPlace != null) txtBirthPlace.setText(this.birthPlace);
		
		LocalDate birthDate = normalizedPersonInfo.getBirthDate();
		if(birthDate != null)
		{
			dpBirthDate.setValue(birthDate);
			dpBirthDate.setDisable(true);
		}
		else if(this.birthDate != null) dpBirthDate.setValue(this.birthDate);
		
		rdoBirthDateUseHijri.setSelected(true);
		if(this.birthDateUseHijri != null && !this.birthDateUseHijri) rdoBirthDateUseGregorian.setSelected(true);
		
		Long personId = normalizedPersonInfo.getPersonId();
		if(personId != null)
		{
			txtPersonId.setText(String.valueOf(personId));
			txtPersonId.setDisable(true);
		}
		
		PersonType personType = normalizedPersonInfo.getPersonType();
		if(personType != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboPersonType, personType);
			if(selected) cboPersonType.setDisable(true);
		}
		else if(this.personType != null) GuiUtils.selectComboBoxItem(cboPersonType, this.personType);
		
		String documentId = normalizedPersonInfo.getDocumentId();
		if(documentId != null)
		{
			txtDocumentId.setText(documentId);
			txtDocumentId.setDisable(true);
		}
		
		DocumentType documentType = normalizedPersonInfo.getDocumentType();
		if(documentType != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboDocumentType, documentType);
			if(selected) cboDocumentType.setDisable(true);
		}
		else if(this.documentType != null) GuiUtils.selectComboBoxItem(cboDocumentType, this.documentType);
		
		LocalDate documentIssuanceDate = normalizedPersonInfo.getDocumentIssuanceDate();
		if(documentIssuanceDate != null)
		{
			dpDocumentIssuanceDate.setValue(documentIssuanceDate);
			dpDocumentIssuanceDate.setDisable(true);
		}
		else if(this.documentIssuanceDate != null) dpDocumentIssuanceDate.setValue(this.documentIssuanceDate);
		
		rdoDocumentIssuanceDateUseHijri.setSelected(true);
		if(this.documentIssuanceDateUseHijri != null && !this.documentIssuanceDateUseHijri)
																rdoDocumentIssuanceDateUseGregorian.setSelected(true);
		
		LocalDate documentExpiryDate = normalizedPersonInfo.getDocumentExpiryDate();
		if(documentExpiryDate != null)
		{
			dpDocumentExpiryDate.setValue(documentExpiryDate);
			dpDocumentExpiryDate.setDisable(true);
		}
		else if(this.documentExpiryDate != null) dpDocumentExpiryDate.setValue(this.documentExpiryDate);
		
		rdoDocumentExpiryDateUseHijri.setSelected(true);
		if(this.documentExpiryDateUseHijri != null && !this.documentExpiryDateUseHijri)
																rdoDocumentExpiryDateUseGregorian.setSelected(true);
		
		if(focusedNode != null) focusedNode.requestFocus();
		else btnNext.requestFocus();
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		String firstName = txtFirstName.getText().trim();
		if(!firstName.isEmpty()) this.firstName = firstName;
		
		String fatherName = txtFatherName.getText().trim();
		if(!fatherName.isEmpty()) this.fatherName = fatherName;
		
		String grandfatherName = txtGrandfatherName.getText().trim();
		if(!grandfatherName.isEmpty()) this.grandfatherName = grandfatherName;
		
		String familyName = txtFamilyName.getText().trim();
		if(!familyName.isEmpty()) this.familyName = familyName;
		
		ComboBoxItem<Gender> genderItem = cboGender.getValue();
		if(genderItem != null) this.gender = genderItem.getItem();
		
		ComboBoxItem<Country> nationalityItem = cboNationality.getValue();
		if(nationalityItem != null) this.nationality = nationalityItem.getItem();
		
		String occupation = txtOccupation.getText();
		if(!occupation.isEmpty()) this.occupation = occupation;
		
		String birthPlace = txtBirthPlace.getText();
		if(!birthPlace.isEmpty()) this.birthPlace = birthPlace;
		
		LocalDate birthDate = dpBirthDate.getValue();
		if(birthDate != null) this.birthDate = birthDate;
		
		String sPersonId = txtPersonId.getText();
		if(!sPersonId.isEmpty()) this.personId = Long.parseLong(sPersonId);
		
		ComboBoxItem<PersonType> personTypeItem = cboPersonType.getValue();
		if(personTypeItem != null) this.personType = personTypeItem.getItem();
		
		String documentId = txtDocumentId.getText();
		if(!documentId.isEmpty()) this.documentId = documentId;
		
		ComboBoxItem<DocumentType> documentTypeItem = cboDocumentType.getValue();
		if(documentTypeItem != null) this.documentType = documentTypeItem.getItem();
		
		LocalDate documentIssuanceDate = dpDocumentIssuanceDate.getValue();
		if(documentIssuanceDate != null) this.documentIssuanceDate = documentIssuanceDate;
		
		LocalDate documentExpiryDate = dpDocumentExpiryDate.getValue();
		if(documentExpiryDate != null) this.documentExpiryDate = documentExpiryDate;
	}
}