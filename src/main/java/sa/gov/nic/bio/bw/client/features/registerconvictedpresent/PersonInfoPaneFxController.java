package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PersonInfoPaneFxController extends WizardStepFxControllerBase
{
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
	public static final String KEY_PERSON_INFO_BIRTH_DATE_USE_HIJRI = "PERSON_INFO_BIRTH_DATE_USE_HIJRI";
	public static final String KEY_PERSON_INFO_SAMIS_ID = "PERSON_INFO_SAMIS_ID";
	public static final String KEY_PERSON_INFO_SAMIS_ID_TYPE = "PERSON_INFO_SAMIS_ID_TYPE";
	public static final String KEY_PERSON_INFO_DOCUMENT_ID = "PERSON_INFO_DOCUMENT_ID";
	public static final String KEY_PERSON_INFO_DOCUMENT_TYPE = "PERSON_INFO_DOCUMENT_TYPE";
	public static final String KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE = "PERSON_INFO_DOCUMENT_ISSUANCE_DATE";
	public static final String KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE_USE_HIJRI =
																		"PERSON_INFO_DOCUMENT_ISSUANCE_DATE_USE_HIJRI";
	public static final String KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE = "PERSON_INFO_DOCUMENT_EXPIRY_DATE";
	public static final String KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE_USE_HIJRI =
																		"PERSON_INFO_DOCUMENT_EXPIRY_DATE_USE_HIJRI";
	private static final String KEY_PERSON_INFO_FIRST_NAME_DISABLED = "PERSON_INFO_FIRST_NAME_DISABLED";
	private static final String KEY_PERSON_INFO_FATHER_NAME_DISABLED = "PERSON_INFO_FATHER__NAME_DISABLED";
	private static final String KEY_PERSON_INFO_GRANDFATHER_NAME_DISABLED = "PERSON_INFO_GRANDFATHER_NAME_DISABLED";
	private static final String KEY_PERSON_INFO_FAMILY_NAME_DISABLED = "PERSON_INFO_FAMILY_NAME_DISABLED";
	private static final String KEY_PERSON_INFO_GENDER_DISABLED = "PERSON_INFO_GENDER_DISABLED";
	private static final String KEY_PERSON_INFO_NATIONALITY_DISABLED = "PERSON_INFO_NATIONALITY_DISABLED";
	private static final String KEY_PERSON_INFO_OCCUPATION_DISABLED = "PERSON_INFO_OCCUPATION_DISABLED";
	private static final String KEY_PERSON_INFO_BIRTH_PLACE_DISABLED = "PERSON_INFO_BIRTH_PLACE_DISABLED";
	private static final String KEY_PERSON_INFO_BIRTH_DATE_DISABLED = "PERSON_INFO_BIRTH_DATE_DISABLED";
	private static final String KEY_PERSON_INFO_SAMIS_ID_DISABLED = "PERSON_INFO_SAMIS_ID_DISABLED";
	private static final String KEY_PERSON_INFO_SAMIS_ID_TYPE_DISABLED = "PERSON_INFO_SAMIS_ID_TYPE_DISABLED";
	private static final String KEY_PERSON_INFO_DOCUMENT_ID_DISABLED = "PERSON_INFO_DOCUMENT_ID_DISABLED";
	private static final String KEY_PERSON_INFO_DOCUMENT_TYPE_DISABLED = "PERSON_INFO_DOCUMENT_TYPE_DISABLED";
	private static final String KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE_DISABLED =
																		"PERSON_INFO_DOCUMENT_ISSUANCE_DATE_DISABLED";
	private static final String KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE_DISABLED =
																		"PERSON_INFO_DOCUMENT_EXPIRY_DATE_DISABLED";
	
	@FXML private Label lblGeneralFileNumber;
	@FXML private TextField txtFirstName;
	@FXML private TextField txtFatherName;
	@FXML private TextField txtGrandfatherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtOccupation;
	@FXML private TextField txtBirthPlace;
	@FXML private TextField txtSamisId;
	@FXML private TextField txtDocumentId;
	@FXML private ComboBox<ItemWithText<GenderType>> cboGender;
	@FXML private ComboBox<HideableItem<CountryBean>> cboNationality;
	@FXML private ComboBox<ItemWithText<SamisIdType>> cboSamisIdType;
	@FXML private ComboBox<ItemWithText<DocumentType>> cboDocumentType;
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
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/personInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnNext.setOnAction(actionEvent -> goNext());
		
		@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
												Context.getUserSession().getAttribute("lookups.countries");
		
		@SuppressWarnings("unchecked") List<SamisIdType> samisIdTypes = (List<SamisIdType>)
												Context.getUserSession().getAttribute("lookups.samisIdTypes");
		
		@SuppressWarnings("unchecked") List<DocumentType> documentTypes = (List<DocumentType>)
												Context.getUserSession().getAttribute("lookups.documentTypes");
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, countries);
		
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboNationality);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboSamisIdType);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboDocumentType);
		
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		ObservableList<ItemWithText<SamisIdType>> SamisIdTypeItems = FXCollections.observableArrayList();
		samisIdTypes.forEach(idType -> SamisIdTypeItems.add(new ItemWithText<>(idType, arabic ?
															idType.getDescriptionAR() : idType.getDescriptionEN())));
		cboSamisIdType.setItems(SamisIdTypeItems);
		
		ObservableList<ItemWithText<DocumentType>> DocumentTypeItems = FXCollections.observableArrayList();
		documentTypes.forEach(documentType -> DocumentTypeItems.add(new ItemWithText<>(documentType,
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
		GuiUtils.applyValidatorToTextField(txtSamisId, "\\d*", "[^\\d]", 10);
		GuiUtils.applyValidatorToTextField(txtDocumentId, null, null, 10);
		
		BooleanBinding txtFirstNameBinding = txtFirstName.textProperty().isEmpty();
		BooleanBinding txtFamilyNameBinding = txtFamilyName.textProperty().isEmpty();
		BooleanBinding cboGenderBinding = cboGender.valueProperty().isNull();
		BooleanBinding cboNationalityBinding = cboNationality.valueProperty().isNull();
		
		btnNext.disableProperty().bind(txtFirstNameBinding.or(txtFamilyNameBinding).or(cboGenderBinding)
				                                                                   .or(cboNationalityBinding));
	}
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.setupNationalityComboBox(cboNationality);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Boolean firstNameDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_FIRST_NAME_DISABLED);
			Boolean fatherNameDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_FATHER_NAME_DISABLED);
			Boolean grandfatherNameDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_GRANDFATHER_NAME_DISABLED);
			Boolean familyNameDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_FAMILY_NAME_DISABLED);
			Boolean genderDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_GENDER_DISABLED);
			Boolean nationalityDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_NATIONALITY_DISABLED);
			Boolean occupationDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_OCCUPATION_DISABLED);
			Boolean birthPlaceDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_BIRTH_PLACE_DISABLED);
			Boolean birthDateDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_BIRTH_DATE_DISABLED);
			Boolean samisIdDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_SAMIS_ID_DISABLED);
			Boolean samisIdTypeDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_SAMIS_ID_TYPE_DISABLED);
			Boolean documentIdDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_ID_DISABLED);
			Boolean documentTypeDisabled = (Boolean) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_TYPE_DISABLED);
			Boolean documentIssuanceDateDisabled = (Boolean)
													uiInputData.get(KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE_DISABLED);
			Boolean documentExpiryDateDisabled = (Boolean)
													uiInputData.get(KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE_DISABLED);
			
			Node focusedNode = null;
			
			String firstName = (String) uiInputData.get(KEY_PERSON_INFO_FIRST_NAME);
			String fatherName = (String) uiInputData.get(KEY_PERSON_INFO_FATHER_NAME);
			String grandfatherName = (String) uiInputData.get(KEY_PERSON_INFO_GRANDFATHER_NAME);
			String familyName = (String) uiInputData.get(KEY_PERSON_INFO_FAMILY_NAME);
			
			if(firstName != null && !firstName.trim().isEmpty() && !firstName.trim().equals("-"))
			{
				txtFirstName.setText(firstName);
				if(firstNameDisabled == null || firstNameDisabled) txtFirstName.setDisable(true);
			}
			else focusedNode = txtFirstName;
			
			if(fatherName != null && !fatherName.trim().isEmpty() && !fatherName.trim().equals("-"))
			{
				txtFatherName.setText(fatherName);
				if(fatherNameDisabled == null || fatherNameDisabled) txtFatherName.setDisable(true);
			}
			else if(focusedNode == null) focusedNode = txtFatherName;
			
			if(grandfatherName != null && !grandfatherName.trim().isEmpty() && !grandfatherName.trim().equals("-"))
			{
				txtGrandfatherName.setText(grandfatherName);
				if(grandfatherNameDisabled == null || grandfatherNameDisabled) txtGrandfatherName.setDisable(true);
			}
			else if(focusedNode == null) focusedNode = txtGrandfatherName;
			
			if(familyName != null && !familyName.trim().isEmpty() && !familyName.trim().equals("-"))
			{
				txtFamilyName.setText(familyName);
				if(familyNameDisabled == null || familyNameDisabled) txtFamilyName.setDisable(true);
			}
			else if(focusedNode == null) focusedNode = txtFamilyName;
			
			Long generalFileNumber = (Long) uiInputData.get(KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
			
			if(generalFileNumber != null) lblGeneralFileNumber.setText(String.valueOf(generalFileNumber));
			else lblGeneralFileNumber.setTextFill(Color.RED);
			
			GenderType genderType = (GenderType) uiInputData.get(KEY_PERSON_INFO_GENDER);
			if(genderType != null) cboGender.getItems()
					                        .stream()
					                        .filter(item -> item.getItem() == genderType)
					                        .findFirst()
					                        .ifPresent(value ->
					                        {
					                        	cboGender.setValue(value);
						                        if(genderDisabled == null || genderDisabled) cboGender.setDisable(true);
					                        });
			else if(focusedNode == null) focusedNode = cboGender;
			
			CountryBean countryBean = (CountryBean) uiInputData.get(KEY_PERSON_INFO_NATIONALITY);
			if(countryBean != null && countryBean.getCode() != 0)
						cboNationality.getItems()
						              .stream()
						              .filter(item -> item.getObject() == countryBean)
						              .findFirst()
						              .ifPresent(value ->
						              {
						                  cboNationality.setValue(value);
						                  if(nationalityDisabled == null || nationalityDisabled)
						                  	                            cboNationality.setDisable(true);
						              });
			else
			{
				String text = resources.getString("combobox.unknownNationality");
				CountryBean unknownNationality = new CountryBean(0, null,
				                                                 text, text);
				
				HideableItem<CountryBean> hideableItem = new HideableItem<>(unknownNationality);
				hideableItem.setText(text);
				
				ObservableList<HideableItem<CountryBean>> items = FXCollections.observableArrayList();
				items.add(hideableItem);
				items.addAll(cboNationality.getItems());
				cboNationality.setItems(items);
				cboNationality.setValue(hideableItem);
			}
			
			String occupation = (String) uiInputData.get(KEY_PERSON_INFO_OCCUPATION);
			if(occupation != null && !occupation.trim().isEmpty())
			{
				txtOccupation.setText(occupation);
				if(occupationDisabled == null || occupationDisabled) txtOccupation.setDisable(true);
			}
			
			String birthPlace = (String) uiInputData.get(KEY_PERSON_INFO_BIRTH_PLACE);
			if(birthPlace != null && !birthPlace.trim().isEmpty())
			{
				txtBirthPlace.setText(birthPlace);
				if(birthPlaceDisabled == null || birthPlaceDisabled) txtBirthPlace.setDisable(true);
			}
			
			LocalDate birthDate = (LocalDate) uiInputData.get(KEY_PERSON_INFO_BIRTH_DATE);
			if(birthDate != null)
			{
				dpBirthDate.setValue(birthDate);
				
				if(birthDateDisabled == null || birthDateDisabled) dpBirthDate.setDisable(true);
			}
			
			Boolean birthDateUseHijri = (Boolean) uiInputData.get(KEY_PERSON_INFO_BIRTH_DATE_USE_HIJRI);
			if(birthDateUseHijri == null || birthDateUseHijri) rdoBirthDateUseHijri.setSelected(true);
			else rdoBirthDateUseGregorian.setSelected(true);
			
			Long samisId = (Long) uiInputData.get(KEY_PERSON_INFO_SAMIS_ID);
			if(samisId != null)
			{
				txtSamisId.setText(String.valueOf(samisId));
				if(samisIdDisabled == null || samisIdDisabled) txtSamisId.setDisable(true);
			}
			
			SamisIdType samisIdType = (SamisIdType) uiInputData.get(KEY_PERSON_INFO_SAMIS_ID_TYPE);
			if(samisIdType != null) cboSamisIdType.getItems()
					.stream()
					.filter(item -> item.getItem() == samisIdType)
					.findFirst()
					.ifPresent(value ->
					{
					    cboSamisIdType.setValue(value);
					    if(samisIdTypeDisabled == null || samisIdTypeDisabled) cboSamisIdType.setDisable(true);
					});
			
			String documentId = (String) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_ID);
			if(documentId != null && !documentId.trim().isEmpty())
			{
				txtDocumentId.setText(documentId);
				if(documentIdDisabled == null || documentIdDisabled) txtDocumentId.setDisable(true);
			}
			
			DocumentType documentType = (DocumentType) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_TYPE);
			if(documentType != null) cboDocumentType.getItems()
						.stream()
						.filter(item -> item.getItem() == documentType)
						.findFirst()
						.ifPresent(value ->
						{
							cboDocumentType.setValue(value);
							if(documentTypeDisabled == null || documentTypeDisabled) cboDocumentType.setDisable(true);
						});
			
			LocalDate idIssuanceDate = (LocalDate) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE);
			if(idIssuanceDate != null)
			{
				dpDocumentIssuanceDate.setValue(idIssuanceDate);
				
				if(documentIssuanceDateDisabled == null || documentIssuanceDateDisabled)
																			dpDocumentIssuanceDate.setDisable(true);
			}
			
			Boolean idIssuanceDateUseHijri = (Boolean)
												uiInputData.get(KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE_USE_HIJRI);
			if(idIssuanceDateUseHijri == null || idIssuanceDateUseHijri)
																	rdoDocumentIssuanceDateUseHijri.setSelected(true);
			else rdoDocumentIssuanceDateUseGregorian.setSelected(true);
			
			LocalDate idExpiryDate = (LocalDate) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE);
			if(idExpiryDate != null)
			{
				dpDocumentExpiryDate.setValue(idExpiryDate);
				
				if(documentExpiryDateDisabled == null || documentExpiryDateDisabled)
																			dpDocumentExpiryDate.setDisable(true);
			}
			
			Boolean idExpiryDateUseHijri = (Boolean) uiInputData.get(KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE_USE_HIJRI);
			if(idExpiryDateUseHijri == null || idExpiryDateUseHijri) rdoDocumentExpiryDateUseHijri.setSelected(true);
			else rdoDocumentExpiryDateUseGregorian.setSelected(true);
			
			if(focusedNode != null) focusedNode.requestFocus();
			else btnNext.requestFocus();
		}
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		String text = txtSamisId.getText();
		if(text != null && !text.isEmpty()) uiDataMap.put(KEY_PERSON_INFO_SAMIS_ID, Long.parseLong(text));
		else uiDataMap.remove(KEY_PERSON_INFO_SAMIS_ID);
		
		text = txtDocumentId.getText();
		if(text != null && !text.isEmpty()) uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_ID, text);
		else uiDataMap.remove(KEY_PERSON_INFO_DOCUMENT_ID);
		
		ItemWithText<GenderType> genderItem = cboGender.getValue();
		if(genderItem != null) uiDataMap.put(KEY_PERSON_INFO_GENDER, genderItem.getItem());
		else uiDataMap.remove(KEY_PERSON_INFO_GENDER);
		
		HideableItem<CountryBean> nationalityItem = cboNationality.getValue();
		if(nationalityItem != null) uiDataMap.put(KEY_PERSON_INFO_NATIONALITY, nationalityItem.getObject());
		else uiDataMap.remove(KEY_PERSON_INFO_NATIONALITY);
		
		uiDataMap.put(KEY_PERSON_INFO_FIRST_NAME, txtFirstName.getText());
		uiDataMap.put(KEY_PERSON_INFO_FATHER_NAME, txtFatherName.getText());
		uiDataMap.put(KEY_PERSON_INFO_GRANDFATHER_NAME, txtGrandfatherName.getText());
		uiDataMap.put(KEY_PERSON_INFO_FAMILY_NAME, txtFamilyName.getText());
		
		String occupation = txtOccupation.getText();
		if(occupation != null && !occupation.trim().isEmpty()) uiDataMap.put(KEY_PERSON_INFO_OCCUPATION, occupation);
		else uiDataMap.remove(KEY_PERSON_INFO_OCCUPATION);
		
		String birthPlace = txtBirthPlace.getText();
		if(birthPlace != null && !birthPlace.trim().isEmpty()) uiDataMap.put(KEY_PERSON_INFO_BIRTH_PLACE, birthPlace);
		else uiDataMap.remove(KEY_PERSON_INFO_BIRTH_PLACE);
		
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_DATE, dpBirthDate.getValue());
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_DATE_USE_HIJRI, rdoBirthDateUseHijri.isSelected());
		
		ItemWithText<SamisIdType> samisIdTypeValue = cboSamisIdType.getValue();
		if(samisIdTypeValue != null) uiDataMap.put(KEY_PERSON_INFO_SAMIS_ID_TYPE, samisIdTypeValue.getItem());
		else uiDataMap.remove(KEY_PERSON_INFO_SAMIS_ID_TYPE);
		
		ItemWithText<DocumentType> documentTypeValue = cboDocumentType.getValue();
		if(documentTypeValue != null) uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_TYPE, documentTypeValue.getItem());
		else uiDataMap.remove(KEY_PERSON_INFO_DOCUMENT_TYPE);
		
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE, dpDocumentIssuanceDate.getValue());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE_USE_HIJRI, rdoDocumentIssuanceDateUseHijri.isSelected());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE, dpDocumentExpiryDate.getValue());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE_USE_HIJRI, rdoDocumentExpiryDateUseHijri.isSelected());
		
		uiDataMap.put(KEY_PERSON_INFO_FIRST_NAME_DISABLED, txtFirstName.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_FATHER_NAME_DISABLED, txtFatherName.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_GRANDFATHER_NAME_DISABLED, txtGrandfatherName.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_FAMILY_NAME_DISABLED, txtFamilyName.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_GENDER_DISABLED, cboGender.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_NATIONALITY_DISABLED, cboNationality.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_OCCUPATION_DISABLED, txtOccupation.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_PLACE_DISABLED, txtBirthPlace.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_DATE_DISABLED, dpBirthDate.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_SAMIS_ID_DISABLED, txtSamisId.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_SAMIS_ID_TYPE_DISABLED, cboSamisIdType.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_ID_DISABLED, txtDocumentId.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_TYPE_DISABLED, cboDocumentType.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE_DISABLED, dpDocumentIssuanceDate.isDisabled());
		uiDataMap.put(KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE_DISABLED, dpDocumentExpiryDate.isDisabled());
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("printConvictedPresent.startingOver.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
}