package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.WatchListRecord;

import java.util.List;

public interface CheckCWLAPI {

    @GET("services-gateway-demographic/api/person/cwl/v1")
    Call<List<WatchListRecord>> checkCWLByBioId(@Header("Workflow-Code") Integer workflowId,
            @Header("Workflow-Tcn") Long workflowTcn, @Query("bio-id") Long bioId);

}
