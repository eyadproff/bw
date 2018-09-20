package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
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
import sa.gov.nic.bio.bw.client.core.controllers.DevicesRunnerGadgetPaneFxController;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.DialingCodesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.PassportTypesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.VisaTypesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.utils.VisaApplicantsEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaTypeBean;

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
	private static final String PASSPORT_ICON_FILE =
								"sa/gov/nic/bio/bw/client/features/visaapplicantsenrollment/images/passport-icon.png";
	
	@Output private String firstName;
	@Output private String secondName;
	@Output private String otherName;
	@Output private String familyName;
	@Output private CountryBean nationality;
	@Output private GenderType gender;
	@Output private CountryBean birthPlace;
	@Output private LocalDate birthDate;
	@Output private Boolean birthDateUseHijri;
	@Output private VisaTypeBean visaType;
	@Output private String passportNumber;
	@Output private LocalDate issueDate;
	@Output private Boolean issueDateUseHijri;
	@Output private LocalDate expirationDate;
	@Output private Boolean expirationDateUseHijri;
	@Output private CountryBean issuanceCountry;
	@Output private PassportTypeBean passportType;
	@Output private CountryDialingCode dialingCode;
	@Output private String mobileNumber;
	
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
	@FXML private RadioButton rdoBirthDateUseHijri;
	@FXML private RadioButton rdoBirthDateUseGregorian;
	@FXML private RadioButton rdoIssueDateUseHijri;
	@FXML private RadioButton rdoIssueDateUseGregorian;
	@FXML private RadioButton rdoExpirationDateUseHijri;
	@FXML private RadioButton rdoExpirationDateUseGregorian;
	@FXML private TextField txtPassportNumber;
	@FXML private Button btnPassportScanner;
	@FXML private Button btnNext;
	
	private Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/applicantInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnNext.setOnAction(event -> goNext());
		
		UserSession userSession = Context.getUserSession();
		
		@SuppressWarnings("unchecked")
		List<CountryBean> countries = (List<CountryBean>) userSession.getAttribute(CountriesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<VisaTypeBean> visaTypes = (List<VisaTypeBean>) userSession.getAttribute(VisaTypesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<PassportTypeBean> passportTypes = (List<PassportTypeBean>)
																	userSession.getAttribute(PassportTypesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<CountryDialingCode> dialingCodes = (List<CountryDialingCode>)
																	userSession.getAttribute(DialingCodesLookup.KEY);
		
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
		
		GuiUtils.initDatePicker(rdoBirthDateUseHijri, dpBirthDate, birthDateValidator);
		GuiUtils.initDatePicker(rdoIssueDateUseHijri, dpIssueDate, null);
		GuiUtils.initDatePicker(rdoExpirationDateUseHijri, dpExpirationDate, null);
		
		GuiUtils.applyValidatorToTextField(txtFirstName, null, null, 30);
		GuiUtils.applyValidatorToTextField(txtSecondName, null, null, 30);
		GuiUtils.applyValidatorToTextField(txtOtherName, null, null, 30);
		GuiUtils.applyValidatorToTextField(txtFamilyName, null, null, 30);
		GuiUtils.applyValidatorToTextField(txtPassportNumber, null, null, 30);
		GuiUtils.applyValidatorToTextField(txtMobileNumber, "\\d*", "[^\\d]",
		                                   30);
		
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
				boolean devicesRunnerAutoRun = "true".equals(
													Context.getConfigManager().getProperty("devicesRunner.autoRun"));
				
				if(devicesRunnerAutoRun) deviceManagerGadgetPaneController.runAndConnectDevicesRunner();
			}
		}
	}
	
	@Override
	protected void onDetachingFromScene()
	{
		Context.getCoreFxController().getDeviceManagerGadgetPaneController().setDevicesRunnerRunningListener(null);
		Context.getCoreFxController().getDeviceManagerGadgetPaneController()
									 .setPassportScannerInitializationListener(null);
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		firstName = txtFirstName.getText();
		secondName = txtSecondName.getText();
		otherName = txtOtherName.getText();
		familyName = txtFamilyName.getText();
		passportNumber = txtPassportNumber.getText();
		mobileNumber = txtMobileNumber.getText();
		
		HideableItem<CountryBean> nationalityItem = cboNationality.getValue();
		nationality = nationalityItem != null ? nationalityItem.getObject() : null;
		
		ItemWithText<GenderType> genderItem = cboGender.getValue();
		gender = genderItem != null ? genderItem.getItem() : null;
		
		HideableItem<CountryBean> birthPlaceItem = cboBirthPlace.getValue();
		birthPlace = birthPlaceItem != null ? birthPlaceItem.getObject() : null;
		
		HideableItem<VisaTypeBean> visaTypeItem = cboVisaType.getValue();
		visaType = visaTypeItem != null ? visaTypeItem.getObject() : null;
		
		HideableItem<CountryBean> issueCountryItem = cboIssuanceCountry.getValue();
		issuanceCountry = issueCountryItem != null ? issueCountryItem.getObject() : null;
		
		HideableItem<PassportTypeBean> passportTypeItem = cboPassportType.getValue();
		passportType = passportTypeItem != null ? passportTypeItem.getObject() : null;
		
		HideableItem<CountryDialingCode> dialingCodeItem = cboDialingCode.getValue();
		dialingCode = dialingCodeItem != null ? dialingCodeItem.getObject() : null;
		
		birthDate = dpBirthDate.getValue();
		issueDate = dpIssueDate.getValue();
		expirationDate = dpExpirationDate.getValue();
		
		birthDateUseHijri = rdoBirthDateUseHijri.isSelected();
		issueDateUseHijri = rdoIssueDateUseHijri.isSelected();
		expirationDateUseHijri = rdoExpirationDateUseHijri.isSelected();
	}
	
	private void loadOldDateIfExist(Map<String, Object> dataMap)
	{
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
		
		if(birthDateUseHijri == null || birthDateUseHijri) rdoBirthDateUseHijri.setSelected(true);
		else rdoBirthDateUseGregorian.setSelected(true);
		
		if(issueDateUseHijri == null || issueDateUseHijri) rdoIssueDateUseHijri.setSelected(true);
		rdoIssueDateUseGregorian.setSelected(true);
		
		if(expirationDateUseHijri == null || expirationDateUseHijri) rdoExpirationDateUseHijri.setSelected(true);
		else rdoExpirationDateUseGregorian.setSelected(true);
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
			
			Label lblMessage = new Label(
								resources.getString("visaApplicantsEnrollment.passportScanning.window.message"));
			Button btnStart = new Button(resources.getString("button.start"));
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
			
			String title = resources.getString("visaApplicantsEnrollment.passportScanning.window.title");
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
					
					            txtFirstName.setText(mrzData.getFirstname());
					            txtFamilyName.setText(mrzData.getLastname());
					            txtPassportNumber.setText(mrzData.getDocNo());
					            
					            String gender = mrzData.getGender();
					            
					            if("M".equalsIgnoreCase(gender)) cboGender.getItems()
					                                                .stream()
					                                                .filter(item -> item.getItem() == GenderType.MALE)
					                                                .findFirst()
					                                                .ifPresent(cboGender::setValue);
					            else if("F".equalsIgnoreCase(gender)) cboGender.getItems()
						                                            .stream()
						                                            .filter(item -> item.getItem() == GenderType.FEMALE)
						                                            .findFirst()
						                                            .ifPresent(cboGender::setValue);
					
					            String nationality = mrzData.getNationality();
					
					            cboNationality.getItems()
							            .stream()
							            .filter(item -> item.getObject().getMofaNationalityCode().equals(nationality))
							            .findFirst()
							            .ifPresent(cboNationality::setValue);
					
					            String issuer = mrzData.getIssuer();
					
					            cboIssuanceCountry.getItems()
							            .stream()
							            .filter(item -> item.getObject().getMofaNationalityCode().equals(issuer))
							            .findFirst()
							            .ifPresent(cboIssuanceCountry::setValue);
					
					            int currentYear = LocalDate.now().getYear();
					            
					            String dob = mrzData.getDOB();
					            dpBirthDate.setValue(calculate6DigitsDate(dob, currentYear));
					
					            String doe = mrzData.getDOE();
					            dpExpirationDate.setValue(calculate6DigitsDate(doe, currentYear));
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
				            String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00003.getCode();
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
				            String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00004.getCode();
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
			String errorCode = VisaApplicantsEnrollmentErrorCodes.N010_00001.getCode();
			
			String guiErrorMessage = Context.getErrorsBundle().getString(errorCode);
			String logErrorMessage = Context.getErrorsBundle().getString(errorCode + ".internal");
			
			LOGGER.info(logErrorMessage);
			showWarningNotification(guiErrorMessage);
		}
	}
	
	private static LocalDate calculate6DigitsDate(String input, int currentYear)
	{
		if(input != null && input.length() == 6)
		{
			String sYear = input.substring(0, 2);
			String sMonth = input.substring(2, 4);
			String sDay = input.substring(4, 6);
			
			int year = Integer.parseInt("20" + sYear);
			int month = Integer.parseInt(sMonth);
			int day = Integer.parseInt(sDay);
			
			if(year <= currentYear) // assuming 2000s
			{
				year = Integer.parseInt("20" + sYear);
			}
			else // assuming 1900s
			{
				year = Integer.parseInt("19" + sYear);
			}
			
			return LocalDate.of(year, month, day);
		}
		
		return null;
	}
}