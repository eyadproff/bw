package sa.gov.nic.bio.bw.client.features.convictedreportinquiry;

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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.FxControllerBase;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.DialogUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Finger;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.features.convictedreportinquiry.utils.ConvictedReportInquiryErrorCodes;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.tasks.BuildConvictedReportTask;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.ConvictedReport;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.CrimeCode;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.JudgementInfo;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ShowReportDialogFxController extends FxControllerBase
{
	@FXML private VBox paneImage;
	@FXML private HBox buttonPane;
	@FXML private Label lblReportNumber;
	@FXML private Label lblEnrollerId;
	@FXML private Label lblEnrollmentTime;
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
	@FXML private Label lblSamisId;
	@FXML private Label lblSamisIdType;
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
	@FXML private Button btnPrintReport;
	@FXML private Button btnSaveReportAsPDF;
	@FXML private Dialog<ButtonType> dialog;
	
	private Map<Integer, String> crimeEventTitles = new HashMap<>();
	private Map<Integer, String> crimeClassTitles = new HashMap<>();
	private FileChooser fileChooser = new FileChooser();
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	
	private ConvictedReport convictedReport;
	private Map<Integer, String> fingerprintImages;
	
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
		
		dialog.setOnShown(event ->
		{
			populateData();
			
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
		long reportNumber = convictedReport.getReportNumber();
		lblReportNumber.setText(AppUtils.localizeNumbers(String.valueOf(reportNumber)));
		
		String sEnrollerId = convictedReport.getOperatorId();
		if(sEnrollerId != null && !sEnrollerId.trim().isEmpty())
		{
			lblEnrollerId.setText(AppUtils.localizeNumbers(sEnrollerId));
		}
		
		long enrollmentTimeLong = convictedReport.getReportDate();
		lblEnrollmentTime.setText(AppUtils.formatHijriGregorianDateTime(enrollmentTimeLong * 1000));
		
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
			
			String maleSeeFemaleRole = Context.getConfigManager().getProperty("face.roles.maleSeeFemale");
			boolean authorized = userRoles.contains(maleSeeFemaleRole);
			if(!authorized && blur) ivPersonPhoto.setEffect(new GaussianBlur(radius));
		}
		
		lblFirstName.setText(convictedReport.getSubjtName().getFirstName());
		lblFatherName.setText(convictedReport.getSubjtName().getFatherName());
		lblGrandfatherName.setText(convictedReport.getSubjtName().getGrandfatherName());
		lblFamilyName.setText(convictedReport.getSubjtName().getFamilyName());
		
		Long biometricsId = convictedReport.getSubjBioId();
		if(biometricsId != null) lblBiometricsId.setText(AppUtils.localizeNumbers(String.valueOf(biometricsId)));
		
		Long generalFileNumber = convictedReport.getGeneralFileNum();
		if(generalFileNumber != null)
						lblGeneralFileNumber.setText(AppUtils.localizeNumbers(String.valueOf(generalFileNumber)));
		
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
			lblNationality.setText(arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN());
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
		
		Long samisId = convictedReport.getSubjSamisId();
		if(samisId != null) lblSamisId.setText(AppUtils.localizeNumbers(String.valueOf(samisId)));
		
		@SuppressWarnings("unchecked") List<SamisIdType> samisIdTypes = (List<SamisIdType>)
													Context.getUserSession().getAttribute("lookups.samisIdTypes");
		
		Integer subjSamisType = convictedReport.getSubjSamisType();
		if(subjSamisType != null)
		{
			SamisIdType samisIdType = null;
			
			for(SamisIdType type : samisIdTypes)
			{
				if(type.getCode() == subjSamisType)
				{
					samisIdType = type;
					break;
				}
			}
			
			if(samisIdType != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				lblSamisIdType.setText(AppUtils.localizeNumbers(arabic ? samisIdType.getDescriptionAR() :
						                                                 samisIdType.getDescriptionEN()));
			}
		}
		
		String subjDocId = convictedReport.getSubjDocId();
		if(subjDocId != null && !subjDocId.trim().isEmpty()) lblDocumentId.setText(AppUtils.localizeNumbers(subjDocId));
		
		@SuppressWarnings("unchecked") List<DocumentType> documentTypes = (List<DocumentType>)
												Context.getUserSession().getAttribute("lookups.documentTypes");
		
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
		
		if(judgementInfo != null)
		{
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
			if(arrestDate != null) lblArrestDate.setText(
													AppUtils.formatHijriGregorianDate(arrestDate * 1000));
			
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
		}
		
		List<Finger> subjFingers = convictedReport.getSubjFingers();
		if(subjFingers != null)
		{
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
				if(fingerprintImage == null)
				{
					return;
				}
				
				ImageView imageView = imageViewMap.get(position);
				String dialogTitle = dialogTitleMap.get(position);
				
				byte[] bytes = Base64.getDecoder().decode(fingerprintImage);
				
				if(bytes == null)
				{
					return;
				}
				
				imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
			    GuiUtils.attachImageDialog(Context.getCoreFxController(), imageView,
			                               dialogTitle, resources.getString("label.contextMenu.showImage"),
			                               false);
			});
		}
	}
	
	public void setConvictedReportWithFingerprintImages(ConvictedReport convictedReport,
	                                                    Map<Integer, String> fingerprintImages)
	{
		this.convictedReport = convictedReport;
		this.fingerprintImages = fingerprintImages;
	}
	
	public void show()
	{
		dialog.show();
	}
	
	@FXML
	private void onPrintReportButtonClicked(ActionEvent actionEvent)
	{
		GuiUtils.showNode(btnPrintReport, false);
		GuiUtils.showNode(btnSaveReportAsPDF, false);
		GuiUtils.showNode(piProgress, true);
		
		if(jasperPrint.get() == null)
		{
			BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
			                                                                                 fingerprintImages);
			buildConvictedReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildConvictedReportTask.getValue();
			    jasperPrint.set(value);
			    printConvictedReport(value);
			});
			buildConvictedReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnPrintReport, true);
			    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			    Throwable exception = buildConvictedReportTask.getException();
			
			    String errorCode = ConvictedReportInquiryErrorCodes.C014_00003.getCode();
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
			GuiUtils.showNode(piProgress, true);
			
			if(jasperPrint.get() == null)
			{
				BuildConvictedReportTask buildConvictedReportTask = new BuildConvictedReportTask(convictedReport,
				                                                                                 fingerprintImages);
				buildConvictedReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildConvictedReportTask.getValue();
				    jasperPrint.set(value);
				    try
				    {
				        saveConvictedReportAsPDF(value, new FileOutputStream(selectedFile));
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piProgress, false);
				        GuiUtils.showNode(btnPrintReport, true);
				        GuiUtils.showNode(btnSaveReportAsPDF, true);
				
				        String errorCode = ConvictedReportInquiryErrorCodes.C014_00004.getCode();
				        String[] errorDetails = {"failed while saving the convicted report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildConvictedReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnPrintReport, true);
				    GuiUtils.showNode(btnSaveReportAsPDF, true);
				
				    Throwable exception = buildConvictedReportTask.getException();
				
				    String errorCode = ConvictedReportInquiryErrorCodes.C014_00005.getCode();
				    String[] errorDetails = {"failed while building the convicted report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildConvictedReportTask);
			}
			else
			{
				try
				{
					saveConvictedReportAsPDF(jasperPrint.get(), new FileOutputStream(selectedFile));
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piProgress, false);
					GuiUtils.showNode(btnPrintReport, true);
					GuiUtils.showNode(btnSaveReportAsPDF, true);
					
					String errorCode = ConvictedReportInquiryErrorCodes.C014_00006.getCode();
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
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		
		    String errorCode = ConvictedReportInquiryErrorCodes.C014_00007.getCode();
		    String[] errorDetails = {"failed while printing the convicted report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveConvictedReportAsPDF(JasperPrint jasperPrint, OutputStream pdfOutputStream)
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint, pdfOutputStream);
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
			
			String title = resources.getString("printConvictedPresent.savingAsPDF.success.title");
			String contentText = resources.getString("printConvictedPresent.savingAsPDF.success.message");
			String buttonText = resources.getString("printConvictedPresent.savingAsPDF.success.button");
			boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			
			DialogUtils.showInformationDialog(stage, Context.getCoreFxController(), title, null, contentText,
			                                  buttonText, rtl);
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnPrintReport, true);
		    GuiUtils.showNode(btnSaveReportAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = ConvictedReportInquiryErrorCodes.C014_00008.getCode();
		    String[] errorDetails = {"failed while saving the convicted report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}