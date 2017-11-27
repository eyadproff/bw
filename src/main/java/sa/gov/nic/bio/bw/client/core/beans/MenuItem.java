package sa.gov.nic.bio.bw.client.core.beans;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;

import java.util.Objects;

public class MenuItem implements Comparable<MenuItem>
{
	private String menuId;
	private String label;
	private int order; // lowest order == top menu
	private String iconId;
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
	
	public int getOrder(){return order;}
	public void setOrder(int order){this.order = order;}
	
	public String getIconId(){return iconId;}
	public void setIconId(String iconId){this.iconId = iconId;}
	
	public boolean isSelected(){ return selected.get();}
	public void setSelected(boolean selected){ this.selected.set(selected);}
	public BooleanProperty selectedProperty(){return selected;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		MenuItem menuItem = (MenuItem) o;
		return order == menuItem.order && Objects.equals(menuId, menuItem.menuId) && Objects.equals(label, menuItem.label) && Objects.equals(iconId, menuItem.iconId) && Objects.equals(selected, menuItem.selected);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(menuId, label, order, iconId, selected);
	}
	
	@Override
	public String toString()
	{
		return "MenuItem{" + "menuId='" + menuId + '\'' + ", label='" + label + '\'' + ", order=" + order + ", iconId='" + iconId + '\'' + ", selected=" + selected + '}';
	}
	
	@Override
	public int compareTo(MenuItem other)
	{
		return other == null ? -1 : -Integer.compare(this.order, other.order);
	}
}