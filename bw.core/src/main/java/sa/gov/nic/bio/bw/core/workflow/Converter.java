package sa.gov.nic.bio.bw.core.workflow;

public interface Converter<I, O>
{
	O convert(I data);
}