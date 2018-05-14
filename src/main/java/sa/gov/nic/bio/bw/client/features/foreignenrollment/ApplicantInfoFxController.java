package sa.gov.nic.bio.bw.client.features.foreignenrollment;

import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.VisaTypeBean;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ApplicantInfoFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(ApplicantInfoFxController.class.getName());
	
	public static final String KEY_FIRST_NAME = "ApplicantInfo.firstName";
	public static final String KEY_SECOND_NAME = "ApplicantInfo.secondName";
	public static final String KEY_OTHER_NAME = "ApplicantInfo.otherName";
	public static final String KEY_FAMILY_NAME = "ApplicantInfo.familyName";
	public static final String KEY_GENDER = "ApplicantInfo.gender";
	public static final String KEY_NATIONALITY = "ApplicantInfo.nationality";
	public static final String KEY_BIRTH_DATE = "ApplicantInfo.birthDate";
	public static final String KEY_BIRTH_DATE_SHOW_HIJRI = "ApplicantInfo.birthDateShowHijri";
	public static final String KEY_PASSPORT_NUMBER = "ApplicantInfo.passportNumber";
	public static final String KEY_VISA_TYPE = "ApplicantInfo.visaType";
	
	private static final String PASSPORT_ICON_FILE =
										"sa/gov/nic/bio/bw/client/features/foreignenrollment/images/passport-icon.png";
	
	@FXML private TextField txtFirstName;
	@FXML private TextField txtSecondName;
	@FXML private TextField txtOtherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtMobileNumber;
	@FXML private ComboBox<ItemWithText<GenderType>> cboGender;
	@FXML private ComboBox<HideableItem<NationalityBean>> cboBirthPlace;
	@FXML private ComboBox<HideableItem<NationalityBean>> cboNationality;
	@FXML private ComboBox<HideableItem<NationalityBean>> cboIssuanceCountry;
	@FXML private ComboBox<HideableItem<?>> cboPassportType; // TODO: change generic type
	@FXML private ComboBox<HideableItem<VisaTypeBean>> cboVisaType;
	@FXML private DatePicker dpBirthDate;
	@FXML private DatePicker dpIssueDate;
	@FXML private DatePicker dpExpirationDate;
	@FXML private CheckBox cbBirthDateShowHijri;
	@FXML private CheckBox cbIssueDateShowHijri;
	@FXML private CheckBox cbExpirationDateShowHijri;
	@FXML private TextField txtPassportNumber;
	@FXML private Button btnPassportScanner;
	@FXML private Button btnNext;
	
	private Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/applicantInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnPassportScanner);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnNext.setOnAction(event -> goNext());
		
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<NationalityBean> nationalities = (List<NationalityBean>)
															userSession.getAttribute("lookups.nationalities");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.visaTypes");
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, nationalities);
		GuiUtils.addAutoCompletionSupportToComboBox(cboBirthPlace, nationalities);
		GuiUtils.addAutoCompletionSupportToComboBox(cboIssuanceCountry, nationalities);
		GuiUtils.addAutoCompletionSupportToComboBox(cboVisaType, visaTypes);
		
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboNationality);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboBirthPlace);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboIssuanceCountry);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboPassportType);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboVisaType);
		
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
		
		GuiUtils.initDatePicker(cbBirthDateShowHijri, dpBirthDate, birthDateValidator);
		GuiUtils.initDatePicker(cbIssueDateShowHijri, dpIssueDate, null);
		GuiUtils.initDatePicker(cbExpirationDateShowHijri, dpExpirationDate, null);
		
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
		
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
	}
	
	@Override
	protected void onAttachedToScene()
	{
		setupNationalityComboBox(cboNationality);
		setupNationalityComboBox(cboBirthPlace);
		setupNationalityComboBox(cboIssuanceCountry);
		
		cboVisaType.getItems().forEach(item ->
		{
		    VisaTypeBean visaTypeBean = item.getObject();
		
		    String text;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC) text = visaTypeBean.getDescriptionAR();
		    else text = visaTypeBean.getDescriptionEN();
		
		    String resultText = text.trim();
		    String normalizedText = Normalizer.normalize(resultText, Normalizer.Form.NFKC);
		
		    item.setText(normalizedText);
		});
		
		btnPassportScanner.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			loadOldDateIfExist(uiInputData);
		}
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		uiDataMap.put(KEY_FIRST_NAME, txtFirstName.getText());
		uiDataMap.put(KEY_SECOND_NAME, txtSecondName.getText());
		uiDataMap.put(KEY_OTHER_NAME, txtOtherName.getText());
		uiDataMap.put(KEY_FAMILY_NAME, txtFamilyName.getText());
		uiDataMap.put(KEY_PASSPORT_NUMBER, txtPassportNumber.getText());
		
		ItemWithText<GenderType> genderItem = cboGender.getValue();
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
	
	private void loadOldDateIfExist(Map<String, Object> dataMap)
	{
		String firstName = (String) dataMap.get(KEY_FIRST_NAME);
		String secondName = (String) dataMap.get(KEY_SECOND_NAME);
		String otherName = (String) dataMap.get(KEY_OTHER_NAME);
		String familyName = (String) dataMap.get(KEY_FAMILY_NAME);
		String gender = (String) dataMap.get(KEY_GENDER);
		String nationality = (String) dataMap.get(KEY_NATIONALITY);
		String birthDate = (String) dataMap.get(KEY_BIRTH_DATE);
		String birthDateShowHijri = (String) dataMap.get(KEY_BIRTH_DATE_SHOW_HIJRI);
		String passportNumber = (String) dataMap.get(KEY_PASSPORT_NUMBER);
		String visaType = (String) dataMap.get(KEY_VISA_TYPE);
		
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
	
	@FXML
	private void onOpenPassportScannerButtonClicked(ActionEvent actionEvent)
	{
		Image passportIcon = new Image(
							Thread.currentThread().getContextClassLoader().getResourceAsStream(PASSPORT_ICON_FILE));
		ImageView imageView = new ImageView(passportIcon);
		
		Label lblMessage = new Label(resources.getString("foreignEnrollment.passportScanning.window.message"));
		Button btnStart = new Button(resources.getString("button.start"));
		GuiUtils.makeButtonClickableByPressingEnter(btnStart);
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setMaxHeight(18.0);
		progressIndicator.setMaxWidth(18.0);
		Label lblProgress = new Label(resources.getString("label.passportScanningInProgress"));
		
		HBox hBox = new HBox(10.0);
		hBox.setAlignment(Pos.CENTER);
		hBox.setVisible(false);
		hBox.getChildren().addAll(lblProgress, progressIndicator);
		
		StackPane paneBottom = new StackPane();
		paneBottom.setPadding(new Insets(10.0, 0, 0, 0));
		paneBottom.getChildren().addAll(hBox, btnStart);
		
		VBox vBox = new VBox(10.0);
		vBox.setPadding(new Insets(10.0));
		vBox.setAlignment(Pos.CENTER);
		vBox.getStylesheets().setAll("sa/gov/nic/bio/bw/client/core/css/style.css");
		
		vBox.getChildren().addAll(imageView, lblMessage, paneBottom);
		
		String title = resources.getString("foreignEnrollment.passportScanning.window.title");
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		Stage dialogStage = DialogUtils.buildCustomDialog(Context.getCoreFxController().getStage(),
		                                                  title, vBox, rtl, true);
		
		Task<Map<String, Object>> passportScanningTask = new Task<Map<String, Object>>()
		{
			@Override
			protected Map<String, Object> call() throws Exception
			{
				Thread.sleep(1000);
				return null;
			}
		};
		passportScanningTask.setOnSucceeded(event ->
		{
			if(dialogStage.isShowing())
			{
				dialogStage.close();
				
				Map<String, Object> passportData = passportScanningTask.getValue();
				
				LOGGER.fine("passportData = " + passportData);
				
				// TODO: populate the passport data
			}
		});
		passportScanningTask.setOnFailed(event ->
		{
			if(dialogStage.isShowing())
			{
				dialogStage.close();
				
				// TODO: report the error
			}
		});
		
		btnStart.setOnAction(event ->
		{
			btnStart.setVisible(false);
			hBox.setVisible(true);
			Context.getExecutorService().submit(passportScanningTask);
		});
		
		dialogStage.showAndWait();
	}
	
	private static void setupNationalityComboBox(ComboBox<HideableItem<NationalityBean>> comboBox)
	{
		comboBox.setConverter(new StringConverter<HideableItem<NationalityBean>>()
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
				
				for(HideableItem<NationalityBean> nationalityBean : comboBox.getItems())
				{
					if(string.equals(nationalityBean.getText())) return nationalityBean;
				}
				
				return null;
			}
		});
		
		comboBox.getItems().forEach(item ->
		{
		    NationalityBean nationalityBean = item.getObject();
		
		    String text;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC) text = nationalityBean.getDescriptionAR();
		    else text = nationalityBean.getDescriptionEN();
		
		    String resultText = text.trim() + " (" + nationalityBean.getMofaNationalityCode() + ")";
		    String normalizedText = Normalizer.normalize(resultText, Normalizer.Form.NFKC);
		
		    item.setText(normalizedText);
		});
	}
}