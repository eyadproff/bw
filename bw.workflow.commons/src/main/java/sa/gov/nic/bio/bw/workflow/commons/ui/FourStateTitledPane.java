package sa.gov.nic.bio.bw.workflow.commons.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class FourStateTitledPane extends TitledPane
{
	private static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
	private static final PseudoClass CAPTURED_PSEUDO_CLASS = PseudoClass.getPseudoClass("captured");
	private static final PseudoClass VALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("valid");
	private static final PseudoClass DUPLICATED_PSEUDO_CLASS = PseudoClass.getPseudoClass("duplicated");
	private static final PseudoClass MISSING_IRIS_PSEUDO_CLASS = PseudoClass.getPseudoClass("missing");
	
	private BooleanProperty active = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return FourStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return ACTIVE_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	private BooleanProperty captured = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(CAPTURED_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return FourStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return CAPTURED_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	private BooleanProperty valid = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(VALID_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return FourStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return VALID_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	private BooleanProperty duplicated = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(DUPLICATED_PSEUDO_CLASS, get());
		}
		
		@Override
		public Object getBean()
		{
			return FourStateTitledPane.this;
		}
		
		@Override
		public String getName()
		{
			return DUPLICATED_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	private BooleanProperty missing = new BooleanPropertyBase(false)
	{
		@Override
		protected void invalidated()
		{
			pseudoClassStateChanged(MISSING_IRIS_PSEUDO_CLASS, get());
		}

		@Override
		public Object getBean()
		{
			return FourStateTitledPane.this;
		}

		@Override
		public String getName()
		{
			return MISSING_IRIS_PSEUDO_CLASS.getPseudoClassName();
		}
	};
	public FourStateTitledPane()
	{
		this(null, null);
	}
	
	public FourStateTitledPane(String title, Node content)
	{
		super(title, content);
		getStyleClass().add("four-state-titled-pane");
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
	
	public boolean isDuplicated(){return duplicated.get();}
	public BooleanProperty duplicatedProperty(){return duplicated;}
	public void setDuplicated(boolean duplicated){this.duplicated.set(duplicated);}

	public boolean isMissing(){return missing.get();}
	public BooleanProperty missingProperty(){return missing;}
	public void setMissing(boolean missing){this.missing.set(missing);}
}