package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.controllers;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import sa.gov.nic.bio.bw.core.workflow.Output;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans.CrimeType;
import sa.gov.nic.bio.bw.workflow.registerconvictedpresent.lookups.CrimeTypesLookup;
import sa.gov.nic.bio.bw.workflow.commons.beans.CrimeCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FxmlFile("judgmentDetails.fxml")
public class JudgmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	@Output private String judgmentIssuer;
	@Output private String judgmentNumber;
	@Output private LocalDate judgmentDate;
	@Output private Boolean judgmentDateUseHijri;
	@Output private String caseFileNumber;
	@Output private Long prisonerNumber;
	@Output private LocalDate arrestDate;
	@Output private Boolean arrestDateUseHijri;
	@Output private List<CrimeCode> crimes;
	
	@FXML private Pane paneCrimeContainer;
	@FXML private Pane paneCrime2;
	@FXML private Pane paneCrime3;
	@FXML private Pane paneCrime4;
	@FXML private Pane paneCrime5;
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
	@FXML private TextField txtJudgmentIssuer;
	@FXML private TextField txtJudgmentNumber;
	@FXML private TextField txtCaseFileNumber;
	@FXML private TextField txtPrisonerNumber;
	@FXML private DatePicker dpArrestDate;
	@FXML private DatePicker dpJudgmentDate;
	@FXML private RadioButton rdoArrestDateUseHijri;
	@FXML private RadioButton rdoArrestDateUseGregorian;
	@FXML private RadioButton rdoJudgmentDateUseHijri;
	@FXML private RadioButton rdoJudgmentDateUseGregorian;
	@FXML private Button btnHidePaneCrime2;
	@FXML private Button btnHidePaneCrime3;
	@FXML private Button btnHidePaneCrime4;
	@FXML private Button btnHidePaneCrime5;
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
		
		Node focusedNode = null;
		
		if(judgmentIssuer != null && !judgmentIssuer.isEmpty()) txtJudgmentIssuer.setText(judgmentIssuer);
		else focusedNode = txtJudgmentIssuer;
		
		if(judgmentNumber != null && !judgmentNumber.isEmpty()) txtJudgmentNumber.setText(judgmentNumber);
		else if(focusedNode == null) focusedNode = txtJudgmentNumber;
		
		if(judgmentDate != null) dpJudgmentDate.setValue(judgmentDate);
		else if(focusedNode == null) focusedNode = dpJudgmentDate;
		
		rdoJudgmentDateUseHijri.setSelected(true);
		if(this.judgmentDateUseHijri != null && !this.judgmentDateUseHijri)
																		rdoJudgmentDateUseGregorian.setSelected(true);
		
		if(caseFileNumber != null && !caseFileNumber.isEmpty()) txtCaseFileNumber.setText(caseFileNumber);
		
		if(prisonerNumber != null) txtPrisonerNumber.setText(AppUtils.localizeNumbers(String.valueOf(prisonerNumber)));
		
		if(arrestDate != null) dpArrestDate.setValue(arrestDate);
		
		rdoArrestDateUseHijri.setSelected(true);
		if(this.arrestDateUseHijri != null && !this.arrestDateUseHijri) rdoArrestDateUseGregorian.setSelected(true);
		
		if(crimes != null)
		{
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
		}
		
		checkForCrimeDuplicates.run();
		
		if(focusedNode != null) focusedNode.requestFocus();
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
		var judgmentIssuer = txtJudgmentIssuer.getText();
		if(!judgmentIssuer.isBlank()) this.judgmentIssuer = judgmentIssuer;
		else this.judgmentIssuer = null;
		
		var judgmentNumber = txtJudgmentNumber.getText();
		if(!judgmentNumber.isBlank()) this.judgmentNumber = judgmentNumber;
		else this.judgmentNumber = null;
		
		this.judgmentDate = dpJudgmentDate.getValue();
		this.judgmentDateUseHijri = rdoJudgmentDateUseHijri.isSelected();
		
		var caseFileNumber = txtCaseFileNumber.getText();
		if(!caseFileNumber.isBlank()) this.caseFileNumber = caseFileNumber;
		else this.caseFileNumber = null;
		
		var prisonerNumber = txtPrisonerNumber.getText();
		if(!prisonerNumber.isBlank()) this.prisonerNumber = Long.parseLong(prisonerNumber);
		else this.prisonerNumber = null;
		
		this.arrestDate = dpArrestDate.getValue();
		this.arrestDateUseHijri = rdoArrestDateUseHijri.isSelected();
		
		crimes = new ArrayList<>();
		crimes.add(new CrimeCode(cboCrimeEvent1.getValue().getItem(), cboCrimeClass1.getValue().getItem()));
		
		for(int i = 1; i <= 4; i++)
		{
			if(cboCrimePaneMap.get(i).isVisible())
			{
				crimes.add(new CrimeCode(cboCrimeEventMap.get(i).getValue().getItem(),
				                         cboCrimeClassMap.get(i).getValue().getItem()));
			}
		}
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
}