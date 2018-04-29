package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.GenderType;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PersonInfoPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_PERSON_INFO_PHOTO = "PERSON_INFO_PHOTO";
	public static final String KEY_PERSON_INFO_FIRST_NAME = "PERSON_INFO_FIRST_NAME";
	public static final String KEY_PERSON_INFO_FATHER_NAME = "PERSON_INFO_FATHER__NAME";
	public static final String KEY_PERSON_INFO_GRANDFATHER_NAME = "PERSON_INFO_GRANDFATHER_NAME";
	public static final String KEY_PERSON_INFO_FAMILY_NAME = "PERSON_INFO_FAMILY_NAME";
	public static final String KEY_PERSON_INFO_GENERAL_FILE_NUMBER = "PERSON_INFO_GENERAL_FILE_NUMBER";
	public static final String KEY_PERSON_INFO_GENDER = "PERSON_INFO_GENDER";
	public static final String KEY_PERSON_INFO_NATIONALITY = "PERSON_INFO_NATIONALITY";
	public static final String KEY_PERSON_INFO_OCCUPATION = "PERSON_INFO_OCCUPATION";
	public static final String KEY_PERSON_INFO_BIRTH_PLACE = "PERSON_INFO_BIRTH_PLACE";
	public static final String KEY_PERSON_INFO_BIRTH_DATE = "PERSON_INFO_BIRTH_DATE";
	public static final String KEY_PERSON_INFO_BIRTH_DATE_SHOW_HIJRI = "PERSON_INFO_BIRTH_DATE_SHOW_HIJRI";
	public static final String KEY_PERSON_INFO_ID_NUMBER = "PERSON_INFO_ID_NUMBER";
	public static final String KEY_PERSON_INFO_ID_TYPE = "PERSON_INFO_ID_TYPE";
	public static final String KEY_PERSON_INFO_ID_ISSUANCE_DATE = "PERSON_INFO_ID_ISSUANCE_DATE";
	public static final String KEY_PERSON_INFO_ID_ISSUANCE_DATE_SHOW_HIJRI = "PERSON_INFO_ID_ISSUANCE_DATE_SHOW_HIJRI";
	public static final String KEY_PERSON_INFO_ID_EXPIRY_DATE = "PERSON_INFO_ID_EXPIRY_DATE";
	public static final String KEY_PERSON_INFO_ID_EXPIRY_DATE_SHOW_HIJRI = "PERSON_INFO_ID_EXPIRY_DATE_SHOW_HIJRI";
	
	@FXML private TextField txtFirstName;
	@FXML private TextField txtFatherName;
	@FXML private TextField txtGrandfatherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtGeneralFileNumber;
	@FXML private TextField txtOccupation;
	@FXML private TextField txtBirthPlace;
	@FXML private TextField txtIdNumber;
	@FXML private TextField txtIdType;
	@FXML private ComboBox<ItemWithText<GenderType>> cboGender;
	@FXML private ComboBox<HideableItem<NationalityBean>> cboNationality;
	@FXML private CheckBox cbBirthDateShowHijri;
	@FXML private CheckBox cbIdIssuanceDateShowHijri;
	@FXML private CheckBox cbIdExpiryDateShowHijri;
	@FXML private DatePicker dpBirthDate;
	@FXML private DatePicker dpIdIssuanceDate;
	@FXML private DatePicker dpIdExpiryDate;
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
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnNext.setOnAction(actionEvent -> goNext());
		
		@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
												Context.getUserSession().getAttribute("lookups.nationalities");
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, nationalities);
		
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboNationality);
		
		cboNationality.setConverter(new StringConverter<HideableItem<NationalityBean>>()
		{
			@Override
			public String toString(HideableItem<NationalityBean> object)
			{
				if(object == null) return "";
				else return object.getText();
			}
			
			@Override
			public HideableItem<NationalityBean> fromString(String string)
			{
				if(string == null || string.trim().isEmpty()) return null;
				
				for(HideableItem<NationalityBean> nationalityBean : cboNationality.getItems())
				{
					if(string.equals(nationalityBean.getText())) return nationalityBean;
				}
				
				return null;
			}
		});
		
		GuiUtils.initDatePicker(cbBirthDateShowHijri, dpBirthDate, birthDateValidator);
		GuiUtils.initDatePicker(cbIdIssuanceDateShowHijri, dpIdIssuanceDate, null);
		GuiUtils.initDatePicker(cbIdExpiryDateShowHijri, dpIdExpiryDate, null);
		
		GuiUtils.applyValidatorToTextField(txtIdNumber, "\\d*", "[^\\d]", 100);
		GuiUtils.applyValidatorToTextField(txtGeneralFileNumber, "\\d*", "[^\\d]",
		                                   100);
		
		BooleanBinding txtFirstNameBinding = txtFirstName.textProperty().isEmpty();
		BooleanBinding txtFatherNameBinding = txtFatherName.textProperty().isEmpty();
		BooleanBinding txtGrandfatherNameBinding = txtGrandfatherName.textProperty().isEmpty();
		BooleanBinding txtFamilyNameBinding = txtFamilyName.textProperty().isEmpty();
		BooleanBinding txtGeneralFileNumberBinding = txtGeneralFileNumber.textProperty().isEmpty();
		BooleanBinding cboGenderBinding = cboGender.valueProperty().isNull();
		BooleanBinding cboNationalityBinding = cboNationality.valueProperty().isNull();
		
		btnNext.disableProperty().bind(txtFirstNameBinding.or(txtFatherNameBinding).or(txtGrandfatherNameBinding)
	                             .or(txtFamilyNameBinding).or(txtGeneralFileNumberBinding).or(cboGenderBinding)
                                 .or(cboNationalityBinding));
	}
	
	@Override
	protected void onAttachedToScene()
	{
		cboNationality.getItems().forEach(item ->
		{
		    NationalityBean nationalityBean = item.getObject();
		
		    String text;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC) text = nationalityBean.getDescriptionAR();
		    else text = nationalityBean.getDescriptionEN();
		
		    String resultText = text.trim() + " (" + nationalityBean.getMofaNationalityCode() + ")";
		    item.setText(resultText);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Node focusedNode = null;
			
			String firstName = (String) uiInputData.get(KEY_PERSON_INFO_FIRST_NAME);
			String fatherName = (String) uiInputData.get(KEY_PERSON_INFO_FATHER_NAME);
			String grandfatherName = (String) uiInputData.get(KEY_PERSON_INFO_GRANDFATHER_NAME);
			String familyName = (String) uiInputData.get(KEY_PERSON_INFO_FAMILY_NAME);
			
			if(firstName != null && !firstName.trim().isEmpty()) txtFirstName.setText(firstName);
			else focusedNode = txtFirstName;
			
			if(fatherName != null && !fatherName.trim().isEmpty()) txtFatherName.setText(fatherName);
			else if(focusedNode == null) focusedNode = txtFatherName;
			
			if(grandfatherName != null && !grandfatherName.trim().isEmpty())
																		txtGrandfatherName.setText(grandfatherName);
			else if(focusedNode == null) focusedNode = txtGrandfatherName;
			
			if(familyName != null && !familyName.trim().isEmpty()) txtFamilyName.setText(familyName);
			else if(focusedNode == null) focusedNode = txtFamilyName;
			
			Long generalFileNumber = (Long) uiInputData.get(KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
			
			if(generalFileNumber != null) txtGeneralFileNumber.setText(String.valueOf(generalFileNumber));
			else if(focusedNode == null) focusedNode = txtGeneralFileNumber;
			
			GenderType genderType = (GenderType) uiInputData.get(KEY_PERSON_INFO_GENDER);
			if(genderType != null) cboGender.getItems()
					                        .stream()
					                        .filter(item -> item.getItem() == genderType)
					                        .findFirst()
					                        .ifPresent(cboGender::setValue);
			else if(focusedNode == null) focusedNode = cboGender;
			
			NationalityBean nationalityBean = (NationalityBean) uiInputData.get(KEY_PERSON_INFO_NATIONALITY);
			if(nationalityBean != null) cboNationality.getItems()
						                              .stream()
						                              .filter(item -> item.getObject() == nationalityBean)
						                              .findFirst()
						                              .ifPresent(cboNationality::setValue);
			else
			{
				String text = resources.getString("combobox.unknownNationality");
				NationalityBean unknownNationality = new NationalityBean(0, null,
				                                                         text, text);
				
				HideableItem<NationalityBean> hideableItem = new HideableItem<>(unknownNationality);
				hideableItem.setText(text);
				
				ObservableList<HideableItem<NationalityBean>> items = FXCollections.observableArrayList();
				items.add(hideableItem);
				items.addAll(cboNationality.getItems());
				cboNationality.setItems(items);
				cboNationality.setValue(hideableItem);
			}
			
			String occupation = (String) uiInputData.get(KEY_PERSON_INFO_OCCUPATION);
			if(occupation != null && !occupation.trim().isEmpty()) txtOccupation.setText(occupation);
			else if(focusedNode == null) focusedNode = txtOccupation;
			
			String birthPlace = (String) uiInputData.get(KEY_PERSON_INFO_BIRTH_PLACE);
			if(birthPlace != null && !birthPlace.trim().isEmpty()) txtBirthPlace.setText(birthPlace);
			else if(focusedNode == null) focusedNode = txtBirthPlace;
			
			LocalDate birthDate = (LocalDate) uiInputData.get(KEY_PERSON_INFO_BIRTH_DATE);
			if(birthDate != null) dpBirthDate.setValue(birthDate);
			else if(focusedNode == null) focusedNode = dpBirthDate;
			
			Boolean birthDateShowHijri = (Boolean) uiInputData.get(KEY_PERSON_INFO_BIRTH_DATE_SHOW_HIJRI);
			cbBirthDateShowHijri.setSelected(birthDateShowHijri != null && birthDateShowHijri);
			
			String idNumber = (String) uiInputData.get(KEY_PERSON_INFO_ID_NUMBER);
			if(idNumber != null && !idNumber.trim().isEmpty()) txtIdNumber.setText(idNumber);
			else if(focusedNode == null) focusedNode = txtIdNumber;
			
			String idType = (String) uiInputData.get(KEY_PERSON_INFO_ID_TYPE);
			if(idType != null && !idType.trim().isEmpty()) txtIdType.setText(idType);
			else if(focusedNode == null) focusedNode = txtIdType;
			
			LocalDate idIssuanceDate = (LocalDate) uiInputData.get(KEY_PERSON_INFO_ID_ISSUANCE_DATE);
			if(idIssuanceDate != null) dpIdIssuanceDate.setValue(idIssuanceDate);
			else if(focusedNode == null) focusedNode = dpIdIssuanceDate;
			
			Boolean idIssuanceDateShowHijri = (Boolean) uiInputData.get(KEY_PERSON_INFO_ID_ISSUANCE_DATE_SHOW_HIJRI);
			cbIdIssuanceDateShowHijri.setSelected(idIssuanceDateShowHijri != null && idIssuanceDateShowHijri);
			
			LocalDate idExpiryDate = (LocalDate) uiInputData.get(KEY_PERSON_INFO_ID_EXPIRY_DATE);
			if(idExpiryDate != null) dpIdExpiryDate.setValue(idExpiryDate);
			else if(focusedNode == null) focusedNode = dpIdExpiryDate;
			
			Boolean idExpiryDateShowHijri = (Boolean) uiInputData.get(KEY_PERSON_INFO_ID_EXPIRY_DATE_SHOW_HIJRI);
			cbIdExpiryDateShowHijri.setSelected(idExpiryDateShowHijri != null && idExpiryDateShowHijri);
			
			if(focusedNode != null) focusedNode.requestFocus();
			else btnNext.requestFocus();
		}
	}
	
	@Override
	public void onLeaving(Map<String, Object> uiDataMap)
	{
		String text = txtGeneralFileNumber.getText();
		if(text != null && !text.isEmpty()) uiDataMap.put(KEY_PERSON_INFO_GENERAL_FILE_NUMBER, Long.parseLong(text));
		
		text = txtIdNumber.getText();
		if(text != null && !text.isEmpty()) uiDataMap.put(KEY_PERSON_INFO_ID_NUMBER, text);
		
		ItemWithText<GenderType> genderItem = cboGender.getValue();
		if(genderItem != null) uiDataMap.put(KEY_PERSON_INFO_GENDER, genderItem.getItem());
		
		HideableItem<NationalityBean> nationalityItem = cboNationality.getValue();
		if(nationalityItem != null) uiDataMap.put(KEY_PERSON_INFO_NATIONALITY, nationalityItem.getObject());
		
		uiDataMap.put(KEY_PERSON_INFO_FIRST_NAME, txtFirstName.getText());
		uiDataMap.put(KEY_PERSON_INFO_FATHER_NAME, txtFatherName.getText());
		uiDataMap.put(KEY_PERSON_INFO_GRANDFATHER_NAME, txtGrandfatherName.getText());
		uiDataMap.put(KEY_PERSON_INFO_FAMILY_NAME, txtFamilyName.getText());
		uiDataMap.put(KEY_PERSON_INFO_OCCUPATION, txtOccupation.getText());
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_PLACE, txtBirthPlace.getText());
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_DATE, dpBirthDate.getValue());
		uiDataMap.put(KEY_PERSON_INFO_BIRTH_DATE_SHOW_HIJRI, cbBirthDateShowHijri.isSelected());
		uiDataMap.put(KEY_PERSON_INFO_ID_TYPE, txtIdType.getText());
		uiDataMap.put(KEY_PERSON_INFO_ID_ISSUANCE_DATE, dpIdIssuanceDate.getValue());
		uiDataMap.put(KEY_PERSON_INFO_ID_ISSUANCE_DATE_SHOW_HIJRI, cbIdIssuanceDateShowHijri.isSelected());
		uiDataMap.put(KEY_PERSON_INFO_ID_EXPIRY_DATE, dpIdExpiryDate.getValue());
		uiDataMap.put(KEY_PERSON_INFO_ID_EXPIRY_DATE_SHOW_HIJRI, cbIdExpiryDateShowHijri.isSelected());
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