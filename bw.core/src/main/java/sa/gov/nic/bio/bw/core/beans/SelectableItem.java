package sa.gov.nic.bio.bw.core.beans;

public class SelectableItem<T> extends JavaBean
{
	private T item;
	private boolean selected;
	
	public SelectableItem(T item, boolean selected)
	{
		this.item = item;
		this.selected = selected;
	}
	
	public T getItem(){return item;}
	public void setItem(T item){this.item = item;}
	
	public boolean isSelected(){return selected;}
	public void setSelected(boolean selected){this.selected = selected;}
}