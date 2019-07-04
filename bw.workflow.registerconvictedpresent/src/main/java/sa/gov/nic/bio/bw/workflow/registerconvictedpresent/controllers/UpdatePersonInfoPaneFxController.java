package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
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
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;

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
		}
		else if(this.facePhotoBase64 != null)
		{
			GuiUtils.attachFacePhotoBase64(ivPersonPhoto, this.facePhotoBase64, true, this.gender);
			photoLoaded.setValue(true);
		}
		
		if(!disable || !photoLoaded.get()) disablePhotoEditing.setValue(false);
		
		if(firstName != null)
		{
			txtFirstName.setText(firstName);
			txtFirstName.setDisable(disable);
		}
		else if(this.firstName != null) txtFirstName.setText(this.firstName);
		else focusedNode = txtFirstName;
		
		
		if(fatherName != null)
		{
			txtFatherName.setText(fatherName);
			txtFatherName.setDisable(disable);
		}
		else if(this.fatherName != null) txtFatherName.setText(this.fatherName);
		
		if(grandfatherName != null)
		{
			txtGrandfatherName.setText(grandfatherName);
			txtGrandfatherName.setDisable(disable);
		}
		else if(this.grandfatherName != null) txtGrandfatherName.setText(this.grandfatherName);
		
		if(familyName != null)
		{
			txtFamilyName.setText(familyName);
			txtFamilyName.setDisable(disable);
		}
		else if(this.familyName != null) txtFamilyName.setText(this.familyName);
		else if(focusedNode == null) focusedNode = txtFamilyName;
		
		if(gender != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboGender, gender);
			if(selected) cboGender.setDisable(disable);
		}
		else if(this.gender != null) GuiUtils.selectComboBoxItem(cboGender, this.gender);
		else if(focusedNode == null) focusedNode = cboGender;
		
		if(nationality != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboNationality, nationality);
			if(selected) cboNationality.setDisable(disable);
		}
		else if(this.nationality != null) GuiUtils.selectComboBoxItem(cboNationality, this.nationality);
		else cboNationality.getSelectionModel().select(0);
		
		if(occupation != null)
		{
			txtOccupation.setText(occupation);
			txtOccupation.setDisable(disable);
		}
		else if(this.occupation != null) txtOccupation.setText(this.occupation);
		
		if(birthPlace != null)
		{
			txtBirthPlace.setText(birthPlace);
			txtBirthPlace.setDisable(disable);
		}
		else if(this.birthPlace != null) txtBirthPlace.setText(this.birthPlace);
		
		if(birthDate != null)
		{
			dpBirthDate.setValue(birthDate);
			dpBirthDate.setDisable(disable);
		}
		else if(this.birthDate != null) dpBirthDate.setValue(this.birthDate);
		
		rdoBirthDateUseHijri.setSelected(true);
		if(this.birthDateUseHijri != null && !this.birthDateUseHijri) rdoBirthDateUseGregorian.setSelected(true);
		
		if(personId != null)
		{
			txtPersonId.setText(String.valueOf(personId));
			txtPersonId.setDisable(disable);
		}
		else if(this.personId != null) txtPersonId.setText(String.valueOf(this.personId));
		
		if(personType != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboPersonType, personType);
			if(selected) cboPersonType.setDisable(disable);
		}
		else if(this.personType != null) GuiUtils.selectComboBoxItem(cboPersonType, this.personType);
		
		if(documentId != null)
		{
			txtDocumentId.setText(documentId);
			txtDocumentId.setDisable(disable);
		}
		else if(this.documentId != null) txtDocumentId.setText(this.documentId);
		
		if(documentType != null)
		{
			boolean selected = GuiUtils.selectComboBoxItem(cboDocumentType, documentType);
			if(selected) cboDocumentType.setDisable(disable);
		}
		else if(this.documentType != null) GuiUtils.selectComboBoxItem(cboDocumentType, this.documentType);
		
		if(documentIssuanceDate != null)
		{
			dpDocumentIssuanceDate.setValue(documentIssuanceDate);
			dpDocumentIssuanceDate.setDisable(disable);
		}
		else if(this.documentIssuanceDate != null) dpDocumentIssuanceDate.setValue(this.documentIssuanceDate);
		
		rdoDocumentIssuanceDateUseHijri.setSelected(true);
		if(this.documentIssuanceDateUseHijri != null && !this.documentIssuanceDateUseHijri)
			rdoDocumentIssuanceDateUseGregorian.setSelected(true);
		
		if(documentExpiryDate != null)
		{
			dpDocumentExpiryDate.setValue(documentExpiryDate);
			dpDocumentExpiryDate.setDisable(disable);
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
				String errorCode = RegisterConvictedPresentErrorCodes.C007_00007.getCode();
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
				String errorCode = RegisterConvictedPresentErrorCodes.C007_00012.getCode();
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
						String errorCode = RegisterConvictedPresentErrorCodes.C007_00013.getCode();
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