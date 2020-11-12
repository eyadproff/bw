package sa.gov.nic.bio.bw.workflow.biometricsexception.lookups;

import retrofit2.Call;
import sa.gov.nic.bio.bw.core.Context;
import sa.gov.nic.bio.bw.core.interfaces.AppLogger;
import sa.gov.nic.bio.commons.TaskResponse;
import sa.gov.nic.bio.bw.workflow.biometricsexception.webservice.LookupAPI;
import sa.gov.nic.bio.bw.workflow.biometricsexception.beans.Cause;

import java.util.List;
import java.util.concurrent.Callable;

public class CausesLookup implements Callable<TaskResponse<?>>, AppLogger {
    public static final String KEY = "lookups.causes";

    @Override
    public TaskResponse<?> call() {
        @SuppressWarnings("unchecked")
        List<Cause> causes = (List<Cause>) Context.getUserSession().getAttribute(KEY);

        if (causes == null) {
            LookupAPI api = Context.getWebserviceManager().getApi(LookupAPI.class);
            Call<List<Cause>> call = api.lookupCauses();
            TaskResponse<List<Cause>> taskResponse = Context.getWebserviceManager().executeApi(call);

            if (taskResponse.isSuccess()) causes = taskResponse.getResult();
            else return taskResponse;

            Context.getUserSession().setAttribute(KEY, causes);
        }

        return TaskResponse.success();
    }
}
