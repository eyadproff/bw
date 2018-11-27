package sa.gov.nic.bio.bw.core.beans;

public class Name extends JavaBean
{
	private String firstName;
	private String familyName;
	private String fatherName;
	private String grandfatherName;
	private String translatedFirstName;
	private String translatedFamilyName;
	private String translatedFatherName;
	private String translatedGrandFatherName;
	
	public Name(String firstName, String familyName, String fatherName, String grandfatherName,
	            String translatedFirstName, String translatedFamilyName, String translatedFatherName,
	            String translatedGrandFatherName)
	{
		this.firstName = firstName;
		this.familyName = familyName;
		this.fatherName = fatherName;
		this.grandfatherName = grandfatherName;
		this.translatedFirstName = translatedFirstName;
		this.translatedFamilyName = translatedFamilyName;
		this.translatedFatherName = translatedFatherName;
		this.translatedGrandFatherName = translatedGrandFatherName;
	}
	
	public String getFirstName(){return firstName;}
	public void setFirstName(String firstName){this.firstName = firstName;}
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	public String getFatherName(){return fatherName;}
	public void setFatherName(String fatherName){this.fatherName = fatherName;}
	
	public String getGrandfatherName(){return grandfatherName;}
	public void setGrandfatherName(String grandfatherName){this.grandfatherName = grandfatherName;}
	
	public String getTranslatedFirstName(){return translatedFirstName;}
	public void setTranslatedFirstName(String translatedFirstName){this.translatedFirstName = translatedFirstName;}
	
	public String getTranslatedFamilyName(){return translatedFamilyName;}
	public void setTranslatedFamilyName(String translatedFamilyName){this.translatedFamilyName = translatedFamilyName;}
	
	public String getTranslatedFatherName(){return translatedFatherName;}
	public void setTranslatedFatherName(String translatedFatherName){this.translatedFatherName = translatedFatherName;}
	
	public String getTranslatedGrandFatherName(){return translatedGrandFatherName;}
	public void setTranslatedGrandFatherName(String translatedGrandFatherName)
	{this.translatedGrandFatherName = translatedGrandFatherName;}
}