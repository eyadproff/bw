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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sa.gov.nic.bio.biokit.beans.ServiceResponse;
import sa.gov.nic.bio.biokit.exceptions.NotConnectedException;
import sa.gov.nic.bio.biokit.exceptions.TimeoutException;
import sa.gov.nic.bio.biokit.passport.beans.CapturePassportResponse;
import sa.gov.nic.bio.biokit.websocket.beans.MRZData;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.utils.ForeignEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.VisaTypeBean;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ApplicantInfoFxController extends WizardStepFxControllerBase
{
	private static final Logger LOGGER = Logger.getLogger(ApplicantInfoFxController.class.getName());
	
	public static final String KEY_FIRST_NAME = "FIRST_NAME";
	public static final String KEY_SECOND_NAME = "SECOND_NAME";
	public static final String KEY_OTHER_NAME = "OTHER_NAME";
	public static final String KEY_FAMILY_NAME = "FAMILY_NAME";
	public static final String KEY_NATIONALITY = "NATIONALITY";
	public static final String KEY_GENDER = "GENDER";
	public static final String KEY_BIRTH_PLACE = "BIRTH_PLACE";
	public static final String KEY_BIRTH_DATE = "BIRTH_DATE";
	public static final String KEY_BIRTH_DATE_SHOW_HIJRI = "BIRTH_DATE_SHOW_HIJRI";
	public static final String KEY_VISA_TYPE = "VISA_TYPE";
	public static final String KEY_PASSPORT_NUMBER = "PASSPORT_NUMBER";
	public static final String KEY_ISSUE_DATE = "ISSUE_DATE";
	public static final String KEY_ISSUE_DATE_SHOW_HIJRI = "ISSUE_DATE_SHOW_HIJRI";
	public static final String KEY_EXPIRATION_DATE = "EXPIRATION_DATE";
	public static final String KEY_EXPIRATION_DATE_SHOW_HIJRI = "EXPIRATION_DATE_SHOW_HIJRI";
	public static final String KEY_ISSUANCE_COUNTRY = "ISSUANCE_COUNTRY";
	public static final String KEY_PASSPORT_TYPE = "PASSPORT_TYPE";
	public static final String KEY_DIALING_CODE = "DIALING_CODE";
	public static final String KEY_MOBILE_NUMBER = "MOBILE_NUMBER";
	
	private static final String PASSPORT_ICON_FILE =
										"sa/gov/nic/bio/bw/client/features/foreignenrollment/images/passport-icon.png";
	
	@FXML private TextField txtFirstName;
	@FXML private TextField txtSecondName;
	@FXML private TextField txtOtherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtMobileNumber;
	@FXML private ComboBox<ItemWithText<GenderType>> cboGender;
	@FXML private ComboBox<HideableItem<CountryBean>> cboBirthPlace;
	@FXML private ComboBox<HideableItem<CountryBean>> cboNationality;
	@FXML private ComboBox<HideableItem<CountryBean>> cboIssuanceCountry;
	@FXML private ComboBox<HideableItem<PassportTypeBean>> cboPassportType;
	@FXML private ComboBox<HideableItem<VisaTypeBean>> cboVisaType;
	@FXML private ComboBox<HideableItem<CountryDialingCode>> cboDialingCode;
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
		List<CountryBean> countries = (List<CountryBean>) userSession.getAttribute("lookups.countries");
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute("lookups.visaTypes");
		
		@SuppressWarnings("unchecked")
		List<PassportTypeBean> passportTypes = (List<PassportTypeBean>)
															userSession.getAttribute("lookups.passportTypes");
		
		@SuppressWarnings("unchecked")
		List<CountryDialingCode> dialingCodes = (List<CountryDialingCode>)
															userSession.getAttribute("lookups.dialingCodes");
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, countries);
		GuiUtils.addAutoCompletionSupportToComboBox(cboBirthPlace, countries);
		GuiUtils.addAutoCompletionSupportToComboBox(cboIssuanceCountry, countries);
		GuiUtils.addAutoCompletionSupportToComboBox(cboVisaType, visaTypes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboPassportType, passportTypes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboDialingCode, dialingCodes);
		
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboNationality);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboBirthPlace);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboIssuanceCountry);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboPassportType);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboVisaType);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboPassportType);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboDialingCode);
		
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
		
		cboPassportType.setConverter(new StringConverter<HideableItem<PassportTypeBean>>()
		{
			@Override
			public String toString(HideableItem<PassportTypeBean> object)
			{
				if(object == null) return "";
				else return object.getText();
			}
			
			@Override
			public HideableItem<PassportTypeBean> fromString(String string)
			{
				if(string == null || string.trim().isEmpty()) return null;
				
				for(HideableItem<PassportTypeBean> passportTypeHideableItem : cboPassportType.getItems())
				{
					if(string.equals(passportTypeHideableItem.getText())) return passportTypeHideableItem;
				}
				
				return null;
			}
		});
		
		cboDialingCode.setConverter(new StringConverter<HideableItem<CountryDialingCode>>()
		{
			@Override
			public String toString(HideableItem<CountryDialingCode> object)
			{
				if(object == null) return "";
				else return object.getText();
			}
			
			@Override
			public HideableItem<CountryDialingCode> fromString(String string)
			{
				if(string == null || string.trim().isEmpty()) return null;
				
				for(HideableItem<CountryDialingCode> dialingCodeHideableItem : cboDialingCode.getItems())
				{
					if(string.equals(dialingCodeHideableItem.getText())) return dialingCodeHideableItem;
				}
				
				return null;
			}
		});
		
		GuiUtils.initDatePicker(cbBirthDateShowHijri, dpBirthDate, birthDateValidator);
		GuiUtils.initDatePicker(cbIssueDateShowHijri, dpIssueDate, null);
		GuiUtils.initDatePicker(cbExpirationDateShowHijri, dpExpirationDate, null);
		
		GuiUtils.applyValidatorToTextField(txtFirstName, null, null, 50);
		GuiUtils.applyValidatorToTextField(txtSecondName, null, null, 50);
		GuiUtils.applyValidatorToTextField(txtOtherName, null, null, 50);
		GuiUtils.applyValidatorToTextField(txtFamilyName, null, null, 50);
		GuiUtils.applyValidatorToTextField(txtPassportNumber, null, null, 50);
		GuiUtils.applyValidatorToTextField(txtMobileNumber, "\\d*", "[^\\d]",
		                                   40);
		
		BooleanBinding txtFirstNameBinding = txtFirstName.textProperty().isEmpty();
		BooleanBinding txtFamilyNameBinding = txtFamilyName.textProperty().isEmpty();
		BooleanBinding cboNationalityBinding = cboNationality.valueProperty().isNull();
		BooleanBinding cboGenderBinding = cboGender.valueProperty().isNull();
		BooleanBinding dpBirthDateBinding = dpBirthDate.valueProperty().isNull();
		BooleanBinding cboVisaTypeBinding = cboVisaType.valueProperty().isNull();
		BooleanBinding txtPassportNumberBinding = txtPassportNumber.textProperty().isEmpty();
		BooleanBinding dpIssueDateBinding = dpIssueDate.valueProperty().isNull();
		BooleanBinding dpExpirationDateBinding = dpExpirationDate.valueProperty().isNull();
		BooleanBinding cboIssuanceCountryBinding = cboIssuanceCountry.valueProperty().isNull();
		BooleanBinding cboPassportTypeBinding = cboPassportType.valueProperty().isNull();
		BooleanBinding cboDialingCodeBinding = cboDialingCode.valueProperty().isNull();
		BooleanBinding txtMobileNumberBinding = txtMobileNumber.textProperty().isEmpty();
		
		btnNext.disableProperty().bind(txtFirstNameBinding.or(txtFamilyNameBinding).or(cboNationalityBinding)
				                 .or(cboGenderBinding).or(dpBirthDateBinding).or(cboVisaTypeBinding)
				                 .or(txtPassportNumberBinding).or(dpIssueDateBinding).or(dpExpirationDateBinding)
								 .or(cboIssuanceCountryBinding).or(cboPassportTypeBinding)
				                 .or(cboDialingCodeBinding).or(txtMobileNumberBinding));
	}
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.setupNationalityComboBox(cboNationality);
		GuiUtils.setupNationalityComboBox(cboBirthPlace);
		GuiUtils.setupNationalityComboBox(cboIssuanceCountry);
		
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
		
		cboPassportType.getItems().forEach(item ->
		{
		    PassportTypeBean passportTypeBean = item.getObject();
		
		    String text;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC) text = passportTypeBean.getDescriptionAR();
		    else text = passportTypeBean.getDescriptionEN();
		
		    String resultText = text.trim();
		    String normalizedText = Normalizer.normalize(resultText, Normalizer.Form.NFKC);
		
		    item.setText(normalizedText);
		});
		
		cboDialingCode.getItems().forEach(item ->
		{
		    CountryDialingCode dialingCode = item.getObject();
		
		    String text;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC)
		    {
		    	final char LEFT_TO_RIGHT_MARK = '\u200e';
		    	text = dialingCode.getCountryArabicName() + " (" + LEFT_TO_RIGHT_MARK + "[+" +
					   dialingCode.getDialingCode() + "] (" + dialingCode.getIsoAlpha3Code();
		    }
		    else
		    {
		    	text = dialingCode.getCountryEnglishName() + " (" + dialingCode.getIsoAlpha3Code() + ") [+" +
			           dialingCode.getDialingCode() + "]";
		    }
		
		    item.setText(text);
		});
		
		txtFirstName.requestFocus();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			loadOldDateIfExist(uiInputData);
			
			DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
			
			// register a listener to the event of the devices-runner being running or not
			deviceManagerGadgetPaneController.setDevicesRunnerRunningListener(running ->
			{
			    if(running && !deviceManagerGadgetPaneController.isPassportScannerInitialized())
			    {
			        deviceManagerGadgetPaneController.initializePassportScanner();
			    }
			});
			
			if(deviceManagerGadgetPaneController.isDevicesRunnerRunning())
			{
				if(!deviceManagerGadgetPaneController.isPassportScannerInitialized())
				{
					deviceManagerGadgetPaneController.initializePassportScanner();
				}
			}
			else
			{
				boolean devicesRunnerAutoRun = "true".equals(System.getProperty("jnlp.bio.bw.devicesRunner.autoRun"));
				
				if(devicesRunnerAutoRun) deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			}
		}
	}
	
	@Override
	public void onLeaving(Map<String, Object> uiDataMap)
	{
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController()
									 .setPassportScannerInitializationListener(null);
		
		uiDataMap.put(KEY_FIRST_NAME, txtFirstName.getText());
		uiDataMap.put(KEY_SECOND_NAME, txtSecondName.getText());
		uiDataMap.put(KEY_OTHER_NAME, txtOtherName.getText());
		uiDataMap.put(KEY_FAMILY_NAME, txtFamilyName.getText());
		uiDataMap.put(KEY_PASSPORT_NUMBER, txtPassportNumber.getText());
		uiDataMap.put(KEY_MOBILE_NUMBER, txtMobileNumber.getText());
		
		HideableItem<CountryBean> nationalityItem = cboNationality.getValue();
		uiDataMap.put(KEY_NATIONALITY, nationalityItem != null ? nationalityItem.getObject() : null);
		
		ItemWithText<GenderType> genderItem = cboGender.getValue();
		uiDataMap.put(KEY_GENDER, genderItem != null ? genderItem.getItem() : null);
		
		HideableItem<CountryBean> birthPlaceItem = cboBirthPlace.getValue();
		uiDataMap.put(KEY_BIRTH_PLACE, birthPlaceItem != null ? birthPlaceItem.getObject() : null);
		
		HideableItem<VisaTypeBean> visaTypeItem = cboVisaType.getValue();
		uiDataMap.put(KEY_VISA_TYPE, visaTypeItem != null ? visaTypeItem.getObject() : null);
		
		HideableItem<CountryBean> issueCountryItem = cboIssuanceCountry.getValue();
		uiDataMap.put(KEY_ISSUANCE_COUNTRY, issueCountryItem != null ? issueCountryItem.getObject() : null);
		
		HideableItem<PassportTypeBean> passportTypeItem = cboPassportType.getValue();
		uiDataMap.put(KEY_PASSPORT_TYPE, passportTypeItem != null ? passportTypeItem.getObject() : null);
		
		HideableItem<CountryDialingCode> dialingCodeItem = cboDialingCode.getValue();
		uiDataMap.put(KEY_DIALING_CODE, dialingCodeItem != null ? dialingCodeItem.getObject() : null);
		
		uiDataMap.put(KEY_BIRTH_DATE, dpBirthDate.getValue());
		uiDataMap.put(KEY_ISSUE_DATE, dpIssueDate.getValue());
		uiDataMap.put(KEY_EXPIRATION_DATE, dpExpirationDate.getValue());
		
		uiDataMap.put(KEY_BIRTH_DATE_SHOW_HIJRI, cbBirthDateShowHijri.isSelected());
		uiDataMap.put(KEY_ISSUE_DATE_SHOW_HIJRI, cbIssueDateShowHijri.isSelected());
		uiDataMap.put(KEY_EXPIRATION_DATE_SHOW_HIJRI, cbExpirationDateShowHijri.isSelected());
	}
	
	private void loadOldDateIfExist(Map<String, Object> dataMap)
	{
		String firstName = (String) dataMap.get(KEY_FIRST_NAME);
		String secondName = (String) dataMap.get(KEY_SECOND_NAME);
		String otherName = (String) dataMap.get(KEY_OTHER_NAME);
		String familyName = (String) dataMap.get(KEY_FAMILY_NAME);
		CountryBean nationality = (CountryBean) dataMap.get(KEY_NATIONALITY);
		GenderType gender = (GenderType) dataMap.get(KEY_GENDER);
		CountryBean birthPlace = (CountryBean) dataMap.get(KEY_BIRTH_PLACE);
		LocalDate birthDate = (LocalDate) dataMap.get(KEY_BIRTH_DATE);
		Boolean birthDateShowHijri = (Boolean) dataMap.get(KEY_BIRTH_DATE_SHOW_HIJRI);
		VisaTypeBean visaType = (VisaTypeBean) dataMap.get(KEY_VISA_TYPE);
		String passportNumber = (String) dataMap.get(KEY_PASSPORT_NUMBER);
		LocalDate issueDate = (LocalDate) dataMap.get(KEY_ISSUE_DATE);
		Boolean issueDateShowHijri = (Boolean) dataMap.get(KEY_ISSUE_DATE_SHOW_HIJRI);
		LocalDate expirationDate = (LocalDate) dataMap.get(KEY_EXPIRATION_DATE);
		Boolean expirationDateShowHijri = (Boolean) dataMap.get(KEY_EXPIRATION_DATE_SHOW_HIJRI);
		CountryBean issuanceCountry = (CountryBean) dataMap.get(KEY_ISSUANCE_COUNTRY);
		PassportTypeBean passportType = (PassportTypeBean) dataMap.get(KEY_PASSPORT_TYPE);
		CountryDialingCode dialingCode = (CountryDialingCode) dataMap.get(KEY_DIALING_CODE);
		String mobileNumber = (String) dataMap.get(KEY_MOBILE_NUMBER);
		
		if(firstName != null) txtFirstName.setText(firstName);
		if(secondName != null) txtSecondName.setText(secondName);
		if(otherName != null) txtOtherName.setText(otherName);
		if(familyName != null) txtFamilyName.setText(familyName);
		if(passportNumber != null) txtPassportNumber.setText(passportNumber);
		if(mobileNumber != null) txtMobileNumber.setText(mobileNumber);
		
		if(nationality != null) cboNationality.getItems()
											  .stream()
											  .filter(item -> item.getObject().equals(nationality))
											  .findFirst()
											  .ifPresent(cboNationality::setValue);
		
		if(gender != null) cboGender.getItems()
									.stream()
									.filter(item -> item.getItem().equals(gender))
									.findFirst()
									.ifPresent(cboGender::setValue);
		
		if(birthPlace != null) cboBirthPlace.getItems()
											.stream()
											.filter(item -> item.getObject().equals(birthPlace))
											.findFirst()
											.ifPresent(cboBirthPlace::setValue);
		
		if(visaType != null) cboVisaType.getItems()
										.stream()
										.filter(item -> item.getObject().equals(visaType))
										.findFirst()
										.ifPresent(cboVisaType::setValue);
		
		if(issuanceCountry != null) cboIssuanceCountry.getItems()
												      .stream()
												      .filter(item -> item.getObject().equals(issuanceCountry))
												      .findFirst()
												      .ifPresent(cboIssuanceCountry::setValue);
		
		if(passportType != null) cboPassportType.getItems()
												.stream()
												.filter(item -> item.getObject().equals(passportType))
												.findFirst()
												.ifPresent(cboPassportType::setValue);
		
		if(dialingCode != null) cboDialingCode.getItems()
											  .stream()
											  .filter(item -> item.getObject().equals(dialingCode))
											  .findFirst()
											  .ifPresent(cboDialingCode::setValue);
		
		if(birthDate != null) dpBirthDate.setValue(birthDate);
		if(issueDate != null) dpIssueDate.setValue(issueDate);
		if(expirationDate != null) dpExpirationDate.setValue(expirationDate);
		
		if(birthDateShowHijri != null) cbBirthDateShowHijri.setSelected(birthDateShowHijri);
		if(issueDateShowHijri != null) cbIssueDateShowHijri.setSelected(issueDateShowHijri);
		if(expirationDateShowHijri != null) cbExpirationDateShowHijri.setSelected(expirationDateShowHijri);
	}
	
	@FXML
	private void onOpenPassportScannerButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		DevicesRunnerGadgetPaneFxController deviceManagerGadgetPaneController =
												Context.getCoreFxController().getDeviceManagerGadgetPaneController();
		
		// check if the device is initialized
		if(deviceManagerGadgetPaneController.isPassportScannerInitialized())
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
			Label lblStatus = new Label();
			lblStatus.setTextFill(Color.RED);
			
			GuiUtils.showNode(progressIndicator, false);
			GuiUtils.showNode(lblProgress, false);
			GuiUtils.showNode(lblStatus, false);
			
			HBox hBox = new HBox(10.0);
			hBox.setMinHeight(17.0);
			hBox.setAlignment(Pos.CENTER);
			hBox.getChildren().addAll(lblStatus, lblProgress, progressIndicator);
			
			VBox paneBottom = new VBox(5.0);
			paneBottom.setAlignment(Pos.CENTER);
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
			
			btnStart.setOnAction(event ->
			{
				Task<ServiceResponse<CapturePassportResponse>> passportScanningTask =
						new Task<ServiceResponse<CapturePassportResponse>>()
				{
					@Override
					protected ServiceResponse<CapturePassportResponse> call() throws Exception
					{
						String passportScannerDeviceName =
													deviceManagerGadgetPaneController.getPassportScannerDeviceName();
						Future<ServiceResponse<CapturePassportResponse>> future = Context.getBioKitManager()
								.getPassportScannerService().capture(passportScannerDeviceName);
						
						return future.get();
					}
				};
				passportScanningTask.setOnSucceeded(e ->
				{
				    if(dialogStage.isShowing())
				    {
				        ServiceResponse<CapturePassportResponse> serviceResponse = passportScanningTask.getValue();
				
				        if(serviceResponse.isSuccess())
				        {
				            CapturePassportResponse result = serviceResponse.getResult();
				
				            int returnCode = result.getReturnCode();
				
				            if(returnCode == CapturePassportResponse.SuccessCodes.SUCCESS)
				            {
				                dialogStage.close();
				                MRZData mrzData = result.getMrzData();
				                System.out.println("mrzData = " + mrzData);
				                // TODO: populate the passport data
				            }
				            else
				            {
				                btnStart.setDisable(false);
				                GuiUtils.showNode(progressIndicator, false);
				                GuiUtils.showNode(lblProgress, false);
					            GuiUtils.showNode(lblStatus, true);
				
				                if(result.getReturnCode() ==
				                   CapturePassportResponse.FailureCodes.EXCEPTION_WHILE_CAPTURING)
				                {
				                    lblStatus.setText(
				                            resources.getString("label.status.exceptionWhileScanningPassport"));
				                }
				                else if(result.getReturnCode() ==
				                        CapturePassportResponse.FailureCodes.DEVICE_NOT_FOUND_OR_UNPLUGGED)
				                {
				                    lblStatus.setText(
				                            resources.getString("label.status.passportDeviceNotFoundOrUnplugged"));
				                }
				                else if(result.getReturnCode() ==
				                        CapturePassportResponse.FailureCodes.NO_DOCUMENT_FOUND_OR_FAILED_TO_READ)
				                {
				                    lblStatus.setText(
				                            resources.getString("label.status.noPassportInsertedIntoScanner"));
				                }
				                else
				                {
				                    lblStatus.setText(String.format(
				                            resources.getString("label.status.failedToScanPassportWithErrorCode"),
				                            result.getReturnCode()));
				                }
				            }
				        }
				        else
				        {
				            dialogStage.close();
				            String errorCode = ForeignEnrollmentErrorCodes.C010_00003.getCode();
				            String[] errorDetails = {"failed to scan the passport!",
				                    "serviceResponse errorCode = " + serviceResponse.getErrorCode()};
				            Context.getCoreFxController().showErrorDialog(errorCode, serviceResponse.getException(),
				                                                          errorDetails);
				        }
				    }
				});
				passportScanningTask.setOnFailed(e ->
				{
				    if(dialogStage.isShowing())
				    {
				        btnStart.setDisable(false);
				        GuiUtils.showNode(progressIndicator, false);
				        GuiUtils.showNode(lblProgress, false);
					    GuiUtils.showNode(lblStatus, true);
				
				        Throwable exception = passportScanningTask.getException();
				
				        if(exception instanceof ExecutionException) exception = exception.getCause();
				
				        if(exception instanceof TimeoutException)
				        {
				            lblStatus.setText(resources.getString("label.status.devicesRunnerReadTimeout"));
				        }
				        else if(exception instanceof NotConnectedException)
				        {
				            lblStatus.setText(resources.getString("label.status.disconnectedFromDevicesRunner"));
				        }
				        else if(exception instanceof CancellationException)
				        {
				            lblStatus.setText(resources.getString("label.status.scanningPassportCancelled"));
				        }
				        else
				        {
				            dialogStage.close();
				            String errorCode = ForeignEnrollmentErrorCodes.C010_00004.getCode();
				            String[] errorDetails = {"failed to scan the passport!"};
				            Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				        }
				    }
				});
				
				btnStart.setDisable(true);
				GuiUtils.showNode(lblStatus, false);
				GuiUtils.showNode(progressIndicator, true);
				GuiUtils.showNode(lblProgress, true);
			    Context.getExecutorService().submit(passportScanningTask);
			});
			
			dialogStage.showAndWait();
		}
		else
		{
			String errorCode = ForeignEnrollmentErrorCodes.N010_00001.getCode();
			
			String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
			String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
			
			LOGGER.info(logErrorMessage);
			showWarningNotification(guiErrorMessage);
		}
	}
}