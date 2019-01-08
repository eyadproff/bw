package sa.gov.nic.bio.bw.workflow.scfaceverification.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.beans.Name;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;
import sa.gov.nic.bio.bw.workflow.commons.beans.WantedInfo;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CountriesLookup;
import sa.gov.nic.bio.bw.workflow.faceverification.beans.FaceMatchingResponse;

import java.util.List;

@FxmlFile("showResult.fxml")
public class ShowResultFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private Long personId;
	@Input(alwaysRequired = true) private Image facePhoto;
	@Input(alwaysRequired = true) private FaceMatchingResponse faceMatchingResponse;
	
	@FXML private Pane matchedPane;
	@FXML private Pane notMatchedPane;
	@FXML private Pane infoPane;
	@FXML private Pane wantedPane;
	@FXML private TableView tvWantedActions;
	@FXML private TableColumn tcSequence;
	@FXML private TableColumn tcIssuer;
	@FXML private TableColumn tcAction;
	@FXML private Label lblNotMatched;
	@FXML private Label lblPersonId;
	@FXML private Label lblFirstName;
	@FXML private Label lblFatherName;
	@FXML private Label lblGrandfatherName;
	@FXML private Label lblFamilyName;
	@FXML private Label lblGender;
	@FXML private Label lblNationality;
	@FXML private Label lblOutOfKingdom;
	@FXML private Label lblSecurityClearance;
	@FXML private Button btnStartOver;
	
	@Override
	protected void onAttachedToScene()
	{
		if(!faceMatchingResponse.isMatched())
		{
			GuiUtils.showNode(notMatchedPane, true);
			lblNotMatched.setText(String.format(resources.getString("label.facePhotoIsNotMatched"),
			                                    AppUtils.localizeNumbers(String.valueOf(personId))));
		}
		else // matched
		{
			GuiUtils.showNode(matchedPane, true);
			GuiUtils.showNode(infoPane, true);
			
			PersonInfo personInfo = faceMatchingResponse.getPersonInfo();
			long samisId = personInfo.getSamisId();
			Name name = personInfo.getName();
			String firstName = name.getFirstName();
			String fatherName = name.getFatherName();
			String grandfatherName = name.getGrandfatherName();
			String familyName = name.getFamilyName();
			
			if(firstName != null && (firstName.trim().isEmpty() || firstName.trim().equals("-"))) firstName = null;
			if(fatherName != null && (fatherName.trim().isEmpty() || fatherName.trim().equals("-")))
				fatherName = null;
			if(grandfatherName != null && (grandfatherName.trim().isEmpty() || grandfatherName.trim().equals("-")))
				grandfatherName = null;
			if(familyName != null && (familyName.trim().isEmpty() || familyName.trim().equals("-")))
				familyName = null;
			
			if(samisId > 0)
			{
				String sSamisId = AppUtils.localizeNumbers(String.valueOf(samisId));
				lblPersonId.setText(sSamisId);
			}
			else
			{
				lblPersonId.setText(resources.getString("label.notAvailable"));
				lblPersonId.setTextFill(Color.RED);
			}
			
			if(firstName != null) lblFirstName.setText(firstName);
			else
			{
				lblFirstName.setText(resources.getString("label.notAvailable"));
				lblFirstName.setTextFill(Color.RED);
			}
			
			if(fatherName != null) lblFatherName.setText(fatherName);
			else
			{
				lblFatherName.setText(resources.getString("label.notAvailable"));
				lblFatherName.setTextFill(Color.RED);
			}
			
			if(grandfatherName != null) lblGrandfatherName.setText(grandfatherName);
			else
			{
				lblGrandfatherName.setText(resources.getString("label.notAvailable"));
				lblGrandfatherName.setTextFill(Color.RED);
			}
			
			if(familyName != null) lblFamilyName.setText(familyName);
			else
			{
				lblFamilyName.setText(resources.getString("label.notAvailable"));
				lblFamilyName.setTextFill(Color.RED);
			}
			
			Gender gender = Gender.values()[personInfo.getGender() - 1];
			lblGender.setText(gender == Gender.MALE ? resources.getString("label.male") :
					                  resources.getString("label.female"));
			
			@SuppressWarnings("unchecked")
			List<Country> countries = (List<Country>)
														Context.getUserSession().getAttribute(CountriesLookup.KEY);
			
			Country countryBean = null;
			
			for(Country country : countries)
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
				lblNationality.setText(arabic ? countryBean.getDescriptionAR() : countryBean.getDescriptionEN());
			}
			else
			{
				lblNationality.setText(resources.getString("label.notAvailable"));
				lblNationality.setTextFill(Color.RED);
			}
			
			Boolean outOfKingdom = personInfo.isOut();
			if(outOfKingdom != null) lblOutOfKingdom.setText(outOfKingdom ? resources.getString("label.yes") :
					                                                 resources.getString("label.no"));
			else
			{
				lblOutOfKingdom.setText(resources.getString("label.notAvailable"));
				lblOutOfKingdom.setTextFill(Color.RED);
			}
			
			List<WantedInfo> wantedInfos = personInfo.getWantedInfos();
			if(wantedInfos != null && !wantedInfos.isEmpty()) // wanted
			{
				lblSecurityClearance.setText(resources.getString("label.wanted"));
				lblSecurityClearance.setTextFill(Color.RED);
				GuiUtils.showNode(wantedPane, true);
			}
			else
			{
				lblSecurityClearance.setText(resources.getString("label.notWanted"));
				lblSecurityClearance.setTextFill(Color.GREEN);
			}
		}
	}
}