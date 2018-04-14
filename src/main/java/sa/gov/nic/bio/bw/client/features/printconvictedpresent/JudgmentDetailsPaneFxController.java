package sa.gov.nic.bio.bw.client.features.printconvictedpresent;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import sa.gov.nic.bio.bw.client.core.Context;
import sa.gov.nic.bio.bw.client.core.beans.HideableItem;
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
		
		GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeEvent1, crimeEventCodes);
		
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeEvent1);
		GuiUtils.makeComboBoxOpenableByPressingSpaceBar(cboCrimeClass1);
	}
	
	@Override
	protected void onAttachedToScene()
	{
		cboCrimeEvent1.getItems().forEach(item -> item.setText(crimeEventTitles.get(item.getObject())));
		cboCrimeEvent1.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue == null)
			{
				cboCrimeClass1.getItems().clear();
				return;
			}
			
			List<Integer> crimeClassCodes = crimeClasses.get(newValue.getObject());
			GuiUtils.addAutoCompletionSupportToComboBox(cboCrimeClass1, crimeClassCodes,
			                                            showingPropertyChangeListenerReference,
			                                            textPropertyChangeListenerReference);
			cboCrimeClass1.getItems().forEach(item -> item.setText(crimeClassTitles.get(item.getObject())));
			cboCrimeClass1.getSelectionModel().selectFirst();
			
		});
		cboCrimeEvent1.getSelectionModel().selectFirst();
	}
	
	@Override
	public void onWorkflowUserTaskLoad(boolean newForm, Map<String, Object> uiInputData)
	{
		if(newForm)
		{
		
		}
	}
}