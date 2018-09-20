package sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperPrint;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.core.workflow.Input;
import sa.gov.nic.bio.bw.client.features.commons.beans.GenderType;
import sa.gov.nic.bio.bw.client.features.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.DocumentTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.lookups.SamisIdTypesLookup;
import sa.gov.nic.bio.bw.client.features.commons.tasks.PrintReportTask;
import sa.gov.nic.bio.bw.client.features.commons.tasks.SaveReportAsPdfTask;
import sa.gov.nic.bio.bw.client.features.commons.ui.ImageViewPane;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CountryBean;
import sa.gov.nic.bio.bw.client.features.commons.webservice.DocumentType;
import sa.gov.nic.bio.bw.client.features.commons.webservice.Name;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonIdInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.client.features.commons.webservice.SamisIdType;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.beans.DeadPersonRecordReport;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.tasks.BuildDeadPersonRecordReportTask;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.utils.PrintDeadPersonRecordPresentErrorCodes;
import sa.gov.nic.bio.bw.client.features.printdeadpersonrecord.webservice.DeadPersonRecord;
import sa.gov.nic.bio.bw.client.login.webservice.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ShowRecordPaneFxController extends WizardStepFxControllerBase
{
	@Input(required = true) private Long recordId;
	@Input(required = true) private DeadPersonRecord deadPersonRecord;
	@Input private PersonInfo personInfo;
	@Input private Map<Integer, String> fingerprintImages;
	
	@FXML private VBox paneImage;
	@FXML private ImageViewPane paneImageView;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
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
	@FXML private Label lblEnrollerId;
	@FXML private Label lblEnrollmentTime;
	@FXML private Label lblRecordId;
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
	@FXML private Button btnPrintRecord;
	@FXML private Button btnSaveRecordAsPDF;
	
	private DeadPersonRecordReport deadPersonRecordReport;
	private AtomicReference<JasperPrint> jasperPrint = new AtomicReference<>();
	private FileChooser fileChooser = new FileChooser();
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("../fxml/showRecord.fxml");
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
			populateData(recordId, deadPersonRecord, personInfo, fingerprintImages);
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
			BuildDeadPersonRecordReportTask buildDeadPersonRecordReportTask =
														new BuildDeadPersonRecordReportTask(deadPersonRecordReport);
			
			buildDeadPersonRecordReportTask.setOnSucceeded(event ->
			{
			    JasperPrint value = buildDeadPersonRecordReportTask.getValue();
			    jasperPrint.set(value);
			    printDeadPersonRecordReport(value);
			});
			buildDeadPersonRecordReportTask.setOnFailed(event ->
			{
			    GuiUtils.showNode(piProgress, false);
			    GuiUtils.showNode(btnStartOver, true);
			    GuiUtils.showNode(btnPrintRecord, true);
			    GuiUtils.showNode(btnSaveRecordAsPDF, true);
			
			    Throwable exception = buildDeadPersonRecordReportTask.getException();
			
			    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00003.getCode();
			    String[] errorDetails = {"failed while building the dead person record report!"};
			    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
			});
			Context.getExecutorService().submit(buildDeadPersonRecordReportTask);
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
				BuildDeadPersonRecordReportTask buildDeadPersonRecordReportTask =
														new BuildDeadPersonRecordReportTask(deadPersonRecordReport);
				buildDeadPersonRecordReportTask.setOnSucceeded(event ->
				{
				    JasperPrint value = buildDeadPersonRecordReportTask.getValue();
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
				
				        String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00004.getCode();
				        String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
				        Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				    }
				});
				buildDeadPersonRecordReportTask.setOnFailed(event ->
				{
				    GuiUtils.showNode(piProgress, false);
				    GuiUtils.showNode(btnStartOver, true);
				    GuiUtils.showNode(btnPrintRecord, true);
				    GuiUtils.showNode(btnSaveRecordAsPDF, true);
				
				    Throwable exception = buildDeadPersonRecordReportTask.getException();
				
				    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00005.getCode();
				    String[] errorDetails = {"failed while building the dead person record report!"};
				    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
				});
				Context.getExecutorService().submit(buildDeadPersonRecordReportTask);
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
					
					String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00006.getCode();
					String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
					Context.getCoreFxController().showErrorDialog(errorCode, e, errorDetails);
				}
			}
		}
	}
	
	private void populateData(Long recordIdLong, DeadPersonRecord deadPersonRecord,
	                          PersonInfo personInfo, Map<Integer, String> fingerprintsImages)
	{
		String samisId = null;
		String samisIdType = null;
		String recordId = null;
		String enrollerId = null;
		String enrollmentTime = null;
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
		String documentId = null;
		String documentType = null;
		String documentIssuanceDate = null;
		String documentExpiryDate = null;
		
		if(recordIdLong != null)
		{
			recordId = AppUtils.localizeNumbers(String.valueOf(recordIdLong));
			lblRecordId.setText(recordId);
		}
		
		Long samisIdLong = deadPersonRecord.getSamisId();
		if(samisIdLong != null)
		{
			samisId = AppUtils.localizeNumbers(String.valueOf(samisIdLong));
			lblSamisId.setText(samisId);
		}
		
		UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
		String inquirerId = AppUtils.localizeNumbers(String.valueOf(userInfo.getOperatorId()));
		
		if(deadPersonRecord != null)
		{
			String sEnrollerId = deadPersonRecord.getOperatorId();
			if(sEnrollerId != null && !sEnrollerId.trim().isEmpty())
			{
				enrollerId = AppUtils.localizeNumbers(sEnrollerId);
				lblEnrollerId.setText(enrollerId);
			}
			
			Long enrollmentTimeLong = deadPersonRecord.getEnrollmentDate();
			if(enrollmentTimeLong != null)
			{
				enrollmentTime = AppUtils.formatHijriGregorianDateTime(enrollmentTimeLong * 1000);
				lblEnrollmentTime.setText(enrollmentTime);
			}
			
			String face = deadPersonRecord.getSubjFace();
			if(face != null)
			{
				faceBase64 = face;
				byte[] bytes = Base64.getDecoder().decode(faceBase64);
				ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
				GuiUtils.attachImageDialog(Context.getCoreFxController(), ivPersonPhoto,
				                           resources.getString("label.deadPersonPhoto"),
				                           resources.getString("label.contextMenu.showImage"), false);
			}
		}
		
		if(personInfo != null)
		{
			Name name = personInfo.getName();
			if(name != null)
			{
				String sFirstName = name.getFirstName();
				if(sFirstName != null && !sFirstName.trim().isEmpty() && !sFirstName.trim().equals("-"))
				{
					firstName = sFirstName;
					lblFirstName.setText(firstName);
				}
				
				String sFatherName = name.getFatherName();
				if(sFatherName != null && !sFatherName.trim().isEmpty() && !sFatherName.trim().equals("-"))
				{
					fatherName = sFatherName;
					lblFatherName.setText(fatherName);
				}
				
				String sGrandfatherName = name.getGrandfatherName();
				if(sGrandfatherName != null && !sGrandfatherName.trim().isEmpty()
																				&& !sGrandfatherName.trim().equals("-"))
				{
					grandfatherName = sGrandfatherName;
					lblGrandfatherName.setText(grandfatherName);
				}
				
				String sFamilyName = name.getFamilyName();
				if(sFamilyName != null && !sFamilyName.trim().isEmpty() && !sFamilyName.trim().equals("-"))
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
															Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
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
			
			samisIdLong = personInfo.getSamisId();
			if(samisIdLong != null)
			{
				samisId = AppUtils.localizeNumbers(String.valueOf(samisIdLong));
				lblSamisId.setText(samisId);
			}
			
			@SuppressWarnings("unchecked")
			List<SamisIdType> samisIdTypes = (List<SamisIdType>)
														Context.getUserSession().getAttribute(SamisIdTypesLookup.KEY);
			
			String personType = personInfo.getPersonType();
			if(personType != null)
			{
				SamisIdType theSamisIdType = null;
				
				for(SamisIdType type : samisIdTypes)
				{
					if(personType.equals(type.getIfrPersonType()))
					{
						theSamisIdType = type;
						break;
					}
				}
				
				if(theSamisIdType != null)
				{
					boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
					samisIdType = AppUtils.localizeNumbers(arabic ? theSamisIdType.getDescriptionAR() :
							                                        theSamisIdType.getDescriptionEN());
					lblSamisIdType.setText(samisIdType);
				}
			}
			
			PersonIdInfo identityInfo = personInfo.getIdentityInfo();
			if(identityInfo != null)
			{
				String sOccupation = identityInfo.getOccupation();
				if(sOccupation != null && !sOccupation.trim().isEmpty())
				{
					occupation = AppUtils.localizeNumbers(sOccupation);
					lblOccupation.setText(occupation);
				}
				
				String sDocumentId = identityInfo.getIdNumber();
				if(sDocumentId != null && !sDocumentId.trim().isEmpty())
				{
					documentId = AppUtils.localizeNumbers(sDocumentId);
					lblDocumentId.setText(documentId);
				}
				
				@SuppressWarnings("unchecked")
				List<DocumentType> documentTypes = (List<DocumentType>)
														Context.getUserSession().getAttribute(DocumentTypesLookup.KEY);
				
				Integer documentTypeInteger = identityInfo.getIdType();
				if(documentTypeInteger != null)
				{
					DocumentType theDocumentType = null;
					
					for(DocumentType type : documentTypes)
					{
						if(type.getCode() == documentTypeInteger)
						{
							theDocumentType = type;
							break;
						}
					}
					
					if(theDocumentType != null)
					{
						documentType = AppUtils.localizeNumbers(theDocumentType.getDesc());
						lblDocumentType.setText(documentType);
					}
				}
				
				Date theIssueDate = identityInfo.getIdIssueDate();
				if(theIssueDate != null && theIssueDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
				{
					LocalDate localDate = theIssueDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
					documentIssuanceDate = AppUtils.formatHijriGregorianDate(
																	AppUtils.gregorianDateToMilliSeconds(localDate));
					lblDocumentIssuanceDate.setText(documentIssuanceDate);
				}
				
				Date theExpiryDate = identityInfo.getIdExpirDate();
				if(theExpiryDate != null && theExpiryDate.getTime() > AppConstants.SAMIS_DB_DATE_NOT_SET_VALUE)
				{
					LocalDate localDate = theExpiryDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate();
					documentExpiryDate = AppUtils.formatHijriGregorianDate(
																	AppUtils.gregorianDateToMilliSeconds(localDate));
					lblDocumentExpiryDate.setText(documentExpiryDate);
				}
			}
			
			String sBirthPlace = personInfo.getBirthPlace();
			if(sBirthPlace != null && !sBirthPlace.trim().isEmpty())
			{
				birthPlace = AppUtils.localizeNumbers(sBirthPlace);
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

		if(fingerprintsImages != null)
		{
			Map<Integer, String> dialogTitleMap = GuiUtils.constructFingerprintDialogTitles(resources);
			Map<Integer, ImageView> imageViewMap = GuiUtils.constructFingerprintImageViewMap(ivRightThumb, ivRightIndex,
			                                                                                 ivRightMiddle, ivRightRing,
			                                                                                 ivRightLittle, ivLeftThumb,
			                                                                                 ivLeftIndex, ivLeftMiddle,
			                                                                                 ivLeftRing, ivLeftLittle);
			
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
		
		deadPersonRecordReport = new DeadPersonRecordReport(recordId, enrollerId, inquirerId, enrollmentTime,
		                                                    faceBase64, firstName, fatherName, grandfatherName,
		                                                    familyName, gender, nationality, occupation, birthPlace,
		                                                    birthDate, samisId, samisIdType, documentId, documentType,
		                                                    documentIssuanceDate, documentExpiryDate,
		                                                    fingerprintsImages);
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
		
		    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00007.getCode();
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
		
		    showSuccessNotification(resources.getString("printDeadPersonRecord.savingAsPDF.success.message"));
		});
		printReportTaskAsPdfTask.setOnFailed(event ->
		{
		    GuiUtils.showNode(piProgress, false);
		    GuiUtils.showNode(btnStartOver, true);
		    GuiUtils.showNode(btnPrintRecord, true);
		    GuiUtils.showNode(btnSaveRecordAsPDF, true);
		
		    Throwable exception = printReportTaskAsPdfTask.getException();
		
		    String errorCode = PrintDeadPersonRecordPresentErrorCodes.C012_00008.getCode();
		    String[] errorDetails = {"failed while saving the dead person record report as PDF!"};
		    Context.getCoreFxController().showErrorDialog(errorCode, exception, errorDetails);
		});
		Context.getExecutorService().submit(printReportTaskAsPdfTask);
	}
}