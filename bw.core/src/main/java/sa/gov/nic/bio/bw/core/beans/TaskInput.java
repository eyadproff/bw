package sa.gov.nic.bio.bw.core.beans;

public class TaskInput<T> extends JavaBean
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
}