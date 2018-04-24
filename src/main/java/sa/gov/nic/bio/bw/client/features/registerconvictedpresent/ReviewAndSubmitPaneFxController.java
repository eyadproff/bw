package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.registerconvictednotpresent.FetchingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.GenderType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.JudgementInfo;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.Name;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
	@FXML private Label lblJudgmentIssuer;
	@FXML private Label lblPoliceFileNumber;
	@FXML private Label lblJudgmentNumber;
	@FXML private Label lblArrestDate;
	@FXML private Label lblJudgmentDate;
	@FXML private Label lblLashes;
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
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnSubmit);
		
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
				
				int radius = Integer.parseInt(System.getProperty("jnlp.bio.bw.image.blur.radius"));
				if(blur) ivPersonPhoto.setEffect(new GaussianBlur(radius));
			}
			
			lblFirstName.setText(convictedReport.getSubjtName().getFirstName());
			lblFatherName.setText(convictedReport.getSubjtName().getFatherName());
			lblGrandfatherName.setText(convictedReport.getSubjtName().getGrandfatherName());
			lblFamilyName.setText(convictedReport.getSubjtName().getFamilyName());
			lblGeneralFileNumber.setText(convictedReport.getGeneralFileNum());
			lblGender.setText("F".equals(convictedReport.getSubjGender()) ? resources.getString("label.female") :
					                                                        resources.getString("label.male"));
			
			@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
												Context.getUserSession().getAttribute("lookups.nationalities");
			
			NationalityBean nationalityBean = null;
			
			for(NationalityBean nationality : nationalities)
			{
				if(nationality.getCode() == convictedReport.getSubjNationalityCode())
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
			}
			
			lblOccupation.setText(convictedReport.getSubjOccupation());
			lblBirthPlace.setText(convictedReport.getSubjBirthPlace());
			lblBirthDate.setText(AppUtils.formatGregorianDate(convictedReport.getSubjBirthDate()));
			lblIdNumber.setText(convictedReport.getSubjDocId());
			lblIdType.setText(convictedReport.getSubjDocType());
			lblIdIssuanceDate.setText(AppUtils.formatGregorianDate(convictedReport.getSubjDocIssDate()));
			lblIdExpiry.setText(AppUtils.formatGregorianDate(convictedReport.getSubjDocExpDate()));
			
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
				}
			}
			
			lblJudgmentIssuer.setText(judgementInfo.getJudgIssuer());
			lblPoliceFileNumber.setText(judgementInfo.getPoliceFileNum());
			lblJudgmentNumber.setText(judgementInfo.getJudgNum());
			lblArrestDate.setText(AppUtils.formatGregorianDate(judgementInfo.getArrestDate()));
			lblJudgmentDate.setText(AppUtils.formatGregorianDate(judgementInfo.getJudgDate()));
			lblLashes.setText(String.valueOf(judgementInfo.getJudgLashesCount()));
			lblFine.setText(String.valueOf(judgementInfo.getJudgFine()));
			lblJailYears.setText(String.valueOf(judgementInfo.getJailYearCount()));
			lblJailMonths.setText(String.valueOf(judgementInfo.getJailMonthCount()));
			lblJailDays.setText(String.valueOf(judgementInfo.getJailDayCount()));
			lblTravelBanYears.setText(String.valueOf(judgementInfo.getTrvlBanYearCount()));
			lblTravelBanMonths.setText(String.valueOf(judgementInfo.getTrvlBanMonthCount()));
			lblTravelBanDays.setText(String.valueOf(judgementInfo.getTrvlBanDayCount()));
			lblExilingYears.setText(String.valueOf(judgementInfo.getExileYearCount()));
			lblExilingMonths.setText(String.valueOf(judgementInfo.getExileMonthCount()));
			lblExilingDays.setText(String.valueOf(judgementInfo.getExileDayCount()));
			lblDeportationYears.setText(String.valueOf(judgementInfo.getDeportYearCount()));
			lblDeportationMonths.setText(String.valueOf(judgementInfo.getDeportMonthCount()));
			lblDeportationDays.setText(String.valueOf(judgementInfo.getDeportDayCount()));
			cbFinalDeportation.setSelected(judgementInfo.isFinalDeport());
			cbLibel.setSelected(judgementInfo.isLibel());
			cbCovenant.setSelected(judgementInfo.isCovenant());
			lblOther.setText(String.valueOf(judgementInfo.getJudgOthers()));
			if(lblOther.getText().isEmpty()) lblOther.setText("-");
			
			// make the checkboxes look like they are enabled
			cbFinalDeportation.setStyle("-fx-opacity: 1");
			cbLibel.setStyle("-fx-opacity: 1");
			cbCovenant.setStyle("-fx-opacity: 1");
			
			List<Finger> subjFingers = convictedReport.getSubjFingers();
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
			
			subjFingers.forEach(finger ->
			{
				String image = finger.getImage();
				int type = finger.getType();
				ImageView imageView = imageViewMap.get(type);
				String dialogTitle = dialogTitleMap.get(type);
				
				if(imageView == null) return; // to skip 13,14,15
				
				// TODO: convert wsq to png
				
				byte[] bytes = Base64.getDecoder().decode(image);
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
			
			@SuppressWarnings("unchecked") ServiceResponse<Long> serviceResponse =
										(ServiceResponse<Long>) uiInputData.get(Workflow.KEY_WEBSERVICE_RESPONSE);
			if(serviceResponse.isSuccess())
			{
				Long result = serviceResponse.getResult();
				if(result != null)
				{
					uiInputData.put(ShowReportPaneFxController.KEY_CONVICTED_REPORT_NUMBER, result);
					goNext();
				}
				else
				{
					String errorCode = RegisterConvictedPresentErrorCodes.C007_00003.getCode();
					String[] errorDetails = {"result is null!"};
					reportNegativeResponse(errorCode, null, errorDetails);
				}
			}
			else
			{
				reportNegativeResponse(serviceResponse.getErrorCode(), serviceResponse.getException(),
				                       serviceResponse.getErrorDetails());
			}
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
		long reportDate = System.currentTimeMillis() / 1000;
		String generalFileNum = String.valueOf(uiInputData.get(
													PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER));
		
		String firstName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FIRST_NAME);
		String fatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FATHER_NAME);
		String grandfatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GRANDFATHER_NAME);
		String familyName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FAMILY_NAME);
		Name subjtName = new Name(firstName, familyName, fatherName, grandfatherName, null,
		                          null, null, null);
		int subjNationalityCode = ((NationalityBean)
								uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_NATIONALITY)).getCode();
		String subjOccupation = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_OCCUPATION);
		String subjGender = ((GenderType) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENDER)).name()
													 .substring(0, 1); // 'M' or 'F'
		long subjBirthDate = ((LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_DATE))
													 .atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		String subjBirthPlace = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_PLACE);
		String subjDocId = String.valueOf(uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_NUMBER));
		String subjDocType = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_TYPE);
		long subjDocIssDate = ((LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_ISSUANCE_DATE))
													  .atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		long subjDocExpDate = ((LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_ID_EXPIRY_DATE))
													  .atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		String judgIssuer = (String)
								uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER);
		long judgDate =
					((LocalDate) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_JUDGMENT_DATE))
											.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		String judgNum = (String) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER);
		int judgLashesCount = (int) uiInputData.get(PunishmentDetailsPaneFxController.KEY_PUNISHMENT_DETAILS_LASHES);
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
		
		List<CrimeCode> crimeCodes = new ArrayList<>();
		
		int crimeEvent1 = (int) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_EVENT_1);
		int crimeEvent2 = (int) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_EVENT_2);
		int crimeEvent3 = (int) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_EVENT_3);
		int crimeClass1 = (int) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_CLASS_1);
		int crimeClass2 = (int) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_CLASS_2);
		int crimeClass3 = (int) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_CLASS_3);
		boolean crime2Enabled = (boolean)
							uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_2_ENABLED);
		boolean crime3Enabled = (boolean)
							uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_CRIME_3_ENABLED);
		
		crimeCodes.add(new CrimeCode(crimeEvent1, crimeClass1));
		if(crime2Enabled) crimeCodes.add(new CrimeCode(crimeEvent2, crimeClass2));
		if(crime3Enabled) crimeCodes.add(new CrimeCode(crimeEvent3, crimeClass3));
		
		String policeFileNum = (String)
							uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER);
		long arrestDate =
					((LocalDate) uiInputData.get(JudgmentDetailsPaneFxController.KEY_JUDGMENT_DETAILS_ARREST_DATE))
											.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		JudgementInfo subjJudgementInfo = new JudgementInfo(judgIssuer, judgDate, judgNum, judgLashesCount, judgFine,
															judgOthers, jailYearCount, jailMonthCount, jailDayCount,
															trvlBanDayCount, trvlBanMonthCount, trvlBanYearCount,
															deportDayCount, deportMonthCount, deportYearCount,
															exileDayCount, exileMonthCount, exileYearCount,
															finalDeport, covenant, libel, crimeCodes, policeFileNum,
															arrestDate);
		
		@SuppressWarnings("unchecked")
		List<Finger> subjFingers = (List<Finger>)
										uiInputData.get(FetchingFingerprintsPaneFxController.KEY_PERSON_FINGERPRINTS);
		List<Integer> subjMissingFingers = new ArrayList<>();
		for(int i = 1; i <= 10; i++) subjMissingFingers.add(i);
		subjFingers.forEach(finger -> subjMissingFingers.remove((Integer) finger.getType()));
		
		String subjFace = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_PHOTO);
		
		return new ConvictedReport(0L, reportDate, generalFileNum, subjtName, subjNationalityCode,
		                           subjOccupation, subjGender, subjBirthDate, subjBirthPlace, subjDocId, subjDocType,
		                           subjDocIssDate, subjDocExpDate, subjJudgementInfo, subjFingers, subjMissingFingers,
		                           subjFace, null);
	}
}