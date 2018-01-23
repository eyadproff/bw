package sa.gov.nic.bio.bw.client.core.wizard;

import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

public class WizardStep
{
	private StringProperty title;
	private StringProperty iconId;
	private ObjectProperty<WizardStepState> wizardStepState = new SimpleObjectProperty<>(WizardStepState.NOT_REACHED);
	
	public static Callback<WizardStep, Observable[]> extractor()
	{
		return param -> new Observable[]{param.wizardStepState};
	}
	
	public WizardStep(@NamedArg("title") String title, @NamedArg("iconId") String iconId)
	{
		this.title = new SimpleStringProperty(this.title, "title", title);
		this.iconId = new SimpleStringProperty(this.iconId, "iconId", iconId);
	}
	
	public final StringProperty titleProperty(){return this.title;}
	public final void setTitle(String value){this.title.set(value);}
	public final String getTitle(){return this.title.get();}
	
	public final StringProperty iconIdProperty(){return this.iconId;}
	public final void setIconId(String value){this.iconId.set(value);}
	public final String getIconId(){return this.iconId.get();}
	
	public final ObjectProperty<WizardStepState> wizardStepStateProperty(){return this.wizardStepState;}
	public final void setWizardStepState(WizardStepState value){this.wizardStepState.set(value);}
	public final WizardStepState getWizardStepState(){return this.wizardStepState.get();}
}