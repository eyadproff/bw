package sa.gov.nic.bio.bw.core.beans;

import java.util.List;

public class TaskResult extends JavaBean
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
}