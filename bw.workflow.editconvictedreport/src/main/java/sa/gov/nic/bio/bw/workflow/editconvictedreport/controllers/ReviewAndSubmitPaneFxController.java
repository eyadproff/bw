package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
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
import sa.gov.nic.bio.bw.workflow.commons.beans.JudgementInfo;
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
	
	@FXML private Pane paneNoDiffMessage;
	@FXML private Pane paneImage;
	@FXML private Pane paneFirstName;
	@FXML private Pane paneFatherName;
	@FXML private Pane paneGrandfatherName;
	@FXML private Pane paneFamilyName;
	@FXML private Pane paneGender;
	@FXML private Pane paneNationality;
	@FXML private Pane paneOccupation;
	@FXML private Pane paneBirthPlace;
	@FXML private Pane paneBirthDate;
	@FXML private Pane panePersonId;
	@FXML private Pane panePersonType;
	@FXML private Pane paneDocumentId;
	@FXML private Pane paneDocumentType;
	@FXML private Pane paneDocumentIssuanceDate;
	@FXML private Pane paneDocumentExpiryDate;
	@FXML private Pane paneCrimeDiffContainer;
	@FXML private Pane paneOldCrimes;
	@FXML private Pane paneNewCrimes;
	@FXML private Pane paneJudgmentIssuer;
	@FXML private Pane paneJudgmentNumber;
	@FXML private Pane paneJudgmentDate;
	@FXML private Pane paneCaseFileNumber;
	@FXML private Pane panePrisonerNumber;
	@FXML private Pane paneArrestDate;
	@FXML private Pane paneTazeerLashes;
	@FXML private Pane paneHadLashes;
	@FXML private Pane paneFine;
	@FXML private Pane paneJailYears;
	@FXML private Pane paneJailMonths;
	@FXML private Pane paneJailDays;
	@FXML private Pane paneTravelBanYears;
	@FXML private Pane paneTravelBanMonths;
	@FXML private Pane paneTravelBanDays;
	@FXML private Pane paneExilingYears;
	@FXML private Pane paneExilingMonths;
	@FXML private Pane paneExilingDays;
	@FXML private Pane paneDeportationYears;
	@FXML private Pane paneDeportationMonths;
	@FXML private Pane paneDeportationDays;
	@FXML private Pane paneOther;
	@FXML private Pane paneFinalDeportation;
	@FXML private Pane paneLibel;
	@FXML private Pane paneCovenant;
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
	@FXML private Label lblFirstNameOldValue;
	@FXML private Label lblFirstNameNewValue;
	@FXML private Label lblFatherNameOldValue;
	@FXML private Label lblFatherNameNewValue;
	@FXML private Label lblGrandfatherNameOldValue;
	@FXML private Label lblGrandfatherNameNewValue;
	@FXML private Label lblFamilyNameOldValue;
	@FXML private Label lblFamilyNameNewValue;
	@FXML private Label lblGenderOldValue;
	@FXML private Label lblGenderNewValue;
	@FXML private Label lblNationalityOldValue;
	@FXML private Label lblNationalityNewValue;
	@FXML private Label lblOccupationOldValue;
	@FXML private Label lblOccupationNewValue;
	@FXML private Label lblBirthPlaceOldValue;
	@FXML private Label lblBirthPlaceNewValue;
	@FXML private Label lblBirthDateOldValue;
	@FXML private Label lblBirthDateNewValue;
	@FXML private Label lblPersonIdOldValue;
	@FXML private Label lblPersonIdNewValue;
	@FXML private Label lblPersonTypeOldValue;
	@FXML private Label lblPersonTypeNewValue;
	@FXML private Label lblDocumentIdOldValue;
	@FXML private Label lblDocumentIdNewValue;
	@FXML private Label lblDocumentTypeOldValue;
	@FXML private Label lblDocumentTypeNewValue;
	@FXML private Label lblDocumentIssuanceDateOldValue;
	@FXML private Label lblDocumentIssuanceDateNewValue;
	@FXML private Label lblDocumentExpiryDateOldValue;
	@FXML private Label lblDocumentExpiryDateNewValue;
	@FXML private Label lblCrime1OldValue;
	@FXML private Label lblCrime2OldValue;
	@FXML private Label lblCrime3OldValue;
	@FXML private Label lblCrime4OldValue;
	@FXML private Label lblCrime5OldValue;
	@FXML private Label lblCrime1NewValue;
	@FXML private Label lblCrime2NewValue;
	@FXML private Label lblCrime3NewValue;
	@FXML private Label lblCrime4NewValue;
	@FXML private Label lblCrime5NewValue;
	@FXML private Label lblJudgmentIssuerOldValue;
	@FXML private Label lblJudgmentIssuerNewValue;
	@FXML private Label lblJudgmentNumberOldValue;
	@FXML private Label lblJudgmentNumberNewValue;
	@FXML private Label lblJudgmentDateOldValue;
	@FXML private Label lblJudgmentDateNewValue;
	@FXML private Label lblCaseFileNumberOldValue;
	@FXML private Label lblCaseFileNumberNewValue;
	@FXML private Label lblPrisonerNumberOldValue;
	@FXML private Label lblPrisonerNumberNewValue;
	@FXML private Label lblArrestDateOldValue;
	@FXML private Label lblArrestDateNewValue;
	@FXML private Label lblTazeerLashesOldValue;
	@FXML private Label lblTazeerLashesNewValue;
	@FXML private Label lblHadLashesOldValue;
	@FXML private Label lblHadLashesNewValue;
	@FXML private Label lblFineOldValue;
	@FXML private Label lblFineNewValue;
	@FXML private Label lblJailYearsOldValue;
	@FXML private Label lblJailYearsNewValue;
	@FXML private Label lblJailMonthsOldValue;
	@FXML private Label lblJailMonthsNewValue;
	@FXML private Label lblJailDaysOldValue;
	@FXML private Label lblJailDaysNewValue;
	@FXML private Label lblTravelBanYearsOldValue;
	@FXML private Label lblTravelBanYearsNewValue;
	@FXML private Label lblTravelBanMonthsOldValue;
	@FXML private Label lblTravelBanMonthsNewValue;
	@FXML private Label lblTravelBanDaysOldValue;
	@FXML private Label lblTravelBanDaysNewValue;
	@FXML private Label lblExilingYearsOldValue;
	@FXML private Label lblExilingYearsNewValue;
	@FXML private Label lblExilingMonthsOldValue;
	@FXML private Label lblExilingMonthsNewValue;
	@FXML private Label lblExilingDaysOldValue;
	@FXML private Label lblExilingDaysNewValue;
	@FXML private Label lblDeportationYearsOldValue;
	@FXML private Label lblDeportationYearsNewValue;
	@FXML private Label lblDeportationMonthsOldValue;
	@FXML private Label lblDeportationMonthsNewValue;
	@FXML private Label lblDeportationDaysOldValue;
	@FXML private Label lblDeportationDaysNewValue;
	@FXML private Label lblOtherOldValue;
	@FXML private Label lblOtherNewValue;
	@FXML private CheckBox cbFinalDeportation;
	@FXML private CheckBox cbLibel;
	@FXML private CheckBox cbCovenant;
	@FXML private CheckBox cbFinalDeportationOldValue;
	@FXML private CheckBox cbFinalDeportationNewValue;
	@FXML private CheckBox cbLibelOldValue;
	@FXML private CheckBox cbLibelNewValue;
	@FXML private CheckBox cbCovenantOldValue;
	@FXML private CheckBox cbCovenantNewValue;
	@FXML private Glyph iconFirstNameArrow;
	@FXML private Glyph iconFatherNameArrow;
	@FXML private Glyph iconGrandfatherNameArrow;
	@FXML private Glyph iconFamilyNameArrow;
	@FXML private Glyph iconGenderArrow;
	@FXML private Glyph iconNationalityArrow;
	@FXML private Glyph iconOccupationArrow;
	@FXML private Glyph iconBirthPlaceArrow;
	@FXML private Glyph iconBirthDateArrow;
	@FXML private Glyph iconPersonIdArrow;
	@FXML private Glyph iconPersonTypeArrow;
	@FXML private Glyph iconDocumentIdArrow;
	@FXML private Glyph iconDocumentTypeArrow;
	@FXML private Glyph iconDocumentIssuanceDateArrow;
	@FXML private Glyph iconDocumentExpiryDateArrow;
	@FXML private Glyph iconCrimeDiff;
	@FXML private Glyph iconJudgmentIssuerArrow;
	@FXML private Glyph iconJudgmentNumberArrow;
	@FXML private Glyph iconJudgmentDateArrow;
	@FXML private Glyph iconCaseFileNumberArrow;
	@FXML private Glyph iconPrisonerNumberArrow;
	@FXML private Glyph iconArrestDateArrow;
	@FXML private Glyph iconTazeerLashesArrow;
	@FXML private Glyph iconHadLashesArrow;
	@FXML private Glyph iconFineArrow;
	@FXML private Glyph iconJailYearsArrow;
	@FXML private Glyph iconJailMonthsArrow;
	@FXML private Glyph iconJailDaysArrow;
	@FXML private Glyph iconTravelBanYearsArrow;
	@FXML private Glyph iconTravelBanMonthsArrow;
	@FXML private Glyph iconTravelBanDaysArrow;
	@FXML private Glyph iconExilingYearsArrow;
	@FXML private Glyph iconExilingMonthsArrow;
	@FXML private Glyph iconExilingDaysArrow;
	@FXML private Glyph iconDeportationYearsArrow;
	@FXML private Glyph iconDeportationMonthsArrow;
	@FXML private Glyph iconDeportationDaysArrow;
	@FXML private Glyph iconOtherArrow;
	@FXML private Glyph iconFinalDeportationArrow;
	@FXML private Glyph iconLibelArrow;
	@FXML private Glyph iconCovenantArrow;
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
		
		if(facePhotoBase64 != null) GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true,
		                                                           genderNewValue);
		
		if(fingerprintBase64Images != null) GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb,
		                                                                     ivRightIndex, ivRightMiddle, ivRightRing,
		                                                                     ivRightLittle, ivLeftThumb, ivLeftIndex,
		                                                                     ivLeftMiddle, ivLeftRing, ivLeftLittle);
		
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
		
		GuiUtils.setLabelText(lblFirstNameOldValue, firstNameOldValue);
		GuiUtils.setLabelText(lblFatherNameOldValue, fatherNameOldValue);
		GuiUtils.setLabelText(lblGrandfatherNameOldValue, grandfatherNameOldValue);
		GuiUtils.setLabelText(lblFamilyNameOldValue, familyNameOldValue);
		GuiUtils.setLabelText(lblGenderOldValue, genderOldValue);
		GuiUtils.setLabelText(lblNationalityOldValue, nationalityOldValue);
		GuiUtils.setLabelText(lblOccupationOldValue, occupationOldValue);
		GuiUtils.setLabelText(lblBirthPlaceOldValue, birthPlaceOldValue);
		GuiUtils.setLabelText(lblBirthDateOldValue, birthDateOldValue);
		GuiUtils.setLabelText(lblPersonIdOldValue, personIdOldValue);
		GuiUtils.setLabelText(lblPersonTypeOldValue, personTypeOldValue);
		GuiUtils.setLabelText(lblDocumentIdOldValue, documentIdOldValue);
		GuiUtils.setLabelText(lblDocumentTypeOldValue, documentTypeOldValue);
		GuiUtils.setLabelText(lblDocumentIssuanceDateOldValue, documentIssuanceDateOldValue);
		GuiUtils.setLabelText(lblDocumentExpiryDateOldValue, documentExpiryDateOldValue);
		GuiUtils.setLabelText(lblJudgmentIssuerOldValue, judgmentIssuerOldValue);
		GuiUtils.setLabelText(lblJudgmentNumberOldValue, judgmentNumberOldValue);
		GuiUtils.setLabelText(lblJudgmentDateOldValue, judgmentDateOldValue);
		GuiUtils.setLabelText(lblCaseFileNumberOldValue, caseFileNumberOldValue);
		GuiUtils.setLabelText(lblPrisonerNumberOldValue, prisonerNumberOldValue);
		GuiUtils.setLabelText(lblArrestDateOldValue, arrestDateOldValue);
		GuiUtils.setLabelText(lblTazeerLashesOldValue, tazeerLashesOldValue);
		GuiUtils.setLabelText(lblHadLashesOldValue, hadLashesOldValue);
		GuiUtils.setLabelText(lblFineOldValue, fineOldValue);
		GuiUtils.setLabelText(lblJailYearsOldValue, jailYearsOldValue);
		GuiUtils.setLabelText(lblJailMonthsOldValue, jailMonthsOldValue);
		GuiUtils.setLabelText(lblJailDaysOldValue, jailDaysOldValue);
		GuiUtils.setLabelText(lblTravelBanYearsOldValue, travelBanYearsOldValue);
		GuiUtils.setLabelText(lblTravelBanMonthsOldValue, travelBanMonthsOldValue);
		GuiUtils.setLabelText(lblTravelBanDaysOldValue, travelBanDaysOldValue);
		GuiUtils.setLabelText(lblExilingYearsOldValue, exilingYearsOldValue);
		GuiUtils.setLabelText(lblExilingMonthsOldValue, exilingMonthsOldValue);
		GuiUtils.setLabelText(lblExilingDaysOldValue, exilingDaysOldValue);
		GuiUtils.setLabelText(lblDeportationYearsOldValue, deportationYearsOldValue);
		GuiUtils.setLabelText(lblDeportationMonthsOldValue, deportationMonthsOldValue);
		GuiUtils.setLabelText(lblDeportationDaysOldValue, deportationDaysOldValue);
		GuiUtils.setLabelText(lblOtherOldValue, otherOldValue);
		GuiUtils.setCheckBoxSelection(cbFinalDeportationOldValue, finalDeportationOldValue);
		GuiUtils.setCheckBoxSelection(cbLibelOldValue, libelOldValue);
		GuiUtils.setCheckBoxSelection(cbCovenantOldValue, covenantOldValue);
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		FontAwesome.Glyph arrowIcon = rtl ? FontAwesome.Glyph.LONG_ARROW_LEFT : FontAwesome.Glyph.LONG_ARROW_RIGHT;
		iconFirstNameArrow.setIcon(arrowIcon);
		iconFatherNameArrow.setIcon(arrowIcon);
		iconGrandfatherNameArrow.setIcon(arrowIcon);
		iconFamilyNameArrow.setIcon(arrowIcon);
		iconGenderArrow.setIcon(arrowIcon);
		iconNationalityArrow.setIcon(arrowIcon);
		iconOccupationArrow.setIcon(arrowIcon);
		iconBirthPlaceArrow.setIcon(arrowIcon);
		iconBirthDateArrow.setIcon(arrowIcon);
		iconPersonIdArrow.setIcon(arrowIcon);
		iconPersonTypeArrow.setIcon(arrowIcon);
		iconDocumentIdArrow.setIcon(arrowIcon);
		iconDocumentTypeArrow.setIcon(arrowIcon);
		iconDocumentIssuanceDateArrow.setIcon(arrowIcon);
		iconDocumentExpiryDateArrow.setIcon(arrowIcon);
		iconCrimeDiff.setIcon(arrowIcon);
		iconJudgmentIssuerArrow.setIcon(arrowIcon);
		iconJudgmentNumberArrow.setIcon(arrowIcon);
		iconJudgmentDateArrow.setIcon(arrowIcon);
		iconCaseFileNumberArrow.setIcon(arrowIcon);
		iconPrisonerNumberArrow.setIcon(arrowIcon);
		iconArrestDateArrow.setIcon(arrowIcon);
		iconTazeerLashesArrow.setIcon(arrowIcon);
		iconHadLashesArrow.setIcon(arrowIcon);
		iconFineArrow.setIcon(arrowIcon);
		iconJailYearsArrow.setIcon(arrowIcon);
		iconJailMonthsArrow.setIcon(arrowIcon);
		iconJailDaysArrow.setIcon(arrowIcon);
		iconTravelBanYearsArrow.setIcon(arrowIcon);
		iconTravelBanMonthsArrow.setIcon(arrowIcon);
		iconTravelBanDaysArrow.setIcon(arrowIcon);
		iconExilingYearsArrow.setIcon(arrowIcon);
		iconExilingMonthsArrow.setIcon(arrowIcon);
		iconExilingDaysArrow.setIcon(arrowIcon);
		iconDeportationYearsArrow.setIcon(arrowIcon);
		iconDeportationMonthsArrow.setIcon(arrowIcon);
		iconDeportationDaysArrow.setIcon(arrowIcon);
		iconOtherArrow.setIcon(arrowIcon);
		iconFinalDeportationArrow.setIcon(arrowIcon);
		iconLibelArrow.setIcon(arrowIcon);
		iconCovenantArrow.setIcon(arrowIcon);
		
		paneFirstName.visibleProperty().bind(
				Bindings.notEqual(lblFirstNameOldValue.textProperty(), lblFirstNameNewValue.textProperty()));
		paneFatherName.visibleProperty().bind(
				Bindings.notEqual(lblFatherNameOldValue.textProperty(), lblFatherNameNewValue.textProperty()));
		paneGrandfatherName.visibleProperty().bind(
				Bindings.notEqual(lblGrandfatherNameOldValue.textProperty(), lblGrandfatherNameNewValue.textProperty()));
		paneFamilyName.visibleProperty().bind(
				Bindings.notEqual(lblFamilyNameOldValue.textProperty(), lblFamilyNameNewValue.textProperty()));
		paneGender.visibleProperty().bind(
				Bindings.notEqual(lblGenderOldValue.textProperty(), lblGenderNewValue.textProperty()));
		paneNationality.visibleProperty().bind(
				Bindings.notEqual(lblNationalityOldValue.textProperty(), lblNationalityNewValue.textProperty()));
		paneOccupation.visibleProperty().bind(
				Bindings.notEqual(lblOccupationOldValue.textProperty(), lblOccupationNewValue.textProperty()));
		paneBirthPlace.visibleProperty().bind(
				Bindings.notEqual(lblBirthPlaceOldValue.textProperty(), lblBirthPlaceNewValue.textProperty()));
		paneBirthDate.visibleProperty().bind(
				Bindings.notEqual(lblBirthDateOldValue.textProperty(), lblBirthDateNewValue.textProperty()));
		paneBirthDate.managedProperty().bind(
				Bindings.notEqual(lblBirthDateOldValue.textProperty(), lblBirthDateNewValue.textProperty()));
		panePersonId.visibleProperty().bind(
				Bindings.notEqual(lblPersonIdOldValue.textProperty(), lblPersonIdNewValue.textProperty()));
		panePersonType.visibleProperty().bind(
				Bindings.notEqual(lblPersonTypeOldValue.textProperty(), lblPersonTypeNewValue.textProperty()));
		paneDocumentId.visibleProperty().bind(
				Bindings.notEqual(lblDocumentIdOldValue.textProperty(), lblDocumentIdNewValue.textProperty()));
		paneDocumentType.visibleProperty().bind(
				Bindings.notEqual(lblDocumentTypeOldValue.textProperty(), lblDocumentTypeNewValue.textProperty()));
		paneDocumentIssuanceDate.visibleProperty().bind(
				Bindings.notEqual(lblDocumentIssuanceDateOldValue.textProperty(),
				                  lblDocumentIssuanceDateNewValue.textProperty()));
		paneDocumentIssuanceDate.managedProperty().bind(
				Bindings.notEqual(lblDocumentIssuanceDateOldValue.textProperty(),
				                  lblDocumentIssuanceDateNewValue.textProperty()));
		paneDocumentExpiryDate.visibleProperty().bind(
				Bindings.notEqual(lblDocumentExpiryDateOldValue.textProperty(),
				                  lblDocumentExpiryDateNewValue.textProperty()));
		paneDocumentExpiryDate.managedProperty().bind(
				Bindings.notEqual(lblDocumentExpiryDateOldValue.textProperty(),
				                  lblDocumentExpiryDateNewValue.textProperty()));
		paneJudgmentIssuer.visibleProperty().bind(
				Bindings.notEqual(lblJudgmentIssuerOldValue.textProperty(), lblJudgmentIssuerNewValue.textProperty()));
		paneJudgmentNumber.visibleProperty().bind(
				Bindings.notEqual(lblJudgmentNumberOldValue.textProperty(), lblJudgmentNumberNewValue.textProperty()));
		paneJudgmentDate.visibleProperty().bind(
				Bindings.notEqual(lblJudgmentDateOldValue.textProperty(), lblJudgmentDateNewValue.textProperty()));
		paneJudgmentDate.managedProperty().bind(
				Bindings.notEqual(lblJudgmentDateOldValue.textProperty(), lblJudgmentDateNewValue.textProperty()));
		paneCaseFileNumber.visibleProperty().bind(
				Bindings.notEqual(lblCaseFileNumberOldValue.textProperty(), lblCaseFileNumberNewValue.textProperty()));
		panePrisonerNumber.visibleProperty().bind(
				Bindings.notEqual(lblPrisonerNumberOldValue.textProperty(), lblPrisonerNumberNewValue.textProperty()));
		paneArrestDate.visibleProperty().bind(
				Bindings.notEqual(lblArrestDateOldValue.textProperty(), lblArrestDateNewValue.textProperty()));
		paneArrestDate.managedProperty().bind(
				Bindings.notEqual(lblArrestDateOldValue.textProperty(), lblArrestDateNewValue.textProperty()));
		paneTazeerLashes.visibleProperty().bind(
				Bindings.notEqual(lblTazeerLashesOldValue.textProperty(), lblTazeerLashesNewValue.textProperty()));
		paneHadLashes.visibleProperty().bind(
				Bindings.notEqual(lblHadLashesOldValue.textProperty(), lblHadLashesNewValue.textProperty()));
		paneFine.visibleProperty().bind(
				Bindings.notEqual(lblFineOldValue.textProperty(), lblFineNewValue.textProperty()));
		paneJailYears.visibleProperty().bind(
				Bindings.notEqual(lblJailYearsOldValue.textProperty(), lblJailYearsNewValue.textProperty()));
		paneJailMonths.visibleProperty().bind(
				Bindings.notEqual(lblJailMonthsOldValue.textProperty(), lblJailMonthsNewValue.textProperty()));
		paneJailDays.visibleProperty().bind(
				Bindings.notEqual(lblJailDaysOldValue.textProperty(), lblJailDaysNewValue.textProperty()));
		paneTravelBanYears.visibleProperty().bind(
				Bindings.notEqual(lblTravelBanYearsOldValue.textProperty(), lblTravelBanYearsNewValue.textProperty()));
		paneTravelBanMonths.visibleProperty().bind(
				Bindings.notEqual(lblTravelBanMonthsOldValue.textProperty(),
				                  lblTravelBanMonthsNewValue.textProperty()));
		paneTravelBanDays.visibleProperty().bind(
				Bindings.notEqual(lblTravelBanDaysOldValue.textProperty(), lblTravelBanDaysNewValue.textProperty()));
		paneExilingYears.visibleProperty().bind(
				Bindings.notEqual(lblExilingYearsOldValue.textProperty(), lblExilingYearsNewValue.textProperty()));
		paneExilingMonths.visibleProperty().bind(
				Bindings.notEqual(lblExilingMonthsOldValue.textProperty(), lblExilingMonthsNewValue.textProperty()));
		paneExilingDays.visibleProperty().bind(
				Bindings.notEqual(lblExilingDaysOldValue.textProperty(), lblExilingDaysNewValue.textProperty()));
		paneDeportationYears.visibleProperty().bind(
				Bindings.notEqual(lblDeportationYearsOldValue.textProperty(),
				                  lblDeportationYearsNewValue.textProperty()));
		paneDeportationMonths.visibleProperty().bind(
				Bindings.notEqual(lblDeportationMonthsOldValue.textProperty(),
				                  lblDeportationMonthsNewValue.textProperty()));
		paneDeportationDays.visibleProperty().bind(
				Bindings.notEqual(lblDeportationDaysOldValue.textProperty(),
				                  lblDeportationDaysNewValue.textProperty()));
		paneOther.visibleProperty().bind(
				Bindings.notEqual(lblOtherOldValue.textProperty(), lblOtherNewValue.textProperty()));
		paneOther.managedProperty().bind(
				Bindings.notEqual(lblOtherOldValue.textProperty(), lblOtherNewValue.textProperty()));
		paneFinalDeportation.visibleProperty().bind(
				Bindings.notEqual(cbFinalDeportationOldValue.selectedProperty(),
				                  cbFinalDeportationNewValue.selectedProperty()));
		paneLibel.visibleProperty().bind(
				Bindings.notEqual(cbLibelOldValue.selectedProperty(), cbLibelNewValue.selectedProperty()));
		paneCovenant.visibleProperty().bind(
				Bindings.notEqual(cbCovenantOldValue.selectedProperty(), cbCovenantNewValue.selectedProperty()));
		
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
		
		counter = 0;
		for(CrimeCode crimeCode : oldCrimes)
		{
			switch(counter++)
			{
				case 0:
				{
					lblCrime1OldValue.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
							                                crimeClassTitles.get(crimeCode.getCrimeClass()));
					break;
				}
				case 1:
				{
					GuiUtils.showNode(lblCrime2OldValue, true);
					lblCrime2OldValue.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
							                                crimeClassTitles.get(crimeCode.getCrimeClass()));
					break;
				}
				case 2:
				{
					GuiUtils.showNode(lblCrime3OldValue, true);
					lblCrime3OldValue.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
							                                crimeClassTitles.get(crimeCode.getCrimeClass()));
					break;
				}
				case 3:
				{
					GuiUtils.showNode(lblCrime4OldValue, true);
					lblCrime4OldValue.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
							                                crimeClassTitles.get(crimeCode.getCrimeClass()));
					break;
				}
				case 4:
				{
					GuiUtils.showNode(lblCrime5OldValue, true);
					lblCrime5OldValue.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
							                                crimeClassTitles.get(crimeCode.getCrimeClass()));
					break;
				}
			}
		}
		
		GuiUtils.showNode(paneCrimeDiffContainer, !sameCrimes(oldCrimes, newCrimes));
		
		boolean noDiff = !paneFirstName.isVisible() && !paneFatherName.isVisible() &&
				!paneGrandfatherName.isVisible() && !paneFamilyName.isVisible() && !paneGender.isVisible() &&
				!paneNationality.isVisible() && !paneOccupation.isVisible() && !paneBirthPlace.isVisible() &&
				!paneBirthDate.isVisible() && !panePersonId.isVisible() && !panePersonType.isVisible() &&
				!paneDocumentId.isVisible() && !paneDocumentType.isVisible() && !paneDocumentIssuanceDate.isVisible() &&
				!paneDocumentExpiryDate.isVisible() && !paneCrimeDiffContainer.isVisible() &&
				!paneJudgmentIssuer.isVisible() && !paneJudgmentNumber.isVisible() && !paneJudgmentDate.isVisible() &&
				!paneCaseFileNumber.isVisible() && !panePrisonerNumber.isVisible() && !paneArrestDate.isVisible() &&
				!paneTazeerLashes.isVisible() && !paneHadLashes.isVisible() && !paneFine.isVisible() &&
				!paneJailYears.isVisible() && !paneJailMonths.isVisible() && !paneJailDays.isVisible() &&
				!paneTravelBanYears.isVisible() && !paneTravelBanMonths.isVisible() && !paneTravelBanDays.isVisible() &&
				!paneExilingYears.isVisible() && !paneExilingMonths.isVisible() && !paneExilingDays.isVisible() &&
				!paneDeportationYears.isVisible() && !paneDeportationMonths.isVisible() &&
				!paneDeportationDays.isVisible() && !paneOther.isVisible() && !paneFinalDeportation.isVisible() &&
				!paneLibel.isVisible() && !paneCovenant.isVisible();
		
		btnSubmit.setDisable(noDiff);
		GuiUtils.showNode(paneNoDiffMessage, noDiff);
		
		Name name = new Name(firstNameNewValue, fatherNameNewValue, grandfatherNameNewValue, familyNameNewValue,
		                     null, null, null,
		                     null);
		
		JudgementInfo judgementInfo = new JudgementInfo(judgmentIssuerNewValue,
                        judgmentDateNewValue != null ? AppUtils.gregorianDateToSeconds(judgmentDateNewValue) : null,
                        judgmentNumberNewValue, tazeerLashesNewValue, hadLashesNewValue, fineNewValue, otherNewValue,
                        jailYearsNewValue, jailMonthsNewValue, jailDaysNewValue, travelBanDaysNewValue,
                        travelBanMonthsNewValue, travelBanYearsNewValue, deportationDaysNewValue,
                        deportationMonthsNewValue, deportationYearsNewValue, exilingDaysNewValue, exilingMonthsNewValue,
                        exilingYearsNewValue, finalDeportationNewValue, covenantNewValue, libelNewValue,
						caseFileNumberNewValue, prisonerNumberNewValue,
                        arrestDateNewValue != null ? AppUtils.gregorianDateToSeconds(arrestDateNewValue) : null);
		
		convictedReport = new ConvictedReport(oldReportNumber, null, criminalBiometricsId, name,
		                                      nationalityNewValue.getCode(),
		                                      nationalityNewValue.getMofaNationalityCode(), occupationNewValue,
		                                      genderNewValue.name().substring(0, 1), // "M" or "F"
		                                      birthDateNewValue != null ?
		                                            AppUtils.gregorianDateToSeconds(birthDateNewValue) : null,
		                                      birthPlaceNewValue, personIdNewValue,
		                                      personTypeNewValue != null ? personTypeNewValue.getCode() : null,
		                                      null, documentIdNewValue,
		                                      documentTypeNewValue != null ? documentTypeNewValue.getCode() : null,
		                                      documentIssuanceDateNewValue != null ?
			                                      AppUtils.gregorianDateToSeconds(documentIssuanceDateNewValue) : null,
		                                      documentExpiryDateNewValue != null ?
			                                      AppUtils.gregorianDateToSeconds(documentExpiryDateNewValue) : null,
		                                      judgementInfo, null, null, null,
		                                      null, newCrimes, null, null,
		                                      null, null, null, null);
	}
	
	@Override
	public void onReturnFromWorkflow(boolean successfulResponse)
	{
		if(successfulResponse) goNext();
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
		String headerText = resources.getString("editingConvictedReport.confirmation.header");
		String contentText = resources.getString("editingConvictedReport.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) continueWorkflow();
	}
	
	private static boolean sameCrimes(List<CrimeCode> crimeCodes1, List<CrimeCode> crimeCodes2)
	{
		if(crimeCodes1 == null || crimeCodes2 == null) return false;
		if(crimeCodes1.size() != crimeCodes2.size()) return false;
		
		for(int i = 0; i < crimeCodes1.size(); i++)
		{
			CrimeCode crimeCode1 = crimeCodes1.get(i);
			CrimeCode crimeCode2 = crimeCodes2.get(i);
			if(crimeCode1.getCrimeEvent() != crimeCode2.getCrimeEvent()) return false;
			if(crimeCode1.getCrimeClass() != crimeCode2.getCrimeClass()) return false;
		}
		
		return true;
	}
}