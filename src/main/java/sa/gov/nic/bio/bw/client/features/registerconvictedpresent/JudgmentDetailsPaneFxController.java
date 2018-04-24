package sa.gov.nic.bio.bw.client.features.registerconvictedpresent;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JudgmentDetailsPaneFxController extends WizardStepFxControllerBase
{
	public static final String KEY_JUDGMENT_DETAILS_CRIME_EVENT_1 = "JUDGMENT_DETAILS_CRIME_EVENT_1";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_CLASS_1 = "JUDGMENT_DETAILS_CRIME_CLASS_1";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_2_ENABLED = "JUDGMENT_DETAILS_CRIME_2_ENABLED";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_EVENT_2 = "JUDGMENT_DETAILS_CRIME_EVENT_2";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_CLASS_2 = "JUDGMENT_DETAILS_CRIME_CLASS_2";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_3_ENABLED = "JUDGMENT_DETAILS_CRIME_3_ENABLED";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_EVENT_3 = "JUDGMENT_DETAILS_CRIME_EVENT_3";
	public static final String KEY_JUDGMENT_DETAILS_CRIME_CLASS_3 = "JUDGMENT_DETAILS_CRIME_CLASS_3";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER = "JUDGMENT_DETAILS_JUDGMENT_ISSUER";
	public static final String KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER = "JUDGMENT_DETAILS_POLICE_FILE_NUMBER";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER = "JUDGMENT_DETAILS_JUDGMENT_NUMBER";
	public static final String KEY_JUDGMENT_DETAILS_ARREST_DATE = "JUDGMENT_DETAILS_ARREST_DATE";
	public static final String KEY_JUDGMENT_DETAILS_ARREST_DATE_SHOW_HIJRI = "JUDGMENT_DETAILS_ARREST_DATE_SHOW_HIJRI";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_DATE = "JUDGMENT_DETAILS_JUDGMENT_DATE";
	public static final String KEY_JUDGMENT_DETAILS_JUDGMENT_DATE_SHOW_HIJRI =
																		"JUDGMENT_DETAILS_JUDGMENT_DATE_SHOW_HIJRI";
	
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
		
		BooleanBinding cboCrimeClassification1Binding = cboCrimeEvent1.valueProperty().isNull()
																	  .or(cboCrimeClass1.valueProperty().isNull());
		BooleanBinding cboCrimeClassification2Binding = cbCrimeClassification2.selectedProperty()
																	.and(cboCrimeEvent2.valueProperty().isNull()
															        .or(cboCrimeClass2.valueProperty().isNull()));
		BooleanBinding cboCrimeClassification3Binding = cbCrimeClassification3.selectedProperty()
																	.and(cboCrimeEvent3.valueProperty().isNull()
															        .or(cboCrimeClass3.valueProperty().isNull()));
		BooleanBinding txtJudgmentIssuerBinding = txtJudgmentIssuer.textProperty().isEmpty();
		BooleanBinding txtPoliceFileNumberBinding = txtPoliceFileNumber.textProperty().isEmpty();
		BooleanBinding txtJudgmentNumberBinding = txtJudgmentNumber.textProperty().isEmpty();
		BooleanBinding dpArrestDateBinding = dpArrestDate.valueProperty().isNull();
		BooleanBinding dpJudgmentDateBinding = dpJudgmentDate.valueProperty().isNull();
		
		btnNext.disableProperty().bind(cboCrimeClassification1Binding.or(cboCrimeClassification2Binding)
				                 .or(cboCrimeClassification3Binding).or(txtJudgmentIssuerBinding)
	                             .or(txtPoliceFileNumberBinding).or(txtJudgmentNumberBinding)
	                             .or(dpArrestDateBinding).or(dpJudgmentDateBinding));
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
			Node focusedNode = null;
			
			Integer crimeEvent1 = (Integer) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_EVENT_1);
			if(crimeEvent1 != null) cboCrimeEvent1.getItems()
												  .stream()
												  .filter(item -> item.getObject().equals(crimeEvent1))
												  .findFirst()
												  .ifPresent(cboCrimeEvent1::setValue);
			else focusedNode = cboCrimeEvent1;
			
			Integer crimeClass1 = (Integer) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_CLASS_1);
			if(crimeClass1 != null) cboCrimeClass1.getItems()
												  .stream()
												  .filter(item -> item.getObject().equals(crimeClass1))
												  .findFirst()
												  .ifPresent(cboCrimeClass1::setValue);
			else if(focusedNode == null) focusedNode = cboCrimeClass1;
			
			Boolean crime2Enabled = (Boolean) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_2_ENABLED);
			cbCrimeClassification2.setSelected(crime2Enabled != null && crime2Enabled);
			
			Integer crimeEvent2 = (Integer) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_EVENT_2);
			if(crimeEvent2 != null) cboCrimeEvent2.getItems()
												  .stream()
												  .filter(item -> item.getObject().equals(crimeEvent2))
												  .findFirst()
												  .ifPresent(cboCrimeEvent2::setValue);
			else if(focusedNode == null && cbCrimeClassification2.isSelected()) focusedNode = cboCrimeEvent2;
			
			Integer crimeClass2 = (Integer) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_CLASS_2);
			if(crimeClass2 != null) cboCrimeClass2.getItems()
												  .stream()
												  .filter(item -> item.getObject().equals(crimeClass2))
												  .findFirst()
												  .ifPresent(cboCrimeClass2::setValue);
			else if(focusedNode == null && cbCrimeClassification2.isSelected()) focusedNode = cboCrimeClass2;
			
			Boolean crime3Enabled = (Boolean) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_3_ENABLED);
			cbCrimeClassification3.setSelected(crime3Enabled != null && crime3Enabled);
			
			Integer crimeEvent3 = (Integer) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_EVENT_3);
			if(crimeEvent3 != null) cboCrimeEvent3.getItems()
					.stream()
					.filter(item -> item.getObject().equals(crimeEvent3))
					.findFirst()
					.ifPresent(cboCrimeEvent3::setValue);
			else if(focusedNode == null && cbCrimeClassification3.isSelected()) focusedNode = cboCrimeEvent3;
			
			Integer crimeClass3 = (Integer) uiInputData.get(KEY_JUDGMENT_DETAILS_CRIME_CLASS_3);
			if(crimeClass3 != null) cboCrimeClass3.getItems()
					.stream()
					.filter(item -> item.getObject().equals(crimeClass3))
					.findFirst()
					.ifPresent(cboCrimeClass3::setValue);
			else if(focusedNode == null && cbCrimeClassification3.isSelected()) focusedNode = cboCrimeClass3;
			
			String judgmentIssuer = (String) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER);
			if(judgmentIssuer != null && !judgmentIssuer.trim().isEmpty()) txtJudgmentIssuer.setText(judgmentIssuer);
			else if(focusedNode == null) focusedNode = txtJudgmentIssuer;
			
			String policeFileNumber = (String) uiInputData.get(KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER);
			if(policeFileNumber != null && !policeFileNumber.trim().isEmpty())
																		txtPoliceFileNumber.setText(policeFileNumber);
			else if(focusedNode == null) focusedNode = txtPoliceFileNumber;
			
			String judgmentNumber = (String) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER);
			if(judgmentNumber != null && !judgmentNumber.trim().isEmpty()) txtJudgmentNumber.setText(judgmentNumber);
			else if(focusedNode == null) focusedNode = txtJudgmentNumber;
			
			LocalDate arrestDate = (LocalDate) uiInputData.get(KEY_JUDGMENT_DETAILS_ARREST_DATE);
			if(arrestDate != null) dpArrestDate.setValue(arrestDate);
			else if(focusedNode == null) focusedNode = dpArrestDate;
			
			Boolean arrestDateShowHijri = (Boolean) uiInputData.get(KEY_JUDGMENT_DETAILS_ARREST_DATE_SHOW_HIJRI);
			cbArrestDateShowHijri.setSelected(arrestDateShowHijri != null && arrestDateShowHijri);
			
			LocalDate judgmentDate = (LocalDate) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE);
			if(judgmentDate != null) dpJudgmentDate.setValue(judgmentDate);
			else if(focusedNode == null) focusedNode = dpJudgmentDate;
			
			Boolean judgmentDateShowHijri = (Boolean) uiInputData.get(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE_SHOW_HIJRI);
			cbJudgmentDateShowHijri.setSelected(judgmentDateShowHijri != null && judgmentDateShowHijri);
			
			if(focusedNode != null) focusedNode.requestFocus();
			else btnNext.requestFocus();
		}
	}
	
	@Override
	public void onLeaving(Map<String, Object> uiDataMap)
	{
		HideableItem<Integer> crimeEventItem1 = cboCrimeEvent1.getValue();
		if(crimeEventItem1 != null) uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_EVENT_1, crimeEventItem1.getObject());
		
		HideableItem<Integer> crimeClassItem1 = cboCrimeClass1.getValue();
		if(crimeClassItem1 != null) uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_CLASS_1, crimeClassItem1.getObject());
		
		HideableItem<Integer> crimeEventItem2 = cboCrimeEvent2.getValue();
		if(crimeEventItem2 != null) uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_EVENT_2, crimeEventItem2.getObject());
		
		HideableItem<Integer> crimeClassItem2 = cboCrimeClass2.getValue();
		if(crimeClassItem2 != null) uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_CLASS_2, crimeClassItem2.getObject());
		
		HideableItem<Integer> crimeEventItem3 = cboCrimeEvent3.getValue();
		if(crimeEventItem3 != null) uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_EVENT_3, crimeEventItem3.getObject());
		
		HideableItem<Integer> crimeClassItem3 = cboCrimeClass3.getValue();
		if(crimeClassItem3 != null) uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_CLASS_3, crimeClassItem3.getObject());
		
		uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_2_ENABLED, cbCrimeClassification2.isSelected());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_CRIME_3_ENABLED, cbCrimeClassification3.isSelected());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_ISSUER, txtJudgmentIssuer.getText());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_POLICE_FILE_NUMBER, txtPoliceFileNumber.getText());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_NUMBER, txtJudgmentNumber.getText());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_ARREST_DATE, dpArrestDate.getValue());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_ARREST_DATE_SHOW_HIJRI, cbArrestDateShowHijri.isSelected());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE, dpJudgmentDate.getValue());
		uiDataMap.put(KEY_JUDGMENT_DETAILS_JUDGMENT_DATE_SHOW_HIJRI, cbJudgmentDateShowHijri.isSelected());
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
	
	@FXML
	private void onStartOverButtonClicked(ActionEvent actionEvent)
	{
		String headerText = resources.getString("printConvictedPresent.startingOver.confirmation.header");
		String contentText = resources.getString("printConvictedPresent.startingOver.confirmation.message");
		boolean confirmed = Context.getCoreFxController().showConfirmationDialogAndWait(headerText, contentText);
		
		if(confirmed) startOver();
	}
}