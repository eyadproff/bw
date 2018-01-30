package sa.gov.nic.bio.bw.client.login.workflow;

public interface WebServiceTask<I, O> extends ServiceTask<I, O>
{
	WebServiceResponse<O> execute(I input);
}