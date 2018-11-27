package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.BiometricsExchangeCrimeType;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.BiometricsExchangeDecision;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.BiometricsExchangeParty;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.BiometricsExchangeCrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.BiometricsExchangePartiesLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FxmlFile("shareInformation.fxml")
public class ShareInformationPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private List<CrimeCode> crimes;
	@Output private List<CrimeCode> crimesWithShares;
	
	@FXML private TitledPane tpCrimeClassification1;
	@FXML private TitledPane tpCrimeClassification2;
	@FXML private TitledPane tpCrimeClassification3;
	@FXML private TitledPane tpCrimeClassification4;
	@FXML private TitledPane tpCrimeClassification5;
	@FXML private Label lblCrimeClassification1;
	@FXML private Label lblCrimeClassification2;
	@FXML private Label lblCrimeClassification3;
	@FXML private Label lblCrimeClassification4;
	@FXML private Label lblCrimeClassification5;
	@FXML private TableView tvCrimeClassification1;
	@FXML private TableView tvCrimeClassification2;
	@FXML private TableView tvCrimeClassification3;
	@FXML private TableView tvCrimeClassification4;
	@FXML private TableView tvCrimeClassification5;
	@FXML private TableColumn tcInputSequence1;
	@FXML private TableColumn tcInputSequence2;
	@FXML private TableColumn tcInputSequence3;
	@FXML private TableColumn tcInputSequence4;
	@FXML private TableColumn tcInputSequence5;
	@FXML private TableColumn tcPartyName1;
	@FXML private TableColumn tcPartyName2;
	@FXML private TableColumn tcPartyName3;
	@FXML private TableColumn tcPartyName4;
	@FXML private TableColumn tcPartyName5;
	@FXML private TableColumn tcSystemDecision1;
	@FXML private TableColumn tcSystemDecision2;
	@FXML private TableColumn tcSystemDecision3;
	@FXML private TableColumn tcSystemDecision4;
	@FXML private TableColumn tcSystemDecision5;
	@FXML private TableColumn tcOperatorDecision1;
	@FXML private TableColumn tcOperatorDecision2;
	@FXML private TableColumn tcOperatorDecision3;
	@FXML private TableColumn tcOperatorDecision4;
	@FXML private TableColumn tcOperatorDecision5;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		List<CrimeCode> crimesToAddSystemDecisions;
		
		if(crimesWithShares != null)
		{
			List<CrimeCode> intersection = crimesWithShares.stream()
														   .filter(crimeCode -> containsCrime(crimes, crimeCode))
														   .collect(Collectors.toList());
			crimesWithShares.retainAll(intersection);
			
			crimesToAddSystemDecisions = crimes.stream()
											   .filter(crimeCode -> !containsCrime(crimesWithShares, crimeCode))
											   .collect(Collectors.toList());
			
			crimesWithShares.addAll(crimesToAddSystemDecisions);
		}
		else
		{
			crimesWithShares = new ArrayList<>();
			for(CrimeCode crime : crimes) crimesWithShares.add(new CrimeCode(crime));
			crimesToAddSystemDecisions = crimesWithShares;
		}
		
		@SuppressWarnings("unchecked")
		List<BiometricsExchangeParty> biometricsExchangeParties =
				(List<BiometricsExchangeParty>) Context.getUserSession().getAttribute(
						BiometricsExchangePartiesLookup.KEY);
		
		@SuppressWarnings("unchecked")
		List<BiometricsExchangeCrimeType> biometricsExchangeCrimeTypes =
				(List<BiometricsExchangeCrimeType>) Context.getUserSession().getAttribute(
						BiometricsExchangeCrimeTypesLookup.KEY);
		
		for(CrimeCode crimeCode : crimesToAddSystemDecisions)
		{
			for(BiometricsExchangeParty biometricsExchangeParty : biometricsExchangeParties)
			{
				Integer partyCode = biometricsExchangeParty.getOrgPartyCode();
				
				List<BiometricsExchangeDecision> criminalBioExchange = crimeCode.getCriminalBioExchange();
				if(criminalBioExchange == null)
				{
					criminalBioExchange = new ArrayList<>();
					crimeCode.setCriminalBioExchange(criminalBioExchange);
				}
				
				boolean systemDecision = getSystemDecision(biometricsExchangeCrimeTypes, partyCode, crimeCode);
				criminalBioExchange.add(new BiometricsExchangeDecision(partyCode, systemDecision,
				                                                       false));
			}
		}
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
	
	private static boolean getSystemDecision(List<BiometricsExchangeCrimeType> biometricsExchangeCrimeTypes,
	                                         Integer partyCode, CrimeCode crime)
	{
		for(BiometricsExchangeCrimeType biometricsExchangeCrimeType : biometricsExchangeCrimeTypes)
		{
			if(biometricsExchangeCrimeType == null ||
				!biometricsExchangeCrimeType.getOrgPartyCode().equals(partyCode))
			{
				continue;
			}
			
			System.out.println("biometricsExchangeCrimeType = " + biometricsExchangeCrimeType);
			CrimeCode crimeCode = new CrimeCode(biometricsExchangeCrimeType.getCrimeEventCode(),
			                                    biometricsExchangeCrimeType.getCrimeClassCode());
			if(equalCrimes(crimeCode, crime)) return true;
		}
		
		return false;
	}
}