package sa.gov.nic.bio.bw.core.workflow;

public class Field<T>
{
	private final String name;
	private Class<T> type;
	
	public Field(String name)
	{
		this.name = name;
	}
	
	public final String getName(){return name;}
}