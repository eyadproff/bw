package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.JudgementInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	@Input private Long civilBiometricsId;
	@Input private Long criminalBiometricsId;
	@Input private String facePhotoBase64;
	@Input(alwaysRequired = true) private String firstName;
	@Input private String fatherName;
	@Input private String grandfatherName;
	@Input(alwaysRequired = true) private String familyName;
	@Input(alwaysRequired = true) private Gender gender;
	@Input(alwaysRequired = true) private Country nationality;
	@Input private String occupation;
	@Input private String birthPlace;
	@Input private LocalDate birthDate;
	@Input private Long personId;
	@Input private PersonType personType;
	@Input private String documentId;
	@Input private DocumentType documentType;
	@Input private LocalDate documentIssuanceDate;
	@Input private LocalDate documentExpiryDate;
	@Input(alwaysRequired = true) private String judgmentIssuer;
	@Input(alwaysRequired = true) private String judgmentNumber;
	@Input(alwaysRequired = true) private LocalDate judgmentDate;
	@Input private String caseFileNumber;
	@Input private String prisonerNumber;
	@Input private LocalDate arrestDate;
	@Input private List<CrimeCode> crimes;
	@Input private Integer tazeerLashes;
	@Input private Integer hadLashes;
	@Input private Integer fine;
	@Input private Integer jailYears;
	@Input private Integer jailMonths;
	@Input private Integer jailDays;
	@Input private Integer travelBanYears;
	@Input private Integer travelBanMonths;
	@Input private Integer travelBanDays;
	@Input private Integer exilingYears;
	@Input private Integer exilingMonths;
	@Input private Integer exilingDays;
	@Input private Integer deportationYears;
	@Input private Integer deportationMonths;
	@Input private Integer deportationDays;
	@Input private Boolean finalDeportation;
	@Input private Boolean libel;
	@Input private Boolean covenant;
	@Input private String other;
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	@Input(alwaysRequired = true) private List<Finger> fingerprints;
	@Input(alwaysRequired = true) private List<Integer> missingFingerprints;
	@Output private ConvictedReport convictedReport;
	
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
	@FXML private Label lblJudgmentNumber;
	@FXML private Label lblJudgmentDate;
	@FXML private Label lblCaseFileNumber;
	@FXML private Label lblPrisonerNumber;
	@FXML private Label lblArrestDate;
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
	
	@Override
	protected void onAttachedToScene()
	{
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
		
		Map<Integer, String> crimeEventTitles = crimeTypes.stream().collect(
									Collectors.toMap(CrimeType::getEventCode, CrimeType::getEventDesc, (k1, k2) -> k1));
		Map<Integer, String> crimeClassTitles = crimeTypes.stream().collect(
									Collectors.toMap(CrimeType::getClassCode, CrimeType::getClassDesc, (k1, k2) -> k1));
		
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, gender);
		GuiUtils.setLabelText(lblFirstName, firstName);
		GuiUtils.setLabelText(lblFatherName, fatherName);
		GuiUtils.setLabelText(lblGrandfatherName, grandfatherName);
		GuiUtils.setLabelText(lblFamilyName, familyName);
		GuiUtils.setLabelText(lblBiometricsId, civilBiometricsId);
		GuiUtils.setLabelText(lblGeneralFileNumber, criminalBiometricsId)
				.orElse(label -> label.setTextFill(Color.RED));
		GuiUtils.setLabelText(lblGender, gender);
		GuiUtils.setLabelText(lblNationality, nationality);
		GuiUtils.setLabelText(lblOccupation, occupation);
		GuiUtils.setLabelText(lblBirthPlace, birthPlace);
		GuiUtils.setLabelText(lblBirthPlace, birthPlace);
		GuiUtils.setLabelText(lblBirthDate, birthDate);
		GuiUtils.setLabelText(lblPersonId, personId);
		GuiUtils.setLabelText(lblPersonType, personType);
		GuiUtils.setLabelText(lblDocumentId, documentId);
		GuiUtils.setLabelText(lblDocumentType, documentType);
		GuiUtils.setLabelText(lblDocumentIssuanceDate, documentIssuanceDate);
		GuiUtils.setLabelText(lblDocumentExpiryDate, documentExpiryDate);
		
		int counter = 0;
		for(CrimeCode crimeCode : crimes)
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
		
		GuiUtils.setLabelText(lblJudgmentIssuer, judgmentIssuer);
		GuiUtils.setLabelText(lblJudgmentNumber, judgmentNumber);
		GuiUtils.setLabelText(lblJudgmentDate, judgmentDate);
		GuiUtils.setLabelText(lblCaseFileNumber, caseFileNumber);
		GuiUtils.setLabelText(lblPrisonerNumber, prisonerNumber);
		GuiUtils.setLabelText(lblArrestDate, arrestDate);
		GuiUtils.setLabelText(lblTazeerLashes, tazeerLashes);
		GuiUtils.setLabelText(lblHadLashes, hadLashes);
		GuiUtils.setLabelText(lblFine, fine);
		GuiUtils.setLabelText(lblJailYears, jailYears);
		GuiUtils.setLabelText(lblJailMonths, jailMonths);
		GuiUtils.setLabelText(lblJailDays, jailDays);
		GuiUtils.setLabelText(lblTravelBanYears, travelBanYears);
		GuiUtils.setLabelText(lblTravelBanMonths, travelBanMonths);
		GuiUtils.setLabelText(lblTravelBanDays, travelBanDays);
		GuiUtils.setLabelText(lblExilingYears, exilingYears);
		GuiUtils.setLabelText(lblExilingMonths, exilingMonths);
		GuiUtils.setLabelText(lblExilingDays, exilingDays);
		GuiUtils.setLabelText(lblDeportationYears, deportationYears);
		GuiUtils.setLabelText(lblDeportationMonths, deportationMonths);
		GuiUtils.setLabelText(lblDeportationDays, deportationDays);
		GuiUtils.setCheckBoxSelection(cbFinalDeportation, finalDeportation);
		GuiUtils.setCheckBoxSelection(cbLibel, libel);
		GuiUtils.setCheckBoxSelection(cbCovenant, covenant);
		GuiUtils.setLabelText(lblOther, other);
		
		GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb, ivRightIndex, ivRightMiddle,
		                                 ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle,
		                                 ivLeftRing, ivLeftLittle);
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(btnPrevious, !bShow);
		GuiUtils.showNode(btnStartOver, !bShow);
		GuiUtils.showNode(btnSubmit, !bShow);
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse) goNext();
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
			Name name = new Name(firstName, fatherName, grandfatherName, familyName, null,
			                     null, null, null);
			
			JudgementInfo judgementInfo = new JudgementInfo(judgmentIssuer,
			                                                AppUtils.gregorianDateToSeconds(judgmentDate),
			                                                judgmentNumber, tazeerLashes, hadLashes, fine, other,
			                                                jailYears, jailMonths, jailDays, travelBanDays,
			                                                travelBanMonths, travelBanYears, deportationDays,
			                                                deportationMonths, deportationYears, exilingDays,
			                                                exilingMonths, exilingYears, finalDeportation, covenant,
			                                                libel, crimes, caseFileNumber,
			                                                arrestDate == null ? null :
				                                                    AppUtils.gregorianDateToSeconds(arrestDate));
			
			UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
			
			convictedReport = new ConvictedReport(0L, 0L, criminalBiometricsId, name,
			                                      nationality.getCode(), occupation,
			                                      gender.name().substring(0, 1), // "M" or "F"
			                                      birthDate == null ? null :
					                                      AppUtils.gregorianDateToSeconds(birthDate), birthPlace,
			                                      personId, personType == null ? null : personType.getCode(),
			                                      civilBiometricsId, documentId,
			                                      documentType == null ? null : documentType.getCode(),
			                                      documentIssuanceDate == null ? null :
					                                        AppUtils.gregorianDateToSeconds(documentIssuanceDate),
			                                      documentExpiryDate == null ? null :
					                                        AppUtils.gregorianDateToSeconds(documentExpiryDate),
			                                      judgementInfo, fingerprints, missingFingerprints, facePhotoBase64,
			                                      AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId())));
			
			continueWorkflow();
		}
	}
}