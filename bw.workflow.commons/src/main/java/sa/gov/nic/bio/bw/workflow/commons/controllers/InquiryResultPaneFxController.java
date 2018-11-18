package sa.gov.nic.bio.bw.workflow.commons.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.UserInfo;
import sa.gov.nic.bio.bw.workflow.commons.webservice.NormalizedPersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.webservice.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.tasks.FingerprintInquiryStatusCheckerWorkflowTask.Status;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;

@FxmlFile("inquiryResult.fxml")
public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_INQUIRY_SAMIS_ID = "INQUIRY_SAMIS_ID";
	public static final String KEY_INQUIRY_HIT_RESULT = "INQUIRY_HIT_RESULT";
	
	@Input private Boolean hideRegisterUnknown;
	@Input private Status status;
	@Input private Long personId;
	@Input private Long civilBiometricsId;
	@Input private Long criminalBiometricsId;
	@Input private PersonInfo personInfo;
	@Output private NormalizedPersonInfo normalizedPersonInfo;
	
	@FXML private ScrollPane infoPane;
	@FXML private GridPane gridPane;
	@FXML private VBox paneNoHitMessage;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblCivilBiometricsIdLabel;
	@FXML private Label lblCivilBiometricsId;
	@FXML private Label lblCriminalBiometricsIdLabel;
	@FXML private Label lblCriminalBiometricsId;
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
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	
	@Override
	protected void onAttachedToScene()
	{
		if(status != null) // workflow: inquiry by fingerprints
		{
			if(status == Status.HIT)
			{
				showPersonInfoPane(true);
				populatePersonInfo(personId, civilBiometricsId, criminalBiometricsId, personInfo);
			}
			else showPersonInfoPane(false);
		}
		else // workflow: inquiry by person id
		{
			gridPane.getChildren().remove(lblCivilBiometricsIdLabel);
			gridPane.getChildren().remove(lblCivilBiometricsId);
			gridPane.getChildren().remove(lblCriminalBiometricsIdLabel);
			gridPane.getChildren().remove(lblCriminalBiometricsId);
			gridPane.setPadding(new Insets(0.0, 5.0, 5.0, 5.0));
			
			showPersonInfoPane(true);
			populatePersonInfo(personId, null, null, personInfo);
		}
	}
	
	private void showPersonInfoPane(boolean bShow)
	{
		GuiUtils.showNode(btnRegisterUnknownPerson,
		                  (hideRegisterUnknown != null && !hideRegisterUnknown && !bShow) ||
				                  (hideRegisterUnknown == null && !bShow));
		GuiUtils.showNode(paneNoHitMessage, !bShow);
		GuiUtils.showNode(ivPersonPhoto, bShow);
		GuiUtils.showNode(infoPane, bShow);
		GuiUtils.showNode(btnConfirmPersonInformation, bShow);
	}
	
	@FXML
	private void onRegisterUnknownPersonButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("inquiry.registerUnknown.confirmation.header");
		String contentText = resources.getString("inquiry.registerUnknown.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) goNext();
	}
	
	private void populatePersonInfo(Long personId, Long civilBiometricsId, Long criminalBiometricsId,
	                                PersonInfo personInfo)
	{
		normalizedPersonInfo = new NormalizedPersonInfo(personId, civilBiometricsId, criminalBiometricsId,
		                                                personInfo, resources.getString("label.notAvailable"),
		                                                resources.getString("label.male"),
		                                                resources.getString("label.female"));
		
		String faceImageBase64 = normalizedPersonInfo.getFaceImageBase64();
		Gender gender = normalizedPersonInfo.getGender();
		
		if(faceImageBase64 != null)
		{
			UserInfo userInfo = (UserInfo) Context.getUserSession().getAttribute("userInfo");
			boolean maleOperator = userInfo != null && userInfo.getGender() > 0 &&
								   Gender.values()[userInfo.getGender() - 1] == Gender.MALE;
			boolean femaleSubject = gender == Gender.FEMALE;
			boolean blur = maleOperator && femaleSubject;
			
			byte[] bytes = Base64.getDecoder().decode(faceImageBase64);
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
		
		lblPersonId.setText(normalizedPersonInfo.getPersonIdLabel());
		lblCivilBiometricsId.setText(normalizedPersonInfo.getCivilBiometricsIdLabel());
		lblCriminalBiometricsId.setText(normalizedPersonInfo.getCriminalBiometricsIdLabel());
		lblFirstName.setText(normalizedPersonInfo.getFirstNameLabel());
		lblFatherName.setText(normalizedPersonInfo.getFatherNameLabel());
		lblGrandfatherName.setText(normalizedPersonInfo.getGrandfatherNameLabel());
		lblFamilyName.setText(normalizedPersonInfo.getFamilyNameLabel());
		lblGender.setText(normalizedPersonInfo.getGenderLabel());
		lblNationality.setText(normalizedPersonInfo.getNationalityLabel());
		lblOccupation.setText(normalizedPersonInfo.getOccupationLabel());
		lblBirthPlace.setText(normalizedPersonInfo.getBirthPlaceLabel());
		lblBirthDate.setText(normalizedPersonInfo.getBirthDateLabel());
		lblPersonType.setText(normalizedPersonInfo.getPersonTypeLabel());
		lblDocumentId.setText(normalizedPersonInfo.getDocumentIdLabel());
		lblDocumentType.setText(normalizedPersonInfo.getDocumentTypeLabel());
		lblDocumentIssuanceDate.setText(normalizedPersonInfo.getDocumentIssuanceDateLabel());
		lblDocumentExpiryDate.setText(normalizedPersonInfo.getDocumentExpiryDateLabel());
		
		if(normalizedPersonInfo.getPersonId() == null) lblPersonId.setTextFill(Color.RED);
		if(normalizedPersonInfo.getCivilBiometricsId() == null) lblCivilBiometricsId.setTextFill(Color.RED);
		if(normalizedPersonInfo.getCriminalBiometricsId() == null) lblCriminalBiometricsId.setTextFill(Color.RED);
		if(normalizedPersonInfo.getFirstName() == null) lblFirstName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getFatherName() == null) lblFatherName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getGrandfatherName() == null) lblGrandfatherName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getFamilyName() == null) lblFamilyName.setTextFill(Color.RED);
		if(normalizedPersonInfo.getGender() == null) lblGender.setTextFill(Color.RED);
		if(normalizedPersonInfo.getNationality() == null) lblNationality.setTextFill(Color.RED);
		if(normalizedPersonInfo.getOccupation() == null) lblOccupation.setTextFill(Color.RED);
		if(normalizedPersonInfo.getBirthPlace() == null) lblBirthPlace.setTextFill(Color.RED);
		if(normalizedPersonInfo.getBirthDate() == null) lblBirthDate.setTextFill(Color.RED);
		if(normalizedPersonInfo.getPersonType() == null) lblPersonType.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentId() == null) lblDocumentId.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentType() == null) lblDocumentType.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentIssuanceDate() == null) lblDocumentIssuanceDate.setTextFill(Color.RED);
		if(normalizedPersonInfo.getDocumentExpiryDate() == null) lblDocumentExpiryDate.setTextFill(Color.RED);
		
		infoPane.autosize();
		btnConfirmPersonInformation.requestFocus();
	}
}