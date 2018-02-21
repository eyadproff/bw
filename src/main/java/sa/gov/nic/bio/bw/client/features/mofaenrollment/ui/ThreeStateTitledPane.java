package sa.gov.nic.bio.bw.client.features.mofaenrollment.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class ThreeStateTitledPane extends TitledPane
{
	private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
	private static final PseudoClass CAPTURED_PSEUDO_CLASS = PseudoClass.getPseudoClass("captured");
	private static final PseudoClass VALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("valid");
	
	public BooleanProperty active = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return ThreeStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return ACTIVE_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	
	public BooleanProperty captured = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(CAPTURED_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return ThreeStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return CAPTURED_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	
	public BooleanProperty valid = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(VALID_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return ThreeStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return VALID_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	
	public ThreeStateTitledPane()
	{
		this(null, null);
	}
	
	public ThreeStateTitledPane(String title, Node content)
	{
		super(title, content);
		getStyleClass().add("three-state-titled-pane");
	}
	
	public boolean isActive(){return active.get();}
	public BooleanProperty activeProperty(){return active;}
	public void setActive(boolean active){this.active.set(active);}
	
	public boolean isCaptured(){return captured.get();}
	public BooleanProperty capturedProperty(){return captured;}
	public void setCaptured(boolean captured){this.captured.set(captured);}
	
	public boolean isValid(){return valid.get();}
	public BooleanProperty validProperty(){return valid;}
	public void setValid(boolean valid){this.valid.set(valid);}
}