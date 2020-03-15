package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sa.gov.nic.bio.bw.commons.resources.images.CommonImages;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.Gender;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeCrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeDecision;
import sa.gov.nic.bio.bw.workflow.commons.beans.BiometricsExchangeParty;
import sa.gov.nic.bio.bw.workflow.commons.beans.Country;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.DocumentType;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangeCrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.BiometricsExchangePartiesLookup;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@FxmlFile("shareInformation.fxml")
public class ShareInformationPaneFxController extends WizardStepFxControllerBase
{
	@Input private String facePhotoBase64;
	@Input private String firstName;
	@Input private String familyName;
	@Input private Gender gender;
	@Input private Country nationality;
	@Input private LocalDate birthDate;
	@Input private String documentId;
	@Input private DocumentType documentType;
	@Input private LocalDate documentIssuanceDate;
	@Input private LocalDate documentExpiryDate;
	@Input private LocalDate judgmentDate;
	@Input(alwaysRequired = true) private List<CrimeCode> crimes;
	@Output private List<CrimeCode> crimesWithShares;
	
	@FXML private TitledPane tpRequirements;
	@FXML private TitledPane tpCrimeClassification1;
	@FXML private TitledPane tpCrimeClassification2;
	@FXML private TitledPane tpCrimeClassification3;
	@FXML private TitledPane tpCrimeClassification4;
	@FXML private TitledPane tpCrimeClassification5;
	@FXML private Pane paneRequirements;
	@FXML private Pane paneNotIsoNationality;
	@FXML private Pane paneCitizenWarning;
	@FXML private Label lblRequirements;
	@FXML private Label lblNotIsoNationality;
	@FXML private Label lblCrimeClassification1;
	@FXML private Label lblCrimeClassification2;
	@FXML private Label lblCrimeClassification3;
	@FXML private Label lblCrimeClassification4;
	@FXML private Label lblCrimeClassification5;
	@FXML private TableView<BiometricsExchangeDecision> tvBiometricsExchangeDecision1;
	@FXML private TableView<BiometricsExchangeDecision> tvBiometricsExchangeDecision2;
	@FXML private TableView<BiometricsExchangeDecision> tvBiometricsExchangeDecision3;
	@FXML private TableView<BiometricsExchangeDecision> tvBiometricsExchangeDecision4;
	@FXML private TableView<BiometricsExchangeDecision> tvBiometricsExchangeDecision5;
	@FXML private TableColumn<BiometricsExchangeDecision, BiometricsExchangeDecision> tcSequence1;
	@FXML private TableColumn<BiometricsExchangeDecision, BiometricsExchangeDecision> tcSequence2;
	@FXML private TableColumn<BiometricsExchangeDecision, BiometricsExchangeDecision> tcSequence3;
	@FXML private TableColumn<BiometricsExchangeDecision, BiometricsExchangeDecision> tcSequence4;
	@FXML private TableColumn<BiometricsExchangeDecision, BiometricsExchangeDecision> tcSequence5;
	@FXML private TableColumn<BiometricsExchangeDecision, String> tcPartyName1;
	@FXML private TableColumn<BiometricsExchangeDecision, String> tcPartyName2;
	@FXML private TableColumn<BiometricsExchangeDecision, String> tcPartyName3;
	@FXML private TableColumn<BiometricsExchangeDecision, String> tcPartyName4;
	@FXML private TableColumn<BiometricsExchangeDecision, String> tcPartyName5;
	@FXML private TableColumn<BiometricsExchangeDecision, ImageView> tcSystemDecision1;
	@FXML private TableColumn<BiometricsExchangeDecision, ImageView> tcSystemDecision2;
	@FXML private TableColumn<BiometricsExchangeDecision, ImageView> tcSystemDecision3;
	@FXML private TableColumn<BiometricsExchangeDecision, ImageView> tcSystemDecision4;
	@FXML private TableColumn<BiometricsExchangeDecision, ImageView> tcSystemDecision5;
	@FXML private TableColumn<BiometricsExchangeDecision, CheckBox> tcOperatorDecision1;
	@FXML private TableColumn<BiometricsExchangeDecision, CheckBox> tcOperatorDecision2;
	@FXML private TableColumn<BiometricsExchangeDecision, CheckBox> tcOperatorDecision3;
	@FXML private TableColumn<BiometricsExchangeDecision, CheckBox> tcOperatorDecision4;
	@FXML private TableColumn<BiometricsExchangeDecision, CheckBox> tcOperatorDecision5;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		List<String> missingFields = new ArrayList<>();
		if(facePhotoBase64 == null) missingFields.add(resources.getString("label.facePhoto.plain"));
		if(firstName == null) missingFields.add(resources.getString("label.firstName.plain"));
		if(familyName == null) missingFields.add(resources.getString("label.familyName.plain"));
		if(gender == null) missingFields.add(resources.getString("label.gender.plain"));
		if(nationality == null) missingFields.add(resources.getString("label.nationality.plain"));
		if(birthDate == null) missingFields.add(resources.getString("label.birthDate.plain"));
		if(documentId == null) missingFields.add(resources.getString("label.documentId.plain"));
		if(documentType == null) missingFields.add(resources.getString("label.documentType.plain"));
		if(documentIssuanceDate == null) missingFields.add(
														resources.getString("label.documentIssuanceDate.plain"));
		if(documentExpiryDate == null) missingFields.add(resources.getString("label.documentExpiryDate.plain"));
		if(judgmentDate == null) missingFields.add(resources.getString("label.judgmentDate.plain"));
		
		boolean isoNationality = nationality != null && nationality.getMofaNationalityCode() != null &&
								 nationality.getMofaNationalityCode().length() == 3;
		boolean citizen = nationality != null && "SAU".equals(nationality.getMofaNationalityCode());
		boolean disableSharing = !missingFields.isEmpty() || !isoNationality || citizen;
		
		if(disableSharing)
		{
			GuiUtils.showNode(tpRequirements, true);
			boolean arabic = Context.getGuiLanguage() == GuiLanguage.ARABIC;
			
			if(!missingFields.isEmpty())
			{
				String requirements = missingFields.stream().collect(Collectors.joining(arabic ? "، " : ", "));
				lblRequirements.setText(requirements);
				GuiUtils.showNode(paneRequirements, true);
			}
			
			if(!isoNationality)
			{
				lblNotIsoNationality.setText(String.format(resources.getString("label.notIsoNationality"),
                                                arabic ? nationality.getArabicText() : nationality.getEnglishText()));
				GuiUtils.showNode(paneNotIsoNationality, true);
			}
			
			if(citizen) GuiUtils.showNode(paneCitizenWarning, true);
			
			crimesWithShares = crimes;
			crimesWithShares.forEach(crimeCode -> crimeCode.setCriminalBioExchange(new ArrayList<>()));
			btnNext.requestFocus();
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<BiometricsExchangeParty> biometricsExchangeParties =
				(List<BiometricsExchangeParty>) Context.getUserSession().getAttribute(
																				BiometricsExchangePartiesLookup.KEY);
		@SuppressWarnings("unchecked")
		List<BiometricsExchangeCrimeType> biometricsExchangeCrimeTypes =
				(List<BiometricsExchangeCrimeType>) Context.getUserSession().getAttribute(
																				BiometricsExchangeCrimeTypesLookup.KEY);
		
		List<CrimeCode> newCrimes;
		
		if(crimesWithShares == null) // first visit
		{
			crimesWithShares = new ArrayList<>();
			for(CrimeCode crime : crimes) crimesWithShares.add(new CrimeCode(crime));
			newCrimes = crimesWithShares;
		}
		else
		{
			List<CrimeCode> intersection = crimesWithShares.stream()
														   .filter(crimeCode -> containsCrime(crimes, crimeCode))
														   .collect(Collectors.toList());
			
			// remove crimes that were removed by the user
			crimesWithShares.retainAll(intersection);
			
			// new crimes
			newCrimes = crimes.stream()
							  .filter(crimeCode -> !containsCrime(crimesWithShares, crimeCode))
							  .collect(Collectors.toList());
			
			// add the new crimes to the final result
			crimesWithShares.addAll(newCrimes);
		}
		
		for(CrimeCode crimeCode : newCrimes)
		{
			List<BiometricsExchangeDecision> criminalBioExchange = new ArrayList<>();
			crimeCode.setCriminalBioExchange(criminalBioExchange);
			
			for(BiometricsExchangeParty biometricsExchangeParty : biometricsExchangeParties)
			{
				Integer partyCode = biometricsExchangeParty.getOrgPartyCode();
				BiometricsExchangeCrimeType biometricsExchangeCrimeType =
								getBiometricsExchangeCrimeType(biometricsExchangeCrimeTypes, partyCode, crimeCode);
				
				if(biometricsExchangeCrimeType != null) criminalBioExchange.add(new BiometricsExchangeDecision(
																partyCode, true, true,
			                                                    biometricsExchangeCrimeType.getOrgPartyCrimeCode()));
				else criminalBioExchange.add(new BiometricsExchangeDecision(partyCode, false,
				                                                            false, null));
			}
		}
		
		Map<Integer, BiometricsExchangeParty> biometricsExchangePartiesMap =
												biometricsExchangeParties.stream().collect(Collectors.toMap(
													BiometricsExchangeParty::getOrgPartyCode, Function.identity()));
		
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
		
		Map<Integer, String> crimeEventTitles = crimeTypes.stream().collect(
								Collectors.toMap(CrimeType::getEventCode, CrimeType::getEventDesc, (k1, k2) -> k1));
		Map<Integer, Map<Integer, String>> crimeClassTitles = crimeTypes.stream().collect(Collectors.groupingBy(CrimeType::getEventCode,
		                                                                                                        Collectors.toMap(CrimeType::getClassCode, CrimeType::getClassDesc, (k1, k2) -> k1)));
		
		// fill data
		for(int i = 0; i < crimesWithShares.size(); i++)
		{
			CrimeCode crimeCode = crimesWithShares.get(i);
			
			switch(i)
			{
				case 0: fillData(tpCrimeClassification1, lblCrimeClassification1, tvBiometricsExchangeDecision1,
				                 tcSequence1, tcPartyName1, tcSystemDecision1, tcOperatorDecision1, crimeCode,
				                 crimeEventTitles, crimeClassTitles, biometricsExchangePartiesMap, disableSharing);
				break;
				case 1: fillData(tpCrimeClassification2, lblCrimeClassification2, tvBiometricsExchangeDecision2,
				                 tcSequence2, tcPartyName2, tcSystemDecision2, tcOperatorDecision2, crimeCode,
				                 crimeEventTitles, crimeClassTitles, biometricsExchangePartiesMap, disableSharing);
				break;
				case 2: fillData(tpCrimeClassification3, lblCrimeClassification3, tvBiometricsExchangeDecision3,
				                 tcSequence3, tcPartyName3, tcSystemDecision3, tcOperatorDecision3, crimeCode,
				                 crimeEventTitles, crimeClassTitles, biometricsExchangePartiesMap, disableSharing);
				break;
				case 3: fillData(tpCrimeClassification4, lblCrimeClassification4, tvBiometricsExchangeDecision4,
				                 tcSequence4, tcPartyName4, tcSystemDecision4, tcOperatorDecision4, crimeCode,
				                 crimeEventTitles, crimeClassTitles, biometricsExchangePartiesMap, disableSharing);
				break;
				case 4: fillData(tpCrimeClassification5, lblCrimeClassification5, tvBiometricsExchangeDecision5,
				                 tcSequence5, tcPartyName5, tcSystemDecision5, tcOperatorDecision5, crimeCode,
				                 crimeEventTitles, crimeClassTitles, biometricsExchangePartiesMap, disableSharing);
				break;
			}
		}
		
		btnNext.requestFocus();
	}
	
	private static boolean containsCrime(List<CrimeCode> crimes, CrimeCode crime)
	{
		for(CrimeCode crimeCode : crimes) if(equalCrimes(crime, crimeCode)) return true;
		return false;
	}
	
	private static boolean equalCrimes(CrimeCode crime1, CrimeCode crime2)
	{
		return crime1 != null && crime2 != null && crime1.getCrimeEvent() == crime2.getCrimeEvent() &&
			   crime1.getCrimeClass() == crime2.getCrimeClass();
	}
	
	private static BiometricsExchangeCrimeType getBiometricsExchangeCrimeType(
											 List<BiometricsExchangeCrimeType> biometricsExchangeCrimeTypes,
	                                         Integer partyCode, CrimeCode crime)
	{
		for(BiometricsExchangeCrimeType biometricsExchangeCrimeType : biometricsExchangeCrimeTypes)
		{
			if(biometricsExchangeCrimeType == null ||
					!biometricsExchangeCrimeType.getOrgPartyCode().equals(partyCode))
			{
				continue;
			}
			
			CrimeCode crimeCode = new CrimeCode(biometricsExchangeCrimeType.getCrimeEventCode(),
			                                    biometricsExchangeCrimeType.getCrimeClassCode());
			if(equalCrimes(crimeCode, crime)) return biometricsExchangeCrimeType;
		}
		
		return null;
	}
	
	private static void fillData(TitledPane tpCrimeClassification, Label lblCrimeClassification,
	                             TableView<BiometricsExchangeDecision> tvBiometricsExchangeDecision,
	                             TableColumn<BiometricsExchangeDecision, BiometricsExchangeDecision> tcSequence,
	                             TableColumn<BiometricsExchangeDecision, String> tcPartyName,
	                             TableColumn<BiometricsExchangeDecision, ImageView> tcSystemDecision,
	                             TableColumn<BiometricsExchangeDecision, CheckBox> tcOperatorDecision,
	                             CrimeCode crimeCode, Map<Integer, String> crimeEventTitles,
	                             Map<Integer, Map<Integer, String>> crimeClassTitles,
	                             Map<Integer, BiometricsExchangeParty> biometricsExchangePartiesMap,
	                             boolean disableSharing)
	{
		GuiUtils.showNode(tpCrimeClassification, true);
		lblCrimeClassification.setText(crimeEventTitles.get(crimeCode.getCrimeEvent()) + ": " +
                                       crimeClassTitles.get(crimeCode.getCrimeEvent()).get(crimeCode.getCrimeClass()));
		
		GuiUtils.initSequenceTableColumn(tcSequence);
		tcSequence.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		
		tcPartyName.setCellValueFactory(param ->
		{
			int bioExchangePartyCode = param.getValue().getPartyCode();
			BiometricsExchangeParty biometricsExchangeParty = biometricsExchangePartiesMap.get(bioExchangePartyCode);
		    return new SimpleStringProperty(biometricsExchangeParty.getLocalizedText());
		});
		
		tcSystemDecision.setCellValueFactory(param ->
		{
			BiometricsExchangeDecision biometricsExchangeDecision = param.getValue();
			
			InputStream is = biometricsExchangeDecision.getSystemDecision() ?
																CommonImages.ICON_SUCCESS_16PX.getAsInputStream() :
																CommonImages.ICON_ERROR_16PX.getAsInputStream();
			Image image = new Image(is);
			ImageView iv = new ImageView(image);
			return new SimpleObjectProperty<>(iv);
		});
		
		tcOperatorDecision.setCellValueFactory(param ->
		{
		    BiometricsExchangeDecision biometricsExchangeDecision = param.getValue();
		    
		    if(disableSharing || !biometricsExchangeDecision.getSystemDecision())
		    	                                                        return new SimpleObjectProperty<>(null);
		    else
		    {
			    CheckBox checkBox = new CheckBox();
			    checkBox.setSelected(biometricsExchangeDecision.getOperatorDecision());
			    checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
				                                            biometricsExchangeDecision.setOperatorDecision(newValue));
			    return new SimpleObjectProperty<>(checkBox);
		    }
		});
		
		List<BiometricsExchangeDecision> criminalBioExchange = crimeCode.getCriminalBioExchange();
		tvBiometricsExchangeDecision.getItems().setAll(criminalBioExchange);
	}
}