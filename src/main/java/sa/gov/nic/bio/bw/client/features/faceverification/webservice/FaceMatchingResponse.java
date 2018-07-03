package sa.gov.nic.bio.bw.client.features.faceverification.webservice;

import java.util.Objects;

public class FaceMatchingResponse
{
	public static class PersonInfo
	{
		private long samisId;
		private long bioId;
		private String image; // base64
		private String firstName;
		private String fatherName;
		private String familyName;
		
		public long getSamisId(){return samisId;}
		public void setSamisId(long samisId){this.samisId = samisId;}
		
		public long getBioId(){return bioId;}
		public void setBioId(long bioId){this.bioId = bioId;}
		
		public String getImage(){return image;}
		public void setImage(String image){this.image = image;}
		
		public String getFirstName(){return firstName;}
		public void setFirstName(String firstName){this.firstName = firstName;}
		
		public String getFatherName(){return fatherName;}
		public void setFatherName(String fatherName){this.fatherName = fatherName;}
		
		public String getFamilyName(){return familyName;}
		public void setFamilyName(String familyName){this.familyName = familyName;}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			PersonInfo that = (PersonInfo) o;
			return samisId == that.samisId && bioId == that.bioId && Objects.equals(image, that.image) &&
				   Objects.equals(firstName, that.firstName) && Objects.equals(fatherName, that.fatherName) &&
				   Objects.equals(familyName, that.familyName);
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(samisId, bioId, image, firstName, fatherName, familyName);
		}
		
		@Override
		public String toString()
		{
			return "PersonInfo{" + "samisId=" + samisId + ", bioId=" + bioId + ", image='" + image + '\'' +
				   ", firstName='" + firstName + '\'' + ", fatherName='" + fatherName + '\'' + ", familyName='" +
				   familyName + '\'' + '}';
		}
	}
	
	private boolean matched;
	private PersonInfo personInfo;
	
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