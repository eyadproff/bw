package sa.gov.nic.bio.bw.workflow.citizenenrollment.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.citizenenrollment.beans.PersonInfo;

public interface PersonInfoByIdAPI {
    @GET("services-gateway-demographic/api/person/info/v3")
    Call<PersonInfo> getPersonInfoById(@Header("Workflow-Code") Integer workflowId,
                                       @Header("Workflow-Tcn") Long workflowTcn,
                                       @Query("person-id") long personId,
                                       @Query("person-type") int personType);
}