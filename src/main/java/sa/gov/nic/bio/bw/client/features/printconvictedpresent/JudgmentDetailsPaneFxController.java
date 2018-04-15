package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
import sa.gov.nic.bio.bw.client.core.utils.GuiLanguage;
import sa.gov.nic.bio.bw.client.core.utils.GuiUtils;
import sa.gov.nic.bio.bw.client.core.wizard.WizardStepFxControllerBase;
import sa.gov.nic.bio.bw.client.features.commons.webservice.CrimeType;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JudgmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent1;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass1;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent2;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass2;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeEvent3;
	@FXML private ComboBox<HideableItem<Integer>> cboCrimeClass3;
	@FXML private CheckBox cbCrimeClassification2;
	@FXML private CheckBox cbCrimeClassification3;
	@FXML private CheckBox cbArrestDateShowHijri;
	@FXML private CheckBox cbJudgmentDateShowHijri;
	@FXML private Label lblCrimeClassification1;
	@FXML private Label lblCrimeClassification2;
	@FXML private Label lblCrimeClassification3;
	@FXML private TextField txtPoliceFileNumber;
	@FXML private TextField txtJudgmentNumber;
	@FXML private TextField txtJudgmentIssuer;
	@FXML private DatePicker dpArrestDate;
	@FXML private DatePicker dpJudgmentDate;
	@FXML private Button btnPrevious;
	@FXML private Button btnStartOver;
	@FXML private Button btnNext;
	
	private Map<Integer, String> crimeEventTitles = new HashMap<>();
	private Map<Integer, String> crimeClassTitles = new HashMap<>();
	private Map<Integer, List<Integer>> crimeClasses = new HashMap<>();
	
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
		GuiUtils.makeButtonClickableByPressingEnter(btnPrevious);
		GuiUtils.makeButtonClickableByPressingEnter(btnStartOver);
		GuiUtils.makeButtonClickableByPressingEnter(btnNext);
		
		btnPrevious.setOnAction(actionEvent -> goPrevious());
		btnStartOver.setOnAction(actionEvent -> startOver());
		btnNext.setOnAction(actionEvent -> goNext());
		
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
		
		GuiUtils.initDatePicker(cbArrestDateShowHijri, dpArrestDate, null);
		GuiUtils.initDatePicker(cbJudgmentDateShowHijri, dpJudgmentDate, null);
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent1, crimeEventCodes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent2, crimeEventCodes);
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent3, crimeEventCodes);
		
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeEvent1);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeClass1);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeEvent2);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeClass2);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeEvent3);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeClass3);
		
		lblCrimeClassification2.disableProperty().bind(cbCrimeClassification2.selectedProperty().not());
		cboCrimeEvent2.disableProperty().bind(cbCrimeClassification2.selectedProperty().not());
		cboCrimeClass2.disableProperty().bind(cbCrimeClassification2.selectedProperty().not());
		lblCrimeClassification3.disableProperty().bind(cbCrimeClassification3.selectedProperty().not());
		cboCrimeEvent3.disableProperty().bind(cbCrimeClassification3.selectedProperty().not());
		cboCrimeClass3.disableProperty().bind(cbCrimeClassification3.selectedProperty().not());
		
		if(Context.getGuiLanguage() == GuiLanguage.ARABIC)
		{
			lblCrimeClassification1.setPadding(new Insets(0, 3.0, 0, 0));
		}
	}
	
	@Override
	protected void onAttachedToScene()
	{
		initCrimeEventComboBox(cboCrimeEvent1, cboCrimeClass1);
		initCrimeEventComboBox(cboCrimeEvent2, cboCrimeClass2);
		initCrimeEventComboBox(cboCrimeEvent3, cboCrimeClass3);
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
		
		}
	}
	
	private void initCrimeEventComboBox(ComboBox<HideableItem<Integer>> cboCrimeEvent,
	                                    ComboBox<HideableItem<Integer>> cboCrimeClass)
	{
		cboCrimeEvent.getItems().forEach(item -> item.setText(crimeEventTitles.get(item.getObject())));
		cboCrimeEvent.valueProperty().addListener((observable, oldValue, newValue) ->
		{
		    if(newValue == null)
		    {
			    cboCrimeClass.getItems().clear();
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
}