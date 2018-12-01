package sa.gov.nic.bio.bw.workflow.searchbyfaceimage.beans;

import sa.gov.nic.bio.bw.core.beans.JavaBean;

public class Candidate extends JavaBean implements Comparable<Candidate>
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
	public int compareTo(Candidate o)
	{
		return o == null ? -1 : -Integer.compare(this.score, o.score);
	}
}