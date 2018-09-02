package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
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
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.webservice.CrimeCode;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JudgmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_JUDGMENT_DETAILS_CRIMES = "JUDGMENT_DETAILS_CRIMES";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER = "JUDGMENT_DETAILS_JUDGMENT_ISSUER";
	public static final String KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER = "JUDGMENT_DETAILS_POLICE_FILE_NUMBER";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER = "JUDGMENT_DETAILS_JUDGMENT_NUMBER";
	public static final String KEY_JUDGMENT_DETAILS_ARREST_DATE = "JUDGMENT_DETAILS_ARREST_DATE";
	public static final String KEY_JUDGMENT_DETAILS_ARREST_DATE_USE_HIJRI = "JUDGMENT_DETAILS_ARREST_DATE_USE_HIJRI";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_DATE = "JUDGMENT_DETAILS_JUDGMENT_DATE";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_DATE_USE_HIJRI =
																			"JUDGMENT_DETAILS_JUDGMENT_DATE_USE_HIJRI";
	
	@FXML private Pane paneCrimeContainer;
	@FXML private Pane paneCrime2;
	@FXML private Pane paneCrime3;
	@FXML private Pane paneCrime4;
	@FXML private Pane paneCrime5;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent1;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass1;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent2;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass2;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent3;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass3;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent4;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass4;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent5;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass5;
	@FXML private TextField txtCaseFileNumber;
	@FXML private TextField txtJudgmentNumber;
	@FXML private TextField txtJudgmentIssuer;
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
	
	private Map<Integer, String> crimeEventTitles = new HashMap<>();
	private Map<Integer, String> crimeClassTitles = new HashMap<>();
	private Map<Integer, List<Integer>> crimeClasses = new HashMap<>();
	private Map<Integer, Pane> cboCrimePaneMap = new HashMap<>();
	private Map<Integer, ComboBox<HideableItem<Integer>>> cboCrimeEventMap = new HashMap<>();
	private Map<Integer, ComboBox<HideableItem<Integer>>> cboCrimeClassMap = new HashMap<>();
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
	public URL getFxmlLocation()
	{
		return getClass().getResource("fxml/judgmentDetails.fxml");
	}
	
	@Override
	protected void initialize()
	{
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		btnNext.setOnAction(actionEvent -> goNext());
		
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
		
		@SuppressWarnings("unchecked") List<CrimeType> crimeTypes = (List<CrimeType>)
												Context.getUserSession().getAttribute("lookups.crimeTypes");
		
		crimeTypes.forEach(crimeType ->
		{
			int eventCode = crimeType.getEventCode();
			int classCode = crimeType.getClassCode();
			String eventTitle = crimeType.getEventDesc();
			String classTitle = crimeType.getClassDesc();
			
			crimeEventTitles.putIfAbsent(eventCode, eventTitle);
			crimeClassTitles.putIfAbsent(classCode, classTitle);
			crimeClasses.putIfAbsent(eventCode, new ArrayList<>());
			crimeClasses.get(eventCode).add(classCode);
		});
		
		List<Integer> crimeEventCodes = crimeTypes.stream()
												  .mapToInt(CrimeType::getEventCode)
												  .distinct()
												  .boxed()
												  .collect(Collectors.toList());
		
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
	}
	
	@Override
	protected void onAttachedToScene()
	{
		initCrimeEventComboBox(cboCrimeEvent1, cboCrimeClass1);
		initCrimeEventComboBox(cboCrimeEvent2, cboCrimeClass2);
		initCrimeEventComboBox(cboCrimeEvent3, cboCrimeClass3);
		initCrimeEventComboBox(cboCrimeEvent4, cboCrimeClass4);
		initCrimeEventComboBox(cboCrimeEvent5, cboCrimeClass5);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
			Node focusedNode = null;
			
			@SuppressWarnings("unchecked")
			List<CrimeCode> crimes = (List<CrimeCode>) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIMES);
			
			if(crimes != null && !crimes.isEmpty())
			{
				for(int i = 0; i < crimes.size(); i++)
				{
					CrimeCode cc = crimes.get(i);
					
					if(cc != null)
					{
						if(i > 0) GuiUtils.showNode(cboCrimePaneMap.get(i), true);
						
						cboCrimeEventMap.get(i).getItems()
								.stream()
								.filter(item -> item.getObject().equals(cc.getCrimeEvent()))
								.findFirst()
								.ifPresent(cboCrimeEventMap.get(i)::setValue);
						cboCrimeClassMap.get(i).getItems()
								.stream()
								.filter(item -> item.getObject().equals(cc.getCrimeClass()))
								.findFirst()
								.ifPresent(cboCrimeClassMap.get(i)::setValue);
					}
				}
			}
			
			checkForCrimeDuplicates.run();
			
			String judgmentIssuer = (String) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER);
			if(judgmentIssuer != null && !judgmentIssuer.trim().isEmpty()) txtJudgmentIssuer.setText(judgmentIssuer);
			else focusedNode = txtJudgmentIssuer;
			
			String policeFileNumber = (String) uiInputData.get(KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER);
			if(policeFileNumber != null && !policeFileNumber.trim().isEmpty())
																		txtCaseFileNumber.setText(policeFileNumber);
			
			String judgmentNumber = (String) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER);
			if(judgmentNumber != null && !judgmentNumber.trim().isEmpty()) txtJudgmentNumber.setText(judgmentNumber);
			else if(focusedNode == null) focusedNode = txtJudgmentNumber;
			
			LocalDate arrestDate = (LocalDate) uiInputData.get(KEY_JUDGMENT_DETAILS_ARREST_DATE);
			if(arrestDate != null) dpArrestDate.setValue(arrestDate);
			
			Boolean arrestDateUseHijri = (Boolean) uiInputData.get(KEY_JUDGMENT_DETAILS_ARREST_DATE_USE_HIJRI);
			if(arrestDateUseHijri == null || arrestDateUseHijri) rdoArrestDateUseHijri.setSelected(true);
			else rdoArrestDateUseGregorian.setSelected(true);
			
			LocalDate judgmentDate = (LocalDate) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE);
			if(judgmentDate != null) dpJudgmentDate.setValue(judgmentDate);
			else if(focusedNode == null) focusedNode = dpJudgmentDate;
			
			Boolean judgmentDatUseHijri = (Boolean) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE_USE_HIJRI);
			if(judgmentDatUseHijri == null || judgmentDatUseHijri) rdoJudgmentDateUseHijri.setSelected(true);
			else rdoJudgmentDateUseGregorian.setSelected(true);
			
			if(focusedNode != null) focusedNode.requestFocus();
			else btnNext.requestFocus();
		}
	}
	
	@Override
	protected void onGoingPrevious(Map<String, Object> uiDataMap)
	{
		onGoingNext(uiDataMap);
	}
	
	@Override
	public void onGoingNext(Map<String, Object> uiDataMap)
	{
		List<CrimeCode> crimes = new ArrayList<>();
		
		crimes.add(new CrimeCode(cboCrimeEvent1.getValue().getObject(), cboCrimeClass1.getValue().getObject()));
		
		for(int i = 1; i <= 4; i++)
		{
			if(cboCrimePaneMap.get(i).isVisible())
			{
				crimes.add(new CrimeCode(cboCrimeEventMap.get(i).getValue().getObject(),
				                         cboCrimeClassMap.get(i).getValue().getObject()));
			}
		}
		
		uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIMES, crimes);
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER, txtJudgmentIssuer.getText());
		
		String policeFileNumber = txtCaseFileNumber.getText();
		if(policeFileNumber != null && !policeFileNumber.trim().isEmpty())
											uiDataMap.put(KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER, policeFileNumber);
		else uiDataMap.remove(KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER);
		
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER, txtJudgmentNumber.getText());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_ARREST_DATE, dpArrestDate.getValue());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_ARREST_DATE_USE_HIJRI, rdoArrestDateUseHijri.isSelected());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE, dpJudgmentDate.getValue());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE_USE_HIJRI, rdoJudgmentDateUseHijri.isSelected());
	}
	
	private void initCrimeEventComboBox(ComboBox<HideableItem<Integer>> cboCrimeEvent,
	                                    ComboBox<HideableItem<Integer>> cboCrimeClass)
	{
		cboCrimeEvent.getItems().forEach(item -> item.setText(crimeEventTitles.get(item.getObject())));
		cboCrimeEvent.valueProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null)
		    {
			    GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeClass, new ArrayList<>(),
			                                                showingPropertyChangeListenerReference,
			                                                textPropertyChangeListenerReference);
		        return;
		    }
		
		    List<Integer> crimeClassCodes = crimeClasses.get(newValue.getObject());
		    GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeClass, crimeClassCodes,
		                                                showingPropertyChangeListenerReference,
		                                                textPropertyChangeListenerReference);
			cboCrimeClass.getItems().forEach(item -> item.setText(crimeClassTitles.get(item.getObject())));
			cboCrimeClass.getSelectionModel().selectFirst();
		
		});
		cboCrimeEvent.getSelectionModel().selectFirst();
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