package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.UserSession;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.GenderType;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Name;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.PersonInfo;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InquiryResultPaneFxController extends WizardStepFxControllerBase
{
	@FXML private VBox paneNoHitMessage;
	@FXML private GridPane gridPane;
	@FXML private ImageView ivPersonPhoto;
	@FXML private Label lblPublicFileNumber;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOccupation;
	@FXML private Label lblBirthPlace;
	@FXML private Label lblBirthDate;
	@FXML private Label lblIdNumber;
	@FXML private Label lblIdType;
	@FXML private Label lblIdIssuanceDate;
	@FXML private Label lblIdExpiry;
	@FXML private Button btnStartOver;
	@FXML private Button btnRegisterUnknownPerson;
	@FXML private Button btnConfirmPersonInformation;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/inquiryResult.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnRegisterUnknownPerson);
		GuiUtils.makeButtonClickableByPressingEnter(btnConfirmPersonInformation);
		
		btnConfirmPersonInformation.setOnAction(actionEvent -> goNext());
		btnStartOver.setOnAction(actionEvent -> startOver());
	}
	
	@Override
	protected void onAttachedToScene()
	{
		ivPersonPhoto.fitWidthProperty().bind(Context.getCoreFxController().getBodyPane().widthProperty()
				                                                                         .divide(2));
		ivPersonPhoto.fitHeightProperty().bind(gridPane.heightProperty().subtract(5.0));
		Context.getCoreFxController().getBodyPane().autosize();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Boolean hit = (Boolean) uiInputData.get(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT);
			
			if(hit != null && hit)
			{
				GuiUtils.showNode(paneNoHitMessage, false);
				GuiUtils.showNode(ivPersonPhoto, true);
				GuiUtils.showNode(gridPane, true);
				GuiUtils.showNode(btnConfirmPersonInformation, true);
				
				FingerprintInquiryStatusResult result = (FingerprintInquiryStatusResult)
										uiInputData.get(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT_RESULT);
				PersonInfo personInfo = result.getPersonInfo().values().iterator().next();
				String faceImageBase64 = personInfo.getFace();
				
				if(faceImageBase64 != null)
				{
					byte[] bytes = Base64.getDecoder().decode(faceImageBase64);
					ivPersonPhoto.setImage(new Image(new ByteArrayInputStream(bytes)));
				}
				
				String notAvailable = resources.getString("label.notAvailable");
				
				if(result.getXCandidate() > 0) lblPublicFileNumber.setText(String.valueOf(result.getXCandidate()));
				else
				{
					lblPublicFileNumber.setText(notAvailable);
					lblPublicFileNumber.setTextFill(Color.RED);
				}
				
				Name name = personInfo.getName();
				
				String firstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(),
				                                          true);
				String fatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(),
				                                           true);
				String grandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(),
				                                                name.getTranslatedGrandFatherName(),
				                                                true);
				String familyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(),
				                                           true);
				
				if(firstName != null) lblFirstName.setText(firstName);
				else
				{
					lblFirstName.setText(notAvailable);
					lblFirstName.setTextFill(Color.RED);
				}
				
				if(fatherName != null) lblFatherName.setText(fatherName);
				else
				{
					lblFatherName.setText(notAvailable);
					lblFatherName.setTextFill(Color.RED);
				}
				
				if(grandfatherName != null) lblGrandfatherName.setText(grandfatherName);
				else
				{
					lblGrandfatherName.setText(notAvailable);
					lblGrandfatherName.setTextFill(Color.RED);
				}
				
				if(familyName != null) lblFamilyName.setText(familyName);
				else
				{
					lblFamilyName.setText(notAvailable);
					lblFamilyName.setTextFill(Color.RED);
				}
				
				GenderType gender = GenderType.values()[personInfo.getGender()];
				if(gender != null)
				{
					switch(gender)
					{
						case MALE: lblGender.setText(resources.getString("label.male")); break;
						case FEMALE: lblGender.setText(resources.getString("label.female")); break;
					}
				}
				else
				{
					lblGender.setText(notAvailable);
					lblGender.setTextFill(Color.RED);
				}
				
				UserSession userSession = Context.getUserSession();
				
				@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
						userSession.getAttribute("lookups.nationalities");
				
				NationalityBean nationalityBean = null;
				
				for(NationalityBean nationality : nationalities)
				{
					if(nationality.getCode() == personInfo.getNationality())
					{
						nationalityBean = nationality;
						break;
					}
				}
				
				if(nationalityBean != null)
				{
					boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
					lblNationality.setText(rtl ? nationalityBean.getDescriptionAR() :
					                             nationalityBean.getDescriptionEN());
				}
				else
				{
					lblNationality.setText(notAvailable);
					lblNationality.setTextFill(Color.RED);
				}
				
				// TODO:
				lblOccupation.setText(notAvailable);
				lblOccupation.setTextFill(Color.RED);
				
				String birthPlace = personInfo.getBirthPlace();
				if(birthPlace != null && !birthPlace.trim().isEmpty()) lblBirthPlace.setText(birthPlace);
				else
				{
					lblBirthPlace.setText(notAvailable);
					lblBirthPlace.setTextFill(Color.RED);
				}
				
				Date birthDate = personInfo.getBirthDate();
				
				if(birthDate != null) lblBirthDate.setText(AppUtils.formatDate(
						birthDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate()));
				else
				{
					lblBirthDate.setText(notAvailable);
					lblBirthDate.setTextFill(Color.RED);
				}
				
				long samisId = personInfo.getSamisId();
				
				if(samisId > 0) lblIdNumber.setText(AppUtils.replaceNumbersOnly(String.valueOf(samisId),
				                                                                Locale.getDefault()));
				else
				{
					lblIdNumber.setText(notAvailable);
					lblIdNumber.setTextFill(Color.RED);
				}
				
				String personType = personInfo.getPersonType();
				if(personType != null && !personType.trim().isEmpty()) lblIdType.setText(personType);
				else
				{
					lblIdType.setText(notAvailable);
					lblIdType.setTextFill(Color.RED);
				}
				
				// TODO:
				lblIdIssuanceDate.setText(notAvailable);
				lblIdIssuanceDate.setTextFill(Color.RED);
				
				// TODO:
				lblIdExpiry.setText(notAvailable);
				lblIdExpiry.setTextFill(Color.RED);
				
				Platform.runLater(btnConfirmPersonInformation::requestFocus);
			}
			else
			{
				GuiUtils.showNode(ivPersonPhoto, false);
				GuiUtils.showNode(gridPane, false);
				GuiUtils.showNode(btnConfirmPersonInformation, false);
				GuiUtils.showNode(paneNoHitMessage, true);
			}
		}
	}
	
	@FXML
	private void onRegisterUnknownPersonButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("printConvictedPresent.RegisterUnknown.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.RegisterUnknown.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) goNext();
	}
}