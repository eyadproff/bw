package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
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
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.beans.JudgementInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.controllers.ConvictedReportNestedFxController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
	@Input private Long prisonerNumber;
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
	
	@FXML private ConvictedReportNestedFxController convictedReportNestedPaneController;
	@FXML private Pane convictedReportNestedPane;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrevious;
	@FXML private Button btnSubmit;
	
	@Override
	protected void onAttachedToScene()
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
		                                                libel, caseFileNumber, prisonerNumber,
		                                                arrestDate == null ? null :
				                                                AppUtils.gregorianDateToSeconds(arrestDate));
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		
		//convictedReport = new ConvictedReport(null, null, criminalBiometricsId, name,
		//                                      nationality.getCode(), nationality.getMofaNationalityCode(),
		//                                      occupation, gender.name().substring(0, 1), // "M" or "F"
		//                                      birthDate == null ? null :
		//		                                      AppUtils.gregorianDateToSeconds(birthDate), birthPlace,
		//                                      personId, personType == null ? null : personType.getCode(),
		//                                      civilBiometricsId, documentId,
		//                                      documentType == null ? null : documentType.getCode(),
		//                                      documentIssuanceDate == null ? null :
		//		                                      AppUtils.gregorianDateToSeconds(documentIssuanceDate),
		//                                      documentExpiryDate == null ? null :
		//		                                      AppUtils.gregorianDateToSeconds(documentExpiryDate),
		//                                      judgementInfo, fingerprints, missingFingerprints, facePhotoBase64,
		//                                      AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId())),
		//                                      crimes);
		
		convictedReportNestedPaneController.setWillBeGeneratedTextOnCriminalBiometricsId();
		convictedReportNestedPaneController.populateConvictedReportData(convictedReport, fingerprintBase64Images);
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