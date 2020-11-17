package sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.*;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.criminalclearancereportanonymousperson.utils.CriminalClearanceReportAnonymousPersonErrorCodes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@FxmlFile("updatePersonInfo.fxml")
public class UpdatePersonInfoPaneFxController extends WizardStepFxControllerBase
{
	@Input private String cameraFacePhotoBase64;
	@Input private String facePhotoBase64FromAnotherSource;
	@Input private Boolean civilHit;
	@Input private NormalizedPersonInfo normalizedPersonInfo;
	@Output private String facePhotoBase64;
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
	
	@FXML private ImageView ivPersonPhoto;
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
	@FXML private Button btnUploadNewPhoto;
	@FXML private Button btnClearPhoto;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	private static final Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
	private BooleanProperty disablePhotoEditing = new SimpleBooleanProperty(true);
	private BooleanProperty photoLoaded = new SimpleBooleanProperty(false);
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.selectImage.title"));
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.selectImage.types"), "*.jpg");
		fileChooser.getExtensionFilters().addAll(extFilterJPG);
		
		@SuppressWarnings("unchecked")
		List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>) Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
		
		List<Country> countriesPlusUnknown = new ArrayList<>(countries);
		String text = resources.getString("combobox.unknownNationality");
		Country unknownNationality = new Country(0, null, text, text);
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
		
		btnClearPhoto.disableProperty().bind(photoLoaded.not().or(disablePhotoEditing));
		btnUploadNewPhoto.disableProperty().bind(disablePhotoEditing);
		
		Node focusedNode = null;
		
		String facePhotoBase64 = null;
		Gender gender = null;
		String firstName = null;
		String fatherName = null;
		String grandfatherName = null;
		String familyName = null;
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
		
		if(normalizedPersonInfo != null)
		{
			facePhotoBase64 = normalizedPersonInfo.getFacePhotoBase64();
			gender = normalizedPersonInfo.getGender();
			firstName = normalizedPersonInfo.getFirstName();
			fatherName = normalizedPersonInfo.getFatherName();
			grandfatherName = normalizedPersonInfo.getGrandfatherName();
			familyName = normalizedPersonInfo.getFamilyName();
			nationality = normalizedPersonInfo.getNationality();
			occupation = normalizedPersonInfo.getOccupation();
			birthPlace = normalizedPersonInfo.getBirthPlace();
			birthDate = normalizedPersonInfo.getBirthDate();
			personId = normalizedPersonInfo.getPersonId();
			personType = normalizedPersonInfo.getPersonType();
			documentId = normalizedPersonInfo.getDocumentId();
			documentType = normalizedPersonInfo.getDocumentType();
			documentIssuanceDate = normalizedPersonInfo.getDocumentIssuanceDate();
			documentExpiryDate = normalizedPersonInfo.getDocumentExpiryDate();
		}

		// civil hit or present
		boolean disable = (civilHit != null && civilHit) || cameraFacePhotoBase64 != null;
		
		if(cameraFacePhotoBase64 != null)
		{
			this.facePhotoBase64 = cameraFacePhotoBase64;
			GuiUtils.attachFacePhotoBase64(ivPersonPhoto, cameraFacePhotoBase64, true, gender != null ?
																								gender : this.gender);
			photoLoaded.setValue(true);
		}
		else if(isFirstLoad())
		{
			if(facePhotoBase64 != null)
			{
				this.facePhotoBase64 = facePhotoBase64;
				GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);
				photoLoaded.setValue(true);
			}
			else if(facePhotoBase64FromAnotherSource != null)
			{
				this.facePhotoBase64 = facePhotoBase64FromAnotherSource;
				GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64FromAnotherSource, true, gender);
				photoLoaded.setValue(true);
			}
		}
		else if(this.facePhotoBase64 != null)
		{
			GuiUtils.attachFacePhotoBase64(ivPersonPhoto, this.facePhotoBase64, true, this.gender);
			photoLoaded.setValue(true);
		}
		
		if(!disable || !photoLoaded.get()) disablePhotoEditing.setValue(false);

		if(isFirstLoad())
		{
			if(firstName != null) txtFirstName.setText(firstName);
			else focusedNode = txtFirstName;
		}
		else if(this.firstName != null) txtFirstName.setText(this.firstName);
		else focusedNode = txtFirstName;

		if(disable && !txtFirstName.getText().isEmpty()) txtFirstName.setDisable(true);

		if(isFirstLoad())
		{
			if(fatherName != null) txtFatherName.setText(fatherName);
		}
		else if(this.fatherName != null) txtFatherName.setText(this.fatherName);

		if(disable && !txtFatherName.getText().isEmpty()) txtFatherName.setDisable(true);

		if(isFirstLoad())
		{
			if(grandfatherName != null) txtGrandfatherName.setText(grandfatherName);
		}
		else if(this.grandfatherName != null) txtGrandfatherName.setText(this.grandfatherName);

		if(disable && !txtGrandfatherName.getText().isEmpty()) txtGrandfatherName.setDisable(true);

		if(isFirstLoad())
		{
			if(familyName != null) txtFamilyName.setText(familyName);
			else if(focusedNode == null) focusedNode = txtFamilyName;
		}
		else if(this.familyName != null) txtFamilyName.setText(this.familyName);
		else if(focusedNode == null) focusedNode = txtFamilyName;

		if(disable && !txtFamilyName.getText().isEmpty()) txtFamilyName.setDisable(true);

		if(isFirstLoad())
		{
			if(gender != null) GuiUtils.selectComboBoxItem(cboGender, gender);
			else if(focusedNode == null) focusedNode = cboGender;
		}
		else if(this.gender != null) GuiUtils.selectComboBoxItem(cboGender, this.gender);
		else if(focusedNode == null) focusedNode = cboGender;

		if(disable && cboGender.getSelectionModel().getSelectedIndex() >= 0) cboGender.setDisable(true);

		if(isFirstLoad())
		{
			if(nationality != null) GuiUtils.selectComboBoxItem(cboNationality, nationality);
			else cboNationality.getSelectionModel().select(0);
		}
		else if(this.nationality != null) GuiUtils.selectComboBoxItem(cboNationality, this.nationality);
		else cboNationality.getSelectionModel().select(0);

		if(disable && cboNationality.getSelectionModel().getSelectedIndex() > 0) cboNationality.setDisable(true);

		if(isFirstLoad())
		{
			if(occupation != null) txtOccupation.setText(occupation);
		}
		else if(this.occupation != null) txtOccupation.setText(this.occupation);

		if(disable && !txtOccupation.getText().isEmpty()) txtOccupation.setDisable(true);

		if(isFirstLoad())
		{
			if(birthPlace != null) txtBirthPlace.setText(birthPlace);
		}
		else if(this.birthPlace != null) txtBirthPlace.setText(this.birthPlace);

		if(disable && !txtBirthPlace.getText().isEmpty()) txtBirthPlace.setDisable(true);

		if(isFirstLoad())
		{
			if(birthDate != null) dpBirthDate.setValue(birthDate);
		}
		else if(this.birthDate != null) dpBirthDate.setValue(this.birthDate);

		if(disable && dpBirthDate.getValue() != null) dpBirthDate.setDisable(true);

		rdoBirthDateUseHijri.setSelected(true);
		if(this.birthDateUseHijri != null && !this.birthDateUseHijri) rdoBirthDateUseGregorian.setSelected(true);

		if(isFirstLoad())
		{
			if(personId != null) txtPersonId.setText(String.valueOf(personId));
		}
		else if(this.personId != null) txtPersonId.setText(String.valueOf(this.personId));

		if(disable && !txtPersonId.getText().isEmpty()) txtPersonId.setDisable(true);

		if(isFirstLoad())
		{
			if(personType != null) GuiUtils.selectComboBoxItem(cboPersonType, personType);
		}
		else if(this.personType != null) GuiUtils.selectComboBoxItem(cboPersonType, this.personType);

		if(disable && cboPersonType.getSelectionModel().getSelectedIndex() >= 0) cboPersonType.setDisable(true);

		if(isFirstLoad())
		{
			if(documentId != null) txtDocumentId.setText(documentId);
		}
		else if(this.documentId != null) txtDocumentId.setText(this.documentId);

		if(disable && !txtDocumentId.getText().isEmpty()) txtDocumentId.setDisable(true);

		if(isFirstLoad())
		{
			if(documentType != null) GuiUtils.selectComboBoxItem(cboDocumentType, documentType);
		}
		else if(this.documentType != null) GuiUtils.selectComboBoxItem(cboDocumentType, this.documentType);

		if(disable && cboDocumentType.getSelectionModel().getSelectedIndex() >= 0) cboDocumentType.setDisable(true);

		if(isFirstLoad())
		{
			if(documentIssuanceDate != null) dpDocumentIssuanceDate.setValue(documentIssuanceDate);
		}
		else if(this.documentIssuanceDate != null) dpDocumentIssuanceDate.setValue(this.documentIssuanceDate);

		if(disable && dpDocumentIssuanceDate.getValue() != null) dpDocumentIssuanceDate.setDisable(true);
		
		rdoDocumentIssuanceDateUseHijri.setSelected(true);
		if(this.documentIssuanceDateUseHijri != null && !this.documentIssuanceDateUseHijri)
																rdoDocumentIssuanceDateUseGregorian.setSelected(true);

		if(isFirstLoad())
		{
			if(documentExpiryDate != null) dpDocumentExpiryDate.setValue(documentExpiryDate);
		}
		else if(this.documentExpiryDate != null) dpDocumentExpiryDate.setValue(this.documentExpiryDate);

		if(disable && dpDocumentExpiryDate.getValue() != null) dpDocumentExpiryDate.setDisable(true);
		
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
		var firstName = txtFirstName.getText().strip();
		if(!firstName.isBlank()) this.firstName = firstName;
		else this.firstName = null;
		
		var fatherName = txtFatherName.getText().strip();
		if(!fatherName.isBlank()) this.fatherName = fatherName;
		else this.fatherName = null;
		
		var grandfatherName = txtGrandfatherName.getText().strip();
		if(!grandfatherName.isBlank()) this.grandfatherName = grandfatherName;
		else this.grandfatherName = null;
		
		var familyName = txtFamilyName.getText().strip();
		if(!familyName.isBlank()) this.familyName = familyName;
		else this.familyName = null;
		
		var genderItem = cboGender.getValue();
		if(genderItem != null) this.gender = genderItem.getItem();
		else this.gender = null;
		
		var nationalityItem = cboNationality.getValue();
		if(nationalityItem != null) this.nationality = nationalityItem.getItem();
		else this.nationality = null;
		
		var occupation = txtOccupation.getText().strip();
		if(!occupation.isBlank()) this.occupation = occupation;
		else this.occupation = null;
		
		var birthPlace = txtBirthPlace.getText().strip();
		if(!birthPlace.isBlank()) this.birthPlace = birthPlace;
		else this.birthPlace = null;
		
		this.birthDate = dpBirthDate.getValue();
		this.birthDateUseHijri = rdoBirthDateUseHijri.isSelected();
		
		var sPersonId = txtPersonId.getText().strip();
		if(!sPersonId.isBlank()) this.personId = Long.parseLong(sPersonId);
		else this.personId = null;
		
		var personTypeItem = cboPersonType.getValue();
		if(personTypeItem != null) this.personType = personTypeItem.getItem();
		else this.personType = null;
		
		var documentId = txtDocumentId.getText().strip();
		if(!documentId.isBlank()) this.documentId = documentId;
		else this.documentId = null;
		
		var documentTypeItem = cboDocumentType.getValue();
		if(documentTypeItem != null) this.documentType = documentTypeItem.getItem();
		else this.documentType = null;
		
		this.documentIssuanceDate = dpDocumentIssuanceDate.getValue();
		this.documentIssuanceDateUseHijri = rdoDocumentIssuanceDateUseHijri.isSelected();
		
		this.documentExpiryDate = dpDocumentExpiryDate.getValue();
		this.documentExpiryDateUseHijri = rdoDocumentExpiryDateUseHijri.isSelected();
	}
	
	@FXML
	private void onUploadNewPhotoButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showOpenDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			try
			{
				long fileSizeBytes = Files.size(selectedFile.toPath());
				double fileSizeKB = fileSizeBytes / 1024.0;
				String maxFileSizeKbProperty =
									Context.getConfigManager().getProperty("config.uploadFaceImage.fileMaxSizeKB");
				
				double maxFileSizeKb = Double.parseDouble(maxFileSizeKbProperty);
				if(fileSizeKB > maxFileSizeKb)
				{
					DecimalFormat df = new DecimalFormat("#.00"); // 2 decimal places
					showWarningNotification(String.format(resources.getString(
											"selectNewFaceImage.fileChooser.exceedMaxFileSize"),
					                                      df.format(fileSizeKB), df.format(maxFileSizeKb)));
					return;
				}
			}
			catch(Exception e)
			{
				String errorCode = CriminalClearanceReportAnonymousPersonErrorCodes.C021_00002.getCode();
				String[] errorDetails = {"Failed to retrieve the file size (" + selectedFile.getAbsolutePath() + ")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
			}
			
			Image photoImage = new Image("file:///" + selectedFile.getAbsolutePath());
			
			PhotoQualityCheckDialogFxController photoQualityCheckDialogFxController = null;
			try
			{
				photoQualityCheckDialogFxController = DialogUtils.buildCustomDialogByFxml(
																		Context.getCoreFxController().getStage(),
	                                                                    PhotoQualityCheckDialogFxController.class,
																		false);
			}
			catch(Exception e)
			{
				String errorCode = CriminalClearanceReportAnonymousPersonErrorCodes.C021_00003.getCode();
				String[] errorDetails = {"Failed to load (" + PhotoQualityCheckDialogFxController.class.getName() +
										")!"};
				Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
				return;
			}
			
			if(photoQualityCheckDialogFxController != null)
			{
				photoQualityCheckDialogFxController.setHostController(this);
				photoQualityCheckDialogFxController.setInputPhotoImage(photoImage);
				photoQualityCheckDialogFxController.showDialogAndWait();
				Image outputPhotoImage = photoQualityCheckDialogFxController.getOutputPhotoImage();
				
				if(outputPhotoImage != null)
				{
					try
					{
						facePhotoBase64 = AppUtils.imageToBase64(outputPhotoImage);
					}
					catch(IOException e)
					{
						String errorCode = CriminalClearanceReportAnonymousPersonErrorCodes.C021_00004.getCode();
						String[] errorDetails = {"Failed to convert the face photo to base64!"};
						Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails, getTabIndex());
						return;
					}
					
					ivPersonPhoto.setImage(outputPhotoImage);
					photoLoaded.setValue(true);
					showSuccessNotification(resources.getString("label.icao.success"));
				}
			}
		}
	}
	
	@FXML
	private void onClearPhotoButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.detachFacePhoto(ivPersonPhoto);
		photoLoaded.setValue(false);
		facePhotoBase64 = null;
	}
}