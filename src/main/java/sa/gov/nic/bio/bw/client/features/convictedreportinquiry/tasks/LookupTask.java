package sa.gov.nic.bio.bw.client.features.convictedreportinquiry.tasks;

import javafx.concurrent.Task;
import sa.gov.nic.bio.bw.client.features.registerconvictedpresent.workflow.ConvictedReportLookupService;
import sa.gov.nic.bio.bw.client.login.workflow.ServiceResponse;

public class LookupTask extends Task<ServiceResponse<Void>>
{
	@Override
	protected ServiceResponse<Void> call()
	{
		return ConvictedReportLookupService.execute();
	}
}