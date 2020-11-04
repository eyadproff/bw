package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sa.gov.nic.bio.bw.workflow.commons.beans.BioExclusion;

import java.util.List;


public interface BioExclusionAPI {


    @FormUrlEncoded
    @POST("services-gateway-biooperation/api/enrollment/biometrics/exclusion/retrieve/v1")
    Call<List<BioExclusion>> retrieveBioExclusions(@Header("Workflow-Code") Integer workflowId,
                                                   @Header("Workflow-Tcn") Long workflowTcn,
                                                   @Field("samis-id") Integer samisId);

}
