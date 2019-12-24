package sa.gov.nic.bio.bw.workflow.criminaltransactions.beans;

import java.util.List;

public class CriminalTransactionsInquiryResult
{
	private Integer total;
	private List<CriminalTransaction> list;
	
	public Integer getTotal(){return total;}
	public void setTotal(Integer total){this.total = total;}
	
	public List<CriminalTransaction> getList(){return list;}
	public void setList(List<CriminalTransaction> list){this.list = list;}
}