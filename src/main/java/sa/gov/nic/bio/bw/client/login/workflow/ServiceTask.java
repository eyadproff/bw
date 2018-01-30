package sa.gov.nic.bio.bw.client.login.workflow;

public interface ServiceTask<I, O>
{
	ServiceResponse<O> execute(I input);
}