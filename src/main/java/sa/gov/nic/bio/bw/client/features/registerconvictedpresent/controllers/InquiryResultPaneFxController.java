package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FxmlFile("inquiryResult.fxml")
public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_INQUIRY_SAMIS_ID = "INQUIRY_SAMIS_ID";
	public static final String KEY_INQUIRY_HIT_RESULT = "INQUIRY_HIT_RESULT";
	
	@Input private Boolean fingerprintHit;
	@Input private Long criminalBioId;
	@Input private Long civilBioId;
	@Input private Long personId;
	@Input private PersonInfo personInfo;
	
	@FXML private ScrollPane infoPane;
	@FXML private GridPane gridPane;
	@FXML private VBox paneNoHitMessage;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblBiometricsIdLabel;
	@FXML private Label lblBiometricsId;
	@FXML private Label lblGeneralFileNumberLabel;
	@FXML private Label lblGeneralFileNumber;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblSamisId;
	@FXML private Label lblSamisIdType;
	@FXML private Label lblDocumentId;
	@FXML private Label lblDocumentType;
	@FXML private Label lblDocumentIssuanceDate;
	@FXML private Label lblDocumentExpiryDate;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	
	private boolean registeringUnknownPerson = false;
	private Map<String, Object> personInfoMap = new HashMap<>();
	
	@Override
	protected void onAttachedToScene()
	{
		btnConfirmPersonInformation.setOnAction(actionEvent -> goNext());
		
		String notAvailable = resources.getString("label.notAvailable");
		
		if(fingerprintHit != null) // workflow: inquiry by fingerprints
		{
			if(fingerprintHit)
			{
				GuiUtils.showNode(btnRegisterUnknownPerson, false);
				GuiUtils.showNode(paneNoHitMessage, false);
				GuiUtils.showNode(ivPersonPhoto, true);
				GuiUtils.showNode(infoPane, true);
				GuiUtils.showNode(btnConfirmPersonInformation, true);
				
				if(criminalBioId != null)
				{
					lblGeneralFileNumber.setText(String.valueOf(criminalBioId));
					personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER,
					                  criminalBioId);
				}
				else
				{
					lblGeneralFileNumber.setText(notAvailable);
					lblGeneralFileNumber.setTextFill(Color.RED);
				}
				
				populatePersonInfo(personId, civilBioId, personInfo, notAvailable);
			}
			else
			{
				GuiUtils.showNode(ivPersonPhoto, false);
				GuiUtils.showNode(infoPane, false);
				GuiUtils.showNode(btnConfirmPersonInformation, false);
				GuiUtils.showNode(paneNoHitMessage, true);
				GuiUtils.showNode(btnRegisterUnknownPerson, true);
			}
		}
		else // workflow: inquiry by samis id
		{
			gridPane.getChildren().remove(lblBiometricsIdLabel);
			gridPane.getChildren().remove(lblBiometricsId);
			gridPane.getChildren().remove(lblGeneralFileNumberLabel);
			gridPane.getChildren().remove(lblGeneralFileNumber);
			gridPane.setPadding(new Insets(0.0, 5.0, 5.0, 5.0));
			
			GuiUtils.showNode(btnRegisterUnknownPerson, false);
			GuiUtils.showNode(paneNoHitMessage, false);
			GuiUtils.showNode(ivPersonPhoto, true);
			GuiUtils.showNode(infoPane, true);
			GuiUtils.showNode(btnConfirmPersonInformation, true);
			
			lblGeneralFileNumber.setText(notAvailable);
			lblGeneralFileNumber.setTextFill(Color.RED);
			
			populatePersonInfo(personId, null, personInfo, notAvailable);
		}
	}
	
	@Override
	protected void onGoingNext(Map<String, Object> uiDataMap)
	{
		if(!registeringUnknownPerson) uiDataMap.putAll(personInfoMap);
	}
	
	@FXML
	private void onRegisterUnknownPersonButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("printConvictedPresent.registerUnknown.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.registerUnknown.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed)
		{
			registeringUnknownPerson = true;
			goNext();
		}
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("printConvictedPresent.startingOver.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
	
	private void populatePersonInfo(Long personId, Long biometricsId, PersonInfo personInfo, String notAvailable)
	{
		if(personId != null)
		{
			lblSamisId.setText(AppUtils.localizeNumbers(String.valueOf(personId)));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_SAMIS_ID, personId);
		}
		else
		{
			lblSamisId.setText(notAvailable);
			lblSamisId.setTextFill(Color.RED);
		}
		
		if(biometricsId != null)
		{
			lblBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(biometricsId)));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_CIVIL_BIO_ID, biometricsId);
		}
		else
		{
			lblBiometricsId.setText(notAvailable);
			lblBiometricsId.setTextFill(Color.RED);
		}
		
		if(personInfo == null)
		{
			lblFirstName.setText(notAvailable);
			lblFirstName.setTextFill(Color.RED);
			lblFatherName.setText(notAvailable);
			lblFatherName.setTextFill(Color.RED);
			lblGrandfatherName.setText(notAvailable);
			lblGrandfatherName.setTextFill(Color.RED);
			lblFamilyName.setText(notAvailable);
			lblFamilyName.setTextFill(Color.RED);
			lblGender.setText(notAvailable);
			lblGender.setTextFill(Color.RED);
			lblNationality.setText(notAvailable);
			lblNationality.setTextFill(Color.RED);
			lblOccupation.setText(notAvailable);
			lblOccupation.setTextFill(Color.RED);
			lblBirthPlace.setText(notAvailable);
			lblBirthPlace.setTextFill(Color.RED);
			lblBirthDate.setText(notAvailable);
			lblBirthDate.setTextFill(Color.RED);
			lblSamisIdType.setText(notAvailable);
			lblSamisIdType.setTextFill(Color.RED);
			lblDocumentId.setText(notAvailable);
			lblDocumentId.setTextFill(Color.RED);
			lblDocumentType.setText(notAvailable);
			lblDocumentType.setTextFill(Color.RED);
			lblDocumentIssuanceDate.setText(notAvailable);
			lblDocumentIssuanceDate.setTextFill(Color.RED);
			lblDocumentExpiryDate.setText(notAvailable);
			lblDocumentExpiryDate.setTextFill(Color.RED);
			
			infoPane.autosize();
			btnConfirmPersonInformation.requestFocus();
			
			return;
		}
		
		String personPhotoBase64 = personInfo.getFace();
		GenderType gender = GenderType.values()[personInfo.getGender() - 1];
		
		if(personPhotoBase64 != null)
		{
			UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
			boolean maleOperator = userInfo != null && userInfo.getGender() > 0 &&
								   GenderType.values()[userInfo.getGender() - 1] == GenderType.MALE;
			boolean femaleSubject = gender == GenderType.FEMALE;
			boolean blur = maleOperator && femaleSubject;
			
			byte[] bytes = Base64.getDecoder().decode(personPhotoBase64);
			ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
			                           resources.getString("label.personPhoto"),
			                           resources.getString("label.contextMenu.showImage"), blur);
			
			int radius = Integer.parseInt(Context.getConfigManager().getProperty("image.blur.radius"));
			@SuppressWarnings("unchecked")
			List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
			String maleSeeFemaleRole = Context.getConfigManager().getProperty("face.roles.maleSeeFemale");
			boolean authorized = userRoles.contains(maleSeeFemaleRole);
			if(!authorized && blur) ivPersonPhoto.setEffect(new GaussianBlur(radius));
		}
		
		Name name = personInfo.getName();
		
		String firstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(),
		                                         false);
		String fatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(),
		                                          false);
		String grandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(),
		                                                name.getTranslatedGrandFatherName(),
		                                               false);
		String familyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(),
		                                          false);
		
		String combinedFirstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(),
		                                                 true);
		String combinedFatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(),
		                                                  true);
		String combinedGrandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(),
		                                                name.getTranslatedGrandFatherName(),
		                                               true);
		String combinedFamilyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(),
		                                                  true);
		
		if(combinedFirstName != null)
		{
			lblFirstName.setText(combinedFirstName);
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_FIRST_NAME, firstName);
		}
		else
		{
			lblFirstName.setText(notAvailable);
			lblFirstName.setTextFill(Color.RED);
		}
		
		if(combinedFatherName != null)
		{
			lblFatherName.setText(combinedFatherName);
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_FATHER_NAME, fatherName);
		}
		else
		{
			lblFatherName.setText(notAvailable);
			lblFatherName.setTextFill(Color.RED);
		}
		
		if(combinedGrandfatherName != null)
		{
			lblGrandfatherName.setText(combinedGrandfatherName);
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GRANDFATHER_NAME, grandfatherName);
		}
		else
		{
			lblGrandfatherName.setText(notAvailable);
			lblGrandfatherName.setTextFill(Color.RED);
		}
		
		if(combinedFamilyName != null)
		{
			lblFamilyName.setText(combinedFamilyName);
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_FAMILY_NAME, familyName);
		}
		else
		{
			lblFamilyName.setText(notAvailable);
			lblFamilyName.setTextFill(Color.RED);
		}
		
		if(gender != null)
		{
			switch(gender)
			{
				case MALE: lblGender.setText(resources.getString("label.male")); break;
				case FEMALE: lblGender.setText(resources.getString("label.female")); break;
			}
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_GENDER, gender);
		}
		else
		{
			lblGender.setText(notAvailable);
			lblGender.setTextFill(Color.RED);
		}
		
		@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
															Context.getUserSession().getAttribute(CountriesLookup.KEY);
		
		CountryBean countryBean = null;
		
		for(CountryBean country : countries)
		{
			if(country.getCode() == personInfo.getNationality())
			{
				countryBean = country;
				break;
			}
		}
		
		if(countryBean != null)
		{
			boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
			lblNationality.setText(arabic ? countryBean.getDescriptionAR() :
					                       countryBean.getDescriptionEN());
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_NATIONALITY, countryBean);
		}
		else
		{
			lblNationality.setText(notAvailable);
			lblNationality.setTextFill(Color.RED);
		}
		
		PersonIdInfo identityInfo = personInfo.getIdentityInfo();
		
		String occupation = identityInfo != null ? identityInfo.getOccupation() : null;
		if(occupation != null && !occupation.trim().isEmpty())
		{
			lblOccupation.setText(AppUtils.localizeNumbers(occupation));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_OCCUPATION, occupation);
		}
		else
		{
			lblOccupation.setText(notAvailable);
			lblOccupation.setTextFill(Color.RED);
		}
		
		String birthPlace = personInfo.getBirthPlace();
		if(birthPlace != null && !birthPlace.trim().isEmpty())
		{
			lblBirthPlace.setText(AppUtils.localizeNumbers(birthPlace));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_PLACE, birthPlace);
		}
		else
		{
			lblBirthPlace.setText(notAvailable);
			lblBirthPlace.setTextFill(Color.RED);
		}
		
		Date birthDate = personInfo.getBirthDate();
		if(birthDate != null && birthDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			LocalDate localDate = birthDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			lblBirthDate.setText(AppUtils.formatHijriGregorianDate(AppUtils.gregorianDateToMilliSeconds(localDate)));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_DATE, localDate);
		}
		else
		{
			lblBirthDate.setText(notAvailable);
			lblBirthDate.setTextFill(Color.RED);
		}
		
		@SuppressWarnings("unchecked")
		List<SamisIdType> samisIdTypes = (List<SamisIdType>)
														Context.getUserSession().getAttribute(SamisIdTypesLookup.KEY);
		
		String samisIdType = personInfo.getPersonType();
		if(samisIdType != null)
		{
			SamisIdType it = null;
			for(SamisIdType type : samisIdTypes)
			{
				if(samisIdType.equals(type.getIfrPersonType()))
				{
					it = type;
					break;
				}
			}
			
			if(it != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblSamisIdType.setText(arabic ? it.getDescriptionAR() : it.getDescriptionEN());
				personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_SAMIS_ID_TYPE, it);
			}
			else
			{
				lblSamisIdType.setText(notAvailable);
				lblSamisIdType.setTextFill(Color.RED);
			}
		}
		else
		{
			lblSamisIdType.setText(notAvailable);
			lblSamisIdType.setTextFill(Color.RED);
		}
		
		String documentId = identityInfo != null ? identityInfo.getIdNumber() : null;
		if(documentId != null && !documentId.trim().isEmpty())
		{
			lblDocumentId.setText(AppUtils.localizeNumbers(documentId));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_ID, documentId);
		}
		else
		{
			lblDocumentId.setText(notAvailable);
			lblDocumentId.setTextFill(Color.RED);
		}
		
		@SuppressWarnings("unchecked")
		List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
		
		Integer documentType = identityInfo != null ? identityInfo.getIdType() : null;
		if(documentType != null)
		{
			DocumentType it = null;
			for(DocumentType type : documentTypes)
			{
				if(type.getCode() == documentType)
				{
					it = type;
					break;
				}
			}
			
			if(it != null)
			{
				lblDocumentType.setText(it.getDesc());
				personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_TYPE, it);
			}
			else
			{
				lblDocumentType.setText(notAvailable);
				lblDocumentType.setTextFill(Color.RED);
			}
		}
		else
		{
			lblDocumentType.setText(notAvailable);
			lblDocumentType.setTextFill(Color.RED);
		}
		
		Date idIssueDate = identityInfo != null ? identityInfo.getIdIssueDate() : null;
		if(idIssueDate != null && idIssueDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			LocalDate localDate = idIssueDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			lblDocumentIssuanceDate.setText(AppUtils.formatHijriGregorianDate(
																	AppUtils.gregorianDateToMilliSeconds(localDate)));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE, localDate);
		}
		else
		{
			lblDocumentIssuanceDate.setText(notAvailable);
			lblDocumentIssuanceDate.setTextFill(Color.RED);
		}
		
		Date idExpiryDate = identityInfo != null ? identityInfo.getIdExpirDate() : null;
		if(idExpiryDate != null && idExpiryDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
		{
			LocalDate localDate = idExpiryDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			lblDocumentExpiryDate.setText(AppUtils.formatHijriGregorianDate(
																	AppUtils.gregorianDateToMilliSeconds(localDate)));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE, localDate);
		}
		else
		{
			lblDocumentExpiryDate.setText(notAvailable);
			lblDocumentExpiryDate.setTextFill(Color.RED);
		}
		
		infoPane.autosize();
		btnConfirmPersonInformation.requestFocus();
	}
}