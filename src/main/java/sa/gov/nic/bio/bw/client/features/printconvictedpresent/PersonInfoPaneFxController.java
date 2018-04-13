package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
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

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PersonInfoPaneFxController extends WizardStepFxControllerBase
{
	@FXML private TextField txtFirstName;
	@FXML private TextField txtFatherName;
	@FXML private TextField txtGrandfatherName;
	@FXML private TextField txtFamilyName;
	@FXML private TextField txtPublicFileNumber;
	@FXML private TextField txtOccupation;
	@FXML private TextField txtBirthPlace;
	@FXML private TextField txtIdNumber;
	@FXML private TextField txtIdType;
	@FXML private ComboBox<GenderType> cboGender;
	@FXML private ComboBox<NationalityBean> cboNationality;
	@FXML private CheckBox cbBirthDateShowHijri;
	@FXML private CheckBox cbIdIssuanceDateShowHijri;
	@FXML private CheckBox cbIdExpiryDateShowHijri;
	@FXML private DatePicker dpBirthDate;
	@FXML private DatePicker dpIdIssuanceDate;
	@FXML private DatePicker dpIdExpiryDate;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/personInfo.fxml");
	}
	
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnStartOver.setOnAction(actionEvent -> startOver());
		btnNext.setOnAction(actionEvent -> goNext());
	}
	
	@Override
	protected void onAttachedToScene()
	{
	
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			FingerprintInquiryStatusResult result = (FingerprintInquiryStatusResult)
					uiInputData.get(InquiryPaneFxController.KEY_FINGERPRINT_INQUIRY_HIT_RESULT);
			PersonInfo personInfo = result.getPersonInfo().values().iterator().next();
			
			Name name = personInfo.getName();
			
			String firstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(),
			                                          false);
			String fatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(),
			                                           false);
			String grandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(),
			                                                name.getTranslatedGrandFatherName(),
			                                                false);
			String familyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(),
			                                           false);
			
			txtFirstName.setText(firstName);
			txtFatherName.setText(fatherName);
			txtGrandfatherName.setText(grandfatherName);
			txtFamilyName.setText(familyName);
			
			long xCandidate = result.getXCandidate();
			if(xCandidate > 0) txtPublicFileNumber.setText(String.valueOf(xCandidate));
			
			txtBirthPlace.setText(personInfo.getBirthPlace());
			
			long samisId = personInfo.getSamisId();
			if(samisId > 0) txtIdNumber.setText(String.valueOf(samisId));
			
			txtIdType.setText(personInfo.getPersonType());
			
			cboGender.setValue(GenderType.values()[personInfo.getGender()]);
			
			UserSession userSession = Context.getUserSession();
			@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
															userSession.getAttribute("lookups.nationalities");
			
			cboNationality.getItems().setAll(nationalities);
			
			NationalityBean nationalityBean = null;
			
			for(NationalityBean nationality : nationalities)
			{
				if(nationality.getCode() == personInfo.getNationality())
				{
					nationalityBean = nationality;
					break;
				}
			}
			
			cboNationality.setValue(nationalityBean);
			
			Date birthDate = personInfo.getBirthDate();
			if(birthDate != null) dpBirthDate.setValue(
									birthDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate());
		}
	}
}