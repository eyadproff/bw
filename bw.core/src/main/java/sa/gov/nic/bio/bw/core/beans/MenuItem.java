package sa.gov.nic.bio.bw.core.beans;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.Workflow;

import java.util.Set;

public class MenuItem extends JavaBean implements Comparable<MenuItem>
{
	private int index;
	private String menuId;
	private String label;
	private int order; // lowest menuOrder == top menu
	private Class<? extends Workflow> workflowClass;
	private String iconId;
	private Set<Device> devices;
	private BooleanProperty selected = new SimpleBooleanProperty();
	
	public static Callback<MenuItem, Observable[]> extractor()
	{
		return param -> new Observable[]{param.selected};
	}
	
	public MenuItem(){}
	
	public int getIndex(){return index;}
	public void setIndex(int index){this.index = index;}
	
	public String getMenuId(){ return menuId; }
	public void setMenuId(String menuId){ this.menuId = menuId; }
	
	public String getLabel(){return label;}
	public void setLabel(String label){ this.label = label;}
	
	public int getOrder(){return order;}
	public void setOrder(int order){this.order = order;}
	
	public Class<? extends Workflow> getWorkflowClass(){return workflowClass;}
	public void setWorkflowClass(Class<? extends Workflow> workflowClass){this.workflowClass = workflowClass;}
	
	public String getIconId(){return iconId;}
	public void setIconId(String iconId){this.iconId = iconId;}
	
	public Set<Device> getDevices(){return devices;}
	public void setDevices(Set<Device> devices){this.devices = devices;}
	
	public boolean isSelected(){ return selected.get();}
	public void setSelected(boolean selected){ this.selected.set(selected);}
	public BooleanProperty selectedProperty(){return selected;}
	
	@Override
	public int compareTo(MenuItem other)
	{
		return other == null ? -1 : -Integer.compare(this.order, other.order);
	}
}