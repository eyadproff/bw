package sa.gov.nic.bio.bw.core.beans;

import java.util.List;
import java.util.Objects;

public class TaskResult
{
	private String name;
	private boolean success;
	private List<TaskOutput<?>> outputs;
	
	public TaskResult(String name, boolean success, List<TaskOutput<?>> outputs)
	{
		this.name = name;
		this.success = success;
		this.outputs = outputs;
	}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public boolean isSuccess(){return success;}
	public void setSuccess(boolean success){this.success = success;}
	
	public List<TaskOutput<?>> getOutputs(){return outputs;}
	public void setOutputs(List<TaskOutput<?>> outputs){this.outputs = outputs;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		TaskResult that = (TaskResult) o;
		return success == that.success && Objects.equals(name, that.name) && Objects.equals(outputs, that.outputs);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(name, success, outputs);
	}
	
	@Override
	public String toString()
	{
		return "TaskResult{" + "name='" + name + '\'' + ", success=" + success + ", outputs=" + outputs + '}';
	}
}