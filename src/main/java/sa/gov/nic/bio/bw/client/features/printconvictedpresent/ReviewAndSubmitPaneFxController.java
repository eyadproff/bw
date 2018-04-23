package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.printconvictednotpresent.FetchingFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.GenderType;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.JudgementInfo;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Name;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

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
				byte[] bytes = Base64.getDecoder().decode(facePhotoBase64);
				ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
				                           resources.getString("label.personPhoto"),
				                           resources.getString("label.contextMenu.showImage"));
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
						lblCrimeClassification1.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + " - " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
					case 1:
					{
						GuiUtils.showNode(lblCrimeClassification2, true);
						lblCrimeClassification2.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + " - " +
								                                crimeClassTitles.get(crimeCode.getCrimeClass()));
						break;
					}
					case 2:
					{
						GuiUtils.showNode(lblCrimeClassification3, true);
						lblCrimeClassification3.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + " - " +
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
			
			// TODO: populate data to the GUI
		}
		else // submission result
		{
		
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
			Map<String, Object> uiDataMap = new HashMap<>();
			uiDataMap.put(KEY_FINAL_CONVICTED_REPORT, convictedReport);
			Context.getWorkflowManager().submitUserTask(uiDataMap);
		}
	}
	
	private ConvictedReport buildConvictedReport(Map<String, Object> uiInputData)
	{
		long reportNumber = 0;
		long reportDate = System.currentTimeMillis();
		String generalFileNum = String.valueOf(uiInputData.get(
													PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER));
		
		String firstName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FIRST_NAME);
		String fatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FATHER_NAME);
		String grandfatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GRANDFATHER_NAME);
		String familyName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FAMILY_NAME);
		Name subjtName = new Name(firstName, fatherName, grandfatherName, familyName, null,
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
		String judgOthers = null;
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
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		String operatorId = userInfo.getUserID();
		
		return new ConvictedReport(reportNumber, reportDate, generalFileNum, subjtName, subjNationalityCode,
		                           subjOccupation, subjGender, subjBirthDate, subjBirthPlace, subjDocId, subjDocType,
		                           subjDocIssDate, subjDocExpDate, subjJudgementInfo, subjFingers, subjMissingFingers,
		                           subjFace, operatorId);
	}
}