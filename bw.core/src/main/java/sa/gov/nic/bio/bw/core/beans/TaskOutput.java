package sa.gov.nic.bio.bw.core.beans;

import java.util.Objects;

public class TaskOutput<T>
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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		TaskOutput<?> mockInput = (TaskOutput<?>) o;
		return Objects.equals(name, mockInput.name) && Objects.equals(type, mockInput.type) &&
			   Objects.equals(value, mockInput.value);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(name, type, value);
	}
	
	@Override
	public String toString()
	{
		return "TaskOutput{" + "name='" + name + '\'' + ", type=" + type + ", value=" + value + '}';
	}
}