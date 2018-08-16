package sa.gov.nic.bio.bw.client.features.fingerprintcardidentification;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.biokit.FingerPosition;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.InquiryByFingerprintsPaneFxController;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.IdType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.beans.FingerprintCardIdentificationRecordReport;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.tasks.
																	BuildFingerprintCardIdentificationRecordReportTask;
import sa.gov.nic.bio.bw.client.features.fingerprintcardidentification.utils.FingerprintCardIdentificationErrorCodes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_INQUIRY_HIT_SAMIS_ID = "INQUIRY_HIT_SAMIS_ID";
	public static final String KEY_INQUIRY_HIT_RESULT = "INQUIRY_HIT_RESULT";
	public static final String KEY_FINGERPRINTS_IMAGES = "FINGERPRINTS_IMAGES";
	
	@FXML private VBox paneNoHitMessage;
	@FXML private ScrollPane paneResult;
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
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblIdNumber;
	@FXML private Label lblIdIssuanceDate;
	@FXML private Label lblIdType;
	@FXML private Label lblIdExpiry;
	@FXML private Label lblOccupation;
	@FXML private ProgressIndicator piProgress;
	@FXML private Button btnStartOver;
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;
	
	private FingerprintCardIdentificationRecordReport fingerprintCardIdentificationRecordReport;
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiryResult.fxml");
	}
	
	@Override
	protected void onAttachedToScene()
	{
		fileChooser.setTitle(resources.getString("fileChooser.saveRecordAsPDF.title"));
		FileChooser.ExtensionFilter extFilterPDF = new FileChooser.ExtensionFilter(
									resources.getString("fileChooser.saveRecordAsPDF.types"), "*.pdf");
		fileChooser.getExtensionFilters().addAll(extFilterPDF);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Boolean fingerprintHit = (Boolean)
					uiInputData.get(InquiryByFingerprintsPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT);
			
			GuiUtils.showNode(paneResult, fingerprintHit);
			GuiUtils.showNode(btnPrintRecord, fingerprintHit);
			GuiUtils.showNode(btnSaveRecordAsPDF, fingerprintHit);
			GuiUtils.showNode(paneNoHitMessage, !fingerprintHit);
			
			if(fingerprintHit)
			{
				Long samisId = (Long) uiInputData.get(KEY_INQUIRY_HIT_SAMIS_ID);
				PersonInfo personInfo = (PersonInfo) uiInputData.get(KEY_INQUIRY_HIT_RESULT);
				
				@SuppressWarnings("unchecked")
				Map<Integer, String> fingerprintImages =
													(Map<Integer, String>) uiInputData.get(KEY_FINGERPRINTS_IMAGES);
				
				populateData(samisId, personInfo, fingerprintImages);
				GuiUtils.showNode(paneResult, true);
			}
		}
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		startOver();
	}
	
	@FXML
	private void onPrintRecordButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		GuiUtils.showNode(btnStartOver, false);
		GuiUtils.showNode(btnPrintRecord, false);
		GuiUtils.showNode(btnSaveRecordAsPDF, false);
		GuiUtils.showNode(piProgress, true);
		
		if(jasperPrint.get() == null)
		{
			BuildFingerprintCardIdentificationRecordReportTask buildFingerprintCardIdentificationRecordReportTask =
					new BuildFingerprintCardIdentificationRecordReportTask(fingerprintCardIdentificationRecordReport);
			
			buildFingerprintCardIdentificationRecordReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildFingerprintCardIdentificationRecordReportTask.getValue();
			    jasperPrint.set(value);
			    printDeadPersonRecordReport(value);
			});
			buildFingerprintCardIdentificationRecordReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintRecord, true);
			    GuiUtils.showNode(btnSaveRecordAsPDF, true);
			
			    Throwable exception = buildFingerprintCardIdentificationRecordReportTask.getException();
			
			    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00004.getCode();
			    String[] errorDetails = {"failed while building the fingerprint card identification report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildFingerprintCardIdentificationRecordReportTask);
		}
		else printDeadPersonRecordReport(jasperPrint.get());
	}
	
	@FXML
	private void onSaveRecordAsPdfButtonClicked(ActionEvent actionEvent)
	{
		hideNotification();
		File selectedFile = fileChooser.showSaveDialog(Context.getCoreFxController().getStage());
		
		if(selectedFile != null)
		{
			GuiUtils.showNode(btnStartOver, false);
			GuiUtils.showNode(btnPrintRecord, false);
			GuiUtils.showNode(btnSaveRecordAsPDF, false);
			GuiUtils.showNode(piProgress, true);
			
			if(jasperPrint.get() == null)
			{
				BuildFingerprintCardIdentificationRecordReportTask buildFingerprintCardIdentificationRecordReportTask =
					new BuildFingerprintCardIdentificationRecordReportTask(fingerprintCardIdentificationRecordReport);
				buildFingerprintCardIdentificationRecordReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildFingerprintCardIdentificationRecordReportTask.getValue();
				    jasperPrint.set(value);
				    try
				    {
				        saveDeadPersonRecordReportAsPDF(value, new FileOutputStream(selectedFile));
				    }
				    catch(Exception e)
				    {
				        GuiUtils.showNode(piProgress, false);
				        GuiUtils.showNode(btnStartOver, true);
				        GuiUtils.showNode(btnPrintRecord, true);
				        GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				        String errorCode = FingerprintCardIdentificationErrorCodes.C013_00005.getCode();
				        String[] errorDetails =
						                    {"failed while saving the fingerprint card identification report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildFingerprintCardIdentificationRecordReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnStartOver, true);
				    GuiUtils.showNode(btnPrintRecord, true);
				    GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				    Throwable exception = buildFingerprintCardIdentificationRecordReportTask.getException();
				
				    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00006.getCode();
				    String[] errorDetails = {"failed while building the fingerprint card identification report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildFingerprintCardIdentificationRecordReportTask);
			}
			else
			{
				try
				{
					saveDeadPersonRecordReportAsPDF(jasperPrint.get(), new FileOutputStream(selectedFile));
				}
				catch(Exception e)
				{
					GuiUtils.showNode(piProgress, false);
					GuiUtils.showNode(btnStartOver, true);
					GuiUtils.showNode(btnPrintRecord, true);
					GuiUtils.showNode(btnSaveRecordAsPDF, true);
					
					String errorCode = FingerprintCardIdentificationErrorCodes.C013_00007.getCode();
					String[] errorDetails = {"failed while saving the fingerprint card identification report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void populateData(Long samisId, PersonInfo personInfo, Map<Integer, String> fingerprintsImages)
	{
		String faceBase64 = null;
		String firstName = null;
		String fatherName = null;
		String grandfatherName = null;
		String familyName = null;
		String gender = null;
		String nationality = null;
		String occupation = null;
		String birthPlace = null;
		String birthDate = null;
		String idNumber = null;
		String idType = null;
		String idIssuanceDate = null;
		String idExpirationDate = null;
		
		if(personInfo != null)
		{
			String face = personInfo.getFace();
			if(face != null)
			{
				faceBase64 = face;
				byte[] bytes = Base64.getDecoder().decode(faceBase64);
				ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
				                           resources.getString("label.personPhoto"),
				                           resources.getString("label.contextMenu.showImage"), false);
			}
			
			Name name = personInfo.getName();
			if(name != null)
			{
				String sFirstName = name.getFirstName();
				if(sFirstName != null && !sFirstName.trim().isEmpty())
				{
					firstName = sFirstName;
					lblFirstName.setText(firstName);
				}
				
				String sFatherName = name.getFatherName();
				if(sFatherName != null && !sFatherName.trim().isEmpty())
				{
					fatherName = sFatherName;
					lblFatherName.setText(fatherName);
				}
				
				String sGrandfatherName = name.getGrandfatherName();
				if(sGrandfatherName != null && !sGrandfatherName.trim().isEmpty())
				{
					grandfatherName = sGrandfatherName;
					lblGrandfatherName.setText(grandfatherName);
				}
				
				String sFamilyName = name.getFamilyName();
				if(sFamilyName != null && !sFamilyName.trim().isEmpty())
				{
					familyName = sFamilyName;
					lblFamilyName.setText(familyName);
				}
			}
			
			GenderType genderType = GenderType.values()[personInfo.getGender() - 1];
			switch(genderType)
			{
				case MALE: gender = resources.getString("label.male"); break;
				case FEMALE: gender = resources.getString("label.female"); break;
			}
			lblGender.setText(gender);
			
			@SuppressWarnings("unchecked") List<CountryBean> countries = (List<CountryBean>)
													Context.getUserSession().getAttribute("lookups.countries");
			
			CountryBean countryBean = null;
			
			for(CountryBean country : countries)
			{
				if(country.getCode() == personInfo.getNationality())
				{
					countryBean = country;
					break;
				}
			}
			
			if(countryBean != null)
			{
				boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
				nationality = arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN();
				lblNationality.setText(nationality);
			}
			
			PersonIdInfo identityInfo = personInfo.getIdentityInfo();
			if(identityInfo != null)
			{
				String sOccupation = identityInfo.getOccupation();
				if(sOccupation != null && !sOccupation.trim().isEmpty())
				{
					occupation = sOccupation;
					lblOccupation.setText(occupation);
				}
				
				String sIdNumber = identityInfo.getIdNumber();
				if(sIdNumber != null && !sIdNumber.trim().isEmpty())
				{
					idNumber = AppUtils.replaceNumbersOnly(sIdNumber, Locale.getDefault());
					lblIdNumber.setText(idNumber);
				}
				
				@SuppressWarnings("unchecked") List<IdType> idTypes = (List<IdType>)
						Context.getUserSession().getAttribute("lookups.idTypes");
				
				Integer idTypeInteger = identityInfo.getIdType();
				if(idTypeInteger != null)
				{
					IdType theIdType = null;
					
					for(IdType type : idTypes)
					{
						if(type.getCode() == idTypeInteger)
						{
							theIdType = type;
							break;
						}
					}
					
					if(theIdType != null)
					{
						idType = AppUtils.replaceNumbersOnly(theIdType.getDesc(), Locale.getDefault());
						lblIdType.setText(idType);
					}
				}
				
				Date theIssueDate = identityInfo.getIdIssueDate();
				if(theIssueDate != null && theIssueDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
				{
					LocalDate localDate = theIssueDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
					idIssuanceDate = AppUtils.formatHijriGregorianDate(AppUtils.gregorianDateToMilliSeconds(localDate));
					lblIdIssuanceDate.setText(idIssuanceDate);
				}
				
				Date theExpirationDate = identityInfo.getIdExpirDate();
				if(theExpirationDate != null &&
						theExpirationDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
				{
					LocalDate localDate = theExpirationDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
					idExpirationDate = AppUtils.formatHijriGregorianDate(
							AppUtils.gregorianDateToMilliSeconds(localDate));
					lblIdExpiry.setText(idExpirationDate);
				}
			}
			
			String sBirthPlace = personInfo.getBirthPlace();
			if(sBirthPlace != null && !sBirthPlace.trim().isEmpty())
			{
				birthPlace = sBirthPlace;
				lblBirthPlace.setText(birthPlace);
			}
			
			Date theBirthDate = personInfo.getBirthDate();
			if(theBirthDate != null && theBirthDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
			{
				LocalDate localDate = theBirthDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
				birthDate = AppUtils.formatHijriGregorianDate(AppUtils.gregorianDateToMilliSeconds(localDate));
				lblBirthDate.setText(birthDate);
			}
		}
		else idNumber = String.valueOf(samisId);
		
		if(fingerprintsImages != null)
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
			
			fingerprintsImages.forEach((position, fingerprintImage) ->
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
		
		fingerprintCardIdentificationRecordReport = new FingerprintCardIdentificationRecordReport(faceBase64, firstName,
                                                          fatherName, grandfatherName, familyName, gender,
                                                          nationality, occupation, birthPlace, birthDate, idNumber,
                                                          idType, idIssuanceDate, idExpirationDate, fingerprintsImages);
	}
	
	private void printDeadPersonRecordReport(JasperPrint jasperPrint)
	{
		PrintReportTask printReportTask = new PrintReportTask(jasperPrint);
		printReportTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		});
		printReportTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTask.getException();
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00008.getCode();
		    String[] errorDetails = {"failed while printing the dead person record report!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTask);
	}
	
	private void saveDeadPersonRecordReportAsPDF(JasperPrint jasperPrint, OutputStream pdfOutputStream)
	{
		SaveReportAsPdfTask printReportTaskAsPdfTask = new SaveReportAsPdfTask(jasperPrint, pdfOutputStream);
		printReportTaskAsPdfTask.setOnSucceeded(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    showSuccessNotification(resources.getString("message.savingAsPDF.success"));
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = FingerprintCardIdentificationErrorCodes.C013_00009.getCode();
		    String[] errorDetails = {"failed while saving the fingerprint card identification report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}