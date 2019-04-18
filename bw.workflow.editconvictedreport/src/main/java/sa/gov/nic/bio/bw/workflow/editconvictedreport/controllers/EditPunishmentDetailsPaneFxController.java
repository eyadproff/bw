package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.Context;
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
	@Output private Integer tazeerLashesOldValue;
	@Output private Integer tazeerLashesNewValue;
	@Output private Integer hadLashesOldValue;
	@Output private Integer hadLashesNewValue;
	@Output private Integer fineOldValue;
	@Output private Integer fineNewValue;
	@Output private Integer jailYearsOldValue;
	@Output private Integer jailYearsNewValue;
	@Output private Integer jailMonthsOldValue;
	@Output private Integer jailMonthsNewValue;
	@Output private Integer jailDaysOldValue;
	@Output private Integer jailDaysNewValue;
	@Output private Integer travelBanYearsOldValue;
	@Output private Integer travelBanYearsNewValue;
	@Output private Integer travelBanMonthsOldValue;
	@Output private Integer travelBanMonthsNewValue;
	@Output private Integer travelBanDaysOldValue;
	@Output private Integer travelBanDaysNewValue;
	@Output private Integer exilingYearsOldValue;
	@Output private Integer exilingYearsNewValue;
	@Output private Integer exilingMonthsOldValue;
	@Output private Integer exilingMonthsNewValue;
	@Output private Integer exilingDaysOldValue;
	@Output private Integer exilingDaysNewValue;
	@Output private Integer deportationYearsOldValue;
	@Output private Integer deportationYearsNewValue;
	@Output private Integer deportationMonthsOldValue;
	@Output private Integer deportationMonthsNewValue;
	@Output private Integer deportationDaysOldValue;
	@Output private Integer deportationDaysNewValue;
	@Output private Boolean finalDeportationOldValue;
	@Output private Boolean finalDeportationNewValue;
	@Output private Boolean libelOldValue;
	@Output private Boolean libelNewValue;
	@Output private Boolean covenantOldValue;
	@Output private Boolean covenantNewValue;
	@Output private String otherOldValue;
	@Output private String otherNewValue;
	
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
	@FXML private CheckBox cbFinalDeportationOldValue;
	@FXML private CheckBox cbFinalDeportationNewValue;
	@FXML private CheckBox cbLibelOldValue;
	@FXML private CheckBox cbLibelNewValue;
	@FXML private CheckBox cbCovenantOldValue;
	@FXML private CheckBox cbCovenantNewValue;
	@FXML private TextField txtOther;
	@FXML private Pane panePunishmentDetails;
	@FXML private Pane paneTazeerLashesReset;
	@FXML private Pane paneHadLashesReset;
	@FXML private Pane paneFineReset;
	@FXML private Pane paneJailYearsReset;
	@FXML private Pane paneJailMonthsReset;
	@FXML private Pane paneJailDaysReset;
	@FXML private Pane paneTravelBanYearsReset;
	@FXML private Pane paneTravelBanMonthsReset;
	@FXML private Pane paneTravelBanDaysReset;
	@FXML private Pane paneExilingYearsReset;
	@FXML private Pane paneExilingMonthsReset;
	@FXML private Pane paneExilingDaysReset;
	@FXML private Pane paneDeportationYearsReset;
	@FXML private Pane paneDeportationMonthsReset;
	@FXML private Pane paneDeportationDaysReset;
	@FXML private Pane paneFinalDeportationReset;
	@FXML private Pane paneLibelReset;
	@FXML private Pane paneCovenantReset;
	@FXML private Pane paneOtherReset;
	@FXML private Glyph iconTazeerLashesArrow;
	@FXML private Glyph iconHadLashesArrow;
	@FXML private Glyph iconFineArrow;
	@FXML private Glyph iconJailYearsArrow;
	@FXML private Glyph iconJailMonthsArrow;
	@FXML private Glyph iconJailDaysArrow;
	@FXML private Glyph iconTravelBanYearsArrow;
	@FXML private Glyph iconTravelBanMonthsArrow;
	@FXML private Glyph iconTravelBanDaysArrow;
	@FXML private Glyph iconExilingYearsArrow;
	@FXML private Glyph iconExilingMonthsArrow;
	@FXML private Glyph iconExilingDaysArrow;
	@FXML private Glyph iconDeportationYearsArrow;
	@FXML private Glyph iconDeportationMonthsArrow;
	@FXML private Glyph iconDeportationDaysArrow;
	@FXML private Glyph iconFinalDeportationArrow;
	@FXML private Glyph iconLibelArrow;
	@FXML private Glyph iconCovenantArrow;
	@FXML private Glyph iconOtherArrow;
	@FXML private Label lblTazeerLashesOldValue;
	@FXML private Label lblTazeerLashesNewValue;
	@FXML private Label lblHadLashesOldValue;
	@FXML private Label lblHadLashesNewValue;
	@FXML private Label lblFineOldValue;
	@FXML private Label lblFineNewValue;
	@FXML private Label lblJailYearsOldValue;
	@FXML private Label lblJailYearsNewValue;
	@FXML private Label lblJailMonthsOldValue;
	@FXML private Label lblJailMonthsNewValue;
	@FXML private Label lblJailDaysOldValue;
	@FXML private Label lblJailDaysNewValue;
	@FXML private Label lblTravelBanYearsOldValue;
	@FXML private Label lblTravelBanYearsNewValue;
	@FXML private Label lblTravelBanMonthsOldValue;
	@FXML private Label lblTravelBanMonthsNewValue;
	@FXML private Label lblTravelBanDaysOldValue;
	@FXML private Label lblTravelBanDaysNewValue;
	@FXML private Label lblExilingYearsOldValue;
	@FXML private Label lblExilingYearsNewValue;
	@FXML private Label lblExilingMonthsOldValue;
	@FXML private Label lblExilingMonthsNewValue;
	@FXML private Label lblExilingDaysOldValue;
	@FXML private Label lblExilingDaysNewValue;
	@FXML private Label lblDeportationYearsOldValue;
	@FXML private Label lblDeportationYearsNewValue;
	@FXML private Label lblDeportationMonthsOldValue;
	@FXML private Label lblDeportationMonthsNewValue;
	@FXML private Label lblDeportationDaysOldValue;
	@FXML private Label lblDeportationDaysNewValue;
	@FXML private Label lblOtherOldValue;
	@FXML private Label lblOtherNewValue;
	@FXML private Button btnTazeerLashesReset;
	@FXML private Button btnHadLashesReset;
	@FXML private Button btnFineReset;
	@FXML private Button btnJailYearsReset;
	@FXML private Button btnJailMonthsReset;
	@FXML private Button btnJailDaysReset;
	@FXML private Button btnTravelBanYearsReset;
	@FXML private Button btnTravelBanMonthsReset;
	@FXML private Button btnTravelBanDaysReset;
	@FXML private Button btnExilingYearsReset;
	@FXML private Button btnExilingMonthsReset;
	@FXML private Button btnExilingDaysReset;
	@FXML private Button btnDeportationYearsReset;
	@FXML private Button btnDeportationMonthsReset;
	@FXML private Button btnDeportationDaysReset;
	@FXML private Button btnFinalDeportationReset;
	@FXML private Button btnLibelReset;
	@FXML private Button btnCovenantReset;
	@FXML private Button btnOtherReset;
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
		
		tazeerLashesOldValue = judgementInfo.getJudgTazeerLashesCount();
		hadLashesOldValue = judgementInfo.getJudgHadLashesCount();
		fineOldValue = judgementInfo.getJudgFine();
		jailYearsOldValue = judgementInfo.getJailYearCount();
		jailMonthsOldValue = judgementInfo.getJailMonthCount();
		jailDaysOldValue = judgementInfo.getJailDayCount();
		travelBanYearsOldValue = judgementInfo.getTrvlBanYearCount();
		travelBanMonthsOldValue = judgementInfo.getTrvlBanMonthCount();
		travelBanDaysOldValue = judgementInfo.getTrvlBanDayCount();
		exilingYearsOldValue = judgementInfo.getExileYearCount();
		exilingMonthsOldValue = judgementInfo.getExileMonthCount();
		exilingDaysOldValue = judgementInfo.getExileDayCount();
		deportationYearsOldValue = judgementInfo.getDeportYearCount();
		deportationMonthsOldValue = judgementInfo.getDeportMonthCount();
		deportationDaysOldValue = judgementInfo.getDeportDayCount();
		finalDeportationOldValue = judgementInfo.isFinalDeport();
		libelOldValue = judgementInfo.isLibel();
		covenantOldValue = judgementInfo.isCovenant();
		otherOldValue = judgementInfo.getJudgOthers();
		
		Integer tazeerLashes = null;
		Integer hadLashes = null;
		Integer fine = null;
		Integer jailYears = null;
		Integer jailMonths = null;
		Integer jailDays = null;
		Integer travelBanYears = null;
		Integer travelBanMonths = null;
		Integer travelBanDays = null;
		Integer exilingYears = null;
		Integer exilingMonths = null;
		Integer exilingDays = null;
		Integer deportationYears = null;
		Integer deportationMonths = null;
		Integer deportationDays = null;
		Boolean finalDeportation = null;
		Boolean libel = null;
		Boolean covenant = null;
		String other = null;
		
		if(isFirstLoad())
		{
			tazeerLashes = tazeerLashesOldValue;
			hadLashes = hadLashesOldValue;
			fine = fineOldValue;
			jailYears = jailYearsOldValue;
			jailMonths = jailMonthsOldValue;
			jailDays = jailDaysOldValue;
			travelBanYears = travelBanYearsOldValue;
			travelBanMonths = travelBanMonthsOldValue;
			travelBanDays = travelBanDaysOldValue;
			exilingYears = exilingYearsOldValue;
			exilingMonths = exilingMonthsOldValue;
			exilingDays = exilingDaysOldValue;
			deportationYears = deportationYearsOldValue;
			deportationMonths = deportationMonthsOldValue;
			deportationDays = deportationDaysOldValue;
			finalDeportation = finalDeportationOldValue;
			libel = libelOldValue;
			covenant = covenantOldValue;
			other = otherOldValue;
		}
		
		if(tazeerLashes != null) spnTazeerLashes.getValueFactory().setValue(tazeerLashes);
		else if(this.tazeerLashesNewValue != null) spnTazeerLashes.getValueFactory().setValue(
																							this.tazeerLashesNewValue);
		
		if(hadLashes != null) spnHadLashes.getValueFactory().setValue(hadLashes);
		else if(this.hadLashesNewValue != null) spnHadLashes.getValueFactory().setValue(this.hadLashesNewValue);
		
		if(fine != null) spnFine.getValueFactory().setValue(fine);
		else if(this.fineNewValue != null) spnFine.getValueFactory().setValue(this.fineNewValue);
		
		if(jailYears != null) spnJailYears.getValueFactory().setValue(jailYears);
		else if(this.jailYearsNewValue != null) spnJailYears.getValueFactory().setValue(this.jailYearsNewValue);
		
		if(jailMonths != null) spnJailMonths.getValueFactory().setValue(jailMonths);
		else if(this.jailMonthsNewValue != null) spnJailMonths.getValueFactory().setValue(this.jailMonthsNewValue);
		
		if(jailDays != null) spnJailDays.getValueFactory().setValue(jailDays);
		else if(this.jailDaysNewValue != null) spnJailDays.getValueFactory().setValue(this.jailDaysNewValue);
		
		if(travelBanYears != null) spnTravelBanYears.getValueFactory().setValue(travelBanYears);
		else if(this.travelBanYearsNewValue != null) spnTravelBanYears.getValueFactory().setValue(
																						this.travelBanYearsNewValue);
		
		if(travelBanMonths != null) spnTravelBanMonths.getValueFactory().setValue(travelBanMonths);
		else if(this.travelBanMonthsNewValue != null) spnTravelBanMonths.getValueFactory().setValue(
																						this.travelBanMonthsNewValue);
		
		if(travelBanDays != null) spnTravelBanDays.getValueFactory().setValue(travelBanDays);
		else if(this.travelBanDaysNewValue != null) spnTravelBanDays.getValueFactory().setValue(
																						this.travelBanDaysNewValue);
		
		if(exilingYears != null) spnExilingYears.getValueFactory().setValue(exilingYears);
		else if(this.exilingYearsNewValue != null) spnExilingYears.getValueFactory().setValue(
																						this.exilingYearsNewValue);
		
		if(exilingMonths != null) spnExilingMonths.getValueFactory().setValue(exilingMonths);
		else if(this.exilingMonthsNewValue != null) spnExilingMonths.getValueFactory().setValue(
																						this.exilingMonthsNewValue);
		
		if(exilingDays != null) spnExilingDays.getValueFactory().setValue(exilingDays);
		else if(this.exilingDaysNewValue != null) spnExilingDays.getValueFactory().setValue(this.exilingDaysNewValue);
		
		if(deportationYears != null) spnDeportationYears.getValueFactory().setValue(deportationYears);
		else if(this.deportationYearsNewValue != null) spnDeportationYears.getValueFactory().setValue(
																						this.deportationYearsNewValue);
		
		if(deportationMonths != null) spnDeportationMonths.getValueFactory().setValue(deportationMonths);
		else if(this.deportationMonthsNewValue != null) spnDeportationMonths.getValueFactory().setValue(
																						this.deportationMonthsNewValue);
		
		if(deportationDays != null) spnDeportationDays.getValueFactory().setValue(deportationDays);
		else if(this.deportationDaysNewValue != null) spnDeportationDays.getValueFactory().setValue(
																						this.deportationDaysNewValue);
		
		if(finalDeportation != null) cbFinalDeportation.setSelected(finalDeportation);
		else if(this.finalDeportationNewValue != null) cbFinalDeportation.setSelected(this.finalDeportationNewValue);
		
		if(libel != null) cbLibel.setSelected(libel);
		else if(this.libelNewValue != null) cbLibel.setSelected(this.libelNewValue);
		
		if(covenant != null) cbCovenant.setSelected(covenant);
		else if(this.covenantNewValue != null) cbCovenant.setSelected(this.covenantNewValue);
		
		if(other != null) txtOther.setText(other);
		else if(this.otherNewValue != null) txtOther.setText(this.otherNewValue);
		
		if(btnNext.isDisabled()) spnTazeerLashes.requestFocus();
		else btnNext.requestFocus();
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		FontAwesome.Glyph arrowIcon = rtl ? FontAwesome.Glyph.LONG_ARROW_LEFT : FontAwesome.Glyph.LONG_ARROW_RIGHT;
		iconTazeerLashesArrow.setIcon(arrowIcon);
		iconHadLashesArrow.setIcon(arrowIcon);
		iconFineArrow.setIcon(arrowIcon);
		iconJailYearsArrow.setIcon(arrowIcon);
		iconJailMonthsArrow.setIcon(arrowIcon);
		iconJailDaysArrow.setIcon(arrowIcon);
		iconTravelBanYearsArrow.setIcon(arrowIcon);
		iconTravelBanMonthsArrow.setIcon(arrowIcon);
		iconTravelBanDaysArrow.setIcon(arrowIcon);
		iconExilingYearsArrow.setIcon(arrowIcon);
		iconExilingMonthsArrow.setIcon(arrowIcon);
		iconExilingDaysArrow.setIcon(arrowIcon);
		iconDeportationYearsArrow.setIcon(arrowIcon);
		iconDeportationMonthsArrow.setIcon(arrowIcon);
		iconDeportationDaysArrow.setIcon(arrowIcon);
		iconFinalDeportationArrow.setIcon(arrowIcon);
		iconLibelArrow.setIcon(arrowIcon);
		iconCovenantArrow.setIcon(arrowIcon);
		iconOtherArrow.setIcon(arrowIcon);
		
		paneTazeerLashesReset.visibleProperty().bind(
			Bindings.notEqual(lblTazeerLashesOldValue.textProperty(), lblTazeerLashesNewValue.textProperty()));
		paneHadLashesReset.visibleProperty().bind(
			Bindings.notEqual(lblHadLashesOldValue.textProperty(), lblHadLashesNewValue.textProperty()));
		paneFineReset.visibleProperty().bind(
			Bindings.notEqual(lblFineOldValue.textProperty(), lblFineNewValue.textProperty()));
		paneJailYearsReset.visibleProperty().bind(
			Bindings.notEqual(lblJailYearsOldValue.textProperty(), lblJailYearsNewValue.textProperty()));
		paneJailMonthsReset.visibleProperty().bind(
			Bindings.notEqual(lblJailMonthsOldValue.textProperty(), lblJailMonthsNewValue.textProperty()));
		paneJailDaysReset.visibleProperty().bind(
			Bindings.notEqual(lblJailDaysOldValue.textProperty(), lblJailDaysNewValue.textProperty()));
		paneTravelBanYearsReset.visibleProperty().bind(
			Bindings.notEqual(lblTravelBanYearsOldValue.textProperty(), lblTravelBanYearsNewValue.textProperty()));
		paneTravelBanMonthsReset.visibleProperty().bind(
			Bindings.notEqual(lblTravelBanMonthsOldValue.textProperty(), lblTravelBanMonthsNewValue.textProperty()));
		paneTravelBanDaysReset.visibleProperty().bind(
			Bindings.notEqual(lblTravelBanDaysOldValue.textProperty(), lblTravelBanDaysNewValue.textProperty()));
		paneExilingYearsReset.visibleProperty().bind(
			Bindings.notEqual(lblExilingYearsOldValue.textProperty(), lblExilingYearsNewValue.textProperty()));
		paneExilingMonthsReset.visibleProperty().bind(
			Bindings.notEqual(lblExilingMonthsOldValue.textProperty(), lblExilingMonthsNewValue.textProperty()));
		paneExilingDaysReset.visibleProperty().bind(
			Bindings.notEqual(lblExilingDaysOldValue.textProperty(), lblExilingDaysNewValue.textProperty()));
		paneDeportationYearsReset.visibleProperty().bind(
			Bindings.notEqual(lblDeportationYearsOldValue.textProperty(), lblDeportationYearsNewValue.textProperty()));
		paneDeportationMonthsReset.visibleProperty().bind(
	        Bindings.notEqual(lblDeportationMonthsOldValue.textProperty(),
	                          lblDeportationMonthsNewValue.textProperty()));
		paneDeportationDaysReset.visibleProperty().bind(
			Bindings.notEqual(lblDeportationDaysOldValue.textProperty(), lblDeportationDaysNewValue.textProperty()));
		paneFinalDeportationReset.visibleProperty().bind(
			Bindings.notEqual(cbFinalDeportationOldValue.selectedProperty(),
			                  cbFinalDeportationNewValue.selectedProperty()));
		paneLibelReset.visibleProperty().bind(
			Bindings.notEqual(cbLibelOldValue.selectedProperty(), cbLibelNewValue.selectedProperty()));
		paneCovenantReset.visibleProperty().bind(
			Bindings.notEqual(cbCovenantOldValue.selectedProperty(), cbCovenantNewValue.selectedProperty()));
		paneOtherReset.visibleProperty().bind(
			Bindings.notEqual(lblOtherOldValue.textProperty(), lblOtherNewValue.textProperty()));
		
		lblTazeerLashesOldValue.setText(String.valueOf(judgementInfo.getJudgTazeerLashesCount()));
		lblHadLashesOldValue.setText(String.valueOf(judgementInfo.getJudgHadLashesCount()));
		lblFineOldValue.setText(String.valueOf(judgementInfo.getJudgFine()));
		lblJailYearsOldValue.setText(String.valueOf(judgementInfo.getJailYearCount()));
		lblJailMonthsOldValue.setText(String.valueOf(judgementInfo.getJailMonthCount()));
		lblJailDaysOldValue.setText(String.valueOf(judgementInfo.getJailDayCount()));
		lblTravelBanYearsOldValue.setText(String.valueOf(judgementInfo.getTrvlBanYearCount()));
		lblTravelBanMonthsOldValue.setText(String.valueOf(judgementInfo.getTrvlBanMonthCount()));
		lblTravelBanDaysOldValue.setText(String.valueOf(judgementInfo.getTrvlBanDayCount()));
		lblExilingYearsOldValue.setText(String.valueOf(judgementInfo.getExileYearCount()));
		lblExilingMonthsOldValue.setText(String.valueOf(judgementInfo.getExileMonthCount()));
		lblExilingDaysOldValue.setText(String.valueOf(judgementInfo.getExileDayCount()));
		lblDeportationYearsOldValue.setText(String.valueOf(judgementInfo.getDeportYearCount()));
		lblDeportationMonthsOldValue.setText(String.valueOf(judgementInfo.getDeportMonthCount()));
		lblDeportationDaysOldValue.setText(String.valueOf(judgementInfo.getDeportDayCount()));
		cbFinalDeportationOldValue.setSelected(judgementInfo.isFinalDeport());
		cbLibelOldValue.setSelected(judgementInfo.isLibel());
		cbCovenantOldValue.setSelected(judgementInfo.isCovenant());
		lblOtherOldValue.setText(judgementInfo.getJudgOthers() != null ? judgementInfo.getJudgOthers() : "");
		
		btnTazeerLashesReset.setOnAction(actionEvent ->
		{
			spnTazeerLashes.getValueFactory().setValue(judgementInfo.getJudgTazeerLashesCount());
		    panePunishmentDetails.requestFocus();
		});
		btnHadLashesReset.setOnAction(actionEvent ->
		{
			spnHadLashes.getValueFactory().setValue(judgementInfo.getJudgHadLashesCount());
		    panePunishmentDetails.requestFocus();
		});
		btnFineReset.setOnAction(actionEvent ->
		{
			spnFine.getValueFactory().setValue(judgementInfo.getJudgFine());
		    panePunishmentDetails.requestFocus();
		});
		btnJailYearsReset.setOnAction(actionEvent ->
		{
			spnJailYears.getValueFactory().setValue(judgementInfo.getJailYearCount());
		    panePunishmentDetails.requestFocus();
		});
		btnJailMonthsReset.setOnAction(actionEvent ->
		{
			spnJailMonths.getValueFactory().setValue(judgementInfo.getJailMonthCount());
		    panePunishmentDetails.requestFocus();
		});
		btnJailDaysReset.setOnAction(actionEvent ->
		{
			spnJailDays.getValueFactory().setValue(judgementInfo.getJailDayCount());
		    panePunishmentDetails.requestFocus();
		});
		btnTravelBanYearsReset.setOnAction(actionEvent ->
		{
			spnTravelBanYears.getValueFactory().setValue(judgementInfo.getTrvlBanYearCount());
		    panePunishmentDetails.requestFocus();
		});
		btnTravelBanMonthsReset.setOnAction(actionEvent ->
		{
			spnTravelBanMonths.getValueFactory().setValue(judgementInfo.getTrvlBanMonthCount());
		    panePunishmentDetails.requestFocus();
		});
		btnTravelBanDaysReset.setOnAction(actionEvent ->
		{
			spnTravelBanDays.getValueFactory().setValue(judgementInfo.getTrvlBanDayCount());
		    panePunishmentDetails.requestFocus();
		});
		btnExilingYearsReset.setOnAction(actionEvent ->
		{
			spnExilingYears.getValueFactory().setValue(judgementInfo.getExileYearCount());
		    panePunishmentDetails.requestFocus();
		});
		btnExilingMonthsReset.setOnAction(actionEvent ->
		{
			spnExilingMonths.getValueFactory().setValue(judgementInfo.getExileMonthCount());
		    panePunishmentDetails.requestFocus();
		});
		btnExilingDaysReset.setOnAction(actionEvent ->
		{
			spnExilingDays.getValueFactory().setValue(judgementInfo.getExileDayCount());
		    panePunishmentDetails.requestFocus();
		});
		btnDeportationYearsReset.setOnAction(actionEvent ->
		{
			spnDeportationYears.getValueFactory().setValue(judgementInfo.getDeportYearCount());
		    panePunishmentDetails.requestFocus();
		});
		btnDeportationMonthsReset.setOnAction(actionEvent ->
		{
			spnDeportationMonths.getValueFactory().setValue(judgementInfo.getDeportMonthCount());
		    panePunishmentDetails.requestFocus();
		});
		btnDeportationDaysReset.setOnAction(actionEvent ->
		{
			spnDeportationDays.getValueFactory().setValue(judgementInfo.getDeportDayCount());
		    panePunishmentDetails.requestFocus();
		});
		btnFinalDeportationReset.setOnAction(actionEvent ->
		{
			cbFinalDeportation.setSelected(judgementInfo.isFinalDeport());
		    panePunishmentDetails.requestFocus();
		});
		btnLibelReset.setOnAction(actionEvent ->
		{
			cbLibel.setSelected(judgementInfo.isLibel());
		    panePunishmentDetails.requestFocus();
		});
		btnCovenantReset.setOnAction(actionEvent ->
		{
			cbCovenant.setSelected(judgementInfo.isCovenant());
		    panePunishmentDetails.requestFocus();
		});
		btnOtherReset.setOnAction(actionEvent ->
		{
			txtOther.setText(judgementInfo.getJudgOthers() != null ? judgementInfo.getJudgOthers() : "");
		    panePunishmentDetails.requestFocus();
		});
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		tazeerLashesNewValue = spnTazeerLashes.getValue();
		hadLashesNewValue = spnHadLashes.getValue();
		fineNewValue = spnFine.getValue();
		jailYearsNewValue = spnJailYears.getValue();
		jailMonthsNewValue = spnJailMonths.getValue();
		jailDaysNewValue = spnJailDays.getValue();
		travelBanYearsNewValue = spnTravelBanYears.getValue();
		travelBanMonthsNewValue = spnTravelBanMonths.getValue();
		travelBanDaysNewValue = spnTravelBanDays.getValue();
		exilingYearsNewValue = spnExilingYears.getValue();
		exilingMonthsNewValue = spnExilingMonths.getValue();
		exilingDaysNewValue = spnExilingDays.getValue();
		deportationYearsNewValue = spnDeportationYears.getValue();
		deportationMonthsNewValue = spnDeportationMonths.getValue();
		deportationDaysNewValue = spnDeportationDays.getValue();
		finalDeportationNewValue = cbFinalDeportation.isSelected();
		libelNewValue = cbLibel.isSelected();
		covenantNewValue = cbCovenant.isSelected();
		
		var other = txtOther.getText();
		if(!other.isBlank()) this.otherNewValue = other;
		else this.otherNewValue = null;
	}
}