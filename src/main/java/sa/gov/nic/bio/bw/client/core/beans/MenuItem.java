package sa.gov.nic.bio.bw.client.core.beans;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;

import java.util.Objects;

public class MenuItem
{
	private String menuId;
	private String label;
	private BooleanProperty selected = new SimpleBooleanProperty();
	
	public static Callback<MenuItem, Observable[]> extractor()
	{
		return param -> new Observable[]{param.selected};
	}
	
	public MenuItem(){}
	
	public String getMenuId(){ return menuId; }
	public void setMenuId(String menuId){ this.menuId = menuId; }
	
	public String getLabel(){return label;}
	public void setLabel(String label){ this.label = label;}
	
	public boolean isSelected(){ return selected.get();}
	public void setSelected(boolean selected){ this.selected.set(selected);}
	public BooleanProperty selectedProperty(){return selected;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		MenuItem item = (MenuItem) o;
		return Objects.equals(menuId, item.menuId) && Objects.equals(label, item.label) && Objects.equals(selected, item.selected);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(menuId, label, selected);
	}
	
	@Override
	public String toString()
	{
		return "MenuItem{" + "menuId='" + menuId + '\'' + ", label='" + label + '\'' + ", selected=" + selected + '}';
	}
}