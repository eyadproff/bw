package sa.gov.nic.bio.bw.workflow.registerconvictedpresent.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class PalmFingerprint extends JavaBean
{
	private String palmWsq;
	private String palmImage;
	private boolean skipped;
	
	public PalmFingerprint(){}
	
	public PalmFingerprint(String palmWsq, String palmImage)
	{
		this.palmWsq = palmWsq;
		this.palmImage = palmImage;
	}
	
	public PalmFingerprint(String palmWsq, String palmImage, boolean skipped)
	{
		this.palmWsq = palmWsq;
		this.palmImage = palmImage;
		this.skipped = skipped;
	}
	
	public String getPalmWsq(){return palmWsq;}
	public void setPalmWsq(String palmWsq){this.palmWsq = palmWsq;}
	
	public String getPalmImage(){return palmImage;}
	public void setPalmImage(String palmImage){this.palmImage = palmImage;}
	
	public boolean isSkipped(){return skipped;}
	public void setSkipped(boolean skipped){this.skipped = skipped;}
}