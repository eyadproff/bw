package sa.gov.nic.bio.bw.core.beans;

public class TaskOutput<T> extends JavaBean
{
	private String name;
	private Class<?> type;
	private T value;
	
	public TaskOutput(String name, Class<?> type, T value)
	{
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public Class<?> getType(){return type;}
	public void setType(Class<?> type){this.type = type;}
	
	public T getValue(){return value;}
	public void setValue(T value){this.value = value;}
}