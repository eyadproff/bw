package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.beans.ItemWithText;
import sa.gov.nic.bio.bw.client.core.utils.AppConstants;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.NationalityBean;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.FingerprintInquiryStatusResult;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.GenderType;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.Name;
import sa.gov.nic.bio.bw.client.features.printconvictedpresent.webservice.PersonInfo;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
	@FXML private ComboBox<ItemWithText<GenderType>> cboGender;
	@FXML private ComboBox<HideableItem<NationalityBean>> cboNationality;
	@FXML private CheckBox cbBirthDateShowHijri;
	@FXML private CheckBox cbIdIssuanceDateShowHijri;
	@FXML private CheckBox cbIdExpiryDateShowHijri;
	@FXML private DatePicker dpBirthDate;
	@FXML private DatePicker dpIdIssuanceDate;
	@FXML private DatePicker dpIdExpiryDate;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	private static final Predicate<LocalDate> birthDateValidator = localDate -> !localDate.isAfter(LocalDate.now());
	
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
		
		@SuppressWarnings("unchecked") List<NationalityBean> nationalities = (List<NationalityBean>)
												Context.getUserSession().getAttribute("lookups.nationalities");
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboNationality, nationalities);
		
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboGender);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboNationality);
		
		cboNationality.setConverter(new StringConverter<HideableItem<NationalityBean>>()
		{
			@Override
			public String toString(HideableItem<NationalityBean> object)
			{
				if(object == null) return "";
				else return object.getText();
			}
			
			@Override
			public HideableItem<NationalityBean> fromString(String string)
			{
				if(string == null || string.trim().isEmpty()) return null;
				
				for(HideableItem<NationalityBean> nationalityBean : cboNationality.getItems())
				{
					if(string.equals(nationalityBean.getText())) return nationalityBean;
				}
				
				return null;
			}
		});
		
		GuiUtils.initDatePicker(cbBirthDateShowHijri, dpBirthDate, birthDateValidator);
		GuiUtils.initDatePicker(cbIdIssuanceDateShowHijri, dpIdIssuanceDate, null);
		GuiUtils.initDatePicker(cbIdExpiryDateShowHijri, dpIdExpiryDate, null);
		
		BooleanBinding txtFirstNameBinding = txtFirstName.textProperty().isEmpty();
		BooleanBinding txtFatherNameBinding = txtFatherName.textProperty().isEmpty();
		BooleanBinding txtGrandfatherNameBinding = txtGrandfatherName.textProperty().isEmpty();
		BooleanBinding txtFamilyNameBinding = txtFamilyName.textProperty().isEmpty();
		BooleanBinding txtPublicFileNumberBinding = txtPublicFileNumber.textProperty().isEmpty();
		BooleanBinding txtOccupationBinding = txtOccupation.textProperty().isEmpty();
		BooleanBinding txtBirthPlaceBinding = txtBirthPlace.textProperty().isEmpty();
		BooleanBinding txtIdNumberBinding = txtIdNumber.textProperty().isEmpty();
		BooleanBinding txtIdTypeBinding = txtIdType.textProperty().isEmpty();
		BooleanBinding cboGenderBinding = cboGender.valueProperty().isNull();
		BooleanBinding cboNationalityBinding = cboNationality.valueProperty().isNull();
		BooleanBinding dpBirthDateBinding = dpBirthDate.valueProperty().isNull();
		BooleanBinding dpIdIssuanceDateBinding = dpIdIssuanceDate.valueProperty().isNull();
		BooleanBinding dpIdExpiryDateBinding = dpIdExpiryDate.valueProperty().isNull();
		
		// TODO: enable this when done
		/*btnNext.disableProperty().bind(txtFirstNameBinding.or(txtFatherNameBinding).or(txtGrandfatherNameBinding)
	                               .or(txtFamilyNameBinding).or(txtPublicFileNumberBinding).or(txtOccupationBinding)
	                               .or(txtBirthPlaceBinding).or(txtIdNumberBinding).or(txtIdTypeBinding)
	                               .or(cboGenderBinding).or(cboNationalityBinding).or(dpBirthDateBinding)
	                               .or(dpIdIssuanceDateBinding).or(dpIdExpiryDateBinding));*/
	}
	
	@Override
	protected void onAttachedToScene()
	{
		cboNationality.getItems().forEach(item ->
		{
		    NationalityBean nationalityBean = item.getObject();
		
		    String text;
		    if(Context.getGuiLanguage() == GuiLanguage.ARABIC) text = nationalityBean.getDescriptionAR();
		    else text = nationalityBean.getDescriptionEN();
		
		    String resultText = text.trim() + " (" + nationalityBean.getMofaNationalityCode() + ")";
		    String normalizedText = Normalizer.normalize(resultText, Normalizer.Form.NFKC);
		
		    item.setText(normalizedText);
		});
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			FingerprintInquiryStatusResult result = (FingerprintInquiryStatusResult)
												uiInputData.get(InquiryResultPaneFxController.KEY_INQUIRY_HIT_RESULT);
			PersonInfo personInfo = result.getPersonInfo();
			
			Name name = personInfo.getName();
			
			Node focusedNode = null;
			
			String firstName = AppUtils.buildNamePart(name.getFirstName(), name.getTranslatedFirstName(),
			                                          false);
			String fatherName = AppUtils.buildNamePart(name.getFatherName(), name.getTranslatedFatherName(),
			                                           false);
			String grandfatherName = AppUtils.buildNamePart(name.getGrandfatherName(),
			                                                name.getTranslatedGrandFatherName(),
			                                                false);
			String familyName = AppUtils.buildNamePart(name.getFamilyName(), name.getTranslatedFamilyName(),
			                                           false);
			
			if(firstName != null && !firstName.trim().isEmpty()) txtFirstName.setText(firstName);
			else focusedNode = txtFirstName;
			
			if(fatherName != null && !fatherName.trim().isEmpty()) txtFatherName.setText(fatherName);
			else if(focusedNode == null) focusedNode = txtFatherName;
			
			if(grandfatherName != null && !grandfatherName.trim().isEmpty())
																		txtGrandfatherName.setText(grandfatherName);
			else if(focusedNode == null) focusedNode = txtGrandfatherName;
			
			if(familyName != null && !familyName.trim().isEmpty()) txtFamilyName.setText(familyName);
			else if(focusedNode == null) focusedNode = txtFamilyName;
			
			long criminalBioId = result.getCrimnalHitBioId();
			if(criminalBioId > 0) txtPublicFileNumber.setText(String.valueOf(criminalBioId));
			else if(focusedNode == null) focusedNode = txtPublicFileNumber;
			
			String birthPlace = personInfo.getBirthPlace();
			if(birthPlace != null && !birthPlace.trim().isEmpty()) txtBirthPlace.setText(birthPlace);
			else if(focusedNode == null) focusedNode = txtBirthPlace;
			
			long samisId = personInfo.getSamisId();
			if(samisId > 0) txtIdNumber.setText(String.valueOf(samisId));
			else if(focusedNode == null) focusedNode = txtIdNumber;
			
			String personType = personInfo.getPersonType();
			if(personType != null && !personType.trim().isEmpty()) txtIdType.setText(personType);
			else if(focusedNode == null) focusedNode = txtIdType;
			
			cboGender.getItems()
					 .stream()
					 .filter(item -> item.getItem() == GenderType.values()[personInfo.getGender()])
					 .findFirst()
					 .ifPresent(cboGender::setValue);
			
			cboNationality.getItems()
						  .stream()
						  .filter(item -> item.getObject().getCode() == personInfo.getNationality())
						  .findFirst()
						  .ifPresent(cboNationality::setValue);
			
			if(cboGender.getValue() == null && focusedNode == null) focusedNode = cboGender;
			if(cboNationality.getValue() == null && focusedNode == null) focusedNode = cboNationality;
			
			Date birthDate = personInfo.getBirthDate();
			if(birthDate != null) dpBirthDate.setValue(
												birthDate.toInstant().atZone(AppConstants.SAUDI_ZONE).toLocalDate());
			else if(focusedNode == null) focusedNode = dpBirthDate;
			
			if(focusedNode != null) focusedNode.requestFocus();
			else btnNext.requestFocus();
		}
	}
}