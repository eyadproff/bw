package sa.gov.nic.bio.bw.workflow.editconvictedreport.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.ComboBoxItem;
import sa.gov.nic.bio.bw.core.controllers.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.core.utils.AppUtils;
import sa.gov.nic.bio.bw.core.utils.FxmlFile;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.commons.beans.JudgementInfo;
import sa.gov.nic.bio.bw.workflow.commons.lookups.CrimeTypesLookup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FxmlFile("editJudgmentDetails.fxml")
public class EditJudgmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	@Input(alwaysRequired = true) private List<CrimeCode> oldCrimes;
	@Input(alwaysRequired = true) private JudgementInfo judgementInfo;
	@Output private String judgmentIssuerOldValue;
	@Output private String judgmentIssuerNewValue;
	@Output private String judgmentNumberOldValue;
	@Output private String judgmentNumberNewValue;
	@Output private LocalDate judgmentDateOldValue;
	@Output private LocalDate judgmentDateNewValue;
	@Output private Boolean judgmentDateUseHijri;
	@Output private String caseFileNumberOldValue;
	@Output private String caseFileNumberNewValue;
	@Output private Long prisonerNumberOldValue;
	@Output private Long prisonerNumberNewValue;
	@Output private LocalDate arrestDateOldValue;
	@Output private LocalDate arrestDateNewValue;
	@Output private Boolean arrestDateUseHijri;
	@Output private List<CrimeCode> newCrimes;
	
	@FXML private Pane paneJudgmentDetails;
	@FXML private Pane paneCrimeContainer;
	@FXML private Pane paneCrime2;
	@FXML private Pane paneCrime3;
	@FXML private Pane paneCrime4;
	@FXML private Pane paneCrime5;
	@FXML private Pane paneJudgmentIssuerReset;
	@FXML private Pane paneJudgmentNumberReset;
	@FXML private Pane paneJudgmentDateReset;
	@FXML private Pane paneCaseFileNumberReset;
	@FXML private Pane panePrisonerNumberReset;
	@FXML private Pane paneArrestDateReset;
	@FXML private Pane paneCrimeDiffContainer;
	@FXML private Pane paneOldCrimes;
	@FXML private Pane paneNewCrimes;
	@FXML private TextField txtJudgmentIssuer;
	@FXML private TextField txtJudgmentNumber;
	@FXML private TextField txtCaseFileNumber;
	@FXML private TextField txtPrisonerNumber;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeEvent1;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeClass1;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeEvent2;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeClass2;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeEvent3;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeClass3;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeEvent4;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeClass4;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeEvent5;
	@FXML private ComboBox<ComboBoxItem<Integer>> cboCrimeClass5;
	@FXML private RadioButton rdoArrestDateUseHijri;
	@FXML private RadioButton rdoArrestDateUseGregorian;
	@FXML private RadioButton rdoJudgmentDateUseHijri;
	@FXML private RadioButton rdoJudgmentDateUseGregorian;
	@FXML private DatePicker dpArrestDate;
	@FXML private DatePicker dpJudgmentDate;
	@FXML private Label lblJudgmentIssuerOldValue;
	@FXML private Label lblJudgmentIssuerNewValue;
	@FXML private Label lblJudgmentNumberOldValue;
	@FXML private Label lblJudgmentNumberNewValue;
	@FXML private Label lblJudgmentDateOldValue;
	@FXML private Label lblJudgmentDateNewValue;
	@FXML private Label lblCaseFileNumberOldValue;
	@FXML private Label lblCaseFileNumberNewValue;
	@FXML private Label lblPrisonerNumberOldValue;
	@FXML private Label lblPrisonerNumberNewValue;
	@FXML private Label lblArrestDateOldValue;
	@FXML private Label lblArrestDateNewValue;
	@FXML private Label lblCrime1NewValue;
	@FXML private Label lblCrime2NewValue;
	@FXML private Label lblCrime3NewValue;
	@FXML private Label lblCrime4NewValue;
	@FXML private Label lblCrime5NewValue;
	@FXML private Glyph iconJudgmentIssuerArrow;
	@FXML private Glyph iconJudgmentNumberArrow;
	@FXML private Glyph iconJudgmentDateArrow;
	@FXML private Glyph iconCaseFileNumberArrow;
	@FXML private Glyph iconPrisonerNumberArrow;
	@FXML private Glyph iconArrestDateArrow;
	@FXML private Glyph iconCrimeDiff;
	@FXML private Button btnJudgmentIssuerReset;
	@FXML private Button btnJudgmentNumberReset;
	@FXML private Button btnJudgmentDateReset;
	@FXML private Button btnCaseFileNumberReset;
	@FXML private Button btnPrisonerNumberReset;
	@FXML private Button btnArrestDateReset;
	@FXML private Button btnHidePaneCrime2;
	@FXML private Button btnHidePaneCrime3;
	@FXML private Button btnHidePaneCrime4;
	@FXML private Button btnHidePaneCrime5;
	@FXML private Button btnCrimesReset;
	@FXML private Button btnAddMore;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	private Map<Integer, List<Integer>> crimeClasses = new HashMap<>();
	private Map<Integer, Pane> cboCrimePaneMap = new HashMap<>();
	private Map<Integer, ComboBox<ComboBoxItem<Integer>>> cboCrimeEventMap = new HashMap<>();
	private Map<Integer, ComboBox<ComboBoxItem<Integer>>> cboCrimeClassMap = new HashMap<>();
	private Map<Integer, Button> btnHidePaneCrimeMap = new HashMap<>();
	private BooleanProperty crimeTypeDuplicated = new SimpleBooleanProperty();
	private int visibleCrimeTypesCount = 1;
	
	private Runnable checkForCrimeDuplicates = () ->
	{
		List<CrimeCode> crimeCodes = new ArrayList<>();
		
		crimeCodes.add(new CrimeCode(cboCrimeEvent1.getSelectionModel().getSelectedIndex(),
		                             cboCrimeClass1.getSelectionModel().getSelectedIndex()));
		
		for(int i = 1; i <= 4; i++)
		{
			if(cboCrimePaneMap.get(i).isVisible())
			{
				CrimeCode crimeCode = new CrimeCode(cboCrimeEventMap.get(i).getSelectionModel().getSelectedIndex(),
				                                    cboCrimeClassMap.get(i).getSelectionModel().getSelectedIndex());
				
				if(crimeCodes.contains(crimeCode))
				{
					crimeTypeDuplicated.set(true);
					return;
				}
				else crimeCodes.add(crimeCode);
			}
		}
		
		crimeTypeDuplicated.set(false);
	};
	
	@SuppressWarnings("unchecked")
	private ChangeListener<Boolean>[] showingPropertyChangeListenerReference = new ChangeListener[]{null};
	@SuppressWarnings("unchecked")
	private ChangeListener<String>[] textPropertyChangeListenerReference = new ChangeListener[]{null};
	
	@Override
	protected void onAttachedToScene()
	{
		Glyph plusIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.PLUS);
		btnAddMore.setGraphic(plusIcon);
		
		Glyph minusIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.MINUS);
		btnHidePaneCrime2.setGraphic(minusIcon);
		minusIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.MINUS);
		btnHidePaneCrime3.setGraphic(minusIcon);
		minusIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.MINUS);
		btnHidePaneCrime4.setGraphic(minusIcon);
		minusIcon = AppUtils.createFontAwesomeIcon(FontAwesome.Glyph.MINUS);
		btnHidePaneCrime5.setGraphic(minusIcon);
		
		btnHidePaneCrime2.visibleProperty().bind(paneCrime2.visibleProperty());
		btnHidePaneCrime3.visibleProperty().bind(paneCrime3.visibleProperty());
		btnHidePaneCrime4.visibleProperty().bind(paneCrime4.visibleProperty());
		btnHidePaneCrime5.visibleProperty().bind(paneCrime5.visibleProperty());
		
		btnHidePaneCrime2.managedProperty().bind(paneCrime2.managedProperty());
		btnHidePaneCrime3.managedProperty().bind(paneCrime3.managedProperty());
		btnHidePaneCrime4.managedProperty().bind(paneCrime4.managedProperty());
		btnHidePaneCrime5.managedProperty().bind(paneCrime5.managedProperty());
		
		btnAddMore.visibleProperty().bind(paneCrime2.visibleProperty().not().or(paneCrime3.visibleProperty().not())
	                                  .or(paneCrime4.visibleProperty().not()).or(paneCrime5.visibleProperty().not()));
		btnAddMore.managedProperty().bind(paneCrime2.managedProperty().not().or(paneCrime3.managedProperty().not())
	                                  .or(paneCrime4.managedProperty().not()).or(paneCrime5.managedProperty().not()));
		
		@SuppressWarnings("unchecked")
		List<CrimeType> crimeTypes = (List<CrimeType>) Context.getUserSession().getAttribute(CrimeTypesLookup.KEY);
		
		Map<Integer, String> crimeEventTitles = crimeTypes.stream().collect(
									Collectors.toMap(CrimeType::getEventCode, CrimeType::getEventDesc, (k1, k2) -> k1));
		Map<Integer, String> crimeClassTitles = crimeTypes.stream().collect(
									Collectors.toMap(CrimeType::getClassCode, CrimeType::getClassDesc, (k1, k2) -> k1));
		crimeClasses = crimeTypes.stream().collect(Collectors.groupingBy(CrimeType::getEventCode,
		                                                                 Collectors.mapping(CrimeType::getClassCode,
		                                                                                    Collectors.toList())));
		
		List<Integer> crimeEventCodes = List.copyOf(crimeEventTitles.keySet());
		
		GuiUtils.initDatePicker(rdoArrestDateUseHijri, dpArrestDate, null);
		GuiUtils.initDatePicker(rdoJudgmentDateUseHijri, dpJudgmentDate, null);
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent1, crimeEventCodes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent2, crimeEventCodes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent3, crimeEventCodes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent4, crimeEventCodes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent5, crimeEventCodes);
		
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeEvent1);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeClass1);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeEvent2);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeClass2);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeEvent3);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeClass3);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeEvent4);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeClass4);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeEvent5);
		GuiUtils.makeComboBoxOpenableByPressingEnter(cboCrimeClass5);
		
		GuiUtils.applyValidatorToTextField(txtCaseFileNumber, null, null, 20);
		GuiUtils.applyValidatorToTextField(txtJudgmentNumber, null, null, 20);
		GuiUtils.applyValidatorToTextField(txtJudgmentIssuer, null, null, 30);
		GuiUtils.applyValidatorToTextField(txtPrisonerNumber, "\\d*", "[^\\d]",
		                                   12);
		
		cboCrimePaneMap.put(1, paneCrime2);
		cboCrimePaneMap.put(2, paneCrime3);
		cboCrimePaneMap.put(3, paneCrime4);
		cboCrimePaneMap.put(4, paneCrime5);
		
		cboCrimeEventMap.put(0, cboCrimeEvent1);
		cboCrimeEventMap.put(1, cboCrimeEvent2);
		cboCrimeEventMap.put(2, cboCrimeEvent3);
		cboCrimeEventMap.put(3, cboCrimeEvent4);
		cboCrimeEventMap.put(4, cboCrimeEvent5);
		
		cboCrimeClassMap.put(0, cboCrimeClass1);
		cboCrimeClassMap.put(1, cboCrimeClass2);
		cboCrimeClassMap.put(2, cboCrimeClass3);
		cboCrimeClassMap.put(3, cboCrimeClass4);
		cboCrimeClassMap.put(4, cboCrimeClass5);
		
		btnHidePaneCrimeMap.put(1, btnHidePaneCrime2);
		btnHidePaneCrimeMap.put(2, btnHidePaneCrime3);
		btnHidePaneCrimeMap.put(3, btnHidePaneCrime4);
		btnHidePaneCrimeMap.put(4, btnHidePaneCrime5);
		
		BooleanBinding txtJudgmentIssuerEmptyBinding = txtJudgmentIssuer.textProperty().isEmpty();
		BooleanBinding txtJudgmentNumberEmptyBinding = txtJudgmentNumber.textProperty().isEmpty();
		BooleanBinding dpJudgmentDateEmptyBinding = dpJudgmentDate.valueProperty().isNull();
		
		// prevent duplicates
		
		ChangeListener<Number> changeListener = (observable, oldValue, newValue) -> checkForCrimeDuplicates.run();
		
		cboCrimeEvent1.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass1.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeEvent2.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass2.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeEvent3.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass3.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeEvent4.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass4.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeEvent5.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass5.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass5.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		cboCrimeClass5.getSelectionModel().selectedIndexProperty().addListener(changeListener);
		
		btnAddMore.setOnAction(actionEvent ->
		{
		    List<Node> children = paneCrimeContainer.getChildren();
		
		    for(int i = 1; i < children.size(); i++)
		    {
		        Node child = children.get(i);
		
		        if(!child.isVisible())
		        {
		            GuiUtils.showNode(child, true);
		            visibleCrimeTypesCount++;
			        checkForCrimeDuplicates.run();
		            break;
		        }
		    }
		});
		
		for(int i = 1; i <= 4; i++)
		{
			final int finalI = i;
			btnHidePaneCrimeMap.get(i).setOnAction(actionEvent ->
			{
			    GuiUtils.showNode(cboCrimePaneMap.get(finalI), false);
			    paneCrimeContainer.getChildren().remove(cboCrimePaneMap.get(finalI));
			    paneCrimeContainer.getChildren().add(visibleCrimeTypesCount-- - 1, cboCrimePaneMap.get(finalI));
			    checkForCrimeDuplicates.run();
			});
		}
		
		btnNext.disableProperty().bind(crimeTypeDuplicated.or(txtJudgmentIssuerEmptyBinding)
				                                          .or(txtJudgmentNumberEmptyBinding)
				                                          .or(dpJudgmentDateEmptyBinding));
		
		initCrimeEventComboBox(cboCrimeEvent1, cboCrimeClass1, crimeEventTitles, crimeClassTitles);
		initCrimeEventComboBox(cboCrimeEvent2, cboCrimeClass2, crimeEventTitles, crimeClassTitles);
		initCrimeEventComboBox(cboCrimeEvent3, cboCrimeClass3, crimeEventTitles, crimeClassTitles);
		initCrimeEventComboBox(cboCrimeEvent4, cboCrimeClass4, crimeEventTitles, crimeClassTitles);
		initCrimeEventComboBox(cboCrimeEvent5, cboCrimeClass5, crimeEventTitles, crimeClassTitles);
		
		judgmentIssuerOldValue = judgementInfo.getJudgIssuer();
		judgmentNumberOldValue = judgementInfo.getJudgNum();
		Long judgmentDateLong = judgementInfo.getJudgDate();
		if(judgmentDateLong != null) judgmentDateOldValue = AppUtils.secondsToGregorianDate(judgmentDateLong);
		caseFileNumberOldValue = judgementInfo.getPoliceFileNum();
		prisonerNumberOldValue = judgementInfo.getPrisonerNumber();
		Long arrestDateLong = judgementInfo.getArrestDate();
		if(arrestDateLong != null) arrestDateOldValue = AppUtils.secondsToGregorianDate(arrestDateLong);
		
		Node focusedNode = null;
		String judgmentIssuer = null;
		String judgmentNumber = null;
		LocalDate judgmentDate = null;
		String caseFileNumber = null;
		Long prisonerNumber = null;
		LocalDate arrestDate = null;
		List<CrimeCode> crimes = null;
		
		if(isFirstLoad())
		{
			judgmentIssuer = judgmentIssuerOldValue;
			judgmentNumber = judgmentNumberOldValue;
			judgmentDate = judgmentDateOldValue;
			caseFileNumber = caseFileNumberOldValue;
			prisonerNumber = prisonerNumberOldValue;
			arrestDate = arrestDateOldValue;
			crimes = oldCrimes;
		}
		
		if(judgmentIssuer != null) txtJudgmentIssuer.setText(judgmentIssuer);
		else if(this.judgmentIssuerNewValue != null) txtJudgmentIssuer.setText(this.judgmentIssuerNewValue);
		else focusedNode = txtJudgmentIssuer;
		
		if(judgmentNumber != null) txtJudgmentNumber.setText(judgmentNumber);
		else if(this.judgmentNumberNewValue != null) txtJudgmentNumber.setText(this.judgmentNumberNewValue);
		else if(focusedNode == null) focusedNode = txtJudgmentNumber;
		
		if(judgmentDate != null) dpJudgmentDate.setValue(judgmentDate);
		else if(this.judgmentDateNewValue != null) dpJudgmentDate.setValue(this.judgmentDateNewValue);
		else if(focusedNode == null) focusedNode = dpJudgmentDate;
		
		rdoJudgmentDateUseHijri.setSelected(true);
		if(this.judgmentDateUseHijri != null && !this.judgmentDateUseHijri)
																		rdoJudgmentDateUseGregorian.setSelected(true);
		
		if(caseFileNumber != null) txtCaseFileNumber.setText(caseFileNumber);
		else if(this.caseFileNumberNewValue != null) txtCaseFileNumber.setText(this.caseFileNumberNewValue);
		
		if(prisonerNumber != null) txtPrisonerNumber.setText(String.valueOf(prisonerNumber));
		else if(this.prisonerNumberNewValue != null) txtPrisonerNumber.setText(String.valueOf(
																						this.prisonerNumberNewValue));
		
		if(arrestDate != null) dpArrestDate.setValue(arrestDate);
		else if(this.arrestDateNewValue != null) dpArrestDate.setValue(this.arrestDateNewValue);
		
		rdoArrestDateUseHijri.setSelected(true);
		if(this.arrestDateUseHijri != null && !this.arrestDateUseHijri) rdoArrestDateUseGregorian.setSelected(true);
		
		if(crimes == null) crimes = newCrimes;
		
		for(int i = 0; i < crimes.size(); i++)
		{
			CrimeCode crimeCode = crimes.get(i);
			
			if(crimeCode != null)
			{
				if(i > 0) GuiUtils.showNode(cboCrimePaneMap.get(i), true);
				
				cboCrimeEventMap.get(i).getItems()
						.stream()
						.filter(item -> item.getItem().equals(crimeCode.getCrimeEvent()))
						.findFirst()
						.ifPresent(cboCrimeEventMap.get(i)::setValue);
				cboCrimeClassMap.get(i).getItems()
						.stream()
						.filter(item -> item.getItem().equals(crimeCode.getCrimeClass()))
						.findFirst()
						.ifPresent(cboCrimeClassMap.get(i)::setValue);
			}
		}
		
		if(newCrimes == null)
		{
			newCrimes = new ArrayList<>();
			updateNewCrimes();
		}
		
		checkForCrimeDuplicates.run();
		
		if(focusedNode != null) focusedNode.requestFocus();
		else btnNext.requestFocus();
		
		lblCrime1NewValue.textProperty().bind(Bindings.createStringBinding(() -> cboCrimeEvent1.getValue().getText() +
																		" - " + cboCrimeClass1.getValue().getText(),
                                                    cboCrimeEvent1.valueProperty(), cboCrimeClass1.valueProperty()));
		lblCrime2NewValue.textProperty().bind(Bindings.createStringBinding(() -> cboCrimeEvent2.getValue().getText() +
	                                                                    " - " + cboCrimeClass2.getValue().getText(),
                                                    cboCrimeEvent2.valueProperty(), cboCrimeClass2.valueProperty()));
		lblCrime3NewValue.textProperty().bind(Bindings.createStringBinding(() -> cboCrimeEvent3.getValue().getText() +
	                                                                    " - " + cboCrimeClass3.getValue().getText(),
                                                    cboCrimeEvent3.valueProperty(), cboCrimeClass3.valueProperty()));
		lblCrime4NewValue.textProperty().bind(Bindings.createStringBinding(() -> cboCrimeEvent4.getValue().getText() +
	                                                                    " - " + cboCrimeClass4.getValue().getText(),
                                                    cboCrimeEvent4.valueProperty(), cboCrimeClass4.valueProperty()));
		lblCrime5NewValue.textProperty().bind(Bindings.createStringBinding(() -> cboCrimeEvent5.getValue().getText() +
	                                                                    " - " + cboCrimeClass5.getValue().getText(),
                                                    cboCrimeEvent5.valueProperty(), cboCrimeClass5.valueProperty()));
		
		lblCrime2NewValue.visibleProperty().bind(paneCrime2.visibleProperty());
		lblCrime2NewValue.managedProperty().bind(paneCrime2.managedProperty());
		lblCrime3NewValue.visibleProperty().bind(paneCrime3.visibleProperty());
		lblCrime3NewValue.managedProperty().bind(paneCrime3.managedProperty());
		lblCrime4NewValue.visibleProperty().bind(paneCrime4.visibleProperty());
		lblCrime4NewValue.managedProperty().bind(paneCrime4.managedProperty());
		lblCrime5NewValue.visibleProperty().bind(paneCrime5.visibleProperty());
		lblCrime5NewValue.managedProperty().bind(paneCrime5.managedProperty());
		
		boolean rtl = Context.getGuiLanguage().getNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
		FontAwesome.Glyph arrowIcon = rtl ? FontAwesome.Glyph.LONG_ARROW_LEFT : FontAwesome.Glyph.LONG_ARROW_RIGHT;
		iconJudgmentIssuerArrow.setIcon(arrowIcon);
		iconJudgmentNumberArrow.setIcon(arrowIcon);
		iconJudgmentDateArrow.setIcon(arrowIcon);
		iconCaseFileNumberArrow.setIcon(arrowIcon);
		iconPrisonerNumberArrow.setIcon(arrowIcon);
		iconArrestDateArrow.setIcon(arrowIcon);
		iconCrimeDiff.setIcon(arrowIcon);
		
		paneJudgmentIssuerReset.visibleProperty().bind(
				Bindings.notEqual(lblJudgmentIssuerOldValue.textProperty(), lblJudgmentIssuerNewValue.textProperty()));
		paneJudgmentNumberReset.visibleProperty().bind(
				Bindings.notEqual(lblJudgmentNumberOldValue.textProperty(), lblJudgmentNumberNewValue.textProperty()));
		paneJudgmentDateReset.visibleProperty().bind(
				Bindings.notEqual(lblJudgmentDateOldValue.textProperty(), lblJudgmentDateNewValue.textProperty()));
		paneCaseFileNumberReset.visibleProperty().bind(
				Bindings.notEqual(lblCaseFileNumberOldValue.textProperty(), lblCaseFileNumberNewValue.textProperty()));
		panePrisonerNumberReset.visibleProperty().bind(
				Bindings.notEqual(lblPrisonerNumberOldValue.textProperty(), lblPrisonerNumberNewValue.textProperty()));
		paneArrestDateReset.visibleProperty().bind(
				Bindings.notEqual(lblArrestDateOldValue.textProperty(), lblArrestDateNewValue.textProperty()));
		
		BooleanBinding crimesChangingBinding = Bindings.createBooleanBinding(() ->
		{
			updateNewCrimes();
			return !sameCrimes(oldCrimes, newCrimes);
		}, cboCrimeClass1.valueProperty(), cboCrimeClass2.valueProperty(), cboCrimeClass3.valueProperty(),
           cboCrimeClass4.valueProperty(), cboCrimeClass5.valueProperty(), cboCrimeEvent1.valueProperty(),
           cboCrimeEvent2.valueProperty(), cboCrimeEvent3.valueProperty(), cboCrimeEvent4.valueProperty(),
           cboCrimeEvent5.valueProperty(), btnAddMore.armedProperty(), btnHidePaneCrime2.armedProperty(),
           btnHidePaneCrime3.armedProperty(), btnHidePaneCrime4.armedProperty(),
           btnHidePaneCrime5.armedProperty(), btnCrimesReset.armedProperty());
		
		paneCrimeDiffContainer.visibleProperty().bind(crimesChangingBinding);
		
		lblJudgmentIssuerOldValue.setText(judgementInfo.getJudgIssuer() != null ? judgementInfo.getJudgIssuer() : "");
		lblJudgmentNumberOldValue.setText(judgementInfo.getJudgNum() != null ? judgementInfo.getJudgNum() : "");
		lblJudgmentDateOldValue.setText(judgementInfo.getJudgDate() != null ?
                                GuiUtils.formatLocalDate(AppUtils.secondsToGregorianDate(judgementInfo.getJudgDate()),
                                                         rdoJudgmentDateUseHijri.isSelected()) : "");
		lblCaseFileNumberOldValue.setText(judgementInfo.getPoliceFileNum() != null ?
		                                  judgementInfo.getPoliceFileNum() : "");
		lblPrisonerNumberOldValue.setText(judgementInfo.getPrisonerNumber() != null ?
				                          String.valueOf(judgementInfo.getPrisonerNumber()) : "");
		lblArrestDateOldValue.setText(judgementInfo.getArrestDate() != null ?
                                GuiUtils.formatLocalDate(AppUtils.secondsToGregorianDate(judgementInfo.getArrestDate()),
                                                         rdoArrestDateUseHijri.isSelected()) : "");
		
		for(CrimeCode oldCrime : oldCrimes)
		{
			String crimeEvent = crimeEventTitles.get(oldCrime.getCrimeEvent());
			String crimeClass = crimeClassTitles.get(oldCrime.getCrimeClass());
			Label label = new Label(crimeEvent + " - " + crimeClass);
			label.getStyleClass().add("old-value");
			paneOldCrimes.getChildren().add(label);
		}
		
		btnJudgmentIssuerReset.setOnAction(actionEvent ->
		{
		    txtJudgmentIssuer.setText(judgementInfo.getJudgIssuer() != null ? judgementInfo.getJudgIssuer() : "");
			paneJudgmentDetails.requestFocus();
		});
		btnJudgmentNumberReset.setOnAction(actionEvent ->
		{
			txtJudgmentNumber.setText(judgementInfo.getJudgNum() != null ? judgementInfo.getJudgNum() : "");
			paneJudgmentDetails.requestFocus();
		});
		btnJudgmentDateReset.setOnAction(actionEvent ->
		{
			dpJudgmentDate.setValue(judgementInfo.getJudgDate() != null ?
			                        AppUtils.secondsToGregorianDate(judgementInfo.getJudgDate()) : null);
			paneJudgmentDetails.requestFocus();
		});
		btnCaseFileNumberReset.setOnAction(actionEvent ->
		{
			txtCaseFileNumber.setText(judgementInfo.getPoliceFileNum() != null ? judgementInfo.getPoliceFileNum() : "");
			paneJudgmentDetails.requestFocus();
		});
		btnPrisonerNumberReset.setOnAction(actionEvent ->
		{
			txtPrisonerNumber.setText(judgementInfo.getPrisonerNumber() != null ?
			                          String.valueOf(judgementInfo.getPrisonerNumber()) : "");
			paneJudgmentDetails.requestFocus();
		});
		btnArrestDateReset.setOnAction(actionEvent ->
		{
			dpArrestDate.setValue(judgementInfo.getArrestDate() != null ?
		                          AppUtils.secondsToGregorianDate(judgementInfo.getArrestDate()) : null);
			paneJudgmentDetails.requestFocus();
		});
		btnCrimesReset.setOnAction(actionEvent ->
		{
			GuiUtils.showNode(paneCrime2, false);
			GuiUtils.showNode(paneCrime3, false);
			GuiUtils.showNode(paneCrime4, false);
			GuiUtils.showNode(paneCrime5, false);
			
			for(int i = 0; i < oldCrimes.size(); i++)
			{
				CrimeCode crimeCode = oldCrimes.get(i);
				
				if(crimeCode != null)
				{
					if(i > 0) GuiUtils.showNode(cboCrimePaneMap.get(i), true);
					
					cboCrimeEventMap.get(i).getItems()
							.stream()
							.filter(item -> item.getItem().equals(crimeCode.getCrimeEvent()))
							.findFirst()
							.ifPresent(cboCrimeEventMap.get(i)::setValue);
					cboCrimeClassMap.get(i).getItems()
							.stream()
							.filter(item -> item.getItem().equals(crimeCode.getCrimeClass()))
							.findFirst()
							.ifPresent(cboCrimeClassMap.get(i)::setValue);
				}
			}
			paneJudgmentDetails.requestFocus();
		});
		
		rdoJudgmentDateUseHijri.selectedProperty().addListener((observable, oldValue, newValue) ->
                                lblJudgmentDateOldValue.setText(judgementInfo.getJudgDate() != null ?
                                GuiUtils.formatLocalDate(AppUtils.secondsToGregorianDate(judgementInfo.getJudgDate()),
                                                                         rdoJudgmentDateUseHijri.isSelected()) : ""));
		rdoArrestDateUseHijri.selectedProperty().addListener((observable, oldValue, newValue) ->
                               lblArrestDateOldValue.setText(judgementInfo.getArrestDate() != null ?
						       GuiUtils.formatLocalDate(AppUtils.secondsToGregorianDate(judgementInfo.getArrestDate()),
                                                                            rdoArrestDateUseHijri.isSelected()) : ""));
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		var judgmentIssuer = txtJudgmentIssuer.getText();
		if(!judgmentIssuer.isBlank()) this.judgmentIssuerNewValue = judgmentIssuer;
		else this.judgmentIssuerNewValue = null;
		
		var judgmentNumber = txtJudgmentNumber.getText();
		if(!judgmentNumber.isBlank()) this.judgmentNumberNewValue = judgmentNumber;
		else this.judgmentNumberNewValue = null;
		
		this.judgmentDateNewValue = dpJudgmentDate.getValue();
		this.judgmentDateUseHijri = rdoJudgmentDateUseHijri.isSelected();
		
		var caseFileNumber = txtCaseFileNumber.getText();
		if(!caseFileNumber.isBlank()) this.caseFileNumberNewValue = caseFileNumber;
		else this.caseFileNumberNewValue = null;
		
		var prisonerNumber = txtPrisonerNumber.getText();
		if(!prisonerNumber.isBlank()) this.prisonerNumberNewValue = Long.parseLong(prisonerNumber);
		else this.prisonerNumberNewValue = null;
		
		this.arrestDateNewValue = dpArrestDate.getValue();
		this.arrestDateUseHijri = rdoArrestDateUseHijri.isSelected();
	}
	
	private void initCrimeEventComboBox(ComboBox<ComboBoxItem<Integer>> cboCrimeEvent,
	                                    ComboBox<ComboBoxItem<Integer>> cboCrimeClass,
	                                    Map<Integer, String> crimeEventTitles, Map<Integer, String> crimeClassTitles)
	{
		cboCrimeEvent.getItems().forEach(item -> item.setText(crimeEventTitles.get(item.getItem())));
		cboCrimeEvent.valueProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null)
		    {
			    GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeClass, new ArrayList<>(),
			                                                showingPropertyChangeListenerReference,
			                                                textPropertyChangeListenerReference);
		        return;
		    }
		
		    List<Integer> crimeClassCodes = crimeClasses.get(newValue.getItem());
		    GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeClass, crimeClassCodes,
		                                                showingPropertyChangeListenerReference,
		                                                textPropertyChangeListenerReference);
			cboCrimeClass.getItems().forEach(item -> item.setText(crimeClassTitles.get(item.getItem())));
			cboCrimeClass.getSelectionModel().selectFirst();
		
		});
		cboCrimeEvent.getSelectionModel().selectFirst();
	}
	
	private void updateNewCrimes()
	{
		newCrimes.clear();
		newCrimes.add(new CrimeCode(cboCrimeEvent1.getValue().getItem(), cboCrimeClass1.getValue().getItem()));
		
		for(int i = 1; i <= 4; i++)
		{
			if(cboCrimePaneMap.get(i).isVisible())
			{
				newCrimes.add(new CrimeCode(cboCrimeEventMap.get(i).getValue().getItem(),
				                            cboCrimeClassMap.get(i).getValue().getItem()));
			}
		}
	}
	
	private static boolean sameCrimes(List<CrimeCode> crimeCodes1, List<CrimeCode> crimeCodes2)
	{
		if(crimeCodes1 == null || crimeCodes2 == null) return false;
		if(crimeCodes1.size() != crimeCodes2.size()) return false;
		
		for(int i = 0; i < crimeCodes1.size(); i++)
		{
			CrimeCode crimeCode1 = crimeCodes1.get(i);
			CrimeCode crimeCode2 = crimeCodes2.get(i);
			if(crimeCode1.getCrimeEvent() != crimeCode2.getCrimeEvent()) return false;
			if(crimeCode1.getCrimeClass() != crimeCode2.getCrimeClass()) return false;
		}
		
		return true;
	}
}