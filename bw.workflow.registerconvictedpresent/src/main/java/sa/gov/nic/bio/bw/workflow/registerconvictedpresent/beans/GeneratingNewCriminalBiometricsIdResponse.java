package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class GeneratingNewCriminalBiometricsIdResponse extends JavaBean
{
	private Long criminalBiometricsId;
	
	public Long getCriminalBiometricsId(){return criminalBiometricsId;}
	public void setCriminalBiometricsId(Long criminalBiometricsId){this.criminalBiometricsId = criminalBiometricsId;}
}