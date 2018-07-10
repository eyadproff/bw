package sa.gov.nic.bio.bw.client.features.faceverification.webservice;

import sa.gov.nic.bio.bw.client.features.commons.webservice.PersonInfo;

import java.util.Objects;

public class FaceMatchingResponse
{
	private boolean matched;
	private PersonInfo personInfo;
	
	public FaceMatchingResponse(boolean matched, PersonInfo personInfo)
	{
		this.matched = matched;
		this.personInfo = personInfo;
	}
	
	public boolean isMatched(){return matched;}
	public void setMatched(boolean matched){this.matched = matched;}
	
	public PersonInfo getPersonInfo(){return personInfo;}
	public void setPersonInfo(PersonInfo personInfo){this.personInfo = personInfo;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		FaceMatchingResponse that = (FaceMatchingResponse) o;
		return matched == that.matched && Objects.equals(personInfo, that.personInfo);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(matched, personInfo);
	}
	
	@Override
	public String toString()
	{
		return "FaceMatchingResponse{" + "matched=" + matched + ", personInfo=" + personInfo + '}';
	}
}