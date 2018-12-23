package sa.gov.nic.bio.bw.login.tasks;

import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.beans.MenuItem;
import sa.gov.nic.bio.bw.core.utils.Device;
import sa.gov.nic.bio.bw.core.workflow.Input;
import sa.gov.nic.bio.bw.core.workflow.WorkflowTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrepareUserMenusWorkflowTask extends WorkflowTask
{
	@Input(alwaysRequired = true) private Map<String, Set<String>> menusRoles;
	@Input(alwaysRequired = true) private List<String> userRoles;
	
	@Override
	public void execute()
	{
		List<MenuItem> menus = new ArrayList<>();
		for(MenuItem menuItem : Context.getSubMenus())
		{
			Set<String> menuRoles = menusRoles.get(menuItem.getMenuId());
			
			if(menuRoles != null && !Collections.disjoint(userRoles, menuRoles))
			{
				menus.add(menuItem);
			}
		}
		
		Set<Device> devices = new HashSet<>();
		for(MenuItem menuItem : menus)
		{
			Set<Device> menuDevices = menuItem.getDevices();
			if(menuDevices != null) devices.addAll(menuDevices);
		}
		
		Context.getUserSession().setAttribute("userRoles", userRoles);
		Context.getUserSession().setAttribute("menus", menus);
		Context.getUserSession().setAttribute("devices", devices);
	}
}