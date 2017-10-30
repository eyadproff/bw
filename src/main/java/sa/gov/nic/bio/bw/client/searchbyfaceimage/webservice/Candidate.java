package sa.gov.nic.bio.bw.client.searchbyfaceimage.webservice;

import java.io.Serializable;

public class Candidate implements Serializable
{
	private int score;
	private String name;
	private String photoPath;
	
	public Candidate(int score, String name, String photoPath)
	{
		this.score = score;
		this.name = name;
		this.photoPath = photoPath;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getPhotoPath()
	{
		return photoPath;
	}
	
	public void setPhotoPath(String photoPath)
	{
		this.photoPath = photoPath;
	}
	
	@Override
	public String toString()
	{
		return "Candidate{" + "score=" + score + ", name='" + name + '\'' + ", photoPath='" + photoPath + '\'' + '}';
	}
}