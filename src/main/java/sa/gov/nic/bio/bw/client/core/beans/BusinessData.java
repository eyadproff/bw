package sa.gov.nic.bio.bw.client.core.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessData
{
	private Map<String, List<String>> menuRolesMap = new HashMap<>();
	
	{
		// TODO: temp
		List<String> cancelLatentRoles = new ArrayList<>();
		cancelLatentRoles.add("WSLatentDeleter");
		//menuRolesMap.put("menu.query.queryXAfis", cancelLatentRoles);
		menuRolesMap.put("menu.delete.cancelLatent", cancelLatentRoles);
		menuRolesMap.put("menu.delete.delinkXAfis", cancelLatentRoles);
	}
	
	public boolean userHasMenuAccess(List<String> userRoles, String menuId)
	{
		List<String> menuRoles = menuRolesMap.get(menuId);
		return menuRoles != null && !Collections.disjoint(userRoles, menuRoles);
	}
}