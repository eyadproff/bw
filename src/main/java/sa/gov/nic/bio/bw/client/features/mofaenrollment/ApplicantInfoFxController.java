package sa.gov.nic.bio.bw.client.features.mofaenrollment;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.enums.Gender;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.mofaenrollment.webservice.VisaTypeBean;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ApplicantInfoFxController extends WizardStepFxControllerBase
{
	private static final String KEY_FIRST_NAME = "ApplicantInfo.firstName";
	private static final String KEY_SECOND_NAME = "ApplicantInfo.secondName";
	private static final String KEY_OTHER_NAME = "ApplicantInfo.otherName";
	private static final String KEY_FAMILY_NAME = "ApplicantInfo.familyName";
	private static final String KEY_GENDER = "ApplicantInfo.gender";
	private static final String KEY_NATIONALITY = "ApplicantInfo.nationality";
	private static final String KEY_BIRTH_DATE = "ApplicantInfo.birthDate";
	private static final String KEY_BIRTH_DATE_SHOW_HIJRI = "ApplicantInfo.birthDateShowHijri";
	private static final String KEY_PASSPORT_NUMBER = "ApplicantInfo.passportNumber";
	private static final String KEY_VISA_TYPE = "ApplicantInfo.visaType";
	
	@FXML private TextField txtFirstName;
	@FXML private TextField txtSecondName;
	@FXML private TextField txtOtherName;
	@FXML private TextField txtFamilyName;
	@FXML private ComboBox<ItemWithText<Gender>> cboGender;
	@FXML private ComboBox<HideableItem<NationalityBean>> cboNationality;
	@FXML private DatePicker dpBirthDate;
	@FXML private CheckBox cbBirthDateShowHijri;
	@FXML private TextField txtPassportNumber;
	@FXML private ComboBox<HideableItem<VisaTypeBean>> cboVisaType;
	@FXML private Button btnNext;
	
	private Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/applicantInfo.fxml");
	}
	
	@FXML
	private void initialize()
	{
		btnNext.setOnAction(event -> goNext());
		
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<NationalityBean> nationalities = (List<NationalityBean>) userSession.getAttribute("lookups.mofa.nationalities");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.mofa.visaTypes");
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, nationalities);
		GuiUtils.addAutoCompletionSupportToComboBox(cboVisaType, visaTypes);
		
		GuiUtils.makeComboBoxOpenableByPressingSpaceBarAndEnter(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBarAndEnter(cboNationality);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBarAndEnter(cboVisaType);
		
		cbBirthDateShowHijri.selectedProperty().addListener(((observable, oldValue, newValue) ->
		{
			if(newValue) dpBirthDate.setChronology(HijrahChronology.INSTANCE);
			else dpBirthDate.setChronology(null);
		}));
		
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
		
		cboVisaType.setConverter(new StringConverter<HideableItem<VisaTypeBean>>()
		{
			@Override
			public String toString(HideableItem<VisaTypeBean> object)
			{
				if(object == null) return "";
				else return object.getText();
			}
			
			@Override
			public HideableItem<VisaTypeBean> fromString(String string)
			{
				if(string == null || string.trim().isEmpty()) return null;
				
				for(HideableItem<VisaTypeBean> visaTypesBean : cboVisaType.getItems())
				{
					if(string.equals(visaTypesBean.getText())) return visaTypesBean;
				}
				
				return null;
			}
		});
		
		initDatePicker();
		
		BooleanBinding txtFirstNameBinding = txtFirstName.textProperty().isEmpty();
		BooleanBinding txtSecondNameBinding = txtSecondName.textProperty().isEmpty();
		BooleanBinding txtOtherNameBinding = txtOtherName.textProperty().isEmpty();
		BooleanBinding txtFamilyNameBinding = txtFamilyName.textProperty().isEmpty();
		BooleanBinding cboGenderBinding = cboGender.valueProperty().isNull();
		BooleanBinding cboNationalityBinding = cboNationality.valueProperty().isNull();
		BooleanBinding dpBirthDateBinding = dpBirthDate.valueProperty().isNull();
		BooleanBinding txtPassportNumberBinding = txtPassportNumber.textProperty().isNull();
		BooleanBinding cboVisaTypeBinding = cboVisaType.valueProperty().isNull();
		
		/*btnNext.disableProperty().bind(txtFirstNameBinding.or(txtSecondNameBinding).or(txtOtherNameBinding)
				                 .or(txtFamilyNameBinding).or(cboGenderBinding).or(cboNationalityBinding)
				                 .or(dpBirthDateBinding).or(txtPassportNumberBinding).or(cboVisaTypeBinding));*/
		
		GuiUtils.makeButtonClickable(btnNext);
	}
	
	@Override
	public void onControllerReady()
	{
		cboNationality.getItems().forEach(item ->
		                                  {
			                                  NationalityBean nationalityBean = item.getObject();
			
			                                  String text;
			                                  if(coreFxController.getGuiState().getLanguage() == GuiLanguage.ARABIC) text = nationalityBean.getDescriptionAR();
			                                  else text = nationalityBean.getDescriptionEN();
			
			                                  String resultText = text.trim() + " (" + nationalityBean.getMofaNationalityCode() + ")";
			                                  String normalizedText = Normalizer.normalize(resultText, Normalizer.Form.NFKC);
			
			                                  item.setText(normalizedText);
		                                  });
		
		cboVisaType.getItems().forEach(item ->
		                               {
			                               VisaTypeBean visaTypeBean = item.getObject();
			
			                               String text;
			                               if(coreFxController.getGuiState().getLanguage() == GuiLanguage.ARABIC) text = visaTypeBean.getDescriptionAR();
			                               else text = visaTypeBean.getDescriptionEN();
			
			                               String resultText = text.trim();
			                               String normalizedText = Normalizer.normalize(resultText, Normalizer.Form.NFKC);
			
			                               item.setText(normalizedText);
		                               });
		
		cboVisaType.setPrefWidth(cboVisaType.getWidth());
		cboVisaType.setMinWidth(cboVisaType.getWidth());
		
		loadOldDateIfExist();
		
		txtFirstName.requestFocus();
		txtFirstName.positionCaret(txtFirstName.getLength());
	}
	
	@Override
	protected void onGoingNext(Map<String, String> uiDataMap)
	{
		uiDataMap.put(KEY_FIRST_NAME, txtFirstName.getText());
		uiDataMap.put(KEY_SECOND_NAME, txtSecondName.getText());
		uiDataMap.put(KEY_OTHER_NAME, txtOtherName.getText());
		uiDataMap.put(KEY_FAMILY_NAME, txtFamilyName.getText());
		uiDataMap.put(KEY_PASSPORT_NUMBER, txtPassportNumber.getText());
		
		ItemWithText<Gender> genderItem = cboGender.getValue();
		String gender = null;
		if(genderItem != null) gender = genderItem.getItem().name();
		uiDataMap.put(KEY_GENDER, gender);
		
		HideableItem<NationalityBean> nationalityItem = cboNationality.getValue();
		String nationality = null;
		if(nationalityItem != null) nationality = String.valueOf(nationalityItem.getObject().getCode());
		uiDataMap.put(KEY_NATIONALITY, nationality);
		
		HideableItem<VisaTypeBean> visaTypeItem = cboVisaType.getValue();
		String visaType = null;
		if(visaTypeItem != null) visaType = String.valueOf(visaTypeItem.getObject().getCode());
		uiDataMap.put(KEY_VISA_TYPE, visaType);
		
		LocalDate birthDateValue = dpBirthDate.getValue();
		String birthDate = null;
		if(birthDateValue != null) birthDate = dpBirthDate.getEditor().getText();
		uiDataMap.put(KEY_BIRTH_DATE, birthDate);
		
		String birthDateShowHijri = String.valueOf(cbBirthDateShowHijri.isSelected());
		uiDataMap.put(KEY_BIRTH_DATE_SHOW_HIJRI, birthDateShowHijri);
	}
	
	private void loadOldDateIfExist()
	{
		String firstName = (String) inputData.get(KEY_FIRST_NAME);
		String secondName = (String) inputData.get(KEY_SECOND_NAME);
		String otherName = (String) inputData.get(KEY_OTHER_NAME);
		String familyName = (String) inputData.get(KEY_FAMILY_NAME);
		String gender = (String) inputData.get(KEY_GENDER);
		String nationality = (String) inputData.get(KEY_NATIONALITY);
		String birthDate = (String) inputData.get(KEY_BIRTH_DATE);
		String birthDateShowHijri = (String) inputData.get(KEY_BIRTH_DATE_SHOW_HIJRI);
		String passportNumber = (String) inputData.get(KEY_PASSPORT_NUMBER);
		String visaType = (String) inputData.get(KEY_VISA_TYPE);
		
		if(firstName != null) txtFirstName.setText(firstName);
		if(secondName != null) txtSecondName.setText(secondName);
		if(otherName != null) txtOtherName.setText(otherName);
		if(familyName != null) txtFamilyName.setText(familyName);
		if(passportNumber != null) txtPassportNumber.setText(passportNumber);
		
		if(gender != null) cboGender.getItems()
				.stream()
				.filter(item -> item.getItem().name().equals(gender))
				.findFirst()
				.ifPresent(cboGender::setValue);
		
		if(nationality != null) cboNationality.getItems()
				.stream()
				.filter(item -> String.valueOf(item.getObject().getCode()).equals(nationality))
				.findFirst()
				.ifPresent(cboNationality::setValue);
		
		if(visaType != null) cboVisaType.getItems()
				.stream()
				.filter(item -> String.valueOf(item.getObject().getCode()).equals(visaType))
				.findFirst()
				.ifPresent(cboVisaType::setValue);
		
		if(birthDate != null) dpBirthDate.setValue(AppUtils.parseFormalDate(birthDate));
		if(birthDateShowHijri != null) cbBirthDateShowHijri.setSelected(Boolean.parseBoolean(birthDateShowHijri));
	}
	
	private void initDatePicker()
	{
		Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell()
		{
			@Override
			public void updateItem(LocalDate item, boolean empty)
			{
				super.updateItem(item, empty);
				
				if(!birthDateValidator.test(item))
				{
					Platform.runLater(() -> setDisable(true));
				}
			}
		};
		
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>()
		{
			DateTimeFormatter dateFormatterForParsing = DateTimeFormatter.ofPattern("d/M/yyyy", AppConstants.Locales.SAUDI_EN_LOCALE);
			DateTimeFormatter dateFormatterForFormatting = DateTimeFormatter.ofPattern("dd/MM/yyyy", AppConstants.Locales.SAUDI_EN_LOCALE);
			
			@Override
			public String toString(LocalDate date)
			{
				if(date != null) return dateFormatterForFormatting.format(date);
				else return "";
			}
			
			@Override
			public LocalDate fromString(String string)
			{
				if(string != null && !string.isEmpty())
				{
					// support "/", "\" and "-" as date separators
					string = string.replace("\\", "/");
					string = string.replace("-", "/");
					
					// if the input is 8 characters long, we will add the separators
					if(string.length() == 8 && !string.contains("/"))
					{
						string = string.substring(0, 2) + "/" + string.substring(2, 4) + "/" + string.substring(4, 8);
					}
					// if the input is 6 characters long, we will consider day and month are both single digits and then add the separators
					else if(string.length() == 6 && !string.contains("/"))
					{
						string = string.substring(0, 1) + "/" + string.substring(1, 2) + "/" + string.substring(2, 6);
					}
					
					LocalDate date = LocalDate.parse(string, dateFormatterForParsing);
					
					if(!birthDateValidator.test(date)) return null;
					else return date;
				}
				else return null;
			}
		};
		
		dpBirthDate.setDayCellFactory(dayCellFactory);
		dpBirthDate.setConverter(converter);
	}
}