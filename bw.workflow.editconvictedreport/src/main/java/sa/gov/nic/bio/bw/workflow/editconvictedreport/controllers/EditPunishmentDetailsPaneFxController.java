package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.JudgementInfo;

import java.util.Map;

@FxmlFile("editPunishmentDetails.fxml")
public class EditPunishmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private JudgementInfo judgementInfo;
	@Output private Integer tazeerLashes;
	@Output private Integer hadLashes;
	@Output private Integer fine;
	@Output private Integer jailYears;
	@Output private Integer jailMonths;
	@Output private Integer jailDays;
	@Output private Integer travelBanYears;
	@Output private Integer travelBanMonths;
	@Output private Integer travelBanDays;
	@Output private Integer exilingYears;
	@Output private Integer exilingMonths;
	@Output private Integer exilingDays;
	@Output private Integer deportationYears;
	@Output private Integer deportationMonths;
	@Output private Integer deportationDays;
	@Output private Boolean finalDeportation;
	@Output private Boolean libel;
	@Output private Boolean covenant;
	@Output private String other;
	
	@FXML private Spinner<Integer> spnTazeerLashes;
	@FXML private Spinner<Integer> spnHadLashes;
	@FXML private Spinner<Integer> spnFine;
	@FXML private Spinner<Integer> spnJailYears;
	@FXML private Spinner<Integer> spnJailMonths;
	@FXML private Spinner<Integer> spnJailDays;
	@FXML private Spinner<Integer> spnTravelBanYears;
	@FXML private Spinner<Integer> spnTravelBanMonths;
	@FXML private Spinner<Integer> spnTravelBanDays;
	@FXML private Spinner<Integer> spnExilingYears;
	@FXML private Spinner<Integer> spnExilingMonths;
	@FXML private Spinner<Integer> spnExilingDays;
	@FXML private Spinner<Integer> spnDeportationYears;
	@FXML private Spinner<Integer> spnDeportationMonths;
	@FXML private Spinner<Integer> spnDeportationDays;
	@FXML private CheckBox cbFinalDeportation;
	@FXML private CheckBox cbLibel;
	@FXML private CheckBox cbCovenant;
	@FXML private TextField txtOther;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	@Override
	protected void onAttachedToScene()
	{
		GuiUtils.applyValidatorToTextField(spnTazeerLashes.getEditor(), "\\d*", "[^\\d]",
		                                   9);
		GuiUtils.applyValidatorToTextField(spnHadLashes.getEditor(), "\\d*", "[^\\d]",
		                                   9);
		GuiUtils.applyValidatorToTextField(spnFine.getEditor(), "\\d*", "[^\\d]",
		                                   9);
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
		GuiUtils.applyValidatorToTextField(txtOther, null, null, 150);
		
		// set a custom converter to avoid NPE in case of empty string
		StringConverter<Integer> integerStringConverter = new StringConverter<>()
		{
			
			@Override
			public String toString(Integer object)
			{
				return object.toString();
			}
			
			@Override
			public Integer fromString(String string)
			{
				if(string.matches("\\d+")) return Integer.valueOf(string);
				else return 0;
			}
			
		};
		spnTazeerLashes.getValueFactory().setConverter(integerStringConverter);
		spnHadLashes.getValueFactory().setConverter(integerStringConverter);
		spnFine.getValueFactory().setConverter(integerStringConverter);
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
		
		class FocusChangeListener implements ChangeListener<Boolean>
		{
			private Spinner<Integer> spinner;
			
			private FocusChangeListener(Spinner<Integer> spinner)
			{
				this.spinner = spinner;
			}
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				if(!newValue)
				{
					spinner.increment(0); // won't change value, but will commit editor
				}
			}
		}
		spnTazeerLashes.focusedProperty().addListener(new FocusChangeListener(spnTazeerLashes));
		spnHadLashes.focusedProperty().addListener(new FocusChangeListener(spnHadLashes));
		spnFine.focusedProperty().addListener(new FocusChangeListener(spnFine));
		spnJailYears.focusedProperty().addListener(new FocusChangeListener(spnJailYears));
		spnJailMonths.focusedProperty().addListener(new FocusChangeListener(spnJailMonths));
		spnJailDays.focusedProperty().addListener(new FocusChangeListener(spnJailDays));
		spnTravelBanYears.focusedProperty().addListener(new FocusChangeListener(spnTravelBanYears));
		spnTravelBanMonths.focusedProperty().addListener(new FocusChangeListener(spnTravelBanMonths));
		spnTravelBanDays.focusedProperty().addListener(new FocusChangeListener(spnTravelBanDays));
		spnExilingYears.focusedProperty().addListener(new FocusChangeListener(spnExilingYears));
		spnExilingMonths.focusedProperty().addListener(new FocusChangeListener(spnExilingMonths));
		spnExilingDays.focusedProperty().addListener(new FocusChangeListener(spnExilingDays));
		spnDeportationYears.focusedProperty().addListener(new FocusChangeListener(spnDeportationYears));
		spnDeportationMonths.focusedProperty().addListener(new FocusChangeListener(spnDeportationMonths));
		spnDeportationDays.focusedProperty().addListener(new FocusChangeListener(spnDeportationDays));
		
		GuiUtils.applyValidatorToTextField(txtOther, null, null, 4000);
		
		BooleanBinding spnTazeerLashesBinding = spnTazeerLashes.valueProperty().isEqualTo(0);
		BooleanBinding spnHadLashesBinding = spnHadLashes.valueProperty().isEqualTo(0);
		BooleanBinding spnFineBinding = spnFine.valueProperty().isEqualTo(0);
		BooleanBinding spnJailYearsBinding = spnJailYears.valueProperty().isEqualTo(0);
		BooleanBinding spnJailMonthsBinding = spnJailMonths.valueProperty().isEqualTo(0);
		BooleanBinding spnJailDaysBinding = spnJailDays.valueProperty().isEqualTo(0);
		BooleanBinding spnTravelBanYearsBinding = spnTravelBanYears.valueProperty().isEqualTo(0);
		BooleanBinding spnTravelBanMonthsBinding = spnTravelBanMonths.valueProperty().isEqualTo(0);
		BooleanBinding spnTravelBanDaysBinding = spnTravelBanDays.valueProperty().isEqualTo(0);
		BooleanBinding spnExilingYearsBinding = spnExilingYears.valueProperty().isEqualTo(0);
		BooleanBinding spnExilingMonthsBinding = spnExilingMonths.valueProperty().isEqualTo(0);
		BooleanBinding spnExilingDaysBinding = spnExilingDays.valueProperty().isEqualTo(0);
		BooleanBinding spnDeportationYearsBinding = spnDeportationYears.valueProperty().isEqualTo(0);
		BooleanBinding spnDeportationMonthsBinding = spnDeportationMonths.valueProperty().isEqualTo(0);
		BooleanBinding spnDeportationDaysBinding = spnDeportationDays.valueProperty().isEqualTo(0);
		BooleanBinding cbFinalDeportationBinding = cbFinalDeportation.selectedProperty().not();
		BooleanBinding cbLibelBinding = cbLibel.selectedProperty().not();
		BooleanBinding cbCovenantBinding = cbCovenant.selectedProperty().not();
		BooleanBinding txtOtherBinding = txtOther.textProperty().isEmpty();
		
		btnNext.disableProperty().bind(spnTazeerLashesBinding.and(spnHadLashesBinding).and(spnFineBinding)
                           .and(spnJailYearsBinding).and(spnJailMonthsBinding).and(spnJailDaysBinding)
                           .and(spnTravelBanYearsBinding).and(spnTravelBanMonthsBinding).and(spnTravelBanDaysBinding)
                           .and(spnExilingYearsBinding).and(spnExilingMonthsBinding).and(spnExilingDaysBinding)
                           .and(spnDeportationYearsBinding).and(spnDeportationMonthsBinding)
                           .and(spnDeportationDaysBinding).and(cbFinalDeportationBinding).and(cbLibelBinding)
                           .and(cbCovenantBinding).and(txtOtherBinding));
		
		if(tazeerLashes != null) spnTazeerLashes.getValueFactory().setValue(tazeerLashes);
		if(hadLashes != null) spnHadLashes.getValueFactory().setValue(hadLashes);
		if(fine != null) spnFine.getValueFactory().setValue(fine);
		if(jailYears != null) spnJailYears.getValueFactory().setValue(jailYears);
		if(jailMonths != null) spnJailMonths.getValueFactory().setValue(jailMonths);
		if(jailDays != null) spnJailDays.getValueFactory().setValue(jailDays);
		if(travelBanYears != null) spnTravelBanYears.getValueFactory().setValue(travelBanYears);
		if(travelBanMonths != null) spnTravelBanMonths.getValueFactory().setValue(travelBanMonths);
		if(travelBanDays != null) spnTravelBanDays.getValueFactory().setValue(travelBanDays);
		if(exilingYears != null) spnExilingYears.getValueFactory().setValue(exilingYears);
		if(exilingMonths != null) spnExilingMonths.getValueFactory().setValue(exilingMonths);
		if(exilingDays != null) spnExilingDays.getValueFactory().setValue(exilingDays);
		if(deportationYears != null) spnDeportationYears.getValueFactory().setValue(deportationYears);
		if(deportationMonths != null) spnDeportationMonths.getValueFactory().setValue(deportationMonths);
		if(deportationDays != null) spnDeportationDays.getValueFactory().setValue(deportationDays);
		
		cbFinalDeportation.setSelected(finalDeportation != null && finalDeportation);
		cbLibel.setSelected(libel != null && libel);
		cbCovenant.setSelected(covenant != null && covenant);
		
		if(other != null) txtOther.setText(other);
		
		if(btnNext.isDisabled()) spnTazeerLashes.requestFocus();
		else btnNext.requestFocus();
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		tazeerLashes = spnTazeerLashes.getValue();
		hadLashes = spnHadLashes.getValue();
		fine = spnFine.getValue();
		jailYears = spnJailYears.getValue();
		jailMonths = spnJailMonths.getValue();
		jailDays = spnJailDays.getValue();
		travelBanYears = spnTravelBanYears.getValue();
		travelBanMonths = spnTravelBanMonths.getValue();
		travelBanDays = spnTravelBanDays.getValue();
		exilingYears = spnExilingYears.getValue();
		exilingMonths = spnExilingMonths.getValue();
		exilingDays = spnExilingDays.getValue();
		deportationYears = spnDeportationYears.getValue();
		deportationMonths = spnDeportationMonths.getValue();
		deportationDays = spnDeportationDays.getValue();
		finalDeportation = cbFinalDeportation.isSelected();
		libel = cbLibel.isSelected();
		covenant = cbCovenant.isSelected();
		
		var other = txtOther.getText();
		if(!other.isBlank()) this.other = other;
		else this.other = null;
	}
}