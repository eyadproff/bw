package sa.gov.nic.bio.bw.workflow.commons.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class SearchQueryResult<T> extends JavaBean
{
	private Integer total;
	private List<T> list;
	
	public Integer getTotal(){return total;}
	public void setTotal(Integer total){this.total = total;}
	
	public List<T> getList(){return list;}
	public void setList(List<T> list){this.list = list;}
}