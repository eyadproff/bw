package sa.gov.nic.bio.bw.client.features.foreignenrollment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.utils.ForeignEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.ForeignInfo;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.foreignenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_FOREIGN_INFO = "FOREIGN_INFO";
	
	@FXML private VBox paneImage;
	@FXML private ImageViewPane paneImageView;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblSecondName;
	@FXML private Label lblOtherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblNationality;
	@FXML private Label lblGender;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblVisaType;
	@FXML private Label lblPassportNumber;
	@FXML private Label lblIssueDate;
	@FXML private Label lblExpirationDate;
	@FXML private Label lblIssuanceCountry;
	@FXML private Label lblPassportType;
	@FXML private Label lblMobileNumber;
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivLeftLittle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftThumb;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnPrevious;
	@FXML private Button btnSubmit;
	
	private ForeignInfo foreignInfo;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/reviewAndSubmit.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnSubmit);
		
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			foreignInfo = buildForeignInfo(uiInputData);
			
			String facePhotoBase64 = foreignInfo.getFace();
			byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
			ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
			                           resources.getString("label.personPhoto"),
			                           resources.getString("label.contextMenu.showImage"), false);
			
			String firstName = foreignInfo.getFirstName();
			String secondName = foreignInfo.getSecondName();
			String otherName = foreignInfo.getOtherName();
			String familyName = foreignInfo.getFamilyName();
			
			if(firstName != null) lblFirstName.setText(firstName);
			if(secondName != null) lblSecondName.setText(secondName);
			if(otherName != null) lblOtherName.setText(otherName);
			if(familyName != null) lblFamilyName.setText(familyName);
			
			@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
												Context.getUserSession().getAttribute("lookups.countries");
			
			Integer nationalityCode = foreignInfo.getNationalityCode();
			Integer birthPlaceCode = foreignInfo.getBirthPlaceCode();
			Integer issuanceCountry = foreignInfo.getIssuanceCountry();
			
			CountryBean nationalityBean = null;
			CountryBean birthPlaceBean = null;
			CountryBean issuanceCountryBean = null;
			
			for(CountryBean country : countries)
			{
				if(nationalityCode != null && country.getCode() == nationalityCode) nationalityBean = country;
				if(birthPlaceCode != null && country.getCode() == birthPlaceCode) birthPlaceBean = country;
				if(issuanceCountry != null && country.getCode() == issuanceCountry) issuanceCountryBean = country;
			}
			
			if(nationalityBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblNationality.setText(arabic ? nationalityBean.getDescriptionAR() :
						                        nationalityBean.getDescriptionEN());
			}
			
			if(birthPlaceBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblBirthPlace.setText(arabic ? birthPlaceBean.getDescriptionAR() :
						                       birthPlaceBean.getDescriptionEN());
			}
			
			if(issuanceCountryBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblIssuanceCountry.setText(arabic ? issuanceCountryBean.getDescriptionAR() :
						                            issuanceCountryBean.getDescriptionEN());
			}
			
			lblGender.setText(foreignInfo.getGenderCode() == 1 ? resources.getString("label.female") :
					                                             resources.getString("label.male"));
			
			Long birthDate = foreignInfo.getBirthDate();
			if(birthDate != null) lblBirthDate.setText(AppUtils.formatGregorianDate(birthDate));
			
			@SuppressWarnings("unchecked") List<VisaTypeBean> visaTypes = (List<VisaTypeBean>)
												Context.getUserSession().getAttribute("lookups.visaTypes");
			
			Integer visaTypeCode = foreignInfo.getVisaTypeCode();
			
			VisaTypeBean visaTypeBean = null;
			
			for(VisaTypeBean visaType : visaTypes)
			{
				if(visaTypeCode != null && visaType.getCode() == visaTypeCode)
				{
					visaTypeBean = visaType;
					break;
				}
			}
			
			if(visaTypeBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblVisaType.setText(arabic ? visaTypeBean.getDescriptionAR() :
						                     visaTypeBean.getDescriptionEN());
			}
			
			String passportNumber = foreignInfo.getPassportNumber();
			if(passportNumber != null) lblPassportNumber.setText(passportNumber);
			
			Long issueDate = foreignInfo.getIssueDate();
			Long expirationDate = foreignInfo.getExpirationDate();
			
			if(issueDate != null) lblIssueDate.setText(AppUtils.formatGregorianDate(issueDate));
			if(expirationDate != null) lblExpirationDate.setText(AppUtils.formatGregorianDate(expirationDate));
			
			@SuppressWarnings("unchecked") List<PassportTypeBean> passportTypes = (List<PassportTypeBean>)
												Context.getUserSession().getAttribute("lookups.passportTypes");
			
			Integer passportType = foreignInfo.getPassportType();
			
			PassportTypeBean passportTypeBean = null;
			
			if(passportTypes != null)
			{
				for(PassportTypeBean type : passportTypes)
				{
					if(passportType != null && type.getCode() == passportType)
					{
						passportTypeBean = type;
						break;
					}
				}
			}
			
			if(passportTypeBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblPassportType.setText(arabic ? passportTypeBean.getDescriptionAR() :
						                         passportTypeBean.getDescriptionEN());
			}
			
			String mobileNumber = foreignInfo.getMobileNumber();
			if(mobileNumber != null) lblMobileNumber.setText(mobileNumber);
			
			@SuppressWarnings("unchecked")
			Map<Integer, String> fingerprintImages = (Map<Integer, String>)
								uiInputData.get(FingerprintCapturingFxController.KEY_FINGERPRINTS_IMAGES);
			Map<Integer, ImageView> imageViewMap = new HashMap<>();
			Map<Integer, String> dialogTitleMap = new HashMap<>();
			
			imageViewMap.put(FingerPosition.RIGHT_THUMB.getPosition(), ivRightThumb);
			imageViewMap.put(FingerPosition.RIGHT_INDEX.getPosition(), ivRightIndex);
			imageViewMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(), ivRightMiddle);
			imageViewMap.put(FingerPosition.RIGHT_RING.getPosition(), ivRightRing);
			imageViewMap.put(FingerPosition.RIGHT_LITTLE.getPosition(), ivRightLittle);
			imageViewMap.put(FingerPosition.LEFT_THUMB.getPosition(), ivLeftThumb);
			imageViewMap.put(FingerPosition.LEFT_INDEX.getPosition(), ivLeftIndex);
			imageViewMap.put(FingerPosition.LEFT_MIDDLE.getPosition(), ivLeftMiddle);
			imageViewMap.put(FingerPosition.LEFT_RING.getPosition(), ivLeftRing);
			imageViewMap.put(FingerPosition.LEFT_LITTLE.getPosition(), ivLeftLittle);
			dialogTitleMap.put(FingerPosition.RIGHT_THUMB.getPosition(),
			                                        resources.getString("label.fingers.thumb") + " (" +
													resources.getString("label.rightHand") + ")");
			dialogTitleMap.put(FingerPosition.RIGHT_INDEX.getPosition(),
			                                        resources.getString("label.fingers.index") + " (" +
					                                resources.getString("label.rightHand") + ")");
			dialogTitleMap.put(FingerPosition.RIGHT_MIDDLE.getPosition(),
			                                        resources.getString("label.fingers.middle") + " (" +
				                                    resources.getString("label.rightHand") + ")");
			dialogTitleMap.put(FingerPosition.RIGHT_RING.getPosition(),
			                                        resources.getString("label.fingers.ring") + " (" +
				                                    resources.getString("label.rightHand") + ")");
			dialogTitleMap.put(FingerPosition.RIGHT_LITTLE.getPosition(),
			                                        resources.getString("label.fingers.little") + " (" +
				                                    resources.getString("label.rightHand") + ")");
			dialogTitleMap.put(FingerPosition.LEFT_THUMB.getPosition(),
			                                        resources.getString("label.fingers.thumb") + " (" +
				                                    resources.getString("label.leftHand") + ")");
			dialogTitleMap.put(FingerPosition.LEFT_INDEX.getPosition(),
			                                        resources.getString("label.fingers.index") + " (" +
				                                    resources.getString("label.leftHand") + ")");
			dialogTitleMap.put(FingerPosition.LEFT_MIDDLE.getPosition(),
			                                        resources.getString("label.fingers.middle") + " (" +
				                                    resources.getString("label.leftHand") + ")");
			dialogTitleMap.put(FingerPosition.LEFT_RING.getPosition(),
			                                        resources.getString("label.fingers.ring") + " (" +
				                                    resources.getString("label.leftHand") + ")");
			dialogTitleMap.put(FingerPosition.LEFT_LITTLE.getPosition(),
			                                        resources.getString("label.fingers.little") + " (" +
				                                    resources.getString("label.leftHand") + ")");
			
			fingerprintImages.forEach((position, fingerprintImage) ->
			{
				String dialogTitle = dialogTitleMap.get(position);
				ImageView imageView = imageViewMap.get(position);
				
				byte[] array = Base64.getDecoder().decode(fingerprintImage);
				imageView.setImage(new Image(new ByteArrayInputStream(array)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
				                           dialogTitle, resources.getString("label.contextMenu.showImage"),
				                          false);
			});
		}
		else // submission result
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnPrevious, true);
			GuiUtils.showNode(btnSubmit, true);
			
			@SuppressWarnings("unchecked") ServiceResponse<Long> serviceResponse =
										(ServiceResponse<Long>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(serviceResponse.isSuccess())
			{
				Long result = serviceResponse.getResult();
				if(result != null)
				{
					foreignInfo.setCandidateId(result);
					goNext();
				}
				else
				{
					String errorCode = ForeignEnrollmentErrorCodes.C010_00001.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                        serviceResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onSubmitButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		String headerText =
					resources.getString("foreignEnrollment.registeringForeign.confirmation.header");
		String contentText =
					resources.getString("foreignEnrollment.registeringForeign.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed)
		{
			GuiUtils.showNode(btnPrevious, false);
			GuiUtils.showNode(btnSubmit, false);
			GuiUtils.showNode(piProgress, true);
			
			Map<String, Object> uiDataMap = new HashMap<>();
			uiDataMap.put(KEY_FOREIGN_INFO, foreignInfo);
			Context.getWorkflowManager().submitUserTask(uiDataMap);
		}
	}
	
	private ForeignInfo buildForeignInfo(Map<String, Object> uiInputData)
	{
		String firstName = (String) uiInputData.get(ApplicantInfoFxController.KEY_FIRST_NAME);
		String secondName = (String) uiInputData.get(ApplicantInfoFxController.KEY_SECOND_NAME);
		String otherName = (String) uiInputData.get(ApplicantInfoFxController.KEY_OTHER_NAME);
		String familyName = (String) uiInputData.get(ApplicantInfoFxController.KEY_FAMILY_NAME);
		
		CountryBean countryBean = (CountryBean) uiInputData.get(ApplicantInfoFxController.KEY_NATIONALITY);
		Integer nationalityCode = null;
		if(countryBean != null) nationalityCode = countryBean.getCode();
		
		LocalDate birthLocalDate = (LocalDate) uiInputData.get(ApplicantInfoFxController.KEY_BIRTH_DATE);
		Long birthDate = null;
		if(birthLocalDate != null) birthDate = birthLocalDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		String passportNumber = (String) uiInputData.get(ApplicantInfoFxController.KEY_PASSPORT_NUMBER);
		
		GenderType genderType = (GenderType) uiInputData.get(ApplicantInfoFxController.KEY_GENDER);
		Integer genderCode = null;
		if(genderType != null) genderCode = genderType.ordinal();
		
		VisaTypeBean visaType = (VisaTypeBean) uiInputData.get(ApplicantInfoFxController.KEY_VISA_TYPE);
		Integer visaTypeCode = null;
		if(visaType != null) visaTypeCode = visaType.getCode();
		
		LocalDate issueLocalDate = (LocalDate) uiInputData.get(ApplicantInfoFxController.KEY_ISSUE_DATE);
		Long issueDate = null;
		if(issueLocalDate != null) issueDate = issueLocalDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		countryBean = (CountryBean) uiInputData.get(ApplicantInfoFxController.KEY_ISSUANCE_COUNTRY);
		Integer issuanceCountry = null;
		if(countryBean != null) issuanceCountry = countryBean.getCode();
		
		LocalDate expirationLocalDate = (LocalDate) uiInputData.get(ApplicantInfoFxController.KEY_EXPIRATION_DATE);
		Long expirationDate = null;
		if(expirationLocalDate != null) expirationDate = expirationLocalDate.atStartOfDay(AppConstants.SAUDI_ZONE)
																			.toEpochSecond();
		
		countryBean = (CountryBean) uiInputData.get(ApplicantInfoFxController.KEY_BIRTH_PLACE);
		Integer birthPlaceCode = null;
		if(countryBean != null) birthPlaceCode = countryBean.getCode();
		
		PassportTypeBean passportTypeBean = (PassportTypeBean)
														uiInputData.get(ApplicantInfoFxController.KEY_PASSPORT_TYPE);
		Integer passportType = null;
		if(passportTypeBean != null) passportType = passportTypeBean.getCode();
		
		CountryDialingCode dialingCode = (CountryDialingCode)
														uiInputData.get(ApplicantInfoFxController.KEY_DIALING_CODE);
		String mobileNumber = (String) uiInputData.get(ApplicantInfoFxController.KEY_MOBILE_NUMBER);
		
		if(dialingCode != null && mobileNumber != null && !mobileNumber.trim().isEmpty())
		{
			mobileNumber = dialingCode.getDialingCode() + mobileNumber;
		}
		else mobileNumber = null;
		
		String face = (String) uiInputData.get(FaceCapturingFxController.KEY_FINAL_FACE_IMAGE);
		
		@SuppressWarnings("unchecked")
		List<Finger> fingers = (List<Finger>)
								uiInputData.get(FingerprintCapturingFxController.KEY_COLLECTED_SLAP_FINGERPRINTS);
		
		@SuppressWarnings("unchecked")
		List<Integer> missingFingers = (List<Integer>)
								uiInputData.get(FingerprintCapturingFxController.KEY_MISSING_FINGERPRINTS);
		
		return new ForeignInfo(null, firstName, secondName, otherName, familyName, nationalityCode,
		                       birthDate, passportNumber, genderCode, visaTypeCode, issueDate, issuanceCountry,
		                       expirationDate, birthPlaceCode, passportType, mobileNumber, face, fingers,
		                       missingFingers);
	}
}