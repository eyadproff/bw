package sa.gov.nic.bio.bw.core.beans;


import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ComboBoxItem<T> extends JavaBean
{
	private final ObjectProperty<T> item = new SimpleObjectProperty<>();
	private final BooleanProperty hidden = new SimpleBooleanProperty() ;
	private final StringProperty text = new SimpleStringProperty();
	
	public ComboBoxItem(@NamedArg("item") T item)
	{
		setItem(item);
	}
	
	public ComboBoxItem(@NamedArg("item") T item, @NamedArg("text") String text)
	{
		setItem(item);
		setText(text);
	}
	
	public ObjectProperty<T> itemProperty(){return this.item;}
	public T getItem(){return this.itemProperty().get();}
	public void setItem(T item){this.itemProperty().set(item);}
	
	public BooleanProperty hiddenProperty(){return this.hidden;}
	public boolean isHidden(){return this.hiddenProperty().get();}
	public void setHidden(boolean hidden){this.hiddenProperty().set(hidden);}
	
	public StringProperty textProperty(){return this.text;}
	public String getText(){return this.textProperty().get();}
	public void setText(String text){this.textProperty().set(text);}
	
	@Override
	public String toString()
	{
		return getItem() == null ? null : getText();
	}
}