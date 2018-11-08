package sa.gov.nic.bio.bw.features.searchbyfaceimage.ui;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class ToggleTitledPane extends TitledPane implements Toggle
{
	private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
	private ObjectProperty<ToggleGroup> toggleGroup = new SimpleObjectProperty<>();
	
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
			return ToggleTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return "selected";
		}
	};
	
	public ToggleTitledPane()
	{
		getStyleClass().add("toggle-titled-pane");
	}
	
	public ToggleTitledPane(String title, Node content)
	{
		super(title, content);
		getStyleClass().add("toggle-titled-pane");
	}
	
	@Override
	public ToggleGroup getToggleGroup()
	{
		return toggleGroup.get();
	}
	
	@Override
	public void setToggleGroup(ToggleGroup toggleGroup)
	{
		this.toggleGroup.set(toggleGroup);
	}
	
	@Override
	public ObjectProperty<ToggleGroup> toggleGroupProperty()
	{
		return toggleGroup;
	}
	
	@Override
	public boolean isSelected()
	{
		return selected.get();
	}
	
	@Override
	public void setSelected(boolean selected)
	{
		this.selected.set(selected);
	}
	
	@Override
	public BooleanProperty selectedProperty()
	{
		return selected;
	}
}