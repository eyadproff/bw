package sa.gov.nic.bio.bw.client.core.wizard;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import sa.gov.nic.bio.bw.client.core.utils.AppUtils;

public class WizardStepIndicator extends StackPane
{
	private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
	private static final PseudoClass VISITED_PSEUDO_CLASS = PseudoClass.getPseudoClass("visited");
	
	public BooleanProperty selected = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return WizardStepIndicator.this;
		}
		
		@Override
		public String getName()
		{
			return "selected";
		}
	};
	
	public BooleanProperty visited = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(VISITED_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return WizardStepIndicator.this;
		}
		
		@Override
		public String getName()
		{
			return "visited";
		}
	};
	
	private Circle circle;
	private Glyph normalIcon;
	private Glyph doneIcon;
	
	public WizardStepIndicator(String iconId)
	{
		final double CIRCLE_RADIUS = 20.0;
		final double LINE_STROKE_WIDTH = 3.0;
		
		circle = new Circle(CIRCLE_RADIUS);
		circle.getStyleClass().add("wizard-indicator-circle");
		Line line = new Line(0.0, CIRCLE_RADIUS / 2, 0.0, CIRCLE_RADIUS / 2);
		line.getStyleClass().add("wizard-indicator-line");
		line.endXProperty().bind(widthProperty());
		line.setStrokeWidth(LINE_STROKE_WIDTH);
		
		if(iconId.startsWith("\\u")) normalIcon = AppUtils.createFontAwesomeIcon((char) Integer.parseInt(iconId.substring(2), 16));
		else
		{
			FontAwesome.Glyph normalGlyph = FontAwesome.Glyph.valueOf(iconId.toUpperCase());
			normalIcon = AppUtils.createFontAwesomeIcon(normalGlyph);
		}
		
		FontAwesome.Glyph doneGlyph = FontAwesome.Glyph.CHECK;
		doneIcon = AppUtils.createFontAwesomeIcon(doneGlyph);
		doneIcon.setVisible(false);
		
		getChildren().addAll(circle, line, normalIcon, doneIcon);
	}
	
	public Circle getCircle()
	{
		return circle;
	}
	
	public Glyph getNormalIcon()
	{
		return normalIcon;
	}
	
	public Glyph getDoneIcon()
	{
		return doneIcon;
	}
	
	public boolean isSelected(){return selected.get();}
	public void setSelected(boolean selected){this.selected.set(selected);}
	public BooleanProperty selectedProperty(){return selected;}
	
	public boolean isVisited(){return visited.get();}
	public void setVisited(boolean selected){this.visited.set(selected);}
	public BooleanProperty visitedProperty(){return visited;}
}