package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.GenderType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Name;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.PersonIdInfo;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_INQUIRY_HIT_RESULT = "INQUIRY_HIT_RESULT";
	
	@FXML private VBox paneNoHitMessage;
	@FXML private GridPane gridPane;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblGeneralFileNumber;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblIdNumber;
	@FXML private Label lblIdType;
	@FXML private Label lblIdIssuanceDate;
	@FXML private Label lblIdExpiry;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	
	private boolean registeringUnknownPerson = false;
	private Map<String, Object> personInfoMap = new HashMap<>();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiryResult.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnRegisterUnknownPerson);
		GuiUtils.makeButtonClickableByPressingEnter(btnConfirmPersonInformation);
		
		btnConfirmPersonInformation.setOnAction(actionEvent -> goNext());
	}
	
	@Override
	protected void onAttachedToScene()
	{
		ivPersonPhoto.fitWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty()
				                                                                         .divide(2));
		ivPersonPhoto.fitHeightProperty().bind(gridPane.heightProperty().subtract(5.0));
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Boolean fingerprintHit = (Boolean) uiInputData.get(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT);
			String notAvailable = resources.getString("label.notAvailable");
			
			if(fingerprintHit != null)
			{
				if(fingerprintHit)
				{
					GuiUtils.showNode(paneNoHitMessage, false);
					GuiUtils.showNode(ivPersonPhoto, true);
					GuiUtils.showNode(gridPane, true);
					GuiUtils.showNode(btnConfirmPersonInformation, true);
					
					Long criminalBioId = (Long) uiInputData.get(
													PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
					PersonInfo personInfo = (PersonInfo) uiInputData.get(KEY_INQUIRY_HIT_RESULT);
					
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
					
					populatePersonInfo(personInfo, notAvailable);
				}
				else
				{
					GuiUtils.showNode(ivPersonPhoto, false);
					GuiUtils.showNode(gridPane, false);
					GuiUtils.showNode(btnConfirmPersonInformation, false);
					GuiUtils.showNode(paneNoHitMessage, true);
				}
			}
			else
			{
				GuiUtils.showNode(btnRegisterUnknownPerson, false);
				GuiUtils.showNode(paneNoHitMessage, false);
				GuiUtils.showNode(ivPersonPhoto, true);
				GuiUtils.showNode(gridPane, true);
				GuiUtils.showNode(btnConfirmPersonInformation, true);
				
				lblGeneralFileNumber.setText(notAvailable);
				lblGeneralFileNumber.setTextFill(Color.RED);
				
				PersonInfo personInfo = (PersonInfo) uiInputData.get(KEY_INQUIRY_HIT_RESULT);
				populatePersonInfo(personInfo, notAvailable);
			}
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
	
	private void populatePersonInfo(PersonInfo personInfo, String notAvailable)
	{
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
			lblIdNumber.setText(notAvailable);
			lblIdNumber.setTextFill(Color.RED);
			lblIdType.setText(notAvailable);
			lblIdType.setTextFill(Color.RED);
			lblIdIssuanceDate.setText(notAvailable);
			lblIdIssuanceDate.setTextFill(Color.RED);
			lblIdExpiry.setText(notAvailable);
			lblIdExpiry.setTextFill(Color.RED);
			
			gridPane.autosize();
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
			
			int radius = Integer.parseInt(System.getProperty("jnlp.bio.bw.image.blur.radius"));
			if(blur) ivPersonPhoto.setEffect(new GaussianBlur(radius));
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
		
		@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
												Context.getUserSession().getAttribute("lookups.nationalities");
		
		NationalityBean nationalityBean = null;
		
		for(NationalityBean nationality : nationalities)
		{
			if(nationality.getCode() == personInfo.getNationality())
			{
				nationalityBean = nationality;
				break;
			}
		}
		
		if(nationalityBean != null)
		{
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			lblNationality.setText(rtl ? nationalityBean.getDescriptionAR() :
					                       nationalityBean.getDescriptionEN());
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_NATIONALITY, nationalityBean);
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
			lblOccupation.setText(occupation);
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
			lblBirthPlace.setText(birthPlace);
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_PLACE, birthPlace);
		}
		else
		{
			lblBirthPlace.setText(notAvailable);
			lblBirthPlace.setTextFill(Color.RED);
		}
		
		Date birthDate = personInfo.getBirthDate();
		if(birthDate != null)
		{
			LocalDate localDate = birthDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			lblBirthDate.setText(AppUtils.formatDate(localDate));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_DATE, localDate);
		}
		else
		{
			lblBirthDate.setText(notAvailable);
			lblBirthDate.setTextFill(Color.RED);
		}
		
		long samisId = personInfo.getSamisId();
		
		if(samisId > 0)
		{
			lblIdNumber.setText(AppUtils.replaceNumbersOnly(String.valueOf(samisId), Locale.getDefault()));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_NUMBER, samisId);
		}
		else
		{
			lblIdNumber.setText(notAvailable);
			lblIdNumber.setTextFill(Color.RED);
		}
		
		//@SuppressWarnings("unchecked") List<IdType> idTypes = (List<IdType>)
		//											Context.getUserSession().getAttribute("lookups.idTypes");
		//
		//IdType theIdType = null;
		//
		//for(IdType idType : idTypes)
		//{
		//	if(idType.getCode() == personInfo.getNationality())
		//	{
		//		//nationalityBean = nationality;
		//		break;
		//	}
		//}
		
		String personType = personInfo.getPersonType();
		if(personType != null && !personType.trim().isEmpty())
		{
			lblIdType.setText(personType);
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_TYPE, personType);
		}
		else
		{
			lblIdType.setText(notAvailable);
			lblIdType.setTextFill(Color.RED);
		}
		
		Date idIssueDate = identityInfo != null ? identityInfo.getIdIssueDate() : null;
		if(idIssueDate != null)
		{
			LocalDate localDate = idIssueDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			lblIdIssuanceDate.setText(AppUtils.formatDate(localDate));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_ISSUANCE_DATE, localDate);
		}
		else
		{
			lblIdIssuanceDate.setText(notAvailable);
			lblIdIssuanceDate.setTextFill(Color.RED);
		}
		
		Date idExpiryDate = identityInfo != null ? identityInfo.getIdExpirDate() : null;
		if(idExpiryDate != null)
		{
			LocalDate localDate = idExpiryDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
			lblIdExpiry.setText(AppUtils.formatDate(localDate));
			personInfoMap.put(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_EXPIRY_DATE, localDate);
		}
		else
		{
			lblIdExpiry.setText(notAvailable);
			lblIdExpiry.setTextFill(Color.RED);
		}
		
		gridPane.autosize();
		btnConfirmPersonInformation.requestFocus();
	}
}