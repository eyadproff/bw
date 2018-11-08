package sa.gov.nic.bio.bw.core.wizard;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sa.gov.nic.bio.bw.core.utils.GuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WizardPane extends BorderPane
{
	private static final String FXML_WIZARD = "/sa/gov/nic/bio/bw/core/fxml/wizard.fxml";
	
	@FXML private Pane stepsIndicatorPane;
	@FXML private ScrollPane stepsIndicatorScrollPane;
	
	private ListProperty<WizardStep> steps;
	private List<WizardStepIndicator> indicators = new ArrayList<>();
	private int currentStep = 0;
	
	public WizardPane(WizardStep[] steps)
	{
		loadFxml();
		this.steps = new SimpleListProperty<>(this.steps, "steps",
	                                                    FXCollections.observableArrayList(WizardStep.extractor()));
		setSteps(FXCollections.observableArrayList(steps));
	}
	
	public void setSteps(ObservableList<WizardStep> value)
	{
		this.steps.get().setAll(value); drawStepsIndicators(true);
	}
	public ObservableList<WizardStep> getSteps(){return this.steps.get();}
	
	private void loadFxml()
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_WIZARD));
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
	
	public int getStepIndexByTitle(String title)
	{
		for(int i = 0; i < steps.size(); i++)
		{
			if(steps.get(i).getTitle().equals(title)) return i;
		}
		
		return -1;
	}
	
	public void updateStep(int stepIndex, String title, String iconId)
	{
		WizardStep wizardStep = steps.get(stepIndex);
		wizardStep.setTitle(title);
		wizardStep.setIconId(iconId);
		
		drawStepsIndicators(false);
		
		for(int i = 0; i < indicators.size(); i++)
		{
			WizardStepIndicator indicator = indicators.get(i);
			
			if(i <= currentStep) indicator.setVisited(true);
			if(i == currentStep)
			{
				indicator.setSelected(true);
				break;
			}
		}
	}
	
	private void drawStepsIndicators(boolean firstBuild)
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
			WizardStepIndicator indicator;
			
			try
			{
				indicator = new WizardStepIndicator(wizardStep.getIconId());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
			
			indicator.setSelected(firstBuild && i == 0);
			indicator.setVisited(firstBuild && i == 0);
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
				GuiUtils.ensureNodeVisibilityInHorizontalScrollPane(stepsIndicatorScrollPane, indicator);
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
				GuiUtils.ensureNodeVisibilityInHorizontalScrollPane(stepsIndicatorScrollPane, indicator);
				return;
			}
		}
	}
	
	public void startOver()
	{
		currentStep = 0;
		
		for(int i = 0; i < indicators.size(); i++)
		{
			WizardStepIndicator indicator = indicators.get(i);
			indicator.setVisited(i == 0);
			indicator.setSelected(i == 0);
		}
		
		Animation animation = new Timeline(new KeyFrame(Duration.seconds(0.5),
				             new KeyValue(stepsIndicatorScrollPane.hvalueProperty(), 0.0)));
		animation.play();
	}
}