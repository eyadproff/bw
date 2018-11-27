package sa.gov.nic.bio.bw.workflow.commons.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import sa.gov.nic.bio.bw.workflow.commons.beans.PersonInfo;

public interface PersonInfoByIdAPI
{
	@GET("services-gateway-demographic/api/person/info/v1")
	Call<PersonInfo> getPersonInfoById(@Header("Workflow-Code") Integer workflowId,
	                                   @Header("Workflow-Tcn") Long workflowTcn,
	                                   @Query("person-id") long personId,
	                                   @Query("person-type") int personType);
}