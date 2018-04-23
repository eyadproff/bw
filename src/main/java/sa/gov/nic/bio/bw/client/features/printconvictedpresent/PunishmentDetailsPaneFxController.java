package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;

import java.net.URL;
import java.util.Map;

public class PunishmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_PUNISHMENT_DETAILS_LASHES = "PUNISHMENT_DETAILS_LASHES";
	public static final String KEY_PUNISHMENT_DETAILS_FINE = "PUNISHMENT_DETAILS_FINE";
	public static final String KEY_PUNISHMENT_DETAILS_JAIL_YEARS = "PUNISHMENT_DETAILS_JAIL_YEARS";
	public static final String KEY_PUNISHMENT_DETAILS_JAIL_MONTHS = "PUNISHMENT_DETAILS_JAIL_MONTHS";
	public static final String KEY_PUNISHMENT_DETAILS_JAIL_DAYS = "PUNISHMENT_DETAILS_JAIL_DAYS";
	public static final String KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_YEARS = "PUNISHMENT_DETAILS_TRAVEL_BAN_YEARS";
	public static final String KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_MONTHS = "PUNISHMENT_DETAILS_TRAVEL_BAN_MONTHS";
	public static final String KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_DAYS = "PUNISHMENT_DETAILS_TRAVEL_BAN_DAYS";
	public static final String KEY_PUNISHMENT_DETAILS_EXILING_YEARS = "PUNISHMENT_DETAILS_EXILING_YEARS";
	public static final String KEY_PUNISHMENT_DETAILS_EXILING_MONTHS = "PUNISHMENT_DETAILS_EXILING_MONTHS";
	public static final String KEY_PUNISHMENT_DETAILS_EXILING_DAYS = "PUNISHMENT_DETAILS_EXILING_DAYS";
	public static final String KEY_PUNISHMENT_DETAILS_DEPORTATION_YEARS = "PUNISHMENT_DETAILS_DEPORTATION_YEARS";
	public static final String KEY_PUNISHMENT_DETAILS_DEPORTATION_MONTHS = "PUNISHMENT_DETAILS_DEPORTATION_MONTHS";
	public static final String KEY_PUNISHMENT_DETAILS_DEPORTATION_DAYS = "PUNISHMENT_DETAILS_DEPORTATION_DAYS";
	public static final String KEY_PUNISHMENT_DETAILS_FINAL_DEPORTATION = "PUNISHMENT_DETAILS_FINAL_DEPORTATION";
	public static final String KEY_PUNISHMENT_DETAILS_LIBEL = "PUNISHMENT_DETAILS_LIBEL";
	public static final String KEY_PUNISHMENT_DETAILS_COVENANT = "PUNISHMENT_DETAILS_COVENANT";
	public static final String KEY_PUNISHMENT_DETAILS_OTHER = "PUNISHMENT_DETAILS_OTHER";
	
	@FXML private Spinner<Integer> spnLashes;
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
		btnNext.setOnAction(actionEvent -> goNext());
		
		GuiUtils.applyValidatorToTextField(spnLashes.getEditor(), "\\d*", "[^\\d]",
		                                   10);
		GuiUtils.applyValidatorToTextField(spnFine.getEditor(), "\\d*", "[^\\d]",
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
		spnLashes.getValueFactory().setConverter(integerStringConverter);
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
		spnLashes.focusedProperty().addListener(new FocusChangeListener(spnLashes));
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
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Node focusedNode = null;
			
			Integer lashes = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_LASHES);
			if(lashes != null) spnLashes.getValueFactory().setValue(lashes);
			else focusedNode = spnLashes;
			
			Integer fine = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_FINE);
			if(fine != null) spnFine.getValueFactory().setValue(fine);
			else if(focusedNode == null) focusedNode = spnFine;
			
			Integer jailYears = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_JAIL_YEARS);
			if(jailYears != null) spnJailYears.getValueFactory().setValue(jailYears);
			else if(focusedNode == null) focusedNode = spnJailYears;
			
			Integer jailMonths = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_JAIL_MONTHS);
			if(jailMonths != null) spnJailMonths.getValueFactory().setValue(jailMonths);
			else if(focusedNode == null) focusedNode = spnJailMonths;
			
			Integer jailDays = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_JAIL_DAYS);
			if(jailDays != null) spnJailDays.getValueFactory().setValue(jailDays);
			else if(focusedNode == null) focusedNode = spnJailDays;
			
			Integer travelBanYears = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_YEARS);
			if(travelBanYears != null) spnTravelBanYears.getValueFactory().setValue(travelBanYears);
			else if(focusedNode == null) focusedNode = spnTravelBanYears;
			
			Integer travelBanMonths = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_MONTHS);
			if(travelBanMonths != null) spnTravelBanMonths.getValueFactory().setValue(travelBanMonths);
			else if(focusedNode == null) focusedNode = spnTravelBanMonths;
			
			Integer travelBanDays = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_DAYS);
			if(travelBanDays != null) spnTravelBanDays.getValueFactory().setValue(travelBanDays);
			else if(focusedNode == null) focusedNode = spnTravelBanDays;
			
			Integer exilingYears = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_EXILING_YEARS);
			if(exilingYears != null) spnExilingYears.getValueFactory().setValue(exilingYears);
			else if(focusedNode == null) focusedNode = spnExilingYears;
			
			Integer exilingMonths = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_EXILING_MONTHS);
			if(exilingMonths != null) spnExilingMonths.getValueFactory().setValue(exilingMonths);
			else if(focusedNode == null) focusedNode = spnExilingMonths;
			
			Integer exilingDays = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_EXILING_DAYS);
			if(exilingDays != null) spnExilingDays.getValueFactory().setValue(exilingDays);
			else if(focusedNode == null) focusedNode = spnExilingDays;
			
			Integer deportationYears = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_DEPORTATION_YEARS);
			if(deportationYears != null) spnDeportationYears.getValueFactory().setValue(deportationYears);
			else if(focusedNode == null) focusedNode = spnDeportationYears;
			
			Integer deportationMonths = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_DEPORTATION_MONTHS);
			if(deportationMonths != null) spnDeportationMonths.getValueFactory().setValue(deportationMonths);
			else if(focusedNode == null) focusedNode = spnDeportationMonths;
			
			Integer deportationDays = (Integer) uiInputData.get(KEY_PUNISHMENT_DETAILS_DEPORTATION_DAYS);
			if(deportationDays != null) spnDeportationDays.getValueFactory().setValue(deportationDays);
			else if(focusedNode == null) focusedNode = spnDeportationDays;
			
			Boolean finalDeportation = (Boolean) uiInputData.get(KEY_PUNISHMENT_DETAILS_FINAL_DEPORTATION);
			cbFinalDeportation.setSelected(finalDeportation != null && finalDeportation);
			
			Boolean libel = (Boolean) uiInputData.get(KEY_PUNISHMENT_DETAILS_LIBEL);
			cbLibel.setSelected(libel != null && libel);
			
			Boolean covenant = (Boolean) uiInputData.get(KEY_PUNISHMENT_DETAILS_COVENANT);
			cbCovenant.setSelected(covenant != null && covenant);
			
			String other = (String) uiInputData.get(KEY_PUNISHMENT_DETAILS_OTHER);
			if(other != null && !other.isEmpty()) txtOther.setText(other);
			else if(focusedNode == null) focusedNode = txtOther;
			
			if(focusedNode != null) focusedNode.requestFocus();
			else btnNext.requestFocus();
		}
	}
	
	@Override
	public void onLeaving(Map<String, Object> uiDataMap)
	{
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_LASHES, spnLashes.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_FINE, spnFine.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_JAIL_YEARS, spnJailYears.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_JAIL_MONTHS, spnJailMonths.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_JAIL_DAYS, spnJailDays.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_YEARS, spnTravelBanYears.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_MONTHS, spnTravelBanMonths.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_TRAVEL_BAN_DAYS, spnTravelBanDays.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_EXILING_YEARS, spnExilingYears.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_EXILING_MONTHS, spnExilingMonths.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_EXILING_DAYS, spnExilingDays.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_DEPORTATION_YEARS, spnDeportationYears.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_DEPORTATION_MONTHS, spnDeportationMonths.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_DEPORTATION_DAYS, spnDeportationDays.getValue());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_FINAL_DEPORTATION, cbFinalDeportation.isSelected());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_LIBEL, cbLibel.isSelected());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_COVENANT, cbCovenant.isSelected());
		uiDataMap.put(KEY_PUNISHMENT_DETAILS_OTHER, txtOther.getText());
	}
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("printConvictedPresent.startingOver.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
}