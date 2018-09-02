package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.IdType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.JudgementInfo;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_FINAL_CONVICTED_REPORT = "FINAL_CONVICTED_REPORT";
	
	@FXML private VBox paneImage;
	@FXML private ImageViewPane paneImageView;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGeneralFileNumber;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblIdNumber;
	@FXML private Label lblIdType;
	@FXML private Label lblIdIssuanceDate;
	@FXML private Label lblIdExpiry;
	@FXML private Label lblCrimeClassification1;
	@FXML private Label lblCrimeClassification2;
	@FXML private Label lblCrimeClassification3;
	@FXML private Label lblCrimeClassification4;
	@FXML private Label lblCrimeClassification5;
	@FXML private Label lblJudgmentIssuer;
	@FXML private Label lblPoliceFileNumber;
	@FXML private Label lblJudgmentNumber;
	@FXML private Label lblArrestDate;
	@FXML private Label lblJudgmentDate;
	@FXML private Label lblTazeerLashes;
	@FXML private Label lblHadLashes;
	@FXML private Label lblFine;
	@FXML private Label lblJailYears;
	@FXML private Label lblJailMonths;
	@FXML private Label lblJailDays;
	@FXML private Label lblTravelBanYears;
	@FXML private Label lblTravelBanMonths;
	@FXML private Label lblTravelBanDays;
	@FXML private Label lblExilingYears;
	@FXML private Label lblExilingMonths;
	@FXML private Label lblExilingDays;
	@FXML private Label lblDeportationYears;
	@FXML private Label lblDeportationMonths;
	@FXML private Label lblDeportationDays;
	@FXML private Label lblOther;
	@FXML private CheckBox cbFinalDeportation;
	@FXML private CheckBox cbLibel;
	@FXML private CheckBox cbCovenant;
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
	@FXML private Button btnStartOver;
	@FXML private Button btnPrevious;
	@FXML private Button btnSubmit;
	
	private ConvictedReport convictedReport;
	private Map<Integer, String> crimeEventTitles = new HashMap<>();
	private Map<Integer, String> crimeClassTitles = new HashMap<>();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/reviewAndSubmit.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		@SuppressWarnings("unchecked") List<CrimeType> crimeTypes = (List<CrimeType>)
				Context.getUserSession().getAttribute("lookups.crimeTypes");
		
		crimeTypes.forEach(crimeType ->
		{
		    int eventCode = crimeType.getEventCode();
		    int classCode = crimeType.getClassCode();
		    String eventTitle = crimeType.getEventDesc();
		    String classTitle = crimeType.getClassDesc();
		
		    crimeEventTitles.putIfAbsent(eventCode, eventTitle);
		    crimeClassTitles.putIfAbsent(classCode, classTitle);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			convictedReport = buildConvictedReport(uiInputData);
			
			String facePhotoBase64 = convictedReport.getSubjFace();
			if(facePhotoBase64 != null)
			{
				UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
				boolean maleOperator = userInfo != null && userInfo.getGender() > 0 &&
						GenderType.values()[userInfo.getGender() - 1] == GenderType.MALE;
				boolean femaleSubject = "F".equals(convictedReport.getSubjGender());
				boolean blur = maleOperator && femaleSubject;
				
				byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
				ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
				                           resources.getString("label.personPhoto"),
				                           resources.getString("label.contextMenu.showImage"), blur);
				
				int radius = Integer.parseInt(Context.getConfigManager().getProperty("image.blur.radius"));
				@SuppressWarnings("unchecked")
				List<String> userRoles = (List<String>) Context.getUserSession().getAttribute("userRoles");
				String maleSeeFemaleRole =
										Context.getConfigManager().getProperty("face.roles.maleSeeFemale");
				boolean authorized = userRoles.contains(maleSeeFemaleRole);
				if(!authorized && blur) ivPersonPhoto.setEffect(new GaussianBlur(radius));
			}
			
			lblFirstName.setText(convictedReport.getSubjtName().getFirstName());
			lblFatherName.setText(convictedReport.getSubjtName().getFatherName());
			lblGrandfatherName.setText(convictedReport.getSubjtName().getGrandfatherName());
			lblFamilyName.setText(convictedReport.getSubjtName().getFamilyName());
			
			Long generalFileNumber = convictedReport.getGeneralFileNum();
			if(generalFileNumber != null) lblGeneralFileNumber.setText(String.valueOf(generalFileNumber));
			else lblGeneralFileNumber.setTextFill(Color.RED);
			
			lblGender.setText("F".equals(convictedReport.getSubjGender()) ? resources.getString("label.female") :
					                                                        resources.getString("label.male"));
			
			@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
												Context.getUserSession().getAttribute("lookups.countries");
			
			CountryBean countryBean = null;
			
			for(CountryBean country : countries)
			{
				if(country.getCode() == convictedReport.getSubjNationalityCode())
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
			}
			else lblNationality.setText(resources.getString("combobox.unknownNationality"));
			
			String subjOccupation = convictedReport.getSubjOccupation();
			if(subjOccupation != null && !subjOccupation.trim().isEmpty())
				lblOccupation.setText(AppUtils.replaceNumbersOnly(subjOccupation, Locale.getDefault()));
			
			String subjBirthPlace = convictedReport.getSubjBirthPlace();
			if(subjBirthPlace != null && !subjBirthPlace.trim().isEmpty())
				lblBirthPlace.setText(AppUtils.replaceNumbersOnly(subjBirthPlace, Locale.getDefault()));
			
			Long subjBirthDate = convictedReport.getSubjBirthDate();
			if(subjBirthDate != null)
				lblBirthDate.setText(AppUtils.formatHijriGregorianDate(subjBirthDate * 1000));
			
			String subjDocId = convictedReport.getSubjDocId();
			if(subjDocId != null && !subjDocId.trim().isEmpty()) lblIdNumber.setText(subjDocId);
			
			@SuppressWarnings("unchecked") List<IdType> idTypes = (List<IdType>)
														Context.getUserSession().getAttribute("lookups.idTypes");
			
			Integer subjDocType = convictedReport.getSubjDocType();
			if(subjDocType != null)
			{
				IdType idType = null;
				
				for(IdType type : idTypes)
				{
					if(type.getCode() == subjDocType)
					{
						idType = type;
						break;
					}
				}
				
				if(idType != null)
								lblIdType.setText(AppUtils.replaceNumbersOnly(idType.getDesc(), Locale.getDefault()));
			}
			
			Long subjDocIssDate = convictedReport.getSubjDocIssDate();
			if(subjDocIssDate != null) lblIdIssuanceDate.setText(
					AppUtils.formatHijriGregorianDate(subjDocIssDate * 1000));
			
			Long subjDocExpDate = convictedReport.getSubjDocExpDate();
			if(subjDocExpDate != null) lblIdExpiry.setText(
					AppUtils.formatHijriGregorianDate(subjDocExpDate * 1000));
			
			JudgementInfo judgementInfo = convictedReport.getSubjJudgementInfo();
			
			int counter = 0;
			List<CrimeCode> crimeCodes = judgementInfo.getCrimeCodes();
			for(CrimeCode crimeCode : crimeCodes)
			{
				switch(counter++)
				{
					case 0:
					{
						lblCrimeClassification1.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
					case 1:
					{
						GuiUtils.showNode(lblCrimeClassification2, true);
						lblCrimeClassification2.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
					case 2:
					{
						GuiUtils.showNode(lblCrimeClassification3, true);
						lblCrimeClassification3.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
					case 3:
					{
						GuiUtils.showNode(lblCrimeClassification4, true);
						lblCrimeClassification4.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
					case 4:
					{
						GuiUtils.showNode(lblCrimeClassification5, true);
						lblCrimeClassification5.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
				}
			}
			
			lblJudgmentIssuer.setText(AppUtils.replaceNumbersOnly(judgementInfo.getJudgIssuer(), Locale.getDefault()));
			
			String policeFileNum = judgementInfo.getPoliceFileNum();
			if(policeFileNum != null)
						lblPoliceFileNumber.setText(AppUtils.replaceNumbersOnly(policeFileNum, Locale.getDefault()));
			lblJudgmentNumber.setText(AppUtils.replaceNumbersOnly(judgementInfo.getJudgNum(), Locale.getDefault()));
			
			Long arrestDate = judgementInfo.getArrestDate();
			if(arrestDate != null)
							lblArrestDate.setText(AppUtils.formatHijriGregorianDate(arrestDate * 1000));
			
			lblJudgmentDate.setText(AppUtils.formatHijriGregorianDate(judgementInfo.getJudgDate() * 1000));
			lblTazeerLashes.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getJudgTazeerLashesCount()), Locale.getDefault()));
			lblHadLashes.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getJudgHadLashesCount()), Locale.getDefault()));
			lblFine.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getJudgFine()), Locale.getDefault()));
			lblJailYears.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getJailYearCount()), Locale.getDefault()));
			lblJailMonths.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getJailMonthCount()), Locale.getDefault()));
			lblJailDays.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getJailDayCount()), Locale.getDefault()));
			lblTravelBanYears.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getTrvlBanYearCount()), Locale.getDefault()));
			lblTravelBanMonths.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getTrvlBanMonthCount()), Locale.getDefault()));
			lblTravelBanDays.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getTrvlBanDayCount()), Locale.getDefault()));
			lblExilingYears.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getExileYearCount()), Locale.getDefault()));
			lblExilingMonths.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getExileMonthCount()), Locale.getDefault()));
			lblExilingDays.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getExileDayCount()), Locale.getDefault()));
			lblDeportationYears.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getDeportYearCount()), Locale.getDefault()));
			lblDeportationMonths.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getDeportMonthCount()), Locale.getDefault()));
			lblDeportationDays.setText(AppUtils.replaceNumbersOnly(
					String.valueOf(judgementInfo.getDeportDayCount()), Locale.getDefault()));
			cbFinalDeportation.setSelected(judgementInfo.isFinalDeport());
			cbLibel.setSelected(judgementInfo.isLibel());
			cbCovenant.setSelected(judgementInfo.isCovenant());
			
			String judgOthers = judgementInfo.getJudgOthers();
			if(judgOthers != null && !judgOthers.trim().isEmpty())
									lblOther.setText(AppUtils.replaceNumbersOnly(judgOthers, Locale.getDefault()));
			
			// make the checkboxes look like they are enabled
			cbFinalDeportation.setStyle("-fx-opacity: 1");
			cbLibel.setStyle("-fx-opacity: 1");
			cbCovenant.setStyle("-fx-opacity: 1");
			
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
				ImageView imageView = imageViewMap.get(position);
				String dialogTitle = dialogTitleMap.get(position);
				
				byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
				imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
				                           dialogTitle, resources.getString("label.contextMenu.showImage"),
				                          false);
			});
		}
		else // submission result
		{
			GuiUtils.showNode(piProgress, false);
			GuiUtils.showNode(btnPrevious, true);
			GuiUtils.showNode(btnStartOver, true);
			GuiUtils.showNode(btnSubmit, true);
			
			@SuppressWarnings("unchecked") ServiceResponse<ConvictedReportResponse> serviceResponse =
						(ServiceResponse<ConvictedReportResponse>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(serviceResponse.isSuccess())
			{
				ConvictedReportResponse result = serviceResponse.getResult();
				if(result != null)
				{
					uiInputData.put(ShowReportPaneFxController.KEY_CONVICTED_REPORT_NUMBER, result.getReportNumber());
					uiInputData.put(ShowReportPaneFxController.KEY_CONVICTED_REPORT_DATE, result.getReportDate());
					goNext();
				}
				else
				{
					String errorCode = RegisterConvictedPresentErrorCodes.C007_00003.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                        serviceResponse.getErrorDetails());
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
	
	@FXML
	private void onSubmitButtonClicked(ActionEvent actionEvent)
	{
		String headerText =
					resources.getString("printConvictedPresent.submittingConvictedReport.confirmation.header");
		String contentText =
					resources.getString("printConvictedPresent.submittingConvictedReport.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed)
		{
			GuiUtils.showNode(btnPrevious, false);
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnSubmit, false);
			GuiUtils.showNode(piProgress, true);
			
			Map<String, Object> uiDataMap = new HashMap<>();
			uiDataMap.put(KEY_FINAL_CONVICTED_REPORT, convictedReport);
			Context.getWorkflowManager().submitUserTask(uiDataMap);
		}
	}
	
	private ConvictedReport buildConvictedReport(Map<String, Object> uiInputData)
	{
		Long generalFileNum = (Long) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
		
		String firstName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FIRST_NAME);
		String fatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FATHER_NAME);
		String grandfatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GRANDFATHER_NAME);
		String familyName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FAMILY_NAME);
		Name subjtName = new Name(firstName, familyName, fatherName, grandfatherName, null,
		                          null, null, null);
		int subjNationalityCode = ((CountryBean)
								uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_NATIONALITY)).getCode();
		String subjOccupation = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_OCCUPATION);
		String subjGender = ((GenderType) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENDER)).name()
													 .substring(0, 1); // 'M' or 'F'
		LocalDate birthDate = (LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_DATE);
		
		Long subjBirthDate = null;
		if(birthDate != null) subjBirthDate = birthDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		String subjBirthPlace = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_PLACE);
		String subjDocId = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_NUMBER);
		IdType docType = (IdType) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_TYPE);
		
		Integer subjDocType = null;
		if(docType != null) subjDocType = docType.getCode();
		
		LocalDate docIssDate = (LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_ISSUANCE_DATE);
		
		Long subjDocIssDate = null;
		if(docIssDate != null) subjDocIssDate = docIssDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		LocalDate docExpDate = (LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_EXPIRY_DATE);
		Long subjDocExpDate = null;
		if(docExpDate != null) subjDocExpDate = docExpDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		String judgIssuer = (String)
								uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER);
		long judgDate =
					((LocalDate) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_JUDGMENT_DATE))
											.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		String judgNum = (String) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER);
		int judgTazeerLashesCount = (int)
								uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_TAZEER_LASHES);
		int judgHadLashesCount = (int)
								uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_HAD_LASHES);
		int judgFine = (int) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_FINE);
		String judgOthers = (String) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_OTHER);
		int jailYearCount = (int) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_JAIL_YEARS);
		int jailMonthCount = (int)
								uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_JAIL_MONTHS);
		int jailDayCount = (int) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_JAIL_DAYS);
		int trvlBanDayCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_DAYS);
		int trvlBanMonthCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_MONTHS);
		int trvlBanYearCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_YEARS);
		int deportDayCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_DEPORTATION_DAYS);
		int deportMonthCount = (int)
						uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_DEPORTATION_MONTHS);
		int deportYearCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_DEPORTATION_YEARS);
		int exileDayCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_EXILING_DAYS);
		int exileMonthCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_EXILING_MONTHS);
		int exileYearCount = (int)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_EXILING_YEARS);
		boolean finalDeport = (boolean)
							uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_FINAL_DEPORTATION);
		boolean covenant = (boolean) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_COVENANT);
		boolean libel = (boolean) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_LIBEL);
		
		@SuppressWarnings("unchecked")
		List<CrimeCode> crimeCodes = (List<CrimeCode>)
										uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIMES);
		
		String policeFileNum = (String)
							uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER);
		LocalDate arrestDate = (LocalDate)
									uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_ARREST_DATE);
		
		Long arrestDateLong = null;
		if(arrestDate != null) arrestDateLong = arrestDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		JudgementInfo subjJudgementInfo = new JudgementInfo(judgIssuer, judgDate, judgNum, judgTazeerLashesCount,
		                                                    judgHadLashesCount, judgFine, judgOthers, jailYearCount,
		                                                    jailMonthCount, jailDayCount, trvlBanDayCount,
		                                                    trvlBanMonthCount, trvlBanYearCount, deportDayCount,
		                                                    deportMonthCount, deportYearCount, exileDayCount,
		                                                    exileMonthCount, exileYearCount, finalDeport, covenant,
		                                                    libel, crimeCodes, policeFileNum, arrestDateLong);
		
		@SuppressWarnings("unchecked")
		List<Finger> subjFingers = (List<Finger>)
								uiInputData.get(FingerprintCapturingFxController.KEY_SLAP_FINGERPRINTS);
		@SuppressWarnings("unchecked")
		List<Integer> subjMissingFingers = (List<Integer>)
								uiInputData.get(FingerprintCapturingFxController.KEY_MISSING_FINGERPRINTS);
		
		String subjFace = (String) uiInputData.get(FaceCapturingFxController.KEY_FINAL_FACE_IMAGE);
		
		return new ConvictedReport(0L, 0L, generalFileNum, subjtName, subjNationalityCode,
		                           subjOccupation, subjGender, subjBirthDate, subjBirthPlace, subjDocId, subjDocType,
		                           subjDocIssDate, subjDocExpDate, subjJudgementInfo, subjFingers, subjMissingFingers,
		                           subjFace, null);
	}
}