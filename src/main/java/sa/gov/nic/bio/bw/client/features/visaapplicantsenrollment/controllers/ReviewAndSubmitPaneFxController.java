package sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.PassportTypesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.lookups.VisaTypesLookup;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.utils.VisaApplicantsEnrollmentErrorCodes;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantEnrollmentResponse;
import sa.gov.nic.bio.commons.TaskResponse;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_VISA_APPLICANT_INFO = "VISA_APPLICANT_INFO";
	
	@Input(required = true) private String firstName;
	@Input private String secondName;
	@Input private String otherName;
	@Input(required = true) private String familyName;
	@Input(required = true) private CountryBean nationality;
	@Input(required = true) private GenderType gender;
	@Input private CountryBean birthPlace;
	@Input(required = true) private LocalDate birthDate;
	@Input(required = true) private Boolean birthDateUseHijri;
	@Input(required = true) private VisaTypeBean visaType;
	@Input(required = true) private String passportNumber;
	@Input(required = true) private LocalDate issueDate;
	@Input(required = true) private Boolean issueDateUseHijri;
	@Input(required = true) private LocalDate expirationDate;
	@Input(required = true) private Boolean expirationDateUseHijri;
	@Input(required = true) private CountryBean issuanceCountry;
	@Input(required = true) private PassportTypeBean passportType;
	@Input(required = true) private CountryDialingCode dialingCode;
	@Input(required = true) private String mobileNumber;
	
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
	
	private VisaApplicantInfo visaApplicantInfo;
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			visaApplicantInfo = buildForeignInfo(uiInputData);
			
			String facePhotoBase64 = visaApplicantInfo.getFace();
			byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
			ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
			GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
			                           resources.getString("label.personPhoto"),
			                           resources.getString("label.contextMenu.showImage"), false);
			
			String firstName = visaApplicantInfo.getFirstName();
			String secondName = visaApplicantInfo.getSecondName();
			String otherName = visaApplicantInfo.getOtherName();
			String familyName = visaApplicantInfo.getFamilyName();
			
			if(firstName != null) lblFirstName.setText(firstName);
			if(secondName != null) lblSecondName.setText(secondName);
			if(otherName != null) lblOtherName.setText(otherName);
			if(familyName != null) lblFamilyName.setText(familyName);
			
			@SuppressWarnings("unchecked")
			List<CountryBean> countries = (List<CountryBean>)
														Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Integer nationalityCode = visaApplicantInfo.getNationalityCode();
			Integer birthPlaceCode = visaApplicantInfo.getBirthPlaceCode();
			Integer issuanceCountry = visaApplicantInfo.getIssuanceCountry();
			
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
			
			lblGender.setText(visaApplicantInfo.getGenderCode() == 1 ? resources.getString("label.female") :
					                                             resources.getString("label.male"));
			
			Long birthDate = visaApplicantInfo.getBirthDate();
			if(birthDate != null) lblBirthDate.setText(AppUtils.formatHijriGregorianDate(birthDate * 1000));
			
			@SuppressWarnings("unchecked") List<VisaTypeBean> visaTypes = (List<VisaTypeBean>)
															Context.getUserSession().getAttribute(VisaTypesLookup.KEY);
			
			Integer visaTypeCode = visaApplicantInfo.getVisaTypeCode();
			
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
			
			String passportNumber = visaApplicantInfo.getPassportNumber();
			if(passportNumber != null) lblPassportNumber.setText(passportNumber);
			
			Long issueDate = visaApplicantInfo.getIssueDate();
			Long expirationDate = visaApplicantInfo.getExpirationDate();
			
			if(issueDate != null) lblIssueDate.setText(AppUtils.formatHijriGregorianDate(issueDate * 1000));
			if(expirationDate != null) lblExpirationDate.setText(
												AppUtils.formatHijriGregorianDate(expirationDate * 1000));
			
			@SuppressWarnings("unchecked")
			List<PassportTypeBean> passportTypes = (List<PassportTypeBean>)
														Context.getUserSession().getAttribute(PassportTypesLookup.KEY);
			
			Integer passportType = visaApplicantInfo.getPassportType();
			
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
			
			String mobileNumber = visaApplicantInfo.getMobileNumber();
			if(mobileNumber != null) lblMobileNumber.setText("+" + mobileNumber);
			
			@SuppressWarnings("unchecked")
			Map<Integer, String> fingerprintImages = (Map<Integer, String>)
								uiInputData.get(FingerprintCapturingFxController.KEY_FINGERPRINTS_IMAGES);
			
			Map<Integer, String> dialogTitleMap = GuiUtils.constructFingerprintDialogTitles(resources);
			Map<Integer, ImageView> imageViewMap = GuiUtils.constructFingerprintImageViewMap(ivRightThumb, ivRightIndex,
			                                                                                 ivRightMiddle, ivRightRing,
			                                                                                 ivRightLittle, ivLeftThumb,
			                                                                                 ivLeftIndex, ivLeftMiddle,
			                                                                                 ivLeftRing, ivLeftLittle);
			
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
			
			@SuppressWarnings("unchecked") TaskResponse<VisaApplicantEnrollmentResponse> taskResponse =
				(TaskResponse<VisaApplicantEnrollmentResponse>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			if(taskResponse.isSuccess())
			{
				VisaApplicantEnrollmentResponse result = taskResponse.getResult();
				if(result != null)
				{
					visaApplicantInfo.setApplicantId(result.getApplicantId());
					visaApplicantInfo.setEnrollmentDate(result.getEnrollmentDate());
					goNext();
				}
				else
				{
					String errorCode = VisaApplicantsEnrollmentErrorCodes.C010_00001.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeTaskResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
			                                taskResponse.getErrorDetails());
		}
	}
	
	@FXML
	private void onSubmitButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		String headerText =
					resources.getString("visaApplicantsEnrollment.registeringVisaApplicant.confirmation.header");
		String contentText =
					resources.getString("visaApplicantsEnrollment.registeringVisaApplicant.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed)
		{
			GuiUtils.showNode(btnPrevious, false);
			GuiUtils.showNode(btnSubmit, false);
			GuiUtils.showNode(piProgress, true);
			
			Map<String, Object> uiDataMap = new HashMap<>();
			uiDataMap.put(KEY_VISA_APPLICANT_INFO, visaApplicantInfo);
			if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
		}
	}
	
	private VisaApplicantInfo buildForeignInfo(Map<String, Object> uiInputData)
	{
		//String firstName = (String) uiInputData.get(ApplicantInfoFxController.KEY_FIRST_NAME);
		//String secondName = (String) uiInputData.get(ApplicantInfoFxController.KEY_SECOND_NAME);
		//String otherName = (String) uiInputData.get(ApplicantInfoFxController.KEY_OTHER_NAME);
		//String familyName = (String) uiInputData.get(ApplicantInfoFxController.KEY_FAMILY_NAME);
		//
		//CountryBean countryBean = (CountryBean) uiInputData.get(ApplicantInfoFxController.KEY_NATIONALITY);
		//Integer nationalityCode = null;
		//if(countryBean != null) nationalityCode = countryBean.getCode();
		//
		//LocalDate birthLocalDate = (LocalDate) uiInputData.get(ApplicantInfoFxController.KEY_BIRTH_DATE);
		//Long birthDate = null;
		//if(birthLocalDate != null) birthDate = birthLocalDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		//
		//String passportNumber = (String) uiInputData.get(ApplicantInfoFxController.KEY_PASSPORT_NUMBER);
		//
		//GenderType genderType = (GenderType) uiInputData.get(ApplicantInfoFxController.KEY_GENDER);
		//Integer genderCode = null;
		//if(genderType != null) genderCode = genderType.ordinal();
		//
		//VisaTypeBean visaType = (VisaTypeBean) uiInputData.get(ApplicantInfoFxController.KEY_VISA_TYPE);
		//Integer visaTypeCode = null;
		//if(visaType != null) visaTypeCode = visaType.getCode();
		//
		//LocalDate issueLocalDate = (LocalDate) uiInputData.get(ApplicantInfoFxController.KEY_ISSUE_DATE);
		//Long issueDate = null;
		//if(issueLocalDate != null) issueDate = issueLocalDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		//
		//countryBean = (CountryBean) uiInputData.get(ApplicantInfoFxController.KEY_ISSUANCE_COUNTRY);
		//Integer issuanceCountry = null;
		//if(countryBean != null) issuanceCountry = countryBean.getCode();
		//
		//LocalDate expirationLocalDate = (LocalDate) uiInputData.get(ApplicantInfoFxController.KEY_EXPIRATION_DATE);
		//Long expirationDate = null;
		//if(expirationLocalDate != null) expirationDate = expirationLocalDate.atStartOfDay(AppConstants.SAUDI_ZONE)
		//																	.toEpochSecond();
		//
		//countryBean = (CountryBean) uiInputData.get(ApplicantInfoFxController.KEY_BIRTH_PLACE);
		//Integer birthPlaceCode = null;
		//if(countryBean != null) birthPlaceCode = countryBean.getCode();
		//
		//PassportTypeBean passportTypeBean = (PassportTypeBean)
		//												uiInputData.get(ApplicantInfoFxController.KEY_PASSPORT_TYPE);
		//Integer passportType = null;
		//if(passportTypeBean != null) passportType = passportTypeBean.getCode();
		//
		//CountryDialingCode dialingCode = (CountryDialingCode)
		//												uiInputData.get(ApplicantInfoFxController.KEY_DIALING_CODE);
		//String mobileNumber = (String) uiInputData.get(ApplicantInfoFxController.KEY_MOBILE_NUMBER);
		//
		//if(dialingCode != null && mobileNumber != null && !mobileNumber.trim().isEmpty())
		//{
		//	mobileNumber = dialingCode.getDialingCode() + "-" + mobileNumber;
		//}
		//else mobileNumber = null;
		//
		//String face = (String) uiInputData.get(FaceCapturingFxController.KEY_FINAL_FACE_IMAGE);
		//
		//@SuppressWarnings("unchecked")
		//List<Finger> fingers = (List<Finger>)
		//						uiInputData.get(FingerprintCapturingFxController.KEY_SLAP_FINGERPRINTS);
		//
		//@SuppressWarnings("unchecked")
		//List<Integer> missingFingers = (List<Integer>)
		//						uiInputData.get(FingerprintCapturingFxController.KEY_MISSING_FINGERPRINTS);
		//
		//return new VisaApplicantInfo(null, null, firstName, secondName, otherName, familyName,
		//                             nationalityCode, birthDate, passportNumber, genderCode, visaTypeCode, issueDate,
		//                             issuanceCountry, expirationDate, birthPlaceCode, passportType, mobileNumber, face,
		//                             fingers, missingFingers);
		return null;
	}
}