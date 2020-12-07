package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import java.util.Map;

public interface PassportInfoAPI {

    @GET("services-gateway-demographic/api/person/passport-number/v2")
    Call<Map<String,Object>> retrievePassportNumberAndNationalityBySamisId(@Header("Workflow-Code") Integer workflowId,
            @Header("Workflow-Tcn") Long workflowTcn,
            @Query("samis-id") long personId);
}
