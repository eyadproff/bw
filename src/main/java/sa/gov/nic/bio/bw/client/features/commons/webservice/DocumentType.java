package sa.gov.nic.bio.bw.client.features.commons.webservice;

import java.util.Objects;

public class DocumentType
{
	private int code;
	private String desc;
	
	public DocumentType(int code, String desc)
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
		DocumentType documentType = (DocumentType) o;
		return code == documentType.code && Objects.equals(desc, documentType.desc);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(code, desc);
	}
	
	@Override
	public String toString()
	{
		return "DocumentType{" + "code=" + code + ", desc='" + desc + '\'' + '}';
	}
}