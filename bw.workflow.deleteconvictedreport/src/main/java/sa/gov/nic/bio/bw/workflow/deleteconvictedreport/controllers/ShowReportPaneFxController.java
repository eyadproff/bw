package sa.gov.nic.bio.bw.workflow.deleteconvictedreport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.JudgementInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FxmlFile("showReport.fxml")
public class ShowReportPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private ConvictedReport convictedReport;
	@Input(alwaysRequired = true) private Map<Integer, String> fingerprintBase64Images;
	
	@FXML private ScrollPane paneReport;
	@FXML private VBox paneLoadingInProgress;
	@FXML private VBox paneLoadingError;
	@FXML private VBox paneImage;
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
	@FXML private ProgressIndicator piDeletingReport;
	@FXML private Button btnStartOver;
	@FXML private Button btnDeleteReport;
	
	@Override
	protected void onAttachedToScene()
	{
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		// make the checkboxes look like they are enabled
		cbFinalDeportation.setStyle("-fx-opacity: 1");
		cbLibel.setStyle("-fx-opacity: 1");
		cbCovenant.setStyle("-fx-opacity: 1");
		
		populateData();
	}
	
	private void populateData()
	{
		Long reportNumber = convictedReport.getReportNumber();
		if(reportNumber != null) lblReportNumber.setText(AppUtils.localizeNumbers(String.valueOf(reportNumber)));
		
		String sEnrollerId = convictedReport.getOperatorId();
		if(sEnrollerId != null && !sEnrollerId.trim().isEmpty())
		{
			lblEnrollerId.setText(AppUtils.localizeNumbers(sEnrollerId));
		}
		
		Long enrollmentTimeLong = convictedReport.getReportDate();
		if(enrollmentTimeLong != null) lblEnrollmentTime.setText(
															AppUtils.formatHijriGregorianDateTime(enrollmentTimeLong));
		
		String facePhotoBase64 = convictedReport.getSubjFace();
		String subjGender = convictedReport.getSubjGender();
		GuiUtils.attachFacePhotoBase64(ivPersonPhoto, facePhotoBase64, true,
		                               "F".equals(subjGender) ? Gender.FEMALE : Gender.MALE);
		
		Name subjtName = convictedReport.getSubjtName();
		if(subjtName != null)
		{
			lblFirstName.setText(subjtName.getFirstName());
			lblFatherName.setText(subjtName.getFatherName());
			lblGrandfatherName.setText(subjtName.getGrandfatherName());
			lblFamilyName.setText(subjtName.getFamilyName());
		}
		
		Long civilBiometricsId = convictedReport.getSubjBioId();
		if(civilBiometricsId != null)
				lblCivilBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(civilBiometricsId)));
		
		Long criminalBiometricsId = convictedReport.getGeneralFileNumber();
		if(criminalBiometricsId != null)
				lblCriminalBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(criminalBiometricsId)));
		
		if(subjGender != null)
		{
			lblGender.setText("F".equals(subjGender) ? resources.getString("label.female") :
					                  resources.getString("label.male"));
		}
		
		Integer subjNationalityCode = convictedReport.getSubjNationalityCode();
		
		if(subjNationalityCode != null)
		{
			@SuppressWarnings("unchecked")
			List<Country> countries = (List<Country>) Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Country countryBean = null;
			
			for(Country country : countries)
			{
				if(country.getCode() == subjNationalityCode)
				{
					countryBean = country;
					break;
				}
			}
			
			if(countryBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblNationality.setText(arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN());
			}
			else lblNationality.setText(resources.getString("combobox.unknownNationality"));
		}
		
		String subjOccupation = convictedReport.getSubjOccupation();
		if(subjOccupation != null && !subjOccupation.trim().isEmpty())
			lblOccupation.setText(AppUtils.localizeNumbers(subjOccupation));
		
		String subjBirthPlace = convictedReport.getSubjBirthPlace();
		if(subjBirthPlace != null && !subjBirthPlace.trim().isEmpty())
			lblBirthPlace.setText(AppUtils.localizeNumbers(subjBirthPlace));
		
		Long subjBirthDate = convictedReport.getSubjBirthDate();
		if(subjBirthDate != null)
			lblBirthDate.setText(AppUtils.formatHijriGregorianDate(subjBirthDate));
		
		Long samisId = convictedReport.getSubjSamisId();
		if(samisId != null) lblPersonId.setText(AppUtils.localizeNumbers(String.valueOf(samisId)));
		
		@SuppressWarnings("unchecked")
		List<PersonType> personTypes = (List<PersonType>)
				Context.getUserSession().getAttribute(PersonTypesLookup.KEY);
		
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
		if(subjDocId != null && !subjDocId.trim().isEmpty()) lblDocumentId.setText(AppUtils.localizeNumbers(subjDocId));
		
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
				AppUtils.formatHijriGregorianDate(subjDocIssDate));
		
		Long subjDocExpDate = convictedReport.getSubjDocExpDate();
		if(subjDocExpDate != null) lblDocumentExpiryDate.setText(
				AppUtils.formatHijriGregorianDate(subjDocExpDate));
		
		JudgementInfo judgementInfo = convictedReport.getSubjJudgementInfo();
		
		if(judgementInfo != null)
		{
			@SuppressWarnings("unchecked")
			List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
			
			Map<Integer, String> crimeEventTitles = crimeTypes.stream().collect(
					Collectors.toMap(CrimeType::getEventCode, CrimeType::getEventDesc, (k1, k2) -> k1));
			Map<Integer, String> crimeClassTitles = crimeTypes.stream().collect(
					Collectors.toMap(CrimeType::getClassCode, CrimeType::getClassDesc, (k1, k2) -> k1));
			
			int counter = 0;
			List<CrimeCode> crimeCodes = convictedReport.getCrimeCodes();
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
			
			String caseFileNumber = judgementInfo.getPoliceFileNum();
			if(caseFileNumber != null) lblCaseFileNumber.setText(AppUtils.localizeNumbers(caseFileNumber));
			lblJudgmentNumber.setText(AppUtils.localizeNumbers(judgementInfo.getJudgNum()));
			
			Long prisonerNumber = judgementInfo.getPrisonerNumber();
			if(prisonerNumber != null) lblPrisonerNumber.setText(AppUtils.localizeNumbers(
																					String.valueOf(prisonerNumber)));
			
			Long arrestDate = judgementInfo.getArrestDate();
			if(arrestDate != null) lblArrestDate.setText(
					AppUtils.formatHijriGregorianDate(arrestDate));
			
			lblJudgmentDate.setText(AppUtils.formatHijriGregorianDate(judgementInfo.getJudgDate()));
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
		}
		
		if(fingerprintBase64Images != null)
		{
			GuiUtils.attachFingerprintImages(fingerprintBase64Images, ivRightThumb, ivRightIndex, ivRightMiddle,
			                                 ivRightRing, ivRightLittle, ivLeftThumb, ivLeftIndex, ivLeftMiddle,
			                                 ivLeftRing, ivLeftLittle);
		}
	}
	
	@FXML
	private void onDeleteReportButtonClicked(ActionEvent actionEvent)
	{
		// TODO: show confirmation message
		continueWorkflow();
	}
}