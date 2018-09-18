package sa.gov.nic.bio.bw.client.core.workflow;

import java.util.function.BooleanSupplier;

public class AlwaysCondition implements BooleanSupplier
{
	@Override
	public boolean getAsBoolean()
	{
		return true;
	}
}