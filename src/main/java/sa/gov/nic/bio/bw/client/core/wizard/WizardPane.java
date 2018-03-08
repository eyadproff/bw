package sa.gov.nic.bio.bw.client.core.wizard;

import javafx.beans.NamedArg;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WizardPane extends BorderPane
{
	private static final String FXML_WIZARD = "sa/gov/nic/bio/bw/client/core/fxml/wizard.fxml";
	@FXML private Pane stepsIndicatorPane;
	
	private ListProperty<WizardStep> steps;
	private List<WizardStepIndicator> indicators = new ArrayList<>();
	private int currentStep = -1;
	
	public WizardPane()
	{
		this(new WizardStep[0]);
	}
	
	public WizardPane(@NamedArg("steps") WizardStep[] steps)
	{
		loadFxml();
		this.steps = new SimpleListProperty<>(this.steps, "steps", FXCollections.observableArrayList(WizardStep.extractor()));
		setSteps(FXCollections.observableArrayList(steps));
	}
	
	public ListProperty<WizardStep> stepsProperty(){return this.steps;}
	public void setSteps(ObservableList<WizardStep> value){this.steps.get().setAll(value); drawStepsIndicators();}
	public ObservableList<WizardStep> getSteps(){return this.steps.get();}
	
	public int getCurrentStep(){return currentStep;}
	public void setCurrentStep(int currentStep){this.currentStep = currentStep;}
	
	public void clearSteps()
	{
		setSteps(FXCollections.observableArrayList());
	}
	
	private void loadFxml()
	{
		FXMLLoader fxmlLoader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource(FXML_WIZARD));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		try
		{
			fxmlLoader.load();
		}
		catch(IOException exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public void updateStep(int stepIndex, String title, String iconId)
	{
		WizardStep wizardStep = steps.get(stepIndex);
		wizardStep.setTitle(title);
		wizardStep.setIconId(iconId);
		
		drawStepsIndicators();
		
		if(currentStep >= 0) currentStep--;
		goNext();
	}
	
	private void drawStepsIndicators()
	{
		final double TITLE_PREF_WIDTH = 65.0;
		
		GridPane gridPane = new GridPane();
		gridPane.setVgap(5.0);
		gridPane.setPadding(new Insets(10.0, 0.0, 0.0, 0.0));
		stepsIndicatorPane.getChildren().setAll(gridPane);
		
		ObservableList<WizardStep> wizardSteps = getSteps();
		indicators.clear();
		
		for(int i = 0; i < wizardSteps.size(); i++)
		{
			WizardStep wizardStep = wizardSteps.get(i);
			
			WizardStepIndicator indicator = new WizardStepIndicator(wizardStep.getIconId());
			indicators.add(indicator);
			indicator.getStyleClass().add("wizard-indicator");
			gridPane.add(indicator, i, 0);
			
			StackPane stackPane = new StackPane();
			stackPane.setAlignment(Pos.TOP_CENTER);
			gridPane.add(stackPane, i, 1);
			Label lblTitle = new Label(wizardStep.getTitle());
			lblTitle.setWrapText(true);
			lblTitle.setAlignment(Pos.TOP_CENTER);
			lblTitle.setTextAlignment(TextAlignment.CENTER);
			lblTitle.setPrefWidth(TITLE_PREF_WIDTH);
			
			indicator.getCircle().fillProperty().addListener((observable, oldValue, newValue) ->
			{
				if(indicator.isVisited()) lblTitle.setTextFill(indicator.getCircle().getFill());
			});
			
			indicator.getNormalIcon().textFillProperty().addListener((observable, oldValue, newValue) ->
			{
				if(!indicator.isVisited()) lblTitle.setTextFill(indicator.getNormalIcon().getTextFill());
			});
			
			ChangeListener<Boolean> tChangeListener = (observable, oldValue, newValue) ->
			{
				boolean done = indicator.isVisited() && !indicator.isSelected();
				indicator.getNormalIcon().setVisible(!done);
				indicator.getDoneIcon().setVisible(done);
			};
			
			indicator.selectedProperty().addListener(tChangeListener);
			indicator.visitedProperty().addListener(tChangeListener);
			
			stackPane.getChildren().add(lblTitle);
		}
	}
	
	public void goNext()
	{
		currentStep++;
		
		for(WizardStepIndicator indicator : indicators)
		{
			if(!indicator.isVisited())
			{
				indicator.setVisited(true);
				indicator.setSelected(true);
				return;
			}
			
			if(indicator.isSelected()) indicator.setSelected(false);
		}
	}
	
	public void goPrevious()
	{
		currentStep--;
		
		for(int i = indicators.size() - 1; i >= 0; i--)
		{
			WizardStepIndicator indicator = indicators.get(i);
			
			if(indicator.isSelected())
			{
				indicator.setVisited(false);
				indicator.setSelected(false);
			}
			
			if(indicator.isVisited())
			{
				indicator.setSelected(true);
				return;
			}
		}
	}
	
	public void startOver()
	{
		currentStep = 0;
		
		for(WizardStepIndicator indicator : indicators)
		{
			indicator.setVisited(false);
			indicator.setSelected(false);
		}
		
		goNext();
	}
}