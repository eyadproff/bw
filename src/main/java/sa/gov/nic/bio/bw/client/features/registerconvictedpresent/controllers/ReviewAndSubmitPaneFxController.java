package sa.gov.nic.bio.bw.client.features.registerconvictedpresent.controllers;

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
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.workflow.Workflow;
import sa.gov.nic.bio.bw.client.features.commons.beans.Gender;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FaceCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.controllers.FingerprintCapturingFxController;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Country;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.utils.RegisterConvictedPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.JudgementInfo;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportResponse;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;
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
	public static final String KEY_FINAL_CONVICTED_REPORT = "FINAL_CONVICTED_REPORT";
	
	@FXML private VBox paneImage;
	@FXML private ImageViewPane paneImageView;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblBiometricsId;
	@FXML private Label lblGeneralFileNumber;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblPersonId;
	@FXML private Label lblPersonType;
	@FXML private Label lblDocumentId;
	@FXML private Label lblDocumentType;
	@FXML private Label lblDocumentIssuanceDate;
	@FXML private Label lblDocumentExpiryDate;
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
	protected void initialize()
	{
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
		
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
						Gender.values()[userInfo.getGender() - 1] == Gender.MALE;
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
			
			Long subjBioId = convictedReport.getSubjBioId();
			if(subjBioId != null) lblBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(subjBioId)));
			
			Long generalFileNumber = convictedReport.getGeneralFileNum();
			if(generalFileNumber != null)
				            lblGeneralFileNumber.setText(AppUtils.localizeNumbers(String.valueOf(generalFileNumber)));
			else lblGeneralFileNumber.setTextFill(Color.RED);
			
			lblGender.setText("F".equals(convictedReport.getSubjGender()) ? resources.getString("label.female") :
					                                                        resources.getString("label.male"));
			
			@SuppressWarnings("unchecked") List<Country> countries = (List<Country>)
															Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Country countryBean = null;
			
			for(Country country : countries)
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
													lblOccupation.setText(AppUtils.localizeNumbers(subjOccupation));
			
			String subjBirthPlace = convictedReport.getSubjBirthPlace();
			if(subjBirthPlace != null && !subjBirthPlace.trim().isEmpty())
													lblBirthPlace.setText(AppUtils.localizeNumbers(subjBirthPlace));
			
			Long subjBirthDate = convictedReport.getSubjBirthDate();
			if(subjBirthDate != null)
				lblBirthDate.setText(AppUtils.formatHijriGregorianDate(subjBirthDate * 1000));
			
			Long subjSamisId = convictedReport.getSubjSamisId();
			if(subjSamisId != null) lblPersonId.setText(AppUtils.localizeNumbers(String.valueOf(subjSamisId)));
			
			@SuppressWarnings("unchecked")
			List<PersonType> personTypes = (List<PersonType>)
														Context.getUserSession().getAttribute(SamisIdTypesLookup.KEY);
			
			Integer subjSamisType = convictedReport.getSubjSamisType();
			if(subjSamisType != null)
			{
				PersonType personType = null;
				
				for(PersonType type : personTypes)
				{
					if(type.getCode() == subjSamisType)
					{
						personType = type;
						break;
					}
				}
				
				if(personType != null)
				{
					boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
					lblPersonType.setText(AppUtils.localizeNumbers(arabic ? personType.getDescriptionAR() :
							                                                 personType.getDescriptionEN()));
				}
			}
			
			String subjDocId = convictedReport.getSubjDocId();
			if(subjDocId != null && !subjDocId.trim().isEmpty())
															lblDocumentId.setText(AppUtils.localizeNumbers(subjDocId));
			
			@SuppressWarnings("unchecked")
			List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
			
			Integer subjDocType = convictedReport.getSubjDocType();
			if(subjDocType != null)
			{
				DocumentType documentType = null;
				
				for(DocumentType type : documentTypes)
				{
					if(type.getCode() == subjDocType)
					{
						documentType = type;
						break;
					}
				}
				
				if(documentType != null) lblDocumentType.setText(AppUtils.localizeNumbers(documentType.getDesc()));
			}
			
			Long subjDocIssDate = convictedReport.getSubjDocIssDate();
			if(subjDocIssDate != null) lblDocumentIssuanceDate.setText(
					AppUtils.formatHijriGregorianDate(subjDocIssDate * 1000));
			
			Long subjDocExpDate = convictedReport.getSubjDocExpDate();
			if(subjDocExpDate != null) lblDocumentExpiryDate.setText(
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
			
			lblJudgmentIssuer.setText(AppUtils.localizeNumbers(judgementInfo.getJudgIssuer()));
			
			String policeFileNum = judgementInfo.getPoliceFileNum();
			if(policeFileNum != null) lblPoliceFileNumber.setText(AppUtils.localizeNumbers(policeFileNum));
			lblJudgmentNumber.setText(AppUtils.localizeNumbers(judgementInfo.getJudgNum()));
			
			Long arrestDate = judgementInfo.getArrestDate();
			if(arrestDate != null)
							lblArrestDate.setText(AppUtils.formatHijriGregorianDate(arrestDate * 1000));
			
			lblJudgmentDate.setText(AppUtils.formatHijriGregorianDate(judgementInfo.getJudgDate() * 1000));
			lblTazeerLashes.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJudgTazeerLashesCount())));
			lblHadLashes.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJudgHadLashesCount())));
			lblFine.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJudgFine())));
			lblJailYears.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJailYearCount())));
			lblJailMonths.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJailMonthCount())));
			lblJailDays.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getJailDayCount())));
			lblTravelBanYears.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getTrvlBanYearCount())));
			lblTravelBanMonths.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getTrvlBanMonthCount())));
			lblTravelBanDays.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getTrvlBanDayCount())));
			lblExilingYears.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getExileYearCount())));
			lblExilingMonths.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getExileMonthCount())));
			lblExilingDays.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getExileDayCount())));
			lblDeportationYears.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getDeportYearCount())));
			lblDeportationMonths.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getDeportMonthCount())));
			lblDeportationDays.setText(AppUtils.localizeNumbers(String.valueOf(judgementInfo.getDeportDayCount())));
			cbFinalDeportation.setSelected(judgementInfo.isFinalDeport());
			cbLibel.setSelected(judgementInfo.isLibel());
			cbCovenant.setSelected(judgementInfo.isCovenant());
			
			String judgOthers = judgementInfo.getJudgOthers();
			if(judgOthers != null && !judgOthers.trim().isEmpty())
															lblOther.setText(AppUtils.localizeNumbers(judgOthers));
			
			// make the checkboxes look like they are enabled
			cbFinalDeportation.setStyle("-fx-opacity: 1");
			cbLibel.setStyle("-fx-opacity: 1");
			cbCovenant.setStyle("-fx-opacity: 1");
			
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
			
			@SuppressWarnings("unchecked") TaskResponse<ConvictedReportResponse> taskResponse =
						(TaskResponse<ConvictedReportResponse>) uiInputData.get(Workflow.KEY_WORKFLOW_TASK_NEGATIVE_RESPONSE);
			if(taskResponse.isSuccess())
			{
				ConvictedReportResponse result = taskResponse.getResult();
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
					reportNegativeTaskResponse(errorCode, null, errorDetails);
				}
			}
			else reportNegativeTaskResponse(taskResponse.getErrorCode(), taskResponse.getException(),
			                                taskResponse.getErrorDetails());
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
			if(!isDetached()) Context.getWorkflowManager().submitUserTask(uiDataMap);
		}
	}
	
	private ConvictedReport buildConvictedReport(Map<String, Object> uiInputData)
	{
		Long biometricsId = (Long) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_CIVIL_BIO_ID);
		Long generalFileNum = (Long) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENERAL_FILE_NUMBER);
		
		String firstName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FIRST_NAME);
		String fatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FATHER_NAME);
		String grandfatherName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GRANDFATHER_NAME);
		String familyName = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_FAMILY_NAME);
		Name subjtName = new Name(firstName, familyName, fatherName, grandfatherName, null,
		                          null, null, null);
		int subjNationalityCode = ((Country)
								uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_NATIONALITY)).getCode();
		String subjOccupation = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_OCCUPATION);
		String subjGender = ((Gender) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_GENDER)).name()
													 .substring(0, 1); // 'M' or 'F'
		LocalDate birthDate = (LocalDate) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_DATE);
		
		Long subjBirthDate = null;
		if(birthDate != null) subjBirthDate = birthDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		String subjBirthPlace = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_BIRTH_PLACE);
		Long subjSamisId = (Long) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_SAMIS_ID);
		PersonType personType = (PersonType)
											uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_SAMIS_ID_TYPE);
		
		Integer subjSamisType = null;
		if(personType != null) subjSamisType = personType.getCode();
		
		String subjDocId = (String) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_ID);
		DocumentType docType = (DocumentType) uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_TYPE);
		
		Integer subjDocType = null;
		if(docType != null) subjDocType = docType.getCode();
		
		LocalDate docIssDate = (LocalDate)
									uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_ISSUANCE_DATE);
		
		Long subjDocIssDate = null;
		if(docIssDate != null) subjDocIssDate = docIssDate.atStartOfDay(AppConstants.SAUDI_ZONE).toEpochSecond();
		
		LocalDate docExpDate = (LocalDate)
									uiInputData.get(PersonInfoPaneFxController.KEY_PERSON_INFO_DOCUMENT_EXPIRY_DATE);
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
		                           subjOccupation, subjGender, subjBirthDate, subjBirthPlace, subjSamisId,
		                           subjSamisType, biometricsId, subjDocId, subjDocType, subjDocIssDate, subjDocExpDate,
		                           subjJudgementInfo, subjFingers, subjMissingFingers, subjFace, null);
	}
}