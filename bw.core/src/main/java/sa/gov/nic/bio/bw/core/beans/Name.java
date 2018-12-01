package sa.gov.nic.bio.bw.core.beans;

public class Name extends JavaBean
{
	private String firstName;
	private String fatherName;
	private String grandfatherName;
	private String familyName;
	private String translatedFirstName;
	private String translatedFatherName;
	private String translatedGrandFatherName;
	private String translatedFamilyName;
	
	public Name(String firstName, String fatherName, String grandfatherName, String familyName,
	            String translatedFirstName, String translatedFatherName, String translatedGrandFatherName,
	            String translatedFamilyName)
	{
		this.firstName = firstName;
		this.fatherName = fatherName;
		this.grandfatherName = grandfatherName;
		this.familyName = familyName;
		this.translatedFirstName = translatedFirstName;
		this.translatedFatherName = translatedFatherName;
		this.translatedGrandFatherName = translatedGrandFatherName;
		this.translatedFamilyName = translatedFamilyName;
	}
	
	public String getFirstName(){return firstName;}
	public void setFirstName(String firstName){this.firstName = firstName;}
	
	public String getFatherName(){return fatherName;}
	public void setFatherName(String fatherName){this.fatherName = fatherName;}
	
	public String getGrandfatherName(){return grandfatherName;}
	public void setGrandfatherName(String grandfatherName){this.grandfatherName = grandfatherName;}
	
	public String getFamilyName(){return familyName;}
	public void setFamilyName(String familyName){this.familyName = familyName;}
	
	public String getTranslatedFirstName(){return translatedFirstName;}
	public void setTranslatedFirstName(String translatedFirstName){this.translatedFirstName = translatedFirstName;}
	
	public String getTranslatedFatherName(){return translatedFatherName;}
	public void setTranslatedFatherName(String translatedFatherName){this.translatedFatherName = translatedFatherName;}
	
	public String getTranslatedGrandFatherName(){return translatedGrandFatherName;}
	public void setTranslatedGrandFatherName(String translatedGrandFatherName)
														{this.translatedGrandFatherName = translatedGrandFatherName;}
	
	public String getTranslatedFamilyName(){return translatedFamilyName;}
	public void setTranslatedFamilyName(String translatedFamilyName){this.translatedFamilyName = translatedFamilyName;}
}