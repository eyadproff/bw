package sa.gov.nic.bio.bw.client.core.workflow;

public interface Converter<I, O>
{
	O convert(I data);
}