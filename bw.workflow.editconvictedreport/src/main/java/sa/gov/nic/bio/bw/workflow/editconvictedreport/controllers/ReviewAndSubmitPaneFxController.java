package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FxmlFile("reviewAndSubmit.fxml")
public class ReviewAndSubmitPaneFxController extends WizardStepFxControllerBase
{
	@Input private Long oldReportNumber;
	@Input private String oldEnrollerId;
	@Input private Long oldEnrollmentTime;
	@Input private String facePhotoBase64;
	@Input private Map<Integer, String> fingerprintBase64Images;
	@Input private Long civilBiometricsId;
	@Input private Long criminalBiometricsId;
	@Input private String firstNameOldValue;
	@Input private String firstNameNewValue;
	@Input private String fatherNameOldValue;
	@Input private String fatherNameNewValue;
	@Input private String grandfatherNameOldValue;
	@Input private String grandfatherNameNewValue;
	@Input private String familyNameOldValue;
	@Input private String familyNameNewValue;
	@Input private Gender genderOldValue;
	@Input private Gender genderNewValue;
	@Input private Country nationalityOldValue;
	@Input private Country nationalityNewValue;
	@Input private String occupationOldValue;
	@Input private String occupationNewValue;
	@Input private String birthPlaceOldValue;
	@Input private String birthPlaceNewValue;
	@Input private LocalDate birthDateOldValue;
	@Input private LocalDate birthDateNewValue;
	@Input private Long personIdOldValue;
	@Input private Long personIdNewValue;
	@Input private PersonType personTypeOldValue;
	@Input private PersonType personTypeNewValue;
	@Input private String documentIdOldValue;
	@Input private String documentIdNewValue;
	@Input private DocumentType documentTypeOldValue;
	@Input private DocumentType documentTypeNewValue;
	@Input private LocalDate documentIssuanceDateOldValue;
	@Input private LocalDate documentIssuanceDateNewValue;
	@Input private LocalDate documentExpiryDateOldValue;
	@Input private LocalDate documentExpiryDateNewValue;
	@Input private String judgmentIssuerOldValue;
	@Input private String judgmentIssuerNewValue;
	@Input private String judgmentNumberOldValue;
	@Input private String judgmentNumberNewValue;
	@Input private LocalDate judgmentDateOldValue;
	@Input private LocalDate judgmentDateNewValue;
	@Input private String caseFileNumberOldValue;
	@Input private String caseFileNumberNewValue;
	@Input private Long prisonerNumberOldValue;
	@Input private Long prisonerNumberNewValue;
	@Input private LocalDate arrestDateOldValue;
	@Input private LocalDate arrestDateNewValue;
	@Input private List<CrimeCode> oldCrimes;
	@Input private List<CrimeCode> newCrimes;
	@Input private Integer tazeerLashesOldValue;
	@Input private Integer tazeerLashesNewValue;
	@Input private Integer hadLashesOldValue;
	@Input private Integer hadLashesNewValue;
	@Input private Integer fineOldValue;
	@Input private Integer fineNewValue;
	@Input private Integer jailYearsOldValue;
	@Input private Integer jailYearsNewValue;
	@Input private Integer jailMonthsOldValue;
	@Input private Integer jailMonthsNewValue;
	@Input private Integer jailDaysOldValue;
	@Input private Integer jailDaysNewValue;
	@Input private Integer travelBanYearsOldValue;
	@Input private Integer travelBanYearsNewValue;
	@Input private Integer travelBanMonthsOldValue;
	@Input private Integer travelBanMonthsNewValue;
	@Input private Integer travelBanDaysOldValue;
	@Input private Integer travelBanDaysNewValue;
	@Input private Integer exilingYearsOldValue;
	@Input private Integer exilingYearsNewValue;
	@Input private Integer exilingMonthsOldValue;
	@Input private Integer exilingMonthsNewValue;
	@Input private Integer exilingDaysOldValue;
	@Input private Integer exilingDaysNewValue;
	@Input private Integer deportationYearsOldValue;
	@Input private Integer deportationYearsNewValue;
	@Input private Integer deportationMonthsOldValue;
	@Input private Integer deportationMonthsNewValue;
	@Input private Integer deportationDaysOldValue;
	@Input private Integer deportationDaysNewValue;
	@Input private Boolean finalDeportationOldValue;
	@Input private Boolean finalDeportationNewValue;
	@Input private Boolean libelOldValue;
	@Input private Boolean libelNewValue;
	@Input private Boolean covenantOldValue;
	@Input private Boolean covenantNewValue;
	@Input private String otherOldValue;
	@Input private String otherNewValue;
	@Output private ConvictedReport convictedReport;
	
	@FXML private Pane paneImage;
	@FXML private Label lblReportNumber;
	@FXML private Label lblEnrollerId;
	@FXML private Label lblEnrollmentTime;
	@FXML private Label lblCivilBiometricsId;
	@FXML private Label lblCriminalBiometricsId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
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
	@FXML private ImageViewPane paneImageView;
	@FXML private ImageView ivPersonPhoto;
	@FXML private ImageView ivRightThumb;
	@FXML private ImageView ivRightIndex;
	@FXML private ImageView ivRightMiddle;
	@FXML private ImageView ivRightRing;
	@FXML private ImageView ivRightLittle;
	@FXML private ImageView ivLeftThumb;
	@FXML private ImageView ivLeftIndex;
	@FXML private ImageView ivLeftMiddle;
	@FXML private ImageView ivLeftRing;
	@FXML private ImageView ivLeftLittle;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrevious;
	@FXML private Button btnSubmit;
	
	@Override
	protected void onAttachedToScene()
	{
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		GuiUtils.setLabelText(lblReportNumber, oldReportNumber);
		GuiUtils.setLabelText(lblEnrollerId, oldEnrollerId);
		
		if(oldEnrollmentTime != null)
		{
			String sDate = AppUtils.formatHijriGregorianDateTime(oldEnrollmentTime);
			GuiUtils.setLabelText(lblEnrollmentTime, sDate);
		}
		
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true, genderNewValue);
		if(fingerprintBase64Images != null)
		{
			GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb, ivRightIndex, ivRightMiddle,
			                                 ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle,
			                                 ivLeftRing, ivLeftLittle);
		}
		
		GuiUtils.setLabelText(lblCivilBiometricsId, civilBiometricsId);
		GuiUtils.setLabelText(lblCriminalBiometricsId, criminalBiometricsId);
		GuiUtils.setLabelText(lblFirstName, firstNameNewValue);
		GuiUtils.setLabelText(lblFatherName, fatherNameNewValue);
		GuiUtils.setLabelText(lblGrandfatherName, grandfatherNameNewValue);
		GuiUtils.setLabelText(lblFamilyName, familyNameNewValue);
		GuiUtils.setLabelText(lblGender, genderNewValue);
		GuiUtils.setLabelText(lblNationality, nationalityNewValue);
		GuiUtils.setLabelText(lblOccupation, occupationNewValue);
		GuiUtils.setLabelText(lblBirthPlace, birthPlaceNewValue);
		GuiUtils.setLabelText(lblBirthDate, birthDateNewValue);
		GuiUtils.setLabelText(lblPersonId, personIdNewValue);
		GuiUtils.setLabelText(lblPersonType, personTypeNewValue);
		GuiUtils.setLabelText(lblDocumentId, documentIdNewValue);
		GuiUtils.setLabelText(lblDocumentType, documentTypeNewValue);
		GuiUtils.setLabelText(lblDocumentIssuanceDate, documentIssuanceDateNewValue);
		GuiUtils.setLabelText(lblDocumentExpiryDate, documentExpiryDateNewValue);
		GuiUtils.setLabelText(lblJudgmentIssuer, judgmentIssuerNewValue);
		GuiUtils.setLabelText(lblJudgmentNumber, judgmentNumberNewValue);
		GuiUtils.setLabelText(lblJudgmentDate, judgmentDateNewValue);
		GuiUtils.setLabelText(lblCaseFileNumber, caseFileNumberNewValue);
		GuiUtils.setLabelText(lblPrisonerNumber, prisonerNumberNewValue);
		GuiUtils.setLabelText(lblArrestDate, arrestDateNewValue);
		GuiUtils.setLabelText(lblTazeerLashes, tazeerLashesNewValue);
		GuiUtils.setLabelText(lblHadLashes, hadLashesNewValue);
		GuiUtils.setLabelText(lblFine, fineNewValue);
		GuiUtils.setLabelText(lblJailYears, jailYearsNewValue);
		GuiUtils.setLabelText(lblJailMonths, jailMonthsNewValue);
		GuiUtils.setLabelText(lblJailDays, jailDaysNewValue);
		GuiUtils.setLabelText(lblTravelBanYears, travelBanYearsNewValue);
		GuiUtils.setLabelText(lblTravelBanMonths, travelBanMonthsNewValue);
		GuiUtils.setLabelText(lblTravelBanDays, travelBanDaysNewValue);
		GuiUtils.setLabelText(lblExilingYears, exilingYearsNewValue);
		GuiUtils.setLabelText(lblExilingMonths, exilingMonthsNewValue);
		GuiUtils.setLabelText(lblExilingDays, exilingDaysNewValue);
		GuiUtils.setLabelText(lblDeportationYears, deportationYearsNewValue);
		GuiUtils.setLabelText(lblDeportationMonths, deportationMonthsNewValue);
		GuiUtils.setLabelText(lblDeportationDays, deportationDaysNewValue);
		GuiUtils.setLabelText(lblOther, otherNewValue);
		GuiUtils.setCheckBoxSelection(cbFinalDeportation, finalDeportationNewValue);
		GuiUtils.setCheckBoxSelection(cbLibel, libelNewValue);
		GuiUtils.setCheckBoxSelection(cbCovenant, covenantNewValue);
		
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
		
		Map<Integer, String> crimeEventTitles = crimeTypes.stream().collect(
				Collectors.toMap(CrimeType::getEventCode, CrimeType::getEventDesc, (k1, k2) -> k1));
		Map<Integer, String> crimeClassTitles = crimeTypes.stream().collect(
				Collectors.toMap(CrimeType::getClassCode, CrimeType::getClassDesc, (k1, k2) -> k1));
		
		int counter = 0;
		for(CrimeCode crimeCode : newCrimes)
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
	}
	
	@Override
	public void onShowingProgress(boolean bShow)
	{
		GuiUtils.showNode(piProgress, bShow);
		GuiUtils.showNode(btnPrevious, !bShow);
		GuiUtils.showNode(btnStartOver, !bShow);
		GuiUtils.showNode(btnSubmit, !bShow);
	}
	
	@FXML
	private void onSubmitButtonClicked(ActionEvent actionEvent)
	{
		String headerText =
					resources.getString("printConvictedPresent.submittingConvictedReport.confirmation.header");
		String contentText =
					resources.getString("printConvictedPresent.submittingConvictedReport.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) goNext();
	}
}