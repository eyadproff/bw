package sa.gov.nic.bio.bw.client.features.commons.webservice;

import java.util.Objects;

public class IdType
{
	private int code;
	private String desc;
	
	public IdType(int code, String desc)
	{
		this.code = code;
		this.desc = desc;
	}
	
	public int getCode(){return code;}
	public void setCode(int code){this.code = code;}
	
	public String getDesc(){return desc;}
	public void setDesc(String desc){this.desc = desc;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		IdType idType = (IdType) o;
		return code == idType.code && Objects.equals(desc, idType.desc);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, desc);
	}
	
	@Override
	public String toString()
	{
		return "IdType{" + "code=" + code + ", desc='" + desc + '\'' + '}';
	}
}