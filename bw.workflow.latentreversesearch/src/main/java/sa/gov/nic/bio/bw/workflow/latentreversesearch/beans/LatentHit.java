package sa.gov.nic.bio.bw.workflow.latentreversesearch.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class LatentHit extends JavaBean
{
	private long transactionNumber;
	private long civilBiometricsId;
	private long personId;
	private long referenceNumber;
	private int locationId;
	private LatentHitProcessingStatus status;
	private long entryDateTime;
	
	public long getTransactionNumber(){return transactionNumber;}
	public void setTransactionNumber(long transactionNumber){this.transactionNumber = transactionNumber;}
	
	public long getCivilBiometricsId(){return civilBiometricsId;}
	public void setCivilBiometricsId(long civilBiometricsId){this.civilBiometricsId = civilBiometricsId;}
	
	public long getPersonId(){return personId;}
	public void setPersonId(long personId){this.personId = personId;}
	
	public long getReferenceNumber(){return referenceNumber;}
	public void setReferenceNumber(long referenceNumber){this.referenceNumber = referenceNumber;}
	
	public int getLocationId(){return locationId;}
	public void setLocationId(int locationId){this.locationId = locationId;}
	
	public LatentHitProcessingStatus getStatus(){return status;}
	public void setStatus(LatentHitProcessingStatus status){this.status = status;}
	
	public long getEntryDateTime(){return entryDateTime;}
	public void setEntryDateTime(long entryDateTime){this.entryDateTime = entryDateTime;}
}