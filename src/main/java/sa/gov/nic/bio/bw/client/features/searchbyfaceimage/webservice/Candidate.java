package sa.gov.nic.bio.bw.client.features.searchbyfaceimage.webservice;

import java.io.Serializable;
import java.util.Objects;

public class Candidate implements Comparable<Candidate>, Serializable
{
	private long samisId;
	private long bioId;
	private int score;
	private String image; // base64
	private String firstName;
	private String fatherName;
	private String familyName;
	
	public Candidate(){}
	
	public long getSamisId(){return samisId;}
	public void setSamisId(long samisId){this.samisId = samisId;}
	
	public long getBioId(){return bioId;}
	public void setBioId(long bioId){this.bioId = bioId;}
	
	public int getScore(){return score;}
	public void setScore(int score){this.score = score;}
	
	public String getImage(){return image;}
	public void setImage(String image){this.image = image;}
	
	public String getFirstName(){return firstName; }
	public void setFirstName(String firstName){this.firstName = firstName; }
	
	public String getFatherName(){return fatherName;}
	public void setFatherName(String fatherName){this.fatherName = fatherName; }
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Candidate candidate = (Candidate) o;
		return samisId == candidate.samisId && bioId == candidate.bioId && score == candidate.score &&
			   Objects.equals(image, candidate.image) && Objects.equals(firstName, candidate.firstName) &&
			   Objects.equals(fatherName, candidate.fatherName) && Objects.equals(familyName, candidate.familyName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(samisId, bioId, score, image, firstName, fatherName, familyName);
	}
	
	@Override
	public String toString()
	{
		return "Candidate{" + "samisId=" + samisId + ", bioId=" + bioId + ", score=" + score + ", image='" + image +
			   '\'' + ", firstName='" + firstName + '\'' + ", fatherName='" + fatherName + '\'' + ", familyName='" +
			   familyName + '\'' + '}';
	}
	
	@Override
	public int compareTo(Candidate o)
	{
		return o == null ? -1 : -Integer.compare(this.score, o.score);
	}
}