package sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.BodyFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.workflow.commons.beans.ConvictedReport;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;
import sa.gov.nic.bio.bw.workflow.commons.beans.JudgementInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonType;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.PersonTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.ConvictedReportInquiryByReportNumberWorkflowTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.workflow.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.workflow.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.workflow.convictedreportinquirybysearchcriteria.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.tasks.BuildConvictedReportTask;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@FxmlFile("showReportDialog.fxml")
public class ShowReportDialogFxController extends BodyFxControllerBase
{
	@FXML private ScrollPane paneReport;
	@FXML private VBox paneLoadingInProgress;
	@FXML private VBox paneLoadingError;
	@FXML private VBox paneImage;
	@FXML private HBox buttonPane;
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
	@FXML private ProgressIndicator piPrinting;
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;
	@FXML private Dialog<ButtonType> dialog;
	
	private FileChooser fileChooser = new FileChooser();
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	
	private Long reportNumber;
	private ConvictedReport convictedReport;
	private Map<Integer, String> fingerprintBase64Images;
	
	@Override
	protected void initialize()
	{
		paneImageView.maxWidthProperty().bind(paneImage.widthProperty());
		
		// make the checkboxes look like they are enabled
		cbFinalDeportation.setStyle("-fx-opacity: 1");
		cbLibel.setStyle("-fx-opacity: 1");
		cbCovenant.setStyle("-fx-opacity: 1");
		
		fileChooser.setTitle(resources.getString("fileChooser.saveReportAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.saveReportAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
		
		dialog.setOnShown(event ->
		{
			setData(ConvictedReportInquiryByReportNumberWorkflowTask.class, "reportNumber", reportNumber);
			boolean success = executeUiTask(ConvictedReportInquiryByReportNumberWorkflowTask.class, new SuccessHandler()
			{
				@Override
				protected void onSuccess()
				{
					convictedReport = getData("convictedReport");
					List<Finger> subjFingers = convictedReport.getSubjFingers();
					List<Integer> subjMissingFingers = convictedReport.getSubjMissingFingers();
					
					setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					        "fingerprints", subjFingers);
					setData(ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class,
					        "missingFingerprints", subjMissingFingers);
					
					boolean success = executeUiTask(
						ConvertWsqFingerprintsToSegmentedFingerprintBase64ImagesWorkflowTask.class, new SuccessHandler()
					{
					    @Override
					    protected void onSuccess()
					    {
					        fingerprintBase64Images = getData("fingerprintBase64Images");
					        populateData();
					    }
					}, throwable ->
					{
					    GuiUtils.showNode(paneLoadingInProgress, false);
					    GuiUtils.showNode(paneLoadingError, true);
					
					    String errorCode = ConvictedReportInquiryErrorCodes.C014_00002.getCode();
					    String[] errorDetails = {"failed to load the fingerprints!"};
					    Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
					});
					
					if(!success)
					{
						GuiUtils.showNode(paneLoadingInProgress, false);
						GuiUtils.showNode(paneLoadingError, true);
					}
				}
			}, throwable ->
			{
			    GuiUtils.showNode(paneLoadingInProgress, false);
			    GuiUtils.showNode(paneLoadingError, true);
			
			    String errorCode = ConvictedReportInquiryErrorCodes.C014_00003.getCode();
			    String[] errorDetails = {"failed to load the convicted report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, throwable, errorDetails);
			});
			
			if(!success)
			{
				GuiUtils.showNode(paneLoadingInProgress, false);
				GuiUtils.showNode(paneLoadingError, true);
			}
			
			// workaround to center buttons and remove extra spaces
			ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
			buttonBar.getButtons().setAll(buttonPane);
			HBox hBox = (HBox) buttonBar.lookup(".container");
			hBox.setPadding(new Insets(0.0, 10.0, 10.0, 10.0));
			hBox.getChildren().remove(0);
			
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.sizeToScene();
			stage.setMinWidth(stage.getWidth());
			stage.setMinHeight(stage.getHeight());
		});
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
		
		btnPrintReport.setDisable(false);
		btnSaveReportAsPDF.setDisable(false);
		GuiUtils.showNode(paneLoadingInProgress, false);
		GuiUtils.showNode(paneReport, true);
	}
	
	void setReportNumber(Long reportNumber)
	{
		this.reportNumber = reportNumber;
	}
	
	void show()
	{
		dialog.show();
	}
	
	@FXML
	private void onPrintReportButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(btnPrintReport, false);
		GuiUtils.showNode(btnSaveReportAsPDF, false);
		GuiUtils.showNode(piPrinting, true);
		
		if(jasperPrint.get() == null)
		{
			BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
			                                                                                 fingerprintBase64Images);
			buildConvictedReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildConvictedReportTask.getValue();
			    jasperPrint.set(value);
			    printConvictedReport(value);
			});
			buildConvictedReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piPrinting, false);
			    GuiUtils.showNode(btnPrintReport, true);
			    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			    Throwable exception = buildConvictedReportTask.getException();
			
			    String errorCode = ConvictedReportInquiryErrorCodes.C014_00004.getCode();
			    String[] errorDetails = {"failed while building the convicted report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildConvictedReportTask);
		}
		else printConvictedReport(jasperPrint.get());
	}
	
	@FXML
	private void onSaveReportAsPdfButtonClicked(ActionEvent actionEvent)
	{
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			GuiUtils.showNode(btnPrintReport, false);
			GuiUtils.showNode(btnSaveReportAsPDF, false);
			GuiUtils.showNode(piPrinting, true);
			
			if(jasperPrint.get() == null)
			{
				BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
			                                                                                 fingerprintBase64Images);
				buildConvictedReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildConvictedReportTask.getValue();
				    jasperPrint.set(value);
				    try
				    {
				        saveConvictedReportAsPDF(value, selectedFile);
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piPrinting, false);
				        GuiUtils.showNode(btnPrintReport, true);
				        GuiUtils.showNode(btnSaveReportAsPDF, true);
				
				        String errorCode = ConvictedReportInquiryErrorCodes.C014_00005.getCode();
				        String[] errorDetails = {"failed while saving the convicted report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildConvictedReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piPrinting, false);
				    GuiUtils.showNode(btnPrintReport, true);
				    GuiUtils.showNode(btnSaveReportAsPDF, true);
				
				    Throwable exception = buildConvictedReportTask.getException();
				
				    String errorCode = ConvictedReportInquiryErrorCodes.C014_00006.getCode();
				    String[] errorDetails = {"failed while building the convicted report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildConvictedReportTask);
			}
			else
			{
				try
				{
					saveConvictedReportAsPDF(jasperPrint.get(), selectedFile);
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piPrinting, false);
					GuiUtils.showNode(btnPrintReport, true);
					GuiUtils.showNode(btnSaveReportAsPDF, true);
					
					String errorCode = ConvictedReportInquiryErrorCodes.C014_00007.getCode();
					String[] errorDetails = {"failed while saving the convicted report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void printConvictedReport(JasperPrint jasperPrint)
	{
		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		
		    String errorCode = ConvictedReportInquiryErrorCodes.C014_00008.getCode();
		    String[] errorDetails = {"failed while printing the convicted report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveConvictedReportAsPDF(JasperPrint jasperPrint, File selectedFile) throws FileNotFoundException
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint,
		                                                                       new FileOutputStream(selectedFile));
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			String title = resources.getString("printConvictedPresent.savingAsPDF.success.title");
			String contentText = resources.getString("printConvictedPresent.savingAsPDF.success.message");
			String buttonText = resources.getString("printConvictedPresent.savingAsPDF.success.button");
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			
			try
			{
				Desktop.getDesktop().open(selectedFile);
			}
			catch(Exception e)
			{
				LOGGER.warning("Failed to open the PDF file (" + selectedFile.getAbsolutePath() + ")!");
			}
			
			DialogUtils.showInformationDialog(stage, Context.getCoreFxController(), title, null, contentText,
			                                  buttonText, rtl);
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piPrinting, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = ConvictedReportInquiryErrorCodes.C014_00009.getCode();
		    String[] errorDetails = {"failed while saving the convicted report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}