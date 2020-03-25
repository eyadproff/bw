package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;
import sa.gov.nic.bio.bw.workflow.commons.beans.Finger;

import java.util.List;
import java.util.Map;

public class LatentHitsDetails extends JavaBean
{
	private Long tcn;
	private Long civilBioId;
	private List<Finger> civilFingers;
	private List<Integer> civilMissingFingers;
	private Map<Long, List<FingerHitDetails>> latentHitDetails;
	private Map<Long, String> latentImagesWsq; // set by the webservice
	private Map<Long, String> latentImagesBase64; // set by the webservice
	private Map<Integer, String> fingerImagesBase64; // set by the webservice
	
	public Long getTcn(){return tcn;}
	public void setTcn(Long tcn){this.tcn = tcn;}
	
	public Long getCivilBioId(){return civilBioId;}
	public void setCivilBioId(Long civilBioId){this.civilBioId = civilBioId;}
	
	public List<Finger> getCivilFingers(){return civilFingers;}
	public void setCivilFingers(List<Finger> civilFingers){this.civilFingers = civilFingers;}
	
	public List<Integer> getCivilMissingFingers(){return civilMissingFingers;}
	public void setCivilMissingFingers(List<Integer> civilMissingFingers){this.civilMissingFingers = civilMissingFingers;}
	
	public Map<Long, List<FingerHitDetails>> getLatentHitDetails(){return latentHitDetails;}
	public void setLatentHitDetails(Map<Long, List<FingerHitDetails>> latentHitDetails){this.latentHitDetails = latentHitDetails;}
	
	public Map<Long, String> getLatentImagesWsq(){return latentImagesWsq;}
	public void setLatentImagesWsq(Map<Long, String> latentImagesWsq){this.latentImagesWsq = latentImagesWsq;}
	
	public Map<Long, String> getLatentImagesBase64(){return latentImagesBase64;}
	public void setLatentImagesBase64(Map<Long, String> latentImagesBase64){this.latentImagesBase64 = latentImagesBase64;}
	
	public Map<Integer, String> getFingerImagesBase64(){return fingerImagesBase64;}
	public void setFingerImagesBase64(Map<Integer, String> fingerImagesBase64){this.fingerImagesBase64 = fingerImagesBase64;}
}