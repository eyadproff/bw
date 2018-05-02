package sa.gov.nic.bio.bw.client.core.beans;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HideableItem<T>
{
	private final ObjectProperty<T> object = new SimpleObjectProperty<>();
	private final BooleanProperty hidden = new SimpleBooleanProperty() ;
	private final StringProperty text = new SimpleStringProperty();
	
	public HideableItem(T object)
	{
		setObject(object);
	}
	
	public ObjectProperty<T> objectProperty(){return this.object;}
	public T getObject(){return this.objectProperty().get();}
	public void setObject(T object){this.objectProperty().set(object);}
	
	public BooleanProperty hiddenProperty(){return this.hidden;}
	public boolean isHidden(){return this.hiddenProperty().get();}
	public void setHidden(boolean hidden){this.hiddenProperty().set(hidden);}
	
	public StringProperty textProperty(){return this.text;}
	public String getText(){return this.textProperty().get();}
	public void setText(String text){this.textProperty().set(text);}
	
	@Override
	public String toString()
	{
		return getObject() == null ? null : getText();
	}
}