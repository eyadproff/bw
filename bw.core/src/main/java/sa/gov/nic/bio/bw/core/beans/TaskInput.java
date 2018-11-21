package sa.gov.nic.bio.bw.core.beans;

import java.util.Objects;

public class TaskInput<T>
{
	private String name;
	private Class<?> type;
	private boolean alwaysRequired;
	private String requirementConditions;
	private T value;
	
	public TaskInput(String name, Class<?> type, boolean alwaysRequired, String requirementConditions, T value)
	{
		this.name = name;
		this.type = type;
		this.alwaysRequired = alwaysRequired;
		this.requirementConditions = requirementConditions;
		this.value = value;
	}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public Class<?> getType(){return type;}
	public void setType(Class<?> type){this.type = type;}
	
	public boolean isAlwaysRequired(){return alwaysRequired;}
	public void setAlwaysRequired(boolean alwaysRequired){this.alwaysRequired = alwaysRequired;}
	
	public String getRequirementConditions(){return requirementConditions;}
	public void setRequirementConditions(String requirementConditions)
																{this.requirementConditions = requirementConditions;}
	
	public T getValue(){return value;}
	public void setValue(T value){this.value = value;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		TaskInput<?> taskInput = (TaskInput<?>) o;
		return alwaysRequired == taskInput.alwaysRequired && Objects.equals(name, taskInput.name) &&
			   Objects.equals(type, taskInput.type) &&
			   Objects.equals(requirementConditions, taskInput.requirementConditions) &&
			   Objects.equals(value, taskInput.value);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(name, type, alwaysRequired, requirementConditions, value);
	}
	
	@Override
	public String toString()
	{
		return "TaskInput{" + "name='" + name + '\'' + ", type=" + type + ", alwaysRequired=" + alwaysRequired +
			   ", requirementConditions='" + requirementConditions + '\'' + ", value=" + value + '}';
	}
}