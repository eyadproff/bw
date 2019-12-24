package sa.gov.nic.bio.bw.workflow.irisinquiry.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

import java.util.List;

public class IrisInquiryStatusResult extends JavaBean
{
	private Long bioId;
	private List<Long> samisIdList;
	
	public Long getBioId(){return bioId;}
	public void setBioId(Long bioId){this.bioId = bioId;}
	
	public List<Long> getSamisIdList(){return samisIdList;}
	public void setSamisIdList(List<Long> samisIdList){this.samisIdList = samisIdList;}
}