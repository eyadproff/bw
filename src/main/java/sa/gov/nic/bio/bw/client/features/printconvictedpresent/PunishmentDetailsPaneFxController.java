package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class PunishmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	@FXML private Spinner spLashes;
	@FXML private Spinner spFine;
	@FXML private Spinner spnJailYears;
	@FXML private Spinner spnJailMonths;
	@FXML private Spinner spnJailDays;
	@FXML private Spinner spnTravelBanYears;
	@FXML private Spinner spnTravelBanMonths;
	@FXML private Spinner spnTravelBanDays;
	@FXML private Spinner spnExilingYears;
	@FXML private Spinner spnExilingMonths;
	@FXML private Spinner spnExilingDays;
	@FXML private Spinner spnDeportationYears;
	@FXML private Spinner spnDeportationMonths;
	@FXML private Spinner spnDeportationDays;
	@FXML private CheckBox cbFinalDeportation;
	@FXML private CheckBox cbLibel;
	@FXML private CheckBox cbCovenant;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	@Override
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/punishmentDetails.fxml");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialize()
	{
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		btnStartOver.setOnAction(actionEvent -> startOver());
		btnNext.setOnAction(actionEvent -> goNext());
		
		GuiUtils.applyValidatorToTextField(spLashes.getEditor(), "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(spFine.getEditor(), "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(spnJailYears.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnJailMonths.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnJailDays.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnTravelBanYears.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnTravelBanMonths.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnTravelBanDays.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnExilingYears.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnExilingMonths.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnExilingDays.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnDeportationYears.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnDeportationMonths.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		GuiUtils.applyValidatorToTextField(spnDeportationDays.getEditor(), "\\d*", "[^\\d]",
		                                   2);
		
		// set a custom converter to avoid NPE in case of empty string
		StringConverter<Integer> integerStringConverter = new StringConverter<Integer>()
		{
			
			@Override
			public String toString(Integer object)
			{
				return object.toString();
			}
			
			@Override
			public Integer fromString(String string)
			{
				if(string.matches("\\d+")) return new Integer(string);
				else return 0;
			}
			
		};
		spLashes.getValueFactory().setConverter(integerStringConverter);
		spFine.getValueFactory().setConverter(integerStringConverter);
		spnJailYears.getValueFactory().setConverter(integerStringConverter);
		spnJailMonths.getValueFactory().setConverter(integerStringConverter);
		spnJailDays.getValueFactory().setConverter(integerStringConverter);
		spnTravelBanYears.getValueFactory().setConverter(integerStringConverter);
		spnTravelBanMonths.getValueFactory().setConverter(integerStringConverter);
		spnTravelBanDays.getValueFactory().setConverter(integerStringConverter);
		spnExilingYears.getValueFactory().setConverter(integerStringConverter);
		spnExilingMonths.getValueFactory().setConverter(integerStringConverter);
		spnExilingDays.getValueFactory().setConverter(integerStringConverter);
		spnDeportationYears.getValueFactory().setConverter(integerStringConverter);
		spnDeportationMonths.getValueFactory().setConverter(integerStringConverter);
		spnDeportationDays.getValueFactory().setConverter(integerStringConverter);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
		
		}
	}
}