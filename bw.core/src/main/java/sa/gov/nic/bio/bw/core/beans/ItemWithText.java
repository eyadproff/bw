package sa.gov.nic.bio.bw.core.beans;

import javafx.beans.NamedArg;

public class ItemWithText<T>
{
	private final T item;
	private final String text;
	
	public ItemWithText(@NamedArg("item") T item, @NamedArg("text") String text)
	{
		this.item = item;
		this.text = text;
	}
	
	public T getItem(){return item;}
	public String getText(){return text;}
	
	@Override
	public String toString(){return getText();}
}