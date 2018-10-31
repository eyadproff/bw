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
import sa.gov.nic.bio.bw.client.core.workflow.Output;
import sa.gov.nic.bio.bw.client.features.commons.beans.Gender;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Country;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.CountryDialingCode;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.PassportTypeBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaApplicantInfo;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.webservice.VisaTypeBean;
import sa.gov.nic.bio.bw.client.features.visaapplicantsenrollment.workflow.VisaApplicantEnrollmentResponse;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private String firstName;
	@Input private String secondName;
	@Input private String otherName;
	@Input(alwaysRequired = true) private String familyName;
	@Input(alwaysRequired = true) private Country nationality;
	@Input(alwaysRequired = true) private Gender gender;
	@Input private Country birthPlace;
	@Input(alwaysRequired = true) private LocalDate birthDate;
	@Input(alwaysRequired = true) private Boolean birthDateUseHijri;
	@Input(alwaysRequired = true) private VisaTypeBean visaType;
	@Input(alwaysRequired = true) private String passportNumber;
	@Input(alwaysRequired = true) private LocalDate issueDate;
	@Input(alwaysRequired = true) private Boolean issueDateUseHijri;
	@Input(alwaysRequired = true) private LocalDate expirationDate;
	@Input(alwaysRequired = true) private Boolean expirationDateUseHijri;
	@Input(alwaysRequired = true) private Country issuanceCountry;
	@Input(alwaysRequired = true) private PassportTypeBean passportType;
	@Input(alwaysRequired = true) private CountryDialingCode dialingCode;
	@Input(alwaysRequired = true) private String mobileNumber;
	@Input(alwaysRequired = true) private Image faceImage;
	@Input(alwaysRequired = true) private String faceImageBase64;
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintImages;
	@Input(alwaysRequired = true) private List<Finger> slapFingerprints;
	@Input(alwaysRequired = true) private List<Integer> missingFingerprints;
	@Input(requiredOnReturn = true) private VisaApplicantEnrollmentResponse visaApplicantEnrollmentResponse;
	@Output private VisaApplicantInfo visaApplicantInfo;
	
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
	
	@Override
	protected void onAttachedToScene()
	{
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		mobileNumber = dialingCode.getDialingCode() + "-" + mobileNumber;
		
		visaApplicantInfo = new VisaApplicantInfo(null, null, firstName, secondName,
		                                          otherName, familyName, nationality, birthDate, passportNumber,
		                                          gender, visaType, issueDate, issuanceCountry, expirationDate,
		                                          birthPlace, passportType, mobileNumber, faceImageBase64,
		                                          slapFingerprints, missingFingerprints);
		
		ivPersonPhoto.setImage(faceImage);
		GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
		                           resources.getString("label.personPhoto"),
		                           resources.getString("label.contextMenu.showImage"), false);
		
		if(firstName != null) lblFirstName.setText(firstName);
		if(secondName != null) lblSecondName.setText(secondName);
		if(otherName != null) lblOtherName.setText(otherName);
		if(familyName != null) lblFamilyName.setText(familyName);
		
		boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
		lblNationality.setText(arabic ? nationality.getDescriptionAR() : nationality.getDescriptionEN());
		lblBirthPlace.setText(arabic ? birthPlace.getDescriptionAR() : birthPlace.getDescriptionEN());
		lblIssuanceCountry.setText(arabic ? issuanceCountry.getDescriptionAR() : issuanceCountry.getDescriptionEN());
		
		lblGender.setText(gender == Gender.FEMALE ? resources.getString("label.female") :
				                  resources.getString("label.male"));
		
		lblBirthDate.setText(AppUtils.formatHijriGregorianDate(AppUtils.gregorianDateToMilliSeconds(birthDate)));
		lblVisaType.setText(arabic ? visaType.getDescriptionAR() : visaType.getDescriptionEN());
		lblPassportNumber.setText(passportNumber);
		
		lblIssueDate.setText(AppUtils.formatHijriGregorianDate(AppUtils.gregorianDateToMilliSeconds(issueDate)));
		lblExpirationDate.setText(AppUtils.formatHijriGregorianDate(
				AppUtils.gregorianDateToMilliSeconds(expirationDate)));
		
		lblPassportType.setText(arabic ? passportType.getDescriptionAR() : passportType.getDescriptionEN());
		
		lblMobileNumber.setText("+" + mobileNumber);
		
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
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse)
		{
			visaApplicantInfo.setApplicantId(visaApplicantEnrollmentResponse.getApplicantId());
			visaApplicantInfo.setEnrollmentDate(AppUtils.milliSecondsToGregorianDateTime(
												visaApplicantEnrollmentResponse.getEnrollmentDate()));
			goNext();
		}
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(btnPrevious, !bShow);
		GuiUtils.showNode(btnSubmit, !bShow);
		GuiUtils.showNode(piProgress, bShow);
	}
	
	@FXML
	private void onSubmitButtonClicked(ActionEvent actionEvent)
	{
		String headerText =
					resources.getString("visaApplicantsEnrollment.registeringVisaApplicant.confirmation.header");
		String contentText =
					resources.getString("visaApplicantsEnrollment.registeringVisaApplicant.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) continueWorkflow();
	}
}